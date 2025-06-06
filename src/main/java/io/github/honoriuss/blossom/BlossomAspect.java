package io.github.honoriuss.blossom;

import io.github.honoriuss.blossom.annotations.AdditionalTrackingInfo;
import io.github.honoriuss.blossom.annotations.AppContext;
import io.github.honoriuss.blossom.annotations.Track;
import io.github.honoriuss.blossom.annotations.TrackParameters;
import io.github.honoriuss.blossom.interfaces.ITrackingAppContextHandler;
import io.github.honoriuss.blossom.interfaces.ITrackingHandler;
import io.github.honoriuss.blossom.interfaces.ITrackingObjectMapper;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;


@Aspect
@Component
class BlossomAspect<T> {
    private final Logger logger = LoggerFactory.getLogger(BlossomAspect.class);

    private final ITrackingObjectMapper<T> trackingObjectMapper;
    private final ITrackingAppContextHandler trackingAppContextHandler;
    private final ITrackingHandler<T> trackingHandler;

    public BlossomAspect(ITrackingObjectMapper<T> trackingObjectMapper, ITrackingAppContextHandler trackingAppContextHandler, ITrackingHandler<T> trackingHandler) {
        this.trackingObjectMapper = trackingObjectMapper;
        this.trackingAppContextHandler = trackingAppContextHandler;
        this.trackingHandler = trackingHandler;
    }

    @Before(value = "@annotation(trackParameters)")
    void track(JoinPoint joinPoint, TrackParameters trackParameters) {
        createTrackingObject(joinPoint, trackParameters.parameterNames(), trackParameters.optKey(), trackParameters.optArg()).flatMap(trackingObj -> {
            var method = ((MethodSignature) joinPoint.getSignature()).getMethod();
            return (method.isAnnotationPresent(AdditionalTrackingInfo.class)) ?
                    trackingHandler.handleTracking(trackingObj, method.getAnnotation(AdditionalTrackingInfo.class)) :
                    trackingHandler.handleTracking(trackingObj);
        }).subscribe();
    }

    @AfterReturning(value = "@annotation(io.github.honoriuss.blossom.annotations.TrackResult)", returning = "result")
    void track(Object result) {
        handleAfterTrack(result).subscribe();
    }

    @Around("@annotation(track)") //TODO Flux needed?
    public Mono<?> trackInputAndOutput(ProceedingJoinPoint joinPoint, Track track) throws Throwable {
        var result = joinPoint.proceed();
        if (result instanceof Mono<?> mono) {
            return mono.doOnSuccess(resValue -> createTracking(joinPoint, track, resValue));
        /*} else if (result instanceof Flux) {
            return ((Flux<Object>) result)
                    .doOnNext(resValue -> createTracking(joinPoint, track, resValue));*/
        }
        throw new IllegalArgumentException("Cant handle reactive stack...");
    }

    private void createTracking(ProceedingJoinPoint joinPoint, Track track, Object result) {
        createTrackingObject(joinPoint, track.parameterNames(), track.optKey(), track.optArg(), track.returnName(), result)
                .flatMap(trackingObj -> {
                    var method = ((MethodSignature) joinPoint.getSignature()).getMethod();
                    if (method.isAnnotationPresent(AdditionalTrackingInfo.class)) {
                        return trackingHandler.handleTracking(trackingObj, method.getAnnotation(AdditionalTrackingInfo.class)).thenReturn(result);
                    } else {
                        return trackingHandler.handleTracking(trackingObj).thenReturn(result);
                    }
                })
                .subscribe();
    }

    protected Mono<Void> addOptionalArgument(List<Object> args, List<String> parameterNames, Object optArg, String optKey) {
        return Mono.fromRunnable(() -> {
            if (!optKey.isEmpty()) {
                args.add(optArg);
                parameterNames.add(optKey);
            }
        });
    }

    protected Mono<Void> addAppContextArgument(ArrayList<Object> args, ArrayList<String> parameterNames, AppContext appContext) {
        return trackingAppContextHandler.addAppContext(args, parameterNames, appContext);
    }

    protected AppContext getOptionalAppContext(JoinPoint joinPoint) {
        Class<?> targetClass = joinPoint.getTarget().getClass();
        if (targetClass.isAnnotationPresent(AppContext.class)) {
            return targetClass.getAnnotation(AppContext.class);
        }

        try { //TODO check if needed --> cause user has bad architecture design?
            String methodName = joinPoint.getSignature().getName();
            Class<?>[] parameterTypes = ((org.aspectj.lang.reflect.MethodSignature) joinPoint.getSignature()).getParameterTypes();
            if (targetClass.getMethod(methodName, parameterTypes).isAnnotationPresent(AppContext.class)) {
                return targetClass.getMethod(methodName, parameterTypes).getAnnotation(AppContext.class);
            }
        } catch (NoSuchMethodException e) {
            logger.debug(e.getMessage());
        }

        return null;
    }

    protected Mono<T> createTrackingObject(JoinPoint joinPoint, String[] parameterNames, String optKey, String optArg) {
        return createTrackingObject(joinPoint, parameterNames, optKey, optArg, "", null);
    }

    protected Mono<T> createTrackingObject(JoinPoint joinPoint, String[] parameterNames, String optKey, String optArg, String resultName, Object result) {
        var args = new ArrayList<>(List.of(joinPoint.getArgs()));
        var paramNames = new ArrayList<>(List.of(parameterNames));

        return Mono.when(addOptionalArgument(args, paramNames, optArg, optKey),
                        addAppContextArgument(args, paramNames, getOptionalAppContext(joinPoint)),
                        addResultArgument(args, paramNames, resultName, result))
                .then(trackingObjectMapper.mapParameters(args, paramNames));
    }

    protected Mono<Void> handleAfterTrack(Object result) {
        return trackingObjectMapper.mapResult(result).flatMap(trackingHandler::handleTracking);
    }

    private Mono<Void> addResultArgument(List<Object> args, List<String> parameterNames, String resultName, Object result) {
        return Mono.fromRunnable(() -> {
            if (resultName != null && !resultName.isEmpty() && result != null) {
                args.add(result);
                parameterNames.add(resultName);
            }
        });
    }
}

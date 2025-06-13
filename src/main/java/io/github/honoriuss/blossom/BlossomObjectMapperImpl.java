package io.github.honoriuss.blossom;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.honoriuss.blossom.interfaces.ITrackingObjectMapper;
import io.github.honoriuss.blossom.interfaces.ITrackingParameterRegistry;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

class BlossomObjectMapperImpl implements ITrackingObjectMapper<String> {
    private final ObjectMapper mapper = new ObjectMapper();
    private final ITrackingParameterRegistry parameterRegistry;

    BlossomObjectMapperImpl(ITrackingParameterRegistry parameterRegistry) {
        this.parameterRegistry = parameterRegistry;
    }

    @Override
    public Mono<String> mapParameters(List<Object> args, List<String> parameterNames) {
        return Mono.fromCallable(() -> {
                    var resultMap = new HashMap<String, Object>();
                    addRegistryEntries(resultMap);
                    addAnnotationEntries(args, parameterNames, resultMap);
                    return resultMap;
                })
                .flatMap(this::writeValueAsJsonStringReactive)
                .subscribeOn(Schedulers.boundedElastic()); // falls blockierend
    }

    @Override
    public Mono<String> mapResult(Object result) {
        return Mono.fromCallable(() -> mapper.writeValueAsString(result))
                .onErrorMap(JsonProcessingException.class, RuntimeException::new);
    }

    private Mono<String> writeValueAsJsonStringReactive(Map<String, Object> map) {
        return Mono.fromCallable(() -> mapper.writeValueAsString(map))
                .onErrorMap(JsonProcessingException.class, RuntimeException::new);
    }

    private void addRegistryEntries(Map<String, Object> resultMap) {
        for (var provider : parameterRegistry.getTrackingParameterProviders()) {
            provider.addBaseParameters(resultMap);
        }
    }

    private void addAnnotationEntries(List<Object> args, List<String> parameterNames, Map<String, Object> resultMap) {
        for (int i = 0; i < parameterNames.size(); ++i) {
            var parameterName = parameterNames.get(i);
            if (!parameterName.isEmpty()) {
                resultMap.put(parameterName, args.get(i));
            }
        }
    }
}

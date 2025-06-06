package io.github.honoriuss.blossom.interfaces;

import io.github.honoriuss.blossom.annotations.AppContext;
import reactor.core.publisher.Mono;

import java.util.List;

public interface ITrackingAppContextHandler {
    Mono<Void> addAppContext(List<Object> args, List<String> parameterNames, AppContext appContext);
}

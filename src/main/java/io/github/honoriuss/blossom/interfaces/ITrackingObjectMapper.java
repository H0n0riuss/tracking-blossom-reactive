package io.github.honoriuss.blossom.interfaces;

import reactor.core.publisher.Mono;

import java.util.List;

public interface ITrackingObjectMapper<T> {
    Mono<T> mapParameters(List<Object> args, List<String> parameterNames);
    Mono<T> mapResult(Object obj);
}

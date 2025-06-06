package io.github.honoriuss.blossom.interfaces;

import io.github.honoriuss.blossom.annotations.AdditionalTrackingInfo;
import reactor.core.publisher.Mono;

public interface ITrackingHandler<T> {
    default Mono<Void> handleTracking(T message) {
        return Mono.empty();
    }

    default Mono<Void> handleTracking(T message, AdditionalTrackingInfo additionalTrackingInfo) {
        return Mono.empty();
    }
}

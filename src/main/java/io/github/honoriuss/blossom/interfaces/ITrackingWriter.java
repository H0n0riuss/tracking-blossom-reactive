package io.github.honoriuss.blossom.interfaces;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ITrackingWriter<T> {

    default Mono<T> writeMono(T message) {
        return Mono.fromRunnable(() -> System.out.println(message.toString()));
    }

    default Flux<T> writeFlux(T message) {
        return Flux.defer(() -> {
            System.out.println(message.toString());
            return Flux.just(message);
        });
    }
}

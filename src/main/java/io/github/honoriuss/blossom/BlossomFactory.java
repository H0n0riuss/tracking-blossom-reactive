package io.github.honoriuss.blossom;

import io.github.honoriuss.blossom.interfaces.*;

import java.util.List;

abstract class BlossomFactory {
    public static ITrackingObjectMapper<String> getDefaultObjectMapper(ITrackingParameterRegistry parameterRegistry) {
        return new BlossomObjectMapperImpl(parameterRegistry);
    }

    public static ITrackingParameterRegistry getDefaultParameterRegistry(List<ITrackingParameterProvider> parameterProviderList) {
        return new BlossomParameterRegistryImpl(parameterProviderList);
    }

    public static <T> ITrackingHandler<T> getDefaultTrackingHandler(ITrackingWriter<T> trackingWriter) {
        return new BlossomHandlerImpl<>(trackingWriter);
    }

    public static <T> ITrackingWriter<T> getDefaultWriter() {
        return new BlossomWriterImpl<>();
    }

    public static ITrackingAppContextHandler getDefaultAppContextHandler() {
        return new BlossomAppContextHandlerImpl();
    }
}

package io.github.honoriuss.blossom;

import io.github.honoriuss.blossom.interfaces.ITrackingParameterProvider;
import io.github.honoriuss.blossom.interfaces.ITrackingParameterRegistry;
import org.slf4j.LoggerFactory;

import java.util.List;

class BlossomParameterRegistryImpl implements ITrackingParameterRegistry {
    private final List<ITrackingParameterProvider> parameterProviderList;

    BlossomParameterRegistryImpl(List<ITrackingParameterProvider> parameterProviderList) {
        this.parameterProviderList = parameterProviderList;

        var logger = LoggerFactory.getLogger(BlossomParameterRegistryImpl.class);
        logger.debug("{} parameter provider registered.", parameterProviderList.size());
    }

    @Override
    public List<ITrackingParameterProvider> getTrackingParameterProviders() {
        return parameterProviderList;
    }
}

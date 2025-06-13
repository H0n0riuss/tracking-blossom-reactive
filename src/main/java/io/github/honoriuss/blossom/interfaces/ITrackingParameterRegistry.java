package io.github.honoriuss.blossom.interfaces;

import java.util.List;

public interface ITrackingParameterRegistry {
    List<ITrackingParameterProvider> getTrackingParameterProviders();
}

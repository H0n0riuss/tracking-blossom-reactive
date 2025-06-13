package io.github.honoriuss.blossom;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "blossom.config")
class BlossomPropertiesConfig {
    private boolean enabled;
    private String sessionIdName = "session_id";
    private String timestampName = "timestamp";

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getSessionIdName() {
        return sessionIdName;
    }

    public void setSessionIdName(String sessionIdName) {
        this.sessionIdName = sessionIdName;
    }

    public String getTimestampName() {
        return timestampName;
    }

    public void setTimestampName(String timestampName) {
        this.timestampName = timestampName;
    }
}

@Configuration
@ConfigurationProperties(prefix = "blossom.optional")
class BlossomPropertiesOptional {
    private Map<String, String> headers;

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public boolean isMapNotEmpty() {
        return headers != null && !headers.isEmpty();
    }
}

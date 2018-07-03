package org.fenixedu.bennu.connect.client;

import org.fenixedu.commons.configuration.ConfigurationInvocationHandler;
import org.fenixedu.commons.configuration.ConfigurationManager;
import org.fenixedu.commons.configuration.ConfigurationProperty;

public class ConnectClientConfiguration {

    @ConfigurationManager(description = "Bennu Connect Client Configuration")
    public static interface ConfigurationProperties {

        @ConfigurationProperty(key = "connect.enabled", defaultValue = "false", description = "Whether the Connect client is enabled")
        public Boolean connectEnabled();

        @ConfigurationProperty(key = "connect.serverUrl", defaultValue = "http://localhost:8080/",
                description = "The base URL of the Connect server")
        public String connectServerUrl();

        @ConfigurationProperty(key = "connect.serviceUrl", description = "The URL to jump to when no callback is specified")
        public String connectServiceUrl();

        @ConfigurationProperty(key = "connect.apiKey", defaultValue = "changeme",
                description = "The API Key used to authenticate this application when synchronizing signing keys with Connect")
        public String connectApiKey();

    }

    public static ConfigurationProperties getConfiguration() {
        return ConfigurationInvocationHandler.getConfiguration(ConfigurationProperties.class);
    }

}

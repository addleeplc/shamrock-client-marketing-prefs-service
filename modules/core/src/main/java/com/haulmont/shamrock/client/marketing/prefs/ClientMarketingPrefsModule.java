package com.haulmont.shamrock.client.marketing.prefs;

import com.haulmont.monaco.annotations.Module;
import com.haulmont.monaco.container.ModuleLoader;

@Module(
        name = "shamrock-client-marketing-prefs-service-module",
        depends = {
                "monaco-jetty",
                "monaco-core",
                "monaco-config",
                "monaco-model",
                "monaco-graylog-reporter",
                "monaco-sentry-reporter",
                "monaco-mybatis",
                "monaco-ds-sybase",
                "monaco-ds-postgresql",
                "monaco-rabbit-mq",
                "monaco-unirest"
        }
)
public class ClientMarketingPrefsModule extends ModuleLoader {

    public static final String IDENTITY_DELETION_MQ = "clientIdentityDeletion.mq";

    public ClientMarketingPrefsModule() {
        super();
        packages(ClientMarketingPrefsModule.class.getPackageName());
    }
}

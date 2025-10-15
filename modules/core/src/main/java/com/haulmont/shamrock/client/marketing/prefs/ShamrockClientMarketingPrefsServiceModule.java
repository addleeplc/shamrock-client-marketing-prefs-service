package com.haulmont.shamrock.client.marketing.prefs;

import com.haulmont.monaco.annotations.Module;
import com.haulmont.monaco.container.ModuleLoader;

@Module(
        name = "shamrock-client-marketing-prefs-service-module",
        depends = {
                "monaco-jetty",
                "monaco-core",
                "monaco-config",
                "monaco-graylog-reporter",
                "monaco-sentry-reporter"
        }
)
public class ShamrockClientMarketingPrefsServiceModule extends ModuleLoader {

    public ShamrockClientMarketingPrefsServiceModule () {
        super();
        packages(ShamrockClientMarketingPrefsServiceModule.class.getPackageName());
    }
}

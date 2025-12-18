/*
 * Copyright (c) 2025 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */

package com.haulmont.shamrock.client.marketing.prefs;

import com.haulmont.monaco.rs.jersey.Application;

import javax.ws.rs.ApplicationPath;

@ApplicationPath("/")
public class ClientMarketingPrefsApplication extends Application {
    public ClientMarketingPrefsApplication() {
        super();
        packages(ClientMarketingPrefsApplication.class.getPackage().getName());
    }
}

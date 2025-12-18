/*
 * Copyright 2008 - 2025 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */

package com.haulmont.shamrock.client.marketing.prefs.mybatis;

import org.picocontainer.annotations.Component;

@Component
public class ClientMarketingPrefsSqlSessionFactory extends AbstractSqlSessionFactory {
    public static final String MYBATIS_NAMESPACE_PREFIX = ClientMarketingPrefsSqlSessionFactory.class.getPackageName();

    public ClientMarketingPrefsSqlSessionFactory() {
        super("shamrock-client-marketing-prefs-ds.xml", MYBATIS_NAMESPACE_PREFIX);
    }
}

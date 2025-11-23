/*
 * Copyright 2008 - 2025 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */

package com.haulmont.shamrock.client.marketing.prefs.db.mybatis;

import org.picocontainer.annotations.Component;

@Component
@Deprecated // get rid of the bean when there is no client using preferences in sybase and all the clients are imported
public class ShamrockSqlSessionFactory extends AbstractSqlSessionFactory {
    public static final String MYBATIS_NAMESPACE_PREFIX = ShamrockSqlSessionFactory.class.getPackageName();

    public ShamrockSqlSessionFactory() {
        super("shamrock-ds.xml", MYBATIS_NAMESPACE_PREFIX);
    }
}

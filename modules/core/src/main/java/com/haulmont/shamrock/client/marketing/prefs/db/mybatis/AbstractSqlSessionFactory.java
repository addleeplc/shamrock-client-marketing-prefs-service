/*
 * Copyright 2008 - 2025 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */

package com.haulmont.shamrock.client.marketing.prefs.db.mybatis;

import com.haulmont.monaco.mybatis.SqlSessionFactoryResource;

public class AbstractSqlSessionFactory extends SqlSessionFactoryResource {
    private final String namespace;

    public AbstractSqlSessionFactory(String configurationResource, String namespace) {
        super(configurationResource);

        this.namespace = namespace;
    }

    public String getStatement(String name) {
        return namespace + "." + name;
    }
}

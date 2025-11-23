/*
 * Copyright 2008 - 2025 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */

package com.haulmont.monaco.jackson;

import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

public class ObjectMapperFactory extends ObjectMapperContainer {
    protected XmlMapper xmlMapper;

    public synchronized XmlMapper xmlMapper() {
        if (this.xmlMapper == null) {
            XmlMapper mapper = this.createXmlMapper();
            this.xmlMapper = mapper;
        }

        return this.xmlMapper;
    }

    protected XmlMapper createXmlMapper() {
        XmlMapper mapper = new XmlMapper();
        this.configure(mapper);
        this.configureSerializationInclusion(mapper);
        this.configureModules(mapper);
        if (!this.isFailOnUnknow()) {
            SimpleFilterProvider filterProvider = new SimpleFilterProvider();
            filterProvider.setFailOnUnknownId(false);
            mapper.setFilterProvider(filterProvider);
        }

        return mapper;
    }
}

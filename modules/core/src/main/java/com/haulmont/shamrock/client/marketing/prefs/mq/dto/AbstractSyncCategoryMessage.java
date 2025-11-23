/*
 * Copyright 2008 - 2025 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */

package com.haulmont.shamrock.client.marketing.prefs.mq.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.haulmont.shamrock.client.marketing.prefs.dto.Category;

import java.util.function.Supplier;

@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class AbstractSyncCategoryMessage extends AbstractSyncModelMessage<AbstractSyncCategoryMessage.Data> {
    public static <R extends AbstractSyncCategoryMessage> R create(Category category, Supplier<R> constructor) {
        R res = constructor.get();

        AbstractSyncCategoryMessage.Data data = new AbstractSyncCategoryMessage.Data();
        data.setCategory(category);

        res.setData(data);

        return res;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Data extends AbstractSyncModelMessage.Data {
        @JsonProperty("category")
        private Category category;

        public Category getCategory() {
            return category;
        }

        public void setCategory(Category category) {
            this.category = category;
        }
    }
}

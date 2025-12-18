/*
 * Copyright 2008 - 2025 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */

package com.haulmont.shamrock.client.marketing.prefs.model;

import com.fasterxml.jackson.annotation.*;
import com.haulmont.shamrock.client.marketing.prefs.jackson.Views;

import java.util.Collection;

@JsonPropertyOrder({"id", "code", "name", "description", "categories"})
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Category extends Identifier {
    @JsonProperty("name")
    @JsonView(Views.All.class)
    protected String name;

    @JsonProperty("description")
    @JsonView(Views.All.class)
    protected String description;

    @JsonProperty("channels")
    @JsonView(Views.All.class)
    private Collection<Channel> channels;

    @JsonProperty("categories")
    @JsonView(Views.All.class)
    private Collection<Category> categories;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Collection<Channel> getChannels() {
        return channels;
    }

    public void setChannels(Collection<Channel> channels) {
        this.channels = channels;
    }

    public Collection<Category> getCategories() {
        return categories;
    }

    public void setCategories(Collection<Category> categories) {
        this.categories = categories;
    }

    @JsonPropertyOrder({"channel_code", "opt_in"})
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true, value = {"id", "code"})
    public static class Channel extends Identifier {
        @JsonProperty("opt_in")
        @JsonView(Views.Store.class)
        private OptIn optIn;

        @Override
        @JsonProperty("channel_code")
        @JsonView(Views.Store.class)
        public String getCode() {
            return super.getCode();
        }

        @Override
        @JsonProperty("channel_code")
        @JsonView(Views.Store.class)
        public void setCode(String code) {
            super.setCode(code);
        }

        public OptIn getOptIn() {
            return optIn;
        }

        public void setOptIn(OptIn optIn) {
            this.optIn = optIn;
        }
    }

    @JsonPropertyOrder({"default", "editable"})
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class OptIn {
        @JsonProperty("default")
        @JsonView(Views.Store.class)
        private Boolean optIn;

        @JsonProperty("editable")
        @JsonView(Views.Store.class)
        private Boolean editable;

        public Boolean getEditable() {
            return editable;
        }

        public void setEditable(Boolean editable) {
            this.editable = editable;
        }

        public Boolean getOptIn() {
            return optIn;
        }

        public void setOptIn(Boolean optIn) {
            this.optIn = optIn;
        }
    }

    public static class PrefsView extends Category {
        public PrefsView(Category category) {
            this.id = category.id;
            this.code = category.code;
            this.name = category.name;
        }

        @JsonIgnore
        @Override
        public String getDescription() {
            return super.getDescription();
        }
    }
}

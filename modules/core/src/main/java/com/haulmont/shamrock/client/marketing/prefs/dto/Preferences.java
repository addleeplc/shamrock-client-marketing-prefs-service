package com.haulmont.shamrock.client.marketing.prefs.dto;

import com.fasterxml.jackson.annotation.*;
import com.haulmont.shamrock.client.marketing.prefs.jackson.Views;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@JsonPropertyOrder({"opt_in", "channels", "categories"})
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Preferences {
    @JsonProperty("opt_in")
    @JsonView(Views.Store.class)
    protected Boolean optIn;

    @JsonProperty("channels")
    @JsonView(Views.Store.class)
    protected Collection<ChannelOptIn> channels;

    @JsonProperty("categories")
    @JsonView(Views.Store.class)
    protected Collection<CategoryOptIn> categories;

    public Boolean getOptIn() {
        return optIn;
    }

    public void setOptIn(Boolean optIn) {
        this.optIn = optIn;
    }

    public Collection<ChannelOptIn> getChannels() {
        return channels;
    }

    public void setChannels(Collection<ChannelOptIn> channels) {
        this.channels = channels;
    }

    public Collection<CategoryOptIn> getCategories() {
        return categories;
    }

    public void setCategories(Collection<CategoryOptIn> categories) {
        this.categories = categories;
    }

    @JsonPropertyOrder({"category", "channels", "categories"})
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class CategoryOptIn {
        @JsonProperty("category")
        @JsonView(Views.Store.class)
        protected Category category;

        @JsonProperty("channels")
        @JsonView(Views.Store.class)
        protected Collection<ChannelOptIn> channels;

        @JsonProperty("categories")
        @JsonView(Views.Store.class)
        protected Collection<CategoryOptIn> categories;

        public Category getCategory() {
            return category;
        }

        public void setCategory(Category category) {
            this.category = category;
        }

        public Collection<ChannelOptIn> getChannels() {
            return channels;
        }

        public void setChannels(Collection<ChannelOptIn> channels) {
            this.channels = channels;
        }

        public Collection<CategoryOptIn> getCategories() {
            return categories;
        }

        public void setCategories(Collection<CategoryOptIn> categories) {
            this.categories = categories;
        }
    }

    public static class CategoryOptInPrefsView extends CategoryOptIn {
        public CategoryOptInPrefsView(CategoryOptIn from, Set<String> onlyChannels) {
            this.category = new Category.PrefsView(from.category);

            if (from.channels != null) {
                this.channels = from.channels.stream()
                        .filter(Objects::nonNull)
                        .filter(ch -> onlyChannels == null || ch.code != null && onlyChannels.contains(ch.code))
                        .map(ChannelOptInPrefsView::new)
                        .collect(Collectors.toUnmodifiableList());
            }

            if (from.categories != null) {
                this.categories = from.categories.stream()
                        .filter(Objects::nonNull)
                        .map(cat -> new CategoryOptInPrefsView(cat, onlyChannels))
                        .filter(cat -> CollectionUtils.isNotEmpty(cat.channels) || CollectionUtils.isNotEmpty(cat.categories))
                        .collect(Collectors.toUnmodifiableList());
            }
        }
    }

    @JsonPropertyOrder({"code", "opt_in"})
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ChannelOptIn {
        @JsonProperty("code")
        @JsonView(Views.Store.class)
        protected String code;

        @JsonProperty("opt_in")
        @JsonView(Views.Store.class)
        protected Boolean optIn;

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public Boolean getOptIn() {
            return optIn;
        }

        public void setOptIn(Boolean optIn) {
            this.optIn = optIn;
        }
    }

    public static class ChannelOptInPrefsView extends ChannelOptIn {
        public ChannelOptInPrefsView(ChannelOptIn ch) {
            this.code = ch.code;
            this.optIn = ch.optIn;
        }
    }

    public static class PrefsView extends Preferences {
        public PrefsView(Preferences from) {
            this(from, null);
        }

        public PrefsView(Preferences from, Set<String> onlyChannels) {
            this.optIn = from.optIn;

            if (from.getChannels() != null) {
                this.channels = from.channels.stream()
                        .filter(Objects::nonNull)
                        .filter(ch -> onlyChannels == null || ch.code != null && onlyChannels.contains(ch.code))
                        .map(ChannelOptInPrefsView::new)
                        .collect(Collectors.toUnmodifiableList());
            }

            if (from.getCategories() != null) {
                this.categories = from.categories.stream()
                        .filter(Objects::nonNull)
                        .map(cat -> new CategoryOptInPrefsView(cat, onlyChannels))
                        .filter(cat -> CollectionUtils.isNotEmpty(cat.channels) || CollectionUtils.isNotEmpty(cat.categories))
                        .collect(Collectors.toUnmodifiableList());

                if (this.categories.isEmpty()) {
                    this.categories = null;
                }
            }
        }
    }
}

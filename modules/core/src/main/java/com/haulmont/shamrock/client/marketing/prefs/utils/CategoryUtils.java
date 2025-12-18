package com.haulmont.shamrock.client.marketing.prefs.utils;

import com.haulmont.shamrock.client.marketing.prefs.model.Category;
import com.haulmont.shamrock.client.marketing.prefs.storage.model.CategoryChannel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class CategoryUtils {
    private CategoryUtils() {
    }

    public static Category convert(com.haulmont.shamrock.client.marketing.prefs.storage.model.Category category) {
        if (category == null) {
            return null;
        }

        Category res = new Category();

        res.setId(category.getId());
        res.setCode(category.getCode());
        res.setName(category.getName());
        res.setDescription(category.getDescription());
        res.setCategories(convert(category.getChildren()));
        res.setChannels(convertChannels(category.getChannels()));

        return res;
    }

    public static Collection<Category> convert(Collection<com.haulmont.shamrock.client.marketing.prefs.storage.model.Category> categories) {
        if (categories == null) {
            return null;
        }

        List<Category> res = new ArrayList<>(categories.size());

        for (com.haulmont.shamrock.client.marketing.prefs.storage.model.Category category : categories) {
            res.add(convert(category));
        }

        return res;
    }

    public static Category.Channel convert(CategoryChannel channel) {
        if (channel == null) {
            return null;
        }

        Category.Channel res = new Category.Channel();

        res.setCode(channel.getCode());
        if (channel.getOptIn() != null || channel.getEditable() != null) {
            Category.OptIn optIn = new Category.OptIn();

            optIn.setOptIn(channel.getOptIn());
            optIn.setEditable(channel.getEditable());

            res.setOptIn(optIn);
        }

        return res;
    }

    public static Collection<Category.Channel> convertChannels(Collection<CategoryChannel> channels) {
        if (channels == null) {
            return null;
        }

        List<Category.Channel> res = new ArrayList<>(channels.size());

        for (CategoryChannel channel : channels) {
            res.add(convert(channel));
        }

        return res;
    }

    public static com.haulmont.shamrock.client.marketing.prefs.storage.model.Category convert(Category category) {
        if (category == null) {
            return null;
        }

        com.haulmont.shamrock.client.marketing.prefs.storage.model.Category res = new com.haulmont.shamrock.client.marketing.prefs.storage.model.Category();

        res.setId(category.getId());
        res.setCode(category.getCode());
        res.setName(category.getName());
        res.setDescription(category.getDescription());
        res.setChildren(convertCategoriesToModel(category.getCategories()));
        res.setChannels(convertChannelsToModel(category.getChannels()));

        return res;
    }

    public static Collection<CategoryChannel> convertChannelsToModel(Collection<Category.Channel> channels) {
        if (channels == null) {
            return null;
        }

        List<CategoryChannel> res = new ArrayList<>(channels.size());

        for (Category.Channel channel : channels) {
            res.add(convert(channel));
        }

        return res;
    }

    public static Collection<com.haulmont.shamrock.client.marketing.prefs.storage.model.Category> convertCategoriesToModel(Collection<Category> categories) {
        if (categories == null) {
            return null;
        }

        List<com.haulmont.shamrock.client.marketing.prefs.storage.model.Category> res = new ArrayList<>(categories.size());

        for (Category category : categories) {
            res.add(convert(category));
        }

        return res;
    }

    public static CategoryChannel convert(Category.Channel channel) {
        if (channel == null) {
            return null;
        }

        CategoryChannel res = new CategoryChannel();

        res.setCode(channel.getCode());
        if (channel.getOptIn() != null) {
            res.setOptIn(channel.getOptIn().getOptIn());
            res.setEditable(channel.getOptIn().getEditable());
        }

        return res;
    }

    public static String print(com.haulmont.shamrock.client.marketing.prefs.storage.model.Category category) {
        if (category == null) {
            return null;
        }

        if (category.getCode() != null) {
            return category.getCode();
        }

        if (category.getId() != null) {
            return category.getId().toString();
        }

        return "empty";
    }

    public static String print(Category category) {
        if (category == null) {
            return null;
        }

        if (category.getCode() != null) {
            return category.getCode();
        }

        if (category.getId() != null) {
            return category.getId().toString();
        }

        return "empty";
    }
}

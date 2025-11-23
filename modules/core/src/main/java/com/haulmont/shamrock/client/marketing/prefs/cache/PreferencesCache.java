/*
 * Copyright 2008 - 2025 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */

package com.haulmont.shamrock.client.marketing.prefs.cache;

import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import com.haulmont.bali.lang.BooleanUtils;
import com.haulmont.bali.lang.StringUtils;
import com.haulmont.monaco.model.cache.CacheManagement;
import com.haulmont.shamrock.client.marketing.prefs.CategoriesService;
import com.haulmont.shamrock.client.marketing.prefs.ChannelsService;
import com.haulmont.shamrock.client.marketing.prefs.ServiceConfiguration;
import com.haulmont.shamrock.client.marketing.prefs.dto.Category;
import com.haulmont.shamrock.client.marketing.prefs.dto.Channel;
import com.haulmont.shamrock.client.marketing.prefs.dto.Preferences;
import com.haulmont.shamrock.client.marketing.prefs.eventbus.ModelSyncEventBus;
import com.haulmont.shamrock.client.marketing.prefs.mq.dto.*;
import org.apache.commons.collections4.CollectionUtils;
import org.picocontainer.annotations.Component;
import org.picocontainer.annotations.Inject;

import java.util.*;

@Component
public class PreferencesCache implements CacheManagement {
    @Inject
    private ServiceConfiguration serviceConfiguration;

    @Inject
    private ChannelsService channelsService;

    @Inject
    private CategoriesService categoriesService;

    @Inject
    private ModelSyncEventBus modelSyncEventBus;


    private SingletonCache<PrefsView> defaultPreferences;


    public void start() {
        defaultPreferences = new SingletonCache<>(() -> {
            Collection<Category> allCategories = categoriesService.getAll();
            return new PrefsView(
                    getDefaultPreferences(allCategories),
                    getUnmodifiableCategoryChannels(allCategories)
            );
        });

        modelSyncEventBus.register(this);
    }

    public void stop() {
        modelSyncEventBus.unregister(this);
    }

    public PrefsView getDefault() {
        return defaultPreferences.get();
    }

    private Preferences getDefaultPreferences(Collection<Category> allCategories) {
        Preferences res = new Preferences();

        res.setOptIn(getPrefsOptInByDefault());

        Collection<Channel> allChannels = channelsService.getAll();

        if (CollectionUtils.isNotEmpty(allChannels)) {
            List<Preferences.ChannelOptIn> channelOptIns = new ArrayList<>(allChannels.size());

            boolean prefsChannelsOptInByDefault = getPrefsChannelsOptInByDefault();
            for (Channel channel : allChannels) {
                Preferences.ChannelOptIn optIn = createChannelOptIn(channel, prefsChannelsOptInByDefault);
                channelOptIns.add(optIn);
            }

            res.setChannels(channelOptIns);
        }

        if (CollectionUtils.isNotEmpty(allCategories)) {
            List<Preferences.CategoryOptIn> categoryOptIns = new ArrayList<>(allCategories.size());

            for (Category category : allCategories) {
                Preferences.CategoryOptIn optIn = createCategoryOptIn(category);
                categoryOptIns.add(optIn);
            }

            propagateInheritedOptIns(null, categoryOptIns);

            res.setCategories(categoryOptIns);
        }

        return res;
    }

    private Preferences.CategoryOptIn createCategoryOptIn(Category category) {
        if (category == null) {
            return null;
        }

        Preferences.CategoryOptIn res = new Preferences.CategoryOptIn();

        res.setCategory(new Category.PrefsView(category));

        if (CollectionUtils.isNotEmpty(category.getChannels())) {
            List<Preferences.ChannelOptIn> categoryChannelOptIns = new ArrayList<>(category.getChannels().size());

            for (Category.Channel channel : category.getChannels()) {
                if (channel.getOptIn() != null) {
                    categoryChannelOptIns.add(createChannelOptIn(channel.getCode(), channel.getOptIn().getOptIn()));
                }
            }

            res.setChannels(categoryChannelOptIns);
        }

        if (CollectionUtils.isNotEmpty(category.getCategories())) {
            List<Preferences.CategoryOptIn> categoryOptIns = new ArrayList<>(category.getCategories().size());

            for (Category child : category.getCategories()) {
                Preferences.CategoryOptIn childOptIns = createCategoryOptIn(child);
                if (childOptIns != null) {
                    categoryOptIns.add(childOptIns);
                }
            }

            res.setCategories(categoryOptIns);
        }

        return res;
    }

    private Preferences.ChannelOptIn createChannelOptIn(String code, Boolean value) {
        if (StringUtils.isBlank(code)) {
            return null;
        }

        Preferences.ChannelOptIn res = new Preferences.ChannelOptIn();

        res.setCode(code);
        res.setOptIn(value);

        return res;
    }

    private Preferences.ChannelOptIn createChannelOptIn(Channel channel, Boolean value) {
        if (channel == null) {
            return null;
        }

        return createChannelOptIn(channel.getCode(), value);
    }

    public void propagateInheritedOptIns(Collection<Preferences.ChannelOptIn> optIns, Collection<Preferences.CategoryOptIn> toCategories) {
        if (CollectionUtils.isNotEmpty(toCategories)) {
            for (Preferences.CategoryOptIn child : toCategories) {
                if (optIns != null && !optIns.isEmpty() && CollectionUtils.isEmpty(child.getChannels())) {
                    child.setChannels(new ArrayList<>(optIns));
                }

                propagateInheritedOptIns(child.getChannels(), child.getCategories());
            }
        }
    }

    private boolean getPrefsOptInByDefault() {
        return Optional.ofNullable(serviceConfiguration.getPrefsOptInByDefault()).orElse(true);
    }

    private boolean getPrefsChannelsOptInByDefault() {
        return Optional.ofNullable(serviceConfiguration.getPrefsChannelsOptInByDefault()).orElse(true);
    }

    @AllowConcurrentEvents
    @Subscribe
    private void handleChannelCreated(ChannelCreated message) {
        channelsUpdate(message);
    }

    @AllowConcurrentEvents
    @Subscribe
    private void handleChannelUpdated(ChannelUpdated message) {
        channelsUpdate(message);
    }

    @AllowConcurrentEvents
    @Subscribe
    private void handleChannelDeleted(ChannelDeleted message) {
        channelsUpdate(message);
    }

    @AllowConcurrentEvents
    @Subscribe
    private void handleCategoryCreated(CategoryCreated message) {
        categoriesUpdate(message);
    }

    @AllowConcurrentEvents
    @Subscribe
    private void handleCategoryUpdated(CategoryUpdated message) {
        categoriesUpdate(message);
    }

    @AllowConcurrentEvents
    @Subscribe
    private void handleCategoryDeleted(CategoryDeleted message) {
        categoriesUpdate(message);
    }

    private void channelsUpdate(AbstractSyncChannelMessage message) {
        defaultPreferences.refresh();
    }

    private void categoriesUpdate(AbstractSyncCategoryMessage message) {
        defaultPreferences.refresh();
    }

    @Override
    public long getSize() {
        return 1;
    }

    @Override
    public void invalidateAll() {
        defaultPreferences.refresh();
    }

    private Map<UUID, Set<String>> getUnmodifiableCategoryChannels(Collection<Category> allCategories) {
        if (allCategories == null) {
            return Collections.emptyMap();
        }

        Map<UUID, Set<String>> res = new HashMap<>();

        for (Category category : allCategories) {
            if (category == null || category.getCategories() == null) {
                continue;
            }

            if (category.getChannels() != null) {
                for (Category.Channel categoryChannel : category.getChannels()) {
                    if (categoryChannel != null && categoryChannel.getOptIn() != null && BooleanUtils.isFalse(categoryChannel.getOptIn().getEditable())) {
                        res.compute(category.getId(), (cId, chCodes) -> {
                            chCodes = chCodes == null ? new HashSet<>() : chCodes;
                            chCodes.add(categoryChannel.getCode());
                            return chCodes;
                        });
                    }
                }
            }

            res.putAll(getUnmodifiableCategoryChannels(category.getCategories()));
        }

        return res;
    }

    public static class PrefsView extends Preferences.PrefsView {
        private final Map<UUID, Set<String>> unmodifiableCategoryChannels;

        public PrefsView(Preferences from, Map<UUID, Set<String>> unmodifiableCategoryChannels) {
            super(from);
            this.unmodifiableCategoryChannels = unmodifiableCategoryChannels;
        }

        public boolean isEditable(UUID categoryId, String channelCode) {
            if (categoryId == null || channelCode == null) {
                return true;
            }

            Set<String> unmodifiableChannelCodes = unmodifiableCategoryChannels.get(categoryId);

            return unmodifiableChannelCodes == null || !unmodifiableChannelCodes.contains(channelCode);
        }
    }
}

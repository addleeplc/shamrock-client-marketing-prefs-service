/*
 * Copyright 2008 - 2025 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */

package com.haulmont.shamrock.client.marketing.prefs;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.haulmont.bali.lang.BooleanUtils;
import com.haulmont.bali.lang.StringUtils;
import com.haulmont.monaco.ServiceException;
import com.haulmont.monaco.response.ErrorCode;
import com.haulmont.shamrock.client.marketing.prefs.cache.ClientByIdCache;
import com.haulmont.shamrock.client.marketing.prefs.cache.ClientPrefsCache;
import com.haulmont.shamrock.client.marketing.prefs.cache.ClientsByEmailCache;
import com.haulmont.shamrock.client.marketing.prefs.cache.PreferencesCache;
import com.haulmont.shamrock.client.marketing.prefs.db.ClientPrefsRepository;
import com.haulmont.shamrock.client.marketing.prefs.dto.*;
import com.haulmont.shamrock.client.marketing.prefs.dto.Preferences.CategoryOptIn;
import com.haulmont.shamrock.client.marketing.prefs.dto.Preferences.ChannelOptIn;
import com.haulmont.shamrock.client.marketing.prefs.jackson.Views;
import com.haulmont.shamrock.client.marketing.prefs.legacy.ShamrockClientPrefsService;
import com.haulmont.shamrock.client.marketing.prefs.mq.ClientPrefsMessagingService;
import com.haulmont.shamrock.client.marketing.prefs.utils.ClientPrefsUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.picocontainer.annotations.Component;
import org.picocontainer.annotations.Inject;
import org.slf4j.Logger;

import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Component
public class ClientPrefsService extends AbstractCachedService<ClientId, ClientPrefs, ClientPrefsCache> {
    @Inject
    private Logger logger;

    @Inject
    private ClientPrefsRepository clientPrefsRepository;

    @Inject
    private ClientByIdCache clientByIdCache;

    @Inject
    private ClientsByEmailCache clientsByEmailCache;

    @Inject
    private PreferencesCache preferencesCache;

    @Inject
    private ChannelsService channelsService;

    @Inject
    private CategoriesService categoriesService;

    @Inject
    private ShamrockClientPrefsService shamrockClientPrefsService;

    @Inject
    private ClientPrefsMessagingService clientPrefsMessagingService;

    public ClientPrefsService(ClientPrefsCache cache) {
        super(cache);
    }

    public ClientPrefs get(ClientId id) {
        return get(id, null);
    }

    public Preferences get() {
        return new Preferences.PrefsView(preferencesCache.getDefault());
    }

    public ClientPrefs get(ClientId id, Set<String> onlyChannels) {
        if (id.getId() != null) {
            Client cached = clientByIdCache.get(id);
            if (cached != null) {
                id = cached;
            }
        }

        ClientPrefs clientPrefs = cache.get(id);
        if (clientPrefs == null) {
            List<ChannelOptIn> legacyChannelPreferences = shamrockClientPrefsService.getPreferences(id);
            if (CollectionUtils.isEmpty(legacyChannelPreferences)) {
                throw Errors.PREFS_NOT_FOUND;
            }

            Preferences preferences = new Preferences();
            preferences.setChannels(legacyChannelPreferences);

            clientPrefs = new ClientPrefs(id, preferences);
        }

        return refineClientPrefs(clientPrefs, onlyChannels);
    }

    public ClientPrefs add(ClientId id, Preferences preferences) {
        if (cache.get(id) != null) {
            throw Errors.PREFS_ALREADY_EXIST;
        }

        Supplier<Preferences> prefsByDefault = Suppliers.memoize(() ->
                new Preferences.PrefsView(preferencesCache.getDefault())
        );

        if (preferences.getOptIn() == null) {
            preferences.setOptIn(prefsByDefault.get().getOptIn());
        }

        if (CollectionUtils.isEmpty(preferences.getChannels())) {
            Collection<ChannelOptIn> channels = prefsByDefault.get().getChannels();
            if (CollectionUtils.isNotEmpty(channels)) {
                preferences.setChannels(channels);
            }
        }

        if (CollectionUtils.isEmpty(preferences.getCategories())) {
            Collection<CategoryOptIn> categoryOptIns = prefsByDefault.get().getCategories();
            if (CollectionUtils.isNotEmpty(categoryOptIns)) {
                preferences.setCategories(categoryOptIns);
            }
        }

        prepareClientId(id);
        prepareClientPrefs(preferences);

        com.haulmont.shamrock.client.marketing.prefs.model.ClientPrefs prefs = ClientPrefsUtils.convert(id, preferences, Views.Store.class);

        doCacheMutatingAction(id, () -> {
            if (!clientPrefsRepository.add(prefs)) {
                throw Errors.PREFS_ALREADY_EXIST;
            }
        });

        ClientPrefs res = get(id);
        if (res != null) {
            clientPrefsMessagingService.publish(res.getPreferences());
        }

        return res;
    }

    public ClientPrefs reset(ClientId id) {
        prepareClientId(id);

        com.haulmont.shamrock.client.marketing.prefs.model.ClientId clientId = ClientPrefsUtils.convert(id);
        com.haulmont.shamrock.client.marketing.prefs.model.ClientPrefs prefs = ClientPrefsUtils.convert(id, preferencesCache.getDefault(), Views.Store.class);

        doCacheMutatingAction(id, () -> {
            if (clientPrefsRepository.update(clientId, prefs) == 0) {
                throw Errors.PREFS_NOT_FOUND;
            }
        });

        ClientPrefs res = get(id);
        if (res != null) {
            clientPrefsMessagingService.publish(res.getPreferences());
        }

        return res;
    }

    public ClientPrefs update(ClientId id, Preferences preferences) {
        prepareClientId(id);
        prepareClientPrefs(preferences);

        com.haulmont.shamrock.client.marketing.prefs.model.ClientId clientId = ClientPrefsUtils.convert(id);

        ClientPrefs clientPrefs = get(id);
        Preferences updatedPrefs = applyPreferencesChanges(preferences, clientPrefs.getPreferences());

        com.haulmont.shamrock.client.marketing.prefs.model.ClientPrefs prefs = ClientPrefsUtils.convert(id, updatedPrefs, Views.Store.class);

        doCacheMutatingAction(id, () -> {
            if (clientPrefsRepository.update(clientId, prefs) == 0) {
                throw Errors.PREFS_NOT_FOUND;
            }
        });

        ClientPrefs res = get(id);
        if (res != null) {
            clientPrefsMessagingService.publish(res.getPreferences());
        }

        return res;
    }

    private void prepareClientId(ClientId id) {
        if (id == null) {
            return;
        }

        if (id.getId() != null) {
            Client client = clientByIdCache.get(id);
            if (client != null) {
                if (client.getId() != null) {
                    id.setId(client.getId());
                }
                if (StringUtils.isNoneBlank(client.getEmail()) && StringUtils.isNotBlank(id.getUid())) {
                    id.setEmail(client.getEmail());
                }
            }
        } else if (StringUtils.isNoneBlank(id.getEmail())) {
            List<Client> clientsByEmail = clientsByEmailCache.get(id.getEmail());
            if (CollectionUtils.isNotEmpty(clientsByEmail) && clientsByEmail.size() == 1) {
                Client client = clientsByEmail.get(0);
                if (client != null && client.getId() != null) {
                    id.setId(client.getId());
                }
            }
        }
    }

    public void delete(ClientId id) {
        com.haulmont.shamrock.client.marketing.prefs.model.ClientId clientId = ClientPrefsUtils.convert(id);
        doCacheMutatingAction(id, () -> {
            if (!clientPrefsRepository.delete(clientId)) {
                throw Errors.PREFS_NOT_FOUND;
            }
        });
    }

    private void prepareClientPrefs(Preferences preferences) {
        preferences.setChannels(prepareChannelsOptIns(preferences.getChannels()));

        if (preferences.getCategories() != null) {
            for (CategoryOptIn category : preferences.getCategories()) {
                prepareClientPrefsCategoryOptIn(category);
            }
        }

        if (BooleanUtils.isFalse(preferences.getOptIn())) {
            Preferences res = new Preferences.PrefsView(preferencesCache.getDefault());
            res.getChannels().forEach(ch -> ch.setOptIn(false));

            preferences.setChannels(res.getChannels());
            preferences.setCategories(res.getCategories());
        }
    }

    private void prepareClientPrefsCategoryOptIn(CategoryOptIn category) {
        if (category == null) {
            throw new ServiceException(ErrorCode.BAD_REQUEST, "Empty category opt-in isn't allowed");
        }

        if (category.getCategory() == null || !category.getCategory().isDefined()) {
            throw new ServiceException(ErrorCode.BAD_REQUEST, "Category opt-in isn't defined properly (category: " + category.getCategory() + ")");
        }

        Category cat = category.getCategory();
        if (!cat.isDefined()) {
            throw new ServiceException(ErrorCode.BAD_REQUEST, "Category without id or code found (category: " + category.getCategory() + ")");
        }

        Category cached = categoriesService.get(cat);
        if (cached == null) {
            throw new ServiceException(ErrorCode.BAD_REQUEST, "Category isn't found (category: " + category.getCategory() + ")");
        }

        category.setCategory(new Category.PrefsView(cached));

        category.setChannels(prepareChannelsOptIns(category.getChannels()));

        if (category.getCategories() != null) {
            for (CategoryOptIn child : category.getCategories()) {
                prepareClientPrefsCategoryOptIn(child);
            }
        }
    }

    private Collection<ChannelOptIn> prepareChannelsOptIns(Collection<ChannelOptIn> channelOptIns) {
        if (CollectionUtils.isEmpty(channelOptIns)) {
            return null;
        }

        List<ChannelOptIn> result = new ArrayList<>(channelOptIns.size());
        for (ChannelOptIn channelOptIn : channelOptIns) {
            if (!prepareClientPrefsChannelOptIn(channelOptIn)) {
                continue;
            }

            result.add(channelOptIn);
        }

        return Collections.unmodifiableList(result);
    }

    private boolean prepareClientPrefsChannelOptIn(ChannelOptIn channel) {
        if (channel == null) {
            throw new ServiceException(ErrorCode.BAD_REQUEST, "Empty channel opt-in isn't allowed");
        }

        if (StringUtils.isBlank(channel.getCode())) {
            throw new ServiceException(ErrorCode.BAD_REQUEST, "Empty channel opt-in code isn't allowed");
        }

        if (channel.getOptIn() == null) {
            throw new ServiceException(ErrorCode.BAD_REQUEST, "Channel opt-in isn't defined properly (code: " + channel.getCode() + ")");
        }

        Identifier id = new Identifier(channel.getCode());
        boolean channelExists = channelsService.getOrDefault(id, null) != null;
        if (!channelExists) {
            logger.warn("Channel isn't found (id: {})", id);
        }

        return channelExists;
    }

    public ClientPrefs refineClientPrefs(ClientPrefs prefs, Set<String> onlyChannels) {
        if (prefs == null) {
            return null;
        }

        Preferences preferences = prefs.getPreferences();

        PreferencesCache.PrefsView defaultPrefs = preferencesCache.getDefault();

        preferences = applyPreferencesChanges(preferences, defaultPrefs, onlyChannels);

        prefs.setPreferences(preferences);

        return prefs;
    }

    public Preferences applyPreferencesChanges(Preferences changes, Preferences base) {
        return applyPreferencesChanges(changes, base, null);
    }

    public Preferences applyPreferencesChanges(Preferences changes, Preferences base, Set<String> onlyChannels) {
        if (changes == null) {
            return null;
        }

        Preferences res = new Preferences.PrefsView(base, onlyChannels);

        if (changes.getOptIn() != null) {
            res.setOptIn(changes.getOptIn());
        }

        if (changes.getChannels() != null) {
            if (res.getChannels() == null) {
                res.setChannels(new ArrayList<>(changes.getChannels().size()));
            }

            refineChannelOptIns(changes.getChannels(), res.getChannels(), null);
        }

        if (changes.getCategories() != null) {
            if (res.getCategories() == null) {
                res.setCategories(new ArrayList<>(changes.getCategories().size()));
            }

            refineCategoriesOptIns(
                    changes.getCategories(),
                    res.getCategories(),
                    !(base instanceof PreferencesCache.PrefsView) ? null :
                            (catId, chCode) -> ((PreferencesCache.PrefsView) base).isEditable(catId, chCode)
            );
        }

        return res;
    }

    private void refineChannelOptIns(Collection<ChannelOptIn> from, Collection<ChannelOptIn> to, Predicate<String> filter) {
        Map<String, ChannelOptIn> fromChannelOptInMap =
                from.stream()
                        .filter(ch -> ch != null && ch.getCode() != null)
                        .map(ch -> Pair.of(ch.getCode(), ch))
                        .collect(Collectors.toMap(Pair::getKey, Pair::getValue));

        for (ChannelOptIn channel : to) {
            if (filter != null && !filter.test(channel.getCode())) {
                continue;
            }

            ChannelOptIn optIn = fromChannelOptInMap.get(channel.getCode());
            if (optIn == null || optIn.getOptIn() == null) {
                continue;
            }

            channel.setOptIn(optIn.getOptIn());
        }
    }

    private void refineCategoriesOptIns(
            Collection<CategoryOptIn> from, Collection<CategoryOptIn> to, BiPredicate<UUID, String> categoryChannelFilter
    ) {
        Map<Category, CategoryOptIn> toCategoryOptInMap = new LinkedHashMap<>();
        for (CategoryOptIn categoryOptIn : to) {
            buildCategoryOptInMap(categoryOptIn, toCategoryOptInMap);
        }

        for (CategoryOptIn categoryOptIn : from) {
            refineCategoryOptIns(categoryOptIn, toCategoryOptInMap, categoryChannelFilter);
        }
    }

    private void buildCategoryOptInMap(CategoryOptIn categoryOptIn, Map<Category, CategoryOptIn> res) {
        if (categoryOptIn == null) {
            return;
        }

        if (CollectionUtils.isNotEmpty(categoryOptIn.getChannels())) {
            res.put(categoryOptIn.getCategory(), categoryOptIn);
        }

        if (categoryOptIn.getCategories() != null) {
            for (CategoryOptIn child : categoryOptIn.getCategories()) {
                buildCategoryOptInMap(child, res);
            }
        }
    }

    private void refineCategoryOptIns(
            CategoryOptIn fromOptIn, Map<Category, CategoryOptIn> toCategoryOptInMap, BiPredicate<UUID, String> categoryChannelFilter
    ) {
        if (fromOptIn.getChannels() != null) {
            CategoryOptIn channelOptIn = toCategoryOptInMap.remove(fromOptIn.getCategory());
            if (channelOptIn != null && fromOptIn.getChannels() != null) {
                refineChannelOptIns(
                        fromOptIn.getChannels(), channelOptIn.getChannels(),
                        categoryChannelFilter == null || fromOptIn.getCategory() == null ? null :
                                chCode -> categoryChannelFilter.test(fromOptIn.getCategory().getId(), chCode)
                );
            }
        }

        if (fromOptIn.getCategories() != null) {
            for (CategoryOptIn child : fromOptIn.getCategories()) {
                refineCategoryOptIns(child, toCategoryOptInMap, categoryChannelFilter);
            }
        }
    }

    public static class Errors {
        public static final RuntimeException PREFS_NOT_FOUND = new ServiceException(ErrorCode.NOT_FOUND, "Client preferences not found");
        public static final RuntimeException PREFS_ALREADY_EXIST = new ServiceException(ErrorCode.CONFLICT, "The client preferences already exist");
    }
}

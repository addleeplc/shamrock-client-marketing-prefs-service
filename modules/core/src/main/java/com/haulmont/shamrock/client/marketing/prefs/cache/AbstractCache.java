/*
 * Copyright 2008 - 2025 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */

package com.haulmont.shamrock.client.marketing.prefs.cache;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalListener;
import com.haulmont.shamrock.client.marketing.prefs.dto.ClientId;
import com.haulmont.shamrock.client.marketing.prefs.model.ModelInstanceId;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

public abstract class AbstractCache<Id, T extends Id> implements CacheManagement<Id, T> {
    private final Function<Id, T> cacheFun;

    protected AtomicReference<Cache<Id, T>> byIdRef = new AtomicReference<>();
    protected Map<String, Index<?, T>> indexMap = Collections.synchronizedMap(new HashMap<>());

    protected AbstractCache(Function<Id, T> cacheFun) {
        this.cacheFun = cacheFun;
    }

    protected <Key> void addIndex(String name, Function<T, Key> extractor) {
        indexMap.put(name, new Index<>(extractor));
    }

    public void start() {
        build();
    }

    protected abstract long getExpiryInSeconds();

    protected abstract long getMaxSize();

    protected abstract T getIndexed(Id id);

    protected void build() {
        Cache<Id, T> byRowId =
                CacheBuilder.newBuilder()
                        .expireAfterWrite(getExpiryInSeconds(), TimeUnit.SECONDS)
                        .maximumSize(getMaxSize())
                        .removalListener((RemovalListener<Id, T>) n -> {
                            if (n.getValue() == null) {
                                return;
                            }
                            indexMap.forEach((k, idx) -> idx.remove(n.getValue()));
                        })
                        .build();

        synchronized (this) {
            this.byIdRef.set(byRowId);
            this.indexMap.forEach((k, idx) -> idx.clear());
        }
    }

    @SuppressWarnings("unchecked")
    protected <Key> Index<Key, T> getIndex(String name) {
        return (Index<Key, T>) indexMap.get(name);
    }

    @Override
    public T get(Id id) {
        T val = getIndexed(id);
        if (val == null) {
            val = cacheFun.apply(id);
            put(id, val);
        }

        return val;
    }

    private void put(Id id, T val) {
        if (id == null || val == null) {
            return;
        }

        indexMap.forEach((k, idx) -> idx.put(val));
        byIdRef.get().put(id, val);
    }

    @Override
    public long getSize() {
        return byIdRef.get().size();
    }

    @Override
    public void invalidate(Id id) {
        T val = get(id);
        indexMap.forEach((k, idx) -> idx.remove(val));
        byIdRef.get().invalidate(id);
    }

    @Override
    public void invalidateAll() {
        build();
    }

    public abstract static class ById<T extends ModelInstanceId> extends AbstractCache<ModelInstanceId, T> {
        public static final String IDX_BY_ID = "id";
        public static final String IDX_BY_CODE = "code";

        protected ById(Function<ModelInstanceId, T> cacheFun) {
            super(cacheFun);

            addIndex(IDX_BY_ID, ModelInstanceId::getId);
            addIndex(IDX_BY_CODE, ModelInstanceId::getCode);
        }

        @Override
        protected T getIndexed(ModelInstanceId instanceId) {
            return instanceId.getId() == null ? getByCode(instanceId.getCode()) : getById(instanceId.getId());
        }

        private T getById(UUID id) {
            return getIndex(IDX_BY_ID).get(id).orElse(null);
        }

        private T getByCode(String code) {
            return getIndex(IDX_BY_CODE).get(code).orElse(null);
        }

        @Override
        protected long getMaxSize() {
            return Long.MAX_VALUE;
        }
    }

    public static abstract class ByClientId<T extends ClientId> extends AbstractCache<ClientId, T> {
        public static final String IDX_BY_ID = "id";
        public static final String IDX_BY_UID = "uid";
        public static final String IDX_BY_EMAIL = "email";

        protected ByClientId(Function<ClientId, T> cacheFun) {
            super(cacheFun);

            addIndex(IDX_BY_ID, ClientId::getId);
            addIndex(IDX_BY_UID, ClientId::getUid);
            addIndex(IDX_BY_EMAIL, ClientId::getEmail);
        }

        @Override
        protected T getIndexed(ClientId clientId) {
            if (clientId.getId() != null) {
                return getById(clientId.getId());
            }

            if (clientId.getUid() != null) {
                return getByUid(clientId.getUid());
            }

            return getByEmail(clientId.getEmail());
        }

        private T getById(UUID id) {
            return getIndex(IDX_BY_ID).get(id).orElse(null);
        }

        private T getByUid(String uid) {
            return getIndex(IDX_BY_UID).get(uid).orElse(null);
        }

        private T getByEmail(String email) {
            return getIndex(IDX_BY_EMAIL).get(email).orElse(null);
        }
    }

    protected static class Index<Key, T> {
        private final Function<T, Key> extractor;

        private final ConcurrentMap<Key, T> idCache = new ConcurrentHashMap<>();

        protected Index(Function<T, Key> extractor) {
            this.extractor = extractor;
        }

        private Optional<T> get(Key key) {
            return Optional.ofNullable(idCache.get(key));
        }

        private void put(T val) {
            Key key = extractor.apply(val);
            if (key == null) {
                return;
            }

            idCache.put(key, val);
        }

        private void remove(T val) {
            if (val == null) {
                return;
            }

            Key key = extractor.apply(val);
            if (key == null) {
                return;
            }

            idCache.remove(key);
        }

        private void clear() {
            idCache.clear();
        }
    }
}

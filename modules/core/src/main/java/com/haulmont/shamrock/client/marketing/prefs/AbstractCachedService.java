/*
 * Copyright 2008 - 2025 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */

package com.haulmont.shamrock.client.marketing.prefs;

import com.haulmont.shamrock.client.marketing.prefs.cache.CacheManagement;

public abstract class AbstractCachedService<Key, Val, T extends CacheManagement<Key, Val>> {
    protected final T cache;

    protected AbstractCachedService(T cache) {
        this.cache = cache;
    }

    protected void doCacheMutatingAction(Key key, Runnable op) {
        try {
            op.run();
        } finally {
            cache.invalidate(key);
        }
    }
}

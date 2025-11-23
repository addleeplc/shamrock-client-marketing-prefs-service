/*
 * Copyright 2008 - 2025 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */

package com.haulmont.shamrock.client.marketing.prefs.cache;

public interface CacheManagement<Key, Val> extends com.haulmont.monaco.model.cache.CacheManagement {
    Val get(Key key);

    void invalidate(Key key);
}

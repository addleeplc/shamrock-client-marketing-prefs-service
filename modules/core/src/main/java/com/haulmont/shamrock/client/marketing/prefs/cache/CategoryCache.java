/*
 * Copyright 2008 - 2025 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */

package com.haulmont.shamrock.client.marketing.prefs.cache;

import com.haulmont.shamrock.client.marketing.prefs.ServiceConfiguration;
import com.haulmont.shamrock.client.marketing.prefs.db.CategoriesRepository;
import com.haulmont.shamrock.client.marketing.prefs.model.Category;
import org.picocontainer.annotations.Component;
import org.picocontainer.annotations.Inject;

import java.util.Optional;

@Component
public class CategoryCache extends AbstractCache.ById<Category> {
    private static final long CATEGORY_CACHE_EXPIRY_SECONDS = 180;

    @Inject
    private ServiceConfiguration configuration;

    public CategoryCache(CategoriesRepository categoriesRepository) {
        super(categoriesRepository::get);
    }

    @Override
    protected long getExpiryInSeconds() {
        return Optional.ofNullable(configuration.getCategoriesCacheExpirySeconds()).orElse(CATEGORY_CACHE_EXPIRY_SECONDS);
    }
}

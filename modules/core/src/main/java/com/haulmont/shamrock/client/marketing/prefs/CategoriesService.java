package com.haulmont.shamrock.client.marketing.prefs;

import com.haulmont.monaco.ServiceException;
import com.haulmont.monaco.response.ErrorCode;
import com.haulmont.shamrock.client.marketing.prefs.cache.CategoryCache;
import com.haulmont.shamrock.client.marketing.prefs.cache.ChannelCache;
import com.haulmont.shamrock.client.marketing.prefs.storage.CategoriesRepository;
import com.haulmont.shamrock.client.marketing.prefs.model.Category;
import com.haulmont.shamrock.client.marketing.prefs.model.Identifier;
import com.haulmont.shamrock.client.marketing.prefs.storage.model.CategoryChannel;
import com.haulmont.shamrock.client.marketing.prefs.storage.model.ModelInstanceId;
import com.haulmont.shamrock.client.marketing.prefs.mq.ModelEventsMessagingService;
import com.haulmont.shamrock.client.marketing.prefs.mq.dto.CategoryCreated;
import com.haulmont.shamrock.client.marketing.prefs.mq.dto.CategoryDeleted;
import com.haulmont.shamrock.client.marketing.prefs.mq.dto.CategoryUpdated;
import com.haulmont.shamrock.client.marketing.prefs.utils.CategoryUtils;
import com.haulmont.shamrock.client.marketing.prefs.utils.IdUtils;
import org.picocontainer.annotations.Component;
import org.picocontainer.annotations.Inject;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

@Component
public class CategoriesService extends AbstractCachedService<ModelInstanceId, com.haulmont.shamrock.client.marketing.prefs.storage.model.Category, CategoryCache> {
    @Inject
    private CategoriesRepository categoriesRepository;

    @Inject
    private ChannelCache channelCache;

    @Inject
    private ModelEventsMessagingService modelEventsMessagingService;


    public CategoriesService(CategoryCache cache) {
        super(cache);
    }

    public Collection<Category> getAll() {
        return CategoryUtils.convert(categoriesRepository.getAll());
    }

    public Category get(Identifier id) {
        ModelInstanceId rowId = IdUtils.convert(id);
        return CategoryUtils.convert(Optional.ofNullable(cache.get(rowId)).orElseThrow(() -> Errors.CATEGORY_NOT_FOUND));
    }

    public Category add(Category category) {
        com.haulmont.shamrock.client.marketing.prefs.storage.model.Category newCategory = CategoryUtils.convert(category);

        addCategory(newCategory);

        Category res = CategoryUtils.convert(cache.get(newCategory));

        modelEventsMessagingService.publish(CategoryCreated.create(res, CategoryCreated::new));

        return res;
    }

    private void addCategory(com.haulmont.shamrock.client.marketing.prefs.storage.model.Category category) {
        if (category == null) {
            return;
        }

        prepareCategoryAndChannelIds(category, true);

        doCacheMutatingAction(category, () -> categoriesRepository.add(category));
    }

    public Category update(Identifier id, Category category) {
        if (category.getId() == null && id.getId() != null) {
            category.setId(id.getId());
        } else if (category.getCode() == null && id.getCode() != null) {
            category.setCode(id.getCode());
        }

        com.haulmont.shamrock.client.marketing.prefs.storage.model.Category catToUpdate = CategoryUtils.convert(category);

        updateCategory(catToUpdate);

        Category res = get(id);

        modelEventsMessagingService.publish(CategoryUpdated.create(res, CategoryUpdated::new));

        return res;
    }

    public void updateCategory(com.haulmont.shamrock.client.marketing.prefs.storage.model.Category category) {
        prepareCategoryAndChannelIds(category, false);

        doCacheMutatingAction(category, () -> categoriesRepository.update(category));

        if (category.getChildren() != null) {
            for (com.haulmont.shamrock.client.marketing.prefs.storage.model.Category child : category.getChildren()) {
                updateCategory(child);
            }
        }
    }

    private void prepareCategoryAndChannelIds(
            com.haulmont.shamrock.client.marketing.prefs.storage.model.Category category, boolean newIdIfNotExists
    ) {
        if (category == null) {
            return;
        }

        boolean extended = extendId(category, cache::get);
        if (category.getParentCategoryId() == null) {
            if (extended && newIdIfNotExists) {
                throw new ServiceException(ErrorCode.CONFLICT, "Category already exists (id: " + category + ")");
            } else if (!extended && !newIdIfNotExists && category.getParentCategoryId() == null) {
                throw Errors.CATEGORY_NOT_FOUND;
            }
        }

        if (category.getId() == null) {
            category.setId(UUID.randomUUID());
        }

        if (category.getChannels() != null) {
            for (CategoryChannel channel : category.getChannels()) {
                if (!extendId(channel, channelCache::get)) {
                    throw new ServiceException(ErrorCode.CONFLICT, "Can't find a category channel (id: " + channel + ")");
                }
            }
        }

        if (category.getChildren() != null) {
            for (com.haulmont.shamrock.client.marketing.prefs.storage.model.Category child : category.getChildren()) {
                if (child.getParentCategoryId() == null) {
                    child.setParentCategoryId(category.getId());
                }

                prepareCategoryAndChannelIds(child, newIdIfNotExists);
            }
        }
    }

    private <T extends ModelInstanceId> boolean extendId(ModelInstanceId id, Function<ModelInstanceId, T> getFromCache) {
        if (id == null || id.getId() == null && id.getCode() == null) {
            return false;
        }

        T cached = getFromCache.apply(id);
        if (cached == null) {
            return false;
        }

        if (id.getId() == null) {
            id.setId(cached.getId());
        } else if (id.getCode() == null) {
            id.setCode(cached.getCode());
        }

        return true;
    }

    public void delete(Identifier id) {
        ModelInstanceId categoryId = IdUtils.convert(id);

        Category category = get(id);

        doCacheMutatingAction(categoryId, () -> {
            if (!categoriesRepository.delete(categoryId)) {
                throw Errors.CATEGORY_NOT_FOUND;
            }
        });

        modelEventsMessagingService.publish(CategoryDeleted.create(category, CategoryDeleted::new));
    }

    public static class Errors {
        public static final RuntimeException CATEGORY_NOT_FOUND = new ServiceException(ErrorCode.NOT_FOUND, "Category not found");
    }
}

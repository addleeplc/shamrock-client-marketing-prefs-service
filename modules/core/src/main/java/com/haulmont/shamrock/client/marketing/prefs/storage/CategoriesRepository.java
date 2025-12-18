/*
 * Copyright 2008 - 2025 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */

package com.haulmont.shamrock.client.marketing.prefs.storage;

import com.haulmont.monaco.ServiceException;
import com.haulmont.monaco.response.ErrorCode;
import com.haulmont.shamrock.client.marketing.prefs.mybatis.ClientMarketingPrefsSqlSessionFactory;
import com.haulmont.shamrock.client.marketing.prefs.mybatis.Constants;
import com.haulmont.shamrock.client.marketing.prefs.storage.model.Category;
import com.haulmont.shamrock.client.marketing.prefs.storage.model.CategoryChannel;
import com.haulmont.shamrock.client.marketing.prefs.storage.model.ModelInstanceId;
import org.apache.ibatis.executor.BatchResult;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.joda.time.DateTime;
import org.picocontainer.annotations.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
public class CategoriesRepository extends AbstractRepository<ClientMarketingPrefsSqlSessionFactory> {
    public CategoriesRepository(ClientMarketingPrefsSqlSessionFactory sqlSessionFactory) {
        super(sqlSessionFactory);
    }

    public List<Category> getAll() {
        return new LoadCategoriesCommand().execute();
    }

    public Category get(ModelInstanceId id) {
        return new LoadCategoryCommand(id).execute();
    }

    public void add(Category category) {
        new InsertCategoryCommand(category).execute();
    }

    public boolean update(Category category) {
        if (!categoryHasPatch(category)) {
            return false;
        }

        return new UpdateCategoryCommand(category).execute() > 0;
    }

    private boolean categoryHasPatch(Category category) {
        return category.getCode() != null || category.getName() != null || category.getDescription() != null ||
                category.getChannels() != null;
    }

    public boolean delete(ModelInstanceId id) {
        return new DeleteCategoryCommand(id).execute() > 0;
    }

    private void setCreateAttributes(Category category) {
        category.setCreateTs(DateTime.now());
        category.setCreatedBy(Constants.USER_NAME);
    }

    private void setUpdateAttributes(Category category) {
        category.setUpdateTs(DateTime.now());
        category.setUpdatedBy(Constants.USER_NAME);
    }

    private class LoadCategoriesCommand extends Command<List<Category>> {
        LoadCategoriesCommand() {
            super("getCategories");
        }

        @Override
        protected List<Category> __execute(SqlSession sqlSession) {
            return sqlSession.selectList(getName());
        }
    }

    private class LoadCategoryCommand extends Command<Category> {
        private final ModelInstanceId id;

        LoadCategoryCommand(ModelInstanceId id) {
            super("getCategory");

            this.id = id;
        }

        @Override
        protected Category __execute(SqlSession sqlSession) {
            return sqlSession.selectOne(getName(), id);
        }
    }

    private class InsertCategoryCommand extends Command<Void> {
        private final Category category;

        InsertCategoryCommand(Category category) {
            super("addCategory");

            if (category.getId() == null) {
                category.setId(UUID.randomUUID());
            }

            setCreateAttributes(category);

            this.category = category;
        }

        @Override
        protected Void __execute(SqlSession sqlSession) {
            sqlSession.insert(getName(), category);

            SqlSession session = sqlSessionFactory.openSession(ExecutorType.BATCH, false);
            try (session) {
                addCategory(session, category);

                session.flushStatements();
                session.commit();
            } catch (Exception e) {
                session.rollback();
                throw new ServiceException(ErrorCode.SERVER_ERROR, "Fail to add category", e);
            }

            return null;
        }

        private void addCategory(SqlSession session, Category category) {
            session.insert(getName(), category);

            if (category.getChannels() != null) {
                for (CategoryChannel channel : category.getChannels()) {
                    session.insert("insertCategoryChannel", Map.of(
                            "category", category,
                            "channel", channel
                    ));
                }
            }

            if (category.getChildren() != null) {
                for (Category child : category.getChildren()) {
                    addCategory(session, child);
                }
            }
        }
    }

    private class UpdateCategoryCommand extends Command<Integer> {
        private final Category patch;

        UpdateCategoryCommand(Category patch) {
            super("updateCategory");

            this.patch = patch;
        }

        @Override
        protected Integer __execute(SqlSession sqlSession) {
            SqlSession session = sqlSessionFactory.openSession(ExecutorType.BATCH, false);
            try (session) {
                setCreateAttributes(patch);
                setUpdateAttributes(patch);

                if (patch.getCode() != null || patch.getName() != null || patch.getDescription() != null) {
                    session.insert(getName() + "-merge", patch);
                }

                if (patch.getChannels() != null) {
                    for (CategoryChannel channel : patch.getChannels()) {

                        session.insert("insertCategoryChannel", Map.of(
                                "category", patch,
                                "channel", channel
                        ));
                    }
                    session.delete(getName() + "-del-old-channels", patch);
                }

                List<BatchResult> batchResults = session.flushStatements();
                int affected = batchResults.stream()
                        .filter(r -> r != null && r.getUpdateCounts() != null)
                        .flatMapToInt(r -> Arrays.stream(r.getUpdateCounts()))
                        .sum();

                session.commit();

                return affected;
            } catch (Exception e) {
                session.rollback();
                throw new ServiceException(ErrorCode.SERVER_ERROR, "Fail to update a category", e);
            }
        }
    }

    private class DeleteCategoryCommand extends Command<Integer> {
        private final ModelInstanceId id;

        DeleteCategoryCommand(ModelInstanceId id) {
            super("deleteCategory");

            this.id = id;
        }

        @Override
        protected Integer __execute(SqlSession sqlSession) {
            return sqlSession.delete(getName(), id);
        }
    }
}

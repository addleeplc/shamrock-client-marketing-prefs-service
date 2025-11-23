package com.haulmont.shamrock.client.marketing.prefs.db;

import com.haulmont.monaco.mybatis.MyBatisCommand;
import com.haulmont.shamrock.client.marketing.prefs.db.mybatis.AbstractSqlSessionFactory;

public abstract class AbstractRepository<T extends AbstractSqlSessionFactory> {
    protected final T sqlSessionFactory;

    public AbstractRepository(T sqlSessionFactory) {
        this.sqlSessionFactory = sqlSessionFactory;
    }

    protected abstract class Command<R> extends MyBatisCommand<R> {
        protected final String name;

        protected Command(String name) {
            super(AbstractRepository.this.sqlSessionFactory);
            this.name = name;
        }

        @Override
        protected String getName() {
            return AbstractRepository.this.sqlSessionFactory.getStatement(name);
        }
    }
}

/*
 * Copyright 2008 - 2025 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */

package com.haulmont.shamrock.client.marketing.prefs.storage;

import com.haulmont.shamrock.client.marketing.prefs.storage.model.ShamrockClientPrefs;
import com.haulmont.shamrock.client.marketing.prefs.storage.model.ClientId;
import com.haulmont.shamrock.client.marketing.prefs.mybatis.ShamrockSqlSessionFactory;
import org.apache.ibatis.session.SqlSession;
import org.picocontainer.annotations.Component;

@Component
@Deprecated // get rid of the bean when there is no client using preferences in sybase and all the clients are imported
public class ShamrockClientPrefsRepository extends AbstractRepository<ShamrockSqlSessionFactory> {
    public ShamrockClientPrefsRepository(ShamrockSqlSessionFactory sqlSessionFactory) {
        super(sqlSessionFactory);
    }

    public ShamrockClientPrefs getPreferencesAsXml(ClientId id) {
        return id == null || id.getUid() == null ? null : new LoadPreferencesAsXmlCommand(id).execute();
    }

    public ShamrockClientPrefs getPreferences(ClientId id) {
        return id == null || id.getId() == null ? null : new LoadPreferencesCommand(id).execute();
    }

    private class LoadPreferencesAsXmlCommand extends Command<ShamrockClientPrefs> {
        private final ClientId id;

        LoadPreferencesAsXmlCommand(ClientId id) {
            super("loadImage");

            this.id = id;
        }

        @Override
        protected ShamrockClientPrefs __execute(SqlSession sqlSession) {
            return sqlSession.selectOne(getName(), id);
        }
    }

    private class LoadPreferencesCommand extends Command<ShamrockClientPrefs> {
        private final ClientId id;

        LoadPreferencesCommand(ClientId id) {
            super("loadPreferences");

            this.id = id;
        }

        @Override
        protected ShamrockClientPrefs __execute(SqlSession sqlSession) {
            return sqlSession.selectOne(getName(), id);
        }
    }
}

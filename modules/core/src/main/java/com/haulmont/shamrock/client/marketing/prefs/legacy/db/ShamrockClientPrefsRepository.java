/*
 * Copyright 2008 - 2025 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */

package com.haulmont.shamrock.client.marketing.prefs.legacy.db;

import com.haulmont.shamrock.client.marketing.prefs.db.AbstractRepository;
import com.haulmont.shamrock.client.marketing.prefs.db.mybatis.ShamrockSqlSessionFactory;
import com.haulmont.shamrock.client.marketing.prefs.legacy.model.Preferences;
import com.haulmont.shamrock.client.marketing.prefs.model.ClientId;
import org.apache.ibatis.session.SqlSession;
import org.picocontainer.annotations.Component;

@Component
@Deprecated // get rid of the bean when there is no client using preferences in sybase and all the clients are imported
public class ShamrockClientPrefsRepository extends AbstractRepository<ShamrockSqlSessionFactory> {
    public ShamrockClientPrefsRepository(ShamrockSqlSessionFactory sqlSessionFactory) {
        super(sqlSessionFactory);
    }

    public Preferences getPreferencesAsXml(ClientId id) {
        return id == null || id.getUid() == null ? null : new LoadPreferencesAsXmlCommand(id).execute();
    }

    public Preferences getPreferences(ClientId id) {
        return id == null || id.getId() == null ? null : new LoadPreferencesCommand(id).execute();
    }

    private class LoadPreferencesAsXmlCommand extends Command<Preferences> {
        private final ClientId id;

        LoadPreferencesAsXmlCommand(ClientId id) {
            super("loadImage");

            this.id = id;
        }

        @Override
        protected Preferences __execute(SqlSession sqlSession) {
            return sqlSession.selectOne(getName(), id);
        }
    }

    private class LoadPreferencesCommand extends Command<Preferences> {
        private final ClientId id;

        LoadPreferencesCommand(ClientId id) {
            super("loadPreferences");

            this.id = id;
        }

        @Override
        protected Preferences __execute(SqlSession sqlSession) {
            return sqlSession.selectOne(getName(), id);
        }
    }
}

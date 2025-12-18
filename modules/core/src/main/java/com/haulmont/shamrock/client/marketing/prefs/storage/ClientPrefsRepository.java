/*
 * Copyright 2008 - 2025 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */

package com.haulmont.shamrock.client.marketing.prefs.storage;

import com.haulmont.monaco.ServiceException;
import com.haulmont.monaco.response.ErrorCode;
import com.haulmont.shamrock.client.marketing.prefs.mybatis.ClientMarketingPrefsSqlSessionFactory;
import com.haulmont.shamrock.client.marketing.prefs.storage.model.ClientId;
import com.haulmont.shamrock.client.marketing.prefs.storage.model.ClientPrefs;
import org.apache.ibatis.session.SqlSession;
import org.picocontainer.annotations.Component;

import java.util.Map;

@Component
public class ClientPrefsRepository extends AbstractRepository<ClientMarketingPrefsSqlSessionFactory> {
    public ClientPrefsRepository(ClientMarketingPrefsSqlSessionFactory sqlSessionFactory) {
        super(sqlSessionFactory);
    }

    public ClientPrefs get(ClientId id) {
        return new LoadClientPrefsCommand(id).execute();
    }

    public boolean add(ClientPrefs clientPrefs) {
        return new InsertClientPrefsCommand(clientPrefs).execute() > 0;
    }

    public int update(ClientId id, ClientPrefs clientPrefs) {
        if (clientPrefs == null || (clientPrefs.getClientId() == null && clientPrefs.getPrefs() == null)) {
            throw new IllegalArgumentException("No real update found");
        }

        return new UpdateClientPrefsCommand(id, clientPrefs).execute();
    }

    public boolean delete(ClientId id) {
        return new DeleteClientPrefsCommand(id).execute() > 0;
    }


    private class LoadClientPrefsCommand extends Command<ClientPrefs> {
        private final ClientId id;

        LoadClientPrefsCommand(ClientId id) {
            super("getClientPrefs");

            this.id = id;
        }

        @Override
        protected ClientPrefs __execute(SqlSession sqlSession) {
            return sqlSession.selectOne(getName(), id);
        }
    }

    private class InsertClientPrefsCommand extends BatchCommand {
        InsertClientPrefsCommand(ClientPrefs clientPrefs) {
            super("addClientPrefs");

            addAction(((session, stats) ->
                    session.update(getStatementName("recover-deleted"), clientPrefs)
            ));

            addAction(((session, stats) -> {
                if (stats.getAffected() == 1) {
                    return;
                } else if (stats.getAffected() > 1) {
                    throw Errors.TOO_MANY_CLIENTS;
                }

                session.insert(getStatementName("add-new"), clientPrefs);
            }));
        }
    }

    private class UpdateClientPrefsCommand extends Command<Integer> {
        private final ClientId id;
        private final ClientPrefs patch;

        UpdateClientPrefsCommand(ClientId id, ClientPrefs patch) {
            super("updateClientPrefs");

            this.id = id;
            this.patch = patch;
        }

        @Override
        protected Integer __execute(SqlSession sqlSession) {
            Map<String, Object> params = Map.of(
                    "key", id,
                    "value", patch
            );

            int updated = sqlSession.update(getName(), params);
            if (updated > 1) {
                throw Errors.TOO_MANY_CLIENTS;
            }

            return updated;
        }
    }

    private class DeleteClientPrefsCommand extends Command<Integer> {
        private final ClientId id;

        DeleteClientPrefsCommand(ClientId id) {
            super("deleteClientPrefs");

            this.id = id;
        }

        @Override
        protected Integer __execute(SqlSession sqlSession) {
            return sqlSession.update(getName(), id);
        }
    }

    public static class Errors {
        public static final RuntimeException TOO_MANY_CLIENTS = new ServiceException(ErrorCode.BAD_REQUEST, "Too many clients to update");
    }
}

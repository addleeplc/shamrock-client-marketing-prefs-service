package com.haulmont.shamrock.client.marketing.prefs.db;

import com.haulmont.shamrock.client.marketing.prefs.db.mybatis.ClientMarketingPrefsSqlSessionFactory;
import com.haulmont.shamrock.client.marketing.prefs.model.ClientId;
import com.haulmont.shamrock.client.marketing.prefs.model.ClientPrefs;
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

    private class InsertClientPrefsCommand extends Command<Integer> {
        private final ClientPrefs clientPrefs;

        InsertClientPrefsCommand(ClientPrefs clientPrefs) {
            super("addClientPrefs");

            this.clientPrefs = clientPrefs;
        }

        @Override
        protected Integer __execute(SqlSession sqlSession) {
            return sqlSession.insert(getName(), clientPrefs);
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

            return sqlSession.update(getName(), params);
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
            return sqlSession.delete(getName(), id);
        }
    }
}

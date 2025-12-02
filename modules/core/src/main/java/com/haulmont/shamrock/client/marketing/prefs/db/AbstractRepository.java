package com.haulmont.shamrock.client.marketing.prefs.db;

import com.haulmont.monaco.mybatis.MyBatisCommand;
import com.haulmont.shamrock.client.marketing.prefs.db.mybatis.AbstractSqlSessionFactory;
import org.apache.ibatis.executor.BatchResult;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

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

    protected abstract class BatchCommand extends MyBatisCommand<Integer> {
        protected final String name;
        protected final List<BatchAction> actions = new LinkedList<>();

        protected BatchCommand(String name) {
            super(AbstractRepository.this.sqlSessionFactory);
            this.name = name;
        }

        protected BatchCommand addAction(BatchAction action) {
            if (action != null) {
                actions.add(action);
            }

            return this;
        }

        @Override
        protected Integer __execute(SqlSession connection) {
            if (actions.isEmpty()) {
                logger.warn("No actions detected for the command {}", name);
            }

            BatchStats batchStats = new BatchStats();

            SqlSession session = sqlSessionFactory.openSession(ExecutorType.BATCH, false);
            try (session) {
                for (BatchAction action : actions) {
                    action.execute(session, batchStats);

                    List<BatchResult> batchResults = session.flushStatements();
                    int affected = batchResults.stream()
                            .filter(r -> r != null && r.getUpdateCounts() != null)
                            .flatMapToInt(r -> Arrays.stream(r.getUpdateCounts()))
                            .sum();

                    batchStats = new BatchStats(batchStats, affected);
                }

                session.commit();

                return batchStats.totalAffected;
            } catch (Exception e) {
                session.rollback();
                throw e;
            }
        }

        protected String getStatementName(String statementName) {
            return name + "-" + statementName;
        }

        @Override
        protected String getName() {
            return AbstractRepository.this.sqlSessionFactory.getStatement(name);
        }
    }

    @FunctionalInterface
    public interface BatchAction {
        void execute(SqlSession session, BatchStats stats);
    }

    public static class BatchStats {
        private final int statementCount;
        private final int affected;
        private final int totalAffected;

        public BatchStats() {
            this.statementCount = 0;
            this.affected = 0;
            this.totalAffected = 0;
        }

        public BatchStats(BatchStats lastStats, int affected) {
            this.statementCount = lastStats == null ? 1 : (lastStats.statementCount + 1);
            this.affected = affected;
            this.totalAffected = lastStats == null ? affected : (lastStats.totalAffected + affected);
        }

        public int getAffected() {
            return affected;
        }

        public int getStatementCount() {
            return statementCount;
        }

        public int getTotalAffected() {
            return totalAffected;
        }
    }
}

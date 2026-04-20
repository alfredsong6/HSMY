package com.hsmy.mapper;

import com.alibaba.druid.pool.DruidDataSource;
import com.hsmy.entity.UserStats;
import com.hsmy.utils.IdGenerator;
import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class UserStatsMapperIntegrationTest {

    private static final String JDBC_URL = "jdbc:mysql://10.10.0.45:3306/ftxy_hs?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=GMT%2B8";
    private static final String JDBC_USERNAME = "root";
    private static final String JDBC_PASSWORD = "123456";

    @Test
    void reduceMeritCoins_allowsDeductingExactBalance() throws Exception {
        long userId = IdGenerator.nextId();
        DruidDataSource dataSource = createDataSource();

        try {
            insertUserStats(dataSource, userId, 10L);

            SqlSessionFactory sqlSessionFactory = buildSqlSessionFactory(dataSource);
            try (SqlSession sqlSession = sqlSessionFactory.openSession(true)) {
                UserStatsMapper mapper = sqlSession.getMapper(UserStatsMapper.class);

                int updated = mapper.reduceMeritCoins(userId, 10L);

                assertEquals(1, updated);

                UserStats refreshed = mapper.selectByUserId(userId);
                assertNotNull(refreshed);
                assertEquals(Long.valueOf(0L), refreshed.getMeritCoins());
            }
        } finally {
            deleteUserStats(dataSource, userId);
            dataSource.close();
        }
    }

    private DruidDataSource createDataSource() {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        dataSource.setUrl(JDBC_URL);
        dataSource.setUsername(JDBC_USERNAME);
        dataSource.setPassword(JDBC_PASSWORD);
        return dataSource;
    }

    private SqlSessionFactory buildSqlSessionFactory(DruidDataSource dataSource) throws Exception {
        TransactionFactory transactionFactory = new JdbcTransactionFactory();
        Environment environment = new Environment("test", transactionFactory, dataSource);
        Configuration configuration = new Configuration(environment);
        configuration.addMapper(UserStatsMapper.class);

        String resource = "mapper/UserStatsMapper.xml";
        try (InputStream inputStream = Resources.getResourceAsStream(resource)) {
            XMLMapperBuilder xmlMapperBuilder = new XMLMapperBuilder(
                    inputStream,
                    configuration,
                    resource,
                    configuration.getSqlFragments()
            );
            xmlMapperBuilder.parse();
        }

        return new SqlSessionFactoryBuilder().build(configuration);
    }

    private void insertUserStats(DruidDataSource dataSource, long userId, long meritCoins) throws Exception {
        String sql = "INSERT INTO t_user_stats " +
                "(id, user_id, total_merit, merit_coins, total_knocks, current_level, create_by, create_time, update_by, update_time, is_deleted) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, NOW(), ?, NOW(), ?)";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, IdGenerator.nextId());
            statement.setLong(2, userId);
            statement.setLong(3, 0L);
            statement.setLong(4, meritCoins);
            statement.setLong(5, 0L);
            statement.setInt(6, 1);
            statement.setString(7, "test");
            statement.setString(8, "test");
            statement.setInt(9, 0);
            statement.executeUpdate();
        }
    }

    private void deleteUserStats(DruidDataSource dataSource, long userId) throws Exception {
        String sql = "DELETE FROM t_user_stats WHERE user_id = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, userId);
            statement.executeUpdate();
        }
    }
}

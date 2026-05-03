package com.cloudnote.dao.pool;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public class DataSource {
    private static final Logger log = LoggerFactory.getLogger(DataSource.class);
    private static HikariDataSource dataSource;

    public static void init() {
        Properties props = loadEnv();
        HikariConfig config = new HikariConfig();
        String host = props.getProperty("DB_HOST", "localhost");
        String port = props.getProperty("DB_PORT", "3306");
        String dbName = props.getProperty("DB_NAME", "cloudnote");
        String user = props.getProperty("DB_USER", "root");
        String password = props.getProperty("DB_PASSWORD", "123456");

        System.out.println("=== 数据库配置 ===");
        System.out.println("host=" + host + ", port=" + port + ", db=" + dbName + ", user=" + user);
        System.out.println("props keys: " + props.stringPropertyNames());

        String jdbcUrl = "jdbc:mysql://" + host + ":" + port + "/" + dbName
                + "?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true&characterEncoding=UTF-8";
        System.out.println("jdbcUrl=" + jdbcUrl);
        config.setJdbcUrl(jdbcUrl);
        config.setUsername(user);
        config.setPassword(password);
        config.setDriverClassName("com.mysql.cj.jdbc.Driver");
        config.setMaximumPoolSize(20);
        config.setMinimumIdle(5);
        config.setConnectionTimeout(3000);
        config.setIdleTimeout(600000);
        config.setMaxLifetime(1800000);

        try {
            dataSource = new HikariDataSource(config);
            // 测试连接
            try (var conn = dataSource.getConnection()) {
                System.out.println("=== 数据库连接成功! ===");
            }
            log.info("HikariCP 连接池初始化成功");
        } catch (Exception e) {
            System.out.println("=== 数据库连接失败! ===");
            System.out.println("异常类型: " + e.getClass().getName());
            System.out.println("异常信息: " + e.getMessage());
            if (e.getCause() != null) {
                System.out.println("原因: " + e.getCause().getMessage());
            }
            log.error("数据库连接池初始化失败", e);
            throw new RuntimeException("数据库连接失败: " + e.getMessage(), e);
        }
    }

    private static Properties loadEnv() {
        Properties props = new Properties();
        // 多路径查找 .env：当前目录、上级目录、上上级目录
        Path[] candidates = {
                Path.of(".env"),
                Path.of("../.env"),
                Path.of("../../.env")
        };
        for (Path envPath : candidates) {
            if (Files.exists(envPath)) {
                try (FileInputStream fis = new FileInputStream(envPath.toFile())) {
                    props.load(fis);
                    log.info("已加载 .env 配置文件: {}", envPath.toAbsolutePath());
                    return props;
                } catch (IOException e) {
                    log.warn(".env 文件读取失败: {}", envPath, e);
                }
            }
        }
        // 尝试从 classpath 加载 db.properties
        try (var is = DataSource.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (is != null) {
                props.load(is);
                log.info("已从 classpath 加载 db.properties");
                return props;
            }
        } catch (IOException e) {
            log.warn("classpath db.properties 读取失败", e);
        }
        log.warn("未找到配置文件，使用默认配置");
        return props;
    }

    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public static void shutdown() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            log.info("连接池已关闭");
        }
    }
}

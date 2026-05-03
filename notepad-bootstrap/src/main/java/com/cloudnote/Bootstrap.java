package com.cloudnote;

import com.cloudnote.dao.pool.DataSource;
import com.cloudnote.ui.App;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Bootstrap {
    private static final Logger log = LoggerFactory.getLogger(Bootstrap.class);

    public static void main(String[] args) {
        log.info("云记事本启动中...");

        try {
            DataSource.init();
            log.info("数据库连接池初始化成功");
        } catch (Exception e) {
            log.error("数据库初始化失败", e);
            System.err.println("错误: " + e.getMessage());
            System.err.println("请确保:");
            System.err.println("  1. MySQL 已启动 (或运行 docker-compose up -d)");
            System.err.println("  2. .env 文件配置正确");
            System.err.println("  3. 已执行 schema.sql 创建数据库表");
            System.exit(1);
        }

        // 注册关闭钩子
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            DataSource.shutdown();
            log.info("云记事本已退出");
        }));

        // 启动 UI
        App.main(args);
    }
}

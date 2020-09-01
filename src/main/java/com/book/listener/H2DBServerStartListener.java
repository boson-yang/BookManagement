package com.book.listener;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.h2.tools.Server;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.sql.SQLException;


/**
 * @ClassName: H2DBServerStartListener
 * @Description: 用于启动H2数据库服务的监听器，在应用系统初始化时就启动H2数据库的服务
 * @date: 2020-08-27
 */
public class H2DBServerStartListener implements ServletContextListener {

    private static final Logger logger = LogManager.getLogger(H2DBServerStartListener.class);

    // H2数据库服务器启动实例
    private Server server;

    /*
     * Web应用初始化时启动H2数据库
     */
    public void contextInitialized(ServletContextEvent sce) {
        try {
            logger.info("Starting H2 DataBase...");
            // 使用org.h2.tools.Server这个类创建一个H2数据库的服务并启动服务，由于没有指定任何参数，那么H2数据库启动时默认占用的端口就是8082
            server = Server.createTcpServer().start();
            logger.info("Start H2 DataBase successfully.");
        } catch (SQLException e) {
            logger.error("Start H2 DataBase failed: " + e.toString());
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /*
     * Web应用销毁时停止H2数据库
     */
    public void contextDestroyed(ServletContextEvent sce) {
        if (this.server != null) {
            // 停止H2数据库
            this.server.stop();
            this.server = null;
        }
    }
}

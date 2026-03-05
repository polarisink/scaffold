package com.scaffold.spi;

import cn.hutool.core.util.ServiceLoaderUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatabaseDriverSystem {
    
    // 数据库驱动接口
    public interface DatabaseDriver {
        String getDriverName();
        String getSupportedDatabase();
        Connection connect(String host, int port, String database, String username, String password);
    }
    
    // 连接对象
    public static class Connection {
        private String database;
        private boolean connected;
        
        public Connection(String database) {
            this.database = database;
            this.connected = true;
        }
        
        public void execute(String sql) {
            if (connected) {
                System.out.println("  执行SQL：" + sql);
            }
        }
        
        public void close() {
            connected = false;
            System.out.println("  连接已关闭");
        }
        
        public boolean isConnected() {
            return connected;
        }
    }
    
    // MySQL驱动
    public static class MysqlDriver implements DatabaseDriver {
        @Override
        public String getDriverName() {
            return "MySQL JDBC Driver";
        }
        
        @Override
        public String getSupportedDatabase() {
            return "mysql";
        }
        
        @Override
        public Connection connect(String host, int port, String database, 
                                 String username, String password) {
            System.out.println("MySQL驱动连接：");
            System.out.println("  主机：" + host + ":" + port);
            System.out.println("  数据库：" + database);
            System.out.println("  用户：" + username);
            return new Connection("mysql");
        }
    }
    
    // PostgreSQL驱动
    public static class PostgresqlDriver implements DatabaseDriver {
        @Override
        public String getDriverName() {
            return "PostgreSQL JDBC Driver";
        }
        
        @Override
        public String getSupportedDatabase() {
            return "postgresql";
        }
        
        @Override
        public Connection connect(String host, int port, String database, 
                                 String username, String password) {
            System.out.println("PostgreSQL驱动连接：");
            System.out.println("  主机：" + host + ":" + port);
            System.out.println("  数据库：" + database);
            System.out.println("  用户：" + username);
            return new Connection("postgresql");
        }
    }
    
    // Oracle驱动
    public static class OracleDriver implements DatabaseDriver {
        @Override
        public String getDriverName() {
            return "Oracle JDBC Driver";
        }
        
        @Override
        public String getSupportedDatabase() {
            return "oracle";
        }
        
        @Override
        public Connection connect(String host, int port, String database, 
                                 String username, String password) {
            System.out.println("Oracle驱动连接：");
            System.out.println("  主机：" + host + ":" + port);
            System.out.println("  数据库：" + database);
            System.out.println("  用户：" + username);
            return new Connection("oracle");
        }
    }
    
    // 驱动管理器
    static class DriverManager {
        private Map<String, DatabaseDriver> drivers;
        
        public DriverManager() {
            this.drivers = new HashMap<>();
            registerDrivers();
        }
        
        private void registerDrivers() {
            List<DatabaseDriver> driverList = ServiceLoaderUtil.loadList(DatabaseDriver.class);
            
            System.out.println("正在注册数据库驱动...");
            for (DatabaseDriver driver : driverList) {
                drivers.put(driver.getSupportedDatabase(), driver);
                System.out.println("  已注册：" + driver.getDriverName());
            }
            System.out.println("共注册 " + driverList.size() + " 个驱动\n");
        }
        
        public Connection getConnection(String dbType, String host, int port, 
                                       String database, String username, String password) {
            DatabaseDriver driver = drivers.get(dbType);
            if (driver == null) {
                System.err.println("不支持的数据库类型：" + dbType);
                return null;
            }
            return driver.connect(host, port, database, username, password);
        }
        
        public void listSupportedDatabases() {
            System.out.println("支持的数据库类型：");
            drivers.forEach((type, driver) -> 
                System.out.println("  - " + type + " (" + driver.getDriverName() + ")"));
        }
    }
    
    public static void main(String[] args) {
        System.out.println("========== 数据库驱动系统 ==========\n");
        
        DriverManager manager = new DriverManager();
        
        // 显示支持的数据库
        manager.listSupportedDatabases();
        
        // 场景1：连接MySQL
        System.out.println("\n场景1：连接MySQL数据库");
        Connection mysqlConn = manager.getConnection("mysql", "localhost", 3306, 
                                                    "testdb", "root", "password");
        if (mysqlConn != null) {
            mysqlConn.execute("SELECT * FROM users");
            mysqlConn.close();
        }
        
        // 场景2：连接PostgreSQL
        System.out.println("\n场景2：连接PostgreSQL数据库");
        Connection pgConn = manager.getConnection("postgresql", "localhost", 5432, 
                                                 "testdb", "postgres", "password");
        if (pgConn != null) {
            pgConn.execute("SELECT * FROM orders");
            pgConn.close();
        }
        
        // 场景3：连接Oracle
        System.out.println("\n场景3：连接Oracle数据库");
        Connection oracleConn = manager.getConnection("oracle", "localhost", 1521, 
                                                     "orcl", "system", "password");
        if (oracleConn != null) {
            oracleConn.execute("SELECT * FROM employees");
            oracleConn.close();
        }
    }
}
package me.arahis.rpcharacter.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import me.arahis.rpcharacter.RPCharacterPlugin;

import java.sql.Connection;
import java.sql.SQLException;

public class DataSource {

    private static HikariConfig config = new HikariConfig();
    private static HikariDataSource ds;
    private static String host = RPCharacterPlugin.getPlugin().getConfig().getString("mysql.host");
    private static String port = RPCharacterPlugin.getPlugin().getConfig().getString("mysql.port");
    private static String database = RPCharacterPlugin.getPlugin().getConfig().getString("mysql.database");
    private static String username = RPCharacterPlugin.getPlugin().getConfig().getString("mysql.username");
    private static String password = RPCharacterPlugin.getPlugin().getConfig().getString("mysql.password");
    private static String url = "jdbc:mysql://" + host + ":" + port + ":" + database;

    static {
        config.setJdbcUrl(url);
        config.setUsername(username);
        config.setPassword(password);
        config.addDataSourceProperty( "cachePrepStmts" , "true" );
        config.addDataSourceProperty( "prepStmtCacheSize" , "250" );
        config.addDataSourceProperty( "prepStmtCacheSqlLimit" , "2048" );
        ds = new HikariDataSource( config );
    }

    private DataSource() {}

    public static Connection getConnection() throws SQLException {
        return ds.getConnection();
    }
}
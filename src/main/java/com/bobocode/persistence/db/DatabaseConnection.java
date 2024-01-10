package com.bobocode.persistence.db;

import com.bobocode.persistence.exception.FileInputStreamException;
import org.postgresql.ds.PGSimpleDataSource;

import javax.sql.DataSource;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public final class DatabaseConnection {

    private static final String DB_CONFIG = "src/main/resources/META-DB/config.properties";

    private DatabaseConnection() {
    }

    public static DataSource getPGDataSource() {
        Properties properties = new Properties();

        try (FileInputStream fileInputStream = new FileInputStream(DB_CONFIG)) {

            properties.load(fileInputStream);

            PGSimpleDataSource dataSource = new PGSimpleDataSource();
            dataSource.setUrl(properties.getProperty("db.url"));
            dataSource.setUser(properties.getProperty("db.user"));
            dataSource.setPassword(properties.getProperty("db.password"));

            return dataSource;

        } catch (IOException e) {
            throw new FileInputStreamException(e.getMessage(), e);
        }
    }

}

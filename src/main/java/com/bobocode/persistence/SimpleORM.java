package com.bobocode.persistence;

import com.bobocode.persistence.api.EntityCollector;
import com.bobocode.persistence.api.EntityInfo;
import com.bobocode.persistence.api.EntityManager;
import com.bobocode.persistence.api.EntityManagerImpl;
import com.bobocode.persistence.db.DatabaseConnection;

import javax.sql.DataSource;
import java.util.Map;

public class SimpleORM {

    private static final String DB_ORM_CONFIG = "src/main/resources/META-DB/orm.properties";
    private final DataSource dataSource = DatabaseConnection.getPGDataSource();
    private final Map<Class<?>, EntityInfo> entityInfos;

    public EntityManager entityManager() {
        return new EntityManagerImpl(dataSource, entityInfos);
    }

    public SimpleORM() {
        EntityCollector entityCollector = new EntityCollector();
        entityInfos = entityCollector.collectEntityInfo(DB_ORM_CONFIG);
    }

}

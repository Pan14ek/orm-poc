package com.bobocode.persistence.api;

import com.bobocode.persistence.exception.InvalidSQLExecutionException;

import javax.sql.DataSource;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;

import static com.bobocode.persistence.util.FieldUtils.getColumnName;
import static java.lang.String.format;

public class EntityManagerImpl implements EntityManager {

    private final DataSource dataSource;
    private final Map<Class<?>, EntityInfo> entityInfos;

    public EntityManagerImpl(DataSource dataSource, Map<Class<?>, EntityInfo> entityInfos) {
        this.dataSource = dataSource;
        this.entityInfos = entityInfos;
    }

    @Override
    public <T> T find(Class<T> entityClass, Object primaryKey) {
        EntityInfo entityInfo = entityInfos.get(entityClass);

        try (Connection connection = dataSource.getConnection()) {
            Statement statement = connection.createStatement();

            ResultSet resultSet = statement.executeQuery(getQueryByPrimaryKey(primaryKey, entityInfo));

            return resultSet.next() ? createEntity(resultSet, entityClass) : null;
        } catch (Exception e) {
            throw new InvalidSQLExecutionException(e.getMessage(), e);
        }
    }

    private static String getQueryByPrimaryKey(Object primaryKey, EntityInfo entityInfo) {
        return format("SELECT * FROM %s WHERE %s=%s", entityInfo.getTableName(), entityInfo.getIdName(), primaryKey);
    }

    private <T> T createEntity(ResultSet resultSet, Class<T> entityClass)
            throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, SQLException {
        T entity = entityClass.getDeclaredConstructor().newInstance();

        for (var field : entityClass.getDeclaredFields()) {
            field.setAccessible(true);
            String columnName = getColumnName(field);
            field.set(entity, getValue(resultSet, field.getType(), columnName));
        }

        return entity;
    }

    private Object getValue(ResultSet resultSet, Class<?> fieldType, String columnName) throws SQLException {
        if (fieldType.equals(Long.TYPE) || fieldType.equals(Long.class)) {
            return resultSet.getLong(columnName);
        }

        return resultSet.getObject(columnName, fieldType);
    }

}
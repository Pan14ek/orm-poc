package com.bobocode.persistence.api;

import com.bobocode.persistence.exception.InvalidFieldException;
import com.bobocode.persistence.exception.InvalidSQLExecutionException;

import javax.sql.DataSource;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static com.bobocode.persistence.util.FieldUtils.getColumnName;
import static java.lang.String.format;

public class EntityManagerImpl implements EntityManager {

    private final DataSource dataSource;
    private final Map<Class<?>, EntityInfo> entityInfos;
    private final Map<PrimaryKey, Object> entities = new HashMap<>();
    private final Map<PrimaryKey, Object[]> entitySnapshots = new HashMap<>();

    public EntityManagerImpl(DataSource dataSource, Map<Class<?>, EntityInfo> entityInfos) {
        this.dataSource = dataSource;
        this.entityInfos = entityInfos;
    }

    @Override
    public <T> T find(Class<T> entityClass, Object primaryKey) {
        PrimaryKey key = new PrimaryKey(entityClass, primaryKey);

        return Optional.ofNullable((T) entities.get(key))
                .orElseGet(() -> loadEntityById(entityClass, primaryKey));
    }

    @Override
    public void close() {
        performUpdatingChangedEntities();
        entities.clear();
        entitySnapshots.clear();
    }

    private <T> T loadEntityById(Class<T> entityClass, Object primaryKey) {
        EntityInfo entityInfo = entityInfos.get(entityClass);
        try (Connection connection = dataSource.getConnection(); Statement statement = connection.createStatement()) {

            ResultSet resultSet = statement.executeQuery(getQueryByPrimaryKey(primaryKey, entityInfo));

            if (resultSet.next()) {
                T entity = createEntity(resultSet, entityClass);

                PrimaryKey newPrimaryKey = new PrimaryKey(entityClass, primaryKey);

                entities.put(newPrimaryKey, entity);
                entitySnapshots.put(newPrimaryKey, getValues(entity));

                return entity;
            }

            return null;
        } catch (Exception e) {
            throw new InvalidSQLExecutionException(e.getMessage(), e);
        }
    }

    private static String getQueryByPrimaryKey(Object primaryKey, EntityInfo entityInfo) {
        return format("SELECT * FROM %s WHERE %s=%s", entityInfo.getTableName(), entityInfo.getIdName(), primaryKey);
    }

    private <T> T createEntity(ResultSet resultSet, Class<T> entityClass) throws NoSuchMethodException, InvocationTargetException,
            InstantiationException, IllegalAccessException, SQLException {
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

    private Object[] getValues(Object entity) {
        Field[] fields = entity.getClass().getDeclaredFields();
        Object[] snapshots = new Object[fields.length];

        for (int i = 0; i < snapshots.length; i++) {
            snapshots[i] = getSnapshotValue(fields[i], entity);
        }

        return snapshots;
    }

    private Object getSnapshotValue(Field field, Object entity) {
        try {
            field.setAccessible(true);
            return field.get(entity);
        } catch (IllegalAccessException e) {
            throw new InvalidFieldException(e.getMessage(), e);
        }
    }

    private void performUpdatingChangedEntities() {
        entities.entrySet().stream()
                .filter(this::isUpdatedEntity)
                .forEach(this::updateEntity);
    }

    private void updateEntity(Map.Entry<PrimaryKey, Object> entry) {
        PrimaryKey primaryKey = entry.getKey();
        EntityInfo entityInfo = entityInfos.get(primaryKey.getType());
        String query = generateUpdateQuery(primaryKey);
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            Field[] fields = primaryKey.getType().getDeclaredFields();
            int index = 0;

            for (Field field : fields) {
                String columnName = getColumnName(field);

                if (!entityInfo.getIdName().equals(columnName)) {
                    field.setAccessible(true);
                    preparedStatement.setObject(index + 1, field.get(entry.getValue()));
                    index++;
                }
            }
            preparedStatement.setObject(index + 1, primaryKey.getIdentifier());

            preparedStatement.executeUpdate();
        } catch (SQLException | IllegalAccessException e) {
            throw new InvalidSQLExecutionException(e.getMessage(), e);
        }
    }

    private boolean isUpdatedEntity(Map.Entry<PrimaryKey, Object> entry) {
        PrimaryKey primaryKey = entry.getKey();
        Field[] fields = primaryKey.getType().getDeclaredFields();
        Object[] snapshots = entitySnapshots.get(primaryKey);

        for (int i = 0; i < snapshots.length; i++) {
            if (!snapshots.equals(getSnapshotValue(fields[i], entry.getValue()))) {
                return true;
            }
        }

        return false;
    }

    private String generateUpdateQuery(PrimaryKey primaryKey) {
        String updateQuery = "UPDATE %s SET %s WHERE %s = ?";
        EntityInfo entityInfo = entityInfos.get(primaryKey.getType());

        return String.format(
                updateQuery,
                entityInfo.getTableName(),
                generateValuesQuery(primaryKey, entityInfo),
                entityInfo.getIdName()
        );
    }

    private String generateValuesQuery(PrimaryKey primaryKey, EntityInfo entityInfo) {
        Class<?> type = primaryKey.getType();

        StringBuilder stringBuilder = new StringBuilder();

        for (Field field : type.getDeclaredFields()) {
            String columnName = getColumnName(field);

            if (!entityInfo.getIdName().equals(columnName)) {
                stringBuilder.append(String.format("%s = ?", columnName)).append(',');
            }
        }

        String query = stringBuilder.toString();

        if (query.endsWith(",")) {
            return query.substring(0, query.length() - 1);
        }

        return query;
    }

}

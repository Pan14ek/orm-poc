package com.bobocode.persistence.api;

import com.bobocode.persistence.annotation.Entity;
import com.bobocode.persistence.annotation.Table;
import com.bobocode.persistence.exception.FileInputStreamException;
import org.reflections.Reflections;

import java.io.FileInputStream;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import static com.bobocode.persistence.util.FieldUtils.getColumnName;
import static com.bobocode.persistence.util.FieldUtils.getIdName;
import static java.util.stream.Collectors.toMap;

public class EntityCollector {

    public Map<Class<?>, EntityInfo> collectEntityInfo(String ormConfigPropertiesPath) {
        Properties properties = new Properties();

        try (FileInputStream fileInputStream = new FileInputStream(ormConfigPropertiesPath)) {
            properties.load(fileInputStream);

            Reflections reflections = new Reflections(properties.getProperty("orm.package"));
            Set<Class<?>> classes = reflections.getTypesAnnotatedWith(Entity.class);

            return classes.stream()
                    .collect(toMap(entityType -> entityType, EntityCollector::createEntityInfo));
        } catch (Exception e) {
            throw new FileInputStreamException(e.getMessage(), e);
        }
    }

    private static EntityInfo createEntityInfo(Class<?> entityType) {
        EntityInfo entityInfo = new EntityInfo();

        String tableName = entityType.getAnnotation(Table.class).name();
        entityInfo.setTableName(tableName);
        entityInfo.setIdName(getIdName(entityType.getDeclaredFields()));

        Map<String, Class<?>> fieldTypes = getFieldTypes(entityType.getDeclaredFields());
        entityInfo.setFieldTypes(fieldTypes);

        return entityInfo;
    }

    private static Map<String, Class<?>> getFieldTypes(Field[] entityType) {
        Map<String, Class<?>> fieldTypes = new HashMap<>();

        for (Field field : entityType) {
            String fieldName = getColumnName(field);
            fieldTypes.put(fieldName, field.getType());
        }
        return fieldTypes;
    }

}

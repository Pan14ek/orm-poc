package com.bobocode.persistence.util;

import com.bobocode.persistence.annotation.Column;
import com.bobocode.persistence.annotation.Id;

import java.lang.reflect.Field;
import java.util.Arrays;

public final class FieldUtils {

    private FieldUtils() {
    }

    public static String getIdName(Field[] fields) {
        return Arrays.stream(fields)
                .filter(field -> field.isAnnotationPresent(Id.class))
                .findFirst()
                .orElseThrow()
                .getName();
    }

    public static String getColumnName(Field field) {
        if (!field.isAnnotationPresent(Column.class)) {
            return field.getName();
        }
        return field.getAnnotation(Column.class).name();
    }

}

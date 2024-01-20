package com.bobocode.persistence.api;

import java.util.Map;
import java.util.Objects;

public class EntityInfo {

    private String tableName;
    private String idName;
    private Map<String, Class<?>> fieldTypes;//todo: I thought about the performance approach... But probably I will remove it.

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getIdName() {
        return idName;
    }

    public void setIdName(String idName) {
        this.idName = idName;
    }

    public Map<String, Class<?>> getFieldTypes() {
        return fieldTypes;
    }

    public void setFieldTypes(Map<String, Class<?>> fieldTypes) {
        this.fieldTypes = fieldTypes;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EntityInfo that = (EntityInfo) o;
        return Objects.equals(tableName, that.tableName) && Objects.equals(idName, that.idName) && Objects.equals(fieldTypes, that.fieldTypes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tableName, idName, fieldTypes);
    }

    @Override
    public String toString() {
        return "EntityInfo{" +
                "tableName='" + tableName + '\'' +
                ", idName='" + idName + '\'' +
                ", fieldTypes=" + fieldTypes +
                '}';
    }
}

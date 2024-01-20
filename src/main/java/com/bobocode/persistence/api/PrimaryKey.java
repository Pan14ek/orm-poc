package com.bobocode.persistence.api;

import java.util.Objects;

public class PrimaryKey {

    private Class<?> type;
    private Object identifier;

    public PrimaryKey(Class<?> type, Object identifier) {
        this.type = type;
        this.identifier = identifier;
    }

    public Class<?> getType() {
        return type;
    }

    public void setType(Class<?> type) {
        this.type = type;
    }

    public Object getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PrimaryKey that = (PrimaryKey) o;
        return Objects.equals(type, that.type) && Objects.equals(identifier, that.identifier);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, identifier);
    }

    @Override
    public String toString() {
        return "PrimaryKey{" +
                "type=" + type +
                ", identifier='" + identifier + '\'' +
                '}';
    }
}

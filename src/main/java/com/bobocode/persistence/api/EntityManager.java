package com.bobocode.persistence.api;

public interface EntityManager {

    public <T> T find(Class<T> entityClass, Object primaryKey);

}

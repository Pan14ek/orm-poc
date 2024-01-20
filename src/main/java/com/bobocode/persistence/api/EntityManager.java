package com.bobocode.persistence.api;

public interface EntityManager extends AutoCloseable {

    <T> T find(Class<T> entityClass, Object primaryKey);

}

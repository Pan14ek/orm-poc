package com.bobocode.persistence;

import com.bobocode.persistence.api.EntityManager;

public class Main {
    public static void main(String[] args) {
        SimpleORM simpleORM = new SimpleORM();

        EntityManager entityManager = simpleORM.entityManager();

        Person person = entityManager.find(Person.class, 1);

        System.out.println(person);

        Note note = entityManager.find(Note.class, 1);

        System.out.println(note);
    }
}
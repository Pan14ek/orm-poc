package com.bobocode.persistence;

import com.bobocode.persistence.api.EntityManager;

public class Main {
    public static void main(String[] args) throws Exception {
        SimpleORM simpleORM = new SimpleORM();

        try (EntityManager entityManager = simpleORM.entityManager()) {
            Person person = entityManager.find(Person.class, 1);
            System.out.println(person);

            person.setFirstName("Name was updated");
            person.setLastName("Surname was updated");
        }

        try (EntityManager entityManager = simpleORM.entityManager()) {
            Person person1 = entityManager.find(Person.class, 1);
            System.out.println(person1);
        }

    }
}
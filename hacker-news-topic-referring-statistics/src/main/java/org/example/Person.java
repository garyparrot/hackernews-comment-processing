package org.example;

import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.serialization.Serializer;

import java.util.Map;

public class Person {
    private String firstName;
    private String lastName;
    private int age;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public Person(String firstName, String lastName, int age) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
    }

    public Person() { }

    public static class PersonSerializer implements Serializer {
        @Override
        public void configure(Map configs, boolean isKey) {

        }

        @Override
        public byte[] serialize(String s, Object o) {
            return new byte[0];
        }

        @Override
        public byte[] serialize(String topic, Headers headers, Object data) {
            return new byte[0];
        }

        @Override
        public void close() {

        }
    }
}

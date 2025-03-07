package org.example;

public class Undergraduate extends Member {
    public Undergraduate(int id, String firstName, String lastName, String personalNumber) {
        super(id, firstName, lastName, personalNumber, 3);
    }

    @Override
    public boolean returnbook(Book book) {
        return false;
    }
}

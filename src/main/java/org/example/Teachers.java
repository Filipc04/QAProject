package org.example;

public class Teachers extends Member {
    public Teachers(int id, String firstName, String lastName, String personalNumber) {
        super(id, firstName, lastName, personalNumber, 10);
    }

    @Override
    public boolean returnbook(Book book) {
        return false;
    }
}


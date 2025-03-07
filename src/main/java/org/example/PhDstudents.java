package org.example;

public class PhDstudents extends Member {
    public PhDstudents(int id, String firstName, String lastName, String personalNumber) {
        super(id, firstName, lastName, personalNumber, 3);
    }

    @Override
    public boolean returnbook(Book book) {
        return false;
    }
}

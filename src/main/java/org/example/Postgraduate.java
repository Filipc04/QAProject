package org.example;
public class Postgraduate extends Member {
    public Postgraduate(int id, String firstName, String lastName, String personalNumber) {
        super(id, firstName, lastName, personalNumber, 5);
    }

    @Override
    public boolean returnbook(Book book) {
        return false;
    }
}

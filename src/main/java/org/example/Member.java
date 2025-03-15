package org.example;

public class Member {
    public String firstName;
    public String id;
    public int level;  // 1 = undergraduate, 2 = postgraduate, 3 = PhD, 4 = teacher
    public int borrowedBooksCount = 0; // Antal böcker medlemmen har lånat
    public int lateReturns = 0; // Räknare för förseningar
    public boolean suspended = false; // Om medlemmen är suspenderad
}
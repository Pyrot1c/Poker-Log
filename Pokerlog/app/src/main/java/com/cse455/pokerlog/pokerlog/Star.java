package com.cse455.pokerlog.pokerlog;

/**
 * Created by noahrivera on 3/2/15.
 */

public class Star {

    private int _id, _count;

    private Contact _owner;

    public Star(int id, Contact owner, int count) {
        _id = id;
        _owner = owner;
        _count = count;
    }

    void decrement() { --_count; }
    void increment() { ++_count; }

    public int getId() { return _id; }

    public Contact getOwner() { return _owner; }

    public String getOwnerName() { return _owner.getName(); }

    public int getCount() { return _count; }
}
package com.cse455.pokerlog.pokerlog;

/**
 * Created by noahrivera on 3/2/15.
 */

public class Food {

    private int _id, _cost;

    private Contact _player;

    public Food(int id, Contact player, int cost) {
        _id = id;
        _player = player;
        _cost = cost;
    }

    public int getId() { return _id; }

    public Contact getPlayer() { return _player; }

    public int getCost() { return _cost; }
}
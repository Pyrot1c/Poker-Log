package com.cse455.pokerlog.pokerlog;

/**
 * Created by noahrivera on 3/16/15.
 */

public class SideBet {

    private int _id, _amount;

    private Contact _winner, _loser;

    public SideBet(Contact winner, Contact loser, int amount) {
        _winner = winner;
        _loser = loser;
        _amount = amount;
    }

    public SideBet(int id, Contact winner, Contact loser, int amount) {
        _id = id;
        _winner = winner;
        _loser = loser;
        _amount = amount;
    }

    public void setId(int id) { _id = id; }
    public int getId() { return _id; }

    public Contact getWinner() { return _winner; }

    public Contact getLoser() { return _loser; }

    public int getAmount() { return _amount; }
}
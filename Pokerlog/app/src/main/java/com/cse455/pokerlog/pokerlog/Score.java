package com.cse455.pokerlog.pokerlog;

/**
 * Created by noahrivera on 3/16/15.
 */

public class Score {

    private int _id, _winnings, _debt;

    private Contact _player;

    public Score(int id, Contact player, int winnings, int debt) {
        _id = id;
        _player = player;
        _winnings = winnings;
        _debt = debt;
    }

    public void takeFromPot() {
        _debt += 5;
    }

    public void returnToPot() {
        _debt -= 5;
    }

    public void updateWinnings(int amount) {
        _winnings += amount;
    }

    public int getId() { return _id; }

    public Contact getPlayer() { return _player; }

    public int getWinnings() { return _winnings; }

    public int getDebt() { return _debt; }
}
package com.cse455.pokerlog.pokerlog;

import android.net.Uri;


public class Contact {

    private String _name, _phone, _email;
    private int _id;

    public Contact(int id, String name, String phone, String email) {
        _id = id;
        _name = name;
        _phone = phone;
        _email = email;

    }

    public int getId() { return _id; }

    public String getName() {
        return _name;
    }

    public String getPhone() {
        return _phone;
    }

    public String getEmail() {
        return _email;
    }


}

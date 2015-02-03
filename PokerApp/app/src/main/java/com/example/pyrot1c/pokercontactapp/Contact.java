package com.example.pyrot1c.pokercontactapp;

/**
 * Created by Pyrot1c on 1/15/2015.
 */
//Contact class describes what a contact is, as strings.
public class Contact {

    private String _name, _phone, _email, _address;

    public Contact (String name, String phone, String email, String address) {
        _name = name;
        _phone = phone;
        _email = email;
        _address = address;

    }

    public String getName() {
        return _name;
    }
    public String getPhone() {
        return _phone;
    }
    public String getEmail() {
        return _email;
    }
    public String getAddress() {
        return _address;
    }
}

package com.cse455.pokerlog.pokerlog;

public class Contact {

    private String _name, _phone, _email, _address;

    private int _id;

    public Contact(String name, String phone, String email, String address) {
        _name = name;
        _phone = phone;
        _email = email;
        _address = address;
    }

    public Contact(int id, String name, String phone, String email, String address) {
        _id = id;
        _name = name;
        _phone = phone;
        _email = email;
        _address = address;
    }

    public void setId(int id) { _id = id; }
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

    public String getAddress() {
        return _address;
    }

}


package com.cse455.pokerlog.pokerlog;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;


public class DatabaseHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_NAME = "pokerdatabase",
            TABLE_CONTACTS = "contacts",
            KEY_ID = "id",
            KEY_NAME = "name",
            KEY_PHONE = "phone",
            KEY_EMAIL = "email",
            KEY_ADDRESS = "address",

            TABLE_STARS = "stars",
            KEY_OWNER_ID = "owner",
            KEY_COUNT = "count";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_CONTACTS + "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_NAME + " TEXT," + KEY_PHONE + " TEXT," + KEY_EMAIL + " TEXT," + KEY_ADDRESS + " TEXT)");
        db.execSQL("CREATE TABLE " + TABLE_STARS + "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_OWNER_ID + " TEXT," + KEY_COUNT + " INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_STARS);

        onCreate(db);
    }

    public void createContact(Contact contact) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(KEY_NAME, contact.getName());
        values.put(KEY_PHONE, contact.getPhone());
        values.put(KEY_EMAIL, contact.getEmail());
        values.put(KEY_ADDRESS, contact.getAddress());

        db.insert(TABLE_CONTACTS, null, values);
        db.close();
    }

    public Contact getContact(int id) {
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(TABLE_CONTACTS, new String[] { KEY_ID, KEY_NAME, KEY_PHONE, KEY_EMAIL, KEY_ADDRESS}, KEY_ID + "=?", new String[] { String.valueOf(id) }, null, null, null, null );

        if (cursor != null)
            cursor.moveToFirst();

        Contact contact = new Contact(Integer.parseInt(cursor.getString(0)), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4));
        db.close();
        cursor.close();
        return contact;
    }

    public void deleteContact(Contact contact) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_CONTACTS, KEY_ID + "=?", new String[] { String.valueOf(contact.getId()) });
        db.close();
    }

    public int getContactsCount() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_CONTACTS, null);
        int count = cursor.getCount();
        db.close();
        cursor.close();

        return count;
    }

    public int updateContact(Contact contact) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(KEY_NAME, contact.getName());
        values.put(KEY_PHONE, contact.getPhone());
        values.put(KEY_EMAIL, contact.getEmail());
        values.put(KEY_ADDRESS, contact.getAddress());

        int rowsAffected = db.update(TABLE_CONTACTS, values, KEY_ID + "=?", new String[] { String.valueOf(contact.getId()) });
        db.close();

        return rowsAffected;
    }

    public List<Contact> getAllContacts() {
        List<Contact> contacts = new ArrayList<Contact>();

        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_CONTACTS, null);

        if (cursor.moveToFirst()) {
            do {
                contacts.add(new Contact(Integer.parseInt(cursor.getString(0)), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4)));
            }
            while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return contacts;
    }

    public void createStar(Star star) {
        // Open the database
        SQLiteDatabase db = getWritableDatabase();

        // Store the star data into an intermediate container
        ContentValues values = new ContentValues();

        values.put(KEY_OWNER_ID, star.getOwner().getId());
        values.put(KEY_COUNT, star.getCount());

        // Insert the new data
        db.insert(TABLE_STARS, null, values);

        // Clean up
        db.close();
    }

    public Star getStar(int id) {
        // Open the database
        SQLiteDatabase db = getReadableDatabase();

        // Find the star
        Cursor cursor = db.query(TABLE_STARS, new String[] {KEY_ID, KEY_OWNER_ID, KEY_COUNT}, KEY_ID + "=" + String.valueOf(id), null, null, null, null, null );

        if (cursor != null)
            cursor.moveToFirst();

        // Create the star object
        Star star = new Star(Integer.parseInt(cursor.getString(0)), getContact(Integer.parseInt(cursor.getString(1))), Integer.parseInt(cursor.getString(2)));

        // Clean up
        cursor.close();
        db.close();

        return star;
    }

    public void deleteStar(Star star) {
        // Open the database
        SQLiteDatabase db = getWritableDatabase();

        // Search for and delete the star
        db.delete(TABLE_STARS, KEY_ID + "=" + String.valueOf(star.getId()), null);

        // Clean up
        db.close();
    }

    public void updateStar(Star star) {
        // Open the database
        SQLiteDatabase db = getWritableDatabase();

        // Store the star data into an intermediate container
        ContentValues values = new ContentValues();

        values.put(KEY_OWNER_ID, star.getOwner().getId());
        values.put(KEY_COUNT, star.getCount());

        // Find and update this star
        db.update(TABLE_STARS, values, KEY_ID + "=" + String.valueOf(star.getId()), null);

        // Clean up
        db.close();
    }

    public List<Star> getAllStars() {
        // Initialize an empty list in which to store stars
        List<Star> stars = new ArrayList<Star>();

        // Open the database
        SQLiteDatabase db = getWritableDatabase();

        // Search for all stars
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_STARS, null);

        // Populate the list
        if (cursor.moveToFirst()) {
            do {
                stars.add(getStar(Integer.parseInt(cursor.getString(0))));
            } while (cursor.moveToNext());
        }

        // Clean up
        cursor.close();
        db.close();

        return stars;
    }

    public int getStarsCount() {
        // Open the database
        SQLiteDatabase db = getReadableDatabase();

        // Count the number of star entries
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_STARS, null);
        int count = cursor.getCount();

        // Clean up
        db.close();
        cursor.close();

        return count;
    }
}

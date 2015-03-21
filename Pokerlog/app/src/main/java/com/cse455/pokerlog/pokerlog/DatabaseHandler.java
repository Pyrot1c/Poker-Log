package com.cse455.pokerlog.pokerlog;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class DatabaseHandler extends SQLiteOpenHelper {

    // Database version
    private static final int DATABASE_VERSION = 1;

    // Data base strings
    private static final String DATABASE_NAME = "pokerdatabase",
            TABLE_CONTACTS = "contacts",
            KEY_ID = "id",
            KEY_NAME = "name",
            KEY_PHONE = "phone",
            KEY_EMAIL = "email",
            KEY_ADDRESS = "address",

            TABLE_STARS = "stars",
            KEY_OWNER_ID = "owner",
            KEY_COUNT = "count",

            TABLE_SCORES = "scores",
            KEY_PLAYER_ID = "player",
            KEY_WINNINGS = "winnings",
            KEY_DEBT = "debt",

            TABLE_SIDEBETS = "sidebets",
            KEY_WINNER_ID = "winner",
            KEY_LOSER_ID = "loser",
            KEY_AMOUNT = "amount",

            TABLE_FOOD = "food",
            // KEY_PLAYER_ID = "player",
            KEY_COST = "cost",

            TABLE_GAME = "game",
            KEY_SHEET_NAME = "sheetname",
            KEY_POT_TOTAL = "pot";

    // Constructor
    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_CONTACTS + "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_NAME + " TEXT," + KEY_PHONE + " TEXT," + KEY_EMAIL + " TEXT," + KEY_ADDRESS + " TEXT)");
        db.execSQL("CREATE TABLE " + TABLE_STARS + "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_OWNER_ID + " INTEGER," + KEY_COUNT + " INTEGER)");
        db.execSQL("CREATE TABLE " + TABLE_SCORES + "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_PLAYER_ID + " INTEGER," + KEY_WINNINGS + " INTEGER," + KEY_DEBT + " INTEGER)");
        db.execSQL("CREATE TABLE " + TABLE_SIDEBETS + "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_WINNER_ID + " INTEGER," + KEY_LOSER_ID + " INTEGER," + KEY_AMOUNT + " INTEGER)");
        db.execSQL("CREATE TABLE " + TABLE_FOOD + "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_PLAYER_ID + " INTEGER," + KEY_COST + " INTEGER)");
        db.execSQL("CREATE TABLE " + TABLE_GAME + "(" + KEY_SHEET_NAME + " STRING, " + KEY_POT_TOTAL + " INTEGER)");

        // Create the game values record
        ContentValues values = new ContentValues();

        // Use a name based on the date and time
        DateFormat df = new SimpleDateFormat("dd-MM-yyyy-HHmm");
        String date = df.format(Calendar.getInstance().getTime());
        values.put(KEY_SHEET_NAME, "Sheet-" + date);
        
        values.put(KEY_POT_TOTAL, 0);
        db.insert(TABLE_GAME, null, values);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_STARS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SCORES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SIDEBETS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FOOD);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GAME);
        onCreate(db);
    }

    public void deleteDataBase() {
        // Open the database
        SQLiteDatabase db = getWritableDatabase();

        // Delete Data
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_STARS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SCORES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SIDEBETS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FOOD);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GAME);

        // Reinitialize data
        onCreate(db);

        // Clean up
        db.close();
    }

    /*------------------------
        Contact Functions
    ------------------------*/

    public void createContact(Contact contact) {
        // Open the database
        SQLiteDatabase db = getWritableDatabase();

        // Store the contact data in an intermediate container
        ContentValues values = new ContentValues();

        values.put(KEY_NAME, contact.getName());
        values.put(KEY_PHONE, contact.getPhone());
        values.put(KEY_EMAIL, contact.getEmail());
        values.put(KEY_ADDRESS, contact.getAddress());

        // Insert the contact
        db.insert(TABLE_CONTACTS, null, values);

        // Create a new score item for the player
        values = new ContentValues();

        values.put(KEY_PLAYER_ID, contact.getId() + 1);
        values.put(KEY_WINNINGS, 0);
        values.put(KEY_DEBT, 0);

        db.insert(TABLE_SCORES, null, values);

        // Clean up
        db.close();
    }

    public void deleteContact(Contact contact) {
        // Open the database
        SQLiteDatabase db = getWritableDatabase();

        // Delete the contact
        db.delete(TABLE_CONTACTS, KEY_ID + "=?", new String[] { String.valueOf(contact.getId()) });

        // Delete other items associated with the contact
        db.delete(TABLE_STARS, KEY_OWNER_ID + "=?", new String[] { String.valueOf(contact.getId()) });
        db.delete(TABLE_SCORES, KEY_PLAYER_ID + "=?", new String[] { String.valueOf(contact.getId()) });
        db.delete(TABLE_SIDEBETS, KEY_WINNER_ID + "=?" + " OR " + KEY_LOSER_ID + "=?", new String[] { String.valueOf(contact.getId()), String.valueOf(contact.getId()) });
        
        // Clean up
        db.close();
    }

    public void updateContact(Contact contact) {
        // Open the database
        SQLiteDatabase db = getWritableDatabase();

        // Store the contact data into an intermediate container
        ContentValues values = new ContentValues();

        values.put(KEY_NAME, contact.getName());
        values.put(KEY_PHONE, contact.getPhone());
        values.put(KEY_EMAIL, contact.getEmail());
        values.put(KEY_ADDRESS, contact.getAddress());

        // Push the updated information ot the database
        db.update(TABLE_CONTACTS, values, KEY_ID + "=?", new String[] { String.valueOf(contact.getId()) });

        // Clean up
        db.close();
    }

    public Contact getContact(int id) {
        // Open the database
        SQLiteDatabase db = getReadableDatabase();

        // Search for the contact
        Cursor cursor = db.query(TABLE_CONTACTS, new String[] { KEY_ID, KEY_NAME, KEY_PHONE, KEY_EMAIL, KEY_ADDRESS}, KEY_ID + "=?", new String[] { String.valueOf(id) }, null, null, null, null );
        
        // Check if the contact exists
        if (!cursor.moveToFirst())
            return null;

        // Create the contact
        Contact contact = new Contact(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4));

        // Clean up
        db.close();
        cursor.close();

        return contact;
    }

    public List<Contact> getAllContacts() {
        // Create a list to store contacts
        List<Contact> contacts = new ArrayList<Contact>();

        // Search for all contacts
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_CONTACTS, null);

        // Add each contact to the list
        if (cursor.moveToFirst()) {
            do {
                contacts.add(new Contact(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4)));
            }
            while (cursor.moveToNext());
        }

        // Clean up
        cursor.close();
        db.close();

        return contacts;
    }

    public int getContactsCount() {
        // Open the database
        SQLiteDatabase db = getReadableDatabase();

        // Search for all contacts
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_CONTACTS, null);

        // Get the amount of contacts
        int count = cursor.getCount();

        // Clean up
        db.close();
        cursor.close();

        return count;
    }

    public String[] getContactStringList() {
        // Get a list of all contacts
        List<Contact> contact_list = getAllContacts();

        // Create the list of strings
        String[] string_list = new String[contact_list.size()];

        // Populate the string list with the contact list
        for(int i = 0; i < contact_list.size(); ++i)
            string_list[i] = contact_list.get(i).getName();

        return string_list;
    }

    public int getContactIdFromName(String name) {
        // Open the database
        SQLiteDatabase db = getReadableDatabase();

        // Search for the contact by name
        Cursor cursor = db.query(TABLE_CONTACTS, new String[] { KEY_ID, KEY_NAME, KEY_PHONE, KEY_EMAIL, KEY_ADDRESS}, KEY_NAME + "=?", new String[] { name }, null, null, null, null );

        // Will return -1 if contact doesn't exist
        int return_id = -1;

        if (cursor != null) {
            if (cursor.moveToFirst()) // Goes to the first entry if contact exists
                return_id = cursor.getInt(0);
        }

        // Cleanup
        db.close();
        cursor.close();

        return return_id;
    }

    /*------------------------
        Score Functions
    ------------------------*/

    public void updateScore(Score score) {
        // Open the database
        SQLiteDatabase db = getWritableDatabase();

        // Store the score value in an intermediate container
        ContentValues values = new ContentValues();

        values.put(KEY_PLAYER_ID, score.getPlayer().getId());
        values.put(KEY_WINNINGS, score.getWinnings());
        values.put(KEY_DEBT, score.getDebt());

       // Find and update this star
        db.update(TABLE_SCORES, values, KEY_ID + "=" + String.valueOf(score.getId()), null);

        // Clean up
        db.close();
    }

    public Score getScore(int id) {
        // Open the database
        SQLiteDatabase db = getReadableDatabase();

        // Find the score
        Cursor cursor = db.query(TABLE_SCORES, new String[] {KEY_ID, KEY_PLAYER_ID, KEY_WINNINGS, KEY_DEBT}, KEY_ID + "=" + String.valueOf(id), null, null, null, null, null );

        // Check if the score exists
        if (!cursor.moveToFirst())
            return null;

        // Create the score object
        Score score = new Score(cursor.getInt(0), getContact(cursor.getInt(1)), cursor.getInt(2), cursor.getInt(3));

        // Clean up
        cursor.close();
        db.close();

        return score;
    }

    public List<Score> getAllScores() {
        // Initialize an empty list in which to store scores
        List<Score> scores = new ArrayList<Score>();

        // Open the database
        SQLiteDatabase db = getWritableDatabase();

        // Search for scores
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_SCORES, null);

        // Populate the list
        if (cursor.moveToFirst()) {
            do {
                scores.add(getScore(cursor.getInt(0)));
            }
            while (cursor.moveToNext());
        }

        // Clean up
        cursor.close();
        db.close();

        return scores;
    }

    public int getContactWinnings(int contact_id) {
        // Open the database
        SQLiteDatabase db = getWritableDatabase();

        // Find the player
        Cursor cursor = db.query(TABLE_SCORES, new String[] {KEY_ID, KEY_PLAYER_ID, KEY_WINNINGS, KEY_DEBT}, KEY_PLAYER_ID + "=" + String.valueOf(contact_id), null, null, null, null, null );

        int value = -1; // Return -1 if score doesn't exist

        if (cursor.moveToFirst())
                value = cursor.getInt(2);

        // Clean up
        cursor.close();
        db.close();

        return value;
    }

    public int getContactDebt(int contact_id) {
        // Open the database
        SQLiteDatabase db = getWritableDatabase();

        // Find the player
        Cursor cursor = db.query(TABLE_SCORES, new String[] {KEY_ID, KEY_PLAYER_ID, KEY_WINNINGS, KEY_DEBT}, KEY_PLAYER_ID + "=" + String.valueOf(contact_id), null, null, null, null, null );

        int value = -1; // Return -1 if score doesn't exist

        if (cursor.moveToFirst())
            value = cursor.getInt(3);

        // Clean up
        cursor.close();
        db.close();

        return value;
    }

    public int getAllWinnings() {
        // Start the sum at 0
        int value = 0;

        // Open the database
        SQLiteDatabase db = getWritableDatabase();

        // Search for scores
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_SCORES, null);

        // Add all winnings
        if (cursor.moveToFirst()) {
            do {
                value += cursor.getInt(2);
            }
            while (cursor.moveToNext());
        }

        // Clean up
        cursor.close();
        db.close();

        return value;
    }

    public int getAllDebt() {
        // Start the sum at 0
        int value = 0;

        // Open the database
        SQLiteDatabase db = getWritableDatabase();

        // Search for scores
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_SCORES, null);

        // Add all debt
        if (cursor.moveToFirst()) {
            do {
                value += cursor.getInt(3);
            }
            while (cursor.moveToNext());
        }

        // Clean up
        cursor.close();
        db.close();

        return value;
    }

    /*------------------------
        Side Bet Functions
    ------------------------*/

    public void createSideBet(SideBet side_bet) {
        // Open the database
        SQLiteDatabase db = getWritableDatabase();

        // Store the side bet data into an intermediate container
        ContentValues values = new ContentValues();

        values.put(KEY_WINNER_ID, side_bet.getWinner().getId());
        values.put(KEY_LOSER_ID, side_bet.getLoser().getId());
        values.put(KEY_AMOUNT, side_bet.getAmount());

        // Insert the new data
        db.insert(TABLE_SIDEBETS, null, values);

        // Clean up
        db.close();
    }

    public void deleteSideBet(SideBet side_bet) {
        // Open the database
        SQLiteDatabase db = getWritableDatabase();

        // Search for and delete the side bet
        db.delete(TABLE_SIDEBETS, KEY_ID + "=" + String.valueOf(side_bet.getId()), null);

        // Clean up
        db.close();
    }

    public SideBet getSideBet(int id) {
        // Open the database
        SQLiteDatabase db = getReadableDatabase();

        // Find the side bet
        Cursor cursor = db.query(TABLE_SIDEBETS, new String[] {KEY_ID, KEY_WINNER_ID, KEY_LOSER_ID, KEY_AMOUNT}, KEY_ID + "=" + String.valueOf(id), null, null, null, null, null );

        if (cursor != null)
            cursor.moveToFirst();

        // Create the side bet object
        SideBet side_bet = new SideBet(cursor.getInt(0), getContact(cursor.getInt(1)), getContact(cursor.getInt(2)), cursor.getInt(3));

        // Clean up
        cursor.close();
        db.close();

        return side_bet;
    }

    public List<SideBet> getAllSideBets() {
        // Initialize an empty list in which to store stars
        List<SideBet> side_bets = new ArrayList<>();

        // Open the database
        SQLiteDatabase db = getWritableDatabase();

        // Search for all side bets
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_SIDEBETS, null);

        // Populate the list
        if (cursor.moveToFirst()) {
            do {
                side_bets.add(getSideBet(cursor.getInt(0)));
            } while (cursor.moveToNext());
        }

        // Clean up
        cursor.close();
        db.close();

        return side_bets;
    }

    public int getSideBetsCount() {
        // Open the database
        SQLiteDatabase db = getReadableDatabase();

        // Count the number of side bet entries
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_SIDEBETS, null);
        int count = cursor.getCount();

        // Clean up
        db.close();
        cursor.close();

        return count;
    }

    public int getContactSideBetTotal(int contact_id) {
        // Open the database
        SQLiteDatabase db = getWritableDatabase();

        int value = 0;

        // Find side bets with the player as a winner
        Cursor cursor = db.query(TABLE_SIDEBETS, new String[] {KEY_ID, KEY_WINNER_ID, KEY_LOSER_ID, KEY_AMOUNT}, KEY_WINNER_ID + "=" + String.valueOf(contact_id), null, null, null, null, null );

        if (cursor.moveToFirst()) {
            do {
                value += cursor.getInt(3);
            } while (cursor.moveToNext());
        }

        // Find side bets with the player as a winner
        cursor = db.query(TABLE_SIDEBETS, new String[] {KEY_ID, KEY_WINNER_ID, KEY_LOSER_ID, KEY_AMOUNT}, KEY_LOSER_ID + "=" + String.valueOf(contact_id), null, null, null, null, null );

        if (cursor.moveToFirst()) {
            do {
                value -= cursor.getInt(3);
            } while (cursor.moveToNext());
        }

        // Clean up
        cursor.close();
        db.close();

        return value;
    }

    /*------------------------
        Star Functions
    ------------------------*/

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

    public Star getStar(int id) {
        // Open the database
        SQLiteDatabase db = getReadableDatabase();

        // Find the star
        Cursor cursor = db.query(TABLE_STARS, new String[] {KEY_ID, KEY_OWNER_ID, KEY_COUNT}, KEY_ID + "=" + String.valueOf(id), null, null, null, null, null );

        if (cursor != null)
            cursor.moveToFirst();

        // Create the star object
        Star star = new Star(cursor.getInt(0), getContact(cursor.getInt(1)), cursor.getInt(2));

        // Clean up
        cursor.close();
        db.close();

        return star;
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
                stars.add(getStar(cursor.getInt(0)));
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

    public int getContactStars(int contact_id) {
        // Open the database
        SQLiteDatabase db = getWritableDatabase();

        // Find the player
        Cursor cursor = db.query(TABLE_STARS, new String[] {KEY_ID, KEY_OWNER_ID, KEY_COUNT}, KEY_OWNER_ID + "=" + String.valueOf(contact_id), null, null, null, null, null );

        int value = 0; // Return 0 if player isn't participating doesn't exist

        if (cursor.moveToFirst())
            value = cursor.getInt(2);

        // Clean up
        cursor.close();
        db.close();

        return value;
    }

    /*------------------------
        Food Functions
    ------------------------*/

    public void createFood(Food food) {
        // Open the database
        SQLiteDatabase db = getWritableDatabase();

        // Store the food data into an intermediate container
        ContentValues values = new ContentValues();

        values.put(KEY_PLAYER_ID, food.getPlayer().getId());
        values.put(KEY_COST, food.getCost());

        // Insert the new data
        db.insert(TABLE_FOOD, null, values);

        // Clean up
        db.close();
    }

    public void deleteFood(Food food) {
        // Open the database
        SQLiteDatabase db = getWritableDatabase();

        // Search for and delete the food item
        db.delete(TABLE_FOOD, KEY_ID + "=" + String.valueOf(food.getId()), null);

        // Clean up
        db.close();
    }

    public Food getFood(int id) {
        // Open the database
        SQLiteDatabase db = getReadableDatabase();

        // Find the food item
        Cursor cursor = db.query(TABLE_FOOD, new String[] {KEY_ID, KEY_PLAYER_ID, KEY_COST}, KEY_ID + "=" + String.valueOf(id), null, null, null, null, null );

        if (cursor != null)
            cursor.moveToFirst();

        // Create the food object
        Food food = new Food(cursor.getInt(0), getContact(cursor.getInt(1)), cursor.getInt(2));

        // Clean up
        cursor.close();
        db.close();

        return food;
    }

    public List<Food> getAllFood() {
        // Initialize an empty list in which to store stars
        List<Food> food_items = new ArrayList<>();

        // Open the database
        SQLiteDatabase db = getWritableDatabase();

        // Search for all food items
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_FOOD, null);

        // Populate the list
        if (cursor.moveToFirst()) {
            do {
                food_items.add(getFood(cursor.getInt(0)));
            } while (cursor.moveToNext());
        }

        // Clean up
        cursor.close();
        db.close();

        return food_items;
    }

    public int getFoodCount() {
        // Open the database
        SQLiteDatabase db = getReadableDatabase();

        // Count the number of food entries
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_FOOD, null);
        int count = cursor.getCount();

        // Clean up
        db.close();
        cursor.close();

        return count;
    }

    public int getContactFoodTotal(int contact_id) {
        // Open the database
        SQLiteDatabase db = getWritableDatabase();

        int value = 0;

        // Find food items with the player as a winner
        Cursor cursor = db.query(TABLE_FOOD, new String[] {KEY_ID, KEY_PLAYER_ID, KEY_COST}, KEY_PLAYER_ID + "=" + String.valueOf(contact_id), null, null, null, null, null );

        if (cursor.moveToFirst()) {
            do {
                value += cursor.getInt(2);
            } while (cursor.moveToNext());
        }

        // Clean up
        cursor.close();
        db.close();

        return value;
    }

    /*------------------------
        Game Functions
    ------------------------*/

    public int getPotTotal() {
        // Open the database
        SQLiteDatabase db = getWritableDatabase();

        // Search for the value
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_GAME, null);

        // Get the value
        int pot_value = 0;

        if (cursor.moveToFirst())
            pot_value = cursor.getInt(1);

        // Clean up
        cursor.close();
        db.close();

        return pot_value;
    }

    public void updatePot(int amount) {
        // Open the database
        SQLiteDatabase db = getWritableDatabase();

        // Store the pot data into an intermediate container
        ContentValues values = new ContentValues();
        values.put(KEY_POT_TOTAL, amount);

        db.update(TABLE_GAME, values, null, null);

        // Clean up
        db.close();
    }
}

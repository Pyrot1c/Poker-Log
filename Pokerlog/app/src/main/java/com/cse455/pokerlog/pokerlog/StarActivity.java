package com.cse455.pokerlog.pokerlog;

import android.content.Context;
import android.content.Intent;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.ArrayList;

public class StarActivity extends ActionBarActivity {

    // Database variables
    DatabaseHandler dbHandler;

    // Star list view variables
    List<Star> star_list = new ArrayList<Star>();
    ArrayAdapter<Star> starAdapter;
    ListView starListView;

    // Navigation drawer variables
    private String[] mNavigationItems;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_star);

        // Initialize navigation
        mNavigationItems = getResources().getStringArray(R.array.navigationstrings);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        mDrawerList.setAdapter(new ArrayAdapter<String>(this, R.layout.drawerlistview_item, mNavigationItems));

        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                switch(position) {
                    case 0:
                        intent.setClass(view.getContext(), MainActivity.class);
                        break;
                    case 1:
                        intent.setClass(view.getContext(), SheetActivity.class);
                        break;
                    case 2:
                        intent.setClass(view.getContext(), StarActivity.class);
                        break;
                    default:
                        intent.setClass(view.getContext(), TotalActivity.class);
                }
                startActivity(intent);
                finish();
            }
        });

        // Initialize the add player button
        Button add_player_button = (Button) findViewById(R.id.button_add_player);
        registerForContextMenu(add_player_button);

        // Initialize the database handler
        dbHandler = new DatabaseHandler(getApplicationContext());

        // Initialize the stars list
        star_list.addAll(dbHandler.getAllStars());
        starAdapter = new StarListAdapter();

        // Connect the stars list to the list view
        starListView = (ListView) findViewById(R.id.starlist_view);
        starListView.setAdapter(starAdapter);
    }

    private class StarListAdapter extends ArrayAdapter<Star> {
        public StarListAdapter() {
            super(StarActivity.this, R.layout.starlistview_item, star_list);
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            if (view == null)
                view = getLayoutInflater().inflate(R.layout.starlistview_item, parent, false);

            Star currentStar = star_list.get(position);

            TextView owner = (TextView) view.findViewById(R.id.owner_name);
            owner.setText(currentStar.getOwner().getName());
            TextView star_count = (TextView) view.findViewById(R.id.star_count);
            star_count.setText("Stars x " + Integer.toString(currentStar.getCount()));

            // Set up the subtract button
            Button button_subtract = (Button) view.findViewById(R.id.button_subtract);
            button_subtract.setTag(position); // Connect the button to this item
            button_subtract.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        // Remove the star item if at 0
                        if (star_list.get((int) v.getTag()).getCount() == 0) {
                            dbHandler.deleteStar(star_list.get((int) v.getTag()));
                            star_list.remove((int) v.getTag());
                        } else {
                            star_list.get((int) v.getTag()).decrement();
                            dbHandler.updateStar(star_list.get((int) v.getTag()));
                        }

                        starAdapter.notifyDataSetChanged();
                    }
               }
            );

            // Set up the addition button
            Button button_add = (Button) view.findViewById(R.id.button_add);
            button_add.setTag(position); // Connect the button to this item
            button_add.setOnClickListener(new View.OnClickListener() {
                  public void onClick(View v) {
                      star_list.get((int) v.getTag()).increment();
                      dbHandler.updateStar(star_list.get((int) v.getTag()));
                      starAdapter.notifyDataSetChanged();
                  }
              }
            );

            return view;
        }
    }

    // intent: Check if a given star counter is already in star_list
    private boolean starExists(Star star) {
        for(int i = 0; i < star_list.size(); ++i)
            if (star_list.get(i).getOwner().getId() == star.getOwner().getId())
                return true;

        return false;
    }

    // The add player menu
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle("Add Player");

        // Get a list of all contacts
        List<Contact> contact_list = new ArrayList<Contact>();
        contact_list.addAll(dbHandler.getAllContacts());

        // Create the menu
        for(int i = 0; i < contact_list.size(); ++i)
            menu.add(0, contact_list.get(i).getId(), 0, contact_list.get(i).getName());
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        // Create a star item
        Contact star_contact = dbHandler.getContact(item.getItemId());
        Star star = new Star(star_contact, 0);

        // Add the star item if it doesn't exist
        if (!starExists(star)) {
            star.setId(dbHandler.createStar(star));
            star_list.add(star);
            starAdapter.notifyDataSetChanged();
            Toast.makeText(getApplicationContext(), "Player added to star keeper!", Toast.LENGTH_SHORT).show();
            return true;
        }

        // Display an error
        Toast.makeText(getApplicationContext(), "That player is already active in stars!", Toast.LENGTH_SHORT).show();

        return true;
    }
}

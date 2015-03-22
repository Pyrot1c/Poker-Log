package com.cse455.pokerlog.pokerlog;

import android.content.Intent;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


public class TotalActivity extends ActionBarActivity {

    // Database variables
    DatabaseHandler dbHandler;

    // Navigation drawer variables
    private String[] mNavigationItems;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;

    // Total list view variables
    List<Total> total_list = new ArrayList<>();
    ArrayAdapter<Total> totalAdapter;
    ListView totalListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_total);

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

        // Initialize the database handler
        dbHandler = new DatabaseHandler(getApplicationContext());

        // Initialize Tabs
        final TabHost tabHost = (TabHost) findViewById(R.id.tabHostSheets);
        tabHost.setup();

        // Totals Tab
        TabHost.TabSpec tabSpec = tabHost.newTabSpec("Totals");
        tabSpec.setContent(R.id.tabTotal);
        tabSpec.setIndicator("Totals");
        tabHost.addTab(tabSpec);

        // File Tab
        tabSpec = tabHost.newTabSpec("File");
        tabSpec.setContent(R.id.tabFile);
        tabSpec.setIndicator("File");
        tabHost.addTab(tabSpec);

        // Update Main totals
        updateMainTotals();

        // Initialize the total list
        populateTotalList();
        totalAdapter = new TotalListAdapter();

        // Connect the total list to the list view
        totalListView = (ListView) findViewById(R.id.totalListView);
        totalListView.setAdapter(totalAdapter);

        // Initialize the delete button
        Button buttonDeleteData = (Button) findViewById(R.id.buttonDeleteData);
        registerForContextMenu(buttonDeleteData);

    }

    private void updateMainTotals() {
        // Get the pot value
        TextView potValue = (TextView) findViewById(R.id.potValue);
        potValue.setText(Integer.toString(dbHandler.getPotTotal()));

        // Get the amount of chips among all players
        TextView totalChips = (TextView) findViewById(R.id.totalChips);
        totalChips.setText(Integer.toString(dbHandler.getAllWinnings()));

        // Get the amount of debt among all players
        TextView totalTaken = (TextView) findViewById(R.id.totalTaken);
        totalTaken.setText(Integer.toString(dbHandler.getAllDebt()));
    }

    private void populateTotalList() {
        // Search the data base for each contact's data
        List<Contact> contact_list = dbHandler.getAllContacts();

        // Create a total object for each contact
        for(int i = 0; i < contact_list.size(); ++i) {
            Contact current_contact = contact_list.get(i);

            // Get chip total
            int chip_count = dbHandler.getContactWinnings(current_contact.getId());

            // Get pot total
            int pot_count = dbHandler.getContactDebt(current_contact.getId());

            // Get side bet total
            int side_count = dbHandler.getContactSideBetTotal(current_contact.getId());

            // Get star total
            int star_count = dbHandler.getContactStars(current_contact.getId());

            // Get food total
            int food_count = dbHandler.getContactFoodTotal(current_contact.getId());

            total_list.add(new Total(current_contact.getName(), chip_count, pot_count, side_count, star_count, food_count));
        }
    }

    // Class that represents a players total data
    class Total {
        private String _player;

        private int _chips, _pot, _side, _stars, _food;

        public Total(String player, int chips, int pot, int side, int stars, int food) {
            _player = player;
            _chips = chips;
            _pot = pot;
            _side = side;
            _stars = stars;
            _food = food;
        }

        public void reset() {
            _chips = 0;
            _pot = 0;
            _side = 0;
            _stars = 0;
            _food = 0;
        }

        public String getPlayer() { return _player; }
        public int getChips() { return _chips; }
        public int getPot() { return _pot; }
        public int getSide() { return _side; }
        public int getStars() { return _stars; }
        public int getFood() { return _food; }
    }

    private class TotalListAdapter extends ArrayAdapter<Total> {
        public TotalListAdapter() {
            super(TotalActivity.this, R.layout.totallistview_item, total_list);
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            if (view == null)
                view = getLayoutInflater().inflate(R.layout.totallistview_item, parent, false);

            Total current_total = total_list.get(position);

            // Get the player name
            TextView player = (TextView) view.findViewById(R.id.playerName);
            player.setText(current_total.getPlayer());

            // Get the chips
            TextView chips = (TextView) view.findViewById(R.id.chips);
            chips.setText(Integer.toString(current_total.getChips()));

            // Get the pot
            TextView pot = (TextView) view.findViewById(R.id.pot);
            pot.setText(Integer.toString(current_total.getPot()));

            // Get the side bets
            TextView sidebets = (TextView) view.findViewById(R.id.side);
            sidebets.setText(Integer.toString(current_total.getSide()));

            // Get the stars
            TextView stars = (TextView) view.findViewById(R.id.stars);
            stars.setText(Integer.toString(current_total.getStars()));

            // Get the stars
            TextView food = (TextView) view.findViewById(R.id.food);
            food.setText(Integer.toString(current_total.getFood()));

            return view;
        }
    }

    // The delete pop up
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle("Are you sure?");

        menu.add(0, 0, 0, "No");
        menu.add(0, 1, 0, "Yes");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        switch(item.getItemId()) {
            case 1: // yes
                dbHandler.deleteDataBase();
                updateMainTotals();
                total_list.clear();
                totalAdapter.notifyDataSetChanged();
                // Display confimation
                Toast.makeText(getApplicationContext(), "All data has been deleted.", Toast.LENGTH_SHORT).show();
            break;
        }

        return true;
    }
}

package com.cse455.pokerlog.pokerlog;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends ActionBarActivity {

    // Constants
    private static final int  DELETE = 1;

    // Database variables
    DatabaseHandler dbHandler;

    // Creator input field variables
    EditText nameTxt, phoneTxt, emailTxt, addressTxt;

    // Contact list view variables
    List<Contact> contact_list = new ArrayList<Contact>();
    ListView contactListView;
    ArrayAdapter<Contact> contactAdapter;
    int longClickedItemIndex;

    // Navigation drawer variables
    private String[] mNavigationItems;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

        // Initialize database
        dbHandler = new DatabaseHandler(getApplicationContext());

        // Initialize input fields
        nameTxt = (EditText) findViewById(R.id.txtName);
        phoneTxt = (EditText) findViewById(R.id.txtPhone);
        emailTxt = (EditText) findViewById(R.id.txtEmail);
        addressTxt = (EditText) findViewById(R.id.txtAddress);

        // Initialize input button
        final Button addBtn = (Button) findViewById(R.id.btnAdd);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Contact contact = new Contact(dbHandler.getContactsCount(), String.valueOf(nameTxt.getText()), String.valueOf(phoneTxt.getText()), String.valueOf(emailTxt.getText()), String.valueOf(addressTxt.getText()));
                if (!contactExists(contact)) {
                    dbHandler.createContact(contact);
                    contact_list.add(contact);
                    contactAdapter.notifyDataSetChanged();
                    Toast.makeText(getApplicationContext(), String.valueOf(nameTxt.getText()) + " has been added to your contact_list!", Toast.LENGTH_SHORT).show();
                    ((EditText) findViewById(R.id.txtName)).setText("");
                    ((EditText) findViewById(R.id.txtPhone)).setText("");
                    ((EditText) findViewById(R.id.txtEmail)).setText("");
                    ((EditText) findViewById(R.id.txtAddress)).setText("");
                    return;
                }
                Toast.makeText(getApplicationContext(), String.valueOf(nameTxt.getText()) + " already exists. Please use a different name.", Toast.LENGTH_SHORT).show();
            }
        });

        // Initialize the contact list view
        contact_list.addAll(dbHandler.getAllContacts());
        contactAdapter = new ContactListAdapter();

        // Connect the contact list to the list view
        contactListView = (ListView) findViewById(R.id.listView);
        contactListView.setAdapter(contactAdapter);

        // Set up the ability to select
        registerForContextMenu(contactListView);

        contactListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                longClickedItemIndex = position;
                return false;
            }
        });

        // Initialize Tabs
        TabHost tabHost = (TabHost) findViewById(R.id.tabHost);

        tabHost.setup();

        // Registration Tab
        TabHost.TabSpec tabSpec = tabHost.newTabSpec("Player Register");
        tabSpec.setContent(R.id.tabCreator);
        tabSpec.setIndicator("Player Register");
        tabHost.addTab(tabSpec);

        // Player List Tab
        tabSpec = tabHost.newTabSpec("Roster");
        tabSpec.setContent(R.id.tabContactList);
        tabSpec.setIndicator("Roster");
        tabHost.addTab(tabSpec);
    }

    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, view, menuInfo);

        menu.setHeaderIcon(R.drawable.pencil_icon);
        menu.setHeaderTitle("Contact Options");
        menu.add(Menu.NONE, DELETE, menu.NONE, "Delete Contact");
    }

    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case DELETE:
                dbHandler.deleteContact(contact_list.get(longClickedItemIndex));
                contact_list.remove(longClickedItemIndex);
                contactAdapter.notifyDataSetChanged();
                break;
        }

        return super.onContextItemSelected(item);
    }

    private boolean contactExists(Contact contact) {
        String name = contact.getName();
        int contactCount = contact_list.size();

        for (int i = 0; i < contactCount; i++) {
            if (name.compareToIgnoreCase(contact_list.get(i).getName()) == 0)
                return true;
        }
        return false;
    }

    private class ContactListAdapter extends ArrayAdapter<Contact> {
        public ContactListAdapter() {
            super (MainActivity.this, R.layout.listview_item, contact_list);
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            if (view == null)
                view = getLayoutInflater().inflate(R.layout.listview_item, parent, false);

            Contact currentContact = contact_list.get(position);

            TextView name = (TextView) view.findViewById(R.id.contactName);
            name.setText(currentContact.getName());
            TextView phone = (TextView) view.findViewById(R.id.phoneNumber);
            phone.setText(currentContact.getPhone());
            TextView email = (TextView) view.findViewById(R.id.emailAddress);
            email.setText(currentContact.getEmail());
            TextView address = (TextView) view.findViewById(R.id.cAddress);
            address.setText(currentContact.getAddress());

            return view;
        }
    }

}

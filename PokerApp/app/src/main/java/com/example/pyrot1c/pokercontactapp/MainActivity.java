package com.example.pyrot1c.pokercontactapp;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

// The main Activity containing two tabs, one for contact entry and one for contact listing

public class MainActivity extends ActionBarActivity {
//Assigning text entry and lists
    EditText nameTxt, phoneTxt, emailTxt, addressTxt;
    List<Contact> Contacts = new ArrayList<Contact>();
    ListView contactListView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        nameTxt = (EditText) findViewById(R.id.txtName);
        phoneTxt = (EditText) findViewById(R.id.txtPhone);
        emailTxt = (EditText) findViewById(R.id.txtEmail);
        addressTxt = (EditText) findViewById(R.id.txtAddress);
        contactListView = (ListView) findViewById(R.id.listView);
        //Setting up tab host for Tabs
        TabHost tabHost = (TabHost) findViewById(R.id.tabHost);

        tabHost.setup();

        TabHost.TabSpec tabSpec = tabHost.newTabSpec("creator");
        tabSpec.setContent(R.id.tabCreator);
        tabSpec.setIndicator("Creator");
        tabHost.addTab(tabSpec);

        tabSpec = tabHost.newTabSpec("list");
        tabSpec.setContent(R.id.tabContactList);
        tabSpec.setIndicator("list");
        tabHost.addTab(tabSpec);


        //button functions
        final Button addBtn = (Button) findViewById(R.id.btnAdd);
        addBtn.setOnClickListener(new View.OnClickListener() {
                                     @Override
                                      public void onClick(View view) {
                                         //contact addition button only enabled when Name.text has entry, Display a toast saying nametxt has been added successfully
                                         addContact(nameTxt.getText().toString(),phoneTxt.getText().toString(),emailTxt.getText().toString(),addressTxt.getText().toString());
                                         Toast.makeText(getApplicationContext(),nameTxt.getText().toString() + " has been Added Successfully", Toast.LENGTH_SHORT).show();
                                      }
                                      });

                nameTxt.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    //Set button to enabled when nametext is fulfilled
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        addBtn.setEnabled(!nameTxt.getText().toString().trim().isEmpty());
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });

    }

    //Add contact
    private void addContact(String name, String phone, String email, String address) {
        Contacts.add(new Contact(name, phone, email, address));

    }

    //contact list adapter, checks contact class and takes names
    private class ContactListAdapter extends ArrayAdapter<Contact> {
        public ContactListAdapter() {
           super (MainActivity.this, R.layout.listview_item, Contacts);
        }

        @Override
        public View getView(int position, View view, ViewGroup parent){
            if (view == null)
                view = getLayoutInflater().inflate(R.layout.listview_item, parent, false);

            Contact currentContact = Contacts.get(position);

            TextView name = (TextView) view.findViewById(R.id.contactName);
            name.setText(currentContact.getName());
            TextView phone = (TextView) view.findViewById(R.id.phoneNumber);
            name.setText(currentContact.getPhone());
            TextView email = (TextView) view.findViewById(R.id.emailAddress);
            name.setText(currentContact.getEmail());
            TextView address = (TextView) view.findViewById(R.id.cAddress);
            name.setText(currentContact.getAddress());

            return view;

        }
    }
    }






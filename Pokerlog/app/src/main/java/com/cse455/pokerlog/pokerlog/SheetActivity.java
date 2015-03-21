package com.cse455.pokerlog.pokerlog;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class SheetActivity extends ActionBarActivity {

    // Database variables
    DatabaseHandler dbHandler;

    // Navigation drawer variables
    private String[] mNavigationItems;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;

    // Pot variables
    int pot_total = 0;
    int pot_current = 0;

    // Food list view variables
    List<Food> food_list = new ArrayList<Food>();
    ArrayAdapter<Food> foodAdapter;
    ListView foodListView;

    // Score list view variables
    List<Score> score_list = new ArrayList<Score>();
    ArrayAdapter<Score> scoreAdapter;
    ListView scoreListView;

    // Side bet list view variables
    List<SideBet> side_bet_list = new ArrayList<SideBet>();
    ArrayAdapter<SideBet> sideBetAdapter;
    ListView sideBetListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sheet);

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

        // Close keyboard when switching tabs
        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            public void onTabChanged(String tabId)
            {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(tabHost.getApplicationWindowToken(), 0);
            }
        });

        // Pot tab
        TabHost.TabSpec tabSpec = tabHost.newTabSpec("Pot");
        tabSpec.setContent(R.id.tabPot);
        tabSpec.setIndicator("Pot");
        tabHost.addTab(tabSpec);

        // Initialize the pot input field
        EditText pot_update_field = (EditText) findViewById(R.id.inputPotAmount);
        pot_update_field.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    // Do nothing if there is no input
                    if (v.getText().toString().length() == 0)
                        return false;

                    // Update the pot
                    dbHandler.updatePot(pot_total + Integer.parseInt(v.getText().toString()));
                    pot_total = dbHandler.getPotTotal();
                    pot_current = calcPotCurrent();
                    updatePotView();

                    v.setText(""); // Clear the field
                    return false; // Needed to be false so the keyboard closes
                }
                return false;
            }
        });

        // Initialize the Food text inputs
        final AutoCompleteTextView foodPlayerTextView = (AutoCompleteTextView) findViewById(R.id.inputFoodPlayer);
        final EditText foodCostTextView = (EditText) findViewById(R.id.inputFoodCost);

        // Initialize the name autofill list
        ArrayAdapter<String> autofill_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, dbHandler.getContactStringList());
        foodPlayerTextView.setAdapter(autofill_adapter);

        // Initialize the food list
        food_list.addAll(dbHandler.getAllFood());
        foodAdapter = new FoodListAdapter();

        // Connect the food list to the list view
        foodListView = (ListView) findViewById(R.id.foodListView);
        foodListView.setAdapter(foodAdapter);

        // Initialize the food button input
        final Button buttonAddfood = (Button) findViewById(R.id.buttonAddFood);
        buttonAddfood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get the input fields
                AutoCompleteTextView playerTextView = (AutoCompleteTextView) findViewById(R.id.inputFoodPlayer);
                EditText costTextView = (EditText) findViewById(R.id.inputFoodCost);

                String playerName = playerTextView.getText().toString();

                // Check if the amount is 0
                int food_cost = Integer.parseInt(costTextView.getText().toString());
                if (food_cost <= 0) {
                    Toast.makeText(getApplicationContext(), "Food cost must be more than zero!", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Check if the player exists
                int player_id = dbHandler.getContactIdFromName(playerName);
                if (player_id == -1) {
                    Toast.makeText(getApplicationContext(), "Player doesn't exist!", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Add the Food item
                Food food = new Food(dbHandler.getFoodCount() + 1, dbHandler.getContact(player_id), food_cost);
                dbHandler.createFood(food);
                food_list.add(food);
                foodAdapter.notifyDataSetChanged();
                pot_current = calcPotCurrent();
                updatePotView();

                Toast.makeText(getApplicationContext(), "Food item added!", Toast.LENGTH_SHORT).show();

                // Clear the input fields
                playerTextView.setText("");
                costTextView.setText("");
            }
        });
        // Enable the button only when the fields are full
        foodPlayerTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                buttonAddfood.setEnabled(String.valueOf(foodPlayerTextView.getText()).trim().length() > 0 && String.valueOf(foodCostTextView.getText()).trim().length() > 0);
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });
        foodCostTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                buttonAddfood.setEnabled(String.valueOf(foodPlayerTextView.getText()).trim().length() > 0 && String.valueOf(foodCostTextView.getText()).trim().length() > 0);
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        // Game tab
        tabSpec = tabHost.newTabSpec("Game");
        tabSpec.setContent(R.id.tabGame);
        tabSpec.setIndicator("Game");
        tabHost.addTab(tabSpec);

        // Initialize the score list
        score_list.addAll(dbHandler.getAllScores());
        scoreAdapter = new ScoreListAdapter();

        // Connect the score list to the list view
        scoreListView = (ListView) findViewById(R.id.scoreListView);
        scoreListView.setAdapter(scoreAdapter);

        // Side Bet tab
        tabSpec = tabHost.newTabSpec("Side Bets");
        tabSpec.setContent(R.id.tabSideBets);
        tabSpec.setIndicator("Side Bets");
        tabHost.addTab(tabSpec);

        // Initialize the side bet list
        side_bet_list.addAll(dbHandler.getAllSideBets());
        sideBetAdapter = new SideBetListAdapter();

        // Connect the side bet list to the list view
        scoreListView = (ListView) findViewById(R.id.sideBetListView);
        scoreListView.setAdapter(sideBetAdapter);

        // Initialize the Side Bet text inputs
        final AutoCompleteTextView winnerTextView = (AutoCompleteTextView) findViewById(R.id.inputWinner);
        final AutoCompleteTextView loserTextView = (AutoCompleteTextView) findViewById(R.id.inputLoser);
        final EditText amountTextView = (EditText) findViewById(R.id.inputSideBetAmount);

        // Initialize the name autofill list
        winnerTextView.setAdapter(autofill_adapter);
        loserTextView.setAdapter(autofill_adapter);

        // Initialize the Side Bet button input
        final Button buttonAddSideBet = (Button) findViewById(R.id.buttonAddSideBet);
        buttonAddSideBet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get the input fields
                AutoCompleteTextView winnerTextView = (AutoCompleteTextView) findViewById(R.id.inputWinner);
                AutoCompleteTextView loserTextView = (AutoCompleteTextView) findViewById(R.id.inputLoser);
                EditText amountTextView = (EditText) findViewById(R.id.inputSideBetAmount);

                String winnerName = winnerTextView.getText().toString();
                String loserName = loserTextView.getText().toString();

                // Check if the amount is 0
                int bet_amount = Integer.parseInt(amountTextView.getText().toString());
                if (bet_amount <= 0) {
                    Toast.makeText(getApplicationContext(), "Bet amount must be more than zero!", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Check if the winner exists
                int winner_id = dbHandler.getContactIdFromName(winnerName);
                if (winner_id == -1) {
                    Toast.makeText(getApplicationContext(), "Winner doesn't exist!", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Check if the loser exists
                int loser_id = dbHandler.getContactIdFromName(loserName);
                if (loser_id == -1) {
                    Toast.makeText(getApplicationContext(), "Loser doesn't exist!", Toast.LENGTH_SHORT).show();
                    return;
                }
                // Check if the winner and loser are identical
                if (winner_id == loser_id) {
                    Toast.makeText(getApplicationContext(), "Both winner and loser are the same!", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Add the Side Bet
                SideBet side_bet = new SideBet(dbHandler.getSideBetsCount() + 1, dbHandler.getContact(winner_id), dbHandler.getContact(loser_id), bet_amount);
                dbHandler.createSideBet(side_bet);
                side_bet_list.add(side_bet);
                sideBetAdapter.notifyDataSetChanged();
                scoreAdapter.notifyDataSetChanged();
                Toast.makeText(getApplicationContext(), "Side bet added!", Toast.LENGTH_SHORT).show();

                // Clear the input fields
                winnerTextView.setText("");
                loserTextView.setText("");
                amountTextView.setText("");
            }
        });

        // Enable the button only when the fields are full
        winnerTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                buttonAddSideBet.setEnabled(String.valueOf(winnerTextView.getText()).trim().length() > 0 && String.valueOf(loserTextView.getText()).trim().length() > 0 && String.valueOf(amountTextView.getText()).trim().length() > 0);
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });
        loserTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                buttonAddSideBet.setEnabled(String.valueOf(winnerTextView.getText()).trim().length() > 0 && String.valueOf(loserTextView.getText()).trim().length() > 0 && String.valueOf(amountTextView.getText()).trim().length() > 0);
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });
        amountTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                buttonAddSideBet.setEnabled(String.valueOf(winnerTextView.getText()).trim().length() > 0 && String.valueOf(loserTextView.getText()).trim().length() > 0 && String.valueOf(amountTextView.getText()).trim().length() > 0);
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        // Initialize the pot view
        pot_total = dbHandler.getPotTotal();
        pot_current = calcPotCurrent();
        updatePotView();

    }

    private class FoodListAdapter extends ArrayAdapter<Food> {
        public FoodListAdapter() {
            super(SheetActivity.this, R.layout.foodlistview_item, food_list);
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            if (view == null)
                view = getLayoutInflater().inflate(R.layout.foodlistview_item, parent, false);

            Food current_food_item = food_list.get(position);

            // Get the player name
            TextView player = (TextView) view.findViewById(R.id.playerName);
            player.setText(current_food_item.getPlayer().getName());

            // Get cost
            TextView cost = (TextView) view.findViewById(R.id.foodCost);
            cost.setText(Integer.toString(current_food_item.getCost()));

            // Set up the delete button
            Button button_delete = (Button) view.findViewById(R.id.buttonDeleteFood);
            button_delete.setTag(position); // Connect the button to this item
            button_delete.setOnClickListener(new View.OnClickListener() {
                     public void onClick(View v) {
                         dbHandler.deleteFood(food_list.get((int) v.getTag()));
                         food_list.remove((int) v.getTag());
                         foodAdapter.notifyDataSetChanged();
                         pot_current = calcPotCurrent();
                         updatePotView();
                     }
                 }
            );

            return view;
        }
    }

    private class ScoreListAdapter extends ArrayAdapter<Score> {
        public ScoreListAdapter() {
            super(SheetActivity.this, R.layout.scorelistview_item, score_list);
        }

        int calcChips(Score score_item) {
            int value = score_item.getDebt();
            value += score_item.getWinnings();

            for(int i = 0; i < side_bet_list.size(); ++i) {
                int current_winner = side_bet_list.get(i).getWinner().getId();
                int current_loser = side_bet_list.get(i).getLoser().getId();

                if (score_item.getPlayer().getId() == current_winner)
                    value += side_bet_list.get(i).getAmount();
                else if (score_item.getPlayer().getId() == current_loser)
                    value -= side_bet_list.get(i).getAmount();
            }

            return value;
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            if (view == null)
                view = getLayoutInflater().inflate(R.layout.scorelistview_item, parent, false);

            Score currentScore = score_list.get(position);

            // Get the player name
            TextView player = (TextView) view.findViewById(R.id.playerName);
            player.setText(currentScore.getPlayer().getName());

            // Get winnings
            TextView winnings = (TextView) view.findViewById(R.id.chipCount);
            winnings.setText(Integer.toString(currentScore.getWinnings()));

            // Get debt
            TextView debt = (TextView) view.findViewById(R.id.debtCount);
            debt.setText(Integer.toString(currentScore.getDebt()));

            // Set up the amount field
            EditText field_add = (EditText) view.findViewById(R.id.addAmount);
            field_add.setTag(position);
            field_add.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        // Do nothing if there is no input
                        if (v.getText().toString().length() == 0)
                            return false;

                        // Update the winnings
                        score_list.get((int) v.getTag()).setWinnings(Integer.parseInt(v.getText().toString()));
                        dbHandler.updateScore(score_list.get((int) v.getTag()));
                        scoreAdapter.notifyDataSetChanged();

                        v.setText(""); // Clear the field
                        return false; // Needed to be false so the keyboard closes
                    }
                    return false;
                }
            });

            // Set up the take button
            Button button_take = (Button) view.findViewById(R.id.buttonTake);
            button_take.setTag(position); // Connect the button to this item
            button_take.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        score_list.get((int) v.getTag()).takeFromPot();
                        dbHandler.updateScore(score_list.get((int) v.getTag()));
                        scoreAdapter.notifyDataSetChanged();
                        pot_current = calcPotCurrent();
                        updatePotView();
                    }
               }
            );

            // Set up the return button
            Button button_return = (Button) view.findViewById(R.id.buttonReturn);
            button_return.setTag(position); // Connect the button to this item
            button_return.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        score_list.get((int) v.getTag()).returnToPot();
                        dbHandler.updateScore(score_list.get((int) v.getTag()));
                        scoreAdapter.notifyDataSetChanged();
                        pot_current = calcPotCurrent();
                        updatePotView();
                    }
               }
            );

            return view;
        }
    }

    private class SideBetListAdapter extends ArrayAdapter<SideBet> {
        public SideBetListAdapter() {
            super(SheetActivity.this, R.layout.sidebetlistview_item, side_bet_list);
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            if (view == null)
                view = getLayoutInflater().inflate(R.layout.sidebetlistview_item, parent, false);

            SideBet current_side_bet = side_bet_list.get(position);

            // Get the winner name
            TextView winner = (TextView) view.findViewById(R.id.winnerName);
            winner.setText(current_side_bet.getWinner().getName());

            // Get the loser name
            TextView loser = (TextView) view.findViewById(R.id.loserName);
            loser.setText(current_side_bet.getLoser().getName());

            // Get amount
            TextView amount = (TextView) view.findViewById(R.id.sideBetAmount);
            amount.setText(Integer.toString(current_side_bet.getAmount()));

            // Set up the delete button
            Button button_delete = (Button) view.findViewById(R.id.buttonDeleteSideBet);
            button_delete.setTag(position); // Connect the button to this item
            button_delete.setOnClickListener(new View.OnClickListener() {
                   public void onClick(View v) {
                       dbHandler.deleteSideBet(side_bet_list.get((int) v.getTag()));
                       side_bet_list.remove((int) v.getTag());
                       scoreAdapter.notifyDataSetChanged();
                       sideBetAdapter.notifyDataSetChanged();
                   }
               }
            );

            return view;
        }
    }

    void updatePotView() {
        TextView potTotal = (TextView) findViewById(R.id.potTotal);
        potTotal.setText(String.valueOf(pot_total));

        TextView potCurrent = (TextView) findViewById(R.id.potCurrent);
        potCurrent.setText(String.valueOf(pot_current));
    }

    int calcPotCurrent() {
        int value = pot_total;

        // Subtract all debt
        for(int i = 0; i < score_list.size(); ++i)
            value -= score_list.get(i).getDebt();

        return value;
    }
}

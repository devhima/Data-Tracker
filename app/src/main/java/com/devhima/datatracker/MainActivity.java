
package com.devhima.datatracker;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.devhima.datatracker.databinding.ActivityMainBinding;
import android.net.TrafficStats;
import androidx.appcompat.app.AppCompatActivity;
import android.app.*;
import android.content.*;
import android.os.*;
import android.view.*;
import android.view.View.*;
import android.widget.*;
import java.text.*;
import java.util.*;
import androidx.core.net.TrafficStatsCompat;
import android.database.Cursor;


public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    
    
    private final String SHARED_NAME_STRING = "devhima.settings";
    private final String USER_NAME_STRING = "isRun";
    private final String USER_NAME_ITEM = "item";
    private DatabaseHelper db;
	private Spinner spinnerViewUsers;
    private TextView textViewUsage;
    private TextView txtViewUsername;
    private List<User> users;
	
    private void mkToast(String txt){
		Toast.makeText(this, txt, Toast.LENGTH_SHORT).show();
    }
    
    private void addUser(){
		// Set an EditText view to get user input 
		final EditText input = new EditText(this);
		new AlertDialog.Builder(this)
			.setTitle("Add user")
			.setMessage("Enter username:")
			.setView(input)
			.setPositiveButton("Add", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					String out = input.getText().toString();
					db.insertUser(out, 0);
                    refreshSpinner(0);
				}
			})
			.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					// Do nothing.
				}
			}).show();
	}
    
    private void saveSetting(boolean setting){
        boolean boolToSave = setting;
         // To save data to SP
        SharedPreferences.Editor editor = getSharedPreferences(SHARED_NAME_STRING, Activity.MODE_PRIVATE).edit();
        editor.putBoolean(USER_NAME_STRING, boolToSave);
        editor.apply();
    }
    
    private void saveSetItem(int item){
        SharedPreferences.Editor editor = getSharedPreferences(SHARED_NAME_STRING, Activity.MODE_PRIVATE).edit();
        editor.putInt(USER_NAME_ITEM, item);
        editor.apply();
    }
    
    private boolean getSetting(){
        // To load the data at a later time
        SharedPreferences prefs = getSharedPreferences(SHARED_NAME_STRING, Activity.MODE_PRIVATE);
        boolean loadedBool = prefs.getBoolean(USER_NAME_STRING,false);
        return loadedBool;
    }
    
    private int getSetItem(){
        // To load the data at a later time
        SharedPreferences prefs = getSharedPreferences(SHARED_NAME_STRING, Activity.MODE_PRIVATE);
        int loadedInt = prefs.getInt(USER_NAME_ITEM,99999999);
        return loadedInt;
    }
    
    private void loadUsersFromDB() {
        try{
            Cursor cursor = db.getAllUsers();
        if (cursor.moveToFirst()) {
            do {
                users.add(new User(
							  cursor.getInt(0),
							  cursor.getString(1),
							  cursor.getLong(2),
                                cursor.getLong(3),
                               cursor.getLong(4)
						  ));
            } while (cursor.moveToNext());
        }
        cursor.close();
        } catch (Exception ex){
            mkToast(ex.getMessage().toString());
            textViewUsage.setText(ex.getMessage().toString());
        }
        
    }
    
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try{
            
        super.onCreate(savedInstanceState);

        // Inflate and get instance of binding
        binding = ActivityMainBinding.inflate(getLayoutInflater());

        // set content view to binding's root
       // setContentView(binding.getRoot());
            setContentView(R.layout.activity_main);
            Button buttonRefresh = findViewById(R.id.buttonRefresh);
            if(getSetting()==false){
                buttonRefresh.setText("Start");
                //mkToast(String.valueOf(getSetting()));
            } else if (getSetting()==true){
                buttonRefresh.setText("Stop");
                //mkToast(String.valueOf(getSetting()));
            } else{
                saveSetting(false);
               // mkToast(String.valueOf(getSetting()));
            }
            
            if(getSetItem() == 99999999){
                saveSetItem(0);
            }
        
        //start
        
        db = new DatabaseHelper(this);
        textViewUsage = findViewById(R.id.textViewUsage);
		spinnerViewUsers = findViewById(R.id.spinnerViewUsers);
            txtViewUsername = findViewById(R.id.labelUsrName);
        
        //spinner 
		spinnerViewUsers.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
				@Override
				public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        
                       // String txtUsr = adapterView.getItemAtPosition(i).toString();
                        User xusr = users.get(adapterView.getSelectedItemPosition());
                        String dataUsage = DataUsage.formatSize(xusr.getDataUsage());
					    textViewUsage.setText(String.format("Current Data Usage: %s", dataUsage));
                        txtViewUsername.setText(xusr.getUsername());
                        
				}

				@Override
				public void onNothingSelected(AdapterView<?> adapterView) {

				}
			});
         
        // Sample data - You should load this from the database in a real app
        users = new ArrayList<>();
        refreshSpinner(getSetItem());
        
		
		
		
        buttonRefresh.setOnClickListener( new OnClickListener() {

				@Override
				public void onClick(View v) {
					// Here you would update data usage from the system,
					// for now, we'll just simulate it
					/*for (User user : users) {
                            mkToast(user.getUsername());
						user.setDataUsage(user.getDataUsage() * 2); // Simulate increase
						db.updateUserUsage(user.getUsername(), user.getDataUsage());
					}*/
                        User xusr = users.get(spinnerViewUsers.getSelectedItemPosition());
                        //mkToast(String.valueOf(getSetting()));
                        if(getSetting()==false){
                            buttonRefresh.setText("Stop");
                            saveSetting(true);
                            long du = DataUsage.getUsageStats();
                            xusr.setDataUsage(xusr.getDataUsage(),du,0);
                            db.updateUserUsage(xusr.getUsername(),xusr.getDataUsage(),du,0);
                            mkToast("Start tracking..");
                        } else if(getSetting()==true){
                            buttonRefresh.setText("Start");
                            saveSetting(false);
                            long after = DataUsage.getUsageStats();
                            long totalUsage;
                            totalUsage = xusr.getDataUsage() + (after - xusr.getBefore());
                            xusr.setDataUsage(totalUsage,xusr.getBefore(),after);
                            db.updateUserUsage(xusr.getUsername(),totalUsage,xusr.getBefore(),after);
                            mkToast("Stop tracking..");
                        }
                    saveSetItem(spinnerViewUsers.getSelectedItemPosition());
					refreshSpinner(getSetItem());
				}
            
        });
		
		Button btnAddUsr = findViewById(R.id.btnAddUsr);
        btnAddUsr.setOnClickListener( new OnClickListener() {

				@Override
				public void onClick(View v) {
					addUser();
				}

			});
            
        Button btnDelUsr = findViewById(R.id.btnDelUsr);
        btnDelUsr.setOnClickListener( new OnClickListener() {

				@Override
				public void onClick(View v) {
                        if(spinnerViewUsers.getCount() != 0){
					    db.deleteNote(String.valueOf(spinnerViewUsers.getSelectedItem().toString()));
                        refreshSpinner(0);
                        saveSetItem(0);
                    }
				}

			});
            
            Button btnRstAll = findViewById(R.id.btnRstAll);
            btnRstAll.setOnClickListener( new OnClickListener() {

				@Override
				public void onClick(View v) {
                        
                        if(spinnerViewUsers.getCount() != 0){
					    for (User user : users) {
						db.updateUserUsage(user.getUsername(),0,0,0);
					    }
                            saveSetItem(0);
                            refreshSpinner(0);
                    }
				}

			});
            
            Button btnRst = findViewById(R.id.btnRstItm);
            btnRst.setOnClickListener( new OnClickListener() {

				@Override
				public void onClick(View v) {
                        
                        if(spinnerViewUsers.getCount() != 0){
					    User xusr = users.get(spinnerViewUsers.getSelectedItemPosition());
						db.updateUserUsage(xusr.getUsername(),0,0,0);
                        refreshSpinner(spinnerViewUsers.getSelectedItemPosition());
                    }
				}

			});
            
            Button btnBackup = findViewById(R.id.btnBackup);
            btnBackup.setOnClickListener( new OnClickListener() {

				@Override
				public void onClick(View v) {
                        //db.backup("", this);
                       
				}

			});
            
            Button btnRestore = findViewById(R.id.btnRestore);
            btnRestore.setOnClickListener( new OnClickListener() {

				@Override
				public void onClick(View v) {
                        
                      //db.importDB("",this);
				}

			});
            
            Button btnAbt = findViewById(R.id.btnAbt);
            btnAbt.setOnClickListener( new OnClickListener() {

				@Override
				public void onClick(View v) {
                        
                       
				}

			});
            
            
            
       } catch (Exception ex){
           mkToast(ex.getMessage().toString());
            textViewUsage.setText("main: " + ex.getMessage().toString());
       }
        
    }
    
    private void refreshSpinner(int item){
        users.clear();
		loadUsersFromDB();
		
		//spinnerArray
		ArrayList<String> spinArry= new ArrayList<String>();
		for (User user : users) {
			spinArry.add(user.getUsername());
		}
		
		// Create ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, spinArry);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        spinnerViewUsers.setAdapter(adapter);
        spinnerViewUsers.setSelection(item,true);
	}
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.binding = null;
    }
}

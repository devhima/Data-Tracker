
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
import android.net.*;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    
    
    private final String SHARED_NAME_STRING = "devhima.settings";
    private final String USER_NAME_STRING = "isRun";
    private final String USER_NAME_ITEM = "item";
    private DatabaseHelper db;
	private Spinner spinnerViewUsers;
    private Button btnRst;
    private Button btnRstAll;
    private Button btnAddUsr;
    private Button btnDelUsr;
    private Button buttonRefresh;
    
    private TextView textViewUsage;
    private TextView txtViewUsername;
    private List<User> users;
    private Context xContext;
    private String downDir;
    private EditText edtPsize;
    private EditText edtPprice;
    
	
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
    
    
    private void showMessageDialog(String title, String msgText){
        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(msgText);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
            new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    }
    });
alertDialog.show();
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
    
    
    private void setRun(boolean state){
        if(state == false){
            buttonRefresh.setText("▶️ Start");
            btnAddUsr.setEnabled(true);
            btnDelUsr.setEnabled(true);
            btnRst.setEnabled(true);
            btnRstAll.setEnabled(true);
            spinnerViewUsers.setEnabled(true);
            
        }else if(state == true){
            buttonRefresh.setText("⏹ Stop");
            btnAddUsr.setEnabled(false);
            btnDelUsr.setEnabled(false);
            btnRst.setEnabled(false);
            btnRstAll.setEnabled(false);
            spinnerViewUsers.setEnabled(false);
            //textViewUsage.setText("Tracking data now...");
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
            
            //buttons
            buttonRefresh = findViewById(R.id.btnRefresh);
            btnAddUsr = findViewById(R.id.btnAddUsr);
            btnDelUsr = findViewById(R.id.btnDelUsr);
            btnRstAll = findViewById(R.id.btnRstAll);
            btnRst = findViewById(R.id.btnRstItm);
            spinnerViewUsers = findViewById(R.id.spinnerViewUsers);
            textViewUsage = findViewById(R.id.textViewUsage);
            
            if(getSetting()==false){
                setRun(false);
                //mkToast(String.valueOf(getSetting()));
            } else if (getSetting()==true){
                setRun(true);
                //mkToast(String.valueOf(getSetting()));
            } else{
                saveSetting(false);
               // mkToast(String.valueOf(getSetting()));
            }
            
            if(getSetItem() == 99999999){
                saveSetItem(0);
            }
        
        //start
        xContext = this;
        downDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
            //mkToast(downDir);
        db = new DatabaseHelper(this);
        
		
        txtViewUsername = findViewById(R.id.labelUsrName);
        edtPsize = findViewById(R.id.editPkgSize);
        edtPprice = findViewById(R.id.editPkgPrice);
            
        //spinner 
		spinnerViewUsers.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
				@Override
				public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        
                       // String txtUsr = adapterView.getItemAtPosition(i).toString();
                        User xusr = users.get(adapterView.getSelectedItemPosition());
                        String dataUsage = DataUsage.formatSize(xusr.getDataUsage());
					    textViewUsage.setText(String.format("Usage: %s", dataUsage));
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
                            setRun(true);
                            saveSetting(true);
                            long du = DataUsage.getUsageStats();
                            xusr.setDataUsage(xusr.getDataUsage(),du,0);
                            db.updateUserUsage(xusr.getUsername(),xusr.getDataUsage(),du,0);
                            mkToast("Start tracking..");
                        } else if(getSetting()==true){
                            setRun(false);
                            saveSetting(false);
                            long after = DataUsage.getUsageStats();
                            long totalUsage;
                            long current = after - xusr.getBefore();
                            totalUsage = xusr.getDataUsage() + current;
                            xusr.setDataUsage(totalUsage,xusr.getBefore(),after);
                            db.updateUserUsage(xusr.getUsername(),totalUsage,xusr.getBefore(),after);
                            mkToast("Used " + DataUsage.formatSize(current));
                        }
                    saveSetItem(spinnerViewUsers.getSelectedItemPosition());
					refreshSpinner(getSetItem());
				}
            
        });
		
		
        btnAddUsr.setOnClickListener( new OnClickListener() {

				@Override
				public void onClick(View v) {
					addUser();
				}

			});
            
        
        btnDelUsr.setOnClickListener( new OnClickListener() {

				@Override
				public void onClick(View v) {
                        
                        AlertDialog.Builder builder = new AlertDialog.Builder(xContext);
                        builder.setMessage("Are you sure?");
                        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener(){
                                @Override
                                public void onClick(DialogInterface dialog, int which){
                                    if(spinnerViewUsers.getCount() != 0){
                                        db.deleteNote(String.valueOf(spinnerViewUsers.getSelectedItem().toString()));
                                        refreshSpinner(0);
                                        saveSetItem(0);
                                    }
                                    dialog.dismiss();
                                    }
                                });
                        builder.setNegativeButton("No", new DialogInterface.OnClickListener(){   
                                @Override
                                public void onClick(DialogInterface dialog, int which){
                                    dialog.dismiss();
                                    }
                                });         
                        builder.show();
                        
                        
				}

			});
            
            btnRstAll.setOnClickListener( new OnClickListener() {

				@Override
				public void onClick(View v) {
                        
                        AlertDialog.Builder builder = new AlertDialog.Builder(xContext);
                        builder.setMessage("Are you sure?");
                        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener(){
                                @Override
                                public void onClick(DialogInterface dialog, int which){
                                    
                                    if(spinnerViewUsers.getCount() != 0){
                                        for (User user : users) {
                                        db.updateUserUsage(user.getUsername(),0,0,0);
                                        }
                                        saveSetItem(0);
                                        refreshSpinner(0);
                                    }
                                    
                                    dialog.dismiss();
                                    }
                                });
                        builder.setNegativeButton("No", new DialogInterface.OnClickListener(){   
                                @Override
                                public void onClick(DialogInterface dialog, int which){
                                    dialog.dismiss();
                                    }
                                });         
                        builder.show();
                        
                        
				}

			});
            
            
            btnRst.setOnClickListener( new OnClickListener() {

				@Override
				public void onClick(View v) {
                        
                        AlertDialog.Builder builder = new AlertDialog.Builder(xContext);
                        builder.setMessage("Are you sure?");
                        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener(){
                                @Override
                                public void onClick(DialogInterface dialog, int which){
                                    if(spinnerViewUsers.getCount() != 0){
                                        User xusr = users.get(spinnerViewUsers.getSelectedItemPosition());
                                        db.updateUserUsage(xusr.getUsername(),0,0,0);
                                        refreshSpinner(spinnerViewUsers.getSelectedItemPosition());
                                        }
                                    dialog.dismiss();
                                    }
                                });
                        builder.setNegativeButton("No", new DialogInterface.OnClickListener(){   
                                @Override
                                public void onClick(DialogInterface dialog, int which){
                                    dialog.dismiss();
                                    }
                                });         
                        builder.show();
                        
                        
				}

			});
            
            Button btnBackup = findViewById(R.id.btnBackup);
            btnBackup.setOnClickListener( new OnClickListener() {

				@Override
				public void onClick(View v) {
                        
                        AlertDialog.Builder builder = new AlertDialog.Builder(xContext);
                        builder.setMessage("Are you sure?");
                        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener(){
                                @Override
                                public void onClick(DialogInterface dialog, int which){
                                    db.backup(downDir + "/datausage.db", xContext);
                                    dialog.dismiss();
                                    }
                                });
                        builder.setNegativeButton("No", new DialogInterface.OnClickListener(){   
                                @Override
                                public void onClick(DialogInterface dialog, int which){
                                    dialog.dismiss();
                                    }
                                });         
                        builder.show();
                        
                        
				}

			});
            
            Button btnRestore = findViewById(R.id.btnRestore);
            btnRestore.setOnClickListener( new OnClickListener() {

				@Override
				public void onClick(View v) {
                        
                        
                        AlertDialog.Builder builder = new AlertDialog.Builder(xContext);
                        builder.setMessage("Are you sure?");
                        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener(){
                                @Override
                                public void onClick(DialogInterface dialog, int which){
                                     db.importDB(downDir + "/datausage.db",xContext);
                                     refreshSpinner(0);
                                    dialog.dismiss();
                                    }
                                });
                        builder.setNegativeButton("No", new DialogInterface.OnClickListener(){   
                                @Override
                                public void onClick(DialogInterface dialog, int which){
                                    dialog.dismiss();
                                    }
                                });         
                        builder.show();
                        
                     
				}

			});
            
            Button btnAbt = findViewById(R.id.btnAbt);
            btnAbt.setOnClickListener( new OnClickListener() {

				@Override
				public void onClick(View v) {
                       Intent browse = new Intent(Intent.ACTION_VIEW, Uri.parse("https://dev-hima.blogspot.com/"));
		   		    startActivity(browse);
				}

			});
            
            Button btnDailyRpt = findViewById(R.id.btnDailyReport);
            btnDailyRpt.setOnClickListener( new OnClickListener() {

				@Override
				public void onClick(View v) {
                        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
                        Date date = new Date();
                        String report = "Report(";
                        report += dateFormat.format(date).toString() + ")";
                        long ttlds = 0;
                        int i = 0;
                        for (User user : users) {
                            ttlds += user.getDataUsage();
                            i += 1;
                            report += "\n (" + String.valueOf(i) + ") " + user.getUsername() + ":  " + DataUsage.formatSize(user.getDataUsage());
                        }
                        report += "\n\n • Total data usage:  " + DataUsage.formatSize(ttlds);
                        showMessageDialog("Total Package Report", report);
				}

			});
            
            Button btnMonyhRpt = findViewById(R.id.btnMonthReport);
            btnMonyhRpt.setOnClickListener( new OnClickListener() {

				@Override
				public void onClick(View v) {
                    try{
                        String p = edtPprice.getText().toString();
                        String s = edtPsize.getText().toString();
                        int price = Integer.parseInt(p);
                        int size = Integer.parseInt(s);
                            
                            double ttlcs = 0;
                           
                            double unitSize = price / size;
                            
                            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
                            Date date = new Date();
                            String report = "Report(";
                            report += dateFormat.format(date).toString() + ")";
                            int i = 0;
                            
                            for (User user : users) {
                                
                                double cds = DataUsage.byteToGB(user.getDataUsage());
                                ttlcs += (cds * unitSize);
                                i += 1;
                                report += "\n (" + String.valueOf(i) + ") " + user.getUsername() + ":  " + (unitSize * cds);
                            }
                            report += "\n\n • Total cost usage:  " + ttlcs;
                            showMessageDialog("Total Bill Report", report);
                        
                    }catch(Exception ex){
                        mkToast(ex.getMessage().toString());
                    }
                        
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

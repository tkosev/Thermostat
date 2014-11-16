package tue.thermostat;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;


public class DaySetting extends Activity{
	
	private TextView lblTitle;
	private Button btnAdd, btnDelete, btnApply;
	private RadioButton selectedButton;
	private TimePicker timePicker;
	
	private Boolean addMode;
	private String dayName;
	private int currentDay;
	private int oldHour, oldMinute;
	
	private String setMode;
	private int intHours, intMinutes;
	
	private RadioButton radSun, radMoon;
	private RadioGroup radioGroup;
	
	GlobalApp global;
	Setting deleteSetting;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addeditscreen);
        
        // App
        global = (GlobalApp)getApplicationContext();
        
        // Elements
        lblTitle = (TextView) findViewById(R.id.lblTitle);
        btnAdd = (Button) findViewById(R.id.btnAddSetting);
        btnApply = (Button) findViewById(R.id.btnApplySetting);
        btnDelete = (Button) findViewById(R.id.btnDeleteSetting);
        timePicker = (TimePicker) findViewById(R.id.timePicker1);
        radioGroup = (RadioGroup) findViewById(R.id.radioModeSelect);
        radSun = (RadioButton) findViewById(R.id.radSun);
        radMoon = (RadioButton) findViewById(R.id.radMoon);
       
        // Check for add mode or edit mode
        Bundle b = getIntent().getExtras();
        addMode = b.getBoolean("addMode");
        dayName = b.getString("selectedDayName");
        currentDay = b.getInt("selectedDay");
        
        if(addMode) {
        	lblTitle.setText("Add program setting");
        	btnApply.setVisibility(View.INVISIBLE);
        	btnDelete.setVisibility(View.INVISIBLE);
        	btnApply.setEnabled(false);
        	btnDelete.setEnabled(false);
        	radMoon.setChecked(true);
        } else {
        	lblTitle.setText("Edit program setting");
        	btnAdd.setVisibility(View.INVISIBLE);
        	btnAdd.setEnabled(false);
        	oldHour = b.getInt("hour");
        	oldMinute = b.getInt("minute");
        	timePicker.setCurrentHour(oldHour);
        	timePicker.setCurrentMinute(oldMinute);
        	if(b.getString("mode").equals("Night")) {
        		radMoon.setChecked(true);
        	} else {
        		radSun.setChecked(true);
        	}
        }
        
        // Set timer
        timePicker.setIs24HourView(true);
    }
    
    public void addTheSetting(View view) {
    	// Get time
    	intHours = timePicker.getCurrentHour();
    	intMinutes = timePicker.getCurrentMinute();
    	
    	// Get mode
    	int selected = radioGroup.getCheckedRadioButtonId();
    	selectedButton = (RadioButton) findViewById(selected);
    	setMode = (String) selectedButton.getText();
    	    	
    	// First check if the setting can be added
    	if(setMode.equals("Night")) {
    		if(global.checkPossible(currentDay, 0, intHours, intMinutes, null)) {
    			// Setting can be added
    			global.addSetting(global.returnList(currentDay), "Night", intHours, intMinutes, currentDay);
    			returnOverviewAdd();
    		}
    	} else {
    		// Dag mode
    		if(global.checkPossible(currentDay, 1, intHours, intMinutes, null)) {
    			// Setting can be added
    			global.addSetting(global.returnList(currentDay), "Day", intHours, intMinutes, currentDay);
    			returnOverviewAdd();
    		}
    	}
    }
    
    public void applyTheSetting(View view) {
    	// Get time
    	intHours = timePicker.getCurrentHour();
    	intMinutes = timePicker.getCurrentMinute();
    	
    	// Get mode
    	int selected = radioGroup.getCheckedRadioButtonId();
    	selectedButton = (RadioButton) findViewById(selected);
    	setMode = (String) selectedButton.getText();
    	
    	// First check if the setting can be added
    	Setting settingToDelete = global.getSetting(global.returnList(currentDay), oldHour, oldMinute);
    	if(setMode.equals("Night")) {
    		if(global.checkPossible(currentDay, 0, intHours, intMinutes, settingToDelete)) {
    			// Delete "current" setting
    			global.removeSetting(global.returnList(currentDay), settingToDelete, currentDay);
    			// Setting can be added
    			global.addSetting(global.returnList(currentDay), "Night", intHours, intMinutes, currentDay);
    			returnOverviewAdd();
    			Toast.makeText(this, "Setting successfully changed.", Toast.LENGTH_LONG).show();
    		}
    	} else {
    		// Dag mode
    		if(global.checkPossible(currentDay, 1, intHours, intMinutes, settingToDelete)) {
    			// Delete "current" setting	
    			global.removeSetting(global.returnList(currentDay), settingToDelete, currentDay);
    			// Setting can be added
    			global.addSetting(global.returnList(currentDay), "Day", intHours, intMinutes, currentDay);
    			returnOverviewAdd();
    			Toast.makeText(this, "Setting successfully changed.", Toast.LENGTH_LONG).show();
    		}
    	}
    }
    
    public void deleteTheSetting(View view) {
    	// Delete selected setting 
    	deleteSetting = global.getSetting(global.returnList(currentDay), oldHour, oldMinute);
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	builder.setMessage("Are you sure that you want to delete this program setting?")
    	       .setCancelable(false)
    	       .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
    	           public void onClick(DialogInterface dialog, int id) {
    	        	   // Delete the setting
    	        	   deleteNow(global.returnList(currentDay), deleteSetting, currentDay);
    	                //global.removeSetting(global.returnList(currentDay), deleteSetting, currentDay);
    	                returnOverviewAdd();
    	    			
    	           }
    	       })
    	       .setNegativeButton("No", new DialogInterface.OnClickListener() {
    	           public void onClick(DialogInterface dialog, int id) {
    	                dialog.cancel();
    	           }
    	       });
    	//AlertDialog alert = builder.create();
    	builder.show();
    }

    private void deleteNow(ArrayList<Setting> list, Setting s, int day) {
    	global.removeSetting(list, s, day);
    	Toast.makeText(this, "Setting successfully deleted.", Toast.LENGTH_LONG).show();
    }
    
	private void returnOverviewAdd() {
	    	Intent returnIntent = new Intent();
	        setResult(RESULT_OK,returnIntent);        
	        finish();
    }

}
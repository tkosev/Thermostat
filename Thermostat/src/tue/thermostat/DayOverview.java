package tue.thermostat;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


public class DayOverview extends ListActivity{
	
	ArrayAdapter<String> adapter;
	
	private String dayName;
	private int currentDay;
	
	private TextView lblTitle;
	ArrayList<String> settingTimes = new ArrayList<String>();
		
	GlobalApp global;
	
	private ListView lv;
	
	String selectedText;
	
	private static final int SETTINGS_ADD_RES = 0;
	private static final int SETTINGS_EDIT_RES = 1;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.daysettings);
        
        // App
        global = (GlobalApp)getApplicationContext();
        
        // Adapter
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, settingTimes);
        setListAdapter(adapter);
        
        // Elements
        lblTitle = (TextView) findViewById(R.id.lblTempState);
        
        // Listener
        lv = getListView(); 
        lv.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
					
					String splitmode[];
					String splittijd[];
					String tMode, tHour, tMinute;
					
					// Go to edit mode with setting
					selectedText = (String) lv.getItemAtPosition(position);
					selectedText = selectedText.replaceAll("\\s",""); //Remove whitespace
					
					splitmode = selectedText.split("-");
					tMode = splitmode[1];
					splittijd = splitmode[0].split(":");
					tHour = splittijd[0];
					tMinute = splittijd[1];
					
					Bundle bundleTemp = new Bundle();
					bundleTemp.putInt("selectedDay", currentDay);
					bundleTemp.putString("selectedDayName", dayName);
					bundleTemp.putBoolean("addMode", false);
					bundleTemp.putInt("hour", Integer.parseInt(tHour));
					bundleTemp.putInt("minute", Integer.parseInt(tMinute));
					bundleTemp.putString("mode", tMode);
					Intent iTemps = new Intent(DayOverview.this, DaySetting.class).putExtras(bundleTemp);
					startActivityForResult(iTemps, SETTINGS_EDIT_RES);
			        
					
					/*Toast.makeText(getApplicationContext(),
						"Click ListItem Number " + position, Toast.LENGTH_LONG)
						.show(); */
			}
        });					
        
        // First, get the day (0 = monday etc)
        Bundle b = getIntent().getExtras();
        dayName = b.getCharSequence("selectedDay").toString();
        currentDay = b.getInt("dayInt");
        lblTitle.setText(dayName + " program");
        
        fillList();
        
    }


	private void fillList() {
	    // TODO Auto-generated method stub
		switch(currentDay) {
		case 0: 
			fillTimes(global.mondaySettings);		
			break;
		case 1:
			fillTimes(global.tuesdaySettings);		
			break;
		case 2:
			fillTimes(global.wednesdaySettings);	
			break;
		case 3:
			fillTimes(global.thursdaySettings);	
			break;
		case 4:
			fillTimes(global.fridaySettings);	
			break;
		case 5:
			fillTimes(global.saturdaySettings);	
			break;
		case 6:
			fillTimes(global.sundaySettings);	
			break;
		}
		fillArrayToList();
    }


	private void fillTimes(ArrayList<Setting> list) {
		for (Setting s : list) { 
			settingTimes.add(s.toString());
		}	    
    }        
	
	private void fillArrayToList(){
		adapter.notifyDataSetChanged();
	}
	
	private void clearListView() {
		settingTimes.clear();
		adapter.notifyDataSetChanged();
	}
	
	public void leftDay(View view) {
		changeDay(-1);
    }
	
	public void rightDay(View view) {
		changeDay(1);
	}
	
	private void changeDay(int change) {
		if (change>0) {
			if (currentDay < 6) {
				currentDay++;
			} else {
				currentDay = 0;
			}
		} else {
			if (currentDay > 0) {
				currentDay--;
			} else {
				currentDay = 6;
			}
		}
		
		// change label and list
		dayName = global.getDayLabel(currentDay);
		lblTitle.setText(dayName + " program");
		clearListView();
		fillList();
	}
	
	// Open the add window
	public void addSetting(View view) {
		Bundle bundleTemp = new Bundle();
		bundleTemp.putInt("selectedDay", currentDay);
		bundleTemp.putString("selectedDayName", dayName);
		bundleTemp.putBoolean("addMode", true);
		Intent iTemps = new Intent(DayOverview.this, DaySetting.class).putExtras(bundleTemp);
		startActivityForResult(iTemps, SETTINGS_ADD_RES);
	}
	
	public void copySettings(View view) {
		List<String> weekDays = new ArrayList<String>();
		weekDays.add("Monday");
		weekDays.add("Tuesday");
		weekDays.add("Wednesday");
		weekDays.add("Thursday");
		weekDays.add("Friday");
		weekDays.add("Saturday");
		weekDays.add("Sunday");
		weekDays.remove(dayName);
		final CharSequence[] items = weekDays.toArray(new CharSequence[weekDays.size()]);
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	builder.setTitle("Select a copy destination, for the " + dayName + " program.");
    	builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                 dialog.cancel();
            }
        });
    	builder.setItems(items, new DialogInterface.OnClickListener() {
    	    public void onClick(DialogInterface dialog, int item) {   	    	
    	    	// Copy the settings from the selected day to the other selected day
    	    	areYouSure(dayName, items[item].toString());
    	    }
    	});
    	//AlertDialog alert = builder.create();
    	builder.show();
	}
	
	private void areYouSure(final String fromDay, final String toDay) {

    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	builder.setMessage("Copying will delete the original program for the destinated day. Are you sure?")
    	       .setCancelable(false)
    	       .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
    	           public void onClick(DialogInterface dialog, int id) {
    	        	   // Delete the setting
    	       			copySettingsFromTo(fromDay, toDay);
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
	
	private void copySettingsFromTo(String fromDay, String toDay) {
        // TODO Auto-generated method stub
		ArrayList<Setting> lijst = new ArrayList<Setting>();
		lijst = global.returnList(currentDay);
		
		int dayNum = global.getDayNum(toDay);
		ArrayList<Setting> destLijst = new ArrayList<Setting>();
		destLijst = global.returnList(dayNum);
		
		//for (Setting s : destLijst) {
			//global.removeSetting(destLijst, s, dayNum);
		//}
		destLijst.clear();
		global.settingCount[dayNum][0] = 0;
		global.settingCount[dayNum][1] = 0;
		
 		for (Setting s : lijst) {
			global.addSetting(destLijst, s.getMode(), s.getHours(), s.getMinutes(), dayNum);
		}
        Toast.makeText(this, "Program from " + fromDay + " has been copied to " + toDay, Toast.LENGTH_SHORT).show();
    }
	
	// RESULTS
	 protected void onActivityResult(int requestCode, int resultCode, Intent data)
	    {
	    	switch(requestCode) {
	    		case SETTINGS_ADD_RES: 
	          		if (resultCode == RESULT_OK) {
	          			// Refresh the list
	          			clearListView();
	          			fillList();
	          		}
	          		break;
	    		case SETTINGS_EDIT_RES: 
	          		if (resultCode == RESULT_OK) {
	          			// Refresh the list
	          			clearListView();
	          			fillList();
	          		}
	          		break;
	    	}
	    }  
	
}
    
  
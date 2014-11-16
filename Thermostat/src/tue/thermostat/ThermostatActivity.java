package tue.thermostat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Toast;
import android.os.Handler; 

public class ThermostatActivity extends Activity implements OnSeekBarChangeListener{
	
	// Seekbar and variables
	private SeekBar prgTemperature;
	private int minTemp = 5;
	private int maxTemp = 30;
	private Double currentTemp = 20.5;
	private double targetTemp = 20.5;
	private boolean touchStart = false;
	private boolean holidayMode = false;
	private double holidayTemp = 16.5;
	private boolean heatingOn = false;
	private boolean nightMode = true; // use nightMode = true for normal 
	private double dayTemp = 19;
	private double nightTemp = 16;
	
	private int theDay;
	private String dayName;
	
	GlobalApp global;
	
	// Time settings
	Format dateFormatter = new SimpleDateFormat("HH:mm:ss");
	private Date currentDate; 
	private Timestamp startTime; 
	Calendar calendar;
    Calendar calNu;
	
	// Timer object
	private RefreshHandler mRedrawHandler = new RefreshHandler();  
	
	class RefreshHandler extends Handler {  
	    @Override  
	    public void handleMessage(Message msg) {  
	    	ThermostatActivity.this.tick();  
	    }  
	  
	    public void sleep(long delayMillis) {  
	      this.removeMessages(0);  
	      sendMessageDelayed(obtainMessage(0), delayMillis);  
	    }  
	  };
	
	// Text views
	private TextView lblTemperature, lblTempLabel, lblTargetTemp, lblNextSwitch, lblCurrentTime, lblCurrentDay;
	
	// Images
	private ImageView imgHeating, imgStatus;
	private ImageButton imgMin, imgPlus;
	
	// Button
	private Button btnProgram;
	
	// Settings identifiers
	private static final int HOLIDAY_RESULT = 0;
	private static final int TEMPERATURE_RESULT = 1;
	private static final int SETTINGS_RESULT = 2;
	
	private Setting newMode;
	private Setting lastMode;
		
	DecimalFormat df = new DecimalFormat("###.#");
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        // App
        global = (GlobalApp)getApplicationContext();
        
        // RESET 
        globalReset();
        
        // Reset date, variables and stuff
        calNu = Calendar.getInstance();
        global.dayList.clear();
    	currentDate = new Date();
    	startTime = new Timestamp(currentDate.getTime());
        lastMode = null;
        newMode = null;              
        
        setCurrentDay(calNu);
               
        // Progress bar
        prgTemperature = (SeekBar) findViewById(R.id.prgTemperature);
        prgTemperature.setOnSeekBarChangeListener(this);
        
        // Initialize the bar
        initProgressBar();
        
        // Button
        btnProgram = (Button) findViewById(R.id.btnProgram);
        
        // Temperature label
        lblTemperature = (TextView) findViewById(R.id.lblTemperature);
        lblTempLabel = (TextView) findViewById(R.id.lblCurrentTemp);
        lblTargetTemp = (TextView) findViewById(R.id.lblTargetTemp);
        lblNextSwitch = (TextView) findViewById(R.id.lblNextSwitch);
        lblCurrentTime = (TextView) findViewById(R.id.lblCurrentTime);
        lblCurrentDay = (TextView) findViewById(R.id.lblCurrentDay);
        
        // Heating image and status image
        imgStatus = (ImageView) findViewById(R.id.imgStatus);
        imgHeating = (ImageView) findViewById(R.id.imgBurn);
        
        // Min and plus button
        imgMin = (ImageButton) findViewById(R.id.imgMin);
        imgPlus = (ImageButton) findViewById(R.id.imgPlus);
        
        // Set them to invisible
        imgHeating.setVisibility(View.INVISIBLE);
        
        // Set temperatures
        setTemperatureToMode();
               
        // Activate timer
        tick();
        
    }

    private void globalReset() {
    	/*
    	 global.mondaySettings = new ArrayList<Setting>();
    	 global.tuesdaySettings = new ArrayList<Setting>();
    	 global.wednesdaySettings = new ArrayList<Setting>();
    	 global.thursdaySettings = new ArrayList<Setting>();
    	 global.fridaySettings = new ArrayList<Setting>();
    	 global.saturdaySettings = new ArrayList<Setting>();
    	 global.sundaySettings = new ArrayList<Setting>();
         
    	 global.settingCount = new int[7][2]; */
    	 global.dayList = new ArrayList<Setting>();    
    	 
    	 dayName = "";
    	 theDay = 0;
    }

	@Override
    protected void onDestroy() {
        super.onDestroy();
        
        // Set globals to null
        lastMode = null;
        global.dayList.clear();
        theDay = 0;
        dayName = null;
        
        newMode = null;
    }

	public void tick() {
        mRedrawHandler.sleep(1000);    
        
        // Create new date and show it
        calendar = Calendar.getInstance();
    	Date nieuweDatum = new Date();
    	Timestamp nuTijd = new Timestamp(nieuweDatum.getTime());    	
    	long difference = nuTijd.getTime() - startTime.getTime(); // Difference in time from now to start
    	
    	calendar.setTimeInMillis(startTime.getTime() + difference * 300);
    	
    	lblCurrentTime.setText("" + dateFormatter.format(calendar.getTime()));
    	
    	int tHour, tMinute;
    	String label;
    	String[] tijd;
    	label = (String) lblCurrentTime.getText();
    	tijd = label.split(":");
    	tHour = Integer.parseInt(tijd[0]);
    	tMinute = Integer.parseInt(tijd[1]);
    	
    	// Set the day
    	int day = theDay;
    	setCurrentDay(calendar);
    	if(theDay!=day) {
    		// Day change -> switch to night mode
    		if(!(holidayMode)) {
        		activateNightMode();
    		}
    		//global.generateDayList(global.theDay, tHour, tMinute);
    	}
    	lblCurrentDay.setText(dayName);
    	setDate();
    	day = theDay;
    	
    	// Check for program switch
    	if(!(holidayMode)) {
    		checkProgram(tHour, tMinute, day);
    	}
    	
    	// Check for heating
    	checkHeating();
    	
    }

	private void setDate() {
	    // TODO Auto-generated method stub
	    if(dayName.equals("Monday")) {
	    	theDay = 0;
	    } else if(dayName.equals("Tuesday")) {
	    	theDay = 1;
	    } else if(dayName.equals("Wednesday")) {
	    	theDay = 2;
	    } else if(dayName.equals("Thursday")) {
	    	theDay = 3;
	    } else if(dayName.equals("Friday")) {
	    	theDay = 4;
	    } else if(dayName.equals("Saturday")) {
	    	theDay = 5;
	    } else if(dayName.equals("Sunday")) {
	    	theDay = 6;
	    }
    }

	private void checkProgram(int hour, int minute, int day) {
        /*if(startRound) {
        	global.generateDayList(global.theDay, hour, minute);
        	newMode = global.returnModeSetting(hour, minute);
        	lastMode = newMode;
        	if(!(newMode==null)) {
            	if(newMode.getMode().equals("Night")) {
    				activateNightMode();
    			} else {
    				activateDayMode();
    			}
        	}

        	startRound = false;
        } else { */
		// Maak nieuwe lijst
		global.generateDayList(day, hour, minute);
		
	    // Check for the activated setting (null -> night)
		newMode = global.returnModeSetting(hour, minute);
				
		if(!(newMode==null)) {
			// De nieuwe mode bestaat wel eerst vergelijken oftie niet aan is, dus activeer hem
			if(!(lastMode==null)) {
				// Er is een vorige bekende mode, vergelijk ze nu
				if(!(lastMode==newMode)){
					// De nieuwe mode is niet dezelfde als de vorige dus activeer hem
					if(newMode.getMode().equals("Night")) {
						activateNightMode();
					} else {
						activateDayMode();
					}
					lastMode = newMode;
				}
			} else {
				// Er was nog geen vorige, dus activeer nieuwe
				if(newMode.getMode().equals("Night")) {
					activateNightMode();
				} else {
					activateDayMode();
				}
				lastMode = newMode;
			}
		}
        
    }

	private void activateNightMode() {
	    nightMode = true;
	    setTemperatureToMode();
    }
	
	private void activateDayMode() {
		nightMode = false;
		setTemperatureToMode();
	}

	private void setCurrentDay(Calendar cal) {
		switch (cal.get(Calendar.DAY_OF_WEEK)) {
		case Calendar.SUNDAY: 
			theDay = 6;
			dayName = "Sunday";
			break;
		case Calendar.MONDAY: 
			theDay = 0;
			dayName = "Monday";
			break;
		case Calendar.TUESDAY: 
			theDay = 1;
			dayName = "Tuesday";
			break;
		case Calendar.WEDNESDAY: 
			theDay = 2;
			dayName = "Wednesday";
			break;
		case Calendar.THURSDAY: 
			theDay = 3;
			dayName = "Thursday";
			break;
		case Calendar.FRIDAY: 
			theDay = 4;
			dayName = "Friday";
			break;
		case Calendar.SATURDAY: 
			theDay = 5;
			dayName = "Saturday";
			break; 	
	}  
	    
    }

	@Override
    public void onProgressChanged(SeekBar seekBar, int progress,
    		boolean fromUser) {
    	// TODO Auto-generated method stub
    	if (touchStart) {
        	double mProg = minTemp + progress*0.1;   
        	try {
	            mProg = df.parse(df.format(mProg)).doubleValue();
            } catch (ParseException e) {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
            }
        	lblTargetTemp.setText("" + mProg);
        	targetTemp = mProg;
    	}
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    	// TODO Auto-generated method stub
    	touchStart = true;
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
    	// TODO Auto-generated method stub
    	seekBar.setSecondaryProgress(seekBar.getProgress());    
    	touchStart = false;
    	checkHeating();
    	lblNextSwitch.setText("Temporary mode");
    }
    
    // Increase temperature with 0.1 (until 30)
    public void incTemp(View view) {
    	if (targetTemp < maxTemp) {
        	targetTemp = targetTemp + 0.1;
        	try {
        		targetTemp = df.parse(df.format(targetTemp)).doubleValue();
            } catch (ParseException e) {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
            }
        	lblTargetTemp.setText("" + targetTemp);
        	initProgressBar();
            checkHeating();
            lblNextSwitch.setText("Temporary mode");
    	}
    }
    
    // Decrease temperature with 0.1 (until 5)
    public void decTemp(View view) {
    	if (targetTemp > minTemp) {
        	targetTemp = targetTemp - 0.1;
        	try {
        		targetTemp = df.parse(df.format(targetTemp)).doubleValue();
            } catch (ParseException e) {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
            }
        	lblTargetTemp.setText("" + targetTemp);
        	initProgressBar();          
            checkHeating();
            lblNextSwitch.setText("Temporary mode");
    	}    	
    }
    
    // Check if heating has to burn (make image visible)
    public void checkHeating() {
    	if (targetTemp > currentTemp) {
    		imgHeating.setVisibility(View.VISIBLE);
    	} else {
    		imgHeating.setVisibility(View.INVISIBLE);
    	}    	
    }
    
	// Options menu 
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
    	//creates a menu inflater
    	MenuInflater inflater = getMenuInflater();
    	//generates a Menu from a menu resource file
    	//R.menu.main_menu represents the ID of the XML resource file
    	inflater.inflate(R.menu.main_menu, menu);
    	return true;
    }
       
    
    
    // When the user clicks on the options button
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    { 	
    	//check selected menu item
    	// R.id.exit is @+id/exit
    	switch (item.getItemId()) {
    		case R.id.exit:
    			this.finish();
    			return true;
    		case R.id.vacationSettings:
    			Bundle bundleVacation = new Bundle();
    			bundleVacation.putDouble("holidayTemperature", holidayTemp);
    			bundleVacation.putBoolean("holidayMode", holidayMode);
    			Intent iVacation = new Intent(ThermostatActivity.this, Vacation.class).putExtras(bundleVacation);
    			startActivityForResult(iVacation, HOLIDAY_RESULT);
    			return true;
    		case R.id.tempSettings:
    			Bundle bundleTemp = new Bundle();
    			bundleTemp.putDouble("dayTemp", dayTemp);
    			bundleTemp.putDouble("nightTemp", nightTemp);
    			Intent iTemps = new Intent(ThermostatActivity.this, Temperatures.class).putExtras(bundleTemp);
    			startActivityForResult(iTemps, TEMPERATURE_RESULT);
    			return true;
    		case R.id.weekProgramm:
    			// Show a pop up with all days of the week
    			showWeekDaysPopup();
    			return true;
    	}
    	
    	return false;
    }
    
    public void showWeekDaysPopup() {
    	final CharSequence[] items = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};

    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	builder.setTitle("Select a day");
    	builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                 dialog.cancel();
            }
        });
    	builder.setItems(items, new DialogInterface.OnClickListener() {
    	    public void onClick(DialogInterface dialog, int item) {   	    	
    	    	// Open daysettings.xml with the selected day
    	    	Bundle bundleTemp = new Bundle();
    			bundleTemp.putCharSequence("selectedDay", items[item]);
    			bundleTemp.putInt("dayInt", item);
    			Intent iTemps = new Intent(ThermostatActivity.this, DayOverview.class).putExtras(bundleTemp);
    			startActivity(iTemps);
    	    }
    	});
    	//AlertDialog alert = builder.create();
    	builder.show();
    	
    }

	public void checkVacation() {
    	if (holidayMode) {
    		// Holiday mode is active, disable most user elements and show holiday image
    		targetTemp = holidayTemp;
    		initProgressBar();
    		prgTemperature.setEnabled(false);
    		btnProgram.setEnabled(false);
    		imgMin.setEnabled(false);
    		imgPlus.setEnabled(false);
    		imgMin.setImageResource(R.drawable.mindis);
    		imgPlus.setImageResource(R.drawable.plusdis);
    		imgStatus.setImageResource(R.drawable.holiday);
    		lblTargetTemp.setText("" + targetTemp);
    		lblNextSwitch.setText("Vacation mode active");
    		checkHeating();
    	} else {
    		// No holiday is active, but set everything back in case it was active before
    		prgTemperature.setEnabled(true);
    		btnProgram.setEnabled(true);
    		imgMin.setEnabled(true);
    		imgPlus.setEnabled(true);
    		imgMin.setImageResource(R.drawable.minsign);
    		imgPlus.setImageResource(R.drawable.plussign);
    		setStatus();
    	}
    }
    
    public void setStatus() {
    	if(nightMode) {
    		// Night mode is active
    		imgStatus.setImageResource(R.drawable.moon);
    		lblNextSwitch.setText("Night mode");
    	} else {
    		// Normal mode
    		imgStatus.setImageResource(R.drawable.sun);
    		lblNextSwitch.setText("Day mode");
    	}
    }
    
    public void initProgressBar() {
    	prgTemperature.setProgress((int) ((targetTemp - minTemp) * 10));
    	prgTemperature.setSecondaryProgress(prgTemperature.getProgress()); 
    }
    
    public void activateProgram(View view) {
    	setTemperatureToMode();
    	Toast.makeText(this, "Target temperature reset to program settings", Toast.LENGTH_SHORT).show();
    }
    
    public void setTemperatureToMode() {
    	if (nightMode) {
    		// Reset temperatures to night mode
    		targetTemp = nightTemp;
    		
    	} else {
    		// Reset temperatures to day mode
    		targetTemp = dayTemp;
    	}
    	initProgressBar();
    	checkHeating();
    	setTargetLabel();
    	setStatus();
    }
    
    public void setTargetLabel() {
    	lblTargetTemp.setText("" + targetTemp);
    }
    
    // Activity results
    
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
    	switch(requestCode) {
    		case HOLIDAY_RESULT: 
          		if (resultCode == RESULT_OK) {
              		holidayMode = data.getBooleanExtra("setMode", false);
              		holidayTemp = data.getDoubleExtra("setTemp", 16.5);
              		checkVacation();
              		if (holidayMode) {
              			Toast.makeText(this, "Vacation mode activated", Toast.LENGTH_SHORT).show();
              		} else {
              			setTemperatureToMode();
              		}
              		checkHeating();
          		}
          		break;
    		case TEMPERATURE_RESULT:
    			if (resultCode == RESULT_OK) {
              		dayTemp = data.getDoubleExtra("dayTemp", 19);
              		nightTemp = data.getDoubleExtra("nightTemp", 16);
              		checkHeating();
              		setTemperatureToMode();
              		Toast.makeText(this, "Temperatures adjusted", Toast.LENGTH_SHORT).show();
          		}
    			break;
    	}
    }        
}
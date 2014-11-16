package tue.thermostat;

import java.text.DecimalFormat;
import java.text.ParseException;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.ToggleButton;

public class Vacation extends Activity implements OnSeekBarChangeListener{
	
	// Variables
	private double targetTemp; 
	private boolean vacationMode;
	private boolean touchStart = false;
	
	private double maxTemp = 30;
	private double minTemp = 5;
	
	DecimalFormat df = new DecimalFormat("###.#");
	
	// Elements
	private SeekBar prgTemperature;
	private TextView lblTemp;
	private ToggleButton switchButton;
	
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vacation);
        
        // Variables
        Bundle b = getIntent().getExtras();
        vacationMode = b.getBoolean("holidayMode");
        targetTemp = b.getDouble("holidayTemperature");    
        
        // Progress bar
        prgTemperature = (SeekBar) findViewById(R.id.prgTemperature);
        prgTemperature.setProgress((int) ((targetTemp - minTemp) * 10));
        prgTemperature.setOnSeekBarChangeListener(this);
        
        // Temperature label
        lblTemp = (TextView) findViewById(R.id.lblTemp);
        lblTemp.setText("" + targetTemp);
        
        // Toggle button
        switchButton = (ToggleButton) findViewById(R.id.toggleButton1);  
        switchButton.setChecked(vacationMode);
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
        	lblTemp.setText("" + targetTemp);
        	int intTemp = (int) ((targetTemp-minTemp)*10);
            prgTemperature.setProgress(intTemp);
            prgTemperature.setSecondaryProgress(prgTemperature.getProgress());   
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
        	lblTemp.setText("" + targetTemp);
        	int intTemp = (int) ((targetTemp-minTemp)*10);
            prgTemperature.setProgress(intTemp);
            prgTemperature.setSecondaryProgress(prgTemperature.getProgress()); 
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
        	lblTemp.setText("" + mProg);
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
    }
     
    public void saveVacation(View view) {
    	Intent returnIntent = new Intent();
        returnIntent.putExtra("setTemp",targetTemp);
        returnIntent.putExtra("setMode", vacationMode);
        setResult(RESULT_OK,returnIntent);        
        finish();
    }     
    
    public void toggleSwitch(View v) {
        if (switchButton.isChecked()) {
            vacationMode = true;
        } else {
        	vacationMode = false;
        }
    }
}
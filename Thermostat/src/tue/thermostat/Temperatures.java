package tue.thermostat;

import java.text.DecimalFormat;
import java.text.ParseException;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.SeekBar;
import android.widget.Toast;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class Temperatures extends Activity implements OnSeekBarChangeListener{
	
	// Variables
	private double dayTemp, nightTemp;
	private double minTemp = 5;
	private double maxTemp = 30;
	private boolean touchStart;
	DecimalFormat df = new DecimalFormat("###.#");
	
	// Progress bars
	private SeekBar prgDayTemp, prgNightTemp;
	
	// Temperature labels
	private TextView lblDayTemp, lblNightTemp;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.temperature);
        
        // Set progress bars
        prgDayTemp = (SeekBar) findViewById(R.id.prgDayTemp);
        prgNightTemp = (SeekBar) findViewById(R.id.prgNightTemp);
        prgDayTemp.setOnSeekBarChangeListener(this);
        prgNightTemp.setOnSeekBarChangeListener(this);
        
        // Set labels
        lblDayTemp = (TextView) findViewById(R.id.lblDayTemp);
        lblNightTemp = (TextView) findViewById(R.id.lblNightTemp);  
        
        // Parameters from creating
        Bundle b = getIntent().getExtras();
        dayTemp = b.getDouble("dayTemp");
        nightTemp = b.getDouble("nightTemp");
        
        // Set progress bars and labels
        prgDayTemp.setProgress((int) ((dayTemp - minTemp) * 10));
        prgNightTemp.setProgress((int) ((nightTemp - minTemp) * 10));
        prgDayTemp.setSecondaryProgress(prgDayTemp.getProgress()); 
        prgNightTemp.setSecondaryProgress(prgNightTemp.getProgress()); 
        lblDayTemp.setText("" + dayTemp);
        lblNightTemp.setText("" + nightTemp);
    }
    
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
    	if(seekBar == prgDayTemp) {
    		double mProg = minTemp + progress*0.1;   
        	try {
        		 mProg = df.parse(df.format(mProg)).doubleValue();
        	} catch (ParseException e) {
        		// TODO Auto-generated catch block
        		e.printStackTrace();
        	}
        	lblDayTemp.setText("" + mProg);
        	dayTemp = mProg;	
    	} else {
    		double mProg = minTemp + progress*0.1;   
        	try {
        		 mProg = df.parse(df.format(mProg)).doubleValue();
        	} catch (ParseException e) {
        		// TODO Auto-generated catch block
        		e.printStackTrace();
        	}
        	lblNightTemp.setText("" + mProg);
        	nightTemp = mProg;	
    	}
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    	// TODO Auto-generated method stub
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
    	// TODO Auto-generated method stub
    	if(seekBar == prgDayTemp) {
    		prgDayTemp.setSecondaryProgress(seekBar.getProgress()); 
    	} else {
    		prgNightTemp.setSecondaryProgress(seekBar.getProgress()); 
    	}
    }
    
 // Decrease temperature with 0.1 (until 5)
    public void decDayTemp(View view) {
    	if (dayTemp > minTemp) {
    		dayTemp = dayTemp - 0.1;
        	try {
        		dayTemp = df.parse(df.format(dayTemp)).doubleValue();
            } catch (ParseException e) {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
            }
        	lblDayTemp.setText("" + dayTemp);
        	int intTemp = (int) ((dayTemp-minTemp)*10);
            prgDayTemp.setProgress(intTemp);
            prgDayTemp.setSecondaryProgress(prgDayTemp.getProgress()); 
    	}    	
    }
    
    public void incDayTemp(View view) {
    	if (dayTemp < maxTemp) {
    		dayTemp = dayTemp + 0.1;
        	try {
        		dayTemp = df.parse(df.format(dayTemp)).doubleValue();
            } catch (ParseException e) {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
            }
        	lblDayTemp.setText("" + dayTemp);
        	int intTemp = (int) ((dayTemp-minTemp)*10);
            prgDayTemp.setProgress(intTemp);
            prgDayTemp.setSecondaryProgress(prgDayTemp.getProgress()); 
    	}    	
    }
    
    public void decNightTemp(View view) {
    	if (nightTemp > minTemp) {
    		nightTemp = nightTemp - 0.1;
        	try {
        		nightTemp = df.parse(df.format(nightTemp)).doubleValue();
            } catch (ParseException e) {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
            }
        	lblNightTemp.setText("" + nightTemp);
        	int intTemp = (int) ((nightTemp-minTemp)*10);
        	prgNightTemp.setProgress(intTemp);
        	prgNightTemp.setSecondaryProgress(prgNightTemp.getProgress()); 
    	}    	
    }
    
    public void incNightTemp(View view) {
    	if (nightTemp < maxTemp) {
    		nightTemp = nightTemp + 0.1;
        	try {
        		nightTemp = df.parse(df.format(nightTemp)).doubleValue();
            } catch (ParseException e) {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
            }
        	lblNightTemp.setText("" + nightTemp);
        	int intTemp = (int) ((nightTemp-minTemp)*10);
            prgNightTemp.setProgress(intTemp);
            prgNightTemp.setSecondaryProgress(prgNightTemp.getProgress()); 
    	}    	
    }
    
    public void saveTemperatures(View view) {
    	Intent returnIntent = new Intent();
        returnIntent.putExtra("dayTemp", dayTemp);
        returnIntent.putExtra("nightTemp", nightTemp);
        setResult(RESULT_OK,returnIntent);        
        finish();
    }
}
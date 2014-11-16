package tue.thermostat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Stack;

import android.app.Application;
import android.os.Bundle;
import android.widget.Toast;

public class GlobalApp extends Application {
	   
	// Array lists
    ArrayList<Setting> mondaySettings;// = new ArrayList<Setting>();
    ArrayList<Setting> tuesdaySettings;// = new ArrayList<Setting>();
    ArrayList<Setting> wednesdaySettings;// = new ArrayList<Setting>();
    ArrayList<Setting> thursdaySettings;// = new ArrayList<Setting>();
    ArrayList<Setting> fridaySettings;// = new ArrayList<Setting>();
    ArrayList<Setting> saturdaySettings;// = new ArrayList<Setting>();
    ArrayList<Setting> sundaySettings;// = new ArrayList<Setting>();
    int[][] settingCount;// = new int[7][2]; // [day][mode] 0=night, 1=day
        
    ArrayList<Setting> dayList;// = new ArrayList<Setting>(); // List with settings for a day
        
    //private Setting lastMode;
    
    //String dayName;
    //int theDay;
    
    @Override
    public void onCreate() {
        super.onCreate();
        
        mondaySettings = new ArrayList<Setting>();
        tuesdaySettings = new ArrayList<Setting>();
        wednesdaySettings = new ArrayList<Setting>();
        thursdaySettings = new ArrayList<Setting>();
        fridaySettings = new ArrayList<Setting>();
        saturdaySettings = new ArrayList<Setting>();
        sundaySettings = new ArrayList<Setting>();
        
        settingCount = new int[7][2];
        dayList = new ArrayList<Setting>();
        
        //dayName = "";
        //theDay = 0;
        
        //lastMode = null;
    }
    
    
    public Boolean checkPossible(int day, int mode, int hours, int minutes, Setting setting) {
    	if(setting==null) {
    		// Add mode
    		if (settingCount[day][mode] == 5) {
        		Toast.makeText(this, "New setting is not allowed, 5 is the maximum for this mode.", Toast.LENGTH_LONG).show();
        		return false;
        	}
        	
        	ArrayList<Setting> list = new ArrayList<Setting>();    	
        	list = returnList(day);
        	
        	for(Setting s : list) {
        		if(s.getHours() == hours && s.getMinutes() == minutes) {
        			Toast.makeText(this, "Time is already been used.", Toast.LENGTH_LONG).show();
        			return false;
        		}
        	}
    	} else {
    		// Apply mode
        	ArrayList<Setting> list = new ArrayList<Setting>();    	
        	list = returnList(day);
        	
        	if ((setting.getMode().equals("Night") && mode==1 && settingCount[day][1]==5)||(setting.getMode().equals("Day") && mode==0 && settingCount[day][0]==5)) {
        		Toast.makeText(this, "New setting is not allowed, 5 is the maximum for this mode.", Toast.LENGTH_LONG).show();
        		return false;	
        	}
        	
        	for(Setting s : list) {
        		if(s.getHours() == hours && s.getMinutes() == minutes) {
        			Setting compareSetting = this.getSetting(list, s.getHours(), s.getMinutes());
        			if(!(compareSetting==setting)) {
        				// Niet dezelfde settings, dus verboden
            			Toast.makeText(this, "Time is already been used.", Toast.LENGTH_LONG).show();
            			return false;
        			}
        		}
        	}
    	}
    	
    	return true;
    }
    
    public void addSetting(ArrayList<Setting> list, String mode, int hours, int minutes, int day) {
    	Setting nieuweSetting = new Setting(hours, minutes, mode);
    	list.add(nieuweSetting);
    	if(mode.equals("Night")){
    		settingCount[day][0]++;
    	} else {
    		settingCount[day][1]++;
    	}
    	listSort(list);
    }
    
    private void listSort(ArrayList<Setting> list) {
	   Collections.sort(list, new Comparator<Object>(){
		   
           public int compare(Object o1, Object o2) {
               Setting p1 = (Setting) o1;
               Setting p2 = (Setting) o2;
              return p1.toString().compareToIgnoreCase(p2.toString());
           }

       });  
    }

    public void removeSetting(ArrayList<Setting> list, Setting theSetting, int day) {
	   list.remove(theSetting);
	   String mode = theSetting.getMode();
	   if(mode.equals("Night")){
		   settingCount[day][0]--;
	   } else {
		   settingCount[day][1]--;
	   }
	   listSort(list);
   }
    
    public Setting getSetting(ArrayList<Setting> list, int hour, int minute) {
    	Setting rSetting = null;
    	for (Setting s : list) {
    		if(s.getHours() == hour && s.getMinutes() == minute) {
    			return s;
    		}
    	}
    	return rSetting;
    }
   
    public void generateDayList(int day, int hour, int minute) {
	   dayList.clear();
	   fillList(this.returnList(day), hour, minute);
   }
      
    public Setting returnModeSetting(int hours, int minutes) {
    	Setting returnSetting;
    	
    	
    	if(!(dayList.isEmpty())) {
    		returnSetting = null;
        	for (Setting s : dayList) {
        		// Loop door de hele dayList heen, en return welke mode gekozen moet worden
        		if(s.getHours() <= hours) {
        			if(s.getHours() == hours && s.getMinutes() <= minutes) {
        				// Goeie setting
        				returnSetting = s;
        			} else if(s.getHours() < hours) {
        				// Goeie setting
        				returnSetting = s;
        			}
        		} else {
        			// Uren van de mode zitten al boven huidige tijd, dus return laatste
        			return returnSetting;
        		}
        	}
        	
        	return returnSetting;
    	}
  
    	return null;
    }
   
    private void fillList(ArrayList<Setting> list, int hour, int minute) {
	   for (Setting s : list) {
		   	dayList.add(s);
	   }
   }
    
    public String getDayLabel(int day) {
    	String label = null;
    	switch(day){
    	case 0: 
    		label = "Monday";
    		break;
    	case 1: 
    		label = "Tuesday";
    		break;
    	case 2: 
    		label = "Wednesday";
    		break;
    	case 3: 
    		label = "Thursday";
    		break;
    	case 4: 
    		label = "Friday";
    		break;
    	case 5: 
    		label = "Saturday";
    		break;
    	case 6: 
    		label = "Sunday";
    		break;	
    	}
    	return label;
    }
    
    public ArrayList<Setting> returnList(int day) {
    	ArrayList<Setting> lijst = new ArrayList<Setting>();
    	switch(day) {
		   case 0:
			   lijst = this.mondaySettings;
			   break;
		   case 1:
			   lijst = this.tuesdaySettings;
			   break;
		   case 2:
			   lijst = this.wednesdaySettings;
			   break;
		   case 3:
			   lijst = this.thursdaySettings;
			   break;
		   case 4:
			   lijst = this.fridaySettings;
			   break;
		   case 5:
			   lijst = this.saturdaySettings;
			   break;
		   case 6:
			   lijst = this.sundaySettings;
			   break;
		   }
    	return lijst;
    }

	public int getDayNum(String dayName) {
	    if(dayName.equals("Monday")) {
	    	return 0;
	    } else if(dayName.equals("Tuesday")) {
	    	return 1;
	    } else if(dayName.equals("Wednesday")) {
	    	return 2;
	    } else if(dayName.equals("Thursday")) {
	    	return 3;
	    } else if(dayName.equals("Friday")) {
	    	return 4;
	    } else if(dayName.equals("Saturday")) {
	    	return 5;
	    } else if(dayName.equals("Sunday")) {
	    	return 6;
	    } else {
	    	return 0;
	    }
    }

}
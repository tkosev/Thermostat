package tue.thermostat;

public class Setting {
	
	private int hours;
	private int minutes;
	private String mode;
	
	public Setting(int hours, int minutes, String mode) {
		setHours(hours);
		setMinutes(minutes);
		setMode(mode);
	}
	
	public int getHours() {
		return hours;
	}
	
	public int getMinutes() {
		return minutes;
	}
	
	public void setHours(int hours) {
		this.hours = hours;
	}
	
	public void setMinutes(int minutes) {
		this.minutes = minutes;
	}
	
	public void setMode(String mode) {
		this.mode = mode;
	}
	
	public String getMode() {
		if(mode.equals("Night")) {
			return "Night";
		} else {
			return "Day";
		}
	}
	
	public String toString() {
		int tHour, tMin;
		tHour = this.getHours();
		tMin = this.getMinutes();
		String uur, minuut;
		
		uur = "" + tHour;
		minuut = "" + tMin;
		
		if (tHour < 10) {
			uur = "0" + tHour;
		}
		
		if (tMin < 10) {
			minuut = "0" + tMin;
		}
		
		return uur + ":" + minuut + "        -        " + this.getMode();
	}
	
	public int compare(Setting s1, Setting s2) {
		int x = s1.toString().compareTo(s2.toString());
		return x;
	}
	
	public int compareTo(Setting s) {
		return compare(this, s);
	}

}

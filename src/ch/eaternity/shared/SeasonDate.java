package ch.eaternity.shared;

import java.io.Serializable;
import java.util.Date;

/*
 * Represents a start and ending date of a season
 * month: 1 is January, 12 is December
 * day: 1 is first of month, 31 possibly last
 * 
 */
public class SeasonDate implements Serializable {

	private static final long serialVersionUID = 8516034014140363621L;
	
	public int month;
	public int day;
	
	public SeasonDate() {}
	
	public SeasonDate(int month, int day) {
		this.month = month;
		this.day = day;
	}
	
	public SeasonDate(SeasonDate toClone) {
		month = toClone.month;
		day = toClone.day;
	}


	/*
	 * Format: dd.MM
	 */
	public boolean setDate(String date) {
		String splitted[] = date.trim().split("\\.");
		if(splitted.length == 2) {
			int day = Integer.parseInt(splitted[0]);
			int month = Integer.parseInt(splitted[1]);
			if (day >= 1 && day <= 31 && month >= 1 && month <= 12) {
				this.day = day;
				this.month = month;
				return true;
			}
			else return false;
		}
		else return false;
	}

	public boolean setDate(Date date) {
		String strDate = date.getDay() + "." + date.getMonth();
		return setDate(strDate);
	}

	/*
	 * if the dates are same, after returns true as well (still in season)
	 */
	public boolean after(SeasonDate other){
		if (this.month > other.month) {
			return true;
		}
		else if (this.month == other.month)
			if (this.day >= other.day)
				return true;
			else 
				return false;
		return false;
	}

	public boolean before(SeasonDate other) {
		if (this.month < other.month) {
			return true;
		}
		else if (this.month == other.month)
			if (this.day <= other.day)
				return true;
			else 
				return false;
		return false;
	}

}


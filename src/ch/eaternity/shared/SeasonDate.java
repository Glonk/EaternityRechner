package ch.eaternity.shared;

import java.util.Date;
import javax.persistence.Id;

/*
 * Represents a start and ending date of a season
 * month: 1 is January, 12 is December
 * day: 1 is first of month, 31 possibly last
 * 
 */
public class SeasonDate{
	
	private static final long serialVersionUID = -2858234034518875678L;
	
	@Id
	private Long id;
	
	public int month;
	public int day;

	public SeasonDate(int month, int day) {
		this.month = month;
		this.day = day;
	}

	public SeasonDate() {
		month = 0;
		day = 0;
	}

	public SeasonDate(Date date) {
		setDate(date);
	}
	
	// copy constructor
	public SeasonDate(SeasonDate toClone) {
		month = toClone.getMonth();
		day = toClone.getDay();
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
		if (date != null) {
			String strDate = date.getDay() + "." + date.getMonth();
			return setDate(strDate);
		}
		else return false;
	}


	/*
	 * if the dates are same, after returns true as well (still in season)
	 */
	public boolean after(SeasonDate other){
		if (this.month > other.getMonth()) {
			return true;
		}
		else if (this.month == other.getMonth())
			if (this.day >= other.getDay())
				return true;
			else 
				return false;
		return false;
	}

	public boolean before(SeasonDate other) {
		if (this.month < other.getMonth()) {
			return true;
		}
		else if (this.month == other.getMonth())
			if (this.day <= other.getDay())
				return true;
			else 
				return false;
		return false;
	}

	public int getMonth() {
		return this.month;
	}

	public int getDay() {
		return this.day;
	}



}
package ch.eaternity.shared;

import java.io.Serializable;

import com.google.gwt.user.client.rpc.IsSerializable;
import com.googlecode.objectify.annotation.*;

public class Season implements Serializable {
	
	private static final long serialVersionUID = -2858323468921887438L;

	@Id private Long id;
	
	@Embed
	private SeasonDate start;
	@Embed
	private SeasonDate stop;
	
	private SeasonType seasonType;
	
	/**
	 * Empty Constructor needed for objectify
	 */
	public Season() {}
	
	public Season(SeasonDate start, SeasonDate stop, SeasonType seasonType) {
		this.start = start;
		this.stop = stop;
		this.seasonType = seasonType;
	}
	
	/**
	 * Copy constructor
	 */
	public Season(Season other) {
		start = new SeasonDate(other.getBeginning());
		stop = new SeasonDate(other.getEnd());
		seasonType = other.getSeasonType();
	}
	
	
	public SeasonDate getBeginning() {
		return start;
	}

	 
	public SeasonDate getEnd() {
		return stop;
	}

	 
	public SeasonType getSeasonType() {
		return seasonType;
	}


	 
	public void setBeginning(SeasonDate beginning) {
		this.start = beginning;
	}

	 
	public void setEnd(SeasonDate end) {
		this.stop = end;
	}

	 
	public void setSeasonType(SeasonType type) {
		this.seasonType = type;
	}

}

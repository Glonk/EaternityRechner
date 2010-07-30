package ch.eaternity.shared;

import java.util.Date;
import java.util.List;

import javax.persistence.Embedded;
import javax.persistence.Id;
import javax.persistence.Transient;

import com.google.gwt.user.client.rpc.IsSerializable;
import com.googlecode.objectify.Key;

public class Extraction implements IsSerializable{
	

	@Id Long id;
    
	public String symbol;
	public Boolean hasSeason;
	public String startSeason;
	public String stopSeason;
	
	public Condition stdCondition;
	public Production stdProduction;
	public MoTransportation stdMoTransportation;
	
	@Embedded
	public ProductLabel[] stdProductLabels;
	
    private Extraction() {
		
	}
	
    public Extraction(String symbol) {
		this.symbol = symbol;
	}


	
}

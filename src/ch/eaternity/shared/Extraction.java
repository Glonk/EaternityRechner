package ch.eaternity.shared;

import com.google.gwt.user.client.rpc.IsSerializable;


import com.googlecode.objectify.annotation.*;
import com.googlecode.objectify.Key;

public class Extraction implements IsSerializable {
	

	/**
	 * 
	 */
	private static final long serialVersionUID = -3996022378367844877L;

	@Id Long id;
    
	public String symbol;

	
	/*
	public Condition stdCondition;
	public Production stdProduction;
	public MoTransportation stdMoTransportation;
	
	@Embedded
	public List<ProductLabel> stdProductLabels;
	
	*/
	
    private Extraction() {}
    
    public Extraction(Extraction toClone) {
    	symbol = new String(toClone.symbol);
    }
	
    public Extraction(String symbol) {
		this.symbol = symbol;
	}
    
    public String getName() {
    	return this.symbol;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Extraction other = (Extraction) obj;
        if ((this.symbol == null) ? (other.symbol != null) : !this.symbol.equals(other.symbol)) {
            return false;
        }
        return true;
    }

}

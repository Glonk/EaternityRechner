package ch.eaternity.shared;

import java.io.Serializable;

import javax.persistence.Id;

import com.google.gwt.user.client.rpc.IsSerializable;

public class MoTransportation implements Serializable, Cloneable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5971128872903171922L;
	@Id Long id;
    public String symbol;
	public Double factor;
    
    private MoTransportation()
    {
    	
    }
    
    public MoTransportation(MoTransportation toClone) {
		symbol = new String(toClone.symbol);
		factor = new Double(toClone.factor);
	}

    public MoTransportation(String symbol) {
    	this.symbol = symbol;
	}
    
    public MoTransportation(String symbol, Double factor) {
    	this.symbol = symbol;
    	this.factor = factor;
	}
}

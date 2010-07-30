package ch.eaternity.shared;

import java.io.Serializable;

import javax.persistence.Id;

import com.google.gwt.user.client.rpc.IsSerializable;

public class MoTransportation implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5971128872903171922L;
	@Id Long id;
    String symbol;
    
    private MoTransportation()
    {
    	
    }

    public MoTransportation(String symbol) {
    	this.symbol = symbol;
	}
}

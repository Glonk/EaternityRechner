package ch.eaternity.shared;

import java.io.Serializable;

import javax.persistence.Id;

import com.google.gwt.user.client.rpc.IsSerializable;

public class Production implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4042328283688002109L;
	@Id Long id;
	public String symbol;
	public Double factor;
	
	private Production()
	{
		
	}
	
    public Production(String symbol) {
		this.symbol = symbol;
	}
}

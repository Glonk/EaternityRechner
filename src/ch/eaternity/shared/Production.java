package ch.eaternity.shared;

import java.io.Serializable;

import com.googlecode.objectify.annotation.*;

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
	
	public Production(Production toClone) {
		symbol = new String(toClone.symbol);
		factor = new Double(toClone.factor);
	}
	
    public Production(String symbol) {
		this.symbol = symbol;
	}
}

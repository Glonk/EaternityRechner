package ch.eaternity.shared;


import java.io.Serializable;

import com.google.gwt.user.client.rpc.IsSerializable;

import com.googlecode.objectify.annotation.*;

public class SavingPotential implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6771844486905410730L;


	@Id Long id;
	public String symbol;
    public int amount;
    
    public SavingPotential(SavingPotential toClone)
    {
    	symbol = new String(toClone.symbol);
    	amount = toClone.amount;
    }
    
    private SavingPotential()
    {
    	
    }
    
    public SavingPotential(String symbol) {
		this.symbol = symbol;
	}

}

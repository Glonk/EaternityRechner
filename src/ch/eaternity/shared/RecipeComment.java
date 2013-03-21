package ch.eaternity.shared;


import com.google.gwt.user.client.rpc.IsSerializable;

import com.googlecode.objectify.annotation.*;

public class RecipeComment implements IsSerializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6771844486905410730L;


	@Id Long id;
	public String symbol;
    public int amount;
    
    public RecipeComment(RecipeComment toClone)
    {
    	symbol = new String(toClone.symbol);
    	amount = toClone.amount;
    }
    
    private RecipeComment()
    {
    	
    }
    
    public RecipeComment(String symbol) {
		this.symbol = symbol;
	}

}

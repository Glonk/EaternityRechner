package ch.eaternity.shared;


import java.io.Serializable;

import javax.persistence.Id;

public class RecipeComment implements Serializable, Cloneable{

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

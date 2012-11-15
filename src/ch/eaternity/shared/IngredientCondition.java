package ch.eaternity.shared;

import java.io.Serializable;

import javax.persistence.Id;

import com.google.gwt.user.client.rpc.IsSerializable;

public class IngredientCondition implements Serializable, Cloneable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6977365500555091257L;
	@Id Long id;
	public String symbol;
	public Double factor;
	
	private IngredientCondition()
	{
		
	}
	
    public IngredientCondition(String symbol) {
		this.symbol = symbol;
	}
    
    public IngredientCondition(String symbol, Double factor) {
    	this.symbol = symbol;
    	this.factor = factor;
	}
}

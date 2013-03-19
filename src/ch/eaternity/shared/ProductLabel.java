package ch.eaternity.shared;

import java.io.Serializable;

import com.googlecode.objectify.annotation.*;

public class ProductLabel implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5720521418971058760L;
	@Id Long id;
    String symbol;
    String logoPngUrl;
    
    private ProductLabel()
    {
    	
    }
    
    public ProductLabel(String symbol) {
		this.symbol = symbol;
	}

}

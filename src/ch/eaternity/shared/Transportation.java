package ch.eaternity.shared;

import java.io.Serializable;

import com.googlecode.objectify.annotation.*;

public class Transportation implements Serializable {

	private static final long serialVersionUID = -5971128872903171922L;
	
	@Id Long id;
    public String symbol;
	public Double factor;
    
    private Transportation()
    {
    	
    }
    
    public Transportation(Transportation toClone) {
		symbol = new String(toClone.symbol);
		factor = new Double(toClone.factor);
	}

    public Transportation(String symbol) {
    	this.symbol = symbol;
	}
    
    public Transportation(String symbol, Double factor) {
    	this.symbol = symbol;
    	this.factor = factor;
	}
}

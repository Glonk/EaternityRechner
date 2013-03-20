package ch.eaternity.shared;

import java.io.Serializable;

import com.googlecode.objectify.annotation.*;

public class Condition implements Serializable {

	private static final long serialVersionUID = -5971128872903171922L;
	
	@Id Long id;

	private String symbol;
	private Double factor;
    
    private Condition() {}
    
    public Condition(Condition toClone) {
		symbol = new String(toClone.symbol);
		factor = new Double(toClone.factor);
	}

    public Condition(String symbol) {
    	this.symbol = symbol;
	}
    
    public Condition(String symbol, Double factor) {
    	this.symbol = symbol;
    	this.factor = factor;
	}
    
    public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public Double getFactor() {
		return factor;
	}

	public void setFactor(Double factor) {
		this.factor = factor;
	}
}

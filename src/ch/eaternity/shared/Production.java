package ch.eaternity.shared;

import java.io.Serializable;

import com.google.gwt.user.client.rpc.IsSerializable;

import com.googlecode.objectify.annotation.*;

public class Production implements Serializable {

	private static final long serialVersionUID = -5971128872903171922L;
	
	@Id Long id;

	private String symbol;
	private Double factor;
    
    private Production() {}
    
    public Production(Production toClone) {
		symbol = new String(toClone.symbol);
		factor = new Double(toClone.factor);
	}

    public Production(String symbol) {
    	this.symbol = symbol;
	}
    
    public Production(String symbol, Double factor) {
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

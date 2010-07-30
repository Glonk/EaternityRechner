package ch.eaternity.shared;

import javax.persistence.Id;

import com.google.gwt.user.client.rpc.IsSerializable;

public class Condition implements IsSerializable{

	@Id Long id;
	String symbol;
	
	
    public Condition(String symbol) {
		this.symbol = symbol;
	}
}

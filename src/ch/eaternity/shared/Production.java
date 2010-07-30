package ch.eaternity.shared;

import javax.persistence.Id;

import com.google.gwt.user.client.rpc.IsSerializable;

public class Production implements IsSerializable{

	@Id Long id;
	String symbol;
	
    public Production(String symbol) {
		this.symbol = symbol;
	}
}

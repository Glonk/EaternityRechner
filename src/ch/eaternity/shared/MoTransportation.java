package ch.eaternity.shared;

import javax.persistence.Id;

import com.google.gwt.user.client.rpc.IsSerializable;

public class MoTransportation implements IsSerializable{

	@Id Long id;
    String symbol;

    public MoTransportation(String symbol) {
    	this.symbol = symbol;
	}
}

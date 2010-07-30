package ch.eaternity.shared;

import javax.persistence.Id;

import com.google.gwt.user.client.rpc.IsSerializable;

public class ProductLabel implements IsSerializable{

	@Id Long id;
    String symbol;
    String logoPngUrl;
    
    public ProductLabel(String symbol) {
		this.symbol = symbol;
	}

}

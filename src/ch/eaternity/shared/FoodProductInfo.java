package ch.eaternity.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

import com.googlecode.objectify.annotation.Id;

public class FoodProductInfo implements IsSerializable {
	
	private static final long serialVersionUID = -285831234679327438L;
	
	@Id private Long id;

	private Long productId;
	
	private String name;
	
	private Double co2eValue;
	
	private boolean inSeason;
	
}

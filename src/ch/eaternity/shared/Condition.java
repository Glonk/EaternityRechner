package ch.eaternity.shared;

import java.io.Serializable;

import javax.persistence.Id;

import com.google.gwt.user.client.rpc.IsSerializable;

public class Condition implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6977365500555091257L;
	@Id Long id;
	public String symbol;
	public Double factor;
	
	private Condition()
	{
		
	}
	
    public Condition(String symbol) {
		this.symbol = symbol;
	}
}

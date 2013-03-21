package ch.eaternity.shared;

import com.google.gwt.user.client.rpc.IsSerializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.googlecode.objectify.annotation.*;

@Entity
public class Commitment implements IsSerializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4270469337154235995L;
	
	@Id
	public Long id;
    
	public List<Date> dates;
	
	@Serialize
	public  List<Recipe> recipes = new ArrayList<Recipe>();
	
	public String email;
	
	public String name;
	
	public Integer numberPerson;
	

	public Commitment() {

	}

}

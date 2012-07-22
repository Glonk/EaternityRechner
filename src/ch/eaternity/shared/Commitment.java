package ch.eaternity.shared;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Id;

public class Commitment implements Serializable, Cloneable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4270469337154235995L;
	
	@Id
	public Long id;
    
	public List<Date> dates;
	public  List<Recipe> recipes = new ArrayList<Recipe>();
	
	public String email;
	
	public String name;
	
	public Integer numberPerson;
	

	public Commitment() {

	}

}

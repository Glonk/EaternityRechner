package ch.eaternity.shared;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.jdo.annotations.Extension;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import javax.persistence.Embedded;
import javax.persistence.Id;

import com.googlecode.objectify.annotation.Serialized;


public class EnergyMix implements Serializable {

	/** 
	 * 
	 */
	private static final long serialVersionUID = 3172640409035191698L;

	@Id String id;
     
	 
	public Double Co2PerKWh;
	
	 
	public String Name;
	
	
	

	public EnergyMix() {

	}

	public EnergyMix(String Name,Double Co2PerKWh) {
		this.Name = Name;
		this.Co2PerKWh = Co2PerKWh;

	}

	public void setId(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}


	

	  

}

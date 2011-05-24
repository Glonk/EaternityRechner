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


public class Device implements Serializable {

	/** 
	 * 
	 */
	private static final long serialVersionUID = 3172640409035191698L;

	@Id Long id;
     
	 
	public Double kWConsumption;
	
	 
	public String deviceName;
	
	 
	public String deviceSpec;
	
	 
	public Long stdDuration;
	
	@Embedded
	public Long[] durations;
	

	public Device() {

	}

	public Device(String deviceName,String deviceSpec, Double kWConsumption,Long[]  list, Long stdDuration) {
		this.deviceName = deviceName;
		this.deviceSpec = deviceSpec;
		this.kWConsumption = kWConsumption;
		this.durations = list;
		this.stdDuration =stdDuration;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getId() {
		return id;
	}


	

	  

}

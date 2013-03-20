package ch.eaternity.shared;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


import com.googlecode.objectify.annotation.*;


public class Device implements Serializable {

	private static final long serialVersionUID = 3172640409035191698L;

	@Id Long id;
     
	public Double kWConsumption;
	 
	public String deviceName;
	 
	public String deviceSpec;
	 
	public Long stdDuration;
	
	public List<Long> durations;
	

	public Device() {}

	public Device(String deviceName,String deviceSpec, Double kWConsumption,List<Long> durations, Long stdDuration) {
		this.deviceName = deviceName;
		this.deviceSpec = deviceSpec;
		this.kWConsumption = kWConsumption;
		this.durations = durations;
		this.stdDuration =stdDuration;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getId() {
		return id;
	}


	

	  

}

package ch.eaternity.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
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
	

	public Device() {
		Long[] longList ={1l,5l,10l,20l};
		
		this.deviceName = "";
		this.deviceSpec = "";
		this.kWConsumption = 0.0;
		this.durations = Arrays.asList(longList);
		this.stdDuration = 10L;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getId() {
		return id;
	}


}

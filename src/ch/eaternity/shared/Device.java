package ch.eaternity.shared;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.jdo.annotations.Extension;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable
public class Device implements Serializable {

	/** 
	 * 
	 */
	private static final long serialVersionUID = 3172640409035191698L;
    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    @Extension(vendorName="datanucleus", key="gae.encoded-pk", value="true")
    private String id;
     
	@Persistent
	public Double kWConsumption;
	
	@Persistent
	public String deviceName;
	
	@Persistent
	public String deviceSpec;
	
	@Persistent
	public Long stdDuration;
	
	@Persistent
	public List<Long> durations;
	

	public Device() {

	}

	public Device(String deviceName,String deviceSpec, Double kWConsumption,List<Long>  list, Long stdDuration) {
		this.deviceName = deviceName;
		this.deviceSpec = deviceSpec;
		this.kWConsumption = kWConsumption;
		this.durations = list;
		this.stdDuration =stdDuration;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}


	

	  

}

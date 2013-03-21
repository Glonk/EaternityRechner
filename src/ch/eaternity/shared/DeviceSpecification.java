package ch.eaternity.shared;


import com.google.gwt.user.client.rpc.IsSerializable;


import com.googlecode.objectify.annotation.*;


public class DeviceSpecification implements IsSerializable  {

	private static final long serialVersionUID = -2701096899616747762L;

	@Id Long id;
	 
	public Double kWConsumption;
	public String deviceName;
	public String deviceSpec;
	public Long duration;

	public DeviceSpecification() {

	}
	
	public DeviceSpecification(DeviceSpecification toClone) {
		kWConsumption = new Double(toClone.kWConsumption);
		deviceName = new String(toClone.deviceName);
		deviceSpec = new String(toClone.deviceSpec);
		duration = new Long(toClone.duration);
	}

	public DeviceSpecification(String deviceName,String deviceSpec, Double kWConsumption, Long duration) {
		this.deviceName = deviceName;
		this.deviceSpec = deviceSpec;
		this.kWConsumption = kWConsumption;
		this.duration =duration;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getId() {
		return id;
	}


	

	  

}

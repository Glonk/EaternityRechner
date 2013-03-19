package ch.eaternity.shared;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


import com.googlecode.objectify.annotation.*;


public class EnergyMix implements Serializable {

	/** 
	 * 
	 */
	private static final long serialVersionUID = 3172640409035191698L;

	@Id String id;
     
	 
	public Double Co2PerKWh;
	
	 
	public String Name;
	
	
	public EnergyMix(EnergyMix toClone) {
		this.Co2PerKWh = new Double(toClone.Co2PerKWh);
		this.Name = new String(toClone.Name);
	}

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

package ch.eaternity.shared;


import java.io.Serializable;

import javax.jdo.annotations.Extension;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable
public class SingleDistance implements Serializable {

	/** 
	 * 
	 */
	private static final long serialVersionUID = 3172640409035191698L;
    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    @Extension(vendorName="datanucleus", key="gae.encoded-pk", value="true")
    private String id;
     
	@Persistent
	private Double distance;
	
	@Persistent
	private String from;
	
	@Persistent
	private String to;
	
	@Persistent
	private Boolean road;
	
	@Persistent
	private Boolean triedRoad;
	
	public SingleDistance(String to, String from, Double distance) {
		this.to = to;
		this.from = from;
		this.distance = distance;
	}


	public void setDistance(Double distance) {
		this.distance = distance;
	}

	public Double getDistance() {
		return distance;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getFrom() {
		return from;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public String getTo() {
		return to;
	}

	public void setRoad(Boolean road) {
		this.road = road;
	}

	public Boolean getRoad() {
		return road;
	}

	public void setTriedRoad(Boolean triedRoad) {
		this.triedRoad = triedRoad;
	}

	public Boolean getTriedRoad() {
		return triedRoad;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}
	
	  

}

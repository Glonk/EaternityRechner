package ch.eaternity.shared;


import com.google.gwt.user.client.rpc.IsSerializable;

import com.googlecode.objectify.annotation.*;

@Entity
public class SingleDistance implements IsSerializable {

	private static final long serialVersionUID = 3172640409035191698L;

    @Id private String id;
     
	private Double distance;
	
	private String from;
	
	private String to;
	
	private Boolean road;

	private Boolean triedRoad;
	
	public SingleDistance() {}
	
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

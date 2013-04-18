package ch.eaternity.shared;


import java.io.Serializable;
import org.eaticious.common.QuantityImpl;
import org.eaticious.common.Unit;

import com.googlecode.objectify.annotation.*;

@Entity
public class Route implements Serializable {

	private static final long serialVersionUID = 3172640409035191698L;

    @Id private Long id;
   
    @Serialize
	private QuantityImpl distance;
	
	private String from;
	
	private String to;
	
	private Boolean roadRoute;

	private Boolean triedRoad;
	
	public Route() {
		distance = new QuantityImpl(0.0, Unit.KILOMETER);
		roadRoute = false;
	}
	
	/**
	 * 
	 * @param from
	 * @param to
	 * @param distance
	 */
	public Route(String from, String to, QuantityImpl distance) {
		this.to = to;
		this.from = from;
		this.distance = distance;
	}


	public void setDistance(QuantityImpl distance) {
		this.distance = distance;
	}

	public QuantityImpl getDistance() {
		return distance;
	}
	
	public QuantityImpl getDistanceKM() {
		return (QuantityImpl)distance.convert(Unit.KILOMETER);
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

	public void setRoadRoute(Boolean roadRoute) {
		this.roadRoute = roadRoute;
	}

	public Boolean isRoadRoute() {
		return roadRoute;
	}

	public void setTriedRoad(Boolean triedRoad) {
		this.triedRoad = triedRoad;
	}

	public Boolean getTriedRoad() {
		return triedRoad;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getId() {
		return id;
	}
	
	  

}

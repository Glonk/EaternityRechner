package ch.eaternity.shared;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;
import com.google.gwt.view.client.HasData;

import com.googlecode.objectify.annotation.Id;

public class FoodProductInfo implements IsSerializable {
	
	private static final long serialVersionUID = -285831234679327438L;
	
	@Id private Long id;
	
	private List<Long> substituteIds = new ArrayList<Long>();
	
	private Boolean notASubstitute;
	
	private String name;
	
	private Double co2eValue;
	
	private boolean inSeason;

	public FoodProductInfo() {}
	
	/**
	 * The Constructor to create an lightweight Info out ouf a FoodProduct 
	 * Take Care: inSeason is not setted here, set manually!
	 * @param product
	 */
	public FoodProductInfo(FoodProduct product) {
		this.id = product.getId();
		this.name = product.getName();
		this.co2eValue = product.getCo2eValue().convert(Unit.GRAM).getAmount();
		this.substituteIds = product.getSubstitues();
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}


	public List<Long> getSubstituteIds() {
		return substituteIds;
	}

	public void setSubstituteIds(List<Long> substituteIds) {
		this.substituteIds = substituteIds;
	}
	
	
	public Boolean isNotASubstitute() {
		return notASubstitute;
	}
	
	public void setNotASubstitute(Boolean notASubstitute) {
		this.notASubstitute = notASubstitute;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Double getCo2eValue() {
		return co2eValue;
	}

	public void setCo2eValue(Double co2eValue) {
		this.co2eValue = co2eValue;
	}

	public boolean isInSeason() {
		return inSeason;
	}

	public void setInSeason(boolean inSeason) {
		this.inSeason = inSeason;
	}
	
}

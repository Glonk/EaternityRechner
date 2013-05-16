package ch.eaternity.shared;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;
import com.google.gwt.view.client.HasData;

import com.googlecode.objectify.annotation.Id;

public class FoodProductInfo implements Serializable {
	
	private static final long serialVersionUID = -285831234679327438L;
	
	@Id private Long id;
	
	private List<Long> substituteIds = new ArrayList<Long>();
	
	private Boolean substitute = false;
	
	private String name;
	
	/**
	 * The equivalent co2 value in [gram co2e / 100 g amount]
	 */
	private Double co2eValue;
	
	private List<String> synonyms = new ArrayList<String>();
	
	private String currentSynonym;
	
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
		// kg / kg -> g / g same, multiplicate by 100 for getting [gram co2e / 100 g amount]
		this.co2eValue = product.getCo2eValue()*100;
		this.substituteIds = product.getSubstitues();
		this.synonyms = product.getSynonyms();
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
	
	
	public Boolean isSubstitute() {
		return substitute;
	}
	
	public void setSubstitute(Boolean substitute) {
		this.substitute = substitute;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/**
	 * The equivalent co2 value in [gram co2e / 100 g amount]
	 */
	public Double getCo2eValue() {
		return co2eValue;
	}

	public void setCo2eValue(Double co2eValue) {
		this.co2eValue = co2eValue;
	}

	public List<String> getSynonyms() {
		return synonyms;
	}

	public void setSynonyms(List<String> synonyms) {
		this.synonyms = synonyms;
	}

	public String getCurrentSynonym() {
		return currentSynonym;
	}

	public void setCurrentSynonym(String currentSynonym) {
		this.currentSynonym = currentSynonym;
	}

	public boolean isInSeason() {
		return inSeason;
	}

	public void setInSeason(boolean inSeason) {
		this.inSeason = inSeason;
	}
	
	//not tested yet
	@Override
	public boolean equals(Object other) {
		if (other == null) return false;
	    if (other == this) return true;
	    if (!(other instanceof FoodProductInfo))return false;
	    if (((FoodProductInfo)other).getId().equals(this.getId())) return true;
	    else return false;
	}
	
}

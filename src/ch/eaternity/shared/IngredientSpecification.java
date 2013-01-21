
package ch.eaternity.shared;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;


import javax.jdo.annotations.Extension;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import javax.persistence.Embedded;
import javax.persistence.Id;


import com.googlecode.objectify.annotation.Serialized;




/**
 * A specification in more detail of the ingredient.
 */
@PersistenceCapable //(detachable = "true")
public class IngredientSpecification  implements Serializable, Cloneable  {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2858311250621887438L;
	
	@Id private Long id;
	
//    @PrimaryKey
//    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
//	private Long id;
//	
//    @Persistent
//    @Extension(vendorName="datanucleus", key="gae.pk-id", value="true")
//    private Long keyId;
	
	
    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    @Extension(vendorName="datanucleus", key="gae.encoded-pk", value="true")
    private String encodedKey;
    
	@Persistent
	private String name;
	
	@Persistent
	private String RezeptKey;
	
	private Long zutat_id;
	

	private int mengeGramm;
	@Serialized
	private Extraction herkunft;
	private Date cookingDate;
	
	@Embedded
	private IngredientCondition zustand;
	@Embedded
	private Production produktion;
	@Embedded
	private MoTransportation transportmittel;
	private Long label;
	private String startSeason;
	private String stopSeason;
//	private ArrayList<Long> Labels = new ArrayList<Long>();
	
	// illustrations
	private double distance; // in km
	private int NormalCO2Value;// in (Kg Co2)/Kg
	
	// no factors included
	private double co2ValueNoFactors;
	
	private double cost; 
	
	public IngredientSpecification(Long zutat_id, String name,
		 Date cookingDate,IngredientCondition symbol,Production symbol2, 
		 MoTransportation symbol3) {
		this.setName(name);
		this.setZutat_id(zutat_id);
		
		this.cookingDate = cookingDate;
		this.zustand = symbol;
		this.produktion = symbol2;
		this.transportmittel = symbol3;
//		this.setLabels(labels);
		
	}
	
	// TODO clone copy constructor for copying object, remove standart constructor...)
	public IngredientSpecification(IngredientSpecification toCopy) {
		
	}
	

	
	public IngredientSpecification(){
		
	}

	public IngredientSpecification(Long zutat_id, String name) {
		this.setZutat_id(zutat_id);
		this.setName(name);
	}

	public void setHerkunft(Extraction stdExtractionSymbol) {
		this.herkunft = stdExtractionSymbol;
	}
	
	
	public Extraction getHerkunft() {
		return herkunft;
	}
	public void setCookingDate(Date cookingDate) {
		this.cookingDate = cookingDate;
	}
	public Date getCookingDate() {
		return cookingDate;
	}
	public void setZustand(IngredientCondition zustand) {
		this.zustand = zustand;
	}
	public IngredientCondition getZustand() {
		return zustand;
	}
	public void setProduktion(Production produktion) {
		this.produktion = produktion;
	}
	public Production getProduktion() {
		return produktion;
	}
	public void setTransportmittel(MoTransportation transportmittel) {
		this.transportmittel = transportmittel;
	}
	public MoTransportation getTransportmittel() {
		return transportmittel;
	}

	public void setLabel(Long label) {
		this.label = label;
	}

	public Long getLabel() {
		return label;
	}
	public void setSeason(String i,String j) {
		this.startSeason = i;
		this.stopSeason = j;
	}

	public String getStartSeason() {
		return startSeason;
	}

	public String getStopSeason() {
		return stopSeason;
	}
	
	public Boolean hasSeason() {
		return ( startSeason != null && stopSeason != null );
	}

	public void setZutat_id(Long zutat_id) {
		this.zutat_id = zutat_id;
	}

	public Long getZutat_id() {
		return zutat_id;
	}

	public void setMengeGramm(int mengeGramm) {
		this.mengeGramm = mengeGramm;
	}

	public int getMengeGramm() {
		return mengeGramm;
	}

//	public void setLabels(ArrayList<Long> labels) {
//		Labels = labels;
//	}
//
//	public ArrayList<Long> getLabels() {
//		return Labels;
//	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
//	public void setId(Long id) {
//		this.id = id;
//	}
//	public Long getId() {
//		return id;
//	}



	public void setDistance(double distance) {
		this.distance = distance;
	}



	public double getDistance() {
		return distance;
	}
	
	public int getKmDistanceRounded() {
		int d = (int)(distance/10000);
		if (d%10 >= 5)
			d = d + 10;
		int dist = ((int)(d/10))*100;
		return dist;
	}
	
	/*
	 *  TODO this is a hack, hardcoded value. remove after all objects are updated
	 */
	public void update() {
		if(transportmittel.factor == null && transportmittel.symbol.equals("LKW")) {
			transportmittel.factor = 0.188D;
		}
	}
	

	public double calculateCo2ValueNoFactors() {
		co2ValueNoFactors = NormalCO2Value*mengeGramm/1000;
		return co2ValueNoFactors;
	}


	public double getCalculatedCO2Value() {
		// just in case its not setted yet
		calculateCo2ValueNoFactors();
		
		// sum up all parts
		return co2ValueNoFactors + getConditionQuota() + getTransportationQuota() + getProductionQuota();
	}
	
	public double getConditionQuota() {
		if(zustand != null && zustand.factor != null){
			return zustand.factor*mengeGramm;
		}
		else
			return 0.0;
	}
	
	public double getTransportationQuota() {
		if(transportmittel != null && transportmittel.factor != null){
			if(distance != 0)
				return transportmittel.factor*distance/1000000*mengeGramm;
			else
				return 0.0;
		} 
		else
			return 0.0;
	}
	
	public double getProductionQuota() {
		if(produktion != null && produktion.factor != null){
			return produktion.factor*mengeGramm;
		}
		else
			return 0.0;
	}


	public void setNormalCO2Value(int normalCO2Value) {
		NormalCO2Value = normalCO2Value;
	}



	public int getNormalCO2Value() {
		return NormalCO2Value;
	}



	public void setEncodedKey(String encodedKey) {
		this.encodedKey = encodedKey;
	}



	public String getEncodedKey() {
		return encodedKey;
	}



	public void setRezeptKey(String key) {
		RezeptKey = key;
	}



	public String getRezeptKey() {
		return RezeptKey;
	}



	public void setId(Long id) {
		this.id = id;
	}



	public Long getId() {
		return id;
	}
	
	// returns -1 if price is not set yet
	public double getCost(){
		return cost;
	}

	public void setCost(double cost)
	{
		this.cost = cost;
	}
}





package ch.eaternity.shared;

import java.io.Serializable;
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
public class ZutatSpecification  implements Serializable  {
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
	private Condition zustand;
	@Embedded
	private Production produktion;
	@Embedded
	private MoTransportation transportmittel;
	private Long label;
	private String startSeason;
	private String stopSeason;
//	private ArrayList<Long> Labels = new ArrayList<Long>();
	
	// illustrations
	private double distance;
	private int NormalCO2Value;
	
	public ZutatSpecification(Long zutat_id, String name,
		 Date cookingDate,Condition symbol,Production symbol2, 
		 MoTransportation symbol3) {
		this.setName(name);
		this.setZutat_id(zutat_id);
		
		this.cookingDate = cookingDate;
		this.zustand = symbol;
		this.produktion = symbol2;
		this.transportmittel = symbol3;
//		this.setLabels(labels);
		
	}
	

	
	public ZutatSpecification(){
		
	}

	public ZutatSpecification(Long zutat_id, String name) {
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
	public void setZustand(Condition zustand) {
		this.zustand = zustand;
	}
	public Condition getZustand() {
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


	//TODO calculate the CO2 value
	public double getCalculatedCO2Value() {
		double wert = NormalCO2Value*mengeGramm/1000;
		
		if(transportmittel != null && transportmittel.factor != null){
		if(distance != 0){
		wert += transportmittel.factor*distance/1000000*mengeGramm;
		}
		}
		
		if(zustand != null && zustand.factor != null){
		wert += zustand.factor*mengeGramm;
		}
		
		if(produktion != null && produktion.factor != null){
		wert += produktion.factor*mengeGramm;
		}

		return wert;
		
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






//	public void setId(String id) {
//		this.id = id;
//	}
//
//
//
//	public String getId() {
//		return id;
//	}



}





package ch.eaternity.shared;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;


import javax.jdo.annotations.Extension;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import ch.eaternity.shared.Zutat.Herkuenfte;
import ch.eaternity.shared.Zutat.Produktionen;
import ch.eaternity.shared.Zutat.Transportmittel;
import ch.eaternity.shared.Zutat.Zustaende;







/**
 * A specification in more detail of the ingredient.
 */
@PersistenceCapable //(detachable = "true")
public class ZutatSpecification  implements Serializable  {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2858311250621887438L;
	

	
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
	private Long RezeptKey;
	
	private Long zutat_id;
	

	private int mengeGramm;
	private Herkuenfte herkunft;
	private Date cookingDate;
	private Zustaende zustand;
	private Produktionen produktion;
	private Transportmittel transportmittel;
	private Long label;
	private String startSeason;
	private String stopSeason;
//	private ArrayList<Long> Labels = new ArrayList<Long>();
	
	// illustrations
	private double distance;
	private int NormalCO2Value;
	
	public ZutatSpecification(Long zutat_id, String name,
		 Date cookingDate,Zustaende zustand,Produktionen produktion, 
			Transportmittel transportmittel) {
		this.setName(name);
		this.setZutat_id(zutat_id);
		
		this.cookingDate = cookingDate;
		this.zustand = zustand;
		this.produktion = produktion;
		this.transportmittel = transportmittel;
//		this.setLabels(labels);
		
	}
	

	
	public ZutatSpecification(){
		
	}

	public ZutatSpecification(Long zutat_id, String name) {
		this.setZutat_id(zutat_id);
		this.setName(name);
	}

	public void setHerkunft(Herkuenfte herkuenfte) {
		this.herkunft = herkuenfte;
	}
	
	
	public Herkuenfte getHerkunft() {
		return herkunft;
	}
	public void setCookingDate(Date cookingDate) {
		this.cookingDate = cookingDate;
	}
	public Date getCookingDate() {
		return cookingDate;
	}
	public void setZustand(Zustaende zustand) {
		this.zustand = zustand;
	}
	public Zustaende getZustand() {
		return zustand;
	}
	public void setProduktion(Produktionen produktion) {
		this.produktion = produktion;
	}
	public Produktionen getProduktion() {
		return produktion;
	}
	public void setTransportmittel(Transportmittel transportmittel) {
		this.transportmittel = transportmittel;
	}
	public Transportmittel getTransportmittel() {
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
		double wert = NormalCO2Value*mengeGramm/100;
		
		if(distance != 0){
		double transportfaktor = 0;
		switch (transportmittel) {
			case Flugzeug: transportfaktor=1.20;break; 
			case  LKW: transportfaktor=0.100;break; 
			case Schiff: transportfaktor=0.010;
		}
		
		wert += transportfaktor*distance/1000000*mengeGramm;
		}
		
		double zustandfaktor = 0;
		switch (zustand) {
		case tiefgekühlt:zustandfaktor = 2;break; 
		case getrocknet:zustandfaktor = .5;break; 
		case eingemacht:zustandfaktor = 1;break; 
		case frisch:zustandfaktor = 0;

		}
		wert += zustandfaktor*mengeGramm;
		
		double produktionsfaktor = 0;
		switch(produktion){
		case biologisch: produktionsfaktor = -0.1;break; 
		case Treibhaus:produktionsfaktor = 3;break; 
		case konventionell:produktionsfaktor = 0;
		}
		wert += produktionsfaktor*mengeGramm;

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



	public void setRezeptKey(Long rezeptKey) {
		RezeptKey = rezeptKey;
	}



	public Long getRezeptKey() {
		return RezeptKey;
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




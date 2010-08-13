package ch.eaternity.shared;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


import javax.persistence.Id;

import com.googlecode.objectify.annotation.Serialized;


public class Rezept implements Serializable, Cloneable{
 

	private static final long serialVersionUID = -5888386800366492104L;

	@Id Long id;
    
	private String symbol;
	
	private String cookInstruction;
	
	public String imageUrl;
	
	private Long persons;
	
	private Date createDate;
	private Long hits;
	private Long popularity;
	
    // @Persistent //(mappedBy = "rezept") //, defaultFetchGroup = "true")
//    @Element(dependent = "true")
	@Serialized
	public ArrayList<ZutatSpecification> Zutaten = new ArrayList<ZutatSpecification>();
    
//    @Persistent 
//    private List<String> ZutatSpecificationKeys = new ArrayList<String>(); 
    
	private Double CO2Value;
	public Boolean open;

	public Rezept() {

	}

	public Rezept(String symbol) {
		this.symbol = symbol;
	}

	public Rezept(Long id, String symbol) {
		this();

		this.symbol = symbol;
	}



	public String getSymbol() {
		return this.symbol;
	}





	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public void addZutaten(List<ZutatSpecification> zutaten) {
		for(ZutatSpecification zutat : zutaten){
//			zutat.setRezept(this);
			this.Zutaten.add(zutat);
		}
		
	}

	public ArrayList<ZutatSpecification> getZutaten() {
		return this.Zutaten;
	}

	public void setZutaten(ArrayList<ZutatSpecification> zutaten) {
			this.Zutaten = zutaten;

	}
	
	public void removeZutat(int index) {
		this.Zutaten.remove(index);
	}


	public void setOpen(boolean open) {
		this.open = open;
	}

	public boolean isOpen() {
		return open;
	}

	public void setHits(Long hits) {
		this.hits = hits;
	}

	public Long getHits() {
		return hits;
	}

	public void setPopularity(Long popularity) {
		this.popularity = popularity;
	}

	public Long getPopularity() {
		return popularity;
	}
	public Rezept getRezept(){
		return this;
	}

	

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCO2Value() {
		double sum = 0;
		for ( ZutatSpecification zutatSpec : Zutaten){
			sum += zutatSpec.getCalculatedCO2Value();
		}
		
		CO2Value = sum/persons;
	}

	public double getCO2Value() {
		return CO2Value;
	}

	public void setCookInstruction(String cookInstruction) {
		this.cookInstruction = cookInstruction;
	}

	public String getCookInstruction() {
		return cookInstruction;
	}

	public void setPersons(Long persons) {
		this.persons = persons;
	}

	public Long getPersons() {
		return persons;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getId() {
		return id;
	}





}
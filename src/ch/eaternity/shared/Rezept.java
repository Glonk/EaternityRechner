package ch.eaternity.shared;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.jdo.annotations.Element;
import javax.jdo.annotations.Extension;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.NotPersistent;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable //(detachable = "true")
public class Rezept implements Serializable{
 

	private static final long serialVersionUID = -5888386800366492104L;

    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    @Extension(vendorName="datanucleus", key="gae.encoded-pk", value="true")
    private String id;
    
	private String symbol;
	private Date createDate;
	private Long hits;
	private Long popularity;
	
    // @Persistent //(mappedBy = "rezept") //, defaultFetchGroup = "true")
//    @Element(dependent = "true")
	@NotPersistent 
	public List<ZutatSpecification> Zutaten = new ArrayList<ZutatSpecification>();
    
//    @Persistent 
//    private List<String> ZutatSpecificationKeys = new ArrayList<String>(); 
    
	private double CO2Value;
	private boolean open;

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

	public List<ZutatSpecification> getZutaten() {
		return this.Zutaten;
	}

	public void setZutaten(List<ZutatSpecification> zutaten) {
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

	public void setId(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
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
		
		CO2Value = sum;
	}

	public double getCO2Value() {
		return CO2Value;
	}





}
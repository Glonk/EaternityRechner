/*
 * Copyright 2007 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package ch.eaternity.shared;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;







/**
 * A simple structure containing the basic components of an ingredient.
 */
@PersistenceCapable
public class Zutat implements Serializable {

	/**
	 * 
	 */
	
	
	private static final long serialVersionUID = 3288370850465495930L;
	
	public enum Zustaende { tiefgekühlt, getrocknet, eingemacht, frisch };
	public enum Herkuenfte { Schweiz, Deutschland, Spanien, Niederlande ,Italien };
	public enum Produktionen { biologisch, Treibhaus , konventionell};
	public enum Transportmittel { Flugzeug, LKW , Schiff};
	
    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Long id;
    @Persistent
	private String symbol;
    @Persistent
	private Date createDate = new Date();
    @Persistent
	private int CO2eWert;
    @Persistent
	private ArrayList<Long> Labels = new ArrayList<Long>();
    @Persistent
	private ArrayList<Long> Alternativen = new ArrayList<Long>();
    
    @Persistent
    private ArrayList<Herkuenfte> herkuenfte = new ArrayList<Herkuenfte>();
    
    @Persistent
	private int stdMengeGramm;
    @Persistent
	private Herkuenfte stdHerkunft;
    @Persistent
	private Zustaende stdZustand;
    @Persistent
	private Produktionen stdProduktion;
    @Persistent
	private Transportmittel stdTransportmittel;
    @Persistent
    private String stdStartSeason;
    @Persistent
	private String stdStopSeason;
    @Persistent
	private ArrayList<Long> stdLabels = new ArrayList<Long>();
	
	
  public Zutat() {
    this.createDate = new Date();
  }

  public Zutat(String symbol) {
    this.symbol = symbol;
    this.createDate = new Date();
  }

  public Zutat(Long id, String symbol, Date createDate) {
    this();
    this.id = id;
    this.symbol = symbol;
    this.createDate = createDate;
  }
  
  public Zutat(Long id,String symbol, Date createDate, int CO2eWert, List<Long> Alternativen, Herkuenfte herkunft, Zustaende zustand,Produktionen produktion, 
			Transportmittel transportmittel, int stdMengeGramm, ArrayList<Herkuenfte> herkuenfte, String sStart, String sStop) {
	    this();
	    this.id = id;
	    this.symbol = symbol;
	    this.createDate = createDate;
	    this.setCO2eWert(CO2eWert);
	    this.setAlternativen(Alternativen);
	    this.setHerkuenfte(herkuenfte);
	    this.setStdStartSeason(sStart);
	    this.setStdStopSeason(sStop);
	    this.setStdValues( herkunft,  zustand, produktion,  transportmittel , stdMengeGramm);
	  }
  public Zutat(Long id,String symbol, Date createDate, int CO2eWert) {
	    this();
	    this.id = id;
	    this.symbol = symbol;
	    this.createDate = createDate;
	    this.setCO2eWert(CO2eWert);    
	  }
  
  private void setAlternativen(List<Long> alternativen2) {
		// TODO make this to make sense
		ArrayList<Long> list = new ArrayList<Long>(alternativen2.size());  
		for (Long i : alternativen2) {  
		    list.add(i);  
		}  
		this.Alternativen = (ArrayList<Long>) list;
		
	}
  
  public void setAlternative(Long alternative) { 
		this.Alternativen.add(alternative);
	}
  
  
  public Long getId() {
      return this.id;
  }

  public String getSymbol() {
      return this.symbol;
  }

  public Date getCreateDate() {
      return this.createDate;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public void setSymbol(String symbol) {
      this.symbol = symbol;
  }




public ArrayList<Long> getAlternativen() {
	return this.Alternativen;
}


public void setLabels(ArrayList<Long> labels) {
	Labels = labels;
}

public ArrayList<Long> getLabels() {
	return Labels;
}



public void setCO2eWert(int cO2eWert) {
	CO2eWert = cO2eWert;
}

public int getCO2eWert() {
	return CO2eWert;
}

public void setAlternativen(ArrayList<Long> newAlternativen) {
	Alternativen = newAlternativen;	
}


public void setStdMengeGramm(int stdMengeGramm) {
	this.stdMengeGramm = stdMengeGramm;
}

public int getStdMengeGramm() {
	return stdMengeGramm;
}

public void setStdHerkunft(Herkuenfte stdHerkunft) {
	this.stdHerkunft = stdHerkunft;
}



public Herkuenfte getStdHerkunft() {
	return stdHerkunft;
}

public void setStdZustand(Zustaende stdZustand) {
	this.stdZustand = stdZustand;
}

public Zustaende getStdZustand() {
	return stdZustand;
}

public void setStdProduktion(Produktionen stdProduktion) {
	this.stdProduktion = stdProduktion;
}

public Produktionen getStdProduktion() {
	return stdProduktion;
}

public void setStdTransportmittel(Transportmittel stdTransportmittel) {
	this.stdTransportmittel = stdTransportmittel;
}

public Transportmittel getStdTransportmittel() {
	return stdTransportmittel;
}

public void setStdStartSeason(String stdStartSeason) {
	this.stdStartSeason = stdStartSeason;
}

public String getStdStartSeason() {
	return stdStartSeason;
}

public void setStdStopSeason(String stdStopSeason) {
	this.stdStopSeason = stdStopSeason;
}

public String getStdStopSeason() {
	return stdStopSeason;
}

public void setStdLabels(ArrayList<Long> stdLabels) {
	this.stdLabels = stdLabels;
}

public ArrayList<Long> getStdLabels() {
	return stdLabels;
}

public void setStdValues(Herkuenfte herkunft, Zustaende zustand,Produktionen produktion, 
		Transportmittel transportmittel, int stdMengeGramm) {

	this.setStdHerkunft( herkunft);
	this.stdZustand = zustand;
	this.stdProduktion = produktion;
	this.stdTransportmittel = transportmittel;
	this.stdMengeGramm = stdMengeGramm;
}

public void setHerkuenfte(ArrayList<Herkuenfte> herkuenfte) {
	this.herkuenfte = herkuenfte;
}

public ArrayList<Herkuenfte> getHerkuenfte() {
	return herkuenfte;
}

}




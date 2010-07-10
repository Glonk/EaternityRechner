package ch.eaternity.shared;

import java.io.Serializable;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;


public class Data implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -8325524250818128692L;
	private  List<Rezept> PublicRezepte;
	private  List<Rezept> YourRezepte;
	private  List<Zutat> Zutaten;

	public void setZutaten(List<Zutat> zutaten) {
		Zutaten = zutaten;
	}
	public List<Zutat> getZutaten() {
		return Zutaten;
	}
	public void setPublicRezepte(List<Rezept> publicRezepte) {
		PublicRezepte = publicRezepte;
	}
	public List<Rezept> getPublicRezepte() {
		return PublicRezepte;
	}
	public void setYourRezepte(List<Rezept> yourRezepte) {
		YourRezepte = yourRezepte;
	}
	public List<Rezept> getYourRezepte() {
		return YourRezepte;
	}
	
	public Zutat getZutatByID(long id){
		for(Zutat zutat : Zutaten){
			if (zutat.getId() == id){
				return zutat;
			}
		}
		return null;
	}
	
	
	public Set<String> getOrcaleIndex() {

		Set<String> oracleIndex = new TreeSet<String>();

		oracleIndex.add("all");

		if(this.Zutaten != null){
			for(Zutat zutat : this.Zutaten){
				if(zutat != null){
					oracleIndex.add(zutat.getSymbol());
				}
			}
		}
		if(this.PublicRezepte != null){
			for(Rezept rezept : this.PublicRezepte){
				if(rezept != null){
					oracleIndex.add(rezept.getSymbol());
				}
			}
		}
		if(this.YourRezepte != null){
			for(Rezept rezept  : this.YourRezepte){
				if(rezept != null){
					oracleIndex.add(rezept.getSymbol());
				}
			}
		}

		return oracleIndex;
	}

}

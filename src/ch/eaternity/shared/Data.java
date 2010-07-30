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
	private  List<Ingredient> ingredients;
	private  List<SingleDistance> distances;

	public Set<String> getOrcaleIndex() {

		Set<String> oracleIndex = new TreeSet<String>();

		oracleIndex.add("all");

		if(this.ingredients != null){
			for(Ingredient zutat : this.ingredients){
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
	
	public Ingredient getIngredientByID(long id){
		for(Ingredient zutat : ingredients){
			if (zutat.getId() == id){
				return zutat;
			}
		}
		return null;
	}
	
	
	public void setDistances(List<SingleDistance> distances) {
		this.distances = distances;
	}
	public List<SingleDistance> getDistances() {
		return distances;
	}
	public void setIngredients(List<Ingredient> ingredients) {
		this.ingredients = ingredients;
	}
	public List<Ingredient> getIngredients() {
		return ingredients;
	}


}

package ch.eaternity.shared;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Util {
	static public Date removeTime(Date date) {    
	    Calendar cal = Calendar.getInstance();  
	    cal.setTime(date);  
	    cal.set(Calendar.HOUR_OF_DAY, 0);  
	    cal.set(Calendar.MINUTE, 0);  
	    cal.set(Calendar.SECOND, 0);  
	    cal.set(Calendar.MILLISECOND, 0);  
	    return cal.getTime(); 
	}
	
	// ----------------------- Mathematic Methods -----------------------
	
	static public Double getMax(Collection<Double> values) {
		double max = 0.0;
		for (Double value : values) {
			if(value > max)
				max = value;
		}
		return max;
	}
	
	static public Double getMaxCO2Value(Collection<CO2Value> values) {
		double max = 0.0;
		for (CO2Value value : values) {
			if(value.totalValue > max)
				max = value.totalValue;
		}
		return max;
	}
	
	static public Double getAverage(Collection<Double> values) {
		Double average = 0D;
		for (Double value : values) {
			average = average + value;
		}
		average = average / values.size();
		return average;
	}
	
	static public Double getMedian(List<Double> values) {
		Collections.sort(values);
		Double median = 0D;
		
	    if (values.size() % 2 == 1)
	    	median = values.get((values.size()+1)/2-1);
	    else
	    {
	    	double lower;
			if (values.size() >= 1)
				lower = values.get(values.size()/2-1);
			else
				lower = values.get(values.size()/2);
			
			double upper = (values.get(values.size()/2));				

			median = ((lower + upper) / 2.0);
	    }
	    return median;
	}
	
	// ----------------------- IngSpec Iteration Methods -----------------------
	
	static public CO2Value getCO2Value(Collection<IngredientSpecification> ingsSpecs) {
		CO2Value co2value = new CO2Value(0.0,0.0,0.0,0.0,0.0);
		for (IngredientSpecification ingSpec : ingsSpecs) {
			co2value.condQuota = co2value.condQuota + ingSpec.getConditionQuota();
			co2value.transQuota = co2value.transQuota + ingSpec.getTransportationQuota();
			co2value.prodQuota = co2value.prodQuota + ingSpec.getProductionQuota();
			co2value.noFactorsQuota = co2value.noFactorsQuota + ingSpec.calculateCo2ValueNoFactors();
			co2value.totalValue = co2value.totalValue + ingSpec.getCalculatedCO2Value();
		}
		return co2value;
	}
	
	static public Collection<CO2Value> getCO2Values(Collection<IngredientSpecification> ingsSpecs) {
		Collection<CO2Value> values = new ArrayList<CO2Value>();
		for (IngredientSpecification ingSpec : ingsSpecs) {
			values.add(new CO2Value(ingSpec));
		}
		return values;
	}
	

	
	static public Double getWeight(Collection<IngredientSpecification> ingredientsSpecifications) {
		Double amount = 0.0;
		for (IngredientSpecification ingredientSpecification : ingredientsSpecifications) {
			amount = amount + ingredientSpecification.getMengeGramm();
		}
		return amount;
	}
	
	static public Double getCost(Collection<IngredientSpecification> ingredientsSpecifications) {
		Double cost = 0.0;
		for (IngredientSpecification ingredientSpecification : ingredientsSpecifications) {
			cost = cost + ingredientSpecification.getCost();
		}
		return cost;
	}
	
	
	// how many of the vegetables and fruits are seasonal, fresh from switzerland
	// lies between zero and one
	static public Pair<Double, Double> getSeasonQuotient(Collection<IngredientSpecification> ingSpecs) {
		Integer numFruitsAndVegetables = 0;
		Integer numAreSeasonal = 0;
		Double seasonalWeight = 0.0;
		Double totalWeight = 0.0;
		for(IngredientSpecification ingSpec: ingSpecs) {
			if (ingSpec.getCookingDate() != null && ingSpec.hasSeason()) {
				numFruitsAndVegetables++;
				totalWeight =  totalWeight + ingSpec.getMengeGramm();
				
				SeasonDate dateStart = new SeasonDate();
				dateStart.setDate(ingSpec.getStartSeason());
				SeasonDate dateStop = new SeasonDate();
				dateStop.setDate(ingSpec.getStopSeason());
				SeasonDate dateCook = new SeasonDate();
				dateCook.setDate(ingSpec.getCookingDate());
				// have season?
				if (dateCook.after(dateStart) && dateCook.before(dateStop)) {
					// are fresh from switzerland
					if (ingSpec.getHerkunft().symbol.equals("Schweiz") && ingSpec.getZustand().symbol.equals("frisch")) {
						numAreSeasonal++;
						seasonalWeight = seasonalWeight + ingSpec.getMengeGramm();
					}
				}
			}
		}

		Pair<Double,Double> pair = new Pair<Double,Double>(numAreSeasonal.doubleValue()/numFruitsAndVegetables.doubleValue(), seasonalWeight/totalWeight);
		return pair;
	}
	
	//returns null if not found
	static public Ingredient getIngredient(IngredientSpecification ingspec, Collection<Ingredient> ingredients) {
		return getIngredient(ingspec.getZutat_id(), ingredients);
	}
	
	//returns null if not found
	static public Ingredient getIngredient(Long id, Collection<Ingredient> ingredients){
		for(Ingredient zutat : ingredients){
			if (zutat.getId().equals(id)){
				return zutat;
			}
		}
		return null;
	}
	
	// ----------------------- Language Methods -----------------------
	
	static public Set<String> getIngredientsNames_de(Collection<IngredientSpecification> ingSpecs){
		Set<String> names = new HashSet<String>();
		for (IngredientSpecification ingSpec : ingSpecs){
			names.add(ingSpec.getName());
		}
		
		return names;
	}
	
	static public Set<String> getIngredientsNames_en(Collection<IngredientSpecification> ingSpecs, Collection<Ingredient> ingredients){
		Set<String> names = new HashSet<String>();
		for (IngredientSpecification ingSpec : ingSpecs){
				names.add(getIngredientName_en(ingSpec, ingredients));
		}	
		return names;
	}
	
	static public String getIngredientName_en(IngredientSpecification ingSpec,Collection<Ingredient> ingredients){
		Ingredient ing = getIngredient(ingSpec, ingredients);
		if (ing == null)
			return ingSpec.getName();
		else if (ing.getSymbol_en() == null)
			return ingSpec.getName() + "(no eng)";
		else
			return ing.getSymbol_en();
	}

}

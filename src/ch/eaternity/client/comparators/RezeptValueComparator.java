package ch.eaternity.client.comparators;

import java.util.Comparator;
import java.util.List;

import ch.eaternity.shared.Recipe;
import ch.eaternity.shared.IngredientSpecification;

public class RezeptValueComparator implements Comparator<Recipe> {
	public int compare(Recipe r1, Recipe r2) {
		Double o1 = getRezeptCO2(r1.getZutaten(),r1.getPersons());
		Double o2 = getRezeptCO2(r2.getZutaten(),r2.getPersons());

		return -Double.valueOf(o2).compareTo(Double.valueOf(o1));

	}
	private Double getRezeptCO2(List<IngredientSpecification> Zutaten, Long persons) {
		Double MenuLabelWert = 0.0;
		for (IngredientSpecification zutatSpec : Zutaten) { 
			MenuLabelWert +=zutatSpec.getCalculatedCO2Value();

		}
		return MenuLabelWert/persons;
	}
}
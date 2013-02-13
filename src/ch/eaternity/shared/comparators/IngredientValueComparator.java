package ch.eaternity.shared.comparators;

import java.util.Comparator;

import ch.eaternity.shared.IngredientSpecification;

public class IngredientValueComparator implements Comparator<IngredientSpecification> {
	public int compare(IngredientSpecification r1, IngredientSpecification r2) {
		Double o1 = r1.getCalculatedCO2Value();
		Double o2 = r2.getCalculatedCO2Value();

		return Double.valueOf(o2).compareTo(Double.valueOf(o1));

	}
}
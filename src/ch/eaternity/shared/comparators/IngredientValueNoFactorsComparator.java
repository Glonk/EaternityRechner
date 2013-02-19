package ch.eaternity.shared.comparators;

import java.util.Comparator;

import ch.eaternity.shared.IngredientSpecification;

public class IngredientValueNoFactorsComparator implements Comparator<IngredientSpecification> {
	public int compare(IngredientSpecification r1, IngredientSpecification r2) {
		Double o1 = r1.calculateCo2ValueNoFactors();
		Double o2 = r2.calculateCo2ValueNoFactors();

		return Double.valueOf(o2).compareTo(Double.valueOf(o1));

	}
}
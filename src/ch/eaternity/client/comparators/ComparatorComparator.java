package ch.eaternity.client.comparators;

import java.util.Comparator;

import ch.eaternity.client.comparators.ComparatorRecipe;

public class ComparatorComparator implements Comparator<ComparatorRecipe> {
	public int compare(ComparatorRecipe z1, ComparatorRecipe z2) {
		Double o1 = z1.recipe.getCO2Value();
		Double o2 = z2.recipe.getCO2Value();
		return Double.valueOf(o2).compareTo(Double.valueOf(o1));
	}
}
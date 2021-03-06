package ch.eaternity.shared.comparators;

import java.util.Comparator;

import ch.eaternity.shared.Ingredient;

public class NameComparator implements Comparator<Ingredient> {
	public int compare(Ingredient z1, Ingredient z2) {
		String o1 = z1.getSymbol();
		String o2 = z2.getSymbol();
		if(o1 instanceof String && o2 instanceof String) {
			String s1 = (String)o1;
			String s2 = (String)o2;
			s1 = s1.substring(0, 1);
			s2 = s2.substring(0, 1);
			return s1.compareToIgnoreCase(s2);
		}
		return 0;
	}
}
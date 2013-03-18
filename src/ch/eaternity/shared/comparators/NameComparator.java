package ch.eaternity.shared.comparators;

import java.util.Comparator;
import java.util.Locale;

import ch.eaternity.shared.FoodProduct;

public class NameComparator implements Comparator<FoodProduct> {
	
	Locale locale;
	
	public NameComparator(Locale locale) {
		this.locale = locale;
	}
	
	public int compare(FoodProduct z1, FoodProduct z2) {
		String o1 = z1.getName(locale);
		String o2 = z2.getName(locale);
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
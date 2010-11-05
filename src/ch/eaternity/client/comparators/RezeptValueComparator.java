package ch.eaternity.client.comparators;

import java.util.Comparator;
import java.util.List;

import ch.eaternity.shared.Rezept;
import ch.eaternity.shared.ZutatSpecification;

public class RezeptValueComparator implements Comparator<Rezept> {
	public int compare(Rezept r1, Rezept r2) {
		Double o1 = getRezeptCO2(r1.getZutaten());
		Double o2 = getRezeptCO2(r2.getZutaten());

		return -Double.valueOf(o2).compareTo(Double.valueOf(o1));

	}
	private Double getRezeptCO2(List<ZutatSpecification> Zutaten) {
		Double MenuLabelWert = 0.0;
		for (ZutatSpecification zutatSpec : Zutaten) { 
			MenuLabelWert +=zutatSpec.getCalculatedCO2Value();

		}
		return MenuLabelWert;
	}
}
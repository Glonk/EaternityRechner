package ch.eaternity.shared.comparators;

import java.util.Comparator;

import ch.eaternity.shared.FoodProduct;
import ch.eaternity.shared.Quantity;
import ch.eaternity.shared.Unit;

public class ValueComparator implements Comparator<FoodProduct> {
	  public int compare(FoodProduct z1, FoodProduct z2) {
		  Quantity o1 = z1.getCo2eValue();
		  Quantity o2 = z2.getCo2eValue();
		  
	    return -Double.valueOf(o2.convert(Unit.GRAM).getAmount()).compareTo(Double.valueOf(o1.convert(Unit.GRAM).getAmount()));
	  }
	}
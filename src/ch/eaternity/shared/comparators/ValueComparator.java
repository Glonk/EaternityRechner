package ch.eaternity.shared.comparators;

import java.util.Comparator;

import ch.eaternity.shared.FoodProduct;
import ch.eaternity.shared.Quantity;
import ch.eaternity.shared.Unit;

public class ValueComparator implements Comparator<FoodProduct> {
	  public int compare(FoodProduct z1, FoodProduct z2) {
		  Double o1 = z1.getCo2eValue();
		  Double o2 = z2.getCo2eValue();
		  
	    return -Double.valueOf(o2).compareTo(Double.valueOf(o1));
	  }
	}
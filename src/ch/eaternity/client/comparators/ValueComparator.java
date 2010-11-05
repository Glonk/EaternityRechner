package ch.eaternity.client.comparators;

import java.util.Comparator;

import ch.eaternity.shared.Ingredient;

public class ValueComparator implements Comparator<Ingredient> {
	  public int compare(Ingredient z1, Ingredient z2) {
		  long o1 = z1.getCo2eValue();
		  long o2 = z2.getCo2eValue();
		  
	    return -Long.valueOf(o2).compareTo(Long.valueOf(o1));
	  }
	}
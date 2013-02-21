package ch.eaternity.shared;

import java.io.Serializable;

public class Quantity implements Serializable{

	private static final long serialVersionUID = -285831125345678438L;
	
	public static enum Weight {
		MILLIGRAM(1000), GRAM(1), KILOGRAM(0.001), TON(0.000001);
		
		// Conversion to standard Unit gram, Unit[enum] * conversionFactor = result[g]
		public double conversionFactor;
		
		private Weight(double conversionFactor) {
			this.conversionFactor = conversionFactor;
		}
		
		@Override
		public String toString() {
		   String s = "";
		   switch (this) {
			   case MILLIGRAM:	
				   s = "mg";
				   break;
			   case GRAM: 
				   s = "g";
				   break;
			   case KILOGRAM: 
				   s = "kg";
				   break;
			   case TON: 
				   s = "t";
				   break;
		   }
		   return s;
		}
	}
	
}

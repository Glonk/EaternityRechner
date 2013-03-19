package ch.eaternity.shared;

public enum Unit {
	
	// base for mass is kilogram
	TON(PhysicalDimension.MASS, 0.001), KILOGRAM(PhysicalDimension.MASS, 1d), METRIC_POUND(PhysicalDimension.MASS, 2d), GRAM(PhysicalDimension.MASS, 1000d), MILlIGRAM(PhysicalDimension.MASS, 1000000d), MIKROGRAM(PhysicalDimension.MASS, 1000000000d), 
	POUND(PhysicalDimension.MASS, 2.2046226218488), OUNCE(PhysicalDimension.MASS, 35.27396194958), GRAIN(PhysicalDimension.MASS, 15432.358352941),
	
	// base unit for volume is liter
	HEKTOLITRE(PhysicalDimension.VOLUME, 0.01), 
	LITRE(PhysicalDimension.VOLUME, 1d), 
	DECILITRE(PhysicalDimension.VOLUME, 10d), 
	CENTILITRE(PhysicalDimension.VOLUME, 100d),
	BARREL(PhysicalDimension.VOLUME, 0.0062848352758387), 
	GALLON_LIQUID(PhysicalDimension.VOLUME, 0.26417205235815), GALLON_DRY(PhysicalDimension.VOLUME, 0.22702074606721), 
	CUP_LIQUID(PhysicalDimension.VOLUME, 4.2267528377304), CUP_DRY(PhysicalDimension.VOLUME, 3.6323319370754), 
	GILL_LIQUID(PhysicalDimension.VOLUME, 8.4535056754607), GILL_DRY(PhysicalDimension.VOLUME, 7.2646638741508),
	
	TABLESPOON_LIQUID(PhysicalDimension.VOLUME, 67.6280454036864), TABLESPOON_DRY(PhysicalDimension.VOLUME, 58.1173109932064), 
	TEASPOON_LIQUID(PhysicalDimension.VOLUME, 202.8841362110592), TEASPOON_DRY(PhysicalDimension.VOLUME, 174.3519329796192),
	
	// base unit is kilometer 
	KILOMETER(PhysicalDimension.DISTANCE, 1d), METER(PhysicalDimension.DISTANCE, 1000d),
	YARD(PhysicalDimension.DISTANCE, 0.00091440), MILE(PhysicalDimension.DISTANCE, 1.6093),
	NAUTICAL_MILE(PhysicalDimension.DISTANCE, 1.852),
	
	// base unit for energy is kWh
	KILOWATTHOUR(PhysicalDimension.ENERGY, 1d), 
	WATTHOUR(PhysicalDimension.ENERGY, 1000d), 
	JOULE(PhysicalDimension.ENERGY, 3600000d), 
	KILOCALORIES(PhysicalDimension.ENERGY, 859.84522785899),
	BRITISH_THERMAL_UNIT(PhysicalDimension.ENERGY, 3412.1416331279),
	
	CO2E(PhysicalDimension.GHG, 1d), CO2(PhysicalDimension.GHG, 1d), CH4(PhysicalDimension.GHG, 25d), N2O(PhysicalDimension.GHG, 298d),  
	
	// these are not really physical units, just used in recipes. We might want to handle them differently
	PIECE(PhysicalDimension.OTHER, 0d), SLICE(PhysicalDimension.OTHER, 0d), NONE(PhysicalDimension.OTHER, 0d);
	
	// The physical dimension measured with this unit, e.g. mass, volume, energy
	private PhysicalDimension dimension;
	
	// the conversion factor to convert this unit into its base unit
	protected Double factor;
	
	/**
	 * 
	 * @param dimension The dimension used (hardcoded as String)
	 * @param factor The conversion factor to convert into the base unit
	 */
	private Unit(PhysicalDimension dimension, Double factor){
		this.dimension = dimension;
		this.factor = factor;
	}
	
	/**
	 * 
	 * @return Returns the physical dimension of this unit as a String
	 */
	public PhysicalDimension getDimension(){
		return this.dimension;
	}
	
	/**
	 * @param unit The unit to convert to
	 * @return true if the target unit has the same dimension as this unit and UnitDimension is NOT OTHER, false otherwise
	 */
	public boolean isConvertable(Unit unit) {
		return this.dimension.equals(unit.getDimension()) && !this.dimension.equals(PhysicalDimension.OTHER);
	}

	/**
	 * 
	 * @param value The value (amount) of this unit to be converted
	 * @param targetUnit The unit to convert to
	 * @return The value (amount) for the target unit
	 * @throws IllegalArgumentException if units (source / target) are not convertable
	 */
	public Double convert(Double value, Unit targetUnit) throws IllegalArgumentException {
		if(!this.isConvertable(targetUnit)){
			throw new IllegalArgumentException("Cannot convert " + targetUnit + " into " + this + " since the describe different dimensions of our world.");
		}
		return value * this.getConversionFactor(targetUnit);
	}
	
	/**
	 * 
	 * @param targetUnit The unit into which to convert using the factor returned
	 * @return The factor used to convert into the targetUnit
	 * @throws IllegalArgumentException thrown if units are not convertable
	 */
	public Double getConversionFactor(Unit targetUnit) throws IllegalArgumentException{
		if(!this.isConvertable(targetUnit)){
			throw new IllegalArgumentException("Cannot convert " + targetUnit + " into " + this + " since the describe different dimensions of our world.");
		}
		return  targetUnit.factor / this.factor;
	}

}
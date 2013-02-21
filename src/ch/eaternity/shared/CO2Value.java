package ch.eaternity.shared;


// co2 equivalent in g 
public class CO2Value {
	public double prodQuota;
	public double transQuota;
	public double condQuota;
	public double noFactorsQuota;
	public double totalValue;
	
	public CO2Value() {
		this.prodQuota = 0.0;
		this.transQuota = 0.0;
		this.condQuota = 0.0;
		this.noFactorsQuota = 0.0;
		this.totalValue = 0.0;
	}
	
	public CO2Value(double totalValue) {
		this.totalValue = totalValue;
	}
	
	public CO2Value(double prodQuota, double transQuota, double condQuota, double noFactorsQuota, double totalValue) {
		this.prodQuota = prodQuota;
		this.transQuota = transQuota;
		this.condQuota = condQuota;
		this.noFactorsQuota = noFactorsQuota;
		this.totalValue = totalValue;
	}
	
	public CO2Value add(CO2Value other) {
		CO2Value sum = new CO2Value();
		sum.prodQuota = this.prodQuota + other.prodQuota;
		sum.transQuota = this.transQuota + other.transQuota;
		sum.condQuota = this.condQuota + other.condQuota;
		sum.noFactorsQuota = this.noFactorsQuota + other.noFactorsQuota;
		sum.totalValue = this.totalValue + other.totalValue;
		return sum;
	}
	
	
	public CO2Value(IngredientSpecification ingSpec) {
		this.prodQuota = ingSpec.getProductionQuota();
		this.transQuota = ingSpec.getTransportationQuota();
		this.condQuota = ingSpec.getConditionQuota();
		this.noFactorsQuota = ingSpec.calculateCo2ValueNoFactors();
		this.totalValue = ingSpec.getCalculatedCO2Value();
	}
	
	public CO2Value mult(Double factor) {
		CO2Value mult = new CO2Value();
		mult.prodQuota = this.prodQuota*factor;
		mult.transQuota = this.transQuota*factor;
		mult.condQuota = this.condQuota*factor;
		mult.noFactorsQuota = this.noFactorsQuota*factor;
		mult.totalValue = this.totalValue*factor;
		return mult;
	}

}

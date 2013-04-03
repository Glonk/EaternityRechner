package ch.eaternity.shared;

import java.io.Serializable;

import com.google.gwt.user.client.rpc.IsSerializable;

public class Quantity implements Serializable {
	
	private static final long serialVersionUID = -2232345677325678L;
	
	private Unit unit;
	private Double amount;
	
	/**
	 * Empty Constructor needed for objectify
	 */
	public Quantity() {}
	
	public Quantity(Double amount, Unit unit){
		this.unit = unit;
		this.amount = amount;
	}
	
	/**
	 * Copy constructor
	 */
	public Quantity(Quantity other) {
		unit = other.getUnit();
		amount = new Double(amount);
	}

	public Double getAmount() {
		return this.amount;
	}


	public Unit getUnit() {
		return this.unit;
	}


	public Quantity convert(Unit unit) {
		Quantity result = new Quantity(this.unit.convert(this.getAmount(), unit), unit);
		return result;
	}


	public void setAmount(double amount) {
		this.amount = amount;
	}


	public void setUnit(Unit unit) {
		this.unit = unit;
	}
}

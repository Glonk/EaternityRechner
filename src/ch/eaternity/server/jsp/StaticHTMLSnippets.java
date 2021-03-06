package ch.eaternity.server.jsp;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Collection;

import ch.eaternity.server.jsp.StaticProperties.ValueType;
import ch.eaternity.shared.CO2Value;
import ch.eaternity.shared.Util;

public class StaticHTMLSnippets {
	static public DecimalFormat formatter = new DecimalFormat("##");
	
	static public String getCo2ValueBar(Collection<CO2Value> allValues, CO2Value value, int totalLength, ValueType valueType) {
		String html = "";
		formatter.setRoundingMode(RoundingMode.FLOOR);
		Double max = Util.getMaxCO2Value(allValues);
		if (valueType == ValueType.EXPANDED) {
			if (value.noFactorsQuota > 0.0) 
				html = html + "<img class='bar' src='gray.png' alt='gray' height='11'  width=" + formatter.format(totalLength/max*value.noFactorsQuota - 1) + " />";
			if (value.transQuota > 0.0) 
				html = html + "<img class='bar' src='orange.png' alt='orange' height='11'  width=" + formatter.format(totalLength/max*value.transQuota - 1) + " />";
			if (value.condQuota > 0.0) 
				html = html + "<img class='bar' src='green.png' alt='green' height='11'  width=" + formatter.format(totalLength/max*value.condQuota - 1) + " />";
			if (value.prodQuota > 0.0) 
				html = html + "<img class='bar' src='light-gray.png' alt='light-gray' height='11'  width=" + formatter.format(totalLength/max*value.prodQuota - 1) + " />";
		}
		else {
			html = "";
			html = html +  "<img class='bar' src='gray.png' alt='gray' height='11'  width=" + formatter.format(totalLength/max*value.totalValue) + " />";
		}
		return html;
	}

	
	static public String getNormalisedLength(Double val, Collection<Double> values) {
		
		return formatter.format(val/Util.getMax(values)*200);
	}
}

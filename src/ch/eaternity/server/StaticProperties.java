package ch.eaternity.server;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;



public class StaticProperties implements Serializable {

	private static final long serialVersionUID = 528832345466492104L;
	
	// -------------------------- public Fields -------------------------
	
	// Here all the Standard values are setted
	public Locale locale = Locale.GERMAN;

	public DecimalFormat formatter = new DecimalFormat("##");
	public DecimalFormat co2_formatter = new DecimalFormat("##");
	public DecimalFormat cost_formatter = new DecimalFormat("##");
	public DecimalFormat weight_formatter = new DecimalFormat("##.#");
	public DecimalFormat distance_formatter = new DecimalFormat("##");
	public SimpleDateFormat dateFormatter = new SimpleDateFormat("dd.MMMM yyyy");
	
	public int co2BarLength = 180;
	public int barOffset = 45;

	public String BASEURL;
	public String tempIds;
	public String permanentId;
	public String kitchenId;
	public String pdfStr;
	public String thresholdStr;
	public String extraStr;
	public String personsStr;
	
	// standard values not setted in StaticPageService
	public Boolean doPdf = false;
	public Integer threshold = 1550;
	public Integer extra = 0;
	public Integer persons = 4;
	
	// -------------------------- public Methods -------------------------
	
	public StaticProperties() {}
	
	public void initialize(HttpServletRequest request) {
		BASEURL = request.getRequestURL().toString();
		tempIds = request.getParameter("ids");
		permanentId = request.getParameter("pid");
		kitchenId = request.getParameter("kid");
		pdfStr = request.getParameter("pdf");
		thresholdStr = request.getParameter("median");
		extraStr = request.getParameter("extra");
		personsStr = request.getParameter("persons");
		
		// parse request parameters
		try {
			if(pdfStr != null)
				doPdf = true;

			if(thresholdStr != null)
				threshold = Integer.valueOf(thresholdStr);
			
			if(extraStr != null)
				extra = Integer.valueOf(extraStr);
			
			if(personsStr != null)
				persons = Integer.valueOf(personsStr);
		}
		catch (NumberFormatException nfe) {}
		
	}
	
	public Locale getLocale() {
		return locale;
	}
	public void setLocale(Locale locale) {
		this.locale = locale;
	}
	public DecimalFormat getFormatter() {
		return formatter;
	}
	public void setFormatter(DecimalFormat formatter) {
		this.formatter = formatter;
	}
	public DecimalFormat getCo2_formatter() {
		return co2_formatter;
	}
	public void setCo2_formatter(DecimalFormat co2_formatter) {
		this.co2_formatter = co2_formatter;
	}
	public DecimalFormat getCost_formatter() {
		return cost_formatter;
	}
	public void setCost_formatter(DecimalFormat cost_formatter) {
		this.cost_formatter = cost_formatter;
	}
	public DecimalFormat getWeight_formatter() {
		return weight_formatter;
	}
	public void setWeight_formatter(DecimalFormat weight_formatter) {
		this.weight_formatter = weight_formatter;
	}
	public DecimalFormat getDistance_formatter() {
		return distance_formatter;
	}
	public void setDistance_formatter(DecimalFormat distance_formatter) {
		this.distance_formatter = distance_formatter;
	}
	public SimpleDateFormat getDateFormatter() {
		return dateFormatter;
	}
	public void setDateFormatter(SimpleDateFormat dateFormatter) {
		this.dateFormatter = dateFormatter;
	}
	public int getCo2BarLength() {
		return co2BarLength;
	}
	public void setCo2BarLength(int co2BarLength) {
		this.co2BarLength = co2BarLength;
	}
	public int getBarOffset() {
		return barOffset;
	}
	public void setBarOffset(int barOffset) {
		this.barOffset = barOffset;
	}
	public Boolean getDoPdf() {
		return doPdf;
	}
	public void setDoPdf(Boolean doPdf) {
		this.doPdf = doPdf;
	}
	public Integer getThreshold() {
		return threshold;
	}
	public void setThreshold(Integer threshold) {
		this.threshold = threshold;
	}
	public Integer getExtra() {
		return extra;
	}
	public void setExtra(Integer extra) {
		this.extra = extra;
	}
	public Integer getPersons() {
		return persons;
	}
	public void setPersons(Integer persons) {
		this.persons = persons;
	}
}

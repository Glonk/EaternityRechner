package ch.eaternity.server.servlets;

import java.io.IOException;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ch.eaternity.server.jsp.StaticDataLoader;
import ch.eaternity.server.jsp.StaticProperties;
import ch.eaternity.server.jsp.StaticTemp;
import ch.eaternity.shared.Quantity.Weight;

public class ReportMenuServlet extends HttpServlet 
{
	private static final long serialVersionUID = -336259876537718L;
	
	
	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse respose)
	        throws ServletException, IOException  {
	}
		
	
	@Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    		throws ServletException, IOException {
		
		StaticProperties props = new StaticProperties();
		StaticDataLoader data = new StaticDataLoader();
		StaticTemp temp = new StaticTemp();
		
		props.locale = Locale.GERMAN;

		props.weightUnit = Weight.GRAM;
		props.co2Unit = Weight.GRAM;		
				
		props.formatter = new DecimalFormat("##");
		props.formatter.setRoundingMode(RoundingMode.FLOOR);
		props.co2_formatter = new DecimalFormat("##");
		props.cost_formatter = new DecimalFormat("##");
		props.weight_formatter = new DecimalFormat("##");
		props.distance_formatter = new DecimalFormat("##");
		props.dateFormatter = new SimpleDateFormat("dd.MMMM yyyy");
		
		props.co2BarLength = 200;
		props.barOffset = 70;
		
		// standard values for request if not set
		props.doPdf = false;
		props.average = 1550;
		props.extra = 0;
		props.persons = 4;
		
		props.valueType = StaticProperties.ValueType.COMPACT;
		props.ingredientRepresentation = StaticProperties.IngredientRepresentation.EXPANDED;
		
		props.initialize(request);
		
		data.initialize(props,false);
		
		request.setAttribute("data", data);
		request.setAttribute("props", props);
		request.setAttribute("temp", temp);
		request.getRequestDispatcher("/report_menu.jsp").forward(request, response);
	}
}

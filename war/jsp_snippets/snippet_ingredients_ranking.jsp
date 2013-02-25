<%@ page contentType="text/html;charset=UTF-8" language="java" %>


<%@ page import="ch.eaternity.shared.IngredientSpecification" %>
<%@ page import="ch.eaternity.shared.comparators.IngredientValueComparator" %>
<%@ page import="ch.eaternity.shared.Util" %>
<%@ page import="ch.eaternity.shared.CO2Value" %>

<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.Collection" %>
<%@ page import="java.util.Collections" %>
<%@ page import="java.text.DecimalFormat" %>

<%@ page import="ch.eaternity.server.jsp.StaticDataLoader" %>
<%@ page import="ch.eaternity.server.jsp.StaticProperties" %>
<%@ page import="ch.eaternity.server.jsp.StaticTemp" %>
<%@ page import="ch.eaternity.server.jsp.StaticHTMLSnippets" %>

     
	
<%
     		/* uses following variables in Temp:
     		- ingredients
     		- personFactor
     		- startIndex
     		- stopIndex
     	*/

     	StaticProperties props = (StaticProperties)request.getAttribute("props");
     	StaticDataLoader data = (StaticDataLoader)request.getAttribute("data");
     	StaticTemp temp = (StaticTemp)request.getAttribute("temp");


     	List<IngredientSpecification> ingredients = temp.getIngredients();
     	Collection<CO2Value> values = new ArrayList<CO2Value>();
     	int counterIterate = 0;

     	Collections.sort(ingredients, new IngredientValueComparator());

     	if (!(props.weightThreshold > 0.0 || props.valueThreshold > 0.0)) {
     		if (temp.getStartIndex() >= 0 && temp.getStartIndex() < ingredients.size() && temp.getStopIndex() >= temp.getStartIndex() && temp.getStopIndex() <= ingredients.size())
     			ingredients = ingredients.subList(temp.getStartIndex(),temp.getStopIndex());
     	}

     	for(IngredientSpecification ingSpec : ingredients){
     		values.add((new CO2Value(ingSpec)).mult(temp.getPersonFactor()));
     	}


     	if (ingredients.size() > 0 && temp.getPersonFactor() > 0.0) {
     	%>
	<table cellspacing="0" cellpadding="0" class="table toc" >
		
		<tr>
		<td></td>
		<td class="gray left-border"></td>
		<td class="gray co2label"><span class="nowrap"><%= props.co2Unit %> CO<sub>2</sub>*</span></td>
		<td></td>
		</tr>
		
		<tr>
			<td class="table-header bottom-border">
			<% if (props.valueThreshold > 0.0 || props.weightThreshold > 0.0 ) { %>
				Zutatenranking 
			<% }
			else { %>
			Top <%= ingredients.size() %> CO<sub>2</sub>-intensive Zutaten in diesem Rezept <% } %>
			</td>
			<td class="left-border"></td>
			<td class="co2value" ></td>
			<td ></td>
		</tr>
		
		
		<%
		for(IngredientSpecification ingSpec : ingredients){ 
			if (ingSpec.getMengeGramm() > Util.getWeight(ingredients)/100.0*props.weightThreshold || ingSpec.calculateCo2ValueNoFactors() > Util.getCO2Value(ingredients).noFactorsQuota/100.0*props.valueThreshold) {
				%>
				<tr <%
				int order = (ingredients.indexOf(ingSpec) - counterIterate ) % 2; 
				if(order == 1) { %>
				class="alternate"
				<% }%> > 
				<td class="menu-name">
				<%= ingSpec.getName() %> (<%= props.weight_formatter.format(ingSpec.getMengeGramm()*props.weightUnit.conversionFactor*temp.getPersonFactor()) + " " + props.weightUnit + ")" %><% if (ingSpec.getCost() > 0) { %> (<%= props.cost_formatter.format(ingSpec.getCost()*temp.getPersonFactor()) %> CHF) <% } %>
				</td>
				<td class="left-border" width="<%= props.co2BarLength + props.barOffset %>px"><%= StaticHTMLSnippets.getCo2ValueBar(values, (new CO2Value(ingSpec)).mult(temp.getPersonFactor()), props.co2BarLength, props.valueType) %></td>
				<td class="co2value" ><%= props.co2_formatter.format(ingSpec.getCalculatedCO2Value()*props.co2Unit.conversionFactor*temp.getPersonFactor()) %></td>
			
				</tr>
		
			<%
			}
		}%>
		</table>
	
	
<%		
}
else { %>
Keine Zutat oder keinen PersonFactor gesetzt in TempBean.
<% }
%>
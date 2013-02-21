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
<%@ page import="ch.eaternity.server.jsp.StaticHTMLSnippets" %>




<jsp:useBean id="vars" scope="session"
     class="ch.eaternity.server.jsp.StaticDataLoader" />
     
<jsp:useBean id="temp" scope="session"
	class="ch.eaternity.server.jsp.StaticTempBean" />
     
<jsp:useBean id="properties" scope="session"
	class="ch.eaternity.server.jsp.StaticProperties" />
	
<%

List<IngredientSpecification> ingredients = temp.getIngredients();
Collection<CO2Value> values = new ArrayList<CO2Value>();
int counterIterate = 0;

Collections.sort(ingredients, new IngredientValueComparator());


List<IngredientSpecification> ingTop = ingredients.subList(temp.getStartIndex(),temp.getStopIndex());

for(IngredientSpecification ingSpec : ingTop){
	values.add((new CO2Value(ingSpec)).mult(temp.getPersonFactor()));
}


if (ingredients.size() > 0) {
	%>
	<table cellspacing="0" cellpadding="0" class="table toc" >
		
		<tr>
		<td></td>
		<td class="gray left-border"></td>
		<td class="gray co2label"><span class="nowrap"><%= properties.co2Unit %> CO<sub>2</sub>*</span></td>
		<td></td>
		</tr>
		
		<tr>
		<td class="table-header bottom-border">Top <%= temp.getStopIndex() %> CO<sub>2</sub>-intensive Zutaten in diesem Rezept</td>
		<td class="left-border"></td>
		<td class="co2value" ></td>
		<td ></td>
		</tr>
		
		
		<%
		for(IngredientSpecification ingSpec : ingTop){ 
			if (ingSpec.getMengeGramm() > Util.getWeight(ingredients)/100.0*properties.weightThreshold || ingSpec.calculateCo2ValueNoFactors() > Util.getCO2Value(ingredients).noFactorsQuota/100.0*properties.valueThreshold) {
				%>
				<tr <%
				int order = (ingTop.indexOf(ingSpec) - counterIterate ) % 2; 
				if(order == 1) { %>
				class="alternate"
				<% }%> > 
				<td class="menu-name">
				<%= ingSpec.getName() %> (<%= properties.weight_formatter.format(ingSpec.getMengeGramm()*properties.weightUnit.conversionFactor*temp.getPersonFactor()) + " " + properties.weightUnit + ")" %><% if (ingSpec.getCost() > 0) { %> (<%= properties.cost_formatter.format(ingSpec.getCost()*temp.getPersonFactor()) %> CHF) <% } %>
				</td>
				<td class="left-border" width="<%= properties.co2BarLength + properties.barOffset %>px"><%= StaticHTMLSnippets.getCo2ValueBar(values, new CO2Value(ingSpec), properties.co2BarLength, properties.valueType) %></td>
				<td class="co2value" ><%= properties.co2_formatter.format(ingSpec.getCalculatedCO2Value()*properties.co2Unit.conversionFactor*temp.getPersonFactor()) %></td>
			
				</tr>
		
			<%
			}
		}%>
		</table>
	
	
<%		
}
%>
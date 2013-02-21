<%@ page contentType="text/html;charset=UTF-8" language="java" %>


<%@ page import="ch.eaternity.shared.Recipe" %>
<%@ page import="ch.eaternity.shared.IngredientSpecification" %>
<%@ page import="ch.eaternity.shared.RecipeComment" %>
<%@ page import="ch.eaternity.shared.CatRyzer" %>
<%@ page import="ch.eaternity.shared.Pair" %>
<%@ page import="ch.eaternity.shared.Util" %>

<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.text.DecimalFormat" %>

<%@ page import="ch.eaternity.server.jsp.StaticDataLoader" %>
<%@ page import="ch.eaternity.server.jsp.StaticProperties" %>
<%@ page import="ch.eaternity.server.jsp.StaticTempBean" %>


<%@ page import="java.util.Date" %>


<jsp:useBean id="vars" scope="session"
     class="ch.eaternity.server.jsp.StaticDataLoader" />
    
<jsp:useBean id="temp" scope="session"
	class="ch.eaternity.server.jsp.StaticTempBean" />
	
<jsp:useBean id="properties" scope="session"
class="ch.eaternity.server.jsp.StaticProperties" />
     
     
<%

List<IngredientSpecification> ingredients = temp.getIngredients();
CatRyzer catryzer = new CatRyzer();
Pair<Double, Double> seasonQuotients;
seasonQuotients = Util.getSeasonQuotient(ingredients);
%>

<table cellspacing="0" cellpadding="0" class="table toc" >
	<tr>
		<td>CO2 Rohwert ohne Anteile [<%= properties.co2Unit %> CO<sub>2</sub>]:</td>
		<td><%=properties.co2_formatter.format( Util.getCO2Value(ingredients).noFactorsQuota*properties.co2Unit.conversionFactor*temp.getPersonFactor())%></td>
	</tr>
	<tr>
		<td >Anteil Transport [<%= properties.co2Unit %> CO<sub>2</sub>]:</td>
		<td><%=properties.co2_formatter.format( Util.getCO2Value(ingredients).transQuota*properties.co2Unit.conversionFactor*temp.getPersonFactor())%></td>
	</tr>
	<tr>
		<td >Anteil Konservierungsmethoden [<%= properties.co2Unit %> CO<sub>2</sub>]:</td>
		<td><%=properties.co2_formatter.format( Util.getCO2Value(ingredients).condQuota*properties.co2Unit.conversionFactor*temp.getPersonFactor())%></td>
	</tr>
	<tr>
		<td >Anteil Produktionsmethoden [<%= properties.co2Unit %> CO<sub>2</sub>]:</td>
		<td><%=properties.co2_formatter.format( Util.getCO2Value(ingredients).prodQuota*properties.co2Unit.conversionFactor*temp.getPersonFactor())%></td>
	</tr>
	<tr>
		<td>Gewicht [<%= properties.weightUnit %>]:</td>
		<td><%= properties.weight_formatter.format( Util.getWeight(ingredients)*properties.weightUnit.conversionFactor*temp.getPersonFactor()) %></td>
	</tr>
	<% if (Util.getCost(ingredients) > 0) { %>
		<tr>
			<td>Kosten [CHF]:</td>
			<td><%= properties.cost_formatter.format( Util.getCost(ingredients)*temp.getPersonFactor()) %></td>
		</tr>
	<% } %>
	<tr>
		<td>Saison Stückzahlquotient [%]:</td>
		<td><%= properties.formatter.format(seasonQuotients.first*100) %></td>
	</tr>
	<tr>
		<td>Saison Gewichtsquotient [%]:</td>
		<td><%= properties.formatter.format(seasonQuotients.second*100) %></td>
	</tr>
</table>
	
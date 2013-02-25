<%@ page contentType="text/html;charset=UTF-8" language="java" %>


<%@ page import="ch.eaternity.shared.IngredientSpecification" %>
<%@ page import="ch.eaternity.shared.CatRyzer" %>
<%@ page import="ch.eaternity.shared.Pair" %>
<%@ page import="ch.eaternity.shared.Util" %>

<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>

<%@ page import="ch.eaternity.server.jsp.StaticDataLoader" %>
<%@ page import="ch.eaternity.server.jsp.StaticProperties" %>
<%@ page import="ch.eaternity.server.jsp.StaticTempBean" %>



     
     
<%
/* uses following variables in Temp:
	ingredients
	personFactor
*/

StaticProperties props = (StaticProperties)request.getAttribute("props");
StaticDataLoader data = (StaticDataLoader)request.getAttribute("data");
StaticTempBean temp = (StaticTempBean)request.getAttribute("temp");

List<IngredientSpecification> ingredients = temp.getIngredients();
CatRyzer catryzer = new CatRyzer();
Pair<Double, Double> seasonQuotients;
seasonQuotients = Util.getSeasonQuotient(ingredients);
%>

<table cellspacing="0" cellpadding="0" class="table toc" >
	<tr>
		<td>CO2 Rohwert ohne Anteile [<%= props.co2Unit %> CO<sub>2</sub>]:</td>
		<td><%=props.co2_formatter.format( Util.getCO2Value(ingredients).noFactorsQuota*props.co2Unit.conversionFactor*temp.getPersonFactor())%></td>
	</tr>
	<tr>
		<td >Anteil Transport [<%= props.co2Unit %> CO<sub>2</sub>]:</td>
		<td><%=props.co2_formatter.format( Util.getCO2Value(ingredients).transQuota*props.co2Unit.conversionFactor*temp.getPersonFactor())%></td>
	</tr>
	<tr>
		<td >Anteil Konservierungsmethoden [<%= props.co2Unit %> CO<sub>2</sub>]:</td>
		<td><%=props.co2_formatter.format( Util.getCO2Value(ingredients).condQuota*props.co2Unit.conversionFactor*temp.getPersonFactor())%></td>
	</tr>
	<tr>
		<td >Anteil Produktionsmethoden [<%= props.co2Unit %> CO<sub>2</sub>]:</td>
		<td><%=props.co2_formatter.format( Util.getCO2Value(ingredients).prodQuota*props.co2Unit.conversionFactor*temp.getPersonFactor())%></td>
	</tr>
	<tr>
		<td>Gewicht [<%= props.weightUnit %>]:</td>
		<td><%= props.weight_formatter.format( Util.getWeight(ingredients)*props.weightUnit.conversionFactor*temp.getPersonFactor()) %></td>
	</tr>
	<% if (Util.getCost(ingredients) > 0) { %>
		<tr>
			<td>Kosten [CHF]:</td>
			<td><%= props.cost_formatter.format( Util.getCost(ingredients)*temp.getPersonFactor()) %></td>
		</tr>
	<% } %>
	<tr>
		<td>Saison Stückzahlquotient [%]:</td>
		<td><%= props.formatter.format(seasonQuotients.first*100) %></td>
	</tr>
	<tr>
		<td>Saison Gewichtsquotient [%]:</td>
		<td><%= props.formatter.format(seasonQuotients.second*100) %></td>
	</tr>
</table>
	
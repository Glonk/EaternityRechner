<%@ page contentType="text/html;charset=UTF-8" language="java" %>


<%@ page import="ch.eaternity.shared.Recipe" %>
<%@ page import="ch.eaternity.shared.IngredientSpecification" %>
<%@ page import="ch.eaternity.shared.RecipeComment" %>
<%@ page import="ch.eaternity.shared.CatRyzer" %>
<%@ page import="ch.eaternity.shared.Pair" %>

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
seasonQuotients = catryzer.getSeasonQuotient(ingredients);
%>

<table cellspacing="0" cellpadding="0" class="table toc" >
	<tr>
		<td>CO2 Rohwert ohne Anteile [g CO<sub>2</sub>]:</td>
		<td><%=properties.co2_formatter.format( catryzer.getCO2Value(ingredients).noFactorsQuota*temp.getPersonFactor())%></td>
	</tr>
	<tr>
		<td >Anteil Transport [g CO<sub>2</sub>]:</td>
		<td><%=properties.co2_formatter.format( catryzer.getCO2Value(ingredients).transQuota*temp.getPersonFactor())%></td>
	</tr>
	<tr>
		<td >Anteil Konservierungsmethoden [g CO<sub>2</sub>]:</td>
		<td><%=properties.co2_formatter.format( catryzer.getCO2Value(ingredients).condQuota*temp.getPersonFactor())%></td>
	</tr>
	<tr>
		<td >Anteil Produktionsmethoden [g CO<sub>2</sub>]:</td>
		<td><%=properties.co2_formatter.format( catryzer.getCO2Value(ingredients).prodQuota*temp.getPersonFactor())%></td>
	</tr>
	<tr>
		<td>Gewicht [g]:</td>
		<td><%= properties.weight_formatter.format( catryzer.getWeight(ingredients)*temp.getPersonFactor()) %></td>
	</tr>
	<% if (catryzer.getCost(ingredients) > 0) { %>
	<tr>
		<td>Kosten [CHF]:</td>
		<td><%= properties.cost_formatter.format( catryzer.getCost(ingredients)*temp.getPersonFactor()) %></td>
	</tr>
	<% } %>
</table>
	
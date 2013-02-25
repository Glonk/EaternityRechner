<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%@ page import="ch.eaternity.shared.IngredientSpecification" %>
<%@ page import="ch.eaternity.shared.CatRyzer" %>
<%@ page import="ch.eaternity.shared.Pair" %>
<%@ page import="ch.eaternity.shared.Util" %>

<%@ page import="java.util.List" %>
<%@ page import="java.util.Set" %>
<%@ page import="java.util.Iterator" %>

<%@ page import="ch.eaternity.server.jsp.StaticDataLoader" %>
<%@ page import="ch.eaternity.server.jsp.StaticProperties" %>
<%@ page import="ch.eaternity.server.jsp.StaticTemp" %>

     
     
<%
          	/* uses following variables in Temp:
          	catryzer
          */

          StaticProperties props = (StaticProperties)request.getAttribute("props");
          StaticDataLoader data = (StaticDataLoader)request.getAttribute("data");
          StaticTemp temp = (StaticTemp)request.getAttribute("temp");
          CatRyzer catryzer = (CatRyzer)request.getAttribute("catryzer");

          List<IngredientSpecification> ingredients = catryzer.getIngredientSpecifications();
          Pair<Double, Double> seasonQuotients;
          seasonQuotients = Util.getSeasonQuotient(ingredients);
          int counter;

          Set<Pair<String,Set<String>>> ingredientsByCategory = catryzer.getIngredientsByCategory();
          %>


<h2>Kategorienübersicht</h2>
<br /><br />
<% 
Iterator<Pair<String,Set<String>>> iter = ingredientsByCategory.iterator();
while (iter.hasNext()) {
	Pair<String,Set<String>> category = iter.next();
	%>
	
	<table cellspacing="0" cellpadding="0" class="table listTable" >
		<tr>
			<td></td>
			<td class="left-border"><br></td>
		</tr>
		
		<tr>
			<td class="bottom-border">
			<h3>Kategorie: <%= category.first() %></h3>
			</td>
			<td class="left-border"></td>
		</tr>
		
		<tr>
			<td>
			<%	
			counter = 0;
			for(String ingredient: category.second()){
				counter = counter + 1;
				
				%><% if(counter != 1){ %>, <% } %><span class="nowrap"><%= ingredient %></span><%
			}
			%>
			
			</td>
			<td class="left-border"><br></td>
		</tr>
		
		<tr>
			<td></td>
			<td class="left-border"><br></td>
		</tr>
		
		<tr>
			<td></td>
			<td class="left-border"><br></td>
		</tr>
	</table>

<%
}
%>
	
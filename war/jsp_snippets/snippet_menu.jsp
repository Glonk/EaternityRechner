<%@ page contentType="text/html;charset=UTF-8" language="java" %>


<%@ page import="ch.eaternity.shared.Recipe" %>
<%@ page import="ch.eaternity.shared.IngredientSpecification" %>
<%@ page import="ch.eaternity.shared.RecipeComment" %>
<%@ page import="ch.eaternity.shared.CO2Value" %>
<%@ page import="ch.eaternity.shared.Util" %>

<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.text.DecimalFormat" %>

<%@ page import="ch.eaternity.server.jsp.StaticDataLoader" %>
<%@ page import="ch.eaternity.server.jsp.StaticHTMLSnippets" %>

<%@ page import="java.util.Date" %>


<jsp:useBean id="vars" scope="session"
     class="ch.eaternity.server.jsp.StaticDataLoader" />
     
<jsp:useBean id="temp" scope="session"
	class="ch.eaternity.server.jsp.StaticTempBean" />
	
<jsp:useBean id="properties" scope="session"
	class="ch.eaternity.server.jsp.StaticProperties" />
     
<%
DecimalFormat formatter = new DecimalFormat("##");

List<Recipe> recipes = temp.getRecipes();

if (recipes.size() > 0 && temp.getCo2Values() != null ) {
	Recipe recipe = recipes.get(0);
	
	recipe.setCO2Value();
	Double recipeValue = recipe.getCO2Value() + properties.getExtra();
	
	// normalise recipeValue to amount of persons
	recipeValue = recipeValue*properties.getPersons();
	
	String co2ValueStr = properties.co2_formatter.format( recipeValue * properties.co2Unit.conversionFactor );
	
	%>
	
	<table cellspacing="0" cellpadding="0" class="table listTable" >
		<tr>
		<td></td>
		<td class="left-border"><br></td>
		</tr>
		
		<tr>
		<td class="bottom-border">
		<img class="smile" src="smiley8.png" alt="smiley" />
		<img class="smile" src="smiley8.png" alt="smiley" />
		<h3><%= recipe.getSymbol() %></h3>
	
		</td>
		<td class="left-border"></td>
		</tr>
	
		<tr>
		<td><div class="amount"><%= co2ValueStr + " " + properties.co2Unit %> CO<sub>2</sub>* total</div></td>
		<td class="left-border"><%=StaticHTMLSnippets.getCo2ValueBar(temp.getCo2Values(), recipe.getCO2ValueExpanded(), properties.co2BarLength, properties.valueType)%></td>
		</tr>
		
		<tr>
		<td>
		
		<span class="subTitle"><%= recipe.getSubTitle() %></span>
		
		<span style="color:gray;">Zutaten für <%= properties.getPersons().toString() %> Personen:</span><br />
	
		
		<%	
		int counter = 0;
		for(IngredientSpecification ingredient: recipe.Zutaten){
			counter = counter + 1;
		
			%><% if(counter != 1){ %>, <% } %><span class="nowrap"><%= properties.weight_formatter.format(ingredient.getMengeGramm()*properties.weightUnit.conversionFactor/recipe.getPersons()*properties.getPersons()) + " " + properties.co2Unit + " " + ingredient.getName() %>
				( <% if(ingredient.getHerkunft() != null){ %><%= ingredient.getHerkunft().symbol %><% } %>  | <%=  ingredient.getKmDistanceRounded() %>km  | <% if(ingredient.getZustand() != null){ %><%= ingredient.getZustand().symbol %> | <% } %><% if(ingredient.getProduktion() != null){ %><%= ingredient.getProduktion().symbol %> | <% } %> <% if(ingredient.getTransportmittel() != null){ %><%= ingredient.getTransportmittel().symbol %><% } %> )
			</span><%
		}
		%>
		</td>
		<td class="left-border"><br></td>
		</tr>
		
		<tr>
		<td></td>
		<td class="left-border"><br></td>
		</tr>
		
		
		<%	
		if(recipe.comments != null){
			for(RecipeComment comment: recipe.comments){
		
			%>
			<tr>
			<td>• <%= comment.symbol %><% if(comment.amount > 0){ %><span class="amount"><%= comment.amount %> g CO<sub>2</sub>* </span><% } %></td>
			<td class="left-border"><% if(comment.amount > 0){ %><img class="bar" src="green.png" alt="green" height="11"  width="<%= comment.amount/recipeValue*temp.personFactor*140 %>" /><% } %></td>
			</tr>
		
			<%
			}
		}
		%>
		
		<tr>
		<td></td>
		<td class="left-border"><br></td>
		</tr>
	</table>
	
	
<%		
}
%>
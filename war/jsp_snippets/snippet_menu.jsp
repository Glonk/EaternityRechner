<%@ page contentType="text/html;charset=UTF-8" language="java" %>


<%@ page import="ch.eaternity.shared.Recipe" %>
<%@ page import="ch.eaternity.shared.IngredientSpecification" %>
<%@ page import="ch.eaternity.shared.RecipeComment" %>

<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.text.DecimalFormat" %>


<%@ page import="ch.eaternity.server.StaticPageService" %>

<%@ page import="java.util.Date" %>


<jsp:useBean id="vars" scope="session"
     class="ch.eaternity.server.StaticPageService" />
     
<jsp:useBean id="temp" scope="session"
	class="ch.eaternity.server.StaticTempBean" />
	
<jsp:useBean id="properties" scope="session"
	class="ch.eaternity.server.StaticProperties" />
     
<%
DecimalFormat formatter = new DecimalFormat("##");

List<Recipe> recipes = temp.getRecipes();

if (recipes.size() > 0) {
	Recipe recipe = recipes.get(0);
	
	recipe.setCO2Value();
	Double recipeValue = recipe.getCO2Value() + properties.getExtra();
		
	String co2ValueStr = properties.co2_formatter.format( recipeValue );
	
	// normalise recipeValue to amount of persons passed by parameter
	recipeValue = recipeValue/recipe.getPersons()*properties.getPersons();
	
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
		<td><div class="amount"><%= co2ValueStr %> g CO<sub>2</sub>* total</div></td>
		<td class="left-border"><img class="bar" height="11"  src="gray.png" alt="gray" width="140" /></td>
		</tr>
		
		<tr>
		<td>
		
		<span class="subTitle"><%= recipe.getSubTitle() %></span>
		
		<span style="color:gray;">Zutaten für <%= properties.getPersons().toString() %> Personen:</span><br />
	
		
		<%	
		int counter = 0;
		for(IngredientSpecification ingredient: recipe.Zutaten){
			counter = counter + 1;
		
			%><% if(counter != 1){ %>, <% } %><span class="nowrap"><%= ingredient.getMengeGramm()/recipe.getPersons()*properties.getPersons() %> g <%= ingredient.getName() %>
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
			<td class="left-border"><% if(comment.amount > 0){ %><img class="bar" src="green.png" alt="green" height="11"  width="<%= comment.amount/recipeValue*140 %>" /><% } %></td>
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
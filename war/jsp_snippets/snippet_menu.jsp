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
<%@ page import="ch.eaternity.server.jsp.StaticProperties" %>
<%@ page import="ch.eaternity.server.jsp.StaticTemp" %>
<%@ page import="ch.eaternity.server.jsp.StaticHTMLSnippets" %>

     
     
<%
	StaticProperties props = (StaticProperties)request.getAttribute("props");
	StaticDataLoader data = (StaticDataLoader)request.getAttribute("data");
	StaticTemp temp = (StaticTemp)request.getAttribute("temp");


    if (temp.getCo2Values() != null ) {
    
    	Double recipeValue = temp.recipe.getCO2Value() + props.getExtra();
    	
    	// normalise recipeValue to amount of persons
    	recipeValue = recipeValue*props.getPersons();
    %>
	
	<table cellspacing="0" cellpadding="0" class="table listTable" >
		<tr>
		<td></td>
		<td class="left-border"><br></td>
		</tr>
		
		<tr>
			<td class="bottom-border">
				<% 
				if (temp.displaySmilies) {
					if (temp.recipe.getCO2Value() < props.average) { %>
					<img class="smile" src="smiley8.png" alt="smiley" />
				<% } if (temp.recipe.getCO2Value() < props.climateFriendlyValue) { %>
					<img class="smile" src="smiley8.png" alt="smiley" />
				<% 	} 
				}%>
				<%= temp.title %>
			</td>
			<td class="left-border"></td>
		</tr>
	
		<tr>
		<td><div class="amount"><%= props.co2_formatter.format( recipeValue * props.co2Unit.conversionFactor ) + " " + props.co2Unit %> CO<sub>2</sub>* total</div></td>
		<td class="left-border"><img class="bar" height="11"  src="gray.png" alt="gray" width="140" /></td>
		</tr>
		
		<tr>
		<td>
		
		<span class="subTitle"><%= temp.recipe.getSubTitle() %></span>
		
		<span style="color:gray;">Zutaten für <%= props.getPersons().toString() %> Personen:</span><br />
		
		<% 
		temp.persons = temp.recipe.getPersons(); 
		temp.ingredients = temp.recipe.getZutaten();
		%>
		<jsp:include page="/jsp_snippets/snippet_ingredients.jsp" />
		
		</td>
		<td class="left-border"><br></td>
		</tr>
		
		<tr>
		<td></td>
		<td class="left-border"><br></td>
		</tr>
		
		
		<%	
		// needs to be corrected with g / kg transformations
		if(temp.recipe.comments != null){
			for(RecipeComment comment: temp.recipe.comments){
		
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
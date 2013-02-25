<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%@ page import="ch.eaternity.shared.IngredientSpecification" %>
<%@ page import="ch.eaternity.shared.Util" %>

<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>

<%@ page import="ch.eaternity.server.jsp.StaticDataLoader" %>
<%@ page import="ch.eaternity.server.jsp.StaticProperties" %>
<%@ page import="ch.eaternity.server.jsp.StaticTempBean" %>
<%@ page import="ch.eaternity.server.jsp.StaticHTMLSnippets" %>
<%@ page import="ch.eaternity.server.jsp.StaticProperties.IngredientRepresentation" %>

  
     
     
<%
StaticProperties props = (StaticProperties)request.getAttribute("props");
StaticDataLoader data = (StaticDataLoader)request.getAttribute("data");
StaticTempBean temp = (StaticTempBean)request.getAttribute("temp");

List<IngredientSpecification> ingSpecs = temp.getIngredients();
		
int counter = 0;
for(IngredientSpecification ingredient: ingSpecs){
	counter = counter + 1;
	%>
	<span class="nowrap">
	<%= props.weight_formatter.format(ingredient.getMengeGramm()*props.weightUnit.conversionFactor/temp.persons*props.getPersons()) + " " + props.weightUnit + " " + ingredient.getName() %>
	<% if (props.ingredientRepresentation == IngredientRepresentation.EXPANDED) { %>
		( <% if(ingredient.getHerkunft() != null){ %><%= ingredient.getHerkunft().symbol %><% } %>  | <%=  ingredient.getKmDistanceRounded() %>km  | <% if(ingredient.getZustand() != null){ %><%= ingredient.getZustand().symbol %> | <% } %><% if(ingredient.getProduktion() != null){ %><%= ingredient.getProduktion().symbol %> | <% } %> <% if(ingredient.getTransportmittel() != null){ %><%= ingredient.getTransportmittel().symbol %><% } %> )
	<% } %>
	<% if(counter < ingSpecs.size()){ %>, <% } %>
	<% if (props.ingredientRepresentation == IngredientRepresentation.EXPANDED) { %><br /><% } %>
	</span><%
}
%>

	
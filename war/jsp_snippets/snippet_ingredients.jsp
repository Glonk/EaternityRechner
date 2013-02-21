<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%@ page import="ch.eaternity.shared.IngredientSpecification" %>
<%@ page import="ch.eaternity.shared.CO2Value" %>
<%@ page import="ch.eaternity.shared.Util" %>

<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.text.DecimalFormat" %>

<%@ page import="ch.eaternity.server.jsp.StaticDataLoader" %>
<%@ page import="ch.eaternity.server.jsp.StaticHTMLSnippets" %>
<%@ page import="ch.eaternity.server.jsp.StaticProperties.IngredientRepresentation" %>

<%@ page import="java.util.Date" %>


<jsp:useBean id="vars" scope="session"
     class="ch.eaternity.server.jsp.StaticDataLoader" />
     
<jsp:useBean id="temp" scope="session"
	class="ch.eaternity.server.jsp.StaticTempBean" />
	
<jsp:useBean id="properties" scope="session"
	class="ch.eaternity.server.jsp.StaticProperties" />
     
<%
DecimalFormat formatter = new DecimalFormat("##");

List<IngredientSpecification> ingSpecs = temp.getIngredients();

		
int counter = 0;
for(IngredientSpecification ingredient: ingSpecs){
	counter = counter + 1;
	%>
	<span class="nowrap">
	<%= properties.weight_formatter.format(ingredient.getMengeGramm()*properties.weightUnit.conversionFactor/temp.persons*properties.getPersons()) + " " + properties.weightUnit + " " + ingredient.getName() %>
	<% if (properties.ingredientRepresentation == IngredientRepresentation.EXPANDED) { %>
		( <% if(ingredient.getHerkunft() != null){ %><%= ingredient.getHerkunft().symbol %><% } %>  | <%=  ingredient.getKmDistanceRounded() %>km  | <% if(ingredient.getZustand() != null){ %><%= ingredient.getZustand().symbol %> | <% } %><% if(ingredient.getProduktion() != null){ %><%= ingredient.getProduktion().symbol %> | <% } %> <% if(ingredient.getTransportmittel() != null){ %><%= ingredient.getTransportmittel().symbol %><% } %> )
	<% } %>
	<% if(counter < ingSpecs.size()){ %>, <% } %>
	<% if (properties.ingredientRepresentation == IngredientRepresentation.EXPANDED) { %><br /><% } %>
	</span><%
}
%>

	
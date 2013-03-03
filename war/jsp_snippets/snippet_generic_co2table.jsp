<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%@ page import="ch.eaternity.shared.CO2Value" %>
<%@ page import="ch.eaternity.shared.Recipe" %>
<%@ page import="ch.eaternity.shared.Util" %>
<%@ page import="ch.eaternity.shared.CategoryQuantities" %>

<%@ page import="ch.eaternity.server.jsp.StaticDataLoader" %>
<%@ page import="ch.eaternity.server.jsp.StaticProperties" %>
<%@ page import="ch.eaternity.server.jsp.StaticTemp" %>
<%@ page import="ch.eaternity.server.jsp.StaticHTMLSnippets" %>

<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.Collection" %>
<%@ page import="java.util.Collections" %>
<%@ page import="java.util.Date" %>

<%@ page import="java.text.DecimalFormat" %>


     
<%
/* uses following variables in Temp:
	- displayCo2Value
	- displayWeight
	- displayCost
	
	- catQuantities

*/

StaticProperties props = (StaticProperties)request.getAttribute("props");
StaticTemp temp = (StaticTemp)request.getAttribute("temp");

List<CO2Value> co2Values = new ArrayList<CO2Value>();

for(CategoryQuantities catQuantity: temp.catQuantities) {
	co2Values.add(catQuantity.co2Value);
}


%>
<table cellspacing="0" cellpadding="0" class="table toc" >

	<tr>
	<td></td>
	<td class="gray left-border"></td>
	<% if (temp.displayCo2Value) { %>
		<td class="gray co2label"><span class="nowrap"><%= props.co2Unit %> CO<sub>2</sub>*</span></td>
	<% } if (temp.displayWeight) { %>	
		<td class="gray co2label" width="40"><span class="nowrap"><%= props.weightUnit %></span></td>
	<% } if (temp.displayCost) { %>	
		<td class="gray co2label" width="40"><span class="nowrap">CHF</span></td><% } %>
	</tr>
	
	<tr>
	<td class="table-header bottom-border"><%= temp.title %></td>
	<td class="left-border"></td>
	<% if (temp.displayCo2Value) { %>
		<td class="co2label">
	<% } if (temp.displayWeight) { %>	
		<td></td>
	<% } if (temp.displayCost) { %>	
		<td></td><% } %>
	</tr>
	
	<% 
	for(CategoryQuantities catQuantity: temp.catQuantities){ %>
	
		<tr <%
			int order = (temp.catQuantities.indexOf(catQuantity) ) % 2; 
			if(order == 1) { %>class="alternate"<% }%> > 
			<td class="menu-name">
				<%= catQuantity.categoryName %>
			</td>
			<td class="left-border" width="<%= props.co2BarLength + props.barOffset %>px"><%= StaticHTMLSnippets.getCo2ValueBar(co2Values, catQuantity.co2Value, props.co2BarLength, props.valueType) %></td>
			<% if (temp.displayCo2Value) { %>
				<td class="co2value" ><%= props.co2_formatter.format(catQuantity.co2Value.totalValue*props.co2Unit.conversionFactor) %></td>
			<% } if (temp.displayWeight) { %>
				<td class="co2value"><%= props.weight_formatter.format(catQuantity.weight*props.weightUnit.conversionFactor) %></td>
			<% } if (temp.displayCost) { %>	
				<td class="co2value"><%= props.cost_formatter.format(catQuantity.cost) %></td><% } %>
		</tr>
	
	<%
	}
	%>
	
</table>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%@ page import="ch.eaternity.shared.CatRyzer" %>
<%@ page import="ch.eaternity.shared.Util" %>
<%@ page import="ch.eaternity.shared.CO2Value" %>

<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>

<%@ page import="ch.eaternity.server.jsp.StaticDataLoader" %>
<%@ page import="ch.eaternity.server.jsp.StaticProperties" %>
<%@ page import="ch.eaternity.server.jsp.StaticTempBean" %>
<%@ page import="ch.eaternity.server.jsp.StaticHTMLSnippets" %>


     
     
<%
/* uses following variables in Temp:
	catryzer
*/
StaticProperties props = (StaticProperties)request.getAttribute("props");
StaticDataLoader data = (StaticDataLoader)request.getAttribute("data");
StaticTempBean temp = (StaticTempBean)request.getAttribute("temp");
CatRyzer catryzer = (CatRyzer)request.getAttribute("catryzer");

List<CatRyzer.CategoryValue> valuesByCategory = catryzer.getCatVals();
List<CO2Value> co2Values = new ArrayList<CO2Value> ();

for(CatRyzer.CategoryValue categoryValue : valuesByCategory){
	co2Values.add(categoryValue.co2value);
}

%>

<table cellspacing="0" cellpadding="0" class="table toc" >

	<tr>
	<td></td>
	<td class="gray left-border"></td>
	<td class="gray co2label"><span class="nowrap"><%= props.co2Unit %> CO<sub>2</sub>*</span></td>
	<td class="gray co2label" width="40"><span class="nowrap"><%= props.weightUnit %></td>
	<td class="gray co2label" width="40"><span class="nowrap">CHF</td>
	</tr>
	
	<tr>
	<td class="table-header bottom-border"><%= temp.title %></td>
	<td class="left-border"></td>
	<td class="co2value" ></td>
	<td ></td>
	<td ></td>
	</tr>
	
	<% 
	for(CatRyzer.CategoryValue categoryValue : valuesByCategory){ %>
		<tr <%
			int order = (valuesByCategory.indexOf(categoryValue) ) % 2; 
			if(order == 1) { %>class="alternate"<% }%> > 
			<td class="menu-name">
				<%= categoryValue.categoryName %>
			</td>
			<td class="left-border" width="<%= props.co2BarLength + props.barOffset %>px"><%= StaticHTMLSnippets.getCo2ValueBar(co2Values, categoryValue.co2value, props.co2BarLength, props.valueType) %></td>
			<td class="co2value" ><%= props.co2_formatter.format(categoryValue.co2value.totalValue*props.co2Unit.conversionFactor) %></td>
			<td class="co2value"><%= props.weight_formatter.format(categoryValue.weight*props.weightUnit.conversionFactor) %></td>
			<td class="co2value"><%= props.cost_formatter.format(categoryValue.cost) %></td>
		</tr>
	
	<%
	}
	%>

</table>
	
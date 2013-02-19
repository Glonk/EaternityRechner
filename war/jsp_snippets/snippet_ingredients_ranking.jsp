<%@ page contentType="text/html;charset=UTF-8" language="java" %>


<%@ page import="ch.eaternity.shared.IngredientSpecification" %>
<%@ page import="ch.eaternity.shared.comparators.IngredientValueComparator" %>

<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.Collection" %>
<%@ page import="java.util.Collections" %>
<%@ page import="java.text.DecimalFormat" %>

<%@ page import="ch.eaternity.server.StaticPageService" %>
<%@ page import="ch.eaternity.server.StaticProperties" %>




<jsp:useBean id="vars" scope="session"
     class="ch.eaternity.server.StaticPageService" />
     
<jsp:useBean id="temp" scope="session"
	class="ch.eaternity.server.StaticTempBean" />
     
<jsp:useBean id="properties" scope="session"
	class="ch.eaternity.server.StaticProperties" />
	
<%

List<IngredientSpecification> ingredients = temp.getIngredients();
Collection<Double> values = new ArrayList<Double>();
int counterIterate = 0;

Collections.sort(ingredients, new IngredientValueComparator());


List<IngredientSpecification> ingTop = ingredients.subList(temp.getStartIndex(),temp.getStopIndex());

for(IngredientSpecification ingSpec : ingTop){
	values.add(ingSpec.getCalculatedCO2Value()*temp.getPersonFactor());
}


if (ingredients.size() > 0) {
	%>
	<table cellspacing="0" cellpadding="0" class="table toc" >
		
		<tr>
		<td></td>
		<td class="gray left-border"></td>
		<td class="gray co2label"><span class="nowrap">g CO<sub>2</sub>*</span></td>
		<td></td>
		</tr>
		
		<tr>
		<td class="table-header bottom-border">Top <%= temp.getStopIndex() %> CO<sub>2</sub>-intensive Zutaten in diesem Rezept</td>
		<td class="left-border"></td>
		<td class="co2value" ></td>
		<td ></td>
		</tr>
		
		
		<%
		for(IngredientSpecification ingSpec : ingTop){ %>
		
			<tr <%
			int order = (ingTop.indexOf(ingSpec) - counterIterate ) % 2; 
			if(order == 1) { %>
			class="alternate"
			<% }%> > 
			<td class="menu-name">
			<%= ingSpec.getName() %> (<%=ingSpec.getMengeGramm()*temp.getPersonFactor()%> g)  (<%= properties.cost_formatter.format(ingSpec.getCost()*temp.getPersonFactor()) %> CHF)
			</td>
			<td class="left-border" width="<%= properties.co2BarLength + properties.barOffset %>px"><%= vars.getCo2ValueBarSimple(values, ingSpec.getCalculatedCO2Value()*temp.getPersonFactor(), properties.co2BarLength) %></td>
			<td class="co2value" ><%= properties.co2_formatter.format(ingSpec.getCalculatedCO2Value()*temp.getPersonFactor()) %></td>
		
			</tr>
		
		<%}%>
		</table>
	
	
<%		
}
%>
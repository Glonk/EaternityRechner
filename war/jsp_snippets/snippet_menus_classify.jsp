<%@ page contentType="text/html;charset=UTF-8" language="java" %>


<%@ page import="ch.eaternity.shared.Recipe" %>
<%@ page import="ch.eaternity.shared.IngredientSpecification" %>
<%@ page import="ch.eaternity.shared.RecipeComment" %>
<%@ page import="ch.eaternity.shared.CO2Value" %>
<%@ page import="ch.eaternity.shared.Util" %>
<%@ page import="ch.eaternity.shared.comparators.RezeptValueComparator" %>
<%@ page import="ch.eaternity.shared.Converter" %>

<%@ page import="ch.eaternity.server.jsp.StaticDataLoader" %>
<%@ page import="ch.eaternity.server.jsp.StaticProperties" %>
<%@ page import="ch.eaternity.server.jsp.StaticTempBean" %>
<%@ page import="ch.eaternity.server.jsp.StaticHTMLSnippets" %>
<%@ page import="ch.eaternity.server.jsp.StaticProperties.IngredientRepresentation" %>

<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.Collection" %>
<%@ page import="java.util.Collections" %>
<%@ page import="java.util.Date" %>

<%@ page import="java.text.DecimalFormat" %>


     
<%

StaticProperties props = (StaticProperties)request.getAttribute("props");
StaticDataLoader data = (StaticDataLoader)request.getAttribute("data");
StaticTempBean temp = (StaticTempBean)request.getAttribute("temp");

DecimalFormat formatter = new DecimalFormat("##");

List<Recipe> recipes = temp.getRecipes();

Collection<Double> values = new ArrayList<Double>();

// this should be disfunctional by now
Date date = new Date();
long iTimeStamp = (long) (date.getTime() * .00003);

%>
	
<form name="htmlAdder" method="POST" action=";">
	
<table cellspacing="0" cellpadding="0" class="table toc" >
	<tr>
		<td></td>
		<td class="gray left-border"></td>
		<td class="gray co2label"><span class="nowrap">g CO<sub>2</sub>* pro Person</span></td>
		<td></td>
	</tr>

<%

for (Recipe recipe : recipes) {
	values.add(recipe.getCO2Value());
}
Collections.sort(data.recipes,new RezeptValueComparator());

Boolean notDoneFirst = true;
Boolean notDoneSeccond = true;
Boolean notDoneThird = true;

Double MaxValueRezept = Util.getMax(values);

Double adjustedAverageLength = props.average/MaxValueRezept*200;
Double climateFriendlyValueLength = props.climateFriendlyValue/MaxValueRezept*200;
String averageLength = props.formatter.format(adjustedAverageLength);
String formattedClimate = props.formatter.format(props.climateFriendlyValue);
String smilies = "";
String extraFormat = props.formatter.format(props.extra);
String lengthExtra = props.formatter.format(props.extra/MaxValueRezept*200);


	String klima = "<tr>"
	+"<td class='table-header'><br /></td>"
	+"<td class='left-border'></td>"
	+"<td class='co2value' ></td>"
	+"<td class='co2percent'  ></td>"
	+"</tr>"
+""
	+"<tr>"
	+"<td class='menu-name' style='text-align:right;'>"
	+"Klimafreundliches Menu"
	+"</td>"
	+"<td class='left-border'><img class='bar' src='orange.png' alt='gray' height='11' width='" + climateFriendlyValueLength + "' /></td>"
	+"<td class='co2value' >" + formattedClimate + "</td>"
	+"<td class='co2percent'  ></td>"
	+"</tr>";
	
	String herko = "<tr>"
	+"<td class='table-header'><br /></td>"
	+"<td class='left-border'></td>"
	+"<td class='co2value' ></td>"
	+"<td class='co2percent'  ></td>"
	+"</tr>"
+""
	+"<tr>"
	+"<td class='menu-name' style='text-align:right;'>"
	+"Herkömmliches Menu"
	+"</td>"
	+"<td class='left-border' style='background:#F7F7F7'><img class='bar' src='gray.png' alt='gray' height='11' width='" + averageLength + "' /></td>"
	+"<td class='co2value' style='background:#F7F7F7;padding:0.2em 1em 0.3em 0.3em;' >" + props.average + "</td>"
	+"<td class='co2percent'  ></td>"
	+"</tr>";


for(Recipe recipe: data.recipes){

	long compute = recipe.getId() * iTimeStamp;

	String code = Converter.toString(compute,34);
	String clear = Converter.toString(recipe.getId(),34);
	String dateString = "";
	if(recipe.cookingDate != null){
		dateString = props.dateFormatter.format(recipe.cookingDate);
	}

	recipe.setCO2Value();
	Double recipeValue = recipe.getCO2Value() + props.extra;
	
	String length = props.formatter.format(recipe.getCO2Value()/MaxValueRezept*200);
	String formatted = props.formatter.format( recipeValue);
	String moreOrLess = "";
	String percent ="";
	
	if((recipeValue+props.extra)>(props.average)){
		percent = "+" + props.formatter.format( ((recipeValue-props.average)/(props.average))*100 ) + "%";
	} else {
		percent = "-" + props.formatter.format( ((props.average-recipeValue)/(props.average))*100 ) + "%";
	}

	if((recipeValue < props.climateFriendlyValue) && notDoneFirst){
		
		smilies = "<img class='smile' src='smiley8.png' alt='smiley' /><img class='smile' src='smiley8.png' alt='smiley' />";
		
		if(notDoneFirst){
			notDoneFirst = false;
		%>
		
		<tr>
		<td class="table-header bottom-border">Grossartig</td>
		<td class="left-border"></td>
		<td class="co2value" ></td>
		<td ></td>
		</tr>
		
		<%
		}
		
		}
		if((recipeValue > props.climateFriendlyValue) && (recipeValue < props.average) &&  notDoneSeccond){ 
			
			smilies = "<img class='smile' src='smiley8.png' alt='smiley' />";
			
		if(notDoneSeccond){
			notDoneSeccond = false;
			out.print(klima);
			klima = ""; %>
		
		<tr>
		<td class="table-header bottom-border">Gut</td>
		<td class="left-border"></td>
		<td class="co2value" ></td>
		<td class="co2percent"  ></td>
		</tr>
		<%
		}
	}
		if((recipeValue > props.average) && notDoneThird){ 
			
			smilies = "";
			
			if(notDoneThird){
				notDoneThird = false;
				out.print(klima);
				out.print(herko);
				klima = "";
				herko = "";
			%>

		<tr>
		<td class="table-header bottom-border">Über Durchschnitt</td>
		<td class="left-border"></td>
		<td class="co2value" ></td>
		<td class="co2percent"  ></td>
		</tr>

		<%
		}
	}
		%>
		
		<tr <%
		int order = (data.recipes.indexOf(recipe)) % 2; 
		if(order == 1) { %>
		class="alternate"
		<% }%> > 
		<td class="menu-name">
		<% if(data.DoItWithPermanentIds) { %><span class="hiddenOnPage" style="display:inline"><%= clear %></span><% } %><input type="checkbox" name="<%= code %>" checked="checked" class="hiddenOnPage" onclick="javascript:addRemoveMenu('<%= code %>')">
		<%= smilies %><%= recipe.getSymbol() %>
		</td>
		<td class="left-border"><img class="bar" src="light-gray.png" alt="gray" height="11" width="<%= lengthExtra %>" /><img class="bar" src="green.png" alt="gray" height="11" width="<%= length %>" /></td>
		<td class="co2value" ><%= formatted %></td>
		<td class="co2percent" ><%= percent %></td>
		</tr>
		<%
}	

out.print(klima);
out.print(herko);

%>
</table>

<ul style="font-size:9pt;color:grey;">
<li>Die Menus haben einen Durchschnitt von: <%= props.formatter.format(Util.getAverage(values)) %> g CO<sub>2</sub>* (Median: <%= props.formatter.format(Util.getMedian((List<Double>)values)) %> g CO<sub>2</sub>*) pro Person.</li>
<% if(props.extra != 0) {%><li><img class="bar" src="light-gray.png" alt="gray" height="11" width="<%= lengthExtra %>" /> Bei den Menüs wurde <%=extraFormat %> g CO<sub>2</sub>* für die Zubereitung hinzugerechnet.</li><% }%>
</ul>
</form>
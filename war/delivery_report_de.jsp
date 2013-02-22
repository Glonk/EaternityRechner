<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%@ page import="ch.eaternity.shared.Recipe" %>
<%@ page import="ch.eaternity.shared.IngredientSpecification" %>
<%@ page import="ch.eaternity.shared.Converter" %>
<%@ page import="ch.eaternity.shared.CatRyzer" %>
<%@ page import="ch.eaternity.shared.Util" %>
<%@ page import="ch.eaternity.shared.Pair" %>
<%@ page import="ch.eaternity.shared.Quantity.Weight" %>

<%@ page import="ch.eaternity.server.jsp.StaticDataLoader" %>
<%@ page import="ch.eaternity.server.jsp.StaticProperties" %>
<%@ page import="ch.eaternity.server.jsp.StaticTempBean" %>

<%@ page import="java.util.Locale" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.Arrays" %>
<%@ page import="java.text.DecimalFormat" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.Date" %>
<%@ page import="java.util.Collection" %>
<%@ page import="java.util.Collections" %>
<%@ page import="java.math.RoundingMode" %>


<html>
<head>

<meta HTTP-EQUIV="Content-Type" CONTENT="text/html; charset=UTF-8">
<title>Klima-Bilanz Report</title>
<link rel="stylesheet" type="text/css" href="report.css">

<!-- Load the StaticPageService as a Bean, handlich Parameter passing between jsp's and Snippets -->
<jsp:useBean id="data" scope="session"
	class="ch.eaternity.server.jsp.StaticDataLoader" />

<jsp:useBean id="props" scope="session"
	class="ch.eaternity.server.jsp.StaticProperties" />

<jsp:useBean id="temp" scope="session"
	class="ch.eaternity.server.jsp.StaticTempBean" />
	
<jsp:useBean id="catryzer" scope="session"
	class="ch.eaternity.shared.CatRyzer" />


<% 
	//Specific Parameters to set for Display] 
	props.locale = Locale.GERMAN;

	props.weightUnit = Weight.GRAM;
	props.co2Unit = Weight.GRAM;		
			
	props.formatter = new DecimalFormat("##");
	props.formatter.setRoundingMode(RoundingMode.FLOOR);
	props.co2_formatter = new DecimalFormat("##");
	props.cost_formatter = new DecimalFormat("##");
	props.weight_formatter = new DecimalFormat("##");
	props.distance_formatter = new DecimalFormat("##");
	props.dateFormatter = new SimpleDateFormat("dd.MMMM yyyy");
	
	props.co2BarLength = 200;
	props.barOffset = 70;
	
	// standard values for request if not set
	props.doPdf = false;
	props.threshold = 1550;
	props.extra = 0;
	props.persons = 4;
	
	props.valueType = StaticProperties.ValueType.COMPACT;
	props.ingredientRepresentation = StaticProperties.IngredientRepresentation.EXPANDED;
	
	props.initialize(request);
	
	data.initialize(props,false);
	
	Integer counter = 0;
	int counterIterate = 0;
	Collection<Double> values = new ArrayList<Double>();
		
	// Initialize Catryzer
	catryzer = new CatRyzer(data.recipes,props.locale);

	// ------------------------ Define Categories Here ------------------------
	
	List<CatRyzer.CatFormula>  categoryFormulas = new ArrayList<CatRyzer.CatFormula>();
	
	categoryFormulas.add(catryzer.new CatFormula("<strong>Pflanzliche Produkte</strong>","rice,spices&herbs,sweets,oil and fat,-animal-based,-animal based,legumes,fruits,mushrooms,preprocessed,grain,nuts,seeds",true));
	categoryFormulas.add(catryzer.new CatFormula("&nbsp;&nbsp;Reisprodukte","rice"));
	categoryFormulas.add(catryzer.new CatFormula("&nbsp;&nbsp;Gewürze & Kräuter","spices&herbs"));
	categoryFormulas.add(catryzer.new CatFormula("&nbsp;&nbsp;Süssigkeiten","sweets"));
	categoryFormulas.add(catryzer.new CatFormula("&nbsp;&nbsp;Pflanzliche Öle und Fette","oil and fats,-animal-based,-animal based"));
	categoryFormulas.add(catryzer.new CatFormula("&nbsp;&nbsp;Gemüse und Früchte","legumes,fruits,mushrooms"));
	categoryFormulas.add(catryzer.new CatFormula("&nbsp;&nbsp;Verarbeitete Gemüseprodukte","preprocessed,-animal-based,-animal based"));
	categoryFormulas.add(catryzer.new CatFormula("&nbsp;&nbsp;Brot und Getreiteprodukte","grains"));
	categoryFormulas.add(catryzer.new CatFormula("&nbsp;&nbsp;Nüsse und Samen","nuts,seeds"));
	
	categoryFormulas.add(catryzer.new CatFormula("<strong>Tierische Produkte</strong>","ruminant,non-ruminant,fish,seafood,diary,oil and fats,-vegetable,eggs,finished product,sauces",true));
	categoryFormulas.add(catryzer.new CatFormula("&nbsp;&nbsp;<strong>Fleischprodukte</strong>","ruminant,non-ruminant,fish,seafood",true));
	categoryFormulas.add(catryzer.new CatFormula("&nbsp;&nbsp;&nbsp;&nbsp;Wiederkäuer","ruminant"));
	categoryFormulas.add(catryzer.new CatFormula("&nbsp;&nbsp;&nbsp;&nbsp;Nichtwiederkäuer","non-ruminant"));
	categoryFormulas.add(catryzer.new CatFormula("&nbsp;&nbsp;&nbsp;&nbsp;Fisch und Meeresfrüchte","fish,seafood"));
	
	categoryFormulas.add(catryzer.new CatFormula("&nbsp;&nbsp;<strong>Milchprodukte</strong>","diary",true));
	categoryFormulas.add(catryzer.new CatFormula("&nbsp;&nbsp;&nbsp;&nbsp;gereifter Käse","ripened"));
	categoryFormulas.add(catryzer.new CatFormula("&nbsp;&nbsp;&nbsp;&nbsp;Frischkäse und übrige Milchprodukte","diary,-ripened"));
	categoryFormulas.add(catryzer.new CatFormula("Tierische Fette","oil and fats,-vegetable"));
	categoryFormulas.add(catryzer.new CatFormula("Eier und Eibasierte Produkte","eggs"));
	categoryFormulas.add(catryzer.new CatFormula("Dosen und verarbeitete Produkte","finished product"));
	categoryFormulas.add(catryzer.new CatFormula("Saucen","sauces"));
	
	categoryFormulas.add(catryzer.new CatFormula("<strong>Getränke</strong>","beverage",true));
	categoryFormulas.add(catryzer.new CatFormula("&nbsp;&nbsp;Getränke (auf Alkoholbasis)","alcohol"));
	categoryFormulas.add(catryzer.new CatFormula("&nbsp;&nbsp;Getränke (auf Fruchtbasis)","fruitjuice"));
	categoryFormulas.add(catryzer.new CatFormula("&nbsp;&nbsp;Getränke (auf Milchbasis)","milk"));
	categoryFormulas.add(catryzer.new CatFormula("&nbsp;&nbsp;Getränke (weitere)","beverage,-alcohol,-fruitjuice,-milk"));
	
	catryzer.setCatFormulas(categoryFormulas);
	
	List<CatRyzer.DateValue> valuesByDate = catryzer.getDateValues();
	List<CatRyzer.CategoryValue> valuesByCategory = catryzer.getCatVals();
	List<CatRyzer.CategoryValuesByDates> valuesByDate_Category = catryzer.getCatValsByDates(); 

%>

<style type="text/css">
<!-- @import url(http://fonts.googleapis.com/css?family=Open+Sans:300,400,600,700,800); -->

@font-face {
  font-family: 'Open Sans';
  font-style: normal;
  font-weight: 300;
  src: local('Open Sans Light'), local('OpenSans-Light'), url('opensans300.woff') format('woff');
}

@page {
  size: A4;
  margin: 40pt 20pt 70pt 40pt;
   padding: 30pt 0pt 0pt 0pt;
	prince-image-resolution: 300dpi;

    @bottom-right { 
        content: counter(page) "/" counter(pages);
		font: 9pt 'Open Sans', Verdana, sans-serif; font-weight: 300;
		 padding-right: 20pt;
		 padding-top: 5pt;
    }
    @bottom { 
      font: 9pt 'Open Sans', Verdana, sans-serif; font-weight: 300;
      }
    @bottom-left { 
      content: flow(footer,start);
     
      }
      
    @top {
     	content: flow(header,start);
    }

}

@page big_table { 
    size: A4 landscape
}
</style>

</head>

<!--  ----------------------------------- Body Begin --------------------------------------- -->

<body>
<% 
// Avoid displaying anything if someting is wrong.
if (!data.everythingFine){
	%>
		Wrong Inputs. See Log for Details.<br /><br />
		<%= data.errorMessage %>
	<%
	
}
else { %>
	


<div class="website-content">

<div id="header-right">
	<img class="logo-klein" src="logo-eaternity-huge_04-11-2010.png" alt="logo-eaternity-huge_04-11-2010" />
</div>


<div id="kopf-logo" class="kopf">
	<img class="logo" src="logo-eaternity-huge_04-11-2010.png" alt="logo-eaternity-huge_04-11-2010" />

	<ul>
	<li>Viaduktstrasse 93-95</li>
	<li>CH-8005 Zürich</li>
	<li><a href="mailto:info@eaternity.ch" >info@eaternity.ch</a></li>
	<li><a href="http://www.eaternity.ch">www.eaternity.ch</a></li>
	</ul>

</div>


<div  id="footer-left">
	<img class="logo-karotte" src="karotte.png" alt="karotte"  />
	Eaternity &#10031; Genuss. Bewusst. Erleben.
	<a href="mailto:info@eaternity.ch" >info@eaternity.ch</a>
	<a href="www.eaternity.ch">www.eaternity.ch</a>
</div>



<div class="content">

<h1>CO2 Nahrungsbeschaffungsreport?</h1>

<% // -------------------------------- Delivery Receipts Overview --------------------------- %>

<table cellspacing="0" cellpadding="0" class="table toc" >

<tr>
<td></td>
<td class="gray left-border"></td>
<td class="gray co2label"><span class="nowrap">kg CO<sub>2</sub>*</span></td>
<td></td>
</tr>

<tr>
<td class="table-header bottom-border">Lieferscheine Übersicht</td>
<td class="left-border"></td>
<td class="co2value" ></td>
<td ></td>
</tr>

<%
for(Recipe recipe: data.recipes){
	values.add(recipe.getCO2Value());
}
for(Recipe recipe: data.recipes){

	Double recipeValue = recipe.getCO2Value();
	
	String clear = Converter.toString(recipe.getId(),34);
	String length = Util.getNormalisedLength(recipeValue, values);
	String recipeValueFormatted = co2_formatter.format(recipeValue/1000);
	%>
			
	<tr <%
	int order = (data.recipes.indexOf(recipe) - counterIterate ) % 2; 
	if(order == 1) { %>
	class="alternate"
	<% }%> > 
	<td class="menu-name">
	<input type="checkbox" name="<%= clear %>" checked="checked" class="hiddenOnPage">
	<%= recipe.getSymbol() %>
	</td>
	<td class="left-border"><img class="bar" src="green.png" alt="gray" height="11" width="<%= length %>" /></td>
	<td class="co2value" ><%= recipeValueFormatted %></td>
	</tr>


<% 
} 
%>


</table>

<br/><br/><br/><br/>

<% // -------------------------------- Total Key Values Overview -------------------------------- %>

<%
temp.clear();
for (Recipe recipe : data.recipes) {
	temp.ingredients.addAll(recipe.getZutaten());
}

temp.personFactor = 1;
%>

<jsp:include page="/jsp_snippets/snippet_totalvalues.jsp" /> 


<% // -------------------------------- Total CO2 Impact by Date --------------------------- %>

<table cellspacing="0" cellpadding="0" class="table toc" >

<tr>
<td></td>
<td class="gray left-border"></td>
<td class="gray co2label"><span class="nowrap">kg CO<sub>2</sub>*</span></td>
<td></td>
</tr>

<tr>
<td class="table-header bottom-border">Gesamte CO2-Emissionen nach Datum</td>
<td class="left-border"></td>
<td class="co2value" ></td>
<td ></td>
</tr>

<%
counterIterate = 0;
data.maxValTemp = 0.0;
data.minValTemp = 10000000.0;


for(CatRyzer.DateValue categoryValue : data.valuesByDate){
	values.add(categoryValue.co2value.totalValue);
}

for(CatRyzer.DateValue categoryValue : data.valuesByDate){
	
%>

<tr <%
int order = (data.valuesByDate.indexOf(categoryValue) - counterIterate ) % 2; 
if(order == 1) { %>
class="alternate"
<% }%> > 
<td class="menu-name">
	<%
	String datumString = "NO DATE SPECIFIED";
	try {
		datumString = data.dateFormatter.format(categoryValue.date);
	} catch (Exception e) {
		            out.println("The Error is: " + e);
	}
	%><%= datumString %>
</td>
<td class="left-border" width="<%= co2BarLength + barOffset %>px"><%= data.getCo2ValueBar(values, categoryValue.co2value, co2BarLength) %></td>
<td class="co2value" ><%= co2_formatter.format(categoryValue.co2value.totalValue/1000) %></td>
</tr>


<%
}
%>

</table>



<br /><br /><br />


<% // -------------------------------- Top intensive Ingredients --------------------------- %>

<%
temp.clear();
for (Recipe recipe : data.recipes) {
	temp.ingredients.addAll(recipe.getZutaten());
}

temp.personFactor = 1;
temp.stopIndex = 20;
%>


<jsp:include page="/jsp_snippets/snippet_ingredients_ranking.jsp" />

<!--  -------------------------------- Total CO2 Impact by Category --------------------------- -->
<% 
temp.title = "Gesamte CO2-Emissionen nach Kategorie";
%>

<jsp:include page="/jsp_snippets/snippet_categories.jsp" />

<br /><br /><br /><br />

<% // -------------------------------- Total CO2 Impact by Date - Category --------------------------- %>

<%
counterIterate = 0;
for(CatRyzer.CategoryValuesByDates categoriesByDates : catryzer.getCatValsByDates()) {

	Date thisDate = categoriesByDates.date.get(0); %>

	<!--   ---------Total CO2 Impact by Category (per one Date) ---- -->
	
	<% 
	String datumString = "NO DATE SPECIFIED";
	try {
		datumString = props.dateFormatter.format(thisDate); }
	catch (Exception e) {
		  out.println("NO DATE SPECIFIED: The Error is: " + e);
	}
	
	temp.title = props.dateFormatter.format(thisDate) + "Gesamte CO2-Emissionen nach Kategorie";
	%>
	
	<jsp:include page="/jsp_snippets/snippet_categories.jsp" />
	

<!-- ------------ Delivery Receipt Overview -------------- -->

<table cellspacing="0" cellpadding="0" class="table new-page listTable" >
<tr>
<td class="table-header">Lieferscheine Übersicht für den   
	<%
	datumString = "NO DATE SPECIFIED";
	try {
		datumString = data.dateFormatter.format(thisDate);
	} catch (Exception e) {
		  out.println("The Error is: " + e);
	}
	%><%= datumString %>
	</td>
<td></td>
</tr>

<tr>
<td class="bottom-border"></td>
<td class="left-border"></td>
</tr>

</table>
	

	<%

	// valuesByDate_Calender

	for(Recipe recipe: data.recipes){

		if(thisDate.equals(recipe.cookingDate)){

				recipe.setCO2Value();
				Double recipeValue = recipe.getCO2Value()  ;

				String formatted = co2_formatter.format( recipeValue/1000 );
				
				datumString = "NO DATE SPECIFIED";
				try {
					datumString = data.dateFormatter.format(recipe.cookingDate);
				} catch (Exception e) {
					  out.println("The Error is: " + e);
				}


				%>

				<table cellspacing="0" cellpadding="0" class="table listTable" >
				<tr>
				<td></td>
				<td class="left-border"><br></td>
				</tr>

				<tr>
				<td class="bottom-border">
				<!-- <img class="smile" src="smiley8.png" alt="smiley" />
				<img class="smile" src="smiley8.png" alt="smiley" /> -->
				<h3>Lieferschein: <%= recipe.getSymbol() %></h3>
				</td>
				<td class="left-border"></td>
				</tr>

				<tr>
				<td><div class="amount"><%= formatted %> kg CO<sub>2</sub>* total</div></td>
				<td class="left-border"><img class="bar" height="11"  src="gray.png" alt="gray" width="200" /></td>
				</tr>

				<tr>
				<td>

				<span class="subTitle">Datum: <%= datumString %></span>

				<!-- <span style="color:gray;"><%= recipe.getSubTitle() %>/span><br /> -->


					<%	
					counter = 0;
					for(IngredientSpecification ingredient: recipe.Zutaten){
					counter = counter + 1;

					%><% if(counter != 1){ %>, <% } %><span class="nowrap"><%= ingredient.getMengeGramm() %> g <%= ingredient.getName() %> 
						
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
	}
}
%>

</table>



<% // -------------------------------- Overview Categories --------------------------- %>


<jsp:include page="/jsp_snippets/snippet_totalvalues.jsp" /> 



</div>

<div id="footer-bottom">
	<img class="logo-karotte" src="karotte.png" alt="karotte"  />
	Eaternity &#10031; Genuss. Bewusst. Erleben.
	<a href="mailto:info@eaternity.ch" >info@eaternity.ch</a>
	<a href="www.eaternity.ch">www.eaternity.ch</a>
	
</div>

</div>
<% if(data.doItWithPermanentIds) { %>
<div class="login">
	<%

	    if (data.user != null) {
	%>
	Diese Angaben sind für den Benutzer <%= data.user.getNickname() %>. <a href="<%= data.userService.createLogoutURL(request.getRequestURI()) %>">Abmelden</a>?
	<%
	    } else {
	    	if (data.tempIds == null){
	%>
	Sie sind nicht angemeldet.
	<a href="<%= data.userService.createLoginURL(request.getRequestURI()) %>">Anmelden</a>?
	<%	
			} else {
			
			%>
			Zurück zur <a href="/view.jsp">Übersicht</a>?
			<%
			
			}
	    }
	%>

</div>


<%
}// 
%>

<%
}// if everthing is fine
%>


</body>


</HTML>



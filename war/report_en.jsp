<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@ page contentType="text/html;charset=UTF-8" language="java" %>


<%@ page import="ch.eaternity.server.StaticPageService" %>

<%@ page import="ch.eaternity.shared.Recipe" %>
<%@ page import="ch.eaternity.shared.RecipeComment" %>
<%@ page import="ch.eaternity.shared.IngredientSpecification" %>

<%@ page import="ch.eaternity.shared.Converter" %>
<%@ page import="ch.eaternity.shared.CatRyzer" %>

<%@ page import="java.util.Date" %>
<%@ page import="java.util.Locale" %>
<%@ page import="java.util.Set" %>
<%@ page import="java.util.Collection" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.text.DecimalFormat" %>





<html>
<head>

<meta HTTP-EQUIV="Content-Type" CONTENT="text/html; charset=UTF-8">
<title>Klima-Bilanz Report</title>
<link rel="stylesheet" type="text/css" href="report.css">


<%
// get request parameters

String BASEURL = request.getRequestURL().toString();
String tempIds = request.getParameter("ids");
String permanentId = request.getParameter("pid");
String kitchenId = request.getParameter("kid");
String pdf = request.getParameter("pdf");


// ------------------------ Define Categories Here ------------------------

CatRyzer catryzer = new CatRyzer();
List<CatRyzer.CatFormula>  categoryFormulas = new ArrayList<CatRyzer.CatFormula>();

//CatFormula(String category, String formula, boolean isHeading)
categoryFormulas.add(catryzer.new CatFormula("<strong>Vegetable Products</strong>","rice,spices&herbs,sweets,oil and fat,-animal-based,-animal based,legumes,fruits,mushrooms,preprocessed,grain,nuts,seeds",true));
categoryFormulas.add(catryzer.new CatFormula("&nbsp;&nbsp;Rice products","rice"));
categoryFormulas.add(catryzer.new CatFormula("&nbsp;&nbsp;Spices & herbs","spices&herbs"));
categoryFormulas.add(catryzer.new CatFormula("&nbsp;&nbsp;Sweets","sweets"));
categoryFormulas.add(catryzer.new CatFormula("&nbsp;&nbsp;Vegetable oils and fat","oil and fats,-animal-based,-animal based"));
categoryFormulas.add(catryzer.new CatFormula("&nbsp;&nbsp;Vegetables and fruits","legumes,fruits,mushrooms"));
categoryFormulas.add(catryzer.new CatFormula("&nbsp;&nbsp;Preprocessed vegetable products","preprocessed,-animal-based,-animal based"));
categoryFormulas.add(catryzer.new CatFormula("&nbsp;&nbsp;Bread and Grain Products","grains"));
categoryFormulas.add(catryzer.new CatFormula("&nbsp;&nbsp;Nuts und seeds","nuts,seeds"));

categoryFormulas.add(catryzer.new CatFormula("<strong>Animal Products</strong>","ruminant,non-ruminant,fish,seafood,diary,oil and fats,-vegetable,eggs,finished product,sauces",true));
categoryFormulas.add(catryzer.new CatFormula("&nbsp;&nbsp;<strong>Meat Products</strong>","ruminant,non-ruminant,fish,seafood",true));
categoryFormulas.add(catryzer.new CatFormula("&nbsp;&nbsp;&nbsp;&nbsp;Ruminants","ruminant"));
categoryFormulas.add(catryzer.new CatFormula("&nbsp;&nbsp;&nbsp;&nbsp;Non-ruminants","non-ruminant"));
categoryFormulas.add(catryzer.new CatFormula("&nbsp;&nbsp;&nbsp;&nbsp;Fish and seafood","fish,seafood"));

categoryFormulas.add(catryzer.new CatFormula("&nbsp;&nbsp;<strong>Diary Products</strong>","diary",true));
categoryFormulas.add(catryzer.new CatFormula("&nbsp;&nbsp;&nbsp;&nbsp;Ripened cheese","ripened"));
categoryFormulas.add(catryzer.new CatFormula("&nbsp;&nbsp;&nbsp;&nbsp;Fresh cheese and diary products","diary,-ripened"));
categoryFormulas.add(catryzer.new CatFormula("Animal based fats","oil and fats,-vegetable"));
categoryFormulas.add(catryzer.new CatFormula("Eggs and egg based products","eggs"));
categoryFormulas.add(catryzer.new CatFormula("Canned and finished products","finished product"));
categoryFormulas.add(catryzer.new CatFormula("Sauces","sauces"));

categoryFormulas.add(catryzer.new CatFormula("<strong>Drinks</strong>","beverage",true));
categoryFormulas.add(catryzer.new CatFormula("&nbsp;&nbsp;Drinks (alkohol based)","alcohol"));
categoryFormulas.add(catryzer.new CatFormula("&nbsp;&nbsp;Drinks (fruit based)","fruitjuice"));
categoryFormulas.add(catryzer.new CatFormula("&nbsp;&nbsp;Drinks (milk based)","milk"));
categoryFormulas.add(catryzer.new CatFormula("&nbsp;&nbsp;Drinks (others)","beverage,-alcohol,-fruitjuice,-milk"));

StaticPageService variables = new StaticPageService(BASEURL,tempIds,permanentId,kitchenId,pdf,categoryFormulas,Locale.ENGLISH);

int counter = 0;
int counterIterate = 0;
Collection<Double> values = new ArrayList<Double>();
DecimalFormat co2_formatter = new DecimalFormat("##");


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

<body>
<% 
// Avoid displaying anything if someting is wrong.
if (!variables.everythingFine){
	%>
		Wrong Inputs. See Log for Details.<br /><br />
		<%= variables.errorMessage %>
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

<h1>CO2 Food-Sourcing Report</h1>

<% // -------------------------------- Delivery Receipts Overview --------------------------- %>

<table cellspacing="0" cellpadding="0" class="table toc" >

<tr>
<td></td>
<td class="gray left-border"></td>
<td class="gray co2label"><span class="nowrap">kg CO<sub>2</sub>*</span></td>
<td></td>
</tr>

<tr>
<td class="table-header bottom-border">Delivery Receipts Overview</td>
<td class="left-border"></td>
<td class="co2value" ></td>
<td ></td>
</tr>

<%
for(Recipe recipe: variables.kitchenRecipes){

	recipe.setCO2Value();
	Double recipeValue = recipe.getCO2Value()  ;
	
	String clear = Converter.toString(recipe.getId(),34);
	String length = variables.getNormalisedLength(recipeValue);
	String recipeValueFormatted = co2_formatter.format(recipeValue/1000);
	%>
			
	<tr <%
	int order = (variables.kitchenRecipes.indexOf(recipe) - counterIterate ) % 2; 
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

<% // -------------------------------- Total CO2 Impact by Date --------------------------- %>

<table cellspacing="0" cellpadding="0" class="table toc" >

<tr>
<td></td>
<td class="gray left-border"></td>
<td class="gray co2label"><span class="nowrap">kg CO<sub>2</sub>*</span></td>
<td></td>
</tr>

<tr>
<td class="table-header bottom-border">Total CO2 Impact by Date</td>
<td class="left-border"></td>
<td class="co2value" ></td>
<td ></td>
</tr>

<%
counterIterate = 0;
variables.maxValTemp = 0.0;
variables.minValTemp = 10000000.0;

for(CatRyzer.DateValue categoryValue : variables.valuesByDate){
	values.add(categoryValue.co2value);
}
variables.setMinMax(values);


for(CatRyzer.DateValue categoryValue : variables.valuesByDate){
		String length = variables.getNormalisedLength(categoryValue.co2value);
	
%>

<tr <%
int order = (variables.valuesByDate.indexOf(categoryValue) - counterIterate ) % 2; 
if(order == 1) { %>
class="alternate"
<% }%> > 
<td class="menu-name">
	<%
	String datumString = "NO DATE SPECIFIED";
	try {
		datumString = variables.dateFormatter.format(categoryValue.date);
	} catch (Exception e) {
		            out.println("The Error is: " + e);
	}
	%><%= datumString %>
</td>
<td class="left-border"><img class="bar" src="green.png" alt="gray" height="11" width="<%= length %>" /></td>
<td class="co2value" ><%= co2_formatter.format(categoryValue.co2value/1000) %></td>
</tr>


<%
}
%>

</table>



<br /><br /><br />


<% // -------------------------------- Top 20 intensive Ingredients --------------------------- %>

<table cellspacing="0" cellpadding="0" class="table toc" >

<tr>
<td></td>
<td class="gray left-border"></td>
<td class="gray co2label"><span class="nowrap">kg CO<sub>2</sub>*</span></td>
<td></td>
</tr>

<tr>
<td class="table-header bottom-border">Top 20 CO<sub>2</sub>-intensive Ingredients</td>
<td class="left-border"></td>
<td class="co2value" ></td>
<td ></td>
</tr>



<%
counterIterate = 0;
variables.maxValTemp = 0.0;
variables.minValTemp = 10000000.0; 

values.clear();

//  go over the Recipes in the Workspace
for(CatRyzer.CategoryValue ingredientValue : variables.valuesByIngredient){
	values.add(ingredientValue.co2value);
}
variables.setMinMax(values);


for(CatRyzer.CategoryValue ingredientValue : variables.valuesByIngredient){
	String length = variables.getNormalisedLength(ingredientValue.co2value);
	
	if (variables.valuesByIngredient.indexOf(ingredientValue) == 20){
		break;
	} 
%>

<tr <%
int order = (variables.valuesByCategory.indexOf(ingredientValue) - counterIterate ) % 2; 
if(order == 1) { %>
class="alternate"
<% }%> > 
<td class="menu-name">
<%= ingredientValue.categoryName %> <!-- (<%=ingredientValue.weight/1000%> kg) -->
</td>
<td class="left-border"><img class="bar" src="green.png" alt="gray" height="11" width="<%= length %>" /></td>
<td class="co2value" ><%= co2_formatter.format(ingredientValue.co2value/1000) %></td>

</tr>

<%
}
%>
</table>


<% // -------------------------------- Total CO2 Impact by Category --------------------------- %>

<table cellspacing="0" cellpadding="0" class="table toc" >

<tr>
<td></td>
<td class="gray left-border"></td>
<td class="gray co2label"><span class="nowrap">kg CO<sub>2</sub>*</span></td>
<td></td>
</tr>

<tr>
<td class="table-header bottom-border">Total CO2 Impact by Category</td>
<td class="left-border"></td>
<td class="co2value" ></td>
<td ></td>
</tr>

<%
counterIterate = 0;
variables.maxValTemp = 0.0;
variables.minValTemp = 10000000.0; 

values.clear();
//  go over the Recipes in the Workspace
for(CatRyzer.CategoryValue categoryValue : variables.valuesByCategory){
	values.add(categoryValue.co2value);
}
variables.setMinMax(values);


for(CatRyzer.CategoryValue categoryValue : variables.valuesByCategory){
	String length = variables.getNormalisedLength(categoryValue.co2value);
%>

	<tr <%
	int order = (variables.valuesByCategory.indexOf(categoryValue) - counterIterate ) % 2; 
	if(order == 1) { %>
	class="alternate"
	<% }%> > 
	<td class="menu-name">
	<%= categoryValue.categoryName %>
	</td>
	<td class="left-border"><img class="bar" src="green.png" alt="gray" height="11" width="<%= length %>" /></td>
	<td class="co2value" ><%= co2_formatter.format(categoryValue.co2value/1000) %></td>
	</tr>

<%
}
%>

</table>


<br /><br /><br /><br />

<% // -------------------------------- Total CO2 Impact by Date - Category --------------------------- %>

<%
counterIterate = 0;
for(CatRyzer.CategoryValuesByDates categoriesByDates : variables.valuesByDate_Category){

// if date == 0, show something for no date
	
	Date thisDate = categoriesByDates.date.get(0);


%>
<table cellspacing="0" cellpadding="0" class="table toc" >

<tr>
<td></td>
<td class="gray left-border"></td>
<td class="gray co2label"><span class="nowrap">kg CO<sub>2</sub>*</span></td>
<td></td>
</tr>

<tr>
<td class="table-header bottom-border">	<%
	String datumString = "NO DATE SPECIFIED";
	try {
		datumString = variables.dateFormatter.format(thisDate);
	} catch (Exception e) {
		  out.println("The Error is: " + e);
	}
	%><%= datumString %>  -  CO2 Impact by Category</td>
<td class="left-border"></td>
<td class="co2value" ></td>
<td ></td>
</tr>

<%
	counterIterate = 0;
variables.maxValTemp = 0.0;
variables.minValTemp = 10000000.0; 

values.clear();
//  go over the Recipes in the Workspace
for(CatRyzer.CategoryValue categoryValue : categoriesByDates.categories){
	values.add(categoryValue.co2value);
}
variables.setMinMax(values);

// -------------------------------- Total CO2 Impact by Category (per one Date) --------------------------- 

for(CatRyzer.CategoryValue categoryValue : categoriesByDates.categories){
	String length = variables.getNormalisedLength(categoryValue.co2value);
%>

<tr <%int order = (categoriesByDates.categories.indexOf(categoryValue) - counterIterate ) % 2; 
if(order == 1) {%>
class="alternate"
<% }%> > 
<td class="menu-name">
<%= categoryValue.categoryName %>
</td>
<td class="left-border"><img class="bar" src="green.png" alt="gray" height="11" width="<%= length %>" /></td>
<td class="co2value" ><%= co2_formatter.format(categoryValue.co2value/1000) %></td>
</tr>


<%
}	
%>
</table>

<table cellspacing="0" cellpadding="0" class="table new-page listTable" >
<tr>
<td class="table-header">Delivery Receipts Overview for  
	<%
	datumString = "NO DATE SPECIFIED";
	try {
		datumString = variables.dateFormatter.format(thisDate);
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

	for(Recipe recipe: variables.kitchenRecipes){

		if(thisDate.equals(recipe.cookingDate)){

				recipe.setCO2Value();
				Double recipeValue = recipe.getCO2Value()  ;

				String formatted = co2_formatter.format( recipeValue/1000 );
				
				datumString = "NO DATE SPECIFIED";
				try {
					datumString = variables.dateFormatter.format(recipe.cookingDate);
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
				<h3>Delivery Receipt: <%= recipe.getSymbol() %></h3>
				</td>
				<td class="left-border"></td>
				</tr>

				<tr>
				<td><div class="amount"><%= formatted %> kg CO<sub>2</sub>* total</div></td>
				<td class="left-border"><img class="bar" height="11"  src="gray.png" alt="gray" width="200" /></td>
				</tr>

				<tr>
				<td>

				<span class="subTitle">Date: <%= datumString %></span>

				<!-- <span style="color:gray;"><%= recipe.getSubTitle() %>/span><br /> -->


					<%	
					counter = 0;
					for(IngredientSpecification ingredient: recipe.Zutaten){
					counter = counter + 1;

					%><% if(counter != 1){ %>, <% } %><span class="nowrap"><%= ingredient.getMengeGramm() %> g <%= variables.catryzer.getIngredientName_en(ingredient) %> 
						
						( <% if(ingredient.getHerkunft() != null){ %><%= ingredient.getHerkunft().symbol %><% } %>  | <% if(ingredient.getZustand() != null){ %><%= ingredient.getZustand().symbol %> | <% } %><% if(ingredient.getProduktion() != null){ %><%= ingredient.getProduktion().symbol %> | <% } %> <% if(ingredient.getTransportmittel() != null){ %><%= ingredient.getTransportmittel().symbol %><% } %> )
						
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

<br /><br /><br />
<h2>Overview Categories</h2>
<br /><br /><br />

<%
// get ingredients per category
for(CatRyzer.CatMapping mapping : variables.catryzer.mappings)
{
	
	Collection<IngredientSpecification> ingredientsSpecification = variables.catryzer.catMultiMap.get(mapping.category);
	Set<String> ingredientsNames = variables.catryzer.getIngredientsNames_en(ingredientsSpecification);
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
		<h3>Category: <%= mapping.category %></h3>
		</td>
		<td class="left-border"></td>
	</tr>
	
	<tr>
		<td>
		<%	
		counter = 0;
		for(String ingredient: ingredientsNames){
		counter = counter + 1;
		
		%><% if(counter != 1){ %>, <% } %><span class="nowrap"><%= ingredient %></span><%
		}
		%>
		
		</td>
		<td class="left-border"><br></td>
	</tr>
	
	<tr>
		<td></td>
		<td class="left-border"><br></td>
	</tr>
	
	<tr>
		<td></td>
		<td class="left-border"><br></td>
	</tr>
</table>

<%
}
%>








</div>

<div id="footer-bottom">
	<img class="logo-karotte" src="karotte.png" alt="karotte"  />
	Eaternity &#10031; Genuss. Bewusst. Erleben.
	<a href="mailto:info@eaternity.ch" >info@eaternity.ch</a>
	<a href="www.eaternity.ch">www.eaternity.ch</a>
	
</div>

</div>
<% if(variables.doItWithPermanentIds) { %>
<div class="login">
	<%

	    if (variables.user != null) {
	%>
	Diese Angaben sind für den Benutzer <%= variables.user.getNickname() %>. <a href="<%= variables.userService.createLogoutURL(request.getRequestURI()) %>">Abmelden</a>?
	<%
	    } else {
	    	if (variables.tempIds == null){
	%>
	Sie sind nicht angemeldet.
	<a href="<%= variables.userService.createLoginURL(request.getRequestURI()) %>">Anmelden</a>?
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


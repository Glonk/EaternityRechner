<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@ page contentType="text/html;charset=UTF-8" language="java" %>


<%@ page import="ch.eaternity.server.StaticPageService" %>

<%@ page import="ch.eaternity.shared.Recipe" %>
<%@ page import="ch.eaternity.shared.RecipeComment" %>
<%@ page import="ch.eaternity.shared.IngredientSpecification" %>

<%@ page import="ch.eaternity.shared.Converter" %>
<%@ page import="ch.eaternity.shared.CatRyzer" %>

<%@ page import="java.util.Date" %>
<%@ page import="java.util.Set" %>
<%@ page import="java.util.Collection" %>




<html>
<head>

<meta HTTP-EQUIV="Content-Type" CONTENT="text/html; charset=UTF-8">
<title>Klima-Bilanz Report</title>


<%
// get request parameters

String BASEURL = request.getRequestURL().toString();
String tempIds = request.getParameter("ids");
String permanentId = request.getParameter("pid");
String kitchenId = request.getParameter("kid");
String pdf = request.getParameter("pdf");

StaticPageService variables = new StaticPageService(BASEURL,tempIds,permanentId,kitchenId,pdf);

int counter = 0;
int counterIterate = 0;


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

table.big { page: big_table }


	#footer-bottom {
	display: none;
	}

body { 
	font-family: 'Open Sans', Verdana, sans-serif; 
	font-weight: 300;
    font-size: 11pt;
    image-resolution: 70dpi;
     }

.hiddenOnPage {
display: none;
}

h1 { 
	font-size: 32pt;
	font-weight: 300;
/*   page-break-before: always; */
  page-break-after: avoid;
  text-align: left;
/*   clear: both;  */
/*   float: left; */
  width: 20em;
  margin-top: 1em;
}

h2 { 
  page-break-after: avoid;

  text-align: center;
    text-align: left;
    font-size: 11pt;
    font-weight: 400;
}
      
h3 { 
  page-break-after: avoid;
	font-weight: 400;
    font-size: 11pt;
    padding: 0px;
    margin: 0px;
    word-wrap: normal;
    clear: none;
    display:compact;
    float:left;
    margin-bottom: -0em;
/*     display: inline; */
}

.label-vergleich {
	font-weight: 400;
    font-size: 11pt;
    padding: 0px;
    margin: 0px;
    word-wrap: normal;
    float:left;

}

.content {
/*     page-break-after: always; */
	margin: 30pt 40pt 30pt 20pt;
}

.page-break {
    page-break-after: always;
}

.new-page {
    page-break-before: always;
}

#header-right {
	float:right;
	margin-bottom: 10px;
	flow: static(header);
}

table {
	  page-break-inside: avoid;
}
.table {
 font-weight: 300;
  font-size: 11pt;
  width: 35em;
  padding-bottom: 1em;
  margin-bottom: 2em;
}

.label-table {
width: 41em;
margin:0 auto; 
}

.rounded {
/* padding:10px; */
border-style: solid;
border-width: 1px;
border-color: black; 
-moz-border-radius: 6px;
border-radius: 6px;
/* border-spacing: 0; */
}


.rounded tr:first-child td:first-child {
-moz-border-radius-topleft:6px;
-webkit-border-top-left-radius:6px;
border-top-left-radius:6px;
}

.rounded tr:first-child td:last-child {
-moz-border-radius-bottomtop:6px;
-webkit-border-top-right-radius:6px;
border-top-right-radius:6px;
}

.rounded tr:last-child td:first-child {
-moz-border-radius-bottomleft:6px;
-webkit-border-bottom-left-radius:6px;
border-bottom-left-radius:6px;
}

.rounded tr:last-child td:last-child {
-moz-border-radius-bottomright:6px;
-webkit-border-bottom-right-radius:6px;
border-bottom-right-radius:6px;
}

.howAreYou {
padding-right: 2.5em;
font-size: 9pt;
font-weight: 400;
}

.kopf { 
	float: right;
	text-align: right;
	font-size: 10pt;
	font-weight: 300;
	font-size: 13px;
	margin-top: -54pt;
	margin-right: 6pt;
}

.kopf ul {
	margin-right: 24pt; 
	margin-top: 0pt;
	}
.logo {
	width:153pt;
	height:68pt;
}

.logo-klein {
margin-top: 50pt;
	height:46pt;
}



#footer-left {
/* 	display: none; */
float:left;
flow: static(footer);
	font-size: 9pt;
	font-weight: 300;
}

#footer-bottom {
/* 	display: none; */
float:left;
	font-size: 9pt;
	font-weight: 300;
}

.logo-karotte {
	margin-right: 0pt;
	margin-bottom: 0pt;
	height:14pt;
}

.subTitle {
margin-top: -1em;
margin-left: 1em;
padding-right: 8em;
display: block;
margin-bottom: 0.5em;
}

.smile{
height: 18pt;
margin-bottom: -4pt;
margin-left: -4pt;
margin-top: -2pt;
margin-right: 4px;
    display: inline;
    float:left;
}

.bar {
padding: 1pt 1pt 1pt 0pt;
}

.table-space {
	width: 10pt;
}

.green {
page-break-after: avoid;
color: white;
background-repeat: repeat-x;
background-image: url(green.png);
/*background: green.png;*/
font-size: 10pt;
font-weight: 700;
text-align: right;
padding: 2pt;
padding-right: 4pt;
width:14em;
}

.gray {
page-break-after: avoid;
color: white;
background-repeat: repeat-x;
background-image: url(light-gray.png);
/*background: light-gray.png;*/
font-size: 10pt;
font-weight: 700;
text-align: right;
padding: 2pt;
padding-right: 4pt;
/* width: 100pt; */
}
.co2label {
display: inline-table;
}

.co2value {
text-align: right;
padding-left: 0.2em;
padding-right: 0.2em;
display: inline-table;
vertical-align: middle;

/* color:gray; */
}

.co2percent {
	text-align: right;
	padding-left: 0.2em;
	padding-right: 0.2em;
/*	display: inline-table;*/
	vertical-align: middle;
	background:#F7F7F7;
	white-space: nowrap; 
}

.alternate {
background-color: #FBF9F8;
}

.amount {
float: right;
padding-right: 4pt;
margin-top: 0em;
}

.menu-name {
vertical-align:top;
padding-left: 1em;
padding-top: 0.2em;
padding-bottom: 0.2em;
/* width: 25em; */
/* display: inline-table; */
padding-right: 2em;

}

.suggest {
text-align: right;
padding-right: 4pt;
/* padding-bottom: 1pt; */
}


.tips {
margin-left: -10pt;
padding: 1em 0.5em 0.5em 3em;
}

.tips li {
	list-style-position: outside;
	list-style-type: disc;
}

.zutat {
margin-top: -10pt;
margin-left: -10pt;
}

.zutat li {
	list-style-position: outside;
	list-style-type: none;
	display: inline;
}

.zutatRow {
width: 40em;
margin-left: 5pt;
}

.zutatRow li {
	list-style-type: circle;
	display: inline;
}

a { padding-left: 5pt; color: #0e5396; text-decoration: none }

a:hover {text-decoration:underline }

.login {
visibility: hidden;
display: none;
}

.id {
visibility: hidden;
	font-weight: 400;
    font-size: 12pt;
    padding: 0px;
    margin: 0px;
    word-wrap: normal;
    display: inline;
}
 
#toc, #ix {
visibility: hidden;
}
 
#toc, #ix, li { list-style-type: none; margin: 0; padding: 0 }

#toc a {
color:black;
}

#toc a:after { content: leader('.') target-counter(attr(href), page) }

#ix span:after { content: leader('.') }
#ix a { content: target-counter(attr(href), page); padding-left: 0.3em }

.nowrap {
	white-space: nowrap; 
}

.toc {
	
	width:43em;
}

.listTable {
	
	width:45em;
}

@media screen {
  html { background: gray; }
  table {
  	font-size: 12pt;

  }

.kopf { 
	margin-top: -14pt;
}
  
  body {
  	margin-top: 2em;
  	font-size: 12pt;
  }

  .website-content { 
    width: 840px;
    background: white;
    margin: 1em auto;
    padding: 3em 3em 3em 3em;
  }
  .login {
  	color: #383838;
  	visibility: visible;
  display: block;
  	width: 840px;
  	
    margin: auto;
    padding: 0em 1em 5em 3em;
    text-align: right;
    font-size: 9pt;
    font-weight: 400;
    
  }
  .id {
  visibility: visible;
  }

  h1 {
  margin-top: 1em;
/*   display:none; */
  }
  
  h2 {
  font-size: 12pt;
  }
  h3 {
  font-size: 12pt;
  margin-bottom: 0em;
/*  clear: both;*/
  }
  
  #header-right {
  	display: none;
  	}
  	
  	#footer-left {
  	display: none;
  	}
  	
  	#footer-bottom {
	display:block;
	}
	.amount {
		margin-top: 0em;
	}
	
	#toc, #ix {
		visibility: visible;
		margin-bottom: 2em;

	}
	
	.table {
  		width: 45em;
	}
	
	.howAreYou {
	padding-right: 2.5em;
	font-weight: 600;
	}
	.subTitle {
	margin-top: -1.5em;
	}
	
	.hiddenOnPage {
		display:inherit;
	}
 
 	.kopf { 
 	margin-right: -14pt;
 	}

	.green {
		width:140px;
	}
}

.table {
border-color: #929292;
border-style: none;
padding-top: 0em;
border-width: 0pt;
}

.table-header {
page-break-after: avoid;
	font-size: 12pt;
	font-weight: 600;
text-align: left;
}

.table p {
padding: 0.1em 0em 1em 1.5em;
}

.table ul{
padding: 1em 0.5em 0.5em 3em;
}

.left-border{
	border-left-color: #929292;
	border-left-style: solid;
	border-left-width: 1px;
	vertical-align: center;
}

.bottom-border{
	border-bottom-color: #929292;
	border-bottom-style: solid;
	border-bottom-width: 1px;
	vertical-align: top;
	width:660px;

}

</style>

</head>

<body>
<% 
// Avoid displaying anything if someting is wrong.
if (!variables.everythingFine){
	%>
		Wrong Inputs. See Log for Details.
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

<h1>CO2 Food-Sourcing Report</h1>


<div  id="footer-left">
	<img class="logo-karotte" src="karotte.png" alt="karotte"  />
	Eaternity &#10031; Genuss. Bewusst. Erleben.
	<a href="mailto:info@eaternity.ch" >info@eaternity.ch</a>
	<a href="www.eaternity.ch">www.eaternity.ch</a>
</div>






<div class="content">


<table cellspacing="0" cellpadding="0" class="table toc" >


<tr>
<td></td>
<td class="gray left-border"></td>
<td class="gray co2label"><span class="nowrap">g CO<sub>2</sub>*</span></td>
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

	String clear = Converter.toString(recipe.getId(),34);

	recipe.setCO2Value();
	
	Double recipeValue = recipe.getCO2Value()  ;
	
	String length = variables.formatter.format(recipeValue/variables.MaxValueRezept*200);

	String formatted = variables.formatter.format(recipeValue);
	String persons = Long.toString(recipe.getPersons());
	
	String moreOrLess = "";
	String percent ="";

		
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
		<td class="co2value" ><%= formatted %></td>
		</tr>


		<%

}	

%>


</table>


<!-- Summary -->
<br/><br/><br/>


<br/><br/><br/><br/>

<!-- Situation -->

<!-- Results -->

 <!-- Calendar -->

<table cellspacing="0" cellpadding="0" class="table toc" >

<tr>
<td></td>
<td class="gray left-border"></td>
<td class="gray co2label"><span class="nowrap">g CO<sub>2</sub>*</span></td>
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



//  go over the Recipes in the Workspace
for(CatRyzer.DateValue categoryValue : variables.valuesByDate){

	if(categoryValue.co2value>variables.MaxValueRezept){
		variables.MaxValueRezept = categoryValue.co2value;
	} 
	if(categoryValue.co2value<variables.MinValueRezept){
		variables.MinValueRezept = categoryValue.co2value;
	}
}




for(CatRyzer.DateValue categoryValue : variables.valuesByDate){
		String length = variables.formatter.format(categoryValue.co2value/variables.MaxValueRezept*200);
	
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
<td class="co2value" ><%= variables.formatter.format(categoryValue.co2value) %></td>
</tr>


<%
}

/*
Output:

Date1: AllCategory,co2value
Date2: AllCategory,co2value
Date3: AllCategory,co2value
Date4: AllCategory,co2value
Date5: AllCategory,co2value
*/



%>

</table>





 <!-- Total Impact -->



<table cellspacing="0" cellpadding="0" class="table toc" >

<tr>
<td></td>
<td class="gray left-border"></td>
<td class="gray co2label"><span class="nowrap">g CO<sub>2</sub>*</span></td>
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

//  go over the Recipes in the Workspace
for(CatRyzer.CategoryValue categoryValue : variables.valuesByCategory){

	if(categoryValue.co2value>variables.MaxValueRezept){
		variables.MaxValueRezept = categoryValue.co2value;
	} 
	if(categoryValue.co2value<variables.MinValueRezept){
		variables.MinValueRezept = categoryValue.co2value;
	}
}


for(CatRyzer.CategoryValue categoryValue : variables.valuesByCategory){
	String length = variables.formatter.format(categoryValue.co2value/variables.MaxValueRezept*200);
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
<td class="co2value" ><%= variables.formatter.format(categoryValue.co2value) %></td>
</tr>


<%

	}

/*
Output:

Alldates: 	Category1, co2value
 			Category2, co2value
			Category3, co2value
			Category4, co2value
 			Category5, co2value
*/


%>

</table>



 <!-- Potential -->

<br /><br />

<br /><br />

 <!-- By Date -->






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
<td class="gray co2label"><span class="nowrap">g CO<sub>2</sub>*</span></td>
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
	%><%= datumString %>  -  CO2 Impact by Category.</td>
<td class="left-border"></td>
<td class="co2value" ></td>
<td ></td>
</tr>

<%

//  go over the Recipes in the Workspace

for(CatRyzer.CategoryValue categoryValue : categoriesByDates.category){


	if(categoryValue.co2value>variables.MaxValueRezept){
		variables.MaxValueRezept = categoryValue.co2value;
	} 
	if(categoryValue.co2value<variables.MinValueRezept){
		variables.MinValueRezept = categoryValue.co2value;
	}
}


	for(CatRyzer.CategoryValue categoryValue : categoriesByDates.category){
		String length = variables.formatter.format(categoryValue.co2value/variables.MaxValueRezept*200);
%>

<tr <%
int order = (categoriesByDates.category.indexOf(categoryValue) - counterIterate ) % 2; 
if(order == 1) { %>
class="alternate"
<% }%> > 
<td class="menu-name">
<%= categoryValue.categoryName %>
</td>
<td class="left-border"><img class="bar" src="green.png" alt="gray" height="11" width="<%= length %>" /></td>
<td class="co2value" ><%= variables.formatter.format(categoryValue.co2value) %></td>
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



				String formatted = variables.formatter.format( recipeValue );
				String persons = Long.toString(recipe.getPersons());
				
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
				<td><div class="amount"><%= formatted %> g CO<sub>2</sub>* total</div></td>
				<td class="left-border"><img class="bar" height="11"  src="gray.png" alt="gray" width="140" /></td>
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
		

	%>


	<%		

	}

	
}

/*
Output:

Date1: Category1, co2value
Date1: Category2, co2value
...
  <!-- per sheet -->
  Date1: Ingredientspec1, Ingredientspec2

Date2: Category1, co2value
Date3: Category2, co2value

  <!-- per sheet -->
  Date2: Ingredientspec1, Ingredientspec2


<!-- Categories -->
Category1: Ingredient1, Ingredient2

Category2: Ingredient1, Ingredient2


*/


%>

</table>




<br /><br /><br />
Overview Categories
<br /><br /><br />


<%



// get ingredients per category example
for(String category : variables.catryzer.catMultiMap.keySet())
{
	
	Collection<IngredientSpecification> ingredientsSpecification = variables.catryzer.catMultiMap.get(category);
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
<h3>Category: <%= category %></h3>
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



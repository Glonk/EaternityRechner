<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.google.appengine.api.users.User" %>
<%@ page import="com.google.appengine.api.users.UserService" %>
<%@ page import="com.google.appengine.api.users.UserServiceFactory" %>

<%@ page import="ch.eaternity.server.DAO" %>
<%@ page import="ch.eaternity.shared.Recipe" %>
<%@ page import="ch.eaternity.shared.Ingredient" %>
<%@ page import="ch.eaternity.shared.IngredientSpecification" %>
<%@ page import="ch.eaternity.shared.Converter" %>
<%@ page import="ch.eaternity.shared.RecipeComment" %>
<%@ page import="ch.eaternity.shared.comparators.RezeptDateComparator" %>
<%@ page import="ch.eaternity.shared.CatRyzer" %>


<%@ page import="java.util.List" %>
<%@ page import="java.util.Calendar" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.Set" %>
<%@ page import="java.util.Collection" %>
<%@ page import="java.util.Arrays" %>
<%@ page import="java.text.DecimalFormat" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.Collections" %>
<%@ page import="java.util.logging.Logger" %>
<%@ page import="java.util.logging.Level" %>


<%@ page import="java.net.MalformedURLException" %>
<%@ page import="java.net.URL" %>
<%@ page import="java.io.BufferedReader" %>
<%@ page import="java.io.InputStreamReader" %>
<%@ page import="java.io.IOException" %>
<%@ page import="java.util.Date" %>



<html>
<head>

<meta HTTP-EQUIV="Content-Type" CONTENT="text/html; charset=UTF-8">
<title>Klima-Bilanz Report</title>


<%

Logger rootLogger = Logger.getLogger("");

boolean everythingFine = true;

// Hole Rezepte die zum Benutzer gehören
String BASEURL = request.getRequestURL().toString();

UserService userService = UserServiceFactory.getUserService();
User user = userService.getCurrentUser();

String tempIds = request.getParameter("ids");
String permanentId = request.getParameter("pid");
String kitchenId = request.getParameter("kid");
String pdf = request.getParameter("pdf");


Boolean doPdf = false;
if(pdf != null){
	doPdf = true;
}

Boolean DoItWithPermanentIds = true;

%>

<%

DAO dao = new DAO();


List<Recipe> kitchenRecipes = new ArrayList<Recipe>();


DecimalFormat formatter = new DecimalFormat("##");
SimpleDateFormat dateFormatter = new SimpleDateFormat("dd.MMMM yyyy");
Long kitchenLongId = 0L;

if(tempIds != null){
	kitchenRecipes = dao.getRecipeByIds(tempIds,true);
} 
else {
	rootLogger.log(Level.SEVERE, "Loading Data: Test if Logger works.");
	if(permanentId != null){
	 	kitchenRecipes = dao.getRecipeByIds(permanentId,false);
		
		DoItWithPermanentIds = false;
	} 
	else {
		if(kitchenId != null){
			kitchenLongId = Long.parseLong(kitchenId);
			kitchenRecipes = dao.getKitchenRecipes(kitchenLongId);
			DoItWithPermanentIds = false;
		}
		else {
			 rootLogger.log(Level.SEVERE, "No Kitchen Id or permanend Id passed. Pass it with ?kid=234234 ");
		 	 everythingFine = false;
		}
	}
}


if (kitchenRecipes.size() == 0)
{
	rootLogger.log(Level.SEVERE, "Kitchen with id=" + kitchenLongId + "doesn't contains any recipes or kitchen doesn't exist.");
	everythingFine = false;
}

for (Recipe recipe : kitchenRecipes){
	if (recipe.cookingDate == null) {
		kitchenRecipes.remove(recipe);
		rootLogger.log(Level.SEVERE, "Following recipe has no cooking date setted and thus been removed: " + recipe.getSymbol());
	}
}

// some precalculation of values



Double MaxValueRezept = 0.0;
Double MinValueRezept = 10000000.0;
Double average = 0.0;
Double median = 0.0;
Integer counter = 0;

//Calendar rightNow = Calendar.getInstance();
//Integer iTimeStamp = rightNow.get(Calendar.WEEK_OF_YEAR);

Date date = new Date();
      long iTimeStamp = (long) (date.getTime() * .00003);

%>

<%

// calculate average, median, min, max
if(kitchenRecipes.size() != 0){
	ArrayList<Double> values = new ArrayList<Double>();

	//  go over the Recipes in the Workspace
	for(Recipe recipe: kitchenRecipes){
		values.add((double) recipe.getCO2Value());
		average = average + recipe.getCO2Value();
		counter++;
		recipe.setCO2Value();
		if(recipe.getCO2Value()>MaxValueRezept){
	MaxValueRezept = recipe.getCO2Value();
		} 
		if(recipe.getCO2Value()<MinValueRezept){
	MinValueRezept = recipe.getCO2Value();
		}
	}
	average = (average /counter)  ;
	MinValueRezept = MinValueRezept  ;
	MaxValueRezept = MaxValueRezept  ;
		
	Collections.sort(values);
  
    if (values.size() % 2 == 1)
		median = values.get((values.size()+1)/2-1)  ;
    else {
		double lower = (values.get(values.size()/2-1))   ;
		double upper = (values.get(values.size()/2))   ;
	 
		median = ((lower + upper) / 2.0);
    }	    
}
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

<script type="text/javascript">

function initThis(){
idsToAdd = new Array(); 
baseUrl = getBaseURL();


<%for(Recipe recipe: kitchenRecipes){
	long compute = recipe.getId() * iTimeStamp;
	String code = Converter.toString(compute,34);%>
idsToAdd['<%=code%>'] = true<%}%>

arrayAdd(idsToAdd)
}

function arrayAdd(idsToAdd){
hrefAdd = "";
	for (var codeIndex in idsToAdd){
/* 		alert(idsToAdd[codeIndex]) */

		if(idsToAdd[codeIndex]){
		 hrefAdd = hrefAdd + codeIndex + ",";
		}

	}
	hrefAdd = hrefAdd.slice(0,hrefAdd.length-1); 
	setHref(hrefAdd)
}



function addRemoveMenu(code){
	if(document.htmlAdder.elements[code].checked == true){

		idsToAdd[code] = true;
		
	} else {
		idsToAdd[code] = false;
	}
	

	arrayAdd(idsToAdd);

}

function setHref(hrefAdd){
    document.getElementById('getPdf').href=baseUrl+"view.jsp?ids="+hrefAdd
}

function getBaseURL() {
    var url = location.href;  // entire url including querystring - also: window.location.href;
    var baseURL = url.substring(0, url.indexOf('/', 14));


    if (baseURL.indexOf('http://localhost') != -1) {
        // Base Url for localhost
        var url = location.href;  // window.location.href;
        var pathname = location.pathname;  // window.location.pathname;
        var index1 = url.indexOf(pathname);
        var index2 = url.indexOf("/", index1 + 1);
        var baseLocalUrl = url.substr(0, index2);

        return baseLocalUrl + "/";
    }
    else {
        // Root Url for domain name
        return baseURL + "/";
    }

}
</script>

</head>

<body onLoad="initThis()">
<% 
// Avoid displaying anything if someting is wrong.
if (!everythingFine){
	%>
		Wrong Inputs. See Log for Details.
	<%
}
else { %>
	
	
	
	
	<%

	
// Define categories here:
// CatFormula(String category, String formula, boolean isHeading)
CatRyzer catryzer = new CatRyzer(kitchenRecipes);

List<CatRyzer.CatFormula>  categoryFormulas = new ArrayList<CatRyzer.CatFormula>();

categoryFormulas.add(catryzer.new CatFormula("<strong>Vegetable Products</strong>","vegetable",true));

categoryFormulas.add(catryzer.new CatFormula("Rice products","rice"));
categoryFormulas.add(catryzer.new CatFormula("Spices & herbs","spices&herbs"));
categoryFormulas.add(catryzer.new CatFormula("Sweets","sweets"));
categoryFormulas.add(catryzer.new CatFormula("Vegetable oils and fat","oil and fat, -animal-based"));
categoryFormulas.add(catryzer.new CatFormula("Vegetables and fruits","legumes, fruits"));
categoryFormulas.add(catryzer.new CatFormula("Preprocessed vegetable products","vegetable"));
categoryFormulas.add(catryzer.new CatFormula("Bread and Grain Products","grain, bread, pasta"));
categoryFormulas.add(catryzer.new CatFormula("Nuts und seeds","nuts, seeds"));

categoryFormulas.add(catryzer.new CatFormula("<strong>Animal Products</strong>","animal-based",true));

categoryFormulas.add(catryzer.new CatFormula("<strong>Meat Products</strong>","meat",true));

categoryFormulas.add(catryzer.new CatFormula("Ruminants","ruminant"));
categoryFormulas.add(catryzer.new CatFormula("Non-ruminants","non-ruminant"));
categoryFormulas.add(catryzer.new CatFormula("Fish and seafood","fish, seafood"));

categoryFormulas.add(catryzer.new CatFormula("<strong>Diary Products</strong>","diary",true));

categoryFormulas.add(catryzer.new CatFormula("Ripened cheese","rippened"));
categoryFormulas.add(catryzer.new CatFormula("Fresh cheese and diary products","cheese,-rippened"));

categoryFormulas.add(catryzer.new CatFormula("Animal based fats","oil and fats, -vegetable"));
categoryFormulas.add(catryzer.new CatFormula("Eggs and egg based products","eggs"));
categoryFormulas.add(catryzer.new CatFormula("Canned and finished products","processed"));
categoryFormulas.add(catryzer.new CatFormula("Sauces","sauces"));


categoryFormulas.add(catryzer.new CatFormula("<strong>Drinks</strong>","beverage",true));

categoryFormulas.add(catryzer.new CatFormula("Drinks (alkohol based)","beverage, alcohol"));
categoryFormulas.add(catryzer.new CatFormula("Drinks (fruit based)","beverage, fruit"));
categoryFormulas.add(catryzer.new CatFormula("Drinks (milk based)","beverage, diary"));



catryzer.setCatFormulas(categoryFormulas);
catryzer.categoryze();

List<CatRyzer.DateValue> valuesByDate = catryzer.getDateValues();

List<CatRyzer.CategoryValue> valuesByCategory = catryzer.getCatVals();

List<CatRyzer.CategoryValuesByDates> valuesByDate_Category = catryzer.getCatValsByDates();
%>



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
<%
	if(DoItWithPermanentIds) {
%>




<a href="http://prod.eaternityrechner.appspot.com/view.jsp?ids=93UJI,93UNM" title="menu_view" class="whatever hiddenOnPage" id="getPdf">Dieses Dokument für die markierten Menus als PDF herunterladen.</a>

<%
	} // just do it simple
%>


<div  id="footer-left">
	<img class="logo-karotte" src="karotte.png" alt="karotte"  />
	Eaternity &#10031; Genuss. Bewusst. Erleben.
	<a href="mailto:info@eaternity.ch" >info@eaternity.ch</a>
	<a href="www.eaternity.ch">www.eaternity.ch</a>
</div>






<div class="content">

<%
if(true){
%>



<table cellspacing="0" cellpadding="0" class="table toc" >


<tr>
<td></td>
<td class="gray left-border"></td>
<td class="gray co2label"><span class="nowrap">g CO<sub>2</sub>*</span></td>
<td></td>
</tr>

<tr>
<td class="table-header bottom-border">Lieferschein Übersicht</td>
<td class="left-border"></td>
<td class="co2value" ></td>
<td ></td>
</tr>


	
<!--  <%= Integer.toString(kitchenRecipes.size()) %>  -->

<%

Collections.sort(kitchenRecipes,new RezeptDateComparator());

Boolean notDoneFirst = true;
Boolean notDoneSeccond = true;
Boolean notDoneThird = true;
int counterIterate = 0;



String smilies = "";



for(Recipe recipe: kitchenRecipes){

	long compute = recipe.getId() * iTimeStamp;
	String code = Converter.toString(compute,34);
	String clear = Converter.toString(recipe.getId(),34);

	recipe.setCO2Value();
	
	Double recipeValue = recipe.getCO2Value()  ;
	
	String length = formatter.format(recipe.getCO2Value()/MaxValueRezept*200);

	String formatted = formatter.format( recipeValue);
	String persons = Long.toString(recipe.getPersons());
	
	String moreOrLess = "";
	String percent ="";

		
		%>
		

		<tr <%
		int order = (kitchenRecipes.indexOf(recipe) - counterIterate ) % 2; 
		if(order == 1) { %>
		class="alternate"
		<% }%> > 
		<td class="menu-name">
		<% if(DoItWithPermanentIds) { %><span class="hiddenOnPage" style="display:inline"><%= clear %></span><% } %><input type="checkbox" name="<%= code %>" checked="checked" class="hiddenOnPage" onclick="javascript:addRemoveMenu('<%= code %>')">
		<%= smilies %><%= recipe.getSymbol() %>
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
The main result was, we due the assumtpion. It came close to. The following potential was discovered <br/><br/><br/><br/>

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


// calculate average, median, min, max


ArrayList<Double> values = new ArrayList<Double>();

//  go over the Recipes in the Workspace
for(CatRyzer.DateValue categoryValue : valuesByDate){
	values.add((double) categoryValue.co2value);
	average = average + categoryValue.co2value;
	counter++;

	if(categoryValue.co2value>MaxValueRezept){
MaxValueRezept = categoryValue.co2value;
	} 
	if(categoryValue.co2value<MinValueRezept){
MinValueRezept = categoryValue.co2value;
	}
}
average = (average /counter)  ;
MinValueRezept = MinValueRezept  ;
MaxValueRezept = MaxValueRezept  ;
	
Collections.sort(values);
 
if (values.size() % 2 == 1)
median = values.get((values.size()+1)/2-1)  ;
else
{
double lower = (values.get(values.size()/2-1))   ;
double upper = (values.get(values.size()/2))   ;

median = ((lower + upper) / 2.0);
}	    




for(CatRyzer.DateValue categoryValue : valuesByDate){
		String length = formatter.format(categoryValue.co2value/MaxValueRezept*200);
	
%>

<tr <%
int order = (valuesByDate.indexOf(categoryValue) - counterIterate ) % 2; 
if(order == 1) { %>
class="alternate"
<% }%> > 
<td class="menu-name">
	<%
	String datumString = "NO DATE SPECIFIED";
	try {
		datumString = dateFormatter.format(categoryValue.date);
	} catch (Exception e) {
		            out.println("The Error is: " + e);
	}
	%><%= datumString %>
</td>
<td class="left-border"><img class="bar" src="green.png" alt="gray" height="11" width="<%= length %>" /></td>
<td class="co2value" ><%= formatter.format(categoryValue.co2value) %></td>
</tr>


<%
}
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
int counterIterate = 0;

ArrayList<Double> values = new ArrayList<Double>();

//  go over the Recipes in the Workspace
for(CatRyzer.CategoryValue categoryValue : valuesByCategory){
	values.add((double) categoryValue.co2value);
	average = average + categoryValue.co2value;
	counter++;

	if(categoryValue.co2value>MaxValueRezept){
MaxValueRezept = categoryValue.co2value;
	} 
	if(categoryValue.co2value<MinValueRezept){
MinValueRezept = categoryValue.co2value;
	}
}
average = (average /counter)  ;
MinValueRezept = MinValueRezept  ;
MaxValueRezept = MaxValueRezept  ;
	
Collections.sort(values);
 
if (values.size() % 2 == 1)
median = values.get((values.size()+1)/2-1)  ;
else
{
double lower = (values.get(values.size()/2-1))   ;
double upper = (values.get(values.size()/2))   ;

median = ((lower + upper) / 2.0);
}	    


for(CatRyzer.CategoryValue categoryValue : valuesByCategory){
	String length = formatter.format(categoryValue.co2value/MaxValueRezept*200);
%>

<tr <%
int order = (valuesByCategory.indexOf(categoryValue) - counterIterate ) % 2; 
if(order == 1) { %>
class="alternate"
<% }%> > 
<td class="menu-name">
<%= categoryValue.categoryName %>
</td>
<td class="left-border"><img class="bar" src="green.png" alt="gray" height="11" width="<%= length %>" /></td>
<td class="co2value" ><%= formatter.format(categoryValue.co2value) %></td>
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
 By choosing less off this, you get more of this.
<br /><br />

 <!-- By Date -->






<%


counterIterate = 0;
for(CatRyzer.CategoryValuesByDates categoriesByDates : valuesByDate_Category){

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
		datumString = dateFormatter.format(thisDate);
	} catch (Exception e) {
		  out.println("The Error is: " + e);
	}
	%><%= datumString %>  -  CO2 Impact by Category.</td>
<td class="left-border"></td>
<td class="co2value" ></td>
<td ></td>
</tr>

<%



values = new ArrayList<Double>();

//  go over the Recipes in the Workspace

for(CatRyzer.CategoryValue categoryValue : categoriesByDates.category){
	values.add((double) categoryValue.co2value);
	average = average + categoryValue.co2value;
	counter++;

	if(categoryValue.co2value>MaxValueRezept){
MaxValueRezept = categoryValue.co2value;
	} 
	if(categoryValue.co2value<MinValueRezept){
MinValueRezept = categoryValue.co2value;
	}
}

average = (average /counter)  ;
MinValueRezept = MinValueRezept  ;
MaxValueRezept = MaxValueRezept  ;
	
Collections.sort(values);
 
if (values.size() % 2 == 1)
median = values.get((values.size()+1)/2-1)  ;
else
{
double lower = (values.get(values.size()/2-1))   ;
double upper = (values.get(values.size()/2))   ;

median = ((lower + upper) / 2.0);
}	    




	for(CatRyzer.CategoryValue categoryValue : categoriesByDates.category){
		String length = formatter.format(categoryValue.co2value/MaxValueRezept*200);
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
<td class="co2value" ><%= formatter.format(categoryValue.co2value) %></td>
</tr>


<%
	}	
%>
	
	
	
	</table>

	<!-- Here comes the old Stuff -->

	<!-- Details follow -->


	<table cellspacing="0" cellpadding="0" class="table new-page listTable" >


	<tr>
	<td class="table-header">Lieferschein Übersicht für den: 
		<%
		datumString = "NO DATE SPECIFIED";
		try {
			datumString = dateFormatter.format(thisDate);
		} catch (Exception e) {
			  out.println("The Error is: " + e);
		}
		%><%= datumString %>
		</td>
	<td></td>
	</tr>

	<tr>
	<td class="bottom-border"></td>
	<td class="green left-border">Potential in g CO<sub>2</sub>*</td>
	</tr>

	<tr>
	<td></td>
	<td class="left-border"><br></td>
	</tr>

	</table>

	<!--  <%= Integer.toString(kitchenRecipes.size()) %>  -->
	<%

	// valuesByDate_Calender

	for(Recipe recipe: kitchenRecipes){

		if(thisDate.compareTo(recipe.cookingDate) == 0 ){

			long compute = recipe.getId() * iTimeStamp;
			String code = Converter.toString(compute,34);

				recipe.setCO2Value();
				Double recipeValue = recipe.getCO2Value()  ;



				String formatted = formatter.format( recipeValue );
				String persons = Long.toString(recipe.getPersons());
				
				datumString = "NO DATE SPECIFIED";
				try {
					datumString = dateFormatter.format(recipe.cookingDate);
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
				<td><div class="amount"><%= formatted %> g CO<sub>2</sub>* total</div></td>
				<td class="left-border"><img class="bar" height="11"  src="gray.png" alt="gray" width="140" /></td>
				</tr>

				<tr>
				<td>

				<span class="subTitle">Tag: <%= datumString %></span>

				<!-- <span style="color:gray;"><%= recipe.getSubTitle() %>/span><br /> -->


					<%	
					counter = 0;
					for(IngredientSpecification ingredient: recipe.Zutaten){
					counter = counter + 1;

					%><% if(counter != 1){ %>, <% } %><span class="nowrap"><%= ingredient.getMengeGramm() %> g <%= ingredient.getName() %> (<%= ingredient.getHerkunft().symbol %>  | <%= ingredient.getZustand().symbol %> | <%= ingredient.getProduktion().symbol %> | <%= ingredient.getTransportmittel().symbol %>)</span><%
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
Übersicht über die Kategorien
<br /><br /><br />


<%



// get ingredients per category example
for(String category : catryzer.catMultiMap.keySet())
{
	
	Collection<IngredientSpecification> ingredientsSpecification = catryzer.catMultiMap.get(category);
	Set<String> ingredientsNames = catryzer.getIngredientsNames(ingredientsSpecification);

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
<h3>Kategorie: <%= category %></h3>
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
<% if(DoItWithPermanentIds) { %>
<div class="login">
	<%

	    if (user != null) {
	%>
	Diese Angaben sind für den Benutzer <%= user.getNickname() %>. <a href="<%= userService.createLogoutURL(request.getRequestURI()) %>">Abmelden</a>?
	<%
	    } else {
	    	if (tempIds == null){
	%>
	Sie sind nicht angemeldet.
	<a href="<%= userService.createLoginURL(request.getRequestURI()) %>">Anmelden</a>?
	<%	
			} else {
			
			%>
			Zurück zur <a href="/view.jsp">Übersicht</a>?
			<%
			
			}
	    }
	%>

</div>
<% } %>


<%
}// if everthing is fine
%>

</body>


</HTML>



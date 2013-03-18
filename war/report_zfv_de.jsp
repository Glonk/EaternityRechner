<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@ page contentType="text/html;charset=UTF-8" language="java" %>


<%@ page import="ch.eaternity.server.StaticPageService" %>

<%@ page import="ch.eaternity.shared.Recipe" %>
<%@ page import="ch.eaternity.shared.RecipeComment" %>
<%@ page import="ch.eaternity.shared.Ingredient" %>

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

categoryFormulas.add(catryzer.new CatFormula("&nbsp;&nbsp;<strong>Milchprodukte</strong>","diary,oil and fats,-vegetable",true));
categoryFormulas.add(catryzer.new CatFormula("&nbsp;&nbsp;&nbsp;&nbsp;gereifter Käse","ripened"));
categoryFormulas.add(catryzer.new CatFormula("&nbsp;&nbsp;&nbsp;&nbsp;Frischkäse und übrige Milchprodukte","diary,-ripened,milk"));
categoryFormulas.add(catryzer.new CatFormula("&nbsp;&nbsp;&nbsp;&nbsp;Tierische Fette","oil and fats,-vegetable"));
categoryFormulas.add(catryzer.new CatFormula("&nbsp;&nbsp;Eier und Eibasierte Produkte","eggs"));
categoryFormulas.add(catryzer.new CatFormula("&nbsp;&nbsp;Dosen und verarbeitete Produkte","finished product"));
categoryFormulas.add(catryzer.new CatFormula("&nbsp;&nbsp;Saucen","sauces"));

categoryFormulas.add(catryzer.new CatFormula("<strong>Getränke</strong>","beverage",true));
categoryFormulas.add(catryzer.new CatFormula("&nbsp;&nbsp;Getränke (auf Alkoholbasis)","alcohol"));
categoryFormulas.add(catryzer.new CatFormula("&nbsp;&nbsp;Getränke (auf Fruchtbasis)","fruitjuice"));
categoryFormulas.add(catryzer.new CatFormula("&nbsp;&nbsp;Getränke (weitere)","beverage,-alcohol,-fruitjuice,-milk"));

StaticPageService variables = new StaticPageService(BASEURL,tempIds,permanentId,kitchenId,pdf,categoryFormulas,Locale.GERMAN);

int counter = 0;
int counterIterate = 0;
Collection<Double> values = new ArrayList<Double>();
DecimalFormat co2_formatter = new DecimalFormat("##.#");
DecimalFormat cost_formatter = new DecimalFormat("##");
DecimalFormat weight_formatter = new DecimalFormat("##.#");
DecimalFormat distance_formatter = new DecimalFormat("##");

int co2BarLength = 180;
int barOffset = 45;
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
		<%=variables.errorMessage%>
	<%
		}
	else {
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




<div  id="footer-left">
	<img class="logo-karotte" src="karotte.png" alt="karotte"  />
	Eaternity &#10031; Genuss. Bewusst. Erleben.
	<a href="mailto:info@eaternity.ch" >info@eaternity.ch</a>
	<a href="www.eaternity.ch">www.eaternity.ch</a>
</div>






<div class="content">

<h1>CO2-Analyse einiger Menus</h1>

ZFV: Offerte BAG

<%
	// -------------------------------- Total CO2 Impact by Category ---------------------------
%>

<table cellspacing="0" cellpadding="0" class="table toc" >

<tr>
<td></td>
<td class="gray left-border"></td>
<td class="gray co2label"><span class="nowrap">kg CO<sub>2</sub>*</span></td>
<td></td>
</tr>

<tr>
<td class="table-header bottom-border">Gesamte CO2-Emissionen nach Kategorie</td>
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
	values.add(categoryValue.co2value.totalValue);
}
variables.setMinMax(values);


for(CatRyzer.CategoryValue categoryValue : variables.valuesByCategory){
%>

	<tr <%int order = (variables.valuesByCategory.indexOf(categoryValue) - counterIterate ) % 2; 
	if(order == 1) {%>
	class="alternate"
	<%}%> > 
	<td class="menu-name">
	<%=categoryValue.categoryName%>
	</td>
	<td class="left-border" width="<%=co2BarLength + barOffset%>px"><%=variables.getCo2ValueBar(values, categoryValue.co2value, co2BarLength)%></td>
	<td class="co2value" ><%=co2_formatter.format(categoryValue.co2value.totalValue/1000)%></td>
	</tr>

<%
	}
%>

</table>


<br /><br /><br /><br />


<%
	// -------------------------------- Top 20 intensive Ingredients ---------------------------
%>

<table cellspacing="0" cellpadding="0" class="table toc" >

<tr>
<td></td>
<td class="gray left-border"></td>
<td class="gray co2label"><span class="nowrap">kg CO<sub>2</sub>*</span></td>
<td></td>
</tr>

<tr>
<td class="table-header bottom-border">Top 15 CO<sub>2</sub>-intensive Zutaten</td>
<td class="left-border"></td>
<td class="co2value" ></td>
<td ></td>
</tr>



<%
	counterIterate = 0;
variables.maxValTemp = 0.0;
variables.minValTemp = 10000000.0; 

values.clear();

for(CatRyzer.CategoryValue ingredientValue : variables.valuesByIngredient){
	values.add(ingredientValue.co2value.totalValue);
}
variables.setMinMax(values);


for(CatRyzer.CategoryValue ingredientValue : variables.valuesByIngredient){
	
	if (variables.valuesByIngredient.indexOf(ingredientValue) == 15){
		break;
	}
%>

<tr <%int order = (variables.valuesByCategory.indexOf(ingredientValue) - counterIterate ) % 2; 
if(order == 1) {%>
class="alternate"
<%}%> > 
<td class="menu-name">
<%=ingredientValue.categoryName%> <!-- (<%=ingredientValue.weight/1000%> kg) -->
</td>
<td class="left-border" width="<%=co2BarLength + barOffset%>px"><%=variables.getCo2ValueBar(values, ingredientValue.co2value, co2BarLength)%></td>
<td class="co2value" ><%=co2_formatter.format(ingredientValue.co2value.totalValue/1000)%></td>

</tr>

<%
	}
%>
</table>






<%
	// -------------------------------- Overview Categories ---------------------------
%>

<br /><br /><br />
<h2>Kategorienübersicht</h2>
<br /><br /><br />

<%
	// get ingredients per category
for(CatRyzer.CatMapping mapping : variables.catryzer.mappings)
{
	
	Collection<Ingredient> ingredientsSpecification = variables.catryzer.catMultiMap.get(mapping.category);
	Set<String> ingredientsNames = variables.catryzer.getIngredientsNames_de(ingredientsSpecification);
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
		<h3>Kategorie: <%= mapping.category %></h3>
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



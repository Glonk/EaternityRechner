<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%@ page import="ch.eaternity.shared.Recipe" %>
<%@ page import="ch.eaternity.shared.IngredientSpecification" %>
<%@ page import="ch.eaternity.shared.Converter" %>
<%@ page import="ch.eaternity.shared.CatRyzer" %>
<%@ page import="ch.eaternity.shared.Util" %>
<%@ page import="ch.eaternity.shared.Pair" %>
<%@ page import="ch.eaternity.shared.Quantity.Weight" %>
<%@ page import="ch.eaternity.shared.CategoryQuantities" %>

<%@ page import="ch.eaternity.server.jsp.StaticDataLoader" %>
<%@ page import="ch.eaternity.server.jsp.StaticProperties" %>
<%@ page import="ch.eaternity.server.jsp.StaticTemp" %>

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

<%@ page import="com.google.appengine.api.users.User" %>
<%@ page import="com.google.appengine.api.users.UserService" %>
<%@ page import="com.google.appengine.api.users.UserServiceFactory" %>


<html>
<head>

<meta HTTP-EQUIV="Content-Type" CONTENT="text/html; charset=UTF-8">
<title>CO2 Bilanzen Lieferscheine</title>
<link rel="stylesheet" type="text/css" href="reports.css">


<%
	StaticProperties props = (StaticProperties)request.getAttribute("props");
	StaticDataLoader data = (StaticDataLoader)request.getAttribute("data");
	StaticTemp temp = (StaticTemp)request.getAttribute("temp");
	CatRyzer catryzer = (CatRyzer)request.getAttribute("catryzer");
	
	Integer counter = 0;
	int counterIterate = 0;
	Collection<Double> values = new ArrayList<Double>();
	
	UserService userService = UserServiceFactory.getUserService();
	User user = userService.getCurrentUser();

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

<h1>CO2 Bilanzen Lieferscheine</h1>

<% // -------------------------------- Delivery Receipts Overview --------------------------- %>

<% 
temp.clear();
for (Recipe recipe : data.recipes) {
	temp.catQuantities.add(new CategoryQuantities(recipe.getSymbol(), recipe.getZutaten()));
}

temp.title = "Lieferscheine Übersicht";
%>

<jsp:include page="/jsp_snippets/snippet_generic_co2table.jsp" />

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

<% 
temp.clear();

temp.catQuantities = catryzer.getDateValues();

temp.title = "Gesamte CO2-Emissionen nach Datum";
%>

<jsp:include page="/jsp_snippets/snippet_generic_co2table.jsp" />


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
temp.clear();

temp.catQuantities = catryzer.getCatVals();

temp.title = "Gesamte CO2-Emissionen nach Kategorie";
temp.displayCost = true;
temp.displayWeight = true;
%>

<jsp:include page="/jsp_snippets/snippet_generic_co2table.jsp" />

<br /><br /><br /><br />

<% // -------------------------------- Total CO2 Impact by Date - Category --------------------------- %>

<%
counterIterate = 0;
for(CatRyzer.CategoryValuesByDate categoriesByDates : catryzer.getCatValsByDates()) {

	Date thisDate = categoriesByDates.date.get(0); %>

	<!--   ---------Total CO2 Impact by Category (per one Date) ---- -->
	
	<% 
	String datumString = "NO DATE SPECIFIED";
	try {
		datumString = props.dateFormatter.format(thisDate); }
	catch (Exception e) {
		  out.println("NO DATE SPECIFIED: The Error is: " + e);
	}
	
	temp.title = props.dateFormatter.format(thisDate) + " Gesamte CO2-Emissionen nach Kategorie";
	temp.catQuantities = categoriesByDates.categories;
	%>
	
	<jsp:include page="/jsp_snippets/snippet_generic_co2table.jsp" />
	

	<!-- ------------ Delivery Receipt Overview -------------- -->


	
	<table cellspacing="0" cellpadding="0" class="table new-page listTable" >
	<tr>
	<td class="table-header">Lieferscheine Übersicht für den  <%= datumString %>
		</td>
	<td></td>
	</tr>
	
	<tr>
	<td class="bottom-border"></td>
	<td class="left-border"></td>
	</tr>
	
	</table>
	

	<%
	for(Recipe recipe: data.recipes){
		if(thisDate.equals(recipe.cookingDate)){

		temp.title = "<h3>Lieferschein: " + recipe.getSymbol() + "</h3>";
		temp.subtitle = "Datum: " + datumString;
		temp.personFactor = 1;
		temp.recipe = recipe;
		temp.displaySmilies = false;
		%>
		
		<jsp:include page="/jsp_snippets/snippet_menu.jsp" />

		<%
		}		
	}
}
%>

</table>



<% // -------------------------------- Overview Categories --------------------------- %>


<jsp:include page="/jsp_snippets/snippet_category_overview.jsp" /> 



</div>

<div id="footer-bottom">
	<img class="logo-karotte" src="karotte.png" alt="karotte"  />
	Eaternity &#10031; Genuss. Bewusst. Erleben.
	<a href="mailto:info@eaternity.ch" >info@eaternity.ch</a>
	<a href="www.eaternity.ch">www.eaternity.ch</a>
	
</div>

</div>
<% if(data.DoItWithPermanentIds) { %>
<div class="login">
	<%

	    if (user != null) {
	%>
	Diese Angaben sind für den Benutzer <%= user.getNickname() %>. <a href="<%= userService.createLogoutURL(request.getRequestURI()) %>">Abmelden</a>?
	<%
	    } else {
	    	if (props.tempIds == null){
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


<%
}// 
%>

<%
}// if everthing is fine
%>


</body>


</HTML>



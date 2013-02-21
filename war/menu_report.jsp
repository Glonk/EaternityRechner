<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%@ page import="ch.eaternity.shared.Recipe" %>
<%@ page import="ch.eaternity.shared.IngredientSpecification" %>
<%@ page import="ch.eaternity.shared.Converter" %>
<%@ page import="ch.eaternity.shared.RecipeComment" %>
<%@ page import="ch.eaternity.shared.comparators.RezeptValueComparator" %>
<%@ page import="ch.eaternity.shared.comparators.IngredientValueComparator" %>
<%@ page import="ch.eaternity.shared.CatRyzer" %>
<%@ page import="ch.eaternity.shared.Pair" %>
<%@ page import="ch.eaternity.shared.Util" %>
<%@ page import="ch.eaternity.shared.Quantity.Weight" %>

<%@ page import="ch.eaternity.server.jsp.StaticDataLoader" %>
<%@ page import="ch.eaternity.server.jsp.StaticProperties" %>
<%@ page import="ch.eaternity.server.jsp.StaticTempBean" %>

<%@ page import="java.util.Locale" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Calendar" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.Arrays" %>
<%@ page import="java.text.DecimalFormat" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.Date" %>
<%@ page import="java.util.Collection" %>
<%@ page import="java.util.Collections" %>

<%@ page import="com.google.appengine.api.users.User" %>
<%@ page import="com.google.appengine.api.users.UserService" %>
<%@ page import="com.google.appengine.api.users.UserServiceFactory" %>

<%@ page import="java.net.MalformedURLException" %>
<%@ page import="java.net.URL" %>
<%@ page import="java.io.BufferedReader" %>
<%@ page import="java.io.InputStreamReader" %>
<%@ page import="java.io.IOException" %>
<%@ page import="java.lang.NumberFormatException" %>


<html>
<head>

<meta HTTP-EQUIV="Content-Type" CONTENT="text/html; charset=UTF-8">
<title>Menü Report</title>
<link rel="stylesheet" type="text/css" href="menu_report.css">

<!-- Load the StaticPageService as a Bean, handlich Parameter passing between jsp's and Snippets -->
<jsp:useBean id="vars" scope="session"
	class="ch.eaternity.server.jsp.StaticDataLoader" />

<jsp:useBean id="properties" scope="session"
	class="ch.eaternity.server.jsp.StaticProperties" />

<jsp:useBean id="temp" scope="session"
	class="ch.eaternity.server.jsp.StaticTempBean" />
	
<%
	//Specific Parameters to set for Display] 
	properties.locale = Locale.GERMAN;

	properties.weightUnit = Weight.KILOGRAM;
	properties.co2Unit = Weight.GRAM;		
			
	properties.formatter = new DecimalFormat("##");
	properties.co2_formatter = new DecimalFormat("##");
	properties.cost_formatter = new DecimalFormat("##");
	properties.weight_formatter = new DecimalFormat("##.#");
	properties.distance_formatter = new DecimalFormat("##");
	properties.dateFormatter = new SimpleDateFormat("dd.MMMM yyyy");
	
	properties.co2BarLength = 180;
	properties.barOffset = 45;
	
	// standard values for request if not set
	properties.doPdf = false;
	properties.threshold = 1550;
	properties.extra = 0;
	properties.persons = 4;
	
	properties.valueType = StaticProperties.ValueType.EXPANDED;
	properties.ingredientRepresentation = StaticProperties.IngredientRepresentation.EXPANDED;
	
	properties.initialize(request);
	
	vars.initialize(properties,false);
	
	Integer counter = 0;
	int counterIterate = 0;
	Collection<Double> values = new ArrayList<Double>();
	
	
	// this should be disfunctional by now
	Date date = new Date();
	long iTimeStamp = (long) (date.getTime() * .00003);
	 
	UserService userService = UserServiceFactory.getUserService();
	User user = userService.getCurrentUser();
		
	// Initialize Catryzer
	CatRyzer catryzer = new CatRyzer(vars.recipes,properties.locale);
	List<CatRyzer.CategoryValue> valuesByIngredient = catryzer.getIngVals();
	%>



	<script src="//ajax.googleapis.com/ajax/libs/jquery/1.6.2/jquery.min.js" type="text/javascript"></script>
	<script src="jquery.docraptor.js" type="text/javascript"></script>
	
	<script  type="text/javascript">
	$(document).ready(function () {
	$(".whatever").docraptor({
	document_type: 'pdf',
	test: true
	},
	'sYkJlCnJYRitdIvkAW'
	);
	});
	</script>


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

h3 {
	font-size: 24pt;
}

</style>

<script type="text/javascript">

function initThis(){
idsToAdd = new Array(); 
baseUrl = getBaseURL();


<% 
for(Recipe recipe: vars.recipes){
	long compute = recipe.getId() * iTimeStamp;
	String code = Converter.toString(compute,34); 
	%>
idsToAdd['<%= code %>'] = true<%	}	%>

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
//Avoid displaying anything if someting is wrong.
if (!vars.everythingFine){
	%>
		Wrong Inputs. See Log for Details.<br /><br />
		<%= vars.errorMessage %>
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

<h1>Menu Optimierung</h1>
<% if(vars.DoItWithPermanentIds) { %>

<a href="http://next.eaternityrechner.appspot.com/view.jsp?ids=93UJI,93UNM" title="menu_view" class="whatever hiddenOnPage" id="getPdf">Dieses Dokument für die markierten Menus als PDF herunterladen.</a>

<% } // just do it simple %>


<div  id="footer-left">
	<img class="logo-karotte" src="karotte.png" alt="karotte"  />
	Eaternity &#10031; Voller Genuss  &#10031; Bewusst Erleben
	<a href="mailto:info@eaternity.ch" >info@eaternity.ch</a>
	<a href="www.eaternity.ch">www.eaternity.ch</a>
</div>


<div class="content">



<!-- --------------------------- Certificate Box -------------------------------------- -->

<%

temp.recipes.addAll(vars.recipes);

%>

<jsp:include page="/jsp_snippets/snippet_certificate.jsp" />


<!-- --------------------------- Menu List -------------------------------------- -->

<form name="htmlAdder" method="POST" action=";">
	
<table cellspacing="0" cellpadding="0" class="table toc" >
	<tr>
		<td></td>
		<td class="gray left-border"></td>
		<td class="gray co2label"><span class="nowrap">g CO<sub>2</sub>* pro Person</span></td>
		<td></td>
	</tr>

<%

for (Recipe recipe : vars.recipes) {
	values.add(recipe.getCO2Value());
}
Collections.sort(vars.recipes,new RezeptValueComparator());

Boolean notDoneFirst = true;
Boolean notDoneSeccond = true;
Boolean notDoneThird = true;

Double MaxValueRezept = Util.getMax(values);

Double adjustedAverageLength = properties.threshold/MaxValueRezept*200;
Double climateFriendlyValueLength = properties.climateFriendlyValue/MaxValueRezept*200;
String averageLength = properties.formatter.format(adjustedAverageLength);
String formattedClimate = properties.formatter.format(properties.climateFriendlyValue);
String smilies = "";
String extraFormat = properties.formatter.format(properties.extra);
String lengthExtra = properties.formatter.format(properties.extra/MaxValueRezept*200);


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
	+"<td class='co2value' style='background:#F7F7F7;padding:0.2em 1em 0.3em 0.3em;' >" + properties.threshold + "</td>"
	+"<td class='co2percent'  ></td>"
	+"</tr>";


for(Recipe recipe: vars.recipes){

	long compute = recipe.getId() * iTimeStamp;

	String code = Converter.toString(compute,34);
	String clear = Converter.toString(recipe.getId(),34);
	String dateString = "";
	if(recipe.cookingDate != null){
		dateString = properties.dateFormatter.format(recipe.cookingDate);
	}

	recipe.setCO2Value();
	Double recipeValue = recipe.getCO2Value() + properties.extra;
	
	String length = properties.formatter.format(recipe.getCO2Value()/MaxValueRezept*200);
	String formatted = properties.formatter.format( recipeValue);
	String moreOrLess = "";
	String percent ="";
	
	if((recipeValue+properties.extra)>(properties.threshold)){
		percent = "+" + properties.formatter.format( ((recipeValue-properties.threshold)/(properties.threshold))*100 ) + "%";
	} else {
		percent = "-" + properties.formatter.format( ((properties.threshold-recipeValue)/(properties.threshold))*100 ) + "%";
	}

	if((recipeValue < properties.climateFriendlyValue) && notDoneFirst){
		
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
		if((recipeValue > properties.climateFriendlyValue) && (recipeValue < properties.threshold) &&  notDoneSeccond){ 
			
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
		if((recipeValue > properties.threshold) && notDoneThird){ 
			
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
		int order = (vars.recipes.indexOf(recipe) - counterIterate ) % 2; 
		if(order == 1) { %>
		class="alternate"
		<% }%> > 
		<td class="menu-name">
		<% if(vars.DoItWithPermanentIds) { %><span class="hiddenOnPage" style="display:inline"><%= clear %></span><% } %><input type="checkbox" name="<%= code %>" checked="checked" class="hiddenOnPage" onclick="javascript:addRemoveMenu('<%= code %>')">
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
<li>Die Menus haben einen Durchschnitt von: <%= properties.formatter.format(Util.getAverage(values)) %> g CO<sub>2</sub>* (Median: <%= properties.formatter.format(Util.getMedian((List<Double>)values)) %> g CO<sub>2</sub>*) pro Person.</li>
<% if(properties.extra != 0) {%><li><img class="bar" src="light-gray.png" alt="gray" height="11" width="<%= lengthExtra %>" /> Bei den Menüs wurde <%=extraFormat %> g CO<sub>2</sub>* für die Zubereitung hinzugerechnet.</li><% }%>
</ul>
</form>



<!-- --------------------------- Menu Details Grossartig -------------------------------------- -->



<table cellspacing="0" cellpadding="0" class="table new-page listTable" >
	<tr>
		<td class="table-header">Grossartig</td>
		<td></td>
	</tr>
	<tr>
		<td><p>Diese Rezepte befinden sich unter den besten 20 Prozent. Sie haben unter <%= properties.co2_formatter.format( properties.climateFriendlyValue ) %> g CO<sub>2</sub>* pro Person. <!--Es sind am Rezept keine weiteren Verbesserungen notwendig. Im Einzelfall kann es noch Unklarheiten geben.--></p></td>
		<td></td>
	</tr>
	<!-- 
	<tr>
		<td class="bottom-border"></td>
		<td class="green left-border">Potential in g CO<sub>2</sub>* pro Person</td>
	</tr>
	<tr>
		<td></td>
		<td class="left-border"><br></td>
	</tr>
	 -->
</table>

<% 
for(Recipe recipe: vars.recipes){
	recipe.setCO2Value();
	Double recipeValue = recipe.getCO2Value() + properties.extra;	
	
	if(recipeValue < properties.climateFriendlyValue){
		temp.clear();
		temp.recipes.add(recipe);
		temp.co2Values.addAll(Util.getCO2Values(recipe.getZutaten()));
		
		temp.ingredients.addAll(recipe.getZutaten());
		temp.stopIndex = 3;
		
		temp.personFactor = properties.persons/recipe.getPersons();

		%>
		
		<jsp:include page="/jsp_snippets/snippet_menu.jsp" />
		
		<jsp:include page="/jsp_snippets/snippet_ingredients_ranking.jsp" />
		
		<jsp:include page="/jsp_snippets/snippet_totalvalues.jsp" /> 
	

	<%}%>
<%}%>



<!-- --------------------------- Menu Details Gut -------------------------------------- -->

<table cellspacing="0" cellpadding="0" class="table new-page listTable" >
	<tr>
	<td class="table-header">Gut</td>
	<td></td>
	</tr>
	
	<tr>
	<td><p>Diese Rezepte sind mit unter <%= properties.formatter.format( properties.threshold ) %> g CO<sub>2</sub>* bereits besser als der Durchschnitt. Das ist schonmal ganz gut. <!--Am Rezept sind teilweise weitere Verbesserungen möglich. Sind einige der Vorschläge pro Rezept umsetzbar, wäre dies natürlich grossartig.--></p></td>
	<td></td>
	</tr>
	<!--  
	<tr>
	<td class="bottom-border"></td>
	<td class="green left-border">Potential in g CO<sub>2</sub>*</td>
	</tr>
	
	<tr>
	<td></td>
	<td class="left-border"><br></td>
	</tr> -->
</table>

<%
for(Recipe recipe: vars.recipes){
	
	recipe.setCO2Value();
	Double recipeValue = recipe.getCO2Value() + properties.extra;
	
	if(recipeValue >= properties.climateFriendlyValue && recipeValue < properties.threshold){
		temp.clear();
		temp.recipes.add(recipe);
		temp.co2Values.addAll(Util.getCO2Values(recipe.getZutaten()));
		
		temp.ingredients.addAll(recipe.getZutaten());
		temp.stopIndex = 3;
		
		temp.personFactor = properties.persons/recipe.getPersons();

		%>
		
		<jsp:include page="/jsp_snippets/snippet_menu.jsp" />
		
		<jsp:include page="/jsp_snippets/snippet_ingredients_ranking.jsp" />
		
		<jsp:include page="/jsp_snippets/snippet_totalvalues.jsp" /> 
		
	<%}%>
<%}%>



<!-- --------------------------- Menu Details Über dem Durchschnitt -------------------------------------- -->



<table cellspacing="0" cellpadding="0" class="table new-page listTable" >
	<tr>
	<td class="table-header">Über dem Durchschnitt</td>
	<td></td>
	</tr>
	
	
	<tr>
	<td><p><!--An diesen Rezepten lässt sich entweder noch etwas verbessern – oder man verwendet ein neues alternatives Rezept. -->Diese Rezepte haben über <%= properties.formatter.format( properties.threshold ) %> g CO<sub>2</sub>*. Sie haben also eine unterdurchschnittliche Klimabilanz. Hier wäre es gut noch nachzubessern.</p></td>
	<td></td>
	</tr>
	<!--  
	<tr>
	<td class="bottom-border"></td>
	<td class="green left-border">Potential in g CO<sub>2</sub>*</td>
	</tr>
	
	<tr>
	<td></td>
	<td class="left-border"><br></td>
	</tr>
	-->
</table>
	
<%

for(Recipe recipe: vars.recipes){

	recipe.setCO2Value();
	Double recipeValue = recipe.getCO2Value() + properties.extra;

	if(recipeValue >= properties.threshold){
		temp.clear();
		temp.recipes.add(recipe);
		temp.co2Values.addAll(Util.getCO2Values(recipe.getZutaten()));
		
		temp.ingredients.addAll(recipe.getZutaten());
		temp.stopIndex = 3;
		
		temp.personFactor = properties.persons/recipe.getPersons();

		%>
		
		<jsp:include page="/jsp_snippets/snippet_menu.jsp" />
		
		<jsp:include page="/jsp_snippets/snippet_ingredients_ranking.jsp" />
		
		<jsp:include page="/jsp_snippets/snippet_totalvalues.jsp" /> 

	<%}%>
<%}%>



</div> <!-- class=content -->

<% } // if everythingFine %>

<div id="footer-bottom">
	<img class="logo-karotte" src="karotte.png" alt="karotte"  />
	Eaternity &#10031; Voller Genuss  &#10031; Bewusst Erleben
	<a href="mailto:info@eaternity.ch" >info@eaternity.ch</a>
	<a href="www.eaternity.ch">www.eaternity.ch</a>
</div>

</div> <!-- class=content-website -->

<% if(vars.DoItWithPermanentIds) { %>
<div class="login">
	<%
	if (user != null) { %>
		Diese Angaben sind für den Benutzer <%= user.getNickname() %>. <a href="<%= userService.createLogoutURL(request.getRequestURI()) %>">Abmelden</a>?
		<%
	} 
	else {
    	if (properties.tempIds == null){
			%>
			Sie sind nicht angemeldet.
			<a href="<%= userService.createLoginURL(request.getRequestURI()) %>">Anmelden</a>?
			<%	
			} else {
			
			%>
			Zurück zur <a href="/menu_report.jsp">Übersicht</a>?
			<%
		}
	}
	%>

</div>
<% } // just the simple version %>

</body>


</HTML>



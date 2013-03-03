<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%@ page import="ch.eaternity.shared.Recipe" %>
<%@ page import="ch.eaternity.shared.IngredientSpecification" %>
<%@ page import="ch.eaternity.shared.Converter" %>
<%@ page import="ch.eaternity.shared.Util" %>
<%@ page import="ch.eaternity.shared.Quantity.Weight" %>

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
<title>Menü Report</title>
<link rel="stylesheet" type="text/css" href="reports.css">
	
<% 
	StaticProperties props = (StaticProperties)request.getAttribute("props");
	StaticDataLoader data = (StaticDataLoader)request.getAttribute("data");
	StaticTemp temp = (StaticTemp)request.getAttribute("temp");
	
	Integer counter = 0;
	int counterIterate = 0;
	Collection<Double> values = new ArrayList<Double>();
	
	
	// this should be disfunctional by now
	Date date = new Date();
	long iTimeStamp = (long) (date.getTime() * .00003);
	 
	UserService userService = UserServiceFactory.getUserService();
	User user = userService.getCurrentUser();
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
for(Recipe recipe: data.recipes){
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

<!--  ----------------------------------- Body Begin --------------------------------------- -->

<body onLoad="initThis()">

<%
//Avoid displaying anything if someting is wrong.
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

<h1>Menu Optimierung</h1>
<% if(data.DoItWithPermanentIds) { %>

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
temp.clear();
temp.co2Values.addAll(Util.getCO2ValuesRecipes(data.recipes));
%>

<jsp:include page="/jsp_snippets/snippet_certificate.jsp" />

<!-- --------------------------- Season Table -------------------------------------- -->

<%
temp.clear();
temp.recipes.addAll(data.recipes);
%>

<table cellspacing="0" cellpadding="0" class="table" >
	<tr>
		<td class="table-header">Prima, Frisch und Saisonal kochen.</td>
		<td></td>
	</tr>
	<tr>
		<td><p>Auch Rezepte können eine Saison haben. Schaut man sich jede einzelne Zutat an sieht man sofort, wann ein Rezept saison hat. Manchmal lohnt es sich also eine saisonale Zutat als alternative zu verwenden - oder das Rezept dann zu kochen, wenn es auch am besten schmeckt. Dann wenn alles frisch ist und aus der Region kommt.</p></td>
		<td></td>
	</tr>
</table>


<jsp:include page="/jsp_snippets/snippet_season_table.jsp" />

<!-- --------------------------- Menu Classify List -------------------------------------- -->

<% 
temp.clear();
temp.recipes.addAll(data.recipes);
%>

<jsp:include page="/jsp_snippets/snippet_menus_classify.jsp" />



<!-- --------------------------- Menu Details Grossartig -------------------------------------- -->

<table cellspacing="0" cellpadding="0" class="table new-page listTable" >
	<tr>
		<td class="table-header">Grossartig</td>
		<td></td>
	</tr>
	<tr>
		<td><p>Diese Rezepte befinden sich unter den besten 20 Prozent. Sie haben unter <%= props.co2_formatter.format( props.climateFriendlyValue ) %> g CO<sub>2</sub>* pro Person. <!--Es sind am Rezept keine weiteren Verbesserungen notwendig. Im Einzelfall kann es noch Unklarheiten geben.--></p></td>
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
	for(Recipe recipe: data.recipes){
	recipe.setCO2Value();
	Double recipeValue = recipe.getCO2Value() + props.extra;	
	
	if(recipeValue < props.climateFriendlyValue){
		temp.clear();
		temp.recipe = recipe;
		temp.co2Values.addAll(Util.getCO2ValuesIngredients(recipe.getZutaten()));
		
		temp.ingredients.addAll(recipe.getZutaten());
		temp.startIndex = 0;
		temp.stopIndex = 5;
		
		temp.personFactor = props.persons/recipe.getPersons().doubleValue();
		temp.title = "<h3>" + recipe.getSymbol() + "</h3>";
		temp.subtitle = recipe.getSubTitle();
%>
		
		<jsp:include page="/jsp_snippets/snippet_menu.jsp" />
		
		<jsp:include page="/jsp_snippets/snippet_ingredients_ranking.jsp" />
		
		<jsp:include page="/jsp_snippets/snippet_totalvalues.jsp" /> 
	

	<%
 			}
 		%>
<%
	}
%>



<!-- --------------------------- Menu Details Gut -------------------------------------- -->

<table cellspacing="0" cellpadding="0" class="table new-page listTable" >
	<tr>
	<td class="table-header">Gut</td>
	<td></td>
	</tr>
	
	<tr>
	<td><p>Diese Rezepte sind mit unter <%=props.formatter.format( props.average )%> g CO<sub>2</sub>* bereits besser als der Durchschnitt. Das ist schonmal ganz gut. <!--Am Rezept sind teilweise weitere Verbesserungen möglich. Sind einige der Vorschläge pro Rezept umsetzbar, wäre dies natürlich grossartig.--></p></td>
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
	for(Recipe recipe: data.recipes){
	
	recipe.setCO2Value();
	Double recipeValue = recipe.getCO2Value() + props.extra;
	
	if(recipeValue >= props.climateFriendlyValue && recipeValue < props.average){
		temp.clear();
		temp.recipes.add(recipe);
		temp.co2Values.addAll(Util.getCO2ValuesIngredients(recipe.getZutaten()));
		
		temp.ingredients.addAll(recipe.getZutaten());
		temp.stopIndex = 3;
		
		temp.personFactor = props.persons/recipe.getPersons().doubleValue();
		temp.title = "<h3>" + recipe.getSymbol() + "</h3>";
		temp.subtitle = recipe.getSubTitle();
%>
		
		<jsp:include page="/jsp_snippets/snippet_menu.jsp" />
		
		<jsp:include page="/jsp_snippets/snippet_ingredients_ranking.jsp" />
		
		<jsp:include page="/jsp_snippets/snippet_totalvalues.jsp" /> 
		
	<%
 				}
 			%>
<%
	}
%>



<!-- --------------------------- Menu Details Über dem Durchschnitt -------------------------------------- -->



<table cellspacing="0" cellpadding="0" class="table new-page listTable" >
	<tr>
	<td class="table-header">Über dem Durchschnitt</td>
	<td></td>
	</tr>
	
	
	<tr>
	<td><p><!--An diesen Rezepten lässt sich entweder noch etwas verbessern – oder man verwendet ein neues alternatives Rezept. -->Diese Rezepte haben über <%=props.formatter.format( props.average )%> g CO<sub>2</sub>*. Sie haben also eine unterdurchschnittliche Klimabilanz. Hier wäre es gut noch nachzubessern.</p></td>
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
		for(Recipe recipe: data.recipes){

		recipe.setCO2Value();
		Double recipeValue = recipe.getCO2Value() + props.extra;

		if(recipeValue >= props.average){
			temp.clear();
			temp.recipes.add(recipe);
			temp.co2Values.addAll(Util.getCO2ValuesIngredients(recipe.getZutaten()));
			
			temp.ingredients.addAll(recipe.getZutaten());
			temp.stopIndex = 3;
			
			temp.personFactor = props.persons/recipe.getPersons().doubleValue();
			temp.title = "<h3>" + recipe.getSymbol() + "</h3>";
			temp.subtitle = recipe.getSubTitle();
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

<% if(data.DoItWithPermanentIds) { %>
<div class="login">
	<%
	if (user != null) { %>
		Diese Angaben sind für den Benutzer <%= user.getNickname() %>. <a href="<%= userService.createLogoutURL(request.getRequestURI()) %>">Abmelden</a>?
		<%
	} 
	else {
    	if (props.tempIds == null){
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



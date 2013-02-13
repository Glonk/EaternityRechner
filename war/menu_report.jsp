<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%@ page import="ch.eaternity.shared.Recipe" %>
<%@ page import="ch.eaternity.shared.IngredientSpecification" %>
<%@ page import="ch.eaternity.shared.Converter" %>
<%@ page import="ch.eaternity.shared.RecipeComment" %>
<%@ page import="ch.eaternity.shared.comparators.RezeptValueComparator" %>

<%@ page import="ch.eaternity.shared.CatRyzer" %>
<%@ page import="ch.eaternity.server.StaticPageService" %>
<%@ page import="java.util.Locale" %>

<%@ page import="java.util.List" %>
<%@ page import="java.util.Calendar" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.Arrays" %>
<%@ page import="java.text.DecimalFormat" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.Collections" %>
<%@ page import="java.util.Date" %>
<%@ page import="java.util.Collection" %>

<%@ page import="java.net.MalformedURLException" %>
<%@ page import="java.net.URL" %>
<%@ page import="java.io.BufferedReader" %>
<%@ page import="java.io.InputStreamReader" %>
<%@ page import="java.io.IOException" %>



<html>
<head>

<meta HTTP-EQUIV="Content-Type" CONTENT="text/html; charset=UTF-8">
<title>Menü Report</title>
<link rel="stylesheet" type="text/css" href="menu_report.css">

<%
	//Specific Parameters to set for Display] 
	Locale locale = Locale.GERMAN;

	DecimalFormat formatter = new DecimalFormat("##");
	DecimalFormat co2_formatter = new DecimalFormat("##.#");
	DecimalFormat cost_formatter = new DecimalFormat("##");
	DecimalFormat weight_formatter = new DecimalFormat("##.#");
	DecimalFormat distance_formatter = new DecimalFormat("##");
	SimpleDateFormat dateFormatter = new SimpleDateFormat("dd.MMMM yyyy");
	
	int co2BarLength = 180;
	int barOffset = 45;

	// parse request parameters
	String pdf = request.getParameter("pdf");
	Boolean doPdf = false;
	if(pdf != null){
		doPdf = true;
	}
	
	String thresholdString = request.getParameter("median");
	Integer threshold = 1550;
	if(thresholdString != null){
		threshold = Integer.valueOf(thresholdString);
	} 
	
	String extraString = request.getParameter("extra");
	Integer extra = 0;
	if(extraString != null){
		extra = Integer.valueOf(extraString);
	}

	Double third = (double)threshold / 3;
	Double half = (double)threshold / 2;
	Double twoFifth = (double)threshold / 5 * 2;
	
	Double climateFriendlyValue = twoFifth;
	
	Integer counter = 0;
	int counterIterate = 0;
	Collection<Double> values = new ArrayList<Double>();
	
	
	// this should be disfunctional by now
	Date date = new Date();
	long iTimeStamp = (long) (date.getTime() * .00003);
	 
	StaticPageService vars = new StaticPageService(request,Locale.GERMAN,false);
		
	// Initialize Catryzer
	CatRyzer catryzer = new CatRyzer(vars.recipes,locale);
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
	
	if(vars.getAverage() > 0){
	
	String total = "200";
	String klimafriendly = formatter.format(200*2/5);
	String length = formatter.format(vars.getAverage()/(threshold)*200);
	String formatted = formatter.format( vars.getAverage() );
	
	String moreOrLess = "";
	String percent ="";
	Integer position = 1;
	
	if(vars.getAverage()<=(threshold)){
		percent = formatter.format( -((vars.getAverage()-threshold)/(threshold))*100 );
	
	
	
	if((climateFriendlyValue-vars.getAverage())<0){
	//	percent = formatter.format( (-(climateFriendlyValue-vars.getAverage())/(climateFriendlyValue))*100 );
		moreOrLess = "weniger";
		position = 2;
	} else {
		percent = formatter.format( ((climateFriendlyValue-vars.getAverage())/(climateFriendlyValue))*100 );
		moreOrLess = "weniger";
	}
	
	}
	
	if(vars.getAverage()>(threshold)){
		position = 3;
		length = "200";
		total = formatter.format((threshold/vars.getAverage())*200);
		klimafriendly = formatter.format((climateFriendlyValue/vars.getAverage())*200);
		percent = formatter.format( ((vars.getAverage()-threshold)/(threshold))*100 );
		moreOrLess = "mehr";
	
	}



		%>			
		<table style="font-weight: 300;font-size: 14pt; margin-top:4em" >
					<!-- label of table -->


					<tr> <td>


		<table cellspacing="0" cellpadding="0" border="1" class="table rounded label-table">

		<!-- head of the label -->
		<tr style="border-bottom:0px;">
			<td style="border-bottom:0px;">
				<table cellspacing="0" cellpadding="0"  border="0" style="font-weight: 300;font-size: 11pt;margin-top:-20px;">
					<tr>
					 <td style="width:2em">

					 </td>

					 <td style="width:6em;text-align: right;padding:0em 1em 0em 1em;background:white;" ><span class="label-vergleich">Im Vergleich</span></td>
					 <td class="left-border" style="text-align: left;padding-left:1em;background:white;">
					 <% if (position<2){ %>
					 Die Menus verursachen <span style="font-size: 11pt;font-weight: 400;"><%= percent %>% weniger</span> CO<sub>2</sub>* als die vergleichbaren klimafreundlichen Menus.
					 <% }else{ %>
					 Die Menus verursachen <span style="font-size: 11pt;font-weight: 400;"><%= percent %>% <%= moreOrLess %></span> CO<sub>2</sub>* als die vergleichbaren Menus im Durchschnitt.
					 <% } %>
					 </td>

					 <td style="width:11em">

					 </td>
					</tr>
				</table>
			</td>
		</tr>

		<!-- body of the label -->
		<tr>
			<td style="border-top:0px;border-bottom:0px;">
				<table cellspacing="0" cellpadding="0"  border="0" style="width:100%">
					<tr>
					 <td>

						<table cellspacing="0" cellpadding="0" border="1" style="font-weight: 300;font-size: 11pt;margin:2.5em 2em 2em 2em;border:0px;">
						<tr><td style="text-align:right;border-top: 0px;border-bottom: 0px;border-left: 0px;"></td><td style="border-top: 0px;border-bottom: 0px;border-right: 0px"></td></tr>
						<% if (position==1){ %>
							<tr  height="28">
							 <td style="text-align:right;vertical-align:middle;border-top: 0px;border-bottom: 0px;border-left: 0px;padding-right:0.3em;white-space:nowrap;font-size: 11pt;font-weight: 600;text-transform: uppercase;">
							 	Die Menus
							 </td>
							 <td style="border-top: 0px;border-bottom: 0px;border-right: 0px">
							 	<img class="bar" src="green.png" alt="green" height="15"  width="<%= length %>" />
<!-- 								 	<%= formatted %> g CO<sub>2</sub>* -->
							 </td>
							</tr>
						<% } %>
							<tr height="28">
							 <td style="text-align:right;vertical-align:top;border-top: 0px;border-bottom: 0px;border-left: 0px;padding-right:0.3em;width:8em;font-size: 9pt; line-height: 11px; ">
							 	klimafreundliche Menus
							 </td>
							 <td style="border-top: 0px;border-bottom: 0px;border-right: 0px">
							 	<img class="bar" src="orange.png" alt="orange" height="15"  width="<%= klimafriendly %>" />
<!-- 								 	<%= formatted %> g CO<sub>2</sub>* -->
							 </td>
							</tr>
						<% if (position==2){ %>
							<tr  height="28">
							 <td style="text-align:right;vertical-align:middle;border-top: 0px;border-bottom: 0px;border-left: 0px;padding-right:0.3em;white-space:nowrap;font-size: 11pt;font-weight: 600;text-transform: uppercase;">
							 	Die Menus
							 </td>
							 <td style="border-top: 0px;border-bottom: 0px;border-right: 0px">
							 	<img class="bar" src="green.png" alt="green" height="15"  width="<%= length %>" />
<!-- 								 	<%= formatted %> g CO<sub>2</sub>* -->
							 </td>
							</tr>
						<% } %>
							<tr height="28">
							 <td style="text-align:right;vertical-align:middle;border-top: 0px;border-bottom: 0px;border-left: 0px;padding-right:0.3em;font-size: 9pt;">
							 	Alle Menus
							 </td>
							 <td style="border-top: 0px;border-bottom: 0px;border-right: 0px">
							 	<img class="bar" src="gray.png" alt="gray" height="15"  width="<%= total %>" />
<!-- 								 	<%= formatted %> g CO<sub>2</sub>* -->
							 </td>
							</tr>
						<% if (position==3){ %>
							<tr  height="28">
							 <td style="text-align:right;vertical-align:middle;border-top: 0px;border-bottom: 0px;border-left: 0px;padding-right:0.3em;white-space:nowrap;font-size: 11pt;font-weight: 600;text-transform: uppercase;">
							 	Die Menus
							 </td>
							 <td style="border-top: 0px;border-bottom: 0px;border-right: 0px">
							 	<img class="bar" src="green.png" alt="green" height="15"  width="<%= length %>" />
<!-- 								 	<%= formatted %> g CO<sub>2</sub>* -->
							 </td>
							</tr>
						<% } %>	

							<tr><td style="text-align:right;border-top: 0px;border-bottom: 0px;border-left: 0px"></td><td style="border-top: 0px;border-bottom: 0px;border-right: 0px"></td></tr>
						</table>
					 </td>

					<td>
								<span class="howAreYou" style="float:right;margin-top:1em;margin-bottom:-1.8em;font-size:10pt;">Wie ist die Klimabilanz:</span>
							 	<table cellspacing="0" cellpadding="0" border="1" class="rounded" style="float:right;font-weight: 300;font-size: 11pt;margin:2em;padding:1em;">
									<tr><td style="text-align:right;border:0px"></td><td style="border:0px"></td></tr>
									<tr height="28">
									<% if (position==1){ %>
									 <td style="border:0px; font-size: 11pt;font-weight: 600;white-space: nowrap;">
									 	&#x25b6; GROSSARTIG <img class="smile" src="smiley8.png" style="float:none;padding-left:0.5em;margin-bottom:-7px;" alt="smiley" /><img class="smile" src="smiley8.png" style="float:none;margin-bottom:-7px;" alt="smiley" />										 
									 	</td>
									 <% } else { %>
									 <td style="border:0px; color:gray;font-size: 9pt;">
									 	Grossartig
									 </td>
									 <% } %>
									</tr>
									<tr height="28">
									<% if (position==2){ %>
									 <td style="border:0px; font-size: 11pt;font-weight: 600;">
									 	&#x25b6; GUT <img class="smile" src="smiley8.png" style="float:none;padding-left:0.5em;margin-bottom:-7px;" alt="smiley" />
									 </td>
									 <% } else { %>
									 <td style="border:0px; color:gray;font-size: 9pt;">
									 	Gut
									 </td>
									 <% } %>
									</tr>
									<tr height="28">
									<% if (position==3){ %>
									 <td style="border:0px; font-size: 9pt;font-weight: 600;text-transform: uppercase;white-space: nowrap;padding-right:0.2em;">
									 	&#x25b6; Unter Durchschnitt
									 </td>
									 <% } else { %>
									 <td style="border:0px; color:gray;white-space: nowrap;font-size: 9pt;padding-right:1em;">
									 	Unter Durchschnitt
									 </td>
									 <% } %>
									</tr>
									<tr><td style="text-align:right;border-top: 0px;border-bottom: 0px;border-left: 0px"></td><td style="border:0px"></td></tr>
								</table>


					 </td>
					</tr>

				</table>
			</td>
		</tr>

		<!-- legend of the label -->
		<tr>
			<td style="border-top:0px;">
				 <table cellspacing="0" cellpadding="0" border="1" style="font-weight: 300;font-size: 11pt;padding:1em;margin:1em 1em 0em 1em;border-bottom:0px;border-left:0px;border-right:0px;">
					<tr >
					 <td style="padding: 0em 1em 0em 0em;width:9em;border:0px;text-transform: uppercase;vertical-align:top; text-align:center;font-size: 11pt;font-weight: 400;">

					 Welche Menus werden verglichen?

					 </td>

					<td style="vertical-align:top;padding: 0.5em 1em 0em 0em;border-top: 0px;border-bottom: 0px;border-right: 0px;border-left: 0px;font-size: 9pt;" >
					 	<img class="bar" src="gray.png" alt="gray" width="11" height="11" style="padding:3px 3px 0px 0px;" />
					 	ALLE MENUS<br /> Alle gemessenen vergleichbaren Menus.

					 </td>
					 <td style="vertical-align:top;padding: 0.5em 1em 0em 0em;border:0px;font-size: 9pt;">
					 	<img class="bar" src="orange.png" alt="gray" width="11" height="11" style="padding:3px 3px 0px 0px;"/>
					 	KLIMAFREUNDLICHE MENUS<br />Die besten 20% aus der Gruppe "Alle Menus".

					 </td>

					 <td style="padding:0em 0em 0em 1em;text-align:right;border:0px;width:4em;" class="left-border"><% if(vars.DoItWithPermanentIds) { %>
<a href="<%= vars.BASEURL %>?pid="><img src="QR--CODE" width="42" height="42" /></a>
						<% } else { %> <span style="color:red;font-size:9pt;"></span> <% } %>
					 </td>
					</tr>


				</table>


				</td>
			</tr>
		</table>
		<br />
	</td>
</tr>


</table>


<%
}	
%>

<!-- --------------------------- Menu List -------------------------------------- -->

<form name="htmlAdder" method="POST" action=";">
	
<table cellspacing="0" cellpadding="0" class="table toc" >


<tr>
<td></td>
<td class="gray left-border"></td>
<td class="gray co2label"><span class="nowrap">g CO<sub>2</sub>*</span></td>
<td></td>
</tr>

<%

Collections.sort(vars.recipes,new RezeptValueComparator());

Boolean notDoneFirst = true;
Boolean notDoneSeccond = true;
Boolean notDoneThird = true;

Double MaxValueRezept = vars.getMax(vars.values);

Double adjustedAverageLength = threshold/MaxValueRezept*200;
Double climateFriendlyValueLength = climateFriendlyValue/MaxValueRezept*200;
String averageLength = formatter.format(adjustedAverageLength);
String formattedClimate = formatter.format(climateFriendlyValue);
String smilies = "";
String extraFormat = formatter.format(extra);
String lengthExtra = formatter.format(extra/MaxValueRezept*200);


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
	+"<td class='co2value' style='background:#F7F7F7;padding:0.2em 1em 0.3em 0.3em;' >" + threshold + "</td>"
	+"<td class='co2percent'  ></td>"
	+"</tr>";


for(Recipe recipe: vars.recipes){

	long compute = recipe.getId() * iTimeStamp;

	String code = Converter.toString(compute,34);
	String clear = Converter.toString(recipe.getId(),34);
	String dateString = "";
	if(recipe.cookingDate != null){
		dateString = dateFormatter.format(recipe.cookingDate);
	}
	

	recipe.setCO2Value();
	
	Double recipeValue = recipe.getCO2Value() + extra;
	
	String length = formatter.format(recipe.getCO2Value()/MaxValueRezept*200);

	String formatted = formatter.format( recipeValue);
	String persons = Long.toString(recipe.getPersons());
	
	String moreOrLess = "";
	String percent ="";

	
	if((recipeValue+extra)>(threshold)){
		percent = "+" + formatter.format( ((recipeValue-threshold)/(threshold))*100 ) + "%";
	} else {
		percent = "-" + formatter.format( ((threshold-recipeValue)/(threshold))*100 ) + "%";
	}

	

	
	if((recipeValue < climateFriendlyValue) && notDoneFirst){
		
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
		if((recipeValue > climateFriendlyValue) && (recipeValue < threshold) &&  notDoneSeccond){ 
			
			smilies = "<img class='smile' src='smiley8.png' alt='smiley' />";
			
		if(notDoneSeccond){
			notDoneSeccond = false;
			out.print(klima);
			klima = "";
			
			%>
		

		
		<tr>
		<td class="table-header bottom-border">Gut</td>
		<td class="left-border"></td>
		<td class="co2value" ></td>
		<td class="co2percent"  ></td>
		</tr>
		
		
		<%
	}
	
		}
		if((recipeValue > threshold) && notDoneThird){ 
			
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
		<%= smilies %><%= recipe.getSymbol() %> - <%= dateString %>
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
<li>Die Menus haben einen Durchschnitt von: <%= formatter.format(vars.getAverage()) %> g CO<sub>2</sub>* (Median: <%= formatter.format(vars.getMedian()) %> g CO<sub>2</sub>*) pro Person.</li>
<% if(extra != 0) {%><li><img class="bar" src="light-gray.png" alt="gray" height="11" width="<%= lengthExtra %>" /> Bei den Menüs wurde <%=extraFormat %> g CO<sub>2</sub>* für die Zubereitung hinzugerechnet.</li><% }%>
</ul>
</form>



<!-- --------------------------- Menu Details Grossartig -------------------------------------- -->



<table cellspacing="0" cellpadding="0" class="table new-page listTable" >


<tr>
<td class="table-header">Grossartig</td>
<td></td>
</tr>
	
<tr>
<td><p>Diese Rezepte befinden sich unter den besten 20 Prozent. Sie haben unter <%= formatter.format( climateFriendlyValue ) %> g CO<sub>2</sub>* pro Person. <!--Es sind am Rezept keine weiteren Verbesserungen notwendig. Im Einzelfall kann es noch Unklarheiten geben.--></p></td>
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
	
<%


for(Recipe recipe: vars.recipes){

	long compute = recipe.getId() * iTimeStamp;
	String code = Converter.toString(compute,34);

	recipe.setCO2Value();
	Double recipeValue = recipe.getCO2Value() + extra;
	if(recipeValue < climateFriendlyValue){
	
	
	String formatted = formatter.format( recipeValue );
	String persons = Long.toString(recipe.getPersons());
	
	
	%>
	
	<table cellspacing="0" cellpadding="0" class="table listTable" >
	<tr>
	<td></td>
	<td class="left-border"><br></td>
	</tr>
	
	<tr>
	<td class="bottom-border">
	<img class="smile" src="smiley8.png" alt="smiley" />
	<img class="smile" src="smiley8.png" alt="smiley" />
	<h3><%= recipe.getSymbol() %></h3>
	</td>
	<td class="left-border"></td>
	</tr>

	<tr>
	<td><div class="amount"><%= formatted %> g CO<sub>2</sub>* total</div></td>
	<td class="left-border"><img class="bar" height="11"  src="gray.png" alt="gray" width="140" /></td>
	</tr>
	
	<tr>
	<td>
	
	<span class="subTitle"><%= recipe.getSubTitle() %></span>
	
	<span style="color:gray;">Zutaten für <%= persons %> Personen:</span><br />

	
		<%	
		counter = 0;
		for(IngredientSpecification ingredient: recipe.Zutaten){
		counter = counter + 1;

		%><% if(counter != 1){ %>, <% } %><span class="nowrap"><%= ingredient.getMengeGramm() %> g <%= ingredient.getName() %></span><%
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

// -------------------------------- Top 3 intensive Ingredients --------------------------- 
 %>

<table cellspacing="0" cellpadding="0" class="table toc" >

<tr>
<td></td>
<td class="gray left-border"></td>
<td class="gray co2label"><span class="nowrap">kg CO<sub>2</sub>*</span></td>
<td></td>
</tr>

<tr>
<td class="table-header bottom-border">Top 3 CO<sub>2</sub>-intensive Zutaten</td>
<td class="left-border"></td>
<td class="co2value" ></td>
<td ></td>
</tr>


<%
counterIterate = 0;

values.clear();

for(CatRyzer.CategoryValue ingredientValue : valuesByIngredient){
	values.add(ingredientValue.co2value.totalValue);
}


for(CatRyzer.CategoryValue ingredientValue : valuesByIngredient){
	if (valuesByIngredient.indexOf(ingredientValue) == 3){
		break;
	} 
%>

	<tr <%
	int order = (valuesByIngredient.indexOf(ingredientValue) - counterIterate ) % 2; 
	if(order == 1) { %>
	class="alternate"
	<% }%> > 
	<td class="menu-name">
	<%= ingredientValue.categoryName %> (<%=ingredientValue.weight/1000%> kg)  (<%= cost_formatter.format(ingredientValue.cost) %> CHF)
	</td>
	<td class="left-border" width="<%= co2BarLength + barOffset %>px"><%= vars.getCo2ValueBar(values, ingredientValue.co2value, co2BarLength) %></td>
	<td class="co2value" ><%= co2_formatter.format(ingredientValue.co2value.totalValue/1000) %></td>
	
	</tr>

<%
}

%>
</table>


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
<td><p>Diese Rezepte sind mit unter <%= formatter.format( threshold ) %> g CO<sub>2</sub>* bereits besser als der Durchschnitt. Das ist schonmal ganz gut. <!--Am Rezept sind teilweise weitere Verbesserungen möglich. Sind einige der Vorschläge pro Rezept umsetzbar, wäre dies natürlich grossartig.--></p></td>
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

<!--  <%= Integer.toString(vars.recipes.size()) %>  -->
<%


for(Recipe recipe: vars.recipes){

long compute = recipe.getId() * iTimeStamp;
String code = Converter.toString(compute,34);

			recipe.setCO2Value();
			Double recipeValue = recipe.getCO2Value() + extra;
			if(recipeValue >= climateFriendlyValue && recipeValue < threshold){

			String formatted = formatter.format( recipeValue );
			String persons = Long.toString(recipe.getPersons());
			%>
			
			<table cellspacing="0" cellpadding="0" class="table listTable" >
			<tr>
			<td></td>
			<td class="left-border"><br></td>
			</tr>
			
			<tr>
			<td class="bottom-border">
			<img class="smile" src="smiley8.png" alt="smiley" />
			<h3><%= recipe.getSymbol() %></h3>
			</td>
			<td class="left-border"></td>
			</tr>

			<tr>
			<td><div class="amount"><%= formatted %> g CO<sub>2</sub>* total</div></td>
			<td class="left-border"><img class="bar" src="gray.png" alt="gray" height="11"  width="140" /></td>
			</tr>
			
			<tr>
			<td>
			
			<span class="subTitle"><%= recipe.getSubTitle() %></span>
			
			<span style="color:gray;">Zutaten für <%= persons %> Personen:</span><br />

			
				<%	
				counter = 0;
				for(IngredientSpecification ingredient: recipe.Zutaten){
				counter = counter + 1;

				%><% if(counter != 1){ %>, <% } %><span class="nowrap"><%= ingredient.getMengeGramm() %> g <%= ingredient.getName() %></span><%
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

%>





<!-- --------------------------- Menu Details Über dem Durchschnitt -------------------------------------- -->



<table cellspacing="0" cellpadding="0" class="table new-page listTable" >


<tr>
<td class="table-header">Über dem Durchschnitt</td>
<td></td>
</tr>


<tr>
<td><p><!--An diesen Rezepten lässt sich entweder noch etwas verbessern – oder man verwendet ein neues alternatives Rezept. -->Diese Rezepte haben über <%= formatter.format( threshold ) %> g CO<sub>2</sub>*. Sie haben also eine unterdurchschnittliche Klimabilanz. Hier wäre es gut noch nachzubessern.</p></td>
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
	
<!--  <%= Integer.toString(vars.recipes.size()) %>  -->
<%


for(Recipe recipe: vars.recipes){

long compute = recipe.getId() * iTimeStamp;
String code = Converter.toString(compute,34);

			recipe.setCO2Value();
			Double recipeValue = recipe.getCO2Value() + extra;
			if(recipeValue >= threshold){
			

			String formatted = formatter.format(recipeValue );
			String persons = Long.toString(recipe.getPersons());
			%>
			
			<table cellspacing="0" cellpadding="0" class="table listTable" >
			<tr>
			<td></td>
			<td class="left-border"><br></td>
			</tr>
			
			<tr>
			<td class="bottom-border">
			<h3><%= recipe.getSymbol() %></h3>
			</td>
			<td class="left-border"></td>
			</tr>

			<tr>
			<td><div class="amount"><%= formatted %> g CO<sub>2</sub>* total</div></td>
			<td class="left-border"><img class="bar" src="gray.png" alt="gray" height="11"  width="140" /></td>
			</tr>
			
			<tr>
			<td>
			
			<span class="subTitle"><%= recipe.getSubTitle() %></span>
			
			<span style="color:gray;">Zutaten für <%= persons %> Personen:</span><br />

			
				<%	
				counter = 0;
				for(IngredientSpecification ingredient: recipe.Zutaten){
				counter = counter + 1;

				%><% if(counter != 1){ %>, <% } %><span class="nowrap"><%= ingredient.getMengeGramm() %> g <%= ingredient.getName() %></span><%
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

%>


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
	if (vars.user != null) { %>
		Diese Angaben sind für den Benutzer <%= vars.user.getNickname() %>. <a href="<%= vars.userService.createLogoutURL(request.getRequestURI()) %>">Abmelden</a>?
		<%
	} 
	else {
    	if (vars.tempIds == null){
			%>
			Sie sind nicht angemeldet.
			<a href="<%= vars.userService.createLoginURL(request.getRequestURI()) %>">Anmelden</a>?
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



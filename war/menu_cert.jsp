<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%@ page import="ch.eaternity.server.DAO" %>
<%@ page import="ch.eaternity.shared.Recipe" %>
<%@ page import="ch.eaternity.shared.IngredientSpecification" %>
<%@ page import="ch.eaternity.shared.Converter" %>

<%@ page import="java.util.List" %>
<%@ page import="java.util.Calendar" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.text.DecimalFormat" %>
<%@ page import="java.util.Collections" %>

<%@ page import="ch.eaternity.shared.CatRyzer" %>
<%@ page import="ch.eaternity.server.reports.StaticPageService" %>
<%@ page import="java.util.Locale" %>

<%@ page import="java.net.MalformedURLException" %>
<%@ page import="java.net.URL" %>
<%@ page import="java.io.BufferedReader" %>
<%@ page import="java.io.InputStreamReader" %>
<%@ page import="java.io.IOException" %>
<%@ page import="java.util.Date" %>
<%@ page import="java.util.Collection" %>

<html>
<head>

<meta HTTP-EQUIV="Content-Type" CONTENT="text/html; charset=UTF-8">
<title>Menü Klima-Bilanz Zertifikat</title>

<link rel="stylesheet" type="text/css" href="menu_cert.css">

<%
	// Specific Parameters to set for Display] 
	Locale locale = Locale.GERMAN;

	DecimalFormat formatter = new DecimalFormat("##");
	DecimalFormat co2_formatter = new DecimalFormat("##.#");
	DecimalFormat cost_formatter = new DecimalFormat("##");
	DecimalFormat weight_formatter = new DecimalFormat("##.#");
	DecimalFormat distance_formatter = new DecimalFormat("##");
	
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



<style type="text/css">
<% if(!doPdf){ %>
	@import url(http://fonts.googleapis.com/css?family=Open+Sans:300,400,600,700,800);
	
	@font-face {
	  font-family: 'Open Sans';
	  font-style: normal;
	  font-weight: 300;
	  src: local('Open Sans Light'), local('OpenSans-Light'), url('opensans300.woff') format('woff');
	}
	<% } %>
	
	@page {
	  size: A4;
	  margin: 40pt 20pt 70pt 40pt;
	   padding: 0pt 0pt 0pt 0pt;
		prince-image-resolution: 300dpi;
	}
	  @bottom { 
	    font: 9pt 'Open Sans', Verdana, sans-serif; font-weight: 300;
	    }
	  @bottom-left { 
	    content: flow(footer);
	    }
	    
	  @top {
	   	content: flow(header);
	  }
	  @media screen {
	html { background: gray; }
	table {
		font-size: 12pt;
	}
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
	String clear = Converter.toString(recipe.getId(),34);

	%>
idsToAdd['<%= clear %>'] = true<%	 }	%>

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
    document.getElementById('getPdf').href=baseUrl+"convert?pid="+hrefAdd
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

<!--  ----------------------------------- Body Begin --------------------------------------- -->

<div class="website-content">

<div id="header-right">
	<img class="logo-klein" src="logo-eaternity-huge_04-11-2010.png" alt="logo-eaternity-huge_04-11-2010" />
</div>


<div class="content">

<% 
if(vars.everythingFine){


for(Recipe recipe: vars.recipes){

	long compute = recipe.getId() * iTimeStamp;
	String code = Converter.toString(compute,34);
	
	String clear = Converter.toString(recipe.getId(),34);

	if(recipe.getCO2Value() > 0){
	
		String total = "200";
		String klimafriendly = formatter.format(200*2/5);
		String length = formatter.format(recipe.getCO2Value()/(threshold)*200);
		String formatted = formatter.format( recipe.getCO2Value() );
		
		String moreOrLess = "";
		String percent ="";
		Integer position = 1;
		
		if((climateFriendlyValue-recipe.getCO2Value())<0){
			percent = formatter.format( (-(climateFriendlyValue-recipe.getCO2Value())/(climateFriendlyValue))*100 );
			moreOrLess = "mehr";
			position = 2;
		} else {
			percent = formatter.format( ((climateFriendlyValue-recipe.getCO2Value())/(climateFriendlyValue))*100 );
			moreOrLess = "weniger";
		}
		
		if(recipe.getCO2Value()>(threshold)){
			position = 3;
			length = "200";
			total = formatter.format((threshold/recipe.getCO2Value())*200);
			klimafriendly = formatter.format((climateFriendlyValue/recipe.getCO2Value())*200);
			percent = formatter.format( ((recipe.getCO2Value()-threshold)/(threshold))*100 );
	}
	
	String persons = Long.toString(recipe.getPersons());
	
	%>	

<!--  ----------------------------------- Recipe Details --------------------------------------- -->
	
<!-- Titel and Image -->
			<table style="font-weight: 300;font-size: 14pt; margin-top:-4em;">
			<tr width="660">
			<td height="140">
				<h3 style="font-size: 28pt; padding-bottom:0em; display:block; margin-right:5em;"><%= recipe.getSymbol() %></h3>
				<p style="display:block;clear:both">	<%= recipe.getSubTitle() %> – <%= formatted %> g CO<sub>2</sub>* pro Person.</p>
				
				<%
				if(recipe.image != null){
					%>
					
					<% if(!doPdf){%>
						<img class="cover-all" src="<%=recipe.image.getServingUrl()%>=s800" />
					<% } else { %>
						<img class="cover-all" src="COVER-<%=recipe.image.getServingUrl()%>-IMAGE" />
					<% } %>
					 
					
					<%
				}
				
				%>
			</td>
			</tr>
			
<!-- Ingredient List -->
			<tr>
			<td>
				<table cellspacing="0" cellpadding="0" class="table cover-up" >
					<tr>
						<td>
						<span style="color:gray;">Für <%= persons %> Personen:</span>
						<ul class="zutat">
							<%for(IngredientSpecification ingredient: recipe.Zutaten){ %>
								<li><%= ingredient.getMengeGramm() %> g <span class="ix"><%= ingredient.getName() %></span> </li>
							<% } %>
	
						</ul>

						</td>
						<td><br>
							<p style="display:block;clear:both"><%= recipe.getCookInstruction() %></p>
						</td>
					</tr>
				</table>
			</td>
			</tr>
			<tr>
			<td>

<!--  ----------------------------------- Certificate Box --------------------------------------- -->	
			
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
						 <% if (position<3){ %>
						 Dieses Menu verursacht <span style="font-size: 11pt;font-weight: 400;"><%= percent %>% <%= moreOrLess %></span> CO<sub>2</sub>* als die vergleichbaren klimafreundlichen Menus.
						 <% }else{ %>
						 Dieses Menu verursacht <span style="font-size: 11pt;font-weight: 400;"><%= percent %>% mehr </span> CO<sub>2</sub>* als die vergleichbaren  Menus im Durchschnitt.
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
								 	Dieses Menu
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
								 	Dieses Menu
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
								 	Dieses Menu
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
						 
						 <td style="padding:0em 0em 0em 1em;text-align:right;border:0px;width:4em;" class="left-border">
	<a href="<%= vars.BASEURL %>?pid=<%= clear %>">
		<% if(!doPdf){%>
			<img src="http://chart.apis.google.com/chart?cht=qr&amp;chs=84x84&amp;chld=M|0&amp;chl=<%= recipe.ShortUrl.substring(7, recipe.ShortUrl.length()) %>" width="42" height="42" />
		<% } else { %>
			<img src="QR-<%= recipe.ShortUrl.substring(7, recipe.ShortUrl.length()) %>-CODE" width="42" height="42" />
		<% } %> </a>
							
						 </td>
						</tr>
						
						
					</table>
				
				
				</td>
			</tr>
		</table>
	</td>
</tr>


</table>
<%
		}	
	

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

<% } %>
<a style="float:right" TARGET="_blank" href="http://test.eaternityrechner.appspot.com/view.jsp?ids=93UJI,93UNM" title="menu_view" class="whatever hiddenOnPage" id="getPdf">Dieses Menu als PDF herunterladen.</a>

<%
}
//if everythingFine = false
else { %>
	Es gibt keine Menus zum Anzeigen. Entschuldigen Sie den Umstand. Uns ist hier ein Fehler unterlaufen.

<% } %>




</div>

<div id="footer-bottom">
	<img class="logo-karotte" src="karotte.png" alt="karotte"  />
	Eaternity
	<a href="mailto:Eaternity%20Info%20%3Cinfo@eaternity.ch%3E" >info@eaternity.ch</a>
	<a TARGET="_blank" href="http://www.eaternity.ch">www.eaternity.ch</a>
	
</div>

</div>

<div class="login">
* CO₂-Äquivalente geben das Treibhauspotential aller freigekommenen Klimagasen umgerechnet in CO₂ an. Die Eaternity Werte berücksichtigen den gesamten Produktionsweg von der Herstellung einer Zutat bis zum Produktverkauf.

</div>

</body>


</HTML>



<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.google.appengine.api.users.User" %>
<%@ page import="com.google.appengine.api.users.UserService" %>
<%@ page import="com.google.appengine.api.users.UserServiceFactory" %>

<%@ page import="ch.eaternity.server.DAO" %>
<%@ page import="ch.eaternity.shared.Recipe" %>
<%@ page import="ch.eaternity.shared.Ingredient" %>
<%@ page import="ch.eaternity.shared.Converter" %>

<%@ page import="java.util.List" %>
<%@ page import="java.util.Calendar" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.text.DecimalFormat" %>
<%@ page import="java.util.Collections" %>


<%@ page import="java.net.MalformedURLException" %>
<%@ page import="java.net.URL" %>
<%@ page import="java.io.BufferedReader" %>
<%@ page import="java.io.InputStreamReader" %>
<%@ page import="java.io.IOException" %>
<%@ page import="java.util.Date" %>


<html>
<head>

<meta HTTP-EQUIV="Content-Type" CONTENT="text/html; charset=UTF-8">
<title>Menü Klima-Bilanz Zertifikat</title>


<%
	// Hole Rezepte die zum Benutzer gehören

		String BASEURL = request.getRequestURL().toString();


	    UserService userService = UserServiceFactory.getUserService();
	    User user = userService.getCurrentUser();
		
		String tempIds = request.getParameter("ids");
		String permanentId = request.getParameter("pid");
		String pdf = request.getParameter("pdf");
		
		Boolean doPdf = false;
		if(pdf != null){
	doPdf = true;
		}
		Boolean DoItAll = true;
		
		String thresholdString = request.getParameter("median");
		Integer threshold = 1550;
		if(thresholdString != null){
	threshold = Integer.valueOf(thresholdString);
		} 

		Double third = (double)threshold / 3;
		Double half = (double)threshold / 2;
		Double twoFifth = (double)threshold / 5 * 2;
		
		Double climateFriendlyValue = twoFifth;
		
		
		DAO dao = new DAO();
		
		List<Recipe> adminRecipes = new ArrayList<Recipe>();
		List<Recipe> rezeptePersonal = new ArrayList<Recipe>();
		List<Recipe> kitchenRecipes = new ArrayList<Recipe>();
		
		if (user != null && permanentId == null) {
	rezeptePersonal = dao.getUserRecipes(user);
	kitchenRecipes = dao.getKitchenRecipes(user);
	
	adminRecipes = dao.adminGetRecipe(user);
	
	// remove double entries for admin
	if(rezeptePersonal != null){
		for(Recipe recipe: rezeptePersonal){
	int removeIndex = -1;
	for(Recipe rezept2:adminRecipes){
		if(rezept2.getId().equals(recipe.getId())){
			removeIndex = adminRecipes.indexOf(rezept2);
		}
	}
	if(removeIndex != -1){
		adminRecipes.remove(removeIndex);
	}
		}
	}
	
		} else {
		 if(tempIds != null){
	rezeptePersonal = dao.getRecipeByIds(tempIds,true);
		 } else {
	 if(permanentId != null){
		rezeptePersonal = dao.getRecipeByIds(permanentId,false);
		DoItAll = false;
	 } 
		 }
		}
		
		

		
		DecimalFormat formatter = new DecimalFormat("##");
		
		Double MaxValueRezept = 0.0;
		Double MinValueRezept = 10000000.0;
		Double average = 0.0;
		Double median = 0.0;
		Integer counter = 0;
		
		//Calendar rightNow = Calendar.getInstance();
		//Integer iTimeStamp = rightNow.get(Calendar.WEEK_OF_YEAR);
		
		// this should be disfunctional by now
		Date date = new Date();
        long iTimeStamp = (long) (date.getTime() * .00003);
		
		if(rezeptePersonal.size() != 0){
	ArrayList<Double> values = new ArrayList<Double>();
	
	//  go over the Recipes in the Workspace
	for(Recipe recipe: rezeptePersonal){
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
	average = average /counter;
	
		
	Collections.sort(values);
		  
		    if (values.size() % 2 == 1)
	median = values.get((values.size()+1)/2-1);
		    else
		    {
	double lower = values.get(values.size()/2-1);
	double upper = values.get(values.size()/2);
		 
	median = (lower + upper) / 2.0;
		    }	    
		}
%>



<style type="text/css">
<%if(!doPdf){%>
@import url(http://fonts.googleapis.com/css?family=Open+Sans:300,400,600,700,800);

@font-face {
  font-family: 'Open Sans';
  font-style: normal;
  font-weight: 300;
  src: local('Open Sans Light'), local('OpenSans-Light'), url('opensans300.woff') format('woff');
}
<%}%>

@page {
  size: A4;
  margin: 40pt 20pt 70pt 40pt;
   padding: 0pt 0pt 0pt 0pt;
	prince-image-resolution: 300dpi;

/*    @bottom-right { 
        content: counter(page) "/" counter(pages);
		font: 9pt 'Open Sans', Verdana, sans-serif; font-weight: 300;
		 padding-right: 20pt;
		 padding-top: 5pt;
    }*/
    @bottom { 
      font: 9pt 'Open Sans', Verdana, sans-serif; font-weight: 300;
      }
    @bottom-left { 
      content: flow(footer);
     
      }
      
    @top {
     	content: flow(header);
    }
	
	

}

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

.cover-all {
/*	margin-bottom:-22em;*/
	position: relative;
	margin-left:1px;
	z-index:0;
	max-height:46em; /* this fits with a title of 2 lines*/
/*	height:48.5em;*/
/*	margin-top:0.5em;*/
}

.cover-up {
	margin-top:5em;
	background:white;
/*	border-color: black; */
/*
	background-image: url(light-gray.png);
	background: light-gray.png;*/
	bottom:20em;	position:absolute;
	-moz-border-radius-bottomright:6px;
	-webkit-border-bottom-right-radius:6px;
	border-bottom-right-radius:6px;
	-moz-border-radius-topright:6px;
	-webkit-border-top-right-radius:6px;
	border-top-right-radius:6px;
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
margin: auto;
/*	margin: 30pt 32pt 30pt 12pt;*/
	margin-top: 30pt;
	margin-bottom: 30pt;
}

.page-break {
    page-break-after: always;
}

#header-right {
	float:right;
	margin-bottom: -10px;
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
  margin-bottom: 4em;

background:white;
opacity: 0.95;

}

.label-table {
width: 41em;
margin:0 auto; 
	bottom:0px;	position:absolute;
}

.rounded {
/* padding:10px; */
border-style: solid;
border-width: 1px;
border-color: black; 
-moz-border-radius: 6px;
border-radius: 6px;
/* border-spacing: 0; */

		margin-top:15em;
}


.rounded tr:first-child td:first-child {
-moz-border-radius-topleft:6px;
-webkit-border-top-left-radius:6px;
border-top-left-radius:6px;
}

.rounded tr:first-child td:last-child {
-moz-border-radius-topright:6px;
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
	margin-top: -24pt;
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
/* background-image: url(green.png);
background: green.png; */
font-size: 10pt;
font-weight: 700;
text-align: right;
padding: 2pt;
padding-right: 4pt;
width: 100pt;
}

.gray {
page-break-after: avoid;
color: white;
background-repeat: repeat-x;
/* background-image: url(light-gray.png);
background: light-gray.png; */
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
padding-top: 0.5em;
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

@media screen {
  html { background: gray; }
  table {
  	font-size: 12pt;
  }
  
  body {
  	margin-top: 2em;
  	font-size: 12pt;
  }

  .website-content { 
    width: 704px;
    background: white;
    margin: 1em auto;
    padding: 3em 3em 3em 3em;
  }
  .login {
  	color: #383838;
  	visibility: visible;
  	display: block;
  	width: 720px;
  	
    margin: auto;
    padding: 0em 1em 5em 3em;
    text-align: right;
    font-size: 9pt;
    font-weight: 400;
    
  }

.label-table {
width: 41em;
margin:0 auto; 
	bottom:0px;	position:relative;
}

.cover-all {

	margin-left:-67px;
	margin-right:-77px;
	width:800px;
}

.cover-up{
	position:relative;
	bottom:0px;
	margin-top:0em;
}
  .id {
  visibility: visible;
  }

  h1 {
  margin-top: 1em;
width:10em;
/*   display:none; */
  }
  
  h2 {
  font-size: 12pt;
  }
  h3 {
  font-size: 12pt;
  margin-bottom: 0em;
  clear: both;
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
}

.table {
border-color: #929292;
border-style: none;
padding-top: 2em;
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
padding: 1em 4em 0.5em 3em;
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

}

</style>

<script type="text/javascript">

function initThis(){
idsToAdd = new Array(); 
baseUrl = getBaseURL();


<%for(Recipe recipe: rezeptePersonal){
	long compute = recipe.getId() * iTimeStamp;
	String code = Converter.toString(compute,34); 
	String clear = Converter.toString(recipe.getId(),34);%>
idsToAdd['<%=clear%>'] = true<%}%>

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



<div class="website-content">

<div id="header-right">
	<img class="logo-klein" src="logo-eaternity-huge_04-11-2010.png" alt="logo-eaternity-huge_04-11-2010" />
</div>


<!--div id="kopf-logo" class="kopf">
	<img class="logo" src="logo-eaternity-huge_04-11-2010.png" alt="logo-eaternity-huge_04-11-2010" />
</div-->


<div class="page-break" id="footer-left">
	<img class="logo-karotte" src="karotte.png" alt="karotte"  />
	Eaternity
	<a href="mailto:Eaternity%20Info%20%3Cinfo@eaternity.ch%3E" >info@eaternity.ch</a>
	<a TARGET="_blank" href="http://www.eaternity.ch">www.eaternity.ch</a>
</div>



<div class="content">

<%
	if(true) {
%>


<%
	boolean doIt = false;
if(rezeptePersonal.size() != 0){
doIt = true;
}

if(doIt){
%>


<%
	}


}






Boolean doIt = false;
if(rezeptePersonal.size() != 0){
	for(Recipe recipe: rezeptePersonal){
	if(recipe.getCO2Value() > 0){
	doIt = true;
	}
	}
}
if(doIt){



for(Recipe recipe: rezeptePersonal){

	long compute = recipe.getId() * iTimeStamp;
	String code = Converter.toString(compute,34);
	
	String clear = Converter.toString(recipe.getId(),34);

	recipe.setCO2Value();
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
			<table style="font-weight: 300;font-size: 14pt; margin-top:-4em;">
						<!-- label of table -->
			<tr width="660">
			<td height="140">
				<h3 style="font-size: 28pt; padding-bottom:0em; display:block; margin-right:5em;"><%=recipe.getSymbol()%></h3>
				<p style="display:block;clear:both">	<%=recipe.getSubTitle()%> – <%=formatted%> g CO<sub>2</sub>* pro Person.</p>
				
				<%
									if(recipe.image != null){
								%>
					
					<%
											if(!doPdf){
										%>
						<img class="cover-all" src="<%=recipe.image.getServingUrl()%>=s800" />
					<%
						} else {
					%>
						<img class="cover-all" src="COVER-<%=recipe.image.getServingUrl()%>-IMAGE" />
					<%
						}
					%>
					 
					
					<%
					 											}
					 										%>
			</td>
			</tr>
			
						<!-- content of table -->
			<tr>
			<td>
				<table cellspacing="0" cellpadding="0" class="table cover-up" >
				<!--  <%=Integer.toString(rezeptePersonal.size())%>  -->
							<tr>
							<td>


							<span style="color:gray;">Für <%=persons%> Personen:</span>
							<ul class="zutat">

							<%
								for(Ingredient ingredient: recipe.ingredients){
							%>

							<li><%=ingredient.getWeight()%> g <span class="ix"><%= ingredient.getName() %></span> </li>

							<%
							}
							%>

							</ul>

							    <!--<ul class="tips">
							      <li>eventuell mit 500g Schinken aufbessern – Anfrage zur besseren Kommunikation.</li>
							      <li>Vorschlag: Menü Woche 12 (zweite Dezember Woche)</li>
							    </ul> -->
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
	<a href="<%= BASEURL %>?pid=<%= clear %>">
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
	}
}









if(rezeptePersonal.size() != 0){

%>
<!-- this is different now...-->
<a style="float:right" TARGET="_blank" href="http://test.eaternityrechner.appspot.com/view.jsp?ids=93UJI,93UNM" title="menu_view" class="whatever hiddenOnPage" id="getPdf">Dieses Menu als PDF herunterladen.</a>

<%
} else {
%>
<h1>Urkunde</h1>
für Institutionen die sich verpflichten vermehrt vegetarisches anzubieten und damit 10 % Ihren Ernährungsfussabdruck einsparen (Berechnungen schicke ich dir)
XY setzt ein deutliches Zeichen zum Schutz unseres Planeten. Vom … bis … stehen hier überdurchschnittlich viele vegetarische Mahlzeiten im Angebot. So reduziert XY seine ernährungsbedingten Umweltbelastungen in diesem Zeitraum um … Prozent.
<%
} 
%>




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


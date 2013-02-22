<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%@ page import="ch.eaternity.shared.Recipe" %>
<%@ page import="ch.eaternity.shared.IngredientSpecification" %>
<%@ page import="ch.eaternity.shared.Converter" %>
<%@ page import="ch.eaternity.shared.CatRyzer" %>
<%@ page import="ch.eaternity.shared.Util" %>
<%@ page import="ch.eaternity.shared.Quantity.Weight" %>

<%@ page import="ch.eaternity.server.jsp.StaticDataLoader" %>
<%@ page import="ch.eaternity.server.jsp.StaticProperties" %>
<%@ page import="ch.eaternity.server.jsp.StaticTempBean" %>

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
<title>Menü Klima-Bilanz Zertifikat</title>

<link rel="stylesheet" type="text/css" href="reports.css">

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

	properties.weightUnit = Weight.GRAM;
	properties.co2Unit = Weight.GRAM;		
			
	properties.formatter = new DecimalFormat("##");
	properties.formatter.setRoundingMode(RoundingMode.FLOOR);
	properties.co2_formatter = new DecimalFormat("##");
	properties.cost_formatter = new DecimalFormat("##");
	properties.weight_formatter = new DecimalFormat("##");
	properties.distance_formatter = new DecimalFormat("##");
	properties.dateFormatter = new SimpleDateFormat("dd.MMMM yyyy");
	
	properties.co2BarLength = 200;
	properties.barOffset = 70;
	
	// standard values for request if not set
	properties.doPdf = false;
	properties.threshold = 1550;
	properties.extra = 0;
	properties.persons = 4;
	
	properties.valueType = StaticProperties.ValueType.COMPACT;
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
	%>


<style type="text/css">
<% if(!properties.doPdf){ %>
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
<%
//Avoid displaying anything if someting is wrong.
if (!vars.everythingFine){
	%>
		Wrong Inputs. See Log for Details.<br /><br />
		<%= vars.errorMessage %>
	<%
	
}
else { %>

<div class="website-content" style="width: 704px;">

<div id="header-right">
	<img class="logo-klein" src="logo-eaternity-huge_04-11-2010.png" alt="logo-eaternity-huge_04-11-2010" />
</div>


<div class="content">

<%
for(Recipe recipe: vars.recipes){

	
	%>	

<!--  ----------------------------------- Recipe Details --------------------------------------- -->
	
<!-- Titel and Image -->
	<table style="font-weight: 300;font-size: 14pt; margin-top:0em;">
		<tr width="660">
			<td height="140">
				<h3 style="font-size: 28pt; padding-bottom:0em; display:block; margin-right:5em;"><%= recipe.getSymbol() %></h3>
				<p style="display:block;clear:both">	<%= recipe.getSubTitle() %> – <%= properties.co2_formatter.format( recipe.getCO2Value() ) %> g CO<sub>2</sub>* pro Person.</p>
				
				<%
				if(recipe.image != null){
					%>
					
					<% if(!properties.doPdf){%>
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
							<span style="color:gray;">Für <%= properties.persons %> Personen:</span>
							<ul class="zutat">
								<%for(IngredientSpecification ingredient: recipe.Zutaten){ %>
									<li><%= ingredient.getMengeGramm()/recipe.getPersons()*properties.persons %> g <span class="ix"><%= ingredient.getName() %></span> </li>
								<% } %>
		
							</ul>
						</td>
						<td style="vertical-align:top;"><br>
							<p style="display:block;clear:both">
							<% 
							if (recipe.getCookInstruction() != null)
								out.print(recipe.getCookInstruction()); %>
							</p>
						</td>
					</tr>
				</table>
			</td>
		</tr>
	</table>

<!-- --------------------------- Certificate Box -------------------------------------- -->

<%
temp.recipes.clear();
temp.co2Value = recipe.getCO2ValueExpanded();
temp.co2Values.addAll(Util.getCO2ValuesRecipes(vars.recipes));
%>

<jsp:include page="/jsp_snippets/snippet_certificate.jsp" />
	
	
<a TARGET="_blank" href="http://test.eaternityrechner.appspot.com/view.jsp?ids=93UJI,93UNM" title="menu_view">Dieses Menu als PDF herunterladen.</a>


	<% } //for loop over recipes %>

</div>

<% } // if everythingFine %>

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



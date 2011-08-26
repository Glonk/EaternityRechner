<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.google.appengine.api.users.User" %>
<%@ page import="com.google.appengine.api.users.UserService" %>
<%@ page import="com.google.appengine.api.users.UserServiceFactory" %>

<%@ page import="ch.eaternity.server.DAO" %>
<%@ page import="ch.eaternity.shared.Recipe" %>
<%@ page import="ch.eaternity.shared.IngredientSpecification" %>
<%@ page import="ch.eaternity.shared.Converter" %>

<%@ page import="java.util.List" %>
<%@ page import="java.util.Calendar" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.text.DecimalFormat" %>
<%@ page import="java.util.Collections" %>

<html>
<head>

<meta HTTP-EQUIV="Content-Type" CONTENT="text/html; charset=UTF-8">
<title>Eaternity Menu Optimierung</title>


<%
// Hole Rezepte die zum Benutzer gehören

	    UserService userService = UserServiceFactory.getUserService();
	    User user = userService.getCurrentUser();
		
		String kitchenIds = request.getParameter("ids");
		String thresholdString = request.getParameter("median");
		Integer threshold = 1350;
		if(thresholdString != null){
			threshold = Integer.valueOf(thresholdString);
		} 

		Double third = (double)threshold / 3;
		
		
		DAO dao = new DAO();
		
		List<Recipe> adminRecipes = new ArrayList<Recipe>();
		List<Recipe> rezeptePersonal = new ArrayList<Recipe>();
		List<Recipe> kitchenRecipes = new ArrayList<Recipe>();
		
		if (user != null) {
			rezeptePersonal = dao.getYourRecipe(user);
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

	<%
		
		} else {
		 if(kitchenIds != null){
			rezeptePersonal = dao.getRecipeByIds(kitchenIds);

		 } 
		}
		
		

		
		DecimalFormat formatter = new DecimalFormat("##");
		
%>





<style type="text/css">
@import url(http://fonts.googleapis.com/css?family=Open+Sans:300,400,600,700,800);


@page {
  size: A4;
  margin: 40pt 40pt 70pt 50pt;
   padding: 0pt 0pt 0pt 0pt;
	prince-image-resolution: 300dpi;

    @bottom-right { 
        content: counter(page) "/" counter(pages);
		font: 9pt 'Open Sans',  sans-serif; font-weight: 300;
		 padding-right: 20pt;
		 padding-top: 5pt;
    }
    @bottom { 
      font: 9pt 'Open Sans',  sans-serif; font-weight: 300;
      }
    @bottom-left { 
      content: flow(footer,start);
     
      }
      
    @top {
     	content: flow(header,start);
    }
	

}

	#footer-bottom {
	display: none;
	}

body { 
	font-family: 'Open Sans',  sans-serif; 
	font-weight: 300;
    font-size: 11pt;
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
    margin-bottom: -1em;
/*     display: inline; */
}

.content {
/*     page-break-after: always; */
	margin: 30pt 40pt 30pt 20pt;
}

.page-break {
    page-break-after: always;
}

#header-right {
	float:right;
	flow: static(header);
}

table {
 font-weight: 300;
  font-size: 11pt;
  width: 35em;
  padding-bottom: 1em;
  page-break-inside: avoid;
}

.kopf { 
	float: right;
	text-align: right;
	font-size: 10pt;
	font-weight: 300;
	font-size: 13px;
	margin-top: -24pt;
	margin-right: -14pt;
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
	height:52pt;
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
margin-top: -1.5em;
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
height: 7pt;
padding: 1pt;
padding-bottom: 4pt;
padding-top: 4pt;

}

.table-space {
	width: 10pt;
}

.green {
page-break-after: avoid;
color: white;
background-repeat: repeat-x;
background-image: url(green.png);
background: green.png;
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
background-image: url(light-gray.png);
background: light-gray.png;
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

/* color:gray; */
}

.alternate {
background-color: #FBF9F8;
}

.amount {
float: right;
padding-right: 4pt;
margin-top: -0.5em;
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
 
}

.table {
border-color: #929292;
border-style: none;
padding-top: 1em;
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
}

.bottom-border{
	border-bottom-color: #929292;
	border-bottom-style: solid;
	border-bottom-width: 1px;
	vertical-align: top;

}

</style>

</head>

<body>

<div class="website-content">
<div id="header-right">
	<img class="logo-klein" src="logo-eaternity-huge_04-11-2010.png" alt="logo-eaternity-huge_04-11-2010" />
</div>



<div id="kopf-logo" class="kopf">
	<img class="logo" src="logo-eaternity-huge_04-11-2010.png" alt="logo-eaternity-huge_04-11-2010" />

	<ul>
	<li>Grabenwies 18</li>
	<li>CH-8057 Zürich</li>
	<li><a href="mailto:info@eaternity.ch" >info@eaternity.ch</a></li>
	<li><a href="www.eaternity.ch">www.eaternity.ch</a></li>
	</ul>

</div>

<h1>Menu Optimierung</h1>


<!--a href="http://next.eaternityrechner.appspot.com/view.jsp?ids=93UJI,93UNM" title="menu_view" class="whatever">Click me for a PDF</a-->


<div id="footer-left">
	<img class="logo-karotte" src="karotte.jpg" alt="karotte"  />
	Eaternity
	<a href="mailto:info@eaternity.ch" >info@eaternity.ch</a>
	<a href="www.eaternity.ch">www.eaternity.ch</a>
</div>






<div class="content">


<!-- Overview -->

<%

Calendar rightNow = Calendar.getInstance();
Integer date = rightNow.get(Calendar.WEEK_OF_YEAR);


boolean doIt = false;
if(rezeptePersonal.size() != 0){
	for(Recipe recipe: rezeptePersonal){
		//if(recipe.getCO2Value() < third){
			doIt = true;
		//}
	}
}
if(doIt){
%>

<table cellspacing="0" cellpadding="0" class="table" >


<tr>
<td></td>
<td class="gray left-border"></td>
<td class="gray co2label"><span>g CO<sub>2</sub>*</span></td>
</tr>

<tr>
<td class="table-header bottom-border">Menu</td>
<td class="left-border"></td>
</tr>

	
<!--  <%= Integer.toString(rezeptePersonal.size()) %>  -->
<%
		ArrayList<Double> values = new ArrayList<Double>();
		Double MaxValueRezept = 0.0;
		Double MinValueRezept = 10000000.0;
		Double average = 0.0;
		Double median = 0.0;
		Integer counter = 0;
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

for(Recipe recipe: rezeptePersonal){




long compute = recipe.getId() * date;
String code = Converter.toString(compute,34);

			//recipe.setCO2Value();
//			if(recipe.getCO2Value() < third){
			
			String length = formatter.format(recipe.getCO2Value()/MaxValueRezept*200);
			String formatted = formatter.format( recipe.getCO2Value() );
			String persons = Long.toString(recipe.getPersons());
			%>
			
			
			
			<tr <%
			int order = rezeptePersonal.indexOf(recipe) % 2; 
			if(order == 1) { %>
			class="alternate"
			<% }%> > 
			<td class="menu-name">
			<!--img class="smile" src="smiley8.png" alt="smiley" />
			<img class="smile" src="smiley8.png" alt="smiley" /-->
			<%= recipe.getSymbol() %> <!-- div class="amount"><%= formatted %> g CO<sub>2</sub>* total</div -->
			</td>
			<td class="left-border"><img class="bar" src="gray.png" alt="gray" width="<%= length %>" /></td>
			<td class="co2value" ><%= formatted %></td>
			</tr>


			<%
//		}	

}

%>


</table>

<ul class="page-break">
<li>Die Menus haben einen Durchschnitt von: <%= formatter.format(average) %> g CO<sub>2</sub>* pro Person.</li>
<li>Die Menus haben einen Median von: <%= formatter.format(median) %> g CO<sub>2</sub>* pro Person.</li>
</ul>



<%
}
%>



<!-- Details follow -->


<%


doIt = false;
if(rezeptePersonal.size() != 0){
	for(Recipe recipe: rezeptePersonal){
		if(recipe.getCO2Value() < third){
			doIt = true;
		}
	}
}
if(doIt){
%>

<table cellspacing="0" cellpadding="0" class="table" >


<tr>
<td class="table-header">Grossartig</td>
<td></td>
</tr>
	
<tr>
<td><p>Diese Rezepte sind unsere Besten. Sie haben unter <%= formatter.format( third ) %> g CO<sub>2</sub>* pro Person. Es sind am Rezept keine weiteren Verbesserungen notwendig. Im Einzelfall kann es noch Unklarheiten geben.</p></td>
<td></td>
</tr>

<tr>
<td></td>
<td class="green left-border">Potential in g CO<sub>2</sub>*</td>
</tr>
	
<!--  <%= Integer.toString(rezeptePersonal.size()) %>  -->
<%


for(Recipe recipe: rezeptePersonal){

long compute = recipe.getId() * date;
String code = Converter.toString(compute,34);

			recipe.setCO2Value();
			if(recipe.getCO2Value() < third){
			
			
			String formatted = formatter.format( recipe.getCO2Value() );
			String persons = Long.toString(recipe.getPersons());
			
			
			%>
			
			
			<tr>
			<td></td>
			<td class="left-border"><br></td>
			</tr>
			
			<tr>
			<td class="bottom-border">
			<img class="smile" src="smiley8.png" alt="smiley" />
			<img class="smile" src="smiley8.png" alt="smiley" />
			<h3><%= recipe.getSymbol() %></h3> <span class="id">(<%= code %>)</span>
			</td>
			<td class="left-border"></td>
			</tr>

			<tr>
			<td><div class="amount"><%= formatted %> g CO<sub>2</sub>* total</div></td>
			<td class="left-border"><img class="bar" src="gray.png" alt="gray" width="140" /></td>
			</tr>
			
			<tr>
			<td>
			
			<span class="subTitle"><%= recipe.getSubTitle() %></span>
			
			<span style="color:gray;">Zutaten für <%= persons %> Personen:</span>
			<ul class="zutat">
			
			<%	
			for(IngredientSpecification ingredient: recipe.Zutaten){
			
			%>
			
			<li><%= ingredient.getMengeGramm() %> g <span class="ix"><%= ingredient.getName() %></span> </li>
			
			<%
			}
			%>
						
			</ul>
			
			    <!--<ul class="tips">
			      <li>eventuell mit 500g Schinken aufbessern – Anfrage zur besseren Kommunikation.</li>
			      <li>Vorschlag: Menü Woche 12 (zweite Dezember Woche)</li>
			    </ul> -->
			</td>
			<td class="left-border"><br></td>
			</tr>
			<%
		}

%>
<tr>
<td></td>
<td class="left-border"><br></td>
</tr>
<%		

}

%>

<!--
	<tr>
	<td></td>
	<td class="left-border"><br></td>
	</tr>
	
	<tr>
	<td class="bottom-border">
	<img class="smile" src="smiley8.png" alt="smiley" />
	<img class="smile" src="smiley8.png" alt="smiley" />
	<h3>Randenknöpfli</h3> <div class="amount">323 g CO<sub>2</sub>* total</div>
	</td>
	<td class="left-border"><img class="bar" src="gray.png" alt="gray" width="140" /></td>
	</tr>
	
	<tr>
	<td>
	<ul class="zutat">
	<li><span class="ix">Zutat</span></li>
	</ul>
	
	    <ul class="tips">
	      <li>eventuell mit 500g Schinken aufbessern – Anfrage zur besseren Kommunikation.</li>
	      <li>Vorschlag: Menü Woche 12 (zweite Dezember Woche)</li>
	    </ul>
	</td>
	<td class="left-border"><br></td>
	</tr>
	
	<tr>
	<td class="suggest">
		500 g Schinken extra
	</td>
	<td class="left-border">
	<img class="bar" src="red.png" alt="red" width="80" />
	<div class="amount">+234</div>
	</td>
	</tr>
		
	<tr>
	<td></td>
	<td class="left-border"><br></td>
	</tr>
	
	
	<tr>
	<td></td>
	<td class="left-border"><br></td>
	</tr>
	
	<tr>
	<td class="bottom-border">
	<img class="smile" src="smiley8.png" alt="smiley" />
	<img class="smile" src="smiley8.png" alt="smiley" />
	<h3>Randenknöpfli</h3> <div class="amount">323 g CO<sub>2</sub>* total</div>
	</td>
	<td class="left-border"><img class="bar" src="gray.png" alt="gray" width="140" /></td>
	</tr>
	
	<tr>
	<td>
	    <ul class="tips">
	      <li>eventuell mit 500g Schinken aufbessern – Anfrage zur besseren Kommunikation.</li>
	      <li>Vorschlag: Menü Woche 12 (zweite Dezember Woche)</li>
	    </ul>
	</td>
	<td class="left-border"><br></td>
	</tr>
	
	<tr>
	<td class="suggest">
		500 g Schinken weniger
	</td>
	<td class="left-border">
	<img class="bar" src="green.png" alt="green" width="80" />
	<div class="amount">+234</div>
	</td>
	</tr>

	<tr>
	<td></td>
	<td class="left-border"><br></td>
	</tr>
	
	-->

</table>
<%
}


doIt = false;
if(rezeptePersonal.size() != 0){
	for(Recipe recipe: rezeptePersonal){
	if(recipe.getCO2Value() >= third && recipe.getCO2Value() < (2*third)){
	doIt = true;
	}
	}
}
if(doIt){
%>


<table cellspacing="0" cellpadding="0" class="table" >


<tr>
<td class="table-header">Gut</td>
<td></td>
</tr>
	
<tr>
<td><p>Diese Rezepte sind mit unter <%= formatter.format( 2*third ) %> g CO<sub>2</sub>* bereits klimafreundlich. Am Rezept sind teilweise weitere Verbesserungen möglich. Sind einige der Vorschläge pro Rezept umsetzbar, wäre dies natürlich grossartig.</p></td>
<td></td>
</tr>

<tr>
<td></td>
<td class="green left-border">Potential in g CO<sub>2</sub>*</td>
</tr>
	
<!--  <%= Integer.toString(rezeptePersonal.size()) %>  -->
<%


for(Recipe recipe: rezeptePersonal){

long compute = recipe.getId() * date;
String code = Converter.toString(compute,34);

			recipe.setCO2Value();
			if(recipe.getCO2Value() >= third && recipe.getCO2Value() < (2*third)){

			String formatted = formatter.format( recipe.getCO2Value() );
			String persons = Long.toString(recipe.getPersons());
			%>
			
			
			<tr>
			<td></td>
			<td class="left-border"><br></td>
			</tr>
			
			<tr>
			<td class="bottom-border">
			<img class="smile" src="smiley8.png" alt="smiley" />
			<h3><%= recipe.getSymbol() %></h3> <span class="id">(<%= code %>)</span>
			</td>
			<td class="left-border"></td>
			</tr>

			<tr>
			<td><div class="amount"><%= formatted %> g CO<sub>2</sub>* total</div></td>
			<td class="left-border"><img class="bar" src="gray.png" alt="gray" width="140" /></td>
			</tr>
			
			<tr>
			<td>
			
			<span class="subTitle"><%= recipe.getSubTitle() %></span>
			
			<span style="color:gray;">Zutaten für <%= persons %> Personen:</span>
			<ul class="zutat">
			
			<%	
			for(IngredientSpecification ingredient: recipe.Zutaten){
			
			%>
			
			<li><%= ingredient.getMengeGramm() %> g <span class="ix"><%= ingredient.getName() %></span> </li>
			
			<%
			}
			%>
						
			</ul>
			
			    <!--<ul class="tips">
			      <li>eventuell mit 500g Schinken aufbessern – Anfrage zur besseren Kommunikation.</li>
			      <li>Vorschlag: Menü Woche 12 (zweite Dezember Woche)</li>
			    </ul> -->
			</td>
			<td class="left-border"><br></td>
			</tr>
			<%
		}
%>
<tr>
<td></td>
<td class="left-border"><br></td>
</tr>
<%		

}

%>


</table>

<%
}


doIt = false;
if(rezeptePersonal.size() != 0){
	for(Recipe recipe: rezeptePersonal){
	if(recipe.getCO2Value() > (2*third)){
	doIt = true;
	}
	}
}
if(doIt){
%>

<table cellspacing="0" cellpadding="0" class="table" >


<tr>
<td class="table-header">Über ⅔ Richtwert</td>
<td></td>
</tr>
	
<tr>
<td><p>An diesen Rezepten lässt sich entweder noch etwas verbessern – oder man verwendet ein neues alternatives Rezept. Diese Rezepte haben über <%= formatter.format( 2*third ) %> g CO<sub>2</sub>*.</p></td>
<td></td>
</tr>

<tr>
<td></td>
<td class="green left-border">Potential in g CO<sub>2</sub>*</td>
</tr>
	
<!--  <%= Integer.toString(rezeptePersonal.size()) %>  -->
<%


for(Recipe recipe: rezeptePersonal){

long compute = recipe.getId() * date;
String code = Converter.toString(compute,34);

			recipe.setCO2Value();
			if(recipe.getCO2Value() > (2*third)){
			

			String formatted = formatter.format( recipe.getCO2Value() );
			String persons = Long.toString(recipe.getPersons());
			%>
			
			
			<tr>
			<td></td>
			<td class="left-border"><br></td>
			</tr>
			
			<tr>
			<td class="bottom-border">
			<h3><%= recipe.getSymbol() %></h3> <span class="id">(<%= code %>)</span>
			</td>
			<td class="left-border"></td>
			</tr>

			<tr>
			<td><div class="amount"><%= formatted %> g CO<sub>2</sub>* total</div></td>
			<td class="left-border"><img class="bar" src="gray.png" alt="gray" width="140" /></td>
			</tr>
			
			<tr>
			<td>
			
			<span class="subTitle"><%= recipe.getSubTitle() %></span>
			
			<span style="color:gray;">Zutaten für <%= persons %> Personen:</span>
			<ul class="zutat">
			
			<%	
			for(IngredientSpecification ingredient: recipe.Zutaten){
			
			%>
			
			<li><%= ingredient.getMengeGramm() %> g <span class="ix"><%= ingredient.getName() %></span> </li>
			
			<%
			}
			%>
						
			</ul>
			
			    <!--<ul class="tips">
			      <li>eventuell mit 500g Schinken aufbessern – Anfrage zur besseren Kommunikation.</li>
			      <li>Vorschlag: Menü Woche 12 (zweite Dezember Woche)</li>
			    </ul> -->
			</td>
			<td class="left-border"><br></td>
			</tr>
			<%
		}
%>
<tr>
<td></td>
<td class="left-border"><br></td>
</tr>
<%		

}

%>


</table>


<%
}
if(rezeptePersonal.size() != 0){

%>

<!--
<h2>Alle Zutaten</h2>

<ul id=ix></ul>
-->

<%
} else {
%>
Es gibt keine Rezepte zum Anzeigen. Melden Sie sich an, oder kontaktieren Sie uns.

<%
} 
%>

</div>

<div id="footer-bottom">
	<img class="logo-karotte" src="karotte.jpg" alt="karotte"  />
	Eaternity
	<a href="mailto:info@eaternity.ch" >info@eaternity.ch</a>
	<a href="www.eaternity.ch">www.eaternity.ch</a>
	
</div>

</div>

<div class="login">
	<%

	    if (user != null) {
	%>
	Diese Angaben sind für den Benutzer <%= user.getNickname() %>. <a href="<%= userService.createLogoutURL(request.getRequestURI()) %>">Abmelden</a>?
	<%
	    } else {
	    	if (kitchenIds == null){
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


</body>


</HTML>



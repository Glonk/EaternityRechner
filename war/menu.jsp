<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.google.appengine.api.users.User" %>
<%@ page import="com.google.appengine.api.users.UserService" %>
<%@ page import="com.google.appengine.api.users.UserServiceFactory" %>

<%@ page import="ch.eaternity.server.DAO" %>
<%@ page import="ch.eaternity.shared.Recipe" %>
<%@ page import="ch.eaternity.shared.IngredientSpecification" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.text.DecimalFormat" %>



<%
// Hole Rezepte die zum Benutzer gehören

	    UserService userService = UserServiceFactory.getUserService();
	    User user = userService.getCurrentUser();
		
		String email = request.getParameter("user");

		DAO dao = new DAO();
		
		List<Recipe> adminRecipes = new ArrayList<Recipe>();
		List<Recipe> rezeptePersonal = new ArrayList<Recipe>();
		List<Recipe> kitchenRecipes = new ArrayList<Recipe>();
		
		if (user != null) {
		rezeptePersonal = dao.getYourRecipe(user);
		kitchenRecipes = dao.getKitchenRecipes(user);
		} else {
		 if(email != null){
			rezeptePersonal = dao.getYourRecipeByName(email);
		  }
		}
		
		
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


<html>
<head>

<meta HTTP-EQUIV="Content-Type" CONTENT="text/html; charset=UTF-8">
<title>Eaternity Menu Optimierung für <%= email %></title>

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
    font-size: 12pt;
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
    font-size: 13pt;
    font-weight: 400;
}
      
h3 { 
	font-weight: 400;
    font-size: 12pt;
    padding: 0px;
    margin: 0px;
    word-wrap: normal;
    display: inline;
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
  font-size: 12pt;
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



.smile{
height: 18pt;
margin-bottom: -2pt;
margin-left: -4pt;
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


.amount {
float: right;
padding-right: 4pt;
margin-top: -1.5em;

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
  body {
  	margin-top: 2em;
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
  	width: 840px;
  	
    margin: auto;
    padding: 0em 1em 5em 3em;
    text-align: right;
    font-size: 9pt;
    font-weight: 400;
    
  }

  h1 {
  margin-top: 1em;
/*   display:none; */
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
 
}

.table {
border-color: #929292;
border-style: none;
padding-top: 1em;
border-width: 0pt;
}

.table-header {
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

<body onload="makeix(); maketoc();">

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

<div id="footer-left">
	<img class="logo-karotte" src="karotte.jpg" alt="karotte"  />
	Eaternity
	<a href="mailto:info@eaternity.ch" >info@eaternity.ch</a>
	<a href="www.eaternity.ch">www.eaternity.ch</a>
</div>






<div class="content">

<ul id=toc></ul>

<table cellspacing="0" cellpadding="0" class="table" >


<tr>
<td class="table-header">Grossartig</td>
<td></td>
</tr>
	
<tr>
<td><p>Diese Rezepte sind unsere besten Menü Plus. Sie haben unter 450 g CO2* pro Person. Es sind am Rezept keine weiteren Verbesserungen notwendig. Im Einzelfall kann es noch Unklarheiten geben.</p></td>
<td></td>
</tr>

<tr>
<td></td>
<td class="green left-border">Potential in g CO<sub>2</sub>*</td>
</tr>
	
<!--  <%= Integer.toString(rezeptePersonal.size()) %>  -->
<%

for(Recipe recipe: rezeptePersonal){

			recipe.setCO2Value();
			if(recipe.getCO2Value() < 450){
			
			DecimalFormat formatter = new DecimalFormat("##");
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
			<h3><%= recipe.getSymbol() %></h3><div class="amount"><%= formatted %> g CO<sub>2</sub>* total</div>
			</td>
			<td class="left-border"><img class="bar" src="gray.png" alt="gray" width="140" /></td>
			</tr>


			
			<tr>
			<td><span style="color:gray;">Zutaten für <%= persons %> Personen:</span>
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




<ul id=ix></ul>

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
	%>
	Sie sind nicht angemeldet.
	<a href="<%= userService.createLoginURL(request.getRequestURI()) %>">Anmelden</a>?
	<%
	    }
	%>

</div>


</body>

<script>
function getText(e)
{
    var text = "";

    for (var x = e.firstChild; x != null; x = x.nextSibling)
    {
	if (x.nodeType == x.TEXT_NODE)
	{
	    text += x.data;
	}
	else if (x.nodeType == x.ELEMENT_NODE)
	{
	    text += getText(x);
	}
    }

    return text;
}

function getElementsByClassName(oElm, strTagName, strClassName){
	var arrElements = (strTagName == "*" && oElm.all)? oElm.all : oElm.getElementsByTagName(strTagName);
	var arrReturnElements = new Array();
	strClassName = strClassName.replace(/\-/g, "\\-");
	var oRegExp = new RegExp("(^|\\s)" + strClassName + "(\\s|$)");
	var oElement;
	for(var i=0; i<arrElements.length; i++){
		oElement = arrElements[i];
		if(oRegExp.test(oElement.className)){
			arrReturnElements.push(oElement);
		}
	}
	return (arrReturnElements)
}


function maketoc()
{
    var hs = document.getElementsByTagName("h3");
    var toc = document.getElementById('toc');
    for(var i=0; i<hs.length; i++)
    {
	var text = document.createTextNode(getText(hs[i]));
        hs[i].setAttribute("id", "ch"+i);
	var link = document.createElement("a");
	link.setAttribute("href", "#ch"+i);
	link.appendChild(text);
	var li = document.createElement("li");
        li.appendChild(link);
	toc.appendChild(li); 
    }
}


function makeix()
{
  var ixcontainer = document.getElementById('ix');
  var ids = new Array();
  var ent = new Array();

// find all elements that contain index entries, go through them sequentially

  ix = getElementsByClassName(document, "*", "ix");
  for(var i=0; i<ix.length; i++)
    {
        ix[i].setAttribute("id", "ix"+i);

// store the reference in a string in an associative array

	var str = getText(ix[i]);
        if (ids[str]) {
	  ids[str] = ids[str]+",ix"+i;
        } else {
          ids[str] = "ix"+i;
        }

// check to see if the index entry is there already, if not add it

	if ((ent.join("")).indexOf(str) < 0)
          { ent.push(str) }
    }

// the ent array now contains list of index entries, sort it!

  ent.sort();

// go through list of index entries, create one li element per entry

  for (var i=0; i<ent.length; i++)
    {
       var li = document.createElement("li");

       var text = document.createTextNode(ent[i]);
       var span = document.createElement("span");
       span.appendChild(text);
       li.appendChild(span); 

       var text = document.createTextNode(" ");
       li.appendChild(text); 

       var idsa = ids[ent[i]].split(",");

       for (var j=0; j<idsa.length; j++)
         {
           var id = document.createTextNode(idsa[j]);
           var link = document.createElement("a");
           link.setAttribute("href", "#"+idsa[j]);
           link.appendChild(id);
           li.appendChild(link); 
         }
       ixcontainer.appendChild(li); 
    }
 
}
</script>

</HTML>



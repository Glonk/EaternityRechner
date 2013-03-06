<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%@ page import="ch.eaternity.shared.Recipe" %>
<%@ page import="ch.eaternity.shared.Pair" %>
<%@ page import="ch.eaternity.shared.Util" %>
<%@ page import="ch.eaternity.shared.SeasonDate" %>

<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.text.DecimalFormat" %>
<%@ page import="java.util.Calendar" %>
<%@ page import="java.util.Locale" %>


<%@ page import="ch.eaternity.server.jsp.StaticProperties" %>
<%@ page import="ch.eaternity.server.jsp.StaticDataLoader" %>
<%@ page import="ch.eaternity.server.jsp.StaticTemp" %>


<style>
	.month td {
		width:2.5em;
		border-left-color: 	#929292;
		border-left-style: solid;
		border-left-width: 1px;
		text-align:center;
		font-size: 10pt;
	}
	
	.selected {
		border-color: 	#929292;
		border-style: solid;
		border-width:1px;
	}
	.bottom-border td {
		border-bottom-color: #929292;
		border-bottom-style: solid;
		border-bottom-width: 1px;
		vertical-align: top;
		width:auto;
	}
</style>
    

  
<%
	/* uses following variables in Temp:
		- ingredients
		- personFactor
	*/
	
	StaticProperties props = (StaticProperties)request.getAttribute("props");
	StaticTemp temp = (StaticTemp)request.getAttribute("temp");
	
	List<Recipe> recipes = temp.getRecipes();
	
	Calendar cookingDateCal = Calendar.getInstance(Locale.GERMAN);
	Pair<SeasonDate,SeasonDate> seasonSpan = new Pair<SeasonDate,SeasonDate>(new SeasonDate(),new SeasonDate());
	String classString = "";
%>

<table cellspacing="0" cellpadding="0" class="table toc" >


	<!-- top row -->
	<tr>
		<td></td>
		<td class="co2label left-border"></td>
		<td class="gray left-border bottom-border" colspan="12" style="text-align:left;width:36em">Monat</td>
		<td class="left-border"></td>
		<td></td>
	</tr>
	
	
	<!-- header row -->
	<tr class="bottom-border month">
		<td class="table-header bottom-border" style="border-left-width: 0px;width:auto; text-align:left;">Name</td>
		<td class="co2label left-border bottom-border table-header" style="width:auto;"><span class="nowrap">&nbsp;<%= props.co2Unit %> CO<sub>2</sub>*&nbsp;</span></td>
		
		<td>&nbsp;Jan&nbsp;</td><td>&nbsp;Feb&nbsp;</td><td>&nbsp;Mär&nbsp;</td><td>&nbsp;Apr&nbsp;</td><td>&nbsp;Mai&nbsp;</td><td>&nbsp;Jun&nbsp;</td><td>&nbsp;Jul&nbsp;</td><td>&nbsp;Aug&nbsp;</td><td>&nbsp;Sep&nbsp;</td><td>&nbsp;Okt&nbsp;</td><td>&nbsp;Nov&nbsp;</td><td>&nbsp;Dez&nbsp;</td>
		
		<td class="left-border table-header bottom-border" style="text-align:center;width:auto;"><span class="nowrap">&nbsp;Woche&nbsp;</span></td>
	</tr>


	<!-- empty row -->
	<tr> 
	<td class="menu-name">&nbsp;
	</td>
	<td class="co2value left-border" ></td>
	
	<td class="left-border"></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td>
		
	
	<td class="left-border"></td>
	</tr>

	<!-- menu rows -->	
	<% for (Recipe recipe : recipes) { 
		seasonSpan = Util.getSeasonSpan(recipe);
		if (recipe.cookingDate != null) {
			cookingDateCal.setTime(recipe.cookingDate);
		}
		%>
		<tr> 
			<td class="menu-name">
				<% if (recipe.getCO2Value() < props.average) { %>
					<img class="smile" src="smiley8.png" alt="smiley" />
				<% } if (recipe.getCO2Value() < props.climateFriendlyValue) { %>
					<img class="smile" src="smiley8.png" alt="smiley" />
				<% } %>
				<span class="nowrap"><%= recipe.getSymbol() %></span>
			</td>
			<td class="co2value left-border right-border" style="text-align:center;font-weight: 600;" ><%= props.co2_formatter.format( recipe.getCO2Value() * props.co2Unit.conversionFactor ) %></td>
			
			<% for (int i=1; i<=12; i++) { %>
				<td<%
				classString = "";
					if (seasonSpan.first.before(new SeasonDate(i,31)) && seasonSpan.second.after(new SeasonDate(i,1))) { 
						classString += " green"; } 
					if (recipe.cookingDate != null && cookingDateCal.get(Calendar.MONTH) == (i-1)) { 
						classString += " selected"; } %>
						
				class="<%= classString %>"></td>
			<% } %>
				
			<td class="woche left-border" style="text-align:center;font-weight: 600;" >
				<% if (recipe.cookingDate != null) { 
					out.print(cookingDateCal.get( Calendar.WEEK_OF_YEAR ));
				} %>
			</td>
		</tr>
	<% } %>
	
	
	<!-- empty row -->
	<tr  > 
		<td class="menu-name">&nbsp;</td>
		<td class="co2value left-border" ></td>
		<td class="left-border"></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td>
		<td class="left-border"></td>
	</tr>
	
</table>
	
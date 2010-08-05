#! /usr/bin/env python
# the file should be a csv and saved as utf-8 that it works properly

import csv

csv.register_dialect('custom',
                     delimiter=';',
                     doublequote=True,
                     escapechar=None,
                     quotechar='"',
                     quoting=csv.QUOTE_MINIMAL,
                     skipinitialspace=False)

with open('CO2-DatenEaternityTool.csv',"rU") as ifile:
	print """<?xml version="1.0" encoding="utf-8"?>"""
	data = csv.reader(ifile, dialect='custom')
	print "<Ingredients>"
	for record in data:
		for i_pre, field_pre in enumerate(record):
			if i_pre == 0 and field_pre.isdigit():
				print "   <ingredient>"
				for i, field in enumerate(record):
					if 	i == 0:
						print  "      <id>" + field + "</id>"
					elif i == 1:
						print  "      <symbol>" + field + "</symbol>"
					elif i == 2:
						print  "      <co2eValue>" + field + "</co2eValue>"
					elif i == 3 and field != "":
						print  "      <Alternatives>"
						for alternative in field.rsplit(','):
							print "         <zutatId>" + alternative + "</zutatId>"
				
						print  "      </Alternatives>"
					elif i == 4:
						if field != "":
							print  "      <hasSeason>true</hasSeason>"
						hasSeason = field
					elif i == 5 and hasSeason=="1":
						startSeason = field 
					elif i == 6 and hasSeason=="1":
						stopSeason = field 
					elif i == 7:
						print  "      <category>" + field + "</category>"
					elif i == 8:
						print  "      <stdAmountGramm>" + field + "</stdAmountGramm>"
					elif i == 9 and field != "":
						print  "      <stdExtraction>" + field + "</stdExtraction>"
					elif i == 10 and field != "":
						print  "      <Extractions>"
						for extractions in field.rsplit(','):
							print "         <extraction>"
							print "             <symbol>" + extractions + "</symbol>"
							if hasSeason=="1":
								# it is just for the convenience to define for every country the same season...
								print "		<startSeason>"+ startSeason +"</startSeason>"
								print "		<stopSeason>"+ stopSeason +"</stopSeason>"
							
							print "		<condition>frisch</condition>"
							print "		<production>konventionell</production>"
							print "		<moTransportation>LKW</moTransportation>"
							print "		<Labels>"
							print "			<label></label>"
							print "		</Labels>"
							print "         </extraction>"
						print  "      </Extractions>"
					elif i == 11 and field != "":
						print  "      <Conditions>"
						conservationSymbols = enumerate(field.rsplit(','))
						
					elif i == 12 and field != "" and conservationSymbols != "":
						conservationFactors =  field.rsplit(',')
						for i,consItem in conservationSymbols :
							print "         <condition>"
							print "             <symbol>" + consItem + "</symbol>"
							print "             <factor>" + conservationFactors[i] + "</factor>"
							print "         </condition>"
						print  "      </Conditions>"
						
					elif i == 13 and field != "":
						print  "      <Productions>"
						productionSymbols = enumerate(field.rsplit(','))
						
					elif i == 14 and field != "" and productionSymbols != "":
						productionFactors =  field.rsplit(',')
						for i,prodItem in productionSymbols :
							print "         <production>"
							print "             <symbol>" + prodItem + "</symbol>"
							print "             <factor>" + productionFactors[i] + "</factor>"
							print "         </production>"
						print  "      </Productions>"
						
					elif i == 15 and field != "":
						print  "      <Transportation>"
						transportationSymbols = enumerate(field.rsplit(','))
						
					elif i == 16 and field != "" and transportationSymbols != "":
						transportationFactors =  field.rsplit(',')
						for i,tranItem in transportationSymbols :
							print "         <moTransportation>"
							print "             <symbol>" + tranItem + "</symbol>"
							print "             <factor>" + transportationFactors[i] + "</factor>"
							print "         </moTransportation>"
						print  "      </Transportation>"
							#  <field%s>" % i + field + "</field%s>" % i
				print "   </ingredient>"
	print "</Ingredients>"
	



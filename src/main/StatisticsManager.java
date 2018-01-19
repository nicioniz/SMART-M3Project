package main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import simulationConfiguration.SimulationConfig;
import sofia_kp.KPICore;
import sofia_kp.SIBResponse;
import sofia_kp.SSAP_sparql_response;
import utils.OntologyReference;
import utils.SIBConfiguration;

public class StatisticsManager {
	
	private static StatisticsManager istance = null;
	
	private KPICore kp = null;
	
	private StatisticsManager(){
		kp = new KPICore(SIBConfiguration.getInstance().getHost(),
				SIBConfiguration.getInstance().getPort(),
				SIBConfiguration.getInstance().getSmartSpaceName());
		
		if(!kp.join().isConfirmed())
			System.out.println("Error joining the SIB");
		else
			System.out.println("StatisticsManager: joined SIB correctly");
	}
	
	public static StatisticsManager getInstance(){	
		if(istance == null)
			istance = new StatisticsManager();

		return istance;
	}
	
	// returns the string that represents the evasion's number statistics, for each ride
	private String evasionNumberForRide(){
		
		// result expected:
		//		EVASION'S NUMBER FOR EACH RIDE OF THE SIMULATION:
		//			Day 1 - Line 32
		//				Ride XXX1 -> 32 evasions
		//				Ride XXX2 -> 36 evasions
		//				...
		//				Ride XXXn -> 35 evasions
		//			Day 1 - Line 20
		//				...
		//			...
		//				...
		//			Day 2 - Line 32
		//				...
		//			...
		//				...
		//			Day N - Line M
		//				...
		
		String result = "\nEVASION'S NUMBER FOR EACH RIDE OF THE SIMULATION:\n";
		
		// query
		String sparqlQuery =
				"select ?dd ?pp ?rp ?ln ?rd ?ts "
						+ "where { "
						+ "?ls <" + OntologyReference.RDF_TYPE + "> <" + OntologyReference.GET_ON_DATA + "> . "
						+ "?ls <" + OntologyReference.HAS_GETTING_ON_PAYING + "> ?pp . "
						+ "?ls <" + OntologyReference.HAS_GETTING_ON + "> ?rp . "
						+ "?ls <" + OntologyReference.HAS_SIMULATION_DAY + "> ?dd . "
						+ "?ls <" + OntologyReference.ON_LINE + "> ?bl . "
						+ "?bl <" + OntologyReference.HAS_NUMBER + "> ?ln . "
						+ "?ls <" + OntologyReference.ON_RIDE + "> ?rd . "
						+ "?rd <" + OntologyReference.AT_TIME + "> ?ts"
						+ " }";
		
		SSAP_sparql_response results = kp.querySPARQL(sparqlQuery).sparqlquery_results;
		
		int daysOfSimulation = SimulationConfig.getInstance().getSimulationDays();
		
		//process results
		if(results != null){
			
			Vector<Vector<String[]>> data = results.getResults();
			
			Map<String, Integer> evasionRideMap = new HashMap<String, Integer>();
			
			// days of simulation cycle
			for(int i=0; i<daysOfSimulation; i++){
				
				// line numbers cycle
				for(String ln : getLineNumbers()){
					
					// results cycle
					for(Vector<String[]> riga : data){
					
						// group by day & line number
						int day = Integer.parseInt(riga.get(0)[2]);
						String lineNumber = riga.get(3)[2];
						
						if(day == i && ln.equals(lineNumber)){
							
							// evasions update
							String ride = riga.get(4)[2].split("#")[1];
							int evasionNumber = Integer.parseInt(riga.get(2)[2]) - Integer.parseInt(riga.get(1)[2]);
							String rideMapKey = ride + "." + lineNumber + "." + day;
							
							if(evasionRideMap.get(rideMapKey) == null)
								evasionRideMap.put(rideMapKey, evasionNumber);
							else
								evasionRideMap.put(rideMapKey, evasionRideMap.get(rideMapKey) + evasionNumber);
							
						}
						
					}
				
				}
					
			}
			
			
			//composing result
			for(int i=0; i<daysOfSimulation; i++){
				for(String ln : getLineNumbers()){
					
					result += "\tDay " + (i+1) + " - Line " + ln + "\n";
					
					for(String rideMapKey : evasionRideMap.keySet()){
				
						int day = Integer.parseInt(rideMapKey.split("\\.")[2]);
						String lineNumber = rideMapKey.split("\\.")[1];
						String ride = rideMapKey.split("\\.")[0];
				
						if(lineNumber.equals(ln) && day == i)
							result += "\t     " + ride + " -> " + evasionRideMap.get(rideMapKey) + " evasions\n";
					}
				}
			}
			
			return result;
		}

		
		
		return "";
		
	}
	
	
	// Returns the string that represents the economic balance of every line, positive or negative.
	// Calculates are based on the ticket price, the number of fines emitted and the fine's price
	private String linesBalance(){
		
		// result expected:
		//		LINES BALANCE:
		// 		fixed costs:  inspector number * inspector cost/day * days
		//		              10 * 10€/day * 2 = 200€
		//
		//		   			  singleLineBalance(lineNumber);
		//
		//					  singleLineBalance(lineNumber);
		//
		//					  ...
		//
		
		String result = "\nLINES BALANCE:\n";
		result += "Fixed costs:\tinspector number * inspector cost/day * days\n            \t" +
				SimulationConfig.getInstance().getMaxInspectors() + " * " + 
				SimulationConfig.getInstance().getInspectorCost() + " €/day * " + 
				SimulationConfig.getInstance().getSimulationDays() + " days = " +
				SimulationConfig.getInstance().getMaxInspectors()*
				SimulationConfig.getInstance().getInspectorCost()*
				SimulationConfig.getInstance().getSimulationDays() + "€\n\n";
		for(String ln : getLineNumbers())
			result += "\t" + singleLineBalance(ln) + "\n\n";
		
//		result += "\n\tAlgorithm used: \n"
//				+ "\t   Positive Half in Balance: [ (fine price * number of fines) + (ticket price * people who have paid) ]\n"
//				+ "\t   Negative Half in Balane: [ inspector's cost * number of inspectors * duration (days) of the simulation ]\n"
//				+ "\t   Balance = Positive Half - Negative Half\n";
		
		return result;
	}
	
	// returns the string that represents the balance for a single line, this method is cycled into linesBalance() method
	private String singleLineBalance(String lineNumber){
		
		// result expected:
		//		Line 32:
		//			Collection from fines: 23 * 5,00€ =		+115,00€
		//			Collection from tickets: 96* 1,00€ = 	+96,00€
		//													--------
		//													+211,00€
		
		String result = "Line " + lineNumber + ":\n";
		float finePrice = SimulationConfig.getInstance().getFine();
//		int inspectorCost = SimulationConfig.getInstance().getInspectorCost();
//		int numInspector = SimulationConfig.getInstance().getMaxInspectors();
		int numFines = getNumFinesForLine(lineNumber);
//		int daysOfSimulation = SimulationConfig.getInstance().getSimulationDays();
		float ticketPrice = SimulationConfig.getInstance().getTicketPrice();
		
		result += "\t   Collection from fines: " + numFines + " * " + String.format("%.2f", finePrice) + "€ = \t+" + String.format("%.2f", (numFines*finePrice)) + "€\n" 
				+ "\t   Collection from tickets: " + getPayingPeopleForLine(lineNumber) + " * " + String.format("%.2f", ticketPrice) + "€ = \t+" + String.format("%.2f", (getPayingPeopleForLine(lineNumber)*ticketPrice)) + "€\n"
				+ "\t                                         \t\t----------------\n"
				+ "\t                                         \t\t+" + String.format("%.2f", (numFines*finePrice + getPayingPeopleForLine(lineNumber)*ticketPrice)) + "€\n";
//				+ "\t   Without inspectors:\n"
//				+ "\t   Collection from tickets: " + getPayingPeopleForLine(lineNumber) + " * " + String.format("%.2f", ticketPrice) + "€ = \t+" + String.format("%.2f", (getPayingPeopleForLine(lineNumber)*ticketPrice)) + "€\n"
//				+ "\t   NOT Collection from tickets: " + (getRealPeopleForLine(lineNumber)-getPayingPeopleForLine(lineNumber)) + " * " + String.format("%.2f", ticketPrice) + "€ = \t-" + String.format("%.2f", ((getRealPeopleForLine(lineNumber)-getPayingPeopleForLine(lineNumber))*ticketPrice)) + "€\n";

//		// positive sheet items
//		float positiveHalfBalance = finePrice*numFines + ticketPrice*getPayingPeopleForLine(lineNumber);
//		
//		//negative sheet items
//		int negativeHalfBalance = inspectorCost*numInspector*daysOfSimulation;
//		
//		// line balance
//		float balance = positiveHalfBalance - negativeHalfBalance;
//		
//		// composing result
//		String pos_neg = "Positive";
//		if(balance < 0)
//			pos_neg = "Negative";
//		
//		result += "\t   Balance: " + pos_neg + " -> " + balance + "€";


		
		
		return result;
	}

	// returns the number of paying people that got on the bus for a specified line number
	private int getPayingPeopleForLine(String lineNumber){
		
		int payingPeople = 0;
		
		// query
		String sparqlQuery = 
			"select ?pp ?ln "
				+ "where { "
				+ "?ls <" + OntologyReference.RDF_TYPE + "> <" + OntologyReference.GET_ON_DATA + "> . "
				+ "?ls <" + OntologyReference.ON_LINE + "> ?bl . "
				+ "?bl <" + OntologyReference.HAS_NUMBER + "> ?ln . "
				+ "?ls <" + OntologyReference.HAS_GETTING_ON_PAYING + "> ?pp"  
				+ " }";
		
		SSAP_sparql_response results = kp.querySPARQL(sparqlQuery).sparqlquery_results;
		
		//process results
		if(results != null){
			
			Vector<Vector<String[]>> data = results.getResults();
			
			for(Vector<String[]> riga : data){
				
				// line number's checking
				String ln = riga.get(1)[2];
				if(ln.equals(lineNumber))
					payingPeople += Integer.parseInt(riga.get(0)[2]);
				
			}
		}
		
		return payingPeople;
		
	}
	
	// returns the number of fines emitted for a specified line number
	private int getNumFinesForLine(String lineNumber){
		int numFines = 0;
		
		//query
		String sparqlQuery = 
			"select ?fi "
				+ "where { "
				+ "?ls <" + OntologyReference.RDF_TYPE + "> <" + OntologyReference.REPORT + "> . "
				+ "?ls <" + OntologyReference.HAS_FINES + "> ?fi . "
				+ "?ls <" + OntologyReference.ON_LINE + "> ?bl . "
				+ "?bl <" + OntologyReference.HAS_NUMBER + "> \"" + lineNumber + "\""
				+ " }";
		
		SSAP_sparql_response results = kp.querySPARQL(sparqlQuery).sparqlquery_results;
		
		// process results
		if(results != null){
			
			Vector<Vector<String[]>> data = results.getResults();
			
			// fine's number updating
			for(Vector<String[]> riga : data)
				numFines += Integer.parseInt(riga.get(0)[2]);
		}
		
	
		return numFines;
	}
	
	// returns the string that represents the line with most number of fines emitted
	private String maxFinesLine(){
		
		// result expected: 
		//		LINE WITH MOST NUMBER OF FINES EMITTED: Line 32 with 22 fines emitted
		
		String result = "\nLINE WITH MOST NUMBER OF FINES EMITTED: Line ";
		
		//query
		String sparqlQuery = 
				"select ?fi ?ln "
						+ "where { "
						+ "?ls <" + OntologyReference.RDF_TYPE + "> <" + OntologyReference.REPORT + "> . "
						+ "?ls <" + OntologyReference.HAS_FINES + "> ?fi . "
						+ "?ls <" + OntologyReference.ON_LINE + "> ?bl . "
						+ "?bl <" + OntologyReference.HAS_NUMBER + "> ?ln"
						+ " }";
		
		// execute query
		SIBResponse response = kp.querySPARQL(sparqlQuery);
		SSAP_sparql_response results = response.sparqlquery_results;
		
		// process results
		if(results != null){
			Vector<Vector<String[]>> data = results.getResults();
				
			// map config
			Map<String, Integer> lineNumbersFinesMap = new HashMap<String, Integer>();
			
			for(String ln : this.getLineNumbers())
				lineNumbersFinesMap.put(ln, 0);
			
			// update fines counter
			for(Vector<String[]> riga : data){
				
				String lineNumber = riga.get(1)[2];
				lineNumbersFinesMap.put(lineNumber, 
						lineNumbersFinesMap.get(lineNumber) + Integer.parseInt(riga.get(0)[2]));
			}
			
			// calculate max fines emitted
			int maxFines = 0;
			String lineNumberMaxF = ""; 
		
			for(String ln : lineNumbersFinesMap.keySet()){
				
				int noFines = lineNumbersFinesMap.get(ln);
				if(noFines >= maxFines){
					maxFines = noFines;
					lineNumberMaxF = ln;
				}
			}
			
			result += lineNumberMaxF + " with " + maxFines + " fines emitted\n";
			
		}
					
					
		return result;
	}
	
	// returns a string containing the bus stop with max real people and the segment with most paying people that get on the bus for every line in every day
	private String maxGetOnForDayAndLine(){

		// result expected: "SEGMENTS WITH MOST PEOPLE AND SEGMENT WITH MOST PAYING PEOPLE THAT GET ON THE BUS FOR EACH DAY AND LINE:"
		//					"	Day 1
		//							segmentMostGetOnFromLineAndDay(int day, int lineNumber)
		//							segmentMostGetOnFromLineAndDay(int day, int lineNumber)
		//							....
		//							segmentMostGetOnFromLineAndDay(int day, int lineNumber)
		//						Day 2
		//							segmentMostGetOnFromLineAndDay(int day, int lineNumber)
		//						    ...
		//						Day N
		//							segmentMostGetOnFromLineAndDay(int day, int lineNumber)
		//							...
		
		String result = "\nSEGMENT WITH MOST PEOPLE AND SEGMENT WITH MOST PAYING PEOPLE THAT GET ON THE BUS FOR EACH DAY AND LINE:\n";
		
		int numDaysOfSimulation = SimulationConfig.getInstance().getSimulationDays();
				
		for(int i=0; i<numDaysOfSimulation; i++){
			result += "\tDay " + (i+1) + "\n";
			
			for(String lineResult : getLineNumbers())
				result += "\t" + segmentMostGetOnFromLineAndDay(i, lineResult);
		}
		
		return result;
	}
	
	// returns the string that contains the segment with most paying people and the segment with most paying people for a specified line number and day
	private String segmentMostGetOnFromLineAndDay(int day, String lineNumber){
			
		// result expected: "Bus Stop with most people that get on the bus for line 32: 
		//						Ride:	Ride1
		//						From Stop:	Piazza Malpighi
		//						To Stop:	Marconi
		//						Max real people: 23
		//					 Bus Stop with most paying people that get on the bus for line 32:
		//						Ride: Ride2
		//						From Stop:	Piazza Minghetti
		//						To Stop:	Marconi
		//						Max paying people: 23"
		
		String result = "Bus Stop with most real people that get on the bus for line " + lineNumber +":\n" +
							"\t\tRide:\t";
		
		//query
		String sparqlQuery = 
				"select ?lacs ?locs ?lans ?lons ?rp ?pp ?rd "
						+ "where { "
						+ "?ls <" + OntologyReference.RDF_TYPE + "> <" + OntologyReference.GET_ON_DATA + "> . "
						+ "?ls <" + OntologyReference.ON_LINE + "> ?bl . "
						+ "?bl <" + OntologyReference.HAS_NUMBER + "> \"" + lineNumber + "\" . "
						+ "?ls <" + OntologyReference.HAS_GETTING_ON + "> ?rp ."
						+ "?ls <" + OntologyReference.HAS_SIMULATION_DAY + "> \"" + day + "\" . "
						+ "?ls <" + OntologyReference.FROM_CURR_STOP + "> ?cs . "
						+ "?ls <" + OntologyReference.TO_NEXT_STOP + "> ?ns . "
						+ "?cs <" + OntologyReference.HAS_LOCATION_DATA + "> ?ldcs . "
						+ "?ns <" + OntologyReference.HAS_LOCATION_DATA + "> ?ldns . "
						+ "?ldcs <" + OntologyReference.HAS_LAT + "> ?lacs . "
						+ "?ldcs <" + OntologyReference.HAS_LON + "> ?locs . "
						+ "?ldns <" + OntologyReference.HAS_LAT + "> ?lans . "
						+ "?ldns <" + OntologyReference.HAS_LON + "> ?lons . "
						+ "?ls <" + OntologyReference.HAS_GETTING_ON_PAYING + "> ?pp . "
						+ "?ls <" + OntologyReference.ON_RIDE + "> ?rd"
						+ " }";
		
		// execute query
		SIBResponse response = kp.querySPARQL(sparqlQuery);
		SSAP_sparql_response results = response.sparqlquery_results;
		
		// process results
		if(results != null){
			Vector<Vector<String[]>> data = results.getResults();
			
			String toStopLatR = "";
			String toStopLonR = "";
			String fromStopLatR = "";
			String fromStopLonR = "";
			String toStopLatP = "";
			String toStopLonP = "";
			String fromStopLatP = "";
			String fromStopLonP = "";
			String rideR = "";
			String rideP = "";
			int maxRealPeople = 0;
			int maxPayingPeople = 0;
			
			for(Vector<String[]> riga : data){
				
				// calculate segment with max people
				int realPeople = Integer.parseInt(riga.get(4)[2]);
				int payingPeople = Integer.parseInt(riga.get(5)[2]);
				
				if(realPeople >= maxRealPeople){
					fromStopLatR = riga.get(0)[2];
					fromStopLonR = riga.get(1)[2];
					toStopLatR = riga.get(2)[2];
					toStopLonR = riga.get(3)[2];
					rideR = riga.get(6)[2].split("#")[1];
					maxRealPeople = realPeople;
				}
				
				if(payingPeople >= maxPayingPeople){
					fromStopLatP = riga.get(0)[2];
					fromStopLonP = riga.get(1)[2];
					toStopLatP = riga.get(2)[2];
					toStopLonP = riga.get(3)[2];
					rideP = riga.get(6)[2].split("#")[1];
					maxPayingPeople = payingPeople;
				}
			}
						
			result += rideR + "\n"
					+ "\t\tFrom Stop:\t" + BusStopManager.getInstance().getBusStopFromLatLngString(lineNumber, fromStopLatR + "-" + fromStopLonR).getName() + "\n"
					+"\t\tTo Stop:\t" + BusStopManager.getInstance().getBusStopFromLatLngString(lineNumber, toStopLatR + "-" + toStopLonR).getName() + "\n"
					+ "\t\tMost real people " + maxRealPeople + "\n"
					+ "\tBus Stop with most paying people that get on the bus for line " + lineNumber +":\n"
					+ "\t\tRide:\t" + rideP + "\n"
					+ "\t\tFrom Stop:\t" + BusStopManager.getInstance().getBusStopFromLatLngString(lineNumber, fromStopLatP + "-" + fromStopLonP).getName() + "\n"
					+"\t\tTo Stop:\t" + BusStopManager.getInstance().getBusStopFromLatLngString(lineNumber, toStopLatP + "-" + toStopLonP).getName() + "\n"
					+ "\t\tMost paying people " + maxPayingPeople + "\n";
			
			return result;
		}
		
		return "";
	}
	
	// returns the max get on of the simulation
	private String maxGetOn(){
		
		// result expected: "MAX GET ON OF THE SIMULATION:
		//						Ride:	ride1
		//						Line:	32
		//						From Stop:	Piazza Minghetti
		//						To Stop:	Marconi
		//						At time:	12:45
		//						Number of people:			45
		//						Number of paying people:	34
		
		String result = "\nMAX GET ON OF THE SIMULATION:\n";
		
		// query
		String sparqlQuery = 
				"select ?ln ?lacs ?locs ?lans ?lons ?ts ?rp ?pp ?rd "
					+ "where { "
					+ "?ls <" + OntologyReference.RDF_TYPE + "> <" + OntologyReference.GET_ON_DATA + "> . "
					+ "?ls <" + OntologyReference.FROM_CURR_STOP + "> ?cs . "
					+ "?cs <" + OntologyReference.HAS_LOCATION_DATA + "> ?ldcs . "
					+ "?ldcs <" + OntologyReference.HAS_LAT + "> ?lacs . "
					+ "?ldcs <" + OntologyReference.HAS_LON + "> ?locs . "
					+ "?ls <" + OntologyReference.TO_NEXT_STOP + "> ?ns . "
					+ "?ns <" + OntologyReference.HAS_LOCATION_DATA + "> ?ldns . "
					+ "?ldns <" + OntologyReference.HAS_LAT + "> ?lans . "
					+ "?ldns <" + OntologyReference.HAS_LON + "> ?lons . "
					+ "?ls <" + OntologyReference.ON_RIDE + "> ?rd . "
					+ "?rd <" + OntologyReference.AT_TIME + "> ?ts . "
					+ "?ls <" + OntologyReference.ON_LINE + "> ?bl . "
					+ "?bl <" + OntologyReference.HAS_NUMBER + "> ?ln . "
					+ "?ls <" + OntologyReference.HAS_GETTING_ON + "> ?rp . "
					+ "?ls <" + OntologyReference.HAS_GETTING_ON_PAYING + "> ?pp . "
					+ "?ls <" + OntologyReference.ON_RIDE + "> ?rd"
					+ " }";
			
		SIBResponse response = kp.querySPARQL(sparqlQuery);
		SSAP_sparql_response results = response.sparqlquery_results;

		// process results
		if(results != null){
			
			Vector<Vector<String[]>> data = results.getResults();
			
			// calculate max people get on
			int maxPeople = 0;
			int realPeople = 0;
			String latFromCurrStop = "";
			String lonFromCurrStop = "";
			String latToNextStop = "";
			String lonToNextStop = "";
			String timestamp = "";
			String payingPeople = "";
			String lineNumber = "";
			String ride = "";
			
			for(Vector<String[]> riga : data){
				realPeople = Integer.parseInt(riga.get(6)[2]);
				
				if(realPeople >= maxPeople){
					maxPeople = realPeople;
					lineNumber = riga.get(0)[2];
					latFromCurrStop = riga.get(1)[2];
					lonFromCurrStop = riga.get(2)[2];
					latToNextStop = riga.get(3)[2];
					lonToNextStop = riga.get(4)[2];
					timestamp = riga.get(5)[2];
					payingPeople = riga.get(7)[2];
					ride = riga.get(8)[2].split("#")[1];
				}
			}
			
			result += "\tLine:\t" + lineNumber + "\n"
				+ "\tRide:\t" + ride + "\n"
				+ "\tFrom Stop:\t" + BusStopManager.getInstance().getBusStopFromLatLngString(lineNumber, latFromCurrStop + "-" + lonFromCurrStop).getName()  + "\n"
				+ "\tTo Stop:\t" + BusStopManager.getInstance().getBusStopFromLatLngString(lineNumber, latToNextStop + "-" + lonToNextStop).getName()  + "\n"
				+ "\tAt Time:\t" + timestamp + "\n"
				+ "\tNumber of people:\t" + maxPeople + "\n"
				+ "\tNumber of paying people:\t" + payingPeople + "\n";
			
			
			return result;
		}
		
		
		return "";
			
	}

	// returns a string containing the segment with max real people and the segment with most paying people for every line in every day
	private String maxAffluenceForDayAndLine(){

		// result expected: "SEGMENTS WITH MOST REAL PEOPLE AND SEGMENT WITH MOST PAYING PEOPLE FOR EACH DAY AND LINE:"
		//					"	Day 1
		//							segmentMostAffluenceFromLineAndDay(int day, int lineNumber)
		//							segmentMostAffluenceFromLineAndDay(int day, int lineNumber)
		//							....
		//							segmentMostAffluenceFromLineAndDay(int day, int lineNumber)
		//						Day 2
		//							segmentMostAffluenceFromLineAndDay(int day, int lineNumber)
		//						    ...
		//						Day N
		//							segmentMostAffluenceFromLineAndDay(int day, int lineNumber)
		//							...
		
		String result = "\nSEGMENT WITH MOST PEOPLE AND SEGMENT WITH MOST PAYING PEOPLE FOR EACH DAY AND LINE:\n";
		
		int numDaysOfSimulation = SimulationConfig.getInstance().getSimulationDays();
		
		
		for(int i=0; i<numDaysOfSimulation; i++){
			result += "\tDay " + (i+1) + "\n";
			
			for(String lineResult : getLineNumbers())
				result += "\t" + segmentMostAffluenceFromLineAndDay(i, lineResult);
		}
		
		return result;
	}
	
	// returns the string that contains the segment with most real people and the segment with most paying people for a specified line number and day
	private String segmentMostAffluenceFromLineAndDay(int day, String lineNumber){
		
		// result expected: "Segment with most real people for line 11:
		//						Ride: ride0
		//						From Stop:	Piazza Malpighi
		//						To Stop:	Marconi	
		//						Max real people: 23
		//					 Segment with most paying people for line 11:
		//						Ride: ride1
		//						From Stop:	Piazza Minghetti
		//						To Stop:	San Vitale
		//						Max paying people: 23"
		
		String result = "Segment with most real people for line " + lineNumber +":\n" +
							"\t\tRide:\t";
		
		//query
		String sparqlQuery = 
				"select ?lacs ?locs ?lans ?lons ?rp ?pp ?rd "
						+ "where { "
						+ "?ls <" + OntologyReference.RDF_TYPE + "> <" + OntologyReference.AFFLUEANCE + "> . "
						+ "?ls <" + OntologyReference.ON_LINE + "> ?bl . "
						+ "?bl <" + OntologyReference.HAS_NUMBER + "> \"" + lineNumber + "\" . "
						+ "?ls <" + OntologyReference.OF_REAL_PERSON + "> ?rp ."
						+ "?ls <" + OntologyReference.HAS_SIMULATION_DAY + "> \"" + day + "\" . "
						+ "?ls <" + OntologyReference.FROM_CURR_STOP + "> ?cs . "
						+ "?ls <" + OntologyReference.TO_NEXT_STOP + "> ?ns . "
						+ "?cs <" + OntologyReference.HAS_LOCATION_DATA + "> ?ldcs . "
						+ "?ns <" + OntologyReference.HAS_LOCATION_DATA + "> ?ldns . "
						+ "?ldcs <" + OntologyReference.HAS_LAT + "> ?lacs . "
						+ "?ldcs <" + OntologyReference.HAS_LON + "> ?locs . "
						+ "?ldns <" + OntologyReference.HAS_LAT + "> ?lans . "
						+ "?ldns <" + OntologyReference.HAS_LON + "> ?lons . "
						+ "?ls <" + OntologyReference.OF_PAYING_PERSON + "> ?pp . "
						+ "?ls <" + OntologyReference.ON_RIDE + "> ?rd"
						+ " }";
		
		// execute query
		SIBResponse response = kp.querySPARQL(sparqlQuery);
		SSAP_sparql_response results = response.sparqlquery_results;
		
		// process results
		if(results != null){
			Vector<Vector<String[]>> data = results.getResults();
			
			String toStopLatR = "";
			String toStopLonR = "";
			String fromStopLatR = "";
			String fromStopLonR = "";
			String toStopLatP = "";
			String toStopLonP = "";
			String fromStopLatP = "";
			String fromStopLonP = "";
			String rideR = "";
			String rideP = "";
			int maxRealPeople = 0;
			int maxPayingPeople = 0;
			
			for(Vector<String[]> riga : data){
				
				// calculate segment with max people
				int realPeople = Integer.parseInt(riga.get(4)[2]);
				int payingPeople = Integer.parseInt(riga.get(5)[2]);
				
				if(realPeople >= maxRealPeople){
					fromStopLatR = riga.get(0)[2];
					fromStopLonR = riga.get(1)[2];
					toStopLatR = riga.get(2)[2];
					toStopLonR = riga.get(3)[2];
					rideR = riga.get(6)[2].split("#")[1];
					maxRealPeople = realPeople;
				}
				
				if(payingPeople >= maxPayingPeople){
					fromStopLatP = riga.get(0)[2];
					fromStopLonP = riga.get(1)[2];
					toStopLatP = riga.get(2)[2];
					toStopLonP = riga.get(3)[2];
					rideP = riga.get(6)[2].split("#")[1];
					maxPayingPeople = payingPeople;
				}
			}
			
			result += rideR + "\n"
					+ "\t\tFrom Stop:\t" + BusStopManager.getInstance().getBusStopFromLatLngString(lineNumber, fromStopLatR + "-" + fromStopLonR).getName()  + "\n"
					+"\t\tTo Stop:\t" + BusStopManager.getInstance().getBusStopFromLatLngString(lineNumber, toStopLatR + "-" + toStopLonR).getName() + "\n"
					+ "\t\tMost real people " + maxRealPeople + "\n"
					+ "\tSegment with most paying people for line " + lineNumber +":\n"
					+ "\t\tRide:\t" + rideP + "\n"
					+ "\t\tFrom Stop:\t" + BusStopManager.getInstance().getBusStopFromLatLngString(lineNumber, fromStopLatP + "-" + fromStopLonP).getName()  + "\n"
					+"\t\tTo Stop:\t" + BusStopManager.getInstance().getBusStopFromLatLngString(lineNumber, toStopLatP + "-" + toStopLonP).getName()  + "\n"
					+ "\t\tMost paying people " + maxPayingPeople + "\n";
			
			return result;
		}
		
		return "";
	}
	
	// returns all the line numbers of the rides in the simulations
	private List<String> getLineNumbers(){
		List<String> lineNumbers = new ArrayList<String>();
		
		//query
		String sparqlQuery = 
				"select distinct ?ln "
					+ "where {"
					+ "?bl <" + OntologyReference.RDF_TYPE + "> <" + OntologyReference.BUS_LINE + "> ."
					+ "?bl <" + OntologyReference.HAS_NUMBER + "> " + "?ln"
					+ "}";
		
		SIBResponse response = kp.querySPARQL(sparqlQuery);
		SSAP_sparql_response results = response.sparqlquery_results;
		
		//process results
		if(results != null){
			Vector<Vector<String[]>> data = results.getResults();
		
			for(Vector<String[]> riga : data)
				lineNumbers.add(riga.get(0)[2]);
		}
		

		return lineNumbers;
	}
	
	
	// returns the max affluence of the simulation
	private String maxAffluence(){
		
		// result expected: "MAX AFFLUENCE OF THE SIMULATION:
		//						Line:	32
		//						Ride:	ride0
		//						From Stop:	Piazza Malpighi
		//						To Stop:	San Vitale
		//						At time:	12:45
		//						Number of people:			45
		//						Number of paying people:	34
		
		String result = "\nMAX AFFLUENCE OF THE SIMULATION:\n";
		
		// query
		String sparqlQuery = 
				"select ?ln ?lacs ?locs ?lans ?lons ?ts ?rp ?pp ?rd "
					+ "where { "
					+ "?ls <" + OntologyReference.RDF_TYPE + "> <" + OntologyReference.AFFLUEANCE + "> . "
					+ "?ls <" + OntologyReference.FROM_CURR_STOP + "> ?cs . "
					+ "?cs <" + OntologyReference.HAS_LOCATION_DATA + "> ?ldcs . "
					+ "?ldcs <" + OntologyReference.HAS_LAT + "> ?lacs . "
					+ "?ldcs <" + OntologyReference.HAS_LON + "> ?locs . "
					+ "?ls <" + OntologyReference.TO_NEXT_STOP + "> ?ns . "
					+ "?ns <" + OntologyReference.HAS_LOCATION_DATA + "> ?ldns . "
					+ "?ldns <" + OntologyReference.HAS_LAT + "> ?lans . "
					+ "?ldns <" + OntologyReference.HAS_LON + "> ?lons . "
					+ "?ls <" + OntologyReference.ON_RIDE + "> ?rd . "
					+ "?rd <" + OntologyReference.AT_TIME + "> ?ts . "
					+ "?ls <" + OntologyReference.ON_LINE + "> ?bl . "
					+ "?bl <" + OntologyReference.HAS_NUMBER + "> ?ln . "
					+ "?ls <" + OntologyReference.OF_REAL_PERSON + "> ?rp . "
					+ "?ls <" + OntologyReference.OF_PAYING_PERSON + "> ?pp . "
					+ "?ls <" + OntologyReference.ON_RIDE + "> ?rd"
					+ " }";
		
		SIBResponse response = kp.querySPARQL(sparqlQuery);
		SSAP_sparql_response results = response.sparqlquery_results;

		// process results
		if(results != null){
			
			Vector<Vector<String[]>> data = results.getResults();
			
			// calculate max people affluence
			int maxPeople = 0;
			int realPeople = 0;
			String latFromCurrStop = "";
			String lonFromCurrStop = "";
			String latToNextStop = "";
			String lonToNextStop = "";
			String timestamp = "";
			String payingPeople = "";
			String lineNumber = "";
			String ride = "";
			
			for(Vector<String[]> riga : data){
				realPeople = Integer.parseInt(riga.get(6)[2]);
				
				if(realPeople >= maxPeople){
					maxPeople = realPeople;
					lineNumber = riga.get(0)[2];
					latFromCurrStop = riga.get(1)[2];
					lonFromCurrStop = riga.get(2)[2];
					latToNextStop = riga.get(3)[2];
					lonToNextStop = riga.get(4)[2];
					timestamp = riga.get(5)[2];
					payingPeople = riga.get(7)[2];
					ride = riga.get(8)[2].split("#")[1];
				}
			}
			
			result += "\tLine:\t" + lineNumber + "\n"
				+ "\tRide:\t" + ride + "\n"
				+ "\tFrom Stop:\t" + BusStopManager.getInstance().getBusStopFromLatLngString(lineNumber, latFromCurrStop + "-" + lonFromCurrStop).getName() + "\n"
				+ "\tTo Stop:\t" + BusStopManager.getInstance().getBusStopFromLatLngString(lineNumber, latToNextStop + "-" + lonToNextStop).getName() + "\n"
				+ "\tAt Time:\t" + timestamp + "\n"
				+ "\tNumber of people:\t" + maxPeople + "\n"
				+ "\tNumber of paying people:\t" + payingPeople + "\n";
			
			
			return result;
		}
		
		
		return "";
		
	}
	
	// return the string that represents the economic summary of the bus lines
	public String economicSummary(){
		return this.maxAffluence()
			+ this.maxGetOn()
			+ this.maxAffluenceForDayAndLine()
			+ this.maxGetOnForDayAndLine()
			+ this.maxFinesLine()
			+ this.linesBalance()
			+ this.evasionNumberForRide()
			;
	}

}
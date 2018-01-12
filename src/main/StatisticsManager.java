package main;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import simulationConfiguration.SimulationConfig;
import sofia_kp.KPICore;
import sofia_kp.SIBResponse;
import sofia_kp.SSAP_sparql_response;
import utils.OntologyReference;
import utils.SIBConfiguration;

public class StatisticsManager {
	
	// this class doesn't connect to the SIB, it uses the method declared in Statistics class to operate, which connects to the SIB
	
	private static StatisticsManager istance = null;
	
	private KPICore kp = null;
	
	private StatisticsManager(){
		kp = new KPICore(SIBConfiguration.getInstance().getHost(),
				SIBConfiguration.getInstance().getPort(),
				SIBConfiguration.getInstance().getSmartSpaceName());
	}
	
	private void joinToTheSIB(){
		if(!kp.join().isConfirmed())
			System.out.println("Error joining the SIB");
		else
			System.out.println("Statistics joined SIB correctly");
	}
	
	public StatisticsManager getInstance(){	
		if(istance == null)
			istance = new StatisticsManager();

		return istance;
	}
	
	// returns a string containing the segment with max people for every line in every day
	private String maxPeopleStatistics(){

		// result expected: "SEGMENTS WITH MOST PEOPLE FOR EACH DAY AND LINE:"
		//					"	Day 1
		//							segmentMostPeopleFromLineAndDay(int day, int lineNumber)
		//							segmentMostPeopleFromLineAndDay(int day, int lineNumber)
		//							....
		//							segmentMostPeopleFromLineAndDay(int day, int lineNumber)
		//						Day 2
		//							segmentMostPeopleFromLineAndDay(int day, int lineNumber)
		//						...
		//						Day N
		//							segmentMostPeopleFromLineAndDay(int day, int lineNumber)
		
		String result = "SEGMENTS WITH MOST PEOPLE FOR EACH DAY AND LINE:\n";
		
		int numDaysOfSimulation = SimulationConfig.getInstance().getSimulationDays();
		
		joinToTheSIB();
		
		for(int i=0; i<numDaysOfSimulation; i++){
			result += "\tDay " + i+1 + "\n";
			
			for(String lineResult : getLineNumbers())
				result += segmentMostPeopleFromLineAndDay(i, lineResult);
		}
		
		return result;
	}
	
	// returns the string that contains the segment with most people for a line in a precious day
	private String segmentMostPeopleFromLineAndDay(int day, String lineNumber){
		
		// result expected: "Line 32: 
		//						From Stop:	Lat= 0.21212;	Lon= 0.232323
		//						To Stop:	Lat= 0.2323;	Lon= 0.232323
		//						23 real people "
		
		String result = "Segment with most people for line " + lineNumber + " (Day " + day + "):\n" +
							"\tFrom Stop:\tLat= ";
		
		joinToTheSIB();
		
		//query
		String sparqlQuery = 
				"select ?lacs ?locs ?lanx ?lons ?rp "
						+ "where { "
						+ "?ls <" + OntologyReference.RDF_TYPE + "> <" + OntologyReference.AFFLUEANCE + "> ."
						+ "?ls <" + OntologyReference.ON_LINE + "> ?bl ."
						+ "?bl <" + OntologyReference.HAS_NUMBER + "> <" + lineNumber + "> ."
						+ "?ls <" + OntologyReference.OF_REAL_PERSON + "> ?rp ."
						+ "?ls <" + OntologyReference.HAS_SIMULATION_DAY + "> <" + day + "> ."
						+ "?ls <" + OntologyReference.FROM_CURR_STOP + "> ?cs ."
						+ "?ls <" + OntologyReference.TO_NEXT_STOP + "> ?ns ."
						+ "?cs <" + OntologyReference.HAS_LAT + "> ?lacs ."
						+ "?cs <" + OntologyReference.HAS_LON + "> ?locs ."
						+ "?ns <" + OntologyReference.HAS_LAT + "> ?lans ."
						+ "?ns <" + OntologyReference.HAS_LON + "> ?lons ."
						+ " }";
		
		// execute query
		SIBResponse response = kp.querySPARQL(sparqlQuery);
		SSAP_sparql_response results = response.sparqlquery_results;
		
		// process results
		if(results != null){
			Vector<Vector<String[]>> data = results.getResults();
			
			String toStopLat = "";
			String toStopLon = "";
			String fromStopLat = "";
			String fromStopLon = "";
			int maxPeople = 0;
			
			for(Vector<String[]> riga : data){
				
				// calculate segment with max people
				int realPeople = Integer.parseInt(riga.get(4)[2]);
				
				if(realPeople >= maxPeople){
					fromStopLat = riga.get(0)[2];
					fromStopLon = riga.get(1)[2];
					toStopLat = riga.get(2)[2];
					toStopLon = riga.get(3)[2];
					maxPeople = realPeople;
				}
			}
			
			result += fromStopLat + ";\tLon= " + fromStopLon + "\n"
					+"\tTo Stop:\tLat= "+ toStopLat + ";\tLon= " + toStopLon + "\n"
					+ "\t" + maxPeople + " real people\n";
			
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
					+ "?bl <" + OntologyReference.HAS_NUMBER + "> " + "?ln ."
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
		//						From Stop:	Lat= 0.4545345;	Lon= 0.34532
		//						To Stop:	Lat= 0.45323;	Lon= 0.345323
		//						At time:	12:45
		//						Number of people:			45
		//						Number of paying people:	34
		
		String result = "MAX AFFLUENCE OF THE SIMULATION:\n";
		
		// query
		String sparqlQuery = 
				"select ?ln ?lacr ?locs ?lans ?lons ?ts ?rp ?pp"
					+ "where {"
					+ "?ls <" + OntologyReference.RDF_TYPE + "> <" + OntologyReference.AFFLUEANCE + "> ."
					+ "?ls <" + OntologyReference.HAS_CURR_STOP + "> ?cs ."
					+ "?cs <" + OntologyReference.HAS_LAT + "> ?lacs ."
					+ "?cs <" + OntologyReference.HAS_LON + "> ?locs ."
					+ "?ls <" + OntologyReference.HAS_NEXT_STOP + "> ?ns ."
					+ "?ns <" + OntologyReference.HAS_LAT + "> ?lans ."
					+ "?ns <" + OntologyReference.HAS_LON + "> ?lons ."
					+ "?ls <" + OntologyReference.HAS_RIDE + "> ?rd ."
					+ "?rd <" + OntologyReference.AT_TIME + "> ?ts ."
					+ "?ls <" + OntologyReference.HAS_NUMBER + "> ?ln ."
					+ "?ls <" + OntologyReference.HAS_REAL_PERSON + "> ?lrp ."
					+ "?ls <" + OntologyReference.HAS_PAYING_PERSON + "> ?pp ."
					+ "}";
		
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
				}
			}
			
			result += "\tLine:\t" + lineNumber + "\n"
				+ "\tFrom Stop:\tLat= " + latFromCurrStop + ";\tLon= " + lonFromCurrStop + "\n"
				+ "\tTo Stop:\tLat= " + latToNextStop + ";\tLon= " + lonToNextStop + "\n"
				+ "\tAt Time:\t" + timestamp + "\n"
				+ "\tNumber of people:\t\t\t" + realPeople + "\n"
				+ "\tNumber of paying people:\t" + maxPeople + "\n";
			
			
			return result;
		}
		
		
		return "";
		
	}
	
/*	// return the string that represents the economic summary of the bus lines
	public String economicSummary(){
		String result = "";
			

			
		return result;
	}
*/

}

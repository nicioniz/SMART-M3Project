package main;

import java.io.FileNotFoundException;
import java.util.Vector;

import com.teamdev.jxmaps.LatLng;

import sofia_kp.KPICore;
import sofia_kp.SIBResponse;
import sofia_kp.SSAP_sparql_response;
import utils.OntologyReference;
import utils.SIBConfiguration;

public class StatisticsVisualizer {
	private KPICore kp;
	private String busline;
	private int days;
	private int rides;

	public StatisticsVisualizer(String busline, int days, int rides) {
		kp = new KPICore(SIBConfiguration.getInstance().getHost(),
				SIBConfiguration.getInstance().getPort(),
				SIBConfiguration.getInstance().getSmartSpaceName());
		this.busline = busline;
		this.days = days;
		this.rides = rides;
	}
	public void getStatistics() {
		System.out.println("CALCULATING STATISTICS...");
		if(!kp.join().isConfirmed())
			System.err.println ("Error joining the SIB");
		else
			System.out.println ("SIB joined correctly");
		
		String sparqlQuery =
				"select ?ls ?la ?lo ?lx ?lc ?lv ?lb ?ln "
					+ "where { "
					+ "?ls <" + OntologyReference.RDF_TYPE + "> <" + OntologyReference.AFFLUEANCE +"> ."
					+ "?lS <" + OntologyReference.OF_REAL_PERSON + "> ?la ."
					+ "?lS <" + OntologyReference.OF_PAYING_PERSON + "> ?lo ."
					+ "?lS <" + OntologyReference.ON_LINE + "> ?lx ."
					+ "?lS <" + OntologyReference.ON_RIDE+ "> ?lc ."
					+ "?lS <" + OntologyReference.FROM_CURR_STOP+ "> ?lv ."
					+ "?lS <" + OntologyReference.TO_NEXT_STOP+ "> ?lb ."
					+ "?lS <" + OntologyReference.HAS_SIMULATION_DAY+ "> ?ln"
				+ " }";		
		SIBResponse resp = kp.querySPARQL(sparqlQuery);
		SSAP_sparql_response results = resp.sparqlquery_results;
		if (results != null)
		{
			Vector<Vector<String[]>> data = results.getResults();
			for(Vector<String[]> riga : data) {
				System.out.println("affluence " + riga.get(0)[2]);
				System.out.println("real person " + riga.get(1)[2]); 
				System.out.println("paying person " + riga.get(2)[2]);
				System.out.println("line " + riga.get(3)[2]); 
				System.out.println("ride " + riga.get(4)[2]);
				System.out.println("current stop " + riga.get(5)[2]); 
				System.out.println("next stop " + riga.get(6)[2]);
				System.out.println("day " + riga.get(7)[2]);
			}
			
		}
	}
}


/*
 
 select ?ls  
 where { 
  	?ls <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://project/IoES1718#Affluence> 
 }

*/



/*
 * subscription query
 * select ?ld ?la ?lo  
 * where { 
 * 	<http://project/IoES1718#BUS32> <http://project/IoES1718#hasLocatioData> ?ld . 
 * 	?ld <http://project/IoES1718#hasLat> ?la .	
 * 	?ld  <http://project/IoES1718#hasLon> ?lo 
 * }
 */
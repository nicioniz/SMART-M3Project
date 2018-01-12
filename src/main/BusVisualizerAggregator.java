package main;

import sofia_kp.KPICore;
import utils.OntologyReference;
import utils.SIBConfiguration;


public class BusVisualizerAggregator extends Thread {
	private String busName;
	private String busLine;
	private KPICore kp;
	private BusMap map;
	private String busColor;
		
	public BusVisualizerAggregator(String busLine, BusMap map, String busColor) {
		this.busLine = busLine;
		this.busName = "BUS" + busLine;
		this.map = map;
		this.busColor = busColor;
		kp = new KPICore(SIBConfiguration.getInstance().getHost(),
					SIBConfiguration.getInstance().getPort(),
					SIBConfiguration.getInstance().getSmartSpaceName());
		}

	@Override
	public void run() {
		
		if(!kp.join().isConfirmed())
			System.err.println ("Error joining the SIB");
		else
			System.out.println ("SIB joined correctly");
		String busNameWithNS = OntologyReference.NS + busName;
		String sparqlQuery =
				"select ?ld ?la ?lo "
					+ "where { "
					+ "<" + busNameWithNS + "> <" + OntologyReference.HAS_LOCATION_DATA + "> ?ld ."
					+ "?ld <" + OntologyReference.HAS_LAT + "> ?la ."
					+ "?ld <" + OntologyReference.HAS_LON + "> ?lo"
				+ " }";			
		
		HandlerSubscriptionLocationData MyHandler = new HandlerSubscriptionLocationData(map, busLine, busColor);  
		map.waitReady();
		kp.subscribeSPARQL(sparqlQuery, MyHandler );
	}
	
	public BusMap getMap() {
		return map;
	}
}



/*
 * subscription query
 * select ?ld ?la ?lo  
 * where { 
 * 	<http://project/IoES1718#BUS32> <http://project/IoES1718#hasLocatioData> ?ld . 
 * 	?ld <http://project/IoES1718#hasLat> ?la .	
 * 	?ld  <http://project/IoES1718#hasLon> ?lo 
 * }
 */
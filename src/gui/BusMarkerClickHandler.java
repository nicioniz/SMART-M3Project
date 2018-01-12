package gui;

import java.util.Vector;

import com.teamdev.jxmaps.InfoWindow;
import com.teamdev.jxmaps.Map;
import com.teamdev.jxmaps.MapMouseEvent;
import com.teamdev.jxmaps.Marker;
import com.teamdev.jxmaps.MouseEvent;

import sofia_kp.KPICore;
import sofia_kp.SIBResponse;
import sofia_kp.SSAP_sparql_response;
import utils.OntologyReference;
import utils.SIBConfiguration;

public class BusMarkerClickHandler extends MapMouseEvent {

	private String lineNumber;
	private Marker marker;
	private Map map;
	private InfoWindow infoWindow;
	private KPICore kp;	
	
	/*
	 * select ?pd ?property ?value
	 * where {
	 * 	{ 	
	 * 		<http://project/IoES1718#BUS[n]> <http://project/IoES1718#hasPersonData> ?pd . 
	 * 		?pd ?property ?value
	 * 	}
	 * 	UNION
	 * 	{ 	
	 * 		<http://project/IoES1718#BUS[n]> <http://project/IoES1718#isInspectorPresent> ?value
	 * 	}
	 * }
	 * 
	 */
	private String query;
	
	public BusMarkerClickHandler(String lineNumber, Marker marker, Map map) {
		super();
		this.lineNumber = lineNumber;
		this.marker = marker;
		this.map = map;
		query = 
				"select ?pd ?property ?value " +
				"where { " +
						"{ " +
							"<" + OntologyReference.NS + "BUS" + lineNumber + "> <" + OntologyReference.HAS_PERSON_DATA + "> ?pd . " +
							"?pd ?property ?value " +
						"} " +
						"UNION " +
						"{ " +
							"<" + OntologyReference.NS + "BUS" + lineNumber + "> <" + OntologyReference.IS_INSPECTOR_PRESENT + "> ?value " +
						"} " +
				"}";
		kp = new KPICore(SIBConfiguration.getInstance().getHost(),
				SIBConfiguration.getInstance().getPort(),
				SIBConfiguration.getInstance().getSmartSpaceName());
		if(!kp.join().isConfirmed())
			System.err.println ("Error joining the SIB");
		else
			System.out.println ("Bus" + lineNumber + " infoWindow, SIB joined correctly");
	
		
		// Creating info window
        infoWindow = new InfoWindow(map);
        
        
        
	}
	
	@Override
	public void onEvent(MouseEvent mouseEvent) {

		
		
		SIBResponse resp = kp.querySPARQL(query);
		
		SSAP_sparql_response newResults = resp.sparqlquery_results;
		
		Vector<Vector<String[]>> data = newResults.getResults();
//		if(indSequence.equals("0"))
//			data = oldResults.getResults();
			
		int payingPerson = -42, realPerson = -42;
		boolean isInspectorPresent = false;
		
		for(Vector<String[]> riga : data) {
			String value = riga.get(2)[2];
			String property = riga.get(1)[2];
			if(property == null)
				isInspectorPresent = value.equals(OntologyReference.TRUE);
			else {
				if(property.equals(OntologyReference.HAS_REAL_PERSON))
					realPerson = Integer.parseInt(value);
				if(property.equals(OntologyReference.HAS_PAYING_PERSON))
					payingPerson = Integer.parseInt(value);
			}
		}
			
		
		//lineSeparator isn't useful: infoWindow show everything on single line
		StringBuilder sb = new StringBuilder();
		sb.append("LINE ");
		sb.append(lineNumber);
		sb.append(System.lineSeparator());
		sb.append("Real person: ");
		sb.append(realPerson);
		sb.append(System.lineSeparator());
		sb.append("Paying person: ");
		sb.append(payingPerson);
		sb.append(System.lineSeparator());
		sb.append("Inspector presence: ");
		sb.append(isInspectorPresent);
	
		
		infoWindow.setContent(sb.toString());
		
		
     // Showing info window under the marker
        infoWindow.open(map, marker);
	}
	
	
	
}

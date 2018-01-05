package main;

import java.util.Random;
import java.util.Vector;

import com.teamdev.jxmaps.LatLng;

import simulationConfiguration.SimulationConfig;
import sofia_kp.KPICore;
import utils.OntologyReference;
import utils.SIBConfiguration;
import utils.Triple;

public class BusStop {

	private String name;
	private String nameWithoutSpaces;
	private String id;
	private LatLng location;
	private boolean inspectorPresent;
		
	private KPICore kp;
	Vector<Vector<String>> oldInspectorPresenceTriple = new Vector<>();
	
	private Random r;
	
	public BusStop(String name, String id, LatLng location) {
		this.name = name;
		nameWithoutSpaces = name.replace(' ', '_');
		this.id = id;
		this.location = location;
		r = new Random(System.currentTimeMillis());
	}
		
	public void init() {
		insertIntoSIB();
		generateInspector();		
	}
	
	private void insertIntoSIB() {
		String locationDataName = "BusStop" + nameWithoutSpaces + "LocationData";
		
		kp = new KPICore(SIBConfiguration.getInstance().getHost(),
				SIBConfiguration.getInstance().getPort(),
				SIBConfiguration.getInstance().getSmartSpaceName());
		
		if(!kp.join().isConfirmed())
			System.err.println ("Error joining the SIB");
		else
			System.out.println ("BusStop joined SIB correctly");
		
		Vector<Vector<String>> newTripleToInsert = new Vector<>();
		
		Vector<String> locationDataTypeTriple = new Triple(
				OntologyReference.NS + locationDataName,
				OntologyReference.RDF_TYPE,
				OntologyReference.LOCATION_DATA,
				Triple.URI,
				Triple.URI).getAsVector();
		newTripleToInsert.add(locationDataTypeTriple);
		
		Vector<String> locationDataLat = new Triple(
				OntologyReference.NS + locationDataName,
				OntologyReference.HAS_LAT,
				location.getLat() + "",
				Triple.URI,
				Triple.LITERAL).getAsVector();
		newTripleToInsert.add(locationDataLat);
		
		Vector<String> locationDataLon = new Triple(
				OntologyReference.NS + locationDataName,
				OntologyReference.HAS_LON,
				location.getLng() + "",
				Triple.URI,
				Triple.LITERAL).getAsVector();
		newTripleToInsert.add(locationDataLon);
		
		Vector<String> typeTriple = new Triple(
				getUri(),
				OntologyReference.RDF_TYPE,
				OntologyReference.BUS_STOP,
				Triple.URI,
				Triple.URI).getAsVector();
		newTripleToInsert.add(typeTriple);
		
		Vector<String> tripleId = new Triple(
				getUri(),
				OntologyReference.HAS_ID,
				id,
				Triple.URI,
				Triple.LITERAL).getAsVector();
		newTripleToInsert.add(tripleId);
		
		Vector<String> tripleName = new Triple(
				getUri(),
				OntologyReference.HAS_NAME,
				name,
				Triple.URI,
				Triple.LITERAL).getAsVector();
		newTripleToInsert.add(tripleName);
		
		Vector<String> tripleLocationData = new Triple(
				getUri(),
				OntologyReference.HAS_LOCATION_DATA,
				OntologyReference.NS + locationDataName,
				Triple.URI,
				Triple.URI).getAsVector();
		newTripleToInsert.add(tripleLocationData);
		
		kp.insert(newTripleToInsert);
	}

	private void generateInspector() {
		boolean probabilityPresence = r.nextInt(101) <= SimulationConfig.getInstance().getInspectorPresencePercentageProbability();
		if(probabilityPresence && SimulationConfig.getInstance().addInspector())
			inspectorPresent = true;
		else
			inspectorPresent = false;
		oldInspectorPresenceTriple.add(new Triple(
				getUri(),
				OntologyReference.IS_INSPECTOR_PRESENT,
				isInspectorPresent() ? OntologyReference.TRUE : OntologyReference.FALSE,
				Triple.URI,
				Triple.URI).getAsVector());
		
		kp.insert(oldInspectorPresenceTriple);
	}

	public boolean isInspectorPresent() {
		return inspectorPresent;
	}

	public void setInspectorPresent(boolean inspectorPresent) {
		this.inspectorPresent = inspectorPresent;
		updateSIB();
	}
	
	private void updateSIB() {
		Vector<Vector<String>> newInspectorPresenceTriple = new Vector<>();
		newInspectorPresenceTriple.add(new Triple(
				getUri(),
				OntologyReference.IS_INSPECTOR_PRESENT,
				isInspectorPresent() ? OntologyReference.TRUE : OntologyReference.FALSE,
				Triple.URI,
				Triple.URI).getAsVector());
		
		kp.update(newInspectorPresenceTriple, oldInspectorPresenceTriple);
		oldInspectorPresenceTriple = newInspectorPresenceTriple;
	}

	/**
	 * 
	 * @return When the bus arrives in a busStop, it call this method.
	 * If the method returns true, the inspector was present and now he isn't (he is on the bus)
	 * If the method returns false the inspector wasn't present and now he is still not present
	 */
	public boolean getInspector() {
		if(isInspectorPresent()) {
			setInspectorPresent(false);
			return true;
		}
		return false;
	}

	public String getUri() {
		return OntologyReference.NS + "BusStop" + nameWithoutSpaces;
	}
		
	/**
	 * 
	 * @return Return a string that represents the location.
	 * The format is "Latitude-Longitude"
	 */
	public String getLocationString() {
		return "" + location.getLat() + "-" + location.getLng();
	}

	public LatLng getLocation() {
		return location;
	}
	
	
}

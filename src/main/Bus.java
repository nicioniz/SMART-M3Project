package main;

import java.util.HashMap;
import java.util.List;
import java.util.Vector;
import com.teamdev.jxmaps.LatLng;
import parser.Parser;
import simulationConfiguration.SimulationConfig;
import sofia_kp.KPICore;
import utils.OntologyReference;
import utils.SIBConfiguration;
import utils.Triple;

public class Bus extends Thread {

	private String name;
	private String filenamePoints; 
	private String filenameStops; 
	private HashMap<Double, Integer> stopsList;

	public Bus(String name, String filenamePoints, String filenameStops) {
		this.name = name;
		this.filenamePoints = filenamePoints;
		this.filenameStops = filenameStops;
		stopsList = new HashMap<Double, Integer>();
	}
	
	@Override
	public void run() {
		//create hash map for stops
		Parser stopsParser;
    	List<LatLng> stopsPoints;
    	stopsParser = new Parser(filenameStops);
    	stopsPoints = stopsParser.getListOfPoint();
		int sizeOfStopsList = stopsPoints.size();
		
		for (int i=0; i<sizeOfStopsList; i++)
			stopsList.put(stopsPoints.get(i).getLat(), i );
		
		LatLng nextPoint;
		KPICore kp = new KPICore(SIBConfiguration.getInstance().getHost(),
				SIBConfiguration.getInstance().getPort(),
				SIBConfiguration.getInstance().getSmartSpaceName());
		
		if(!kp.join().isConfirmed())
			System.err.println ("Error joining the SIB");
		else
			System.out.println ("Bus joined SIB correctly");
	
		//insert bus into SIB
		if(!kp.insert(
				OntologyReference.NS + name,
				OntologyReference.RDF_TYPE,
				OntologyReference.BUS,
				Triple.URI,
				Triple.URI).isConfirmed())
			System.err.println ("Error inserting bus");
		else
			System.out.println ("Bus correctly inserted " + name);
		
		//get list of stops
		Parser p1;
    	List<LatLng> points1;
    	p1 = new Parser(filenamePoints);
		points1 = p1.getListOfPoint();
		int size1 = points1.size();
		
		//move bus: for each point insert new triple
		
		String locationDataName = name + "LocationData";
		String booleanDataNameTrue = name + "booleanDataTrue";
		String booleanDataNameFalse = name + "booleanDataFalse";

		Vector<Vector<String>> newTripleToInsert = new Vector<>();
		Vector<Vector<String>> newBoolean = new Vector<>();
		Vector<Vector<String>> oldTriple = new Vector<>();
		Vector<Vector<String>> newStatus = new Vector<>();
		nextPoint = points1.get(0);
		
		Vector<String> locationData = new Triple(
				OntologyReference.NS + locationDataName,
				OntologyReference.RDF_TYPE,
				OntologyReference.LOCATION_DATA,
				Triple.URI,
				Triple.URI).getAsVector();
		
		Vector<String> booleanDataFalse = new Triple(
				OntologyReference.NS + booleanDataNameFalse,
				OntologyReference.RDF_TYPE,
				OntologyReference.BOOLEAN,
				Triple.URI,
				Triple.URI).getAsVector();
		
		Vector<String> booleanDataTrue = new Triple(
				OntologyReference.NS + booleanDataNameTrue,
				OntologyReference.RDF_TYPE,
				OntologyReference.BOOLEAN,
				Triple.URI,
				Triple.URI).getAsVector();
		
		newTripleToInsert.add(locationData);
		newBoolean.add(booleanDataFalse);
		newBoolean.add(booleanDataTrue);
		kp.insert(newBoolean);
		Vector<String> busLocationDataArch = new Triple(
				OntologyReference.NS + name,
				OntologyReference.HAS_LOCATION_DATA,
				OntologyReference.NS + locationDataName,
				Triple.URI,
				Triple.URI).getAsVector();
		
		newTripleToInsert.add(busLocationDataArch);
		
		newTripleToInsert.add(new Triple(
				OntologyReference.NS + locationDataName,
				OntologyReference.HAS_LAT,
				String.valueOf(nextPoint.getLat()),
				Triple.URI,
				Triple.LITERAL).getAsVector());
		
		newTripleToInsert.add(new Triple(
				OntologyReference.NS + locationDataName,
				OntologyReference.HAS_LON,
				String.valueOf(nextPoint.getLng()),
				Triple.URI,
				Triple.LITERAL).getAsVector());
		
		kp.insert(newTripleToInsert);
		
		newTripleToInsert.remove(locationData);
		newTripleToInsert.remove(busLocationDataArch);
		
		oldTriple = newTripleToInsert;
		
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		for (int i = 1; i < size1; i++) {
			nextPoint = points1.get(i);
			Double latNextPoint = new Double(nextPoint.getLat());
			Integer stopIndex = stopsList.get(latNextPoint);		
			
			newTripleToInsert = new Vector<>();
			
			if  (stopIndex != null){
				newTripleToInsert.add(new Triple(
				OntologyReference.NS + name,
				OntologyReference.IS_IN_TRANSIT,
				OntologyReference.BOOLEAN + booleanDataNameFalse,
				Triple.URI,
				Triple.URI).getAsVector());
			}else {
				newTripleToInsert.add(new Triple(
				OntologyReference.NS + name,
				OntologyReference.IS_IN_TRANSIT,
				OntologyReference.BOOLEAN + booleanDataNameTrue,
				Triple.URI,
				Triple.URI).getAsVector());
			}

			newTripleToInsert.add(new Triple(
					OntologyReference.NS + locationDataName,
					OntologyReference.HAS_LAT,
					String.valueOf(nextPoint.getLat()),
					Triple.URI,
					Triple.LITERAL).getAsVector());
			
			newTripleToInsert.add(new Triple(
					OntologyReference.NS + locationDataName,
					OntologyReference.HAS_LON,
					String.valueOf(nextPoint.getLng()),
					Triple.URI,
					Triple.LITERAL).getAsVector());
			
			kp.update(newTripleToInsert, oldTriple);
			
			oldTriple = newTripleToInsert;
			try {
				Thread.sleep(Math.round(100/SimulationConfig.getInstance().getSimulationVelocity()));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}

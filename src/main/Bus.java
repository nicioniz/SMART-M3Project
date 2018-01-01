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
	private int days;
	private String filenamePoints; 
	private String filenameStops; 
	private HashMap<Double, Integer> stopsList;

	public Bus(String name, String filenamePoints, String filenameStops, int days) {
		this.name = name;
		this.days = days;
		this.filenamePoints = filenamePoints;
		this.filenameStops = filenameStops;
		stopsList = new HashMap<Double, Integer>();
	}
	
	@Override
	public void run() {
		LatLng nextPoint;
		Double latNextPoint;
		Integer stopIndex;
		//get list of point
		Parser parserForPoints;
	   	List<LatLng> listOfPoints;
		parserForPoints = new Parser(filenamePoints);
		listOfPoints = parserForPoints.getListOfPoint();
		int listOfPointSize = listOfPoints.size();
		
		//create hash map for stops
		Parser stopsParser;
    	List<LatLng> stopsPoints;
    	stopsParser = new Parser(filenameStops);
    	stopsPoints = stopsParser.getListOfPoint();
		int sizeOfStopsList = stopsPoints.size();
		//insert into hash map all the stop
		for (int i=0; i<sizeOfStopsList; i++)
			stopsList.put(stopsPoints.get(i).getLat(), i );
		
		//connect to sib
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
	
			String locationDataName = name + "LocationData";
			String booleanDataNameTrue = name + "booleanDataTrue";
			String booleanDataNameFalse = name + "booleanDataFalse";

			Vector<Vector<String>> newTripleToInsert = new Vector<>();
			Vector<Vector<String>> newBoolean = new Vector<>();
			Vector<Vector<String>> oldTriple = new Vector<>();
			
			//write datatype of objects
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
			
			Vector<String> busLocationDataArch = new Triple(
					OntologyReference.NS + name,
					OntologyReference.HAS_LOCATION_DATA,
					OntologyReference.NS + locationDataName,
					Triple.URI,
					Triple.URI).getAsVector();
			
			newTripleToInsert.add(busLocationDataArch);
			
			kp.insert(newBoolean);
			kp.insert(newTripleToInsert);
			
			newTripleToInsert.remove(locationData);
			newTripleToInsert.remove(busLocationDataArch);
			
			oldTriple = newTripleToInsert;
			
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			//move bus: for each point insert new locationData and new Status(in Transit) 
			//the bus must repeat this cycle 'days' times
			
		for (int day=0; day<days; day++) {
			for (int i = 0; i < listOfPointSize; i++) {
				nextPoint = listOfPoints.get(i);
				latNextPoint = new Double(nextPoint.getLat());
				stopIndex = stopsList.get(latNextPoint);	
				newTripleToInsert = new Vector<>();
				
				//check whether the next point is a bus stop
				if  (stopIndex != null){
					//in this case bus is not in transit
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
				
				if (i==0)
					kp.insert(newTripleToInsert);
				else
					kp.update(newTripleToInsert, oldTriple);
				
				oldTriple = newTripleToInsert;
				try {
					Thread.sleep(Math.round(100/SimulationConfig.getInstance().getSimulationVelocity()));
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			kp.remove(newTripleToInsert);
			System.out.printf("Day %d terminated\n", day);
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
	}
}

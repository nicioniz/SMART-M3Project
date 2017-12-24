package main;

import java.util.Random;
import java.util.Vector;

import com.teamdev.jxmaps.LatLng;

import simulationConfiguration.SimulationConfig;
import sofia_kp.KPICore;
import utils.OntologyReference;
import utils.SIBConfiguration;
import utils.Triple;

public class BusStop extends Thread {

	private String name;
	private String id;
	private LatLng location;
	
	private Random r;
	
	public BusStop(String name, String id, LatLng location) {
		super();
		this.name = name;
		this.id = id;
		this.location = location;
		r = new Random(System.currentTimeMillis());
	}
	
	@Override
	public void run() {
		
		String sensorName = "BusStop" + name + "Sensor";
		String locationDataName = "BusStop" + name + "LocationData";
		
		KPICore kp = new KPICore(SIBConfiguration.getInstance().getHost(),
				SIBConfiguration.getInstance().getPort(),
				SIBConfiguration.getInstance().getSmartSpaceName());
		
		if(!kp.join().isConfirmed())
			System.err.println ("Error joining the SIB");
		else
			System.out.println ("Bus joined SIB correctly");
		
		Vector<Vector<String>> newTripleToInsert = new Vector<>();
		Vector<Vector<String>> oldTriple = new Vector<>();
		
		Vector<String> sensor = new Triple(
				OntologyReference.NS + sensorName,
				OntologyReference.RDF_TYPE,
				OntologyReference.SENSOR,
				Triple.URI,
				Triple.URI).getAsVector();
		newTripleToInsert.add(sensor);
		
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
				OntologyReference.NS + "BusStop" + name,
				OntologyReference.RDF_TYPE,
				OntologyReference.BUS_STOP,
				Triple.URI,
				Triple.URI).getAsVector();
		newTripleToInsert.add(typeTriple);
		
		Vector<String> tripleId = new Triple(
				OntologyReference.NS + "BusStop" + name,
				OntologyReference.HAS_ID,
				id,
				Triple.URI,
				Triple.LITERAL).getAsVector();
		newTripleToInsert.add(tripleId);
		
		Vector<String> tripleName = new Triple(
				OntologyReference.NS + "BusStop" + name,
				OntologyReference.HAS_NAME,
				name,
				Triple.URI,
				Triple.LITERAL).getAsVector();
		newTripleToInsert.add(tripleName);
		
		Vector<String> tripleSensor = new Triple(
				OntologyReference.NS + "BusStop" + name,
				OntologyReference.HAS_SENSOR,
				OntologyReference.NS + sensorName,
				Triple.URI,
				Triple.URI).getAsVector();
		newTripleToInsert.add(tripleSensor);
		
		Vector<String> tripleLocationData = new Triple(
				OntologyReference.NS + "BusStop" + name,
				OntologyReference.HAS_LOCATION_DATA,
				OntologyReference.NS + locationDataName,
				Triple.URI,
				Triple.URI).getAsVector();
		newTripleToInsert.add(tripleLocationData);
		
		newTripleToInsert.add(new Triple(OntologyReference.NS + name,
				OntologyReference.HAS_WAITING_PERSON,
				getWaitingPeople() + "",
				Triple.URI,
				Triple.LITERAL).getAsVector());
		
		kp.insert(newTripleToInsert);

		newTripleToInsert.remove(sensor);
		newTripleToInsert.remove(locationDataTypeTriple);
		newTripleToInsert.remove(locationDataLat);
		newTripleToInsert.remove(locationDataLon);
		newTripleToInsert.remove(typeTriple);
		newTripleToInsert.remove(tripleId);
		newTripleToInsert.remove(tripleName);
		newTripleToInsert.remove(tripleSensor);
		newTripleToInsert.remove(tripleLocationData);
		
		oldTriple = newTripleToInsert;
		
		for(;;) {
			
			newTripleToInsert = new Vector<>();
			
			newTripleToInsert.add(new Triple(OntologyReference.NS + name,
					OntologyReference.HAS_WAITING_PERSON,
					getWaitingPeople() + "",
					Triple.URI,
					Triple.LITERAL).getAsVector());
			
			kp.update(newTripleToInsert, oldTriple);
			oldTriple = newTripleToInsert;
			
			try {
				sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		
	}
	
	private int getWaitingPeople() {
		int error = (int)Math.round(SimulationConfig.getInstance().getPeopleWaitingAtBusStop() * SimulationConfig.getInstance().getPercErrorPeopleWaitingAtBusStop() / 100.0);
		return r.nextInt(2 * error + 1) - error + SimulationConfig.getInstance().getPeopleWaitingAtBusStop();
	}
		
}

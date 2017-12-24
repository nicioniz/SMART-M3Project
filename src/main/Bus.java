package main;

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
	private String filename; 

	public Bus(String name, String filename) {
		this.name = name;
		this.filename = filename;
	}
	
	@Override
	public void run() {
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
    	p1 = new Parser(filename);
		points1 = p1.getListOfPoint();
		int size1 = points1.size();
		
		
		//move bus: for each point insert new triple
		
		String locationDataName = name + "LocationData";
		Vector<Vector<String>> newTripleToInsert = new Vector<>();
		Vector<Vector<String>> oldTriple = new Vector<>();
		nextPoint = points1.get(0);
		
		Vector<String> locationData = new Triple(
				OntologyReference.NS + locationDataName,
				OntologyReference.RDF_TYPE,
				OntologyReference.LOCATION_DATA,
				Triple.URI,
				Triple.URI).getAsVector();
		
		newTripleToInsert.add(locationData);
		
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
			newTripleToInsert = new Vector<>();

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

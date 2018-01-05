package main;

import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Vector;
import com.teamdev.jxmaps.LatLng;
import parser.BusPathParser;
import simulationConfiguration.SimulationConfig;
import sofia_kp.KPICore;
import utils.OntologyReference;
import utils.SIBConfiguration;
import utils.Triple;

public class Bus extends Thread {

	private String name;
	private int days;
	private String line;
	private int busRides;
	private String filenamePoints; 
	private String filenameStops; 
	private HashMap<Double, Integer> stopsList;
	private Random random;

	public Bus(String name, String line, String filenamePoints, String filenameStops, int days, int busRides) {
		this.name = name;
		this.days = days;
		this.line = line;
		this.busRides = busRides;
		this.filenamePoints = filenamePoints;
		this.filenameStops = filenameStops;
		stopsList = new HashMap<Double, Integer>();
		random = new Random();
	}
	
	private void insertSensor(String busSensorName, String busType, String autobusName, Vector<Vector<String>> newTripleToInsert ) {
		Vector<String> busSensorGPS = new Triple(
				OntologyReference.NS + busSensorName,
				OntologyReference.HAS_SENSOR_TYPE,
				busType,
				Triple.URI,
				Triple.URI).getAsVector();
		
		newTripleToInsert.add(busSensorGPS);
		
		Vector<String> busSensorGPSId = new Triple(
				OntologyReference.NS + busSensorName,
				OntologyReference.HAS_ID,
				String.valueOf(random.nextInt(1000)),
				Triple.URI,
				Triple.LITERAL).getAsVector();
		
		newTripleToInsert.add(busSensorGPSId);
		
		Vector<String> busSensorGPSArch = new Triple(
				OntologyReference.NS + autobusName,
				OntologyReference.HAS_SENSOR,
				OntologyReference.NS + busSensorName,
				Triple.URI,
				Triple.URI).getAsVector();
		
		newTripleToInsert.add(busSensorGPSArch);
		
	}
	
	@Override
	public void run() {
		//DA CAPIRE QUALI DI QUESTE VARIABILI VANNO ELIMINATE E MESSE NELLA SIB
		int descendedRealPerson;
		int descendedPayingPerson;
		int ascendedRealPerson;
		int ascendedPayingPerson;
		int realPerson = 0;
		int payingPerson = 0;
		
		LatLng nextPoint;
		Double latNextPoint;
		Integer stopIndex;
		//get list of point
		BusPathParser parserForPoints;
	   	List<LatLng> listOfPoints;
		parserForPoints = new BusPathParser(filenamePoints);
		listOfPoints = parserForPoints.getListOfPoint();
		int listOfPointSize = listOfPoints.size();
		
		Vector<Vector<String>> newTripleToInsert = new Vector<>();
	//	Vector<Vector<String>> oldTriple = new Vector<>();
		Vector<Vector<String>> newTriplePoint = new Vector<>();
		Vector<Vector<String>> oldTriplePoint = new Vector<>();
		Vector<Vector<String>> newRide = new Vector<>();
		Vector<Vector<String>> oldRide = new Vector<>();
		Vector<Vector<String>> currentAndNextStop = new Vector<>();
		Vector<Vector<String>> oldCurrentAndNextStop = new Vector<>();
		
		//create hash map for stops
//		BusPathParser stopsParser;
//    	List<LatLng> stopsPoints;
//    	stopsParser = new BusPathParser(filenameStops);
//    	stopsPoints = stopsParser.getListOfPoint();
		List<LatLng> stopsPoints = BusStopManager.getInstance().getStopsPoints(line);
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
		String personDataName = name + "PersonData";
		String busLineName =  name+"BusLine";
		String busRideName = name+"BusRide";
		String busSensorNameGPS = name+"GPSSensor";
		String busSensorNameCameraEnter = name+"CameraEnterSensor";
		String busSensorNameCameraExit = name+"CameraExitSensor";
		String busSensorFareBoxEnter = name+"FareBoxEnterSensor";
		String busSensorFareBoxExit = name+"FareBoxExitSensor";
			
		//write datatype of objects
		Vector<String> locationData = new Triple(
				OntologyReference.NS + locationDataName,
				OntologyReference.RDF_TYPE,
				OntologyReference.LOCATION_DATA,
				Triple.URI,
				Triple.URI).getAsVector();
			
		newTripleToInsert.add(locationData);
		
		Vector<String> personData = new Triple(
				OntologyReference.NS + personDataName,
				OntologyReference.RDF_TYPE,
				OntologyReference.PERSON_DATA,
				Triple.URI,
				Triple.URI).getAsVector();
			
		newTripleToInsert.add(personData);
			
		Vector<String> busLocationDataArch = new Triple(
				OntologyReference.NS + name,
				OntologyReference.HAS_LOCATION_DATA,
				OntologyReference.NS + locationDataName,
				Triple.URI,
				Triple.URI).getAsVector();
			
		newTripleToInsert.add(busLocationDataArch);
		
		Vector<String> busPersonDataArch = new Triple(
				OntologyReference.NS + name,
				OntologyReference.HAS_PERSON_DATA,
				OntologyReference.NS + personDataName,
				Triple.URI,
				Triple.URI).getAsVector();
			
		newTripleToInsert.add(busPersonDataArch);
			
		Vector<String> busId = new Triple(
				OntologyReference.NS + name,
				OntologyReference.HAS_ID,
				String.valueOf(random.nextInt(100)),
				Triple.URI,
				Triple.LITERAL).getAsVector();
			
		newTripleToInsert.add(busId);
			
		//insert line for autobus
		Vector<String> busLine = new Triple(
				OntologyReference.NS + busLineName,
				OntologyReference.RDF_TYPE,
				OntologyReference.BUS_LINE,
				Triple.URI,
				Triple.URI).getAsVector();
			
		newTripleToInsert.add(busLine);
		
		Vector<String> busLineNumber = new Triple(
				OntologyReference.NS + busLineName,
				OntologyReference.HAS_NUMBER,
				line,
				Triple.URI,
				Triple.LITERAL).getAsVector();
			
		newTripleToInsert.add(busLineNumber);
		
		//insert arch between bus and line
		Vector<String> busLineArch = new Triple(
				OntologyReference.NS + name,
				OntologyReference.ON_LINE,
				OntologyReference.NS + busLineName,
				Triple.URI,
				Triple.URI).getAsVector();
		
		newTripleToInsert.add(busLineArch);
		
		//insert max seats
		Vector<String> busMaxSeats = new Triple(
				OntologyReference.NS + name,
				OntologyReference.HAS_MAX_SEATS,
				String.valueOf(SimulationConfig.getInstance().getAutobusMaxSeats()),
				Triple.URI,
				Triple.LITERAL).getAsVector();
		newTripleToInsert.add(busMaxSeats);		
			
		//insert gps sensor with type, id and arch with current bus
		insertSensor(busSensorNameGPS, OntologyReference.GPS, name, newTripleToInsert);
		//insert enterCamera sensor with type, id and arch with current bus
		insertSensor(busSensorNameCameraEnter, OntologyReference.CAMERA, name, newTripleToInsert);
		//insert exitCamera sensor with type, id and arch with current bus
		insertSensor(busSensorNameCameraExit, OntologyReference.CAMERA, name, newTripleToInsert);
		//insert enterFareBox sensor with type, id and arch with current bus
		insertSensor(busSensorFareBoxEnter, OntologyReference.FAREBOX, name, newTripleToInsert);
		//insert exitFareBox sensor with type, id and arch with current bus
		insertSensor(busSensorFareBoxExit, OntologyReference.FAREBOX, name, newTripleToInsert);
		
		//insert all previous triples
		kp.insert(newTripleToInsert);
			
		//move bus: for each point insert new locationData and new Status(in Transit) 
		//the bus must repeat this cycle 'days' times
			
		for (int day=0; day<days; day++) {
			for (int ride=0; ride<busRides; ride++) {
				//insert ride data into SIB
				newRide = new Vector<>();

				kp.insert(
						OntologyReference.NS + busRideName + ride,
						OntologyReference.RDF_TYPE,
						OntologyReference.RIDE,
						Triple.URI,
						Triple.URI);
				
				kp.insert(
						OntologyReference.NS + busLineName,
						OntologyReference.HAS_RIDE,
						OntologyReference.NS + busRideName + ride,
						Triple.URI,
						Triple.URI);
				
				kp.insert(
						OntologyReference.NS + busRideName + ride,
						OntologyReference.AT_TIME,
						"time"+ride,
						Triple.URI,
						Triple.LITERAL);
				
				newRide.add(new Triple(
						OntologyReference.NS + name,
						OntologyReference.ON_RIDE,
						OntologyReference.NS + busRideName + ride,
						Triple.URI,
						Triple.URI).getAsVector());
				
				if (ride==0) {
					kp.insert(newRide);
				}else {
					kp.update(newRide, oldRide);
				}
				oldRide= newRide;				
				
				for (int i = 0; i < listOfPointSize; i++) {
					nextPoint = listOfPoints.get(i);
					latNextPoint = new Double(nextPoint.getLat());
					stopIndex = stopsList.get(latNextPoint);	
					newTriplePoint = new Vector<>();
					
					//check whether the next point is a bus stop
					if  (stopIndex != null){
						
						//person generation logic
						//PER ORA NON VIENE USATA LA SIB
						ascendedRealPerson = generateAscendingRealPerson(realPerson, SimulationConfig.getInstance().getAutobusMaxSeats());
						ascendedPayingPerson = generateAscendingPayingPerson(ascendedRealPerson);
						descendedRealPerson = generateDescendingRealPerson(realPerson);
						descendedPayingPerson = generateDescendingPayingPerson(descendedRealPerson);
						realPerson += ascendedRealPerson - descendedRealPerson;
						payingPerson += ascendedPayingPerson - descendedPayingPerson;
						
						currentAndNextStop = new Vector<>();
						//in this case bus is not in transit
						newTriplePoint.add(new Triple(
						OntologyReference.NS + name,
						OntologyReference.IS_IN_TRANSIT,
						OntologyReference.FALSE,
						Triple.URI,
						Triple.URI).getAsVector());
						
						//update current stop

						Vector<String> currentStop = new Triple(
								OntologyReference.NS + name,
								OntologyReference.HAS_CURR_STOP,
								String.valueOf(stopIndex),
								Triple.URI,
								Triple.LITERAL).getAsVector();
						
						currentAndNextStop.add(currentStop);
						
/*
 * 					INCOMPLETO, dovremmo inserire sulla SIB i dati di realPerson e payingPerson
 * 						
						//update real person after current stop
						Vector<String> realPersonAfterCurrentStop = new Triple(
								OntologyReference.NS + name,
								OntologyReference.HAS_CURR_STOP,
								String.valueOf(stopIndex),
								Triple.URI,
								Triple.LITERAL).getAsVector();
												
						currentAndNextStop.add(realPersonAfterCurrentStop);
												
						//update paying person after current stop 
												
						Vector<String> payingPersonAfterCurrentStop = new Triple(
								OntologyReference.NS + name,
								OntologyReference.HAS_CURR_STOP,
								String.valueOf(stopIndex),
								Triple.URI,
								Triple.LITERAL).getAsVector();
											
						currentAndNextStop.add(payingPersonAfterCurrentStop);
*
*/						
						//update next stop
						
						Vector<String> nextStop = new Triple(
								OntologyReference.NS + name,
								OntologyReference.HAS_NEXT_STOP,
								String.valueOf(stopIndex+1),
								Triple.URI,
								Triple.LITERAL).getAsVector();
						
						currentAndNextStop.add(nextStop);
						
						if (stopIndex==0)
							kp.insert(currentAndNextStop);
						else
							kp.update(currentAndNextStop, oldCurrentAndNextStop);
						oldCurrentAndNextStop = currentAndNextStop;
						
					}else {
						newTriplePoint.add(new Triple(
						OntologyReference.NS + name,
						OntologyReference.IS_IN_TRANSIT,
						OntologyReference.TRUE,
						Triple.URI,
						Triple.URI).getAsVector());
					}

					newTriplePoint.add(new Triple(
							OntologyReference.NS + locationDataName,
							OntologyReference.HAS_LAT,
							String.valueOf(nextPoint.getLat()),
							Triple.URI,
							Triple.LITERAL).getAsVector());
					
					newTriplePoint.add(new Triple(
							OntologyReference.NS + locationDataName,
							OntologyReference.HAS_LON,
							String.valueOf(nextPoint.getLng()),
							Triple.URI,
							Triple.LITERAL).getAsVector());
									
					if (i==0)
						kp.insert(newTriplePoint);
					else
						kp.update(newTriplePoint, oldTriplePoint);
					oldTriplePoint = newTriplePoint;

					try {
						Thread.sleep(Math.round(100/SimulationConfig.getInstance().getSimulationVelocity()));
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				
				kp.remove(currentAndNextStop);
				kp.remove(newTriplePoint);
				System.out.printf("ride %d terminated\n", ride+1);
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			//this barrier is needed to avoid that one bus start day i+1 before another bus has finished day i
			SimulationConfig.getInstance().waitForBarrier();
			
			System.out.printf("day %d terminated\n", day+1);
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}	
	}
	
	public int generateAscendingRealPerson(int realPerson, int maxSeats) {
		int availableSpace = maxSeats - realPerson;
		int ascendedRealPerson = (int)Math.ceil(random.nextInt(availableSpace) / 3.0);
		return ascendedRealPerson;
	}
		
	public int generateAscendingPayingPerson(int ascendedRealPerson) {
		int ascendedPayingPerson = random.nextInt(ascendedRealPerson);
		return ascendedPayingPerson;
	}
	
	public int generateDescendingRealPerson(int realPerson) {
		int descendedRealPerson = (int)Math.ceil(random.nextInt(realPerson) / 2.5);
		return descendedRealPerson;	
	}
		
	public int generateDescendingPayingPerson(int descendedRealPerson) {
		int descendedPayingPerson = random.nextInt(descendedRealPerson);
		return descendedPayingPerson;
	}
	
}

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
	private HashMap<String, Integer> stopsList;
	private Random random;
	private boolean circular;
	private boolean inspectorPresent;

	public Bus(String name, String line, String filenamePoints, String filenameStops, int days, int busRides) {
		this.name = name;
		this.days = days;
		this.line = line;
		this.busRides = busRides;
		this.filenamePoints = filenamePoints;
		this.filenameStops = filenameStops;
		stopsList = new HashMap<String, Integer>();
		random = new Random();
		if (line.equals("32"))
			circular = true;
		else
			circular = false;
		inspectorPresent = false;
			
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
		int fines;
		LatLng currentPoint;
		LatLng nextPoint;
		Double latNextPoint;
		Integer stopIndex;
		int c = 0;
		
		//get list of point
		BusPathParser parserForPoints;
	   	List<LatLng> listOfPoints;
		parserForPoints = new BusPathParser(filenamePoints);
		listOfPoints = parserForPoints.getListOfPoint();
		int listOfPointSize = listOfPoints.size();
		
		Vector<Vector<String>> newTripleToInsert = new Vector<>();
		Vector<Vector<String>> newTriplePoint = new Vector<>();
		Vector<Vector<String>> oldTriplePoint = new Vector<>();
		Vector<Vector<String>> newRide = new Vector<>();
		Vector<Vector<String>> oldRide = new Vector<>();
		Vector<Vector<String>> currentAndNextStop = new Vector<>();
		Vector<Vector<String>> oldCurrentAndNextStop = new Vector<>();
		
		List<LatLng> stopsPoints = BusStopManager.getInstance().getStopsPoints(line);
		int sizeOfStopsList = stopsPoints.size();

		//insert into hash map all the stop
		for (int i=0; i<sizeOfStopsList; i++)
			stopsList.put(stopsPoints.get(i).getLat()+"-"+stopsPoints.get(i).getLng(), i );
	
		//if the line is circular add one stop, since the last stop is the first stop
		if (circular)
			sizeOfStopsList++;
		
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
		String busLineName = "BusLine"+line;
		String busRideName = "BusRide";
		String busSensorNameGPS = name+"GPSSensor";
		String busSensorNameCameraEnter = name+"CameraEnterSensor";
		String busSensorNameCameraExit = name+"CameraExitSensor";
		String busSensorFareBoxEnter = name+"FareBoxEnterSensor";
		String busSensorFareBoxExit = name+"FareBoxExitSensor";
		String affluenceName = name + "Affluence";
		String getOnName = name + "GetOn";

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
				int currentStopIndex = 0;
				//insert ride data into SIB
				newRide = new Vector<>();
				//insert ride information only the first day
				if(day == 0) {
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
				}		
				
				for (int i = 0; i < listOfPointSize; i++) {
					currentPoint = listOfPoints.get(i);
					stopIndex = stopsList.get(currentPoint.getLat()+"-"+currentPoint.getLng());	
					newTriplePoint = new Vector<>();
					
					//check whether the next point is a bus stop
					if  (stopIndex != null){
						// check if this is the last stop, if it is the case don't do anything
						if (currentStopIndex < sizeOfStopsList-1 ) {							
						
							//person generation logic
							//PER ORA NON VIENE USATA LA SIB
      						ascendedRealPerson = generateAscendingRealPerson(realPerson, SimulationConfig.getInstance().getAutobusMaxSeats());
      						System.out.printf("\n Bus linea %s corsa %s, salite %d persone reali\n", line, ride, ascendedRealPerson);
							ascendedPayingPerson = generateAscendingPayingPerson(ascendedRealPerson);
							System.out.printf("\n Bus linea %s corsa %s, salite %d persone paganti\n", line, ride, ascendedPayingPerson);
							descendedRealPerson = generateDescendingRealPerson(realPerson, currentStopIndex, sizeOfStopsList);
							System.out.printf("\n Bus linea %s corsa %s, scese %d persone reali\n", line, ride, descendedRealPerson);
							descendedPayingPerson = generateDescendingPayingPerson(descendedRealPerson, currentStopIndex, sizeOfStopsList, payingPerson, realPerson); //payingPerson passato come parametro serve solo per far scendere tutti all'ultima fermata
							System.out.printf("\n Bus linea %s corsa %s, scese %d persone paganti\n", line, ride, descendedPayingPerson);
							realPerson += (ascendedRealPerson - descendedRealPerson);
							System.out.printf("\n Bus linea %s corsa %s, persone reali a bordo %d \n", line, ride, realPerson);
							payingPerson += (ascendedPayingPerson - descendedPayingPerson);
							System.out.printf("\n Bus linea %s corsa %s, persone paganti a bordo %d\n", line, ride, payingPerson);
							currentAndNextStop = new Vector<>();
							//in this case bus is not in transit
							newTriplePoint.add(new Triple(
							OntologyReference.NS + name,
							OntologyReference.IS_IN_TRANSIT,
							OntologyReference.FALSE,
							Triple.URI,
							Triple.URI).getAsVector());
							
							//update current stop
							
							BusStop currentBusStop = BusStopManager.getInstance().getBusStopFromLatLngString(line, currentPoint.getLat()+"-"+currentPoint.getLng());
							String currentBusStopUri = currentBusStop.getUri();
							Vector<String> currentStop = new Triple(
									OntologyReference.NS + name,
									OntologyReference.HAS_CURR_STOP,
									currentBusStopUri,
									Triple.URI,
									Triple.LITERAL).getAsVector();
							
							currentAndNextStop.add(currentStop);

							if (circular && currentStopIndex==sizeOfStopsList-2)
								nextPoint = stopsPoints.get(0);
							else
								nextPoint = stopsPoints.get(currentStopIndex+1);
							
							BusStop nextBusStop = BusStopManager.getInstance().getBusStopFromLatLngString(line, nextPoint.getLat()+"-"+nextPoint.getLng());

							//update next stop
							String nextBusStopUri = nextBusStop.getUri();
							Vector<String> nextStop = new Triple(
									OntologyReference.NS + name,
									OntologyReference.HAS_NEXT_STOP,
									nextBusStopUri,
									Triple.URI,
									Triple.LITERAL).getAsVector();
							
							currentAndNextStop.add(nextStop);
						
							if (inspectorPresent && !currentBusStop.isInspectorPresent()) {
								//if there was a ticket inspector on the bus he must get off
								currentBusStop.setInspectorPresent(true);
								inspectorPresent=false;
							}else if (!inspectorPresent && currentBusStop.isInspectorPresent()) {
								//if there wasn't a ticket inspector on the bus he must get on
								inspectorPresent=true;
								currentBusStop.setInspectorPresent(false);
							}
							if (inspectorPresent) {
								currentAndNextStop.add(new Triple(
										OntologyReference.NS + name,
										OntologyReference.IS_INSPECTOR_PRESENT,
										OntologyReference.TRUE,
										Triple.URI,
										Triple.URI).getAsVector());
							}else {
								currentAndNextStop.add(new Triple(
										OntologyReference.NS + name,
										OntologyReference.IS_INSPECTOR_PRESENT,
										OntologyReference.FALSE,
										Triple.URI,
										Triple.URI).getAsVector());
							}
							
							//update personData object
							currentAndNextStop.add(new Triple(
									OntologyReference.NS + personDataName ,
									OntologyReference.HAS_REAL_PERSON,
									String.valueOf(realPerson),
									Triple.URI,
									Triple.LITERAL).getAsVector());			
							
							currentAndNextStop.add(new Triple(
									OntologyReference.NS + personDataName ,
									OntologyReference.HAS_PAYING_PERSON,
									String.valueOf(payingPerson),
									Triple.URI,
									Triple.LITERAL).getAsVector());		
							
							//insert how many people are present
							kp.insert(
									OntologyReference.NS + affluenceName + c,
									OntologyReference.RDF_TYPE,
									OntologyReference.AFFLUEANCE,
									Triple.URI,
									Triple.URI);							
							
							kp.insert(
									OntologyReference.NS + affluenceName + c,
									OntologyReference.OF_REAL_PERSON,
									String.valueOf(realPerson),
									Triple.URI,
									Triple.LITERAL);

							kp.insert(
									OntologyReference.NS + affluenceName + c,
									OntologyReference.OF_PAYING_PERSON,
									String.valueOf(payingPerson),
									Triple.URI,
									Triple.LITERAL);
						
							kp.insert(
									OntologyReference.NS + affluenceName + c,
									OntologyReference.ON_LINE,
									busLineName,
									Triple.URI,
									Triple.URI);

							kp.insert(
									OntologyReference.NS + affluenceName + c,
									OntologyReference.ON_RIDE,
									busRideName + ride,
									Triple.URI,
									Triple.URI);
							
							kp.insert(
									OntologyReference.NS + affluenceName + c,
									OntologyReference.FROM_CURR_STOP,
									currentBusStopUri,
									Triple.URI,
									Triple.URI);
							
							kp.insert(
									OntologyReference.NS + affluenceName + c,
									OntologyReference.TO_NEXT_STOP,
									nextBusStopUri,
									Triple.URI,
									Triple.URI);	
							
							kp.insert(
									OntologyReference.NS + affluenceName + c,
									OntologyReference.HAS_SIMULATION_DAY,
									String.valueOf(day),
									Triple.URI,
									Triple.LITERAL);	
									
							//insert how many people get on 
							kp.insert(
									OntologyReference.NS + getOnName + c,
									OntologyReference.RDF_TYPE,
									OntologyReference.GET_ON_DATA,
									Triple.URI,
									Triple.URI);
							
							kp.insert(
									OntologyReference.NS + getOnName + c,
									OntologyReference.HAS_GETTING_ON,
									String.valueOf(ascendedRealPerson),
									Triple.URI,
									Triple.LITERAL);

							kp.insert(
									OntologyReference.NS + getOnName + c,
									OntologyReference.HAS_GETTING_ON_PAYING,
									String.valueOf(ascendedPayingPerson),
									Triple.URI,
									Triple.LITERAL);
						
							kp.insert(
									OntologyReference.NS + getOnName + c,
									OntologyReference.ON_LINE,
									busLineName,
									Triple.URI,
									Triple.URI);

							kp.insert(
									OntologyReference.NS + getOnName + c,
									OntologyReference.ON_RIDE,
									busRideName + ride,
									Triple.URI,
									Triple.URI);
							
							kp.insert(
									OntologyReference.NS + getOnName + c,
									OntologyReference.FROM_CURR_STOP,
									currentBusStopUri,
									Triple.URI,
									Triple.URI);
							
							kp.insert(
									OntologyReference.NS + getOnName + c,
									OntologyReference.TO_NEXT_STOP,
									nextBusStopUri,
									Triple.URI,
									Triple.URI);
							
							kp.insert(
									OntologyReference.NS + getOnName + c,
									OntologyReference.HAS_SIMULATION_DAY,
									String.valueOf(day),
									Triple.URI,
									Triple.LITERAL);
							
							if (currentStopIndex==0)
								kp.insert(currentAndNextStop);
							else
								kp.update(currentAndNextStop, oldCurrentAndNextStop);
							oldCurrentAndNextStop = currentAndNextStop;
							currentStopIndex++;
							c++;
						}else {
							System.out.println("last stop");
							descendedRealPerson = generateDescendingRealPerson(realPerson, currentStopIndex, sizeOfStopsList);
							System.out.printf("\n Bus linea %s corsa %s, scese %d persone reali\n", line, ride, descendedRealPerson);
							descendedPayingPerson = generateDescendingPayingPerson(descendedRealPerson, currentStopIndex, sizeOfStopsList, payingPerson, realPerson);
							System.out.printf("\n Bus linea %s corsa %s, scese %d persone paganti\n", line, ride, descendedPayingPerson);
						}

						if(inspectorPresent) {
							fines = realPerson - payingPerson;
							System.out.printf("\nIl controllore è salito e ha fatto %d multe\n", fines);
						}
						
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
							String.valueOf(currentPoint.getLat()),
							Triple.URI,
							Triple.LITERAL).getAsVector());
					
					newTriplePoint.add(new Triple(
							OntologyReference.NS + locationDataName,
							OntologyReference.HAS_LON,
							String.valueOf(currentPoint.getLng()),
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
		SimulationConfig.getInstance().waitThreadsEnd();
	}
	
	public int generateAscendingRealPerson(int realPerson, int maxSeats) {
		int availableSpace = maxSeats - realPerson;
		int ascendedRealPerson = 0;
		if(availableSpace > 0)
			ascendedRealPerson = (int)Math.ceil(random.nextInt(availableSpace) / 3.0);
		return ascendedRealPerson;
	}
		
	public int generateAscendingPayingPerson(int ascendedRealPerson) {
		int ascendedPayingPerson = 0;
		float ticketEvasion = SimulationConfig.getInstance().getTicketEvasion();
		if(ascendedRealPerson > 0) {
			for(int i = 0; i<ascendedRealPerson; i++) 
				if(random.nextInt(101) >= ticketEvasion)
					ascendedPayingPerson++;
		}
		return ascendedPayingPerson;
	}
	
	public int generateDescendingRealPerson(int realPerson, int currentStopIndex, int sizeOfStopsList) {
		int descendedRealPerson = 0;
		if((realPerson > 0) && (currentStopIndex < sizeOfStopsList-1))
			descendedRealPerson = (int)Math.ceil(random.nextInt(realPerson) / 2.5);
		if(currentStopIndex == sizeOfStopsList-1)
			descendedRealPerson = realPerson;
		return descendedRealPerson;	
	}
		
	public int generateDescendingPayingPerson(int descendedRealPerson, int currentStopIndex, int sizeOfStopsList, int payingPerson, int realPerson) {
		int descendedPayingPerson = 0;
		float ticketEvasion = SimulationConfig.getInstance().getTicketEvasion();
		if((descendedRealPerson > 0) && (currentStopIndex < sizeOfStopsList-1))
			if(realPerson == payingPerson) 
				descendedPayingPerson = descendedRealPerson;
			else {
				for(int i = 0; i<descendedRealPerson && descendedPayingPerson<payingPerson; i++) { 
				if(random.nextInt(101) >= ticketEvasion)
					descendedPayingPerson++;
				}
			}
		if((currentStopIndex == sizeOfStopsList-1))
			descendedPayingPerson = payingPerson;
		return descendedPayingPerson;
	}
	
}

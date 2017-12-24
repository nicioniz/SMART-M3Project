package utils;

public class OntologyReference {

	private OntologyReference(){};
	
	private static final String RDF = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
	public static final String RDF_TYPE = RDF + "type";
	
	//namespace
	public static final String NS = "http://project/IoES1718#";
	
	//bus class with its properties
	public static final String BUS = NS + "Bus";
	public static final String HAS_LOCATION_DATA = NS + "hasLocatioData";
	public static final String HAS_ID = NS + "hasId";
	public static final String ON_LINE = NS + "onLine";
	public static final String HAS_SENSOR = NS + "hasSensor";
	public static final String HAS_MAX_SEATS = NS + "hasMaxSeats";
	public static final String HAS_CURR_STOP = NS + "hasCurrStop";
	public static final String HAS_NEXT_STOP = NS + "hasNextStop";
	public static final String IS_IN_TRANSIT = NS + "isInTransit";
	public static final String IS_INSPECTOR_PRESENT = NS + "isInspectorPresent";
	public static final String HAS_PERSON_DATA = NS + "hasPersonData";
	public static final String ON_RIDE = NS + "onRide";

	//LocationDAta class
	public static final String LOCATION_DATA = NS + "LocationData";
	public static final String HAS_LAT = NS + "hasLat";
	public static final String HAS_LON = NS + "hasLon";
	
	//BusSTOP class
	public static final String BUS_STOP = NS + "BusStop";
	public static final String HAS_NAME = NS + "hasName";
	public static final String HAS_WAITING_PERSON = NS + "hasWaitingPerson";
	
	//Boolean class
	public static final String BOOLEAN = NS + "Boolean";
	
	//Sensor class
	public static final String SENSOR = NS + "Sensor";
	
	//Ride class
	public static final String RIDE = NS + "Ride";
	public static final String AT_TIME = NS + "atTime";

	//Affluence class
	public static final String AFFLUEANCE = NS + "Affluence";
	public static final String OF_REAL_PERSON = NS + "ofRealPerson";
	public static final String OF_PAYING_PERSON = NS + "ofPayingPerson";

	//BusLine class
	public static final String BUS_LINE = NS + "BusLine";
	public static final String HAS_NUMBER = NS + "hasNumber";
	
	//SensorType class
	public static final String SENSOR_TYPE = NS + "SensorType";
}

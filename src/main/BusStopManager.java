package main;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.teamdev.jxmaps.LatLng;

import parser.BusStopParser;

public class BusStopManager {

	private static BusStopManager instance = null;
	
	private HashMap<String, List<BusStop>> busStopLists = new HashMap<>(); 
	private HashMap<String, HashMap<String, BusStop>> busStopHashmaps = new HashMap<>();
	
	private boolean initCalled = false;
	
	private BusStopManager(){
		
//	Following lines of code are more scalable, but can not iterate over a directory within Jar
//	(a seprate folder with gpx near to Jar is requested)	
		
//		File gpxDir = new File("gpx");
//		if(!gpxDir.isDirectory());
////			throw new Exception("ERROR: gpx directory not found");
//		
//		List<String> fileNames = Arrays.asList(gpxDir.list()).stream()
//						.filter(s -> s.matches("^bus\\d+StopList.gpx$"))
//						.map(s -> "gpx/" + s)
//						.collect(Collectors.toList());
//		for(String s : fileNames)
//			addLine(s);
		
		addLine("/gpx/bus11StopList.gpx");
		addLine("/gpx/bus20StopList.gpx");
		addLine("/gpx/bus32StopList.gpx");
	}
	
	private void addLine(String lineFileName) {
		String lineNumber = lineFileName.replaceAll("[^\\d]", "");
		BusStopParser p = new BusStopParser(lineFileName);
		List<BusStop> stops = p.getBusStops();
		stops = stops.stream().map(bs->new BusStop(bs.getName() + " (" + lineNumber + ")", bs.getId(), bs.getLocation())).collect(Collectors.toList());
		busStopLists.put(lineNumber, stops);
		HashMap<String, BusStop> hMBusStop = new HashMap<>();
		stops.forEach(bs -> hMBusStop.put(bs.getLocationString(), bs));
		busStopHashmaps.put(lineNumber, hMBusStop);
	}
	
	public static BusStopManager getInstance() {
		if(instance == null)
			instance = new BusStopManager();
		return instance;
	}
	
	public void init() {
		if(initCalled)
			return;
		
		busStopLists.values().stream().forEach(lbs -> lbs.stream().forEach(bs -> bs.init()));
		List<BusStop> allBS = Stream.concat(busStopLists.get("11").stream(), Stream.concat(busStopLists.get("20").stream(), busStopLists.get("32").stream())).collect(Collectors.toList()); 
		Collections.shuffle(allBS);
		allBS.stream().forEach(bs -> bs.generateInspector());
//		Stream<String> out = Stream.of(a, b, c)
//			      .reduce(Stream::concat)
//			      .orElseGet(Stream::empty);
//			  out.forEach(System.out::println);
		initCalled = true;
	}

	public synchronized BusStop getBusStopFromLatLngString(String lineNumber, String latLng) {
		return busStopHashmaps.get(lineNumber).get(latLng.trim().replace(" ", ""));
	}
	
	/**
	 * 
	 * @param lineNumber
	 * @param latLng
	 * @return ATTENTION!!! No control inside this method.
	 * DO NOT call with the last BusStop of a line!!!
	 */
	public synchronized BusStop getNextBusStopFromActualLatLngString(String lineNumber, String latLng) {
		
		BusStop actual = getBusStopFromLatLngString(lineNumber, latLng);
		int actualIndex = busStopLists.get(lineNumber).indexOf(actual);
		return busStopLists.get(lineNumber).get(actualIndex + 1);
	}
	
	public synchronized List<LatLng> getStopsPoints(String lineNumber) {
		List<LatLng> res = new ArrayList<>();
		busStopLists.get(lineNumber).stream().forEach(bs -> res.add(bs.getLocation()));
		return res;
	}
}

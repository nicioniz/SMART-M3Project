package main;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import com.teamdev.jxmaps.LatLng;

import parser.BusStopParser;

public class BusStopManager {

	private static BusStopManager instance = null;
	
	private HashMap<String, List<BusStop>> busStopLists = new HashMap<>(); 
	private HashMap<String, HashMap<String, BusStop>> busStopHashmaps = new HashMap<>();
	
	private boolean initCalled = false;
	
	private BusStopManager(){
		
		File gpxDir = new File("gpx");
		if(!gpxDir.isDirectory());
//			throw new Exception("ERROR: gpx directory not found");
		
		List<String> fileNames = Arrays.asList(gpxDir.list()).stream()
						.filter(s -> s.matches("^bus\\d+StopList.gpx$"))
						.collect(Collectors.toList());
		for(String s : fileNames)
			addLine(s);	
		
	}
	
	private void addLine(String lineFileName) {
		String lineNumber = lineFileName.replaceAll("[^\\d]", "");
		BusStopParser p = new BusStopParser(lineFileName);
		List<BusStop> stops = p.getBusStops();
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
		initCalled = true;
	}

	public BusStop getBusStopFromLatLngString(String lineNumber, String latLng) {
		return busStopHashmaps.get(lineNumber).get(latLng);
	}
	
	/**
	 * 
	 * @param lineNumber
	 * @param latLng
	 * @return ATTENTION!!! No control inside this method.
	 * DO NOT call with the last BusStop of a line!!!
	 */
	public BusStop getNextBusStopFromActualLatLngString(String lineNumber, String latLng) {
		BusStop actual = getBusStopFromLatLngString(lineNumber, latLng);
		int actualIndex = busStopLists.get(lineNumber).indexOf(actual);
		return busStopLists.get(lineNumber).get(actualIndex + 1);
	}
	
	public List<LatLng> getStopsPoints(String lineNumber) {
		List<LatLng> res = new ArrayList<>();
		busStopLists.get(lineNumber).stream().forEach(bs -> res.add(bs.getLocation()));
		return res;
	}
}

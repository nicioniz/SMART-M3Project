package parser;

import java.util.List;

import main.BusStop;

public class BusStopParser extends GenericParser {

	private static SAXHandlerGPXBusStop handler = new SAXHandlerGPXBusStop();
	
	public BusStopParser(String fileName) {
		super(fileName, handler);
	}
	
	public List<BusStop> getBusStops(){
		return handler.getBusStop();
	}
	
}

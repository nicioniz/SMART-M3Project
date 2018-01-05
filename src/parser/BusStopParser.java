package parser;

import java.util.List;

import main.BusStop;

public class BusStopParser extends GenericParser {

	private SAXHandlerGPXBusStop handler;
	
	public BusStopParser(String fileName) {
		super();
		handler = new SAXHandlerGPXBusStop();
		this.setContentHandler(handler);
		this.parse(fileName);
	}
	
	public List<BusStop> getBusStops(){
		return handler.getBusStop();
	}
	
}

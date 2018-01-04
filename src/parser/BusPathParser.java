package parser;

import java.util.List;

import com.teamdev.jxmaps.LatLng;


public class BusPathParser extends GenericParser{
	
	private SAXHandlerGPX handler;
	
	public BusPathParser(String fileName) {
		super();
		handler = new SAXHandlerGPX();
		this.setContentHandler(handler);
		this.parse(fileName);
	}
	
	public List<LatLng> getListOfPoint(){
		return handler.getPoints();
	}
}
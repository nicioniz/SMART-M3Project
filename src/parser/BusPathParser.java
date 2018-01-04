package parser;

import java.util.List;

import com.teamdev.jxmaps.LatLng;


public class BusPathParser extends GenericParser{
	
	private static SAXHandlerGPX handler = new SAXHandlerGPX();
	
	public BusPathParser(String fileName) {
		super(fileName, handler);
	}
	
	public List<LatLng> getListOfPoint(){
		return handler.getPoints();
	}
}
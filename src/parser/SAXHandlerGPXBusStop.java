package parser;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.teamdev.jxmaps.LatLng;

import main.BusStop;

public class SAXHandlerGPXBusStop extends DefaultHandler {

	private List<BusStop> busStop = new ArrayList<>();
	
	private boolean inTrkpt = false;
	private boolean inName = false;
	private LatLng actualLocation;
	private String actualName;
	
	private int i = new Random(System.currentTimeMillis()).nextInt(1000);
	
	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		switch (localName) {
		case "trkpt": 
			actualLocation = new LatLng(Double.parseDouble(attributes.getValue("lat")), Double.parseDouble(attributes.getValue("lon")));
			inTrkpt = true;
			break;
		case "name":
			inName = true;
			break;

		default:
		}
	}
	
	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		switch (localName) {
		case "trkpt": 
			inTrkpt = false;
			busStop.add(new BusStop(actualName, "" + (i++), actualLocation));
			break;
		case "name":
			inName = false;
			break;

		default:
		}
	}
	
	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		if(inName && inTrkpt)
			actualName = new String(ch, start, length).trim();
	}
	
	public List<BusStop> getBusStop() {
		return busStop;
	}
}

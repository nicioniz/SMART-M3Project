package parser;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import com.teamdev.jxmaps.LatLng;


public class SAXHandlerGPX extends DefaultHandler {

	private List<LatLng> points = new ArrayList<LatLng>();

	@Override
	public void startElement (String namespaceURI, String localName,
			String rawName, Attributes atts) {
		switch (localName) {
		case "trkpt": 
			double dlat = Double.parseDouble(atts.getValue("lat"));
			double dlon = Double.parseDouble(atts.getValue("lon"));
			points.add(new LatLng(dlat,dlon));
			break;

		default:
		}
	}
	
	public List<LatLng> getPoints() {
		return points;
	}
}
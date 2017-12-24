package parser;

import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.ContentHandler;
import org.xml.sax.XMLReader;

import com.teamdev.jxmaps.LatLng;


public class Parser {
	
	private List<LatLng> points;

	public Parser(String fileName) {
		SAXParserFactory spf = SAXParserFactory.newInstance();

		try{
			spf.setNamespaceAware(true);
			spf.setValidating(true);

			SAXParser saxParser = spf.newSAXParser();
			XMLReader xmlReader = saxParser.getXMLReader();
			ContentHandler handler = new SAXHandlerGPX();
			ErrorChecker errors = new ErrorChecker();

			xmlReader.setErrorHandler(errors);
			xmlReader.setFeature("http://apache.org/xml/features/validation/schema", true);
			xmlReader.setContentHandler(handler);
			xmlReader.parse(fileName);
			points = ((SAXHandlerGPX) handler).getPoints();

		}catch (Exception e) {
			System.err.println(e.getMessage());
			System.exit(1);
		}
	}
	
	public List<LatLng> getListOfPoint(){
		return points;
	}
}
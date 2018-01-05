package parser;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.ContentHandler;
import org.xml.sax.XMLReader;

public class GenericParser {

	private XMLReader xmlReader;
	
	public GenericParser() {
		SAXParserFactory spf = SAXParserFactory.newInstance();

		try{
			spf.setNamespaceAware(true);
			spf.setValidating(true);

			SAXParser saxParser = spf.newSAXParser();
			xmlReader = saxParser.getXMLReader();
			ErrorChecker errors = new ErrorChecker();

			xmlReader.setErrorHandler(errors);
			xmlReader.setFeature("http://apache.org/xml/features/validation/schema", true);

		}catch (Exception e) {
			System.err.println(e.getMessage());
			System.exit(1);
		}
	}
	
	public void setContentHandler(ContentHandler handler) {
		xmlReader.setContentHandler(handler);
	}
	
	public void parse(String fileName) {
		try {
			xmlReader.parse(fileName);
		}catch (Exception e) {
			System.err.println(e.getMessage());
			System.exit(1);
		}
	}
	
}

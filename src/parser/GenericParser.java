package parser;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.ContentHandler;
import org.xml.sax.XMLReader;

public class GenericParser {

	public GenericParser(String fileName, ContentHandler handler) {
		SAXParserFactory spf = SAXParserFactory.newInstance();

		try{
			spf.setNamespaceAware(true);
			spf.setValidating(true);

			SAXParser saxParser = spf.newSAXParser();
			XMLReader xmlReader = saxParser.getXMLReader();
			ErrorChecker errors = new ErrorChecker();

			xmlReader.setErrorHandler(errors);
			xmlReader.setFeature("http://apache.org/xml/features/validation/schema", true);
			xmlReader.setContentHandler(handler);
			xmlReader.parse(fileName);

		}catch (Exception e) {
			System.err.println(e.getMessage());
			System.exit(1);
		}
	}
	
	
}

package gui;

import java.util.Vector;

import javax.swing.JTextArea;

import sofia_kp.KPICore;
import sofia_kp.SIBResponse;
import sofia_kp.SSAP_sparql_response;
import utils.OntologyReference;
import utils.SIBConfiguration;

public class BusRuntimeVisualizerThread extends Thread {

	private int refreshRate;
	private JTextArea txtResult;
	private KPICore kp;
	
	public BusRuntimeVisualizerThread(int refreshRate, JTextArea txtResult) {
		super();
		this.refreshRate = refreshRate;
		this.txtResult = txtResult;
	}
	
	
	@Override
	public void run() {
		kp = new KPICore(SIBConfiguration.getInstance().getHost(),
				SIBConfiguration.getInstance().getPort(),
				SIBConfiguration.getInstance().getSmartSpaceName());
		
		if(!kp.join().isConfirmed())
			System.err.println ("Error joining the SIB");
		else
			System.out.println ("BusRuntimeVisualizerThread (#" + refreshRate + " msec) joined SIB correctly");
		
		
		while(!isInterrupted()) {
			StringBuilder sb = new StringBuilder();
			appendBusInfo("11", sb);
			appendBusInfo("20", sb);
			appendBusInfo("32", sb);
			txtResult.setText(sb.toString());
			try {
				sleep(refreshRate);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				System.out.println ("BusRuntimeVisualizerThread (#" + refreshRate + " msec) ending...");
				return;
			}
		}
		System.out.println ("BusRuntimeVisualizerThread (#" + refreshRate + " msec) ending...");
		
		
	}
	
	private void appendBusInfo(String lineNumber, StringBuilder sb) {
		String query = 
				"select ?pd ?property ?value " +
				"where { " +
						"{ " +
							"<" + OntologyReference.NS + "BUS" + lineNumber + "> <" + OntologyReference.HAS_PERSON_DATA + "> ?pd . " +
							"?pd ?property ?value " +
						"} " +
						"UNION " +
						"{ " +
							"<" + OntologyReference.NS + "BUS" + lineNumber + "> <" + OntologyReference.IS_INSPECTOR_PRESENT + "> ?value " +
						"} " +
				"}";
		
		SIBResponse resp = kp.querySPARQL(query);
		
		SSAP_sparql_response newResults = resp.sparqlquery_results;
		if(newResults == null)
			return;
		Vector<Vector<String[]>> data = newResults.getResults();
//		if(indSequence.equals("0"))
//			data = oldResults.getResults();
			
		int payingPerson = -42, realPerson = -42;
		boolean isInspectorPresent = false;
		
		for(Vector<String[]> riga : data) {
			String value = riga.get(2)[2];
			String property = riga.get(1)[2];
			if(property == null)
				isInspectorPresent = value.equals(OntologyReference.TRUE);
			else {
				if(property.equals(OntologyReference.HAS_REAL_PERSON))
					realPerson = Integer.parseInt(value);
				if(property.equals(OntologyReference.HAS_PAYING_PERSON))
					payingPerson = Integer.parseInt(value);
			}
		}
			
		
		//lineSeparator isn't useful: infoWindow show everything on single line
		if(realPerson == -42 && payingPerson == -42 && isInspectorPresent == false)
			return;
		sb.append("LINE ");
		sb.append(lineNumber);
		sb.append(System.lineSeparator());
		sb.append("Real person: ");
		sb.append(realPerson);
		sb.append(System.lineSeparator());
		sb.append("Paying person: ");
		sb.append(payingPerson);
		sb.append(System.lineSeparator());
		sb.append("Inspector presence: ");
		sb.append(isInspectorPresent);
		sb.append(System.lineSeparator());
		sb.append(System.lineSeparator());
		
		
	}
	
}

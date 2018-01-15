package main;
import com.teamdev.jxmaps.LatLng;

import java.io.FileNotFoundException;
import java.util.Vector;
import com.teamdev.jxmaps.Marker;
import sofia_kp.SSAP_sparql_response;
import sofia_kp.iKPIC_subscribeHandler2;


public class HandlerSubscriptionLocationData implements iKPIC_subscribeHandler2 {

	private Marker m;
	private BusMap map;
	private String lineNumber;
	private String busColor;
	
	
	public HandlerSubscriptionLocationData(BusMap map, String lineNumber, String busColor) {
		this.map = map;
		this.lineNumber = lineNumber;
		this.busColor = busColor;
	}

	@Override
	public void kpic_RDFEventHandler(Vector<Vector<String>> newTriples,
			Vector<Vector<String>> oldTriples, String indSequence, String subID) {

		String temp = "\nNotification " + indSequence + " id = " + subID +"\n";
		for(int i = 0; i < newTriples.size(); i++ )
		{
			temp+="New triple s =" + newTriples.elementAt(i).elementAt(0) + "  + predicate" + newTriples.elementAt(i).elementAt(1) + "object =" + newTriples.elementAt(i).elementAt(2) +"\n";
		}
		for(int i = 0; i < oldTriples.size(); i++ )
		{
			temp+="Obsolete triple s =" + oldTriples.elementAt(i).elementAt(0) + "  + predicate" + oldTriples.elementAt(i).elementAt(1) + "object =" + oldTriples.elementAt(i).elementAt(2) + "\n";
		}
	//	System.out.println(temp);
		
		
		
	}

	@Override
	public void kpic_SPARQLEventHandler(SSAP_sparql_response newResults, SSAP_sparql_response oldResults, String indSequence, String subID) {
	//	System.out.println("\nNotification " + indSequence  +" id = " + subID + "\n");
		
		if (newResults != null)
		{
			Vector<Vector<String[]>> data = newResults.getResults();
			for(Vector<String[]> riga : data) {
		//		System.out.println("Location data:" + riga.get(0)[2] + "has lat: " + riga.get(1)[2]+ " has lon: " + riga.get(2)[2]); 	
				String lat = riga.get(1)[2]+"0";
				String lon = riga.get(2)[2]+"0";
				try {
					if (Integer.parseInt(indSequence) == 1) {
						LatLng point = new LatLng(Double.parseDouble(lat),Double.parseDouble(lon));
						m = map.addBus(point, lineNumber, busColor);
					}
					else 
						map.moveBus(m, new LatLng(Double.parseDouble(lat),Double.parseDouble(lon)));
				} catch (NumberFormatException | FileNotFoundException e) {
					e.printStackTrace();
				}
			}
			
		}
		
		
		
		if (oldResults != null)
		{
	//		System.out.println("obsolete: \n " + oldResults.print_as_string());
			
		}
	}

	@Override
	public void kpic_UnsubscribeEventHandler(String sub_ID) {
		System.out.println("Unsubscribed " + sub_ID);
	

	}

	@Override
	public void kpic_ExceptionEventHandler(Throwable SocketException) {
		System.out.println("Exception in subscription handler " + SocketException.toString());
		

	}

}

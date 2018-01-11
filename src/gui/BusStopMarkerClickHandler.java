package gui;

import com.teamdev.jxmaps.InfoWindow;
import com.teamdev.jxmaps.Map;
import com.teamdev.jxmaps.MapMouseEvent;
import com.teamdev.jxmaps.Marker;
import com.teamdev.jxmaps.MouseEvent;

import main.BusStop;

public class BusStopMarkerClickHandler extends MapMouseEvent {

	private BusStop bs;
	private Map map;
	private Marker marker;
	
	public BusStopMarkerClickHandler(BusStop bs, Map map, Marker marker) {
		this.bs = bs;
		this.map = map;
		this.marker = marker;
	}
	
	@Override
	public void onEvent(MouseEvent arg0) {
		// Creating info window
        InfoWindow infoWindow = new InfoWindow(map);
        
        infoWindow.setContent("Bus Stop: " + bs.getName() + System.lineSeparator() + "Inspector Presence: " + bs.isInspectorPresent());
        // Showing info window under the marker
        infoWindow.open(map, marker);
	}

}

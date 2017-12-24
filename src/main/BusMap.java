package main;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.Semaphore;

import com.teamdev.jxmaps.ControlPosition;
import com.teamdev.jxmaps.Icon;
import com.teamdev.jxmaps.LatLng;
import com.teamdev.jxmaps.MapOptions;
import com.teamdev.jxmaps.MapStatus;
import com.teamdev.jxmaps.MapTypeControlOptions;
import com.teamdev.jxmaps.Marker;
import com.teamdev.jxmaps.Polyline;
import com.teamdev.jxmaps.PolylineOptions;
import com.teamdev.jxmaps.swing.MapView;


@SuppressWarnings("serial")
public class BusMap extends MapView{
	
	private Semaphore s;
    public BusMap() {
        // Setting of a ready handler to MapView object. onMapReady will be called when map initialization is done and
        // the map object is ready to use. Current implementation of onMapReady customizes the map object.
    	s = new Semaphore(0);
    	
    	setOnMapReadyHandler(this::onMapReady);
    }
    
    public void addBusline(List<LatLng> points , String color) {
    	// Creating a path (array of coordinates) that represents a polyline
    	LatLng[] path = new LatLng[points.size()];
    	path = points.toArray(path);
        
        // Creating a new busline object
        Polyline polyline = new Polyline(getMap());
        // Initializing the busline with created path
        polyline.setPath(path);
        // Creating a busline options object
        PolylineOptions options = new PolylineOptions();
                        
        
        // Setting geodesic property value
        options.setGeodesic(true);
        // Setting stroke color value
        options.setStrokeColor(color);
        // Setting stroke opacity value
        options.setStrokeOpacity(1.0);
        // Setting stroke weight value
        options.setStrokeWeight(2.0);
        // Applying options to the busline
        polyline.setOptions(options);
    }
    
    public Marker addBus(LatLng ll) throws FileNotFoundException {
    	Marker marker = new Marker(getMap());
    	
    	Icon icon = new Icon();
    	InputStream inputstream = new FileInputStream("./res/bus.png");
    	icon.loadFromStream(inputstream, "png");
    	marker.setIcon(icon);
    	
        marker.setPosition(ll);
        return marker;
    }
    
    public void moveBus(Marker m, LatLng ll) {
    	m.setPosition(ll);
    }
    
    public void waitForMap() throws InterruptedException {
    	s.acquire();
    	s.release();
    }
    
    public void onMapReady(MapStatus status) {
        // Check if the map is loaded correctly
        if (status == MapStatus.MAP_STATUS_OK) {
            // Creating a map options object
        	
            MapOptions mapOptions = new MapOptions();
            // Creating a map type control options object
            MapTypeControlOptions controlOptions = new MapTypeControlOptions();
            // Changing position of the map type control
            controlOptions.setPosition(ControlPosition.TOP_RIGHT);
            // Setting map type control options
            mapOptions.setMapTypeControlOptions(controlOptions);
            // Setting map options
            getMap().setOptions(mapOptions);
            // Setting the map center
            getMap().setCenter(new LatLng(44.493889, 11.342778));
            // Setting initial zoom value
            getMap().setZoom(14.0);      
            
            s.release(); 
        }
    }
}

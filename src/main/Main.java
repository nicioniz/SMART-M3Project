package main;

import gui.SimConfigurationFrame;
import simulationConfiguration.SimulationConfig;
import sofia_kp.KPICore;
import utils.OntologyReference;
import utils.SIBConfiguration;
import utils.Triple;

public class Main {

	private static KPICore kp;
	
	public static void main(String[] args) throws InterruptedException {
		initializer();
		SimConfigurationFrame configurator = new SimConfigurationFrame();
		configurator.setVisible(true);
		SimulationConfig.getInstance().getStartSimulationSemaphore().acquire();
		SimulationConfig.getInstance().waitThreadsEnd();
		StatisticsManager statistics = StatisticsManager.getInstance();
		System.out.println(statistics.economicSummary());
	}

	private static void initializer() {
		kp = new KPICore(SIBConfiguration.getInstance().getHost(),
				SIBConfiguration.getInstance().getPort(),
				SIBConfiguration.getInstance().getSmartSpaceName());
		
		if(!kp.join().isConfirmed())
			System.err.println ("MAIN Initializer: Error joining the SIB");
		else
			System.out.println ("MAIN Initializer: Joined SIB correctly");
		insertBooleanInstances();
		insertSensorTypeInstances();
		
		//generate BusStopManager by calling for the first time is getInstance() method
		BusStopManager.getInstance();
	}

	private static void insertSensorTypeInstances() {
		kp.insert(
				OntologyReference.CAMERA,
				OntologyReference.RDF_TYPE,
				OntologyReference.SENSOR_TYPE,
				Triple.URI,
				Triple.URI);		
		kp.insert(
				OntologyReference.GPS,
				OntologyReference.RDF_TYPE,
				OntologyReference.SENSOR_TYPE,
				Triple.URI,
				Triple.URI);
		kp.insert(
				OntologyReference.FAREBOX,
				OntologyReference.RDF_TYPE,
				OntologyReference.SENSOR_TYPE,
				Triple.URI,
				Triple.URI);
	}

	private static void insertBooleanInstances() {
		kp.insert(
				OntologyReference.TRUE,
				OntologyReference.RDF_TYPE,
				OntologyReference.BOOLEAN,
				Triple.URI,
				Triple.URI);
		kp.insert(
				OntologyReference.FALSE,
				OntologyReference.RDF_TYPE,
				OntologyReference.BOOLEAN,
				Triple.URI,
				Triple.URI);
	}
	
}

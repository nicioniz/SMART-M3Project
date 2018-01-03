package simulationConfiguration;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class SimulationConfig {

	private double simulationVelocity = 1;
	private int simulationDays;
	private int busRides;
	private CyclicBarrier barrier;
	private int maxInspectors;
	private int inspectorPresencePercentageProbability;

	private static SimulationConfig instance = null;
	
	private SimulationConfig() {}
	
	public static SimulationConfig getInstance() {
		if(instance == null)
			instance = new SimulationConfig();
		return instance;
	}
	
	public void setWaitingThreadForBarrier(int numThreads) {
		barrier = new CyclicBarrier(numThreads);
	}
	
	public void waitForBarrier() {
		try {
			barrier.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (BrokenBarrierException e) {
			e.printStackTrace();
		}
	}
	
	public double getSimulationVelocity() {
		return simulationVelocity;
	}

	public void setSimulationVelocity(double simulationVelocity) {
		this.simulationVelocity = simulationVelocity;
	}
	
	public void setSimulationDays(int days) {
		this.simulationDays = days;
	}
	
	public int getSimulationDays() {
		return simulationDays;
	}
	
	public void setBusRides(int rides) {
		this.busRides = rides;
	}
	
	public int getBusRides() {
		return busRides;
	}
		
	public int getMaxInspectors() {
		return maxInspectors;
	}

	public void setMaxInspectors(int maxInspectors) {
		this.maxInspectors = maxInspectors;
	}

	public int getInspectorPresencePercentageProbability() {
		return inspectorPresencePercentageProbability;
	}

	public void setInspectorPresencePercentageProbability(int inspectorPresencePercentageProbability) {
		this.inspectorPresencePercentageProbability = inspectorPresencePercentageProbability;
	}

}

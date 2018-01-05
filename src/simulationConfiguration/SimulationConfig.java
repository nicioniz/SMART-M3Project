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
	private int numberOfPresentInsectors = 0;
	private int autobusMaxSeats = 80;
	private float ticketPrice;
	private float ticketEvasion;
	private int inspectorCost;
	private int inspectors;
	private float fine;
	private int veichleCost;
	
	
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
	
	public int getAutobusMaxSeats() {
		return autobusMaxSeats;
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

	public int getNumberOfPresentInsectors() {
		return numberOfPresentInsectors;
	}
	
	public float getTicketPrice() {
		return ticketPrice;
	}

	public void setTicketPrice(float ticketPrice) {
		this.ticketPrice = ticketPrice;
	}

	public float getTicketEvasion() {
		return ticketEvasion;
	}

	public void setTicketEvasion(float ticketEvasion) {
		this.ticketEvasion = ticketEvasion;
	}

	public int getInspectorCost() {
		return inspectorCost;
	}

	public void setInspectorCost(int inspectorCost) {
		this.inspectorCost = inspectorCost;
	}

	public int getInspectors() {
		return inspectors;
	}

	public void setInspectors(int inspectors) {
		this.inspectors = inspectors;
	}

	public float getFine() {
		return fine;
	}

	public void setFine(float fine) {
		this.fine = fine;
	}

	public int getVeichleCost() {
		return veichleCost;
	}

	public void setVeichleCost(int veichleCost) {
		this.veichleCost = veichleCost;
	}

	public boolean addInspector(int numberOfAddedInspectors) {
		if(numberOfPresentInsectors + numberOfAddedInspectors <= maxInspectors) {
			numberOfPresentInsectors += numberOfAddedInspectors;
			return true;
		}
		return false;
	}
	
	public boolean addInspector() {
		return addInspector(1);
	}
}

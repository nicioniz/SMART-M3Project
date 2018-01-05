package gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.io.FileNotFoundException;
import java.text.ParseException;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.border.EmptyBorder;

import main.Bus;
import main.BusMap;
import main.BusVisualizerAggregator;
import main.BusStopManager;
import simulationConfiguration.SimulationConfig;

@SuppressWarnings("serial")
public class SimConfigurationFrame extends JFrame {

	private JPanel simulationDaysContentPane;
	private BusMap busMap;
	private JCheckBox lineNo32CheckBox, lineNo20CheckBox;
	private JLabel lblSimulationVelocity;
	private JSlider velocitySlider;
	private JLabel simulationDaysLabel;
	private JLabel busRidesLabel;
	private JCheckBox lineNo11CheckBox;
	private JSpinner simulationDaysSpinner;
	private JSpinner busRidesSpinner;
	
	public SimConfigurationFrame() {
		setResizable(false);
		setTitle("SimConfiguration");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 397, 316);
		simulationDaysContentPane = new JPanel();
		simulationDaysContentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(simulationDaysContentPane);
		
		GridBagLayout gbl_simulationDaysContentPane = new GridBagLayout();
		gbl_simulationDaysContentPane.columnWidths = new int[]{0, 0, 0, 0};
		gbl_simulationDaysContentPane.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		gbl_simulationDaysContentPane.columnWeights = new double[]{0.0, 0.0, 1.0, Double.MIN_VALUE};
		gbl_simulationDaysContentPane.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		simulationDaysContentPane.setLayout(gbl_simulationDaysContentPane);
		
		lblSimulationVelocity = new JLabel("Simulation velocity");
		GridBagConstraints gbc_lblSimulationVelocity = new GridBagConstraints();
		gbc_lblSimulationVelocity.insets = new Insets(0, 0, 5, 0);
		gbc_lblSimulationVelocity.gridx = 2;
		gbc_lblSimulationVelocity.gridy = 2;
		simulationDaysContentPane.add(lblSimulationVelocity, gbc_lblSimulationVelocity);
		
		lineNo32CheckBox = new JCheckBox("Line No. 32");
		lineNo32CheckBox.setSelected(true);
		GridBagConstraints gbc_lineNo32CheckBox = new GridBagConstraints();
		gbc_lineNo32CheckBox.insets = new Insets(0, 0, 5, 5);
		gbc_lineNo32CheckBox.gridx = 0;
		gbc_lineNo32CheckBox.gridy = 2;
		simulationDaysContentPane.add(lineNo32CheckBox, gbc_lineNo32CheckBox);
		
		lineNo20CheckBox = new JCheckBox("Line No. 20");
		lineNo20CheckBox.setSelected(true);
		GridBagConstraints gbc_lineNo20CheckBox = new GridBagConstraints();
		gbc_lineNo20CheckBox.insets = new Insets(0, 0, 5, 5);
		gbc_lineNo20CheckBox.gridx = 0;
		gbc_lineNo20CheckBox.gridy = 3;
		simulationDaysContentPane.add(lineNo20CheckBox, gbc_lineNo20CheckBox);
		
		velocitySlider = new JSlider();
		velocitySlider.setToolTipText("Simulation velocity (from 0.1x to 2.0x)");
		velocitySlider.setValue(15);
		velocitySlider.setMinimum(1);
		velocitySlider.setMaximum(20);
		GridBagConstraints gbc_velocitySlider = new GridBagConstraints();
		gbc_velocitySlider.insets = new Insets(0, 0, 5, 0);
		gbc_velocitySlider.gridx = 2;
		gbc_velocitySlider.gridy = 3;
		simulationDaysContentPane.add(velocitySlider, gbc_velocitySlider);
		
		JButton startSimButton = new JButton("START SIM");
		startSimButton.addActionListener(this::startSimButtonPressed);
		
		lineNo11CheckBox = new JCheckBox("Line No. 11");
		lineNo11CheckBox.setSelected(true);
		GridBagConstraints gbc_lineNo11CheckBox = new GridBagConstraints();
		gbc_lineNo11CheckBox.insets = new Insets(0, 0, 5, 5);
		gbc_lineNo11CheckBox.gridx = 0;
		gbc_lineNo11CheckBox.gridy = 4;
		simulationDaysContentPane.add(lineNo11CheckBox, gbc_lineNo11CheckBox);
		
		simulationDaysLabel = new JLabel("Simulation Days");
		GridBagConstraints gbc_simulationDaysLabel = new GridBagConstraints();
		gbc_simulationDaysLabel.insets = new Insets(0, 0, 5, 5);
		gbc_simulationDaysLabel.gridx = 0;
		gbc_simulationDaysLabel.gridy = 6;
		simulationDaysContentPane.add(simulationDaysLabel, gbc_simulationDaysLabel);
		
		simulationDaysSpinner = new JSpinner();
		GridBagConstraints gbc_simulationDaysSpinner = new GridBagConstraints();
		gbc_simulationDaysSpinner.fill = GridBagConstraints.HORIZONTAL;
		gbc_simulationDaysSpinner.insets = new Insets(0, 0, 5, 0);
		gbc_simulationDaysSpinner.gridx = 2;
		gbc_simulationDaysSpinner.gridy = 6;
		simulationDaysSpinner.setValue(1);
		simulationDaysContentPane.add(simulationDaysSpinner, gbc_simulationDaysSpinner);
		
		busRidesLabel = new JLabel("Bus rides for day");
		GridBagConstraints gbc_busRidesLabel = new GridBagConstraints();
		gbc_busRidesLabel.insets = new Insets(0, 0, 5, 5);
		gbc_busRidesLabel.gridx = 0;
		gbc_busRidesLabel.gridy = 7;
		simulationDaysContentPane.add(busRidesLabel, gbc_busRidesLabel);
		
		busRidesSpinner = new JSpinner();
		GridBagConstraints gbc_busRidesSpinner = new GridBagConstraints();
		gbc_busRidesSpinner.fill = GridBagConstraints.HORIZONTAL;
		gbc_busRidesSpinner.insets = new Insets(0, 0, 5, 0);
		gbc_busRidesSpinner.gridx = 2;
		gbc_busRidesSpinner.gridy = 7;
		busRidesSpinner.setValue(1);
		simulationDaysContentPane.add(busRidesSpinner, gbc_busRidesSpinner);
		
		GridBagConstraints gbc_startSimButton = new GridBagConstraints();
		gbc_startSimButton.insets = new Insets(0, 0, 0, 5);
		gbc_startSimButton.gridx = 0;
		gbc_startSimButton.gridy = 9;
		simulationDaysContentPane.add(startSimButton, gbc_startSimButton);
	}

	public void startSimButtonPressed(ActionEvent e) {
		this.busMap = new BusMap();
		
		int numberOfStartedThread = 0;
		//necessario per considerare anche i valori scritti manualmente nello spinner
		try {
			simulationDaysSpinner.commitEdit();
			busRidesSpinner.commitEdit();
		} catch (ParseException e1) {
			e1.printStackTrace();
		}
		int simulationDays = (Integer) simulationDaysSpinner.getValue();
		SimulationConfig.getInstance().setSimulationDays(simulationDays);
		int busRides = (Integer) busRidesSpinner.getValue();
		SimulationConfig.getInstance().setBusRides(busRides);
		
		//insert all the stops into the SIB and generate the inspectors. It take some time
		BusStopManager.getInstance().init();
		
		//wait for map, otherwise can't call addStops()..
		busMap.waitReady();
		double simVel = velocitySlider.getValue() / 10.0;
		SimulationConfig.getInstance().setSimulationVelocity(simVel);
		new BusMapFrame(busMap);	
		
		if(lineNo32CheckBox.isSelected()) {
			numberOfStartedThread++;
			prepareNewBus("32", simulationDays, busRides);
		}
		if(lineNo20CheckBox.isSelected()) {
			numberOfStartedThread++;
			prepareNewBus("20", simulationDays, busRides);
		}
		if(lineNo11CheckBox.isSelected()) {
			numberOfStartedThread++;
			prepareNewBus("11", simulationDays, busRides);
		}
		
		SimulationConfig.getInstance().setWaitingThreadForBarrier(numberOfStartedThread);
		this.dispose();
	}
	
	private void prepareNewBus(String busNumber, int simulationDays, int busRides) {
		try {
			busMap.addStops(busNumber);
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		
		BusVisualizerAggregator aggregator = new BusVisualizerAggregator("BUS" + busNumber, busMap);
		aggregator.start();
		new Bus("BUS" + busNumber, busNumber , "gpx/bus" + busNumber + ".gpx","gpx/bus" + busNumber + "StopList.gpx", simulationDays, busRides).start();
	}
}
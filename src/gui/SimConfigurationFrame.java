package gui;

import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.io.FileNotFoundException;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import com.teamdev.jxmaps.LatLng;

import main.Bus;
import main.BusMap;
import main.BusStopManager;
import main.BusVisualizerAggregator;
import main.StatisticsManager;
import parser.BusPathParser;
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
	private JPanel topPanel;
	private JPanel bottomPanel;
	private JSeparator separator_1;
	private JSeparator separator_2;
	private Component verticalStrut;
	private JLabel label_2;
	private JLabel inspectorLabel;
	private JSpinner inspectorsSpinner;
	private JLabel ticketPriceLabel;
	private JSpinner ticketPriceSpinner;
	private JSpinner ticketEvasionSpinner;
	private JSpinner inspectorDailyCostSpinner;
	private JLabel inspectorDailyCostLabel;
	private JLabel ticketEvasionLabel;
	private Component verticalStrut_1;
	private Component verticalStrut_2;
	private JLabel fineLabel;
	private JSpinner fineSpinner;
	private JLabel inspectorGenerationLabel;
	private JSpinner inspectorGenerationSpinner;
	private Component verticalStrut_4;

	private JFrame f;
	
	public SimConfigurationFrame() {
		javax.swing.ToolTipManager.sharedInstance().setDismissDelay(5000);
		javax.swing.ToolTipManager.sharedInstance().setInitialDelay(100);
		
		setBounds(new Rectangle(61, 24, 400, 500));
		setResizable(true);
		setTitle("SimConfiguration");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 385, 271);
		simulationDaysContentPane = new JPanel();
		simulationDaysContentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(simulationDaysContentPane);
		simulationDaysContentPane.setLayout(new BoxLayout(simulationDaysContentPane, BoxLayout.Y_AXIS));
		
		verticalStrut_4 = Box.createVerticalStrut(10);
		simulationDaysContentPane.add(verticalStrut_4);
		
		topPanel = new JPanel();
		topPanel.setAlignmentY(Component.TOP_ALIGNMENT);
		simulationDaysContentPane.add(topPanel);
		topPanel.setLayout(new GridLayout(3, 2, 0, 0));
		
		lineNo32CheckBox = new JCheckBox("Line No. 32");
		topPanel.add(lineNo32CheckBox);
		lineNo32CheckBox.setSelected(true);
		
		lblSimulationVelocity = new JLabel("Simulation velocity");
		lblSimulationVelocity.setHorizontalAlignment(SwingConstants.CENTER);
		topPanel.add(lblSimulationVelocity);
		
		lineNo20CheckBox = new JCheckBox("Line No. 20");
		topPanel.add(lineNo20CheckBox);
		lineNo20CheckBox.setSelected(true);
		
		velocitySlider = new JSlider();
		topPanel.add(velocitySlider);
		velocitySlider.setToolTipText("Simulation velocity (from 0.1x to 2.0x)");
		velocitySlider.setValue(15);
		velocitySlider.setMinimum(1);
		velocitySlider.setMaximum(20);
		
		lineNo11CheckBox = new JCheckBox("Line No. 11");
		topPanel.add(lineNo11CheckBox);
		lineNo11CheckBox.setSelected(true);
		
		label_2 = new JLabel("");
		topPanel.add(label_2);
		
		JButton startSimButton = new JButton("START SIMULATION");
		startSimButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		startSimButton.addActionListener(this::startSimButtonPressed);
		
		verticalStrut_1 = Box.createVerticalStrut(10);
		simulationDaysContentPane.add(verticalStrut_1);
		
		separator_1 = new JSeparator();
		separator_1.setAlignmentY(Component.TOP_ALIGNMENT);
		simulationDaysContentPane.add(separator_1);
		
		verticalStrut_2 = Box.createVerticalStrut(10);
		simulationDaysContentPane.add(verticalStrut_2);
		
		bottomPanel = new JPanel();
		bottomPanel.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
		simulationDaysContentPane.add(bottomPanel);
		GridBagLayout gbl_bottomPanel = new GridBagLayout();
		gbl_bottomPanel.columnWidths = new int[] {95, 25, 95, 25, 0};
		gbl_bottomPanel.rowHeights = new int[] {20, 20, 20, 0, 0, 0};
		gbl_bottomPanel.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		gbl_bottomPanel.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		bottomPanel.setLayout(gbl_bottomPanel);
		
		simulationDaysLabel = new JLabel("Simulation Days");
		GridBagConstraints gbc_simulationDaysLabel = new GridBagConstraints();
		gbc_simulationDaysLabel.fill = GridBagConstraints.BOTH;
		gbc_simulationDaysLabel.insets = new Insets(0, 0, 5, 5);
		gbc_simulationDaysLabel.gridx = 0;
		gbc_simulationDaysLabel.gridy = 0;
		bottomPanel.add(simulationDaysLabel, gbc_simulationDaysLabel);
		
		simulationDaysSpinner = new JSpinner();
		simulationDaysSpinner.setModel(new SpinnerNumberModel(new Integer(1), new Integer(1), null, new Integer(1)));
		GridBagConstraints gbc_simulationDaysSpinner = new GridBagConstraints();
		gbc_simulationDaysSpinner.fill = GridBagConstraints.BOTH;
		gbc_simulationDaysSpinner.insets = new Insets(0, 0, 5, 5);
		gbc_simulationDaysSpinner.gridx = 1;
		gbc_simulationDaysSpinner.gridy = 0;
		bottomPanel.add(simulationDaysSpinner, gbc_simulationDaysSpinner);
		simulationDaysSpinner.setValue(1);
		
		busRidesLabel = new JLabel("Bus rides for day");
		GridBagConstraints gbc_busRidesLabel = new GridBagConstraints();
		gbc_busRidesLabel.fill = GridBagConstraints.BOTH;
		gbc_busRidesLabel.insets = new Insets(0, 0, 5, 5);
		gbc_busRidesLabel.gridx = 2;
		gbc_busRidesLabel.gridy = 0;
		bottomPanel.add(busRidesLabel, gbc_busRidesLabel);
		
		busRidesSpinner = new JSpinner();
		busRidesSpinner.setModel(new SpinnerNumberModel(new Integer(1), new Integer(1), null, new Integer(1)));
		GridBagConstraints gbc_busRidesSpinner = new GridBagConstraints();
		gbc_busRidesSpinner.fill = GridBagConstraints.BOTH;
		gbc_busRidesSpinner.insets = new Insets(0, 0, 5, 5);
		gbc_busRidesSpinner.gridx = 3;
		gbc_busRidesSpinner.gridy = 0;
		bottomPanel.add(busRidesSpinner, gbc_busRidesSpinner);
		busRidesSpinner.setValue(1);
		
		ticketPriceLabel = new JLabel("Ticket Price");
		ticketPriceLabel.setToolTipText("");
		GridBagConstraints gbc_ticketPriceLabel = new GridBagConstraints();
		gbc_ticketPriceLabel.fill = GridBagConstraints.BOTH;
		gbc_ticketPriceLabel.insets = new Insets(0, 0, 5, 5);
		gbc_ticketPriceLabel.gridx = 0;
		gbc_ticketPriceLabel.gridy = 1;
		bottomPanel.add(ticketPriceLabel, gbc_ticketPriceLabel);
		
		ticketPriceSpinner = new JSpinner();
		ticketPriceSpinner.setToolTipText("Float (min 0.5)");
		ticketPriceSpinner.setModel(new SpinnerNumberModel(new Float(1), new Float(0.5), null, new Float(0.1)));
		GridBagConstraints gbc_ticketPriceSpinner = new GridBagConstraints();
		gbc_ticketPriceSpinner.fill = GridBagConstraints.BOTH;
		gbc_ticketPriceSpinner.insets = new Insets(0, 0, 5, 5);
		gbc_ticketPriceSpinner.gridx = 1;
		gbc_ticketPriceSpinner.gridy = 1;
		bottomPanel.add(ticketPriceSpinner, gbc_ticketPriceSpinner);
		
		ticketEvasionLabel = new JLabel("Ticket Evasion");
		ticketEvasionLabel.setToolTipText("Probability that a person doesn't pay ticket");
		GridBagConstraints gbc_ticketEvasionLabel = new GridBagConstraints();
		gbc_ticketEvasionLabel.fill = GridBagConstraints.BOTH;
		gbc_ticketEvasionLabel.insets = new Insets(0, 0, 5, 5);
		gbc_ticketEvasionLabel.gridx = 2;
		gbc_ticketEvasionLabel.gridy = 1;
		bottomPanel.add(ticketEvasionLabel, gbc_ticketEvasionLabel);
		
		ticketEvasionSpinner = new JSpinner();
		ticketEvasionSpinner.setToolTipText("Int (min 0, max 100)");
		ticketEvasionSpinner.setModel(new SpinnerNumberModel(25, 0, 100, 1));
		GridBagConstraints gbc_ticketEvasionSpinner = new GridBagConstraints();
		gbc_ticketEvasionSpinner.fill = GridBagConstraints.BOTH;
		gbc_ticketEvasionSpinner.insets = new Insets(0, 0, 5, 5);
		gbc_ticketEvasionSpinner.gridx = 3;
		gbc_ticketEvasionSpinner.gridy = 1;
		bottomPanel.add(ticketEvasionSpinner, gbc_ticketEvasionSpinner);
		
		inspectorDailyCostLabel = new JLabel("Inspector Cost");
		inspectorDailyCostLabel.setToolTipText("Inspector daily cost");
		GridBagConstraints gbc_inspectorDailyCostLabel = new GridBagConstraints();
		gbc_inspectorDailyCostLabel.fill = GridBagConstraints.BOTH;
		gbc_inspectorDailyCostLabel.insets = new Insets(0, 0, 5, 5);
		gbc_inspectorDailyCostLabel.gridx = 0;
		gbc_inspectorDailyCostLabel.gridy = 2;
		bottomPanel.add(inspectorDailyCostLabel, gbc_inspectorDailyCostLabel);
		
		inspectorDailyCostSpinner = new JSpinner();
		inspectorDailyCostSpinner.setModel(new SpinnerNumberModel(new Integer(10), new Integer(0), null, new Integer(1)));
		GridBagConstraints gbc_inspectorDailyCostSpinner = new GridBagConstraints();
		gbc_inspectorDailyCostSpinner.fill = GridBagConstraints.BOTH;
		gbc_inspectorDailyCostSpinner.insets = new Insets(0, 0, 5, 5);
		gbc_inspectorDailyCostSpinner.gridx = 1;
		gbc_inspectorDailyCostSpinner.gridy = 2;
		bottomPanel.add(inspectorDailyCostSpinner, gbc_inspectorDailyCostSpinner);
		
		inspectorLabel = new JLabel("Inspectors");
		inspectorLabel.setToolTipText("Number of Inspectors");
		GridBagConstraints gbc_inspectorLabel = new GridBagConstraints();
		gbc_inspectorLabel.fill = GridBagConstraints.BOTH;
		gbc_inspectorLabel.insets = new Insets(0, 0, 5, 5);
		gbc_inspectorLabel.gridx = 2;
		gbc_inspectorLabel.gridy = 2;
		bottomPanel.add(inspectorLabel, gbc_inspectorLabel);
		
		inspectorsSpinner = new JSpinner();
		inspectorsSpinner.setModel(new SpinnerNumberModel(new Integer(1), new Integer(0), null, new Integer(1)));
		GridBagConstraints gbc_inspectorsSpinner = new GridBagConstraints();
		gbc_inspectorsSpinner.insets = new Insets(0, 0, 5, 5);
		gbc_inspectorsSpinner.fill = GridBagConstraints.BOTH;
		gbc_inspectorsSpinner.gridx = 3;
		gbc_inspectorsSpinner.gridy = 2;
		bottomPanel.add(inspectorsSpinner, gbc_inspectorsSpinner);
		
		fineLabel = new JLabel("Fine");
		fineLabel.setToolTipText("Price for every fine done by an inspector");
		GridBagConstraints gbc_fineLabel = new GridBagConstraints();
		gbc_fineLabel.fill = GridBagConstraints.BOTH;
		gbc_fineLabel.insets = new Insets(0, 0, 5, 5);
		gbc_fineLabel.gridx = 0;
		gbc_fineLabel.gridy = 3;
		bottomPanel.add(fineLabel, gbc_fineLabel);
		
		fineSpinner = new JSpinner();
		fineSpinner.setToolTipText("Float (min 0.1)");
		fineSpinner.setModel(new SpinnerNumberModel(new Float(5), new Float(0.1), null, new Float(0.5)));
		GridBagConstraints gbc_fineSpinner = new GridBagConstraints();
		gbc_fineSpinner.fill = GridBagConstraints.BOTH;
		gbc_fineSpinner.insets = new Insets(0, 0, 5, 5);
		gbc_fineSpinner.gridx = 1;
		gbc_fineSpinner.gridy = 3;
		bottomPanel.add(fineSpinner, gbc_fineSpinner);
		
		inspectorGenerationLabel = new JLabel("Inspector Generation");
		inspectorGenerationLabel.setToolTipText("Probability that an inspector appears in a Bus Stop");
		GridBagConstraints gbc_inspectorGenerationLabel = new GridBagConstraints();
		gbc_inspectorGenerationLabel.fill = GridBagConstraints.BOTH;
		gbc_inspectorGenerationLabel.insets = new Insets(0, 0, 5, 5);
		gbc_inspectorGenerationLabel.gridx = 2;
		gbc_inspectorGenerationLabel.gridy = 3;
		bottomPanel.add(inspectorGenerationLabel, gbc_inspectorGenerationLabel);
		
		inspectorGenerationSpinner = new JSpinner();
		inspectorGenerationSpinner.setToolTipText("Int  (min 0, max 100)");
		inspectorGenerationSpinner.setModel(new SpinnerNumberModel(10, 0, 100, 1));
		GridBagConstraints gbc_inspectorGenerationSpinner = new GridBagConstraints();
		gbc_inspectorGenerationSpinner.fill = GridBagConstraints.BOTH;
		gbc_inspectorGenerationSpinner.insets = new Insets(0, 0, 5, 5);
		gbc_inspectorGenerationSpinner.gridx = 3;
		gbc_inspectorGenerationSpinner.gridy = 3;
		bottomPanel.add(inspectorGenerationSpinner, gbc_inspectorGenerationSpinner);
		
		Component verticalStrut_3 = Box.createVerticalStrut(10);
		simulationDaysContentPane.add(verticalStrut_3);
		
		separator_2 = new JSeparator();
		simulationDaysContentPane.add(separator_2);
		
		verticalStrut = Box.createVerticalStrut(10);
		simulationDaysContentPane.add(verticalStrut);
		simulationDaysContentPane.add(startSimButton);
		f = new BusRuntimeVisualizer();
		f.setVisible(true);
	}

	public void startSimButtonPressed(ActionEvent e) {
		this.busMap = new BusMap();		
		int numberOfStartedThread = 0;

		// retrieving configuration values
		int simulationDays = (Integer) simulationDaysSpinner.getValue();
		SimulationConfig.getInstance().setSimulationDays(simulationDays);
		int busRides = (Integer) busRidesSpinner.getValue();
		SimulationConfig.getInstance().setBusRides(busRides);
		float ticketPrice = (Float) ticketPriceSpinner.getValue();
		SimulationConfig.getInstance().setTicketPrice(ticketPrice);
		int ticketEvasion = (Integer) ticketEvasionSpinner.getValue();
		SimulationConfig.getInstance().setTicketEvasion(ticketEvasion);
		int inspectorCost = (Integer) inspectorDailyCostSpinner.getValue();
		SimulationConfig.getInstance().setInspectorCost(inspectorCost);
		int inspectors = (Integer) inspectorsSpinner.getValue();
		SimulationConfig.getInstance().setMaxInspectors(inspectors);
		float fine = (Float) fineSpinner.getValue();
		SimulationConfig.getInstance().setFine(fine);
		int inspectorGeneration = (Integer) inspectorGenerationSpinner.getValue();
		SimulationConfig.getInstance().setInspectorGeneration(inspectorGeneration);
		
		
		//insert all the stops into the SIB and generate the inspectors. It take some time
		BusStopManager.getInstance().init();
		
		//wait for map, otherwise can't call addStops()..
		busMap.waitReady();
		double simVel = velocitySlider.getValue() / 10.0;
		SimulationConfig.getInstance().setSimulationVelocity(simVel);
		new BusMapFrame(busMap);	
		
		if(lineNo32CheckBox.isSelected()) {
			numberOfStartedThread++;
			prepareNewBus("32", simulationDays, busRides, BusColor.ORANGE);
			busMap.addBusline(new BusPathParser("gpx/bus32.gpx").getListOfPoint(), "#FFA500");
		}
		if(lineNo20CheckBox.isSelected()) {
			numberOfStartedThread++;
			prepareNewBus("20", simulationDays, busRides, BusColor.RED);
			busMap.addBusline(new BusPathParser("gpx/bus20.gpx").getListOfPoint(), "#FF0000");
		}
		if(lineNo11CheckBox.isSelected()) {
			numberOfStartedThread++;
			prepareNewBus("11", simulationDays, busRides, BusColor.BLUE);
			busMap.addBusline(new BusPathParser("gpx/bus11.gpx").getListOfPoint(), "#0000FF");
		}
		
		
		
		SimulationConfig.getInstance().setWaitingThreadForBarrier(numberOfStartedThread);
		busMap.getMap().setCenter(new LatLng(44.4914, 11.3428));
		
		
		SimulationConfig.getInstance().getStartSimulationSemaphore().release();
			
		SimulationConfig.getInstance().setEndBarrier(numberOfStartedThread+1); // +1 because also this thread is counted
		

		dispose();

	}
	
	private void prepareNewBus(String busNumber, int simulationDays, int busRides, String busColor) {
		try {
			busMap.addStops(busNumber);
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		
		BusVisualizerAggregator aggregator = new BusVisualizerAggregator(busNumber, busMap, busColor);
		aggregator.start();
		new Bus("BUS" + busNumber, busNumber , "gpx/bus" + busNumber + ".gpx","gpx/bus" + busNumber + "StopList.gpx", simulationDays, busRides).start();
	}
}
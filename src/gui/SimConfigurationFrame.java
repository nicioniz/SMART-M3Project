package gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.io.FileNotFoundException;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import main.BusVisualizerAggregator;
import main.Bus;
import main.BusMap;
import simulationConfiguration.SimulationConfig;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.JTextField;

@SuppressWarnings("serial")
public class SimConfigurationFrame extends JFrame {

	private JPanel simulationDaysContentPane;
	private BusMap busMap;
	private JCheckBox lineNo32CheckBox, lineNo20CheckBox;
	private JLabel lblSimulationVelocity;
	private JSlider velocitySlider;
	private JLabel simulationDaysLabel;
	private JTextField simulationDaysTextField;
	private JLabel busRidesLabel;
	private JTextField busRidesTextField;
		
	public SimConfigurationFrame() {
		setTitle("SimConfiguration");
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		simulationDaysContentPane = new JPanel();
		simulationDaysContentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(simulationDaysContentPane);
		
		GridBagLayout gbl_simulationDaysContentPane = new GridBagLayout();
		gbl_simulationDaysContentPane.columnWidths = new int[]{0, 0, 0, 0};
		gbl_simulationDaysContentPane.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0, 0};
		gbl_simulationDaysContentPane.columnWeights = new double[]{0.0, 0.0, 1.0, Double.MIN_VALUE};
		gbl_simulationDaysContentPane.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
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
		
		simulationDaysLabel = new JLabel("Simulation Days");
		GridBagConstraints gbc_simulationDaysLabel = new GridBagConstraints();
		gbc_simulationDaysLabel.insets = new Insets(0, 0, 5, 5);
		gbc_simulationDaysLabel.gridx = 0;
		gbc_simulationDaysLabel.gridy = 4;
		simulationDaysContentPane.add(simulationDaysLabel, gbc_simulationDaysLabel);
		
		simulationDaysTextField = new JTextField();
		GridBagConstraints gbc_simulationDaysTextField = new GridBagConstraints();
		gbc_simulationDaysTextField.insets = new Insets(0, 0, 5, 0);
		gbc_simulationDaysTextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_simulationDaysTextField.gridx = 2;
		gbc_simulationDaysTextField.gridy = 4;
		simulationDaysContentPane.add(simulationDaysTextField, gbc_simulationDaysTextField);
		simulationDaysTextField.setColumns(10);
		
		busRidesLabel = new JLabel("Bus rides for day");
		GridBagConstraints gbc_busRidesLabel = new GridBagConstraints();
		gbc_busRidesLabel.insets = new Insets(0, 0, 5, 5);
		gbc_busRidesLabel.gridx = 0;
		gbc_busRidesLabel.gridy = 5;
		simulationDaysContentPane.add(busRidesLabel, gbc_busRidesLabel);
		
		busRidesTextField = new JTextField();
		GridBagConstraints gbc_busRidesTextField = new GridBagConstraints();
		gbc_busRidesTextField.insets = new Insets(0, 0, 5, 0);
		gbc_busRidesTextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_busRidesTextField.gridx = 2;
		gbc_busRidesTextField.gridy = 5;
		simulationDaysContentPane.add(busRidesTextField, gbc_busRidesTextField);
		busRidesTextField.setColumns(10);
		
		GridBagConstraints gbc_startSimButton = new GridBagConstraints();
		gbc_startSimButton.insets = new Insets(0, 0, 0, 5);
		gbc_startSimButton.gridx = 0;
		gbc_startSimButton.gridy = 6;
		simulationDaysContentPane.add(startSimButton, gbc_startSimButton);
	}
	
	public void startSimButtonPressed(ActionEvent e) {
		this.busMap = new BusMap();

		String tempSimulationDays = "";
		tempSimulationDays = simulationDaysTextField.getText();
		int simulationDays = Integer.parseInt(tempSimulationDays);
		SimulationConfig.getInstance().setSimulationDays(simulationDays);
		String tempBusRides = "";
		tempBusRides = busRidesTextField.getText();
		int busRides = Integer.parseInt(tempBusRides);
		SimulationConfig.getInstance().setBusRides(busRides);
		//wait for map, otherwise can't call addStops()..
		busMap.waitReady();
		double simVel = velocitySlider.getValue() / 10.0;
		SimulationConfig.getInstance().setSimulationVelocity(simVel);
		
		if(lineNo32CheckBox.isSelected()) {
			try {
				busMap.addStops("gpx/bus32StopList.gpx");
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			}
			BusVisualizerAggregator aggregator = new BusVisualizerAggregator("BUS32", busMap);
			aggregator.start();
			new Bus("BUS32", "gpx/bus32.gpx","gpx/bus32StopList.gpx", simulationDays).start();
		}
		if(lineNo20CheckBox.isSelected()) {
			
			BusVisualizerAggregator aggregator2 = new BusVisualizerAggregator("BUS20", busMap);
			aggregator2.start();
			//===============CORREGGERE PATH PER LE FERMATE DEL 20 !!!===============
			new Bus("BUS20", "gpx/bus20.gpx", "gpx/bus32StopList.gpx", simulationDays).start();
		}
		new BusMapFrame(busMap);		
		this.dispose();
	}
}
package gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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

@SuppressWarnings("serial")
public class SimConfigurationFrame extends JFrame {

	private JPanel contentPane;
	private BusMap busMap;
	private JCheckBox lineNo32CheckBox, lineNo20CheckBox;
	private JLabel lblSimulationVelocity;
	private JSlider velocitySlider;
	
	public SimConfigurationFrame() {
		setTitle("SimConfiguration");
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		
		GridBagLayout gbl_contentPane = new GridBagLayout();
		gbl_contentPane.columnWidths = new int[]{0, 0, 0, 0};
		gbl_contentPane.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0};
		gbl_contentPane.columnWeights = new double[]{0.0, 0.0, 0.0, Double.MIN_VALUE};
		gbl_contentPane.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		contentPane.setLayout(gbl_contentPane);
		
		lblSimulationVelocity = new JLabel("Simulation velocity");
		GridBagConstraints gbc_lblSimulationVelocity = new GridBagConstraints();
		gbc_lblSimulationVelocity.insets = new Insets(0, 0, 5, 0);
		gbc_lblSimulationVelocity.gridx = 2;
		gbc_lblSimulationVelocity.gridy = 2;
		contentPane.add(lblSimulationVelocity, gbc_lblSimulationVelocity);
		
		lineNo32CheckBox = new JCheckBox("Line No. 32");
		lineNo32CheckBox.setSelected(true);
		GridBagConstraints gbc_lineNo32CheckBox = new GridBagConstraints();
		gbc_lineNo32CheckBox.insets = new Insets(0, 0, 5, 5);
		gbc_lineNo32CheckBox.gridx = 0;
		gbc_lineNo32CheckBox.gridy = 2;
		contentPane.add(lineNo32CheckBox, gbc_lineNo32CheckBox);
		
		JButton startSimButton = new JButton("START SIM");
		startSimButton.addActionListener(this::startSimButtonPressed);
		
		lineNo20CheckBox = new JCheckBox("Line No. 20");
		lineNo20CheckBox.setSelected(true);
		GridBagConstraints gbc_lineNo20CheckBox = new GridBagConstraints();
		gbc_lineNo20CheckBox.insets = new Insets(0, 0, 5, 5);
		gbc_lineNo20CheckBox.gridx = 0;
		gbc_lineNo20CheckBox.gridy = 3;
		contentPane.add(lineNo20CheckBox, gbc_lineNo20CheckBox);
		
		velocitySlider = new JSlider();
		velocitySlider.setToolTipText("Simulation velocity (from 0.1x to 2.0x)");
		velocitySlider.setValue(15);
		velocitySlider.setMinimum(1);
		velocitySlider.setMaximum(20);
		GridBagConstraints gbc_velocitySlider = new GridBagConstraints();
		gbc_velocitySlider.insets = new Insets(0, 0, 5, 0);
		gbc_velocitySlider.gridx = 2;
		gbc_velocitySlider.gridy = 3;
		contentPane.add(velocitySlider, gbc_velocitySlider);
		
		GridBagConstraints gbc_startSimButton = new GridBagConstraints();
		gbc_startSimButton.insets = new Insets(0, 0, 0, 5);
		gbc_startSimButton.gridx = 0;
		gbc_startSimButton.gridy = 5;
		contentPane.add(startSimButton, gbc_startSimButton);
	}
	
	public void startSimButtonPressed(ActionEvent e) {
		this.busMap = new BusMap();
		
		double simVel = velocitySlider.getValue() / 10.0;
		SimulationConfig.getInstance().setSimulationVelocity(simVel);
		
		if(lineNo32CheckBox.isSelected()) {
			BusVisualizerAggregator aggregator = new BusVisualizerAggregator("BUS32", busMap);
			aggregator.start();
			new Bus("BUS32", "gpx/bus32.gpx").start();
		}
		if(lineNo20CheckBox.isSelected()) {
			BusVisualizerAggregator aggregator2 = new BusVisualizerAggregator("BUS20", busMap);
			aggregator2.start();
			new Bus("BUS20", "gpx/bus20.gpx").start();
		}
		new BusMapFrame(busMap);		
		this.dispose();
	}
}

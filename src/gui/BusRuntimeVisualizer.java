package gui;

import java.awt.BorderLayout;
import java.awt.Rectangle;

import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.ActionEvent;

public class BusRuntimeVisualizer extends JFrame {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JComboBox<String> cmbRefreshRate;
	private JTextArea txtResult;
	private Thread runningThread = null;
	
	public BusRuntimeVisualizer() {
		super();
		setTitle("Bus Runtime Visualizer");
		setSize(400, 400);
		setResizable(true);
		setVisible(false);
		JPanel southPanel = new JPanel();
		getContentPane().add(southPanel, BorderLayout.SOUTH);
		
		JLabel lblRefreshRate = new JLabel("Refresh rate: ");
		southPanel.add(lblRefreshRate);
		
		cmbRefreshRate = new JComboBox<>();
		cmbRefreshRate.addItem("500 msec");
		cmbRefreshRate.addItem("1 sec");
		cmbRefreshRate.addItem("2 sec");
		cmbRefreshRate.addItem("5 sec");
		cmbRefreshRate.setSelectedIndex(0);
		
		
		southPanel.add(cmbRefreshRate);
		
		JButton btnShow = new JButton("Show");
		btnShow.addActionListener(this::clickBtnShow);
		southPanel.add(btnShow);
		
		JPanel panel = new JPanel();
		getContentPane().add(panel, BorderLayout.CENTER);
		panel.setLayout(new BorderLayout(0, 0));
		
		txtResult = new JTextArea();
		txtResult.setRows(20);
		txtResult.setEditable(false);
		panel.add(txtResult);
		
		runningThread = new BusRuntimeVisualizerThread(500, txtResult);
		runningThread.start();
		
		
	}
	
	private void clickBtnShow(ActionEvent ae) {
		if(runningThread != null)
			runningThread.interrupt();
		
		txtResult.setText("");
		
		int refreshRate;
		if(cmbRefreshRate.getSelectedItem().equals("500 msec"))
			refreshRate = 500;
		else 
			refreshRate = 1000 * Integer.parseInt(((String)cmbRefreshRate.getSelectedItem()).substring(0, 1));
		
		runningThread = new BusRuntimeVisualizerThread(refreshRate, txtResult);
		runningThread.start();
	}

	
	@Override
	public void dispose() {
		if(runningThread != null)
			runningThread.interrupt();
		super.dispose();
	}
	
	

}

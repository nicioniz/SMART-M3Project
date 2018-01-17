package gui;

import javax.swing.JFrame;
import java.awt.Insets;

import main.StatisticsManager;

import javax.swing.JTextArea;
import javax.swing.JScrollPane;

public class StatisticsFrame extends JFrame{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public StatisticsFrame() {
		setTitle("Simulation Statistics");
		
		// text area
		JTextArea txt = new JTextArea();
		txt.setEditable(false);
		txt.setToolTipText("");
		txt.setText(StatisticsManager.getInstance().economicSummary());
		
		// set margin
		Insets insets = this.getInsets();
		insets.set(10, 10, 10, 10);
		txt.setMargin(insets);
		
		// scroll pane
		JScrollPane scrollPane = new JScrollPane(txt);
		this.getContentPane().add(scrollPane);

		// size
		setSize(1200, 800);
		
		// close operation
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}

}

package gui;
								
import java.awt.BorderLayout;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

import main.BusMap;

@SuppressWarnings("serial")
public class BusMapFrame extends JFrame{
	
	public BusMapFrame(BusMap map) {
		super("BusMap");
	
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		add(map, BorderLayout.CENTER);
		setSize(1400, 1000);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);
		setResizable(false);
	}
	
}

package gui;

import java.awt.BorderLayout;
import java.io.FileNotFoundException;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import main.BusMap;

@SuppressWarnings("serial")
public class BusMapFrame extends JFrame{
	
	public BusMapFrame(BusMap map) {
		super("BusMap");
		try {
			map.addStop();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		add(map, BorderLayout.CENTER);
		setSize(1400, 1000);
		setLocationRelativeTo(null);
		setVisible(true);
		setResizable(false);
	}
	
}

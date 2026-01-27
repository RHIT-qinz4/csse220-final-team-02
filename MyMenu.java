package starter;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
/**
 * A simple control panel containing buttons used to interact with the game.
 */

public class MyMenu extends JPanel {
	
	private JButton leftButton;
	private JButton rightButton;
	private JButton resetButton;
	
	public MyMenu() {
		setPreferredSize(new Dimension(300,50));
		setLayout(new FlowLayout(FlowLayout.CENTER));
		setBackground(Color.DARK_GRAY); 
		
		leftButton = new JButton("Left");
		rightButton = new JButton("Right");
		resetButton = new JButton("Reset");
		
		add(leftButton);
		add(resetButton);
		add(rightButton);
	}
	
	  public JButton getLeftButton() { 
	        return leftButton; 
	      }
	public JButton getRightButton() { 
	  return rightButton; 
	}
	public JButton getResetButton() { 
	  return resetButton; 
	}
	
	
}

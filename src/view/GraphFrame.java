package view;

import javax.swing.JFrame;
import javax.swing.JPanel;

import java.awt.*;

import graph.TransportGraph;

public class GraphFrame extends JFrame {
	 private GraphPanel graphPanel;
	// konstruktor
	 public GraphFrame(TransportGraph graph) {
		    setSize(1200, 1200);
		    setTitle("Prikaz grafa");
		    setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		    setLocationRelativeTo(null);

		    // Initialize the instance field, not a local variable
		    this.graphPanel = new GraphPanel(graph);

		    setContentPane(this.graphPanel);
		}


	 public GraphPanel getGraphPanel() {
	        return graphPanel;
	    }
}

package view;

import javax.swing.JFrame;

import graph.TransportGraph;

public class GraphFrame extends JFrame {
	/**
	* 
	*/
	private static final long serialVersionUID = 1L;
	private GraphPanel graphPanel;

	public GraphFrame(TransportGraph graph) {
		setSize(1200, 1200);
		setTitle("Prikaz grafa");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setLocationRelativeTo(null);

		this.graphPanel = new GraphPanel(graph);

		setContentPane(this.graphPanel);
	}

	public GraphPanel getGraphPanel() {
		return graphPanel;
	}
}

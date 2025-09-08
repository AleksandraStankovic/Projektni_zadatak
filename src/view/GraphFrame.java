package view;

import javax.swing.JFrame;

import graph.TransportGraph;
/**
 * A JFrame that displays a transport graph visualization.
 * 
 * <p>
 * This frame embeds a GraphPanel that renders the given {@link TransportGraph} using GraphStream.
 * It provides a standard window setup with a title, size, and close operation.
 * </p>
 */
public class GraphFrame extends JFrame {

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

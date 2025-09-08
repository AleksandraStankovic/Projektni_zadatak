package view;

import java.awt.BorderLayout;
import javax.swing.JPanel;

import org.graphstream.graph.Edge;
import org.graphstream.graph.implementations.MultiGraph;
import org.graphstream.ui.swing_viewer.SwingViewer;
import org.graphstream.ui.swing_viewer.ViewPanel;

import graph.CityGraph;
import java.util.List;

/**
 * A JPanel component that visualizes a city graph using GraphStream.
 * 
 * <p>
 * This panel embeds a GraphStream SwingViewer to display the graph, allows
 * updating the graph, and supports highlighting a path of cities by visually
 * distinguishing nodes and edges along the path.
 * </p>
 * 
 * <p>
 * Node and edge styles can be dynamically updated to show the currently
 * highlighted path, while all other nodes and edges are displayed in default
 * styles.
 * </p>
 */
public class CityGraphPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	private MultiGraph graph;

	public CityGraphPanel(CityGraph cityGraph) {
		setLayout(new BorderLayout());

		this.graph = (MultiGraph) cityGraph.buildGraph();
		initViewer();
	}

	private void initViewer() {
		graph.setAttribute("layout.quality", 4);

		SwingViewer viewer = new SwingViewer(graph, SwingViewer.ThreadingModel.GRAPH_IN_GUI_THREAD);
		viewer.enableAutoLayout();
		ViewPanel viewPanel = (ViewPanel) viewer.addDefaultView(false);
		viewPanel.setAutoscrolls(true);

		add(viewPanel, BorderLayout.CENTER);
	}

	/**
	 * Highlights a path of cities in the graph by updating the visual styles of
	 * nodes and edges.
	 * 
	 * <p>
	 * All edges and nodes are first reset to default "cityEdge" and "cityNode"
	 * styles. Then, for each consecutive pair of cities in the given path, the
	 * corresponding edge and its source and target nodes are styled as
	 * "highlightedEdge" and "highlightedCity".
	 * </p>
	 * 
	 * <p>
	 * If the provided city path is null or empty, no highlighting is applied.
	 * </p>
	 * 
	 * @param cityPath a list of city names representing the path to highlight
	 */

	public void highlightCityPath(List<String> cityPath) {

		graph.nodes().forEach(n -> n.setAttribute("ui.class", "nonOptimalNode"));
		graph.edges().forEach(e -> e.setAttribute("ui.class", "nonOptimal"));

		if (cityPath == null || cityPath.size() < 2)
			return;

		for (int i = 0; i < cityPath.size() - 1; i++) {
			String from = cityPath.get(i);
			String to = cityPath.get(i + 1);

			Edge edge = graph.getEdge(from + "-" + to);
			if (edge == null)
				edge = graph.getEdge(to + "-" + from); // check both directions

			if (edge != null) {
				edge.setAttribute("ui.class", "optimal");
				edge.getSourceNode().setAttribute("ui.class", "optimal");
				edge.getTargetNode().setAttribute("ui.class", "optimal");
			}
		}
	}

	public void updateGraph(MultiGraph newGraph) {
		this.graph = newGraph;
		removeAll();
		initViewer();
		revalidate();
		repaint();
	}
}

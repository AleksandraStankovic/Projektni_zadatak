package view;

import java.awt.BorderLayout;
import javax.swing.JPanel;

import org.graphstream.graph.Edge;
import org.graphstream.graph.implementations.MultiGraph;
import org.graphstream.ui.swing_viewer.SwingViewer;
import org.graphstream.ui.swing_viewer.ViewPanel;

import graph.CityGraph;
import java.util.List;

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
        ViewPanel viewPanel = (ViewPanel) viewer.addDefaultView(false); // false = embed in JPanel
        viewPanel.setAutoscrolls(true);

        add(viewPanel, BorderLayout.CENTER);
    }

    public void highlightCityPath(List<String> cityPath) {
        graph.edges().forEach(edge -> edge.setAttribute("ui.class", "cityEdge"));
        graph.nodes().forEach(node -> node.setAttribute("ui.class", "cityNode"));

        if (cityPath == null) return;

        for (int i = 0; i < cityPath.size() - 1; i++) {
            String from = cityPath.get(i);
            String to = cityPath.get(i + 1);
            Edge edge = graph.getEdge(from + "-" + to);
            if (edge == null) edge = graph.getEdge(to + "-" + from); // undirected
            if (edge != null) {
                edge.setAttribute("ui.class", "highlightedEdge");
                edge.getSourceNode().setAttribute("ui.class", "highlightedCity");
                edge.getTargetNode().setAttribute("ui.class", "highlightedCity");
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

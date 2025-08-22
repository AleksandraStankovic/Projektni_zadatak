package view;

import java.awt.BorderLayout;
import javax.swing.JPanel;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Node;
import org.graphstream.graph.Path;
import org.graphstream.graph.implementations.MultiGraph;
import model.PathInfo;
import java.util.Iterator;


import org.graphstream.ui.swing_viewer.SwingViewer;
import org.graphstream.ui.swing_viewer.ViewPanel;

import graph.TransportGraph;






public class GraphPanel extends JPanel {
	private static final long serialVersionUID = 1L;
//	 private Graph graph;
	private MultiGraph graph;  // instead of Graph

   
    public GraphPanel(TransportGraph transportGraph) { 
        setLayout(new BorderLayout());
        
        

        this.graph =(MultiGraph) transportGraph.buildGraph();
        initViewer();
    }

    private void initViewer() {
    	  graph.setAttribute("layout.quality", 4);
    	   
    	
        SwingViewer viewer = new SwingViewer(graph, 
            SwingViewer.ThreadingModel.GRAPH_IN_GUI_THREAD);
        viewer.enableAutoLayout();
        ViewPanel viewPanel = (ViewPanel) viewer.addDefaultView(false);
        viewPanel.setAutoscrolls(true);           // enable scroll/drag view

        add(viewPanel, BorderLayout.CENTER);
    }



    
    public void highlightPath(PathInfo pathInfo) {
        // Set all edges to gray
        graph.edges().forEach(edge -> edge.setAttribute("ui.class", "nonOptimal"));

        // Set all nodes to gray
        graph.nodes().forEach(node -> node.setAttribute("ui.class", "nonOptimalNode"));

        if (pathInfo == null) return;

        // Highlight optimal edges & nodes using IDs
        for (Edge edgeInPath : pathInfo.getPath().getEdgePath()) {
            String edgeId = edgeInPath.getId();
            Edge edge = graph.getEdge(edgeId);  // get the edge from the actual graph
            if (edge != null) {
                edge.setAttribute("ui.class", "optimal");
                edge.getSourceNode().setAttribute("ui.class", "optimal");
                edge.getTargetNode().setAttribute("ui.class", "optimal");
            }
        }
    }






    public void updateGraph(MultiGraph newGraph) {
        this.graph = (MultiGraph)newGraph;
        removeAll();
        initViewer();
        revalidate();
        repaint();
    }
}
package view;

import java.awt.BorderLayout;
import javax.swing.JPanel;
import org.graphstream.graph.Graph;

import org.graphstream.ui.swing_viewer.SwingViewer;
import org.graphstream.ui.swing_viewer.ViewPanel;

import graph.TransportGraph;
//i guess da ce ovdje trebati sve drugacije kad budemo generisali svoj graf
//trebace se sediti izgled samog grafa, ovo sad se nesto krece, idk....


public class GraphPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	 private Graph graph;
   
    public GraphPanel(TransportGraph transportGraph) { //moramo proslijediti graf...
        setLayout(new BorderLayout());
        

        this.graph = transportGraph.buildGraph();
        initViewer();
    }

    private void initViewer() {
        SwingViewer viewer = new SwingViewer(graph, 
            SwingViewer.ThreadingModel.GRAPH_IN_GUI_THREAD);
        viewer.enableAutoLayout();
        ViewPanel viewPanel = (ViewPanel) viewer.addDefaultView(false);
        add(viewPanel, BorderLayout.CENTER);
    }


    public void updateGraph(Graph newGraph) {
        this.graph = newGraph;
        removeAll();
        initViewer();
        revalidate();
        repaint();
    }
}
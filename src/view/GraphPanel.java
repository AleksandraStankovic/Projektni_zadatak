package view;

import java.awt.BorderLayout;
import javax.swing.JPanel;
import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.swing_viewer.SwingViewer;
import org.graphstream.ui.swing_viewer.ViewPanel;
import org.graphstream.graph.Node;
import org.graphstream.graph.Edge;
import java.util.Random;
//i guess da ce ovdje trebati sve drugacije kad budemo generisali svoj graf
//trebace se sediti izgled samog grafa, ovo sad se nesto krece, idk....


public class GraphPanel extends JPanel {
	private static final long serialVersionUID = 1L;
    private Graph graph;
    private Random random = new Random();

    public GraphPanel() {
        setLayout(new BorderLayout());
        graph = createLargeTransportNetwork();
        initViewer();
    }

    private void initViewer() {
        SwingViewer viewer = new SwingViewer(graph, 
            SwingViewer.ThreadingModel.GRAPH_IN_GUI_THREAD);
        viewer.enableAutoLayout();
        ViewPanel viewPanel = (ViewPanel) viewer.addDefaultView(false);
        add(viewPanel, BorderLayout.CENTER);
    }

    private Graph createLargeTransportNetwork() {
        Graph g = new SingleGraph("Transport Network");
        
        // Create 20 cities
        for (int i = 0; i < 20; i++) {
            String cityName = "City_" + (char)('A' + i);
            
            // Add bus station
            Node busStation = g.addNode("BUS_" + cityName);
            busStation.setAttribute("ui.label", cityName + " Bus");
            busStation.setAttribute("ui.class", "bus");
            
            // Add train station
            Node trainStation = g.addNode("TRAIN_" + cityName);
            trainStation.setAttribute("ui.label", cityName + " Train");
            trainStation.setAttribute("ui.class", "train");
            
            // Connect bus and train stations in same city
            Edge transferEdge = g.addEdge(cityName + "_Transfer", "BUS_" + cityName, "TRAIN_" + cityName);
            transferEdge.setAttribute("ui.class", "transfer");
        }
        
        // Create connections between cities
        String[] cityNames = new String[20];
        for (int i = 0; i < 20; i++) {
            cityNames[i] = "City_" + (char)('A' + i);
        }
        
        // Connect nearby cities (creating a mesh network)
        for (int i = 0; i < 20; i++) {
            for (int j = i + 1; j < Math.min(i + 5, 20); j++) {
                // 70% chance of bus connection
                if (random.nextDouble() < 0.7) {
                    createTransportRoute(g, cityNames[i], cityNames[j], "BUS", 
                                       random.nextInt(30) + 20, random.nextInt(15) + 5);
                }
                
                // 50% chance of train connection (less frequent than bus)
                if (random.nextDouble() < 0.5) {
                    createTransportRoute(g, cityNames[i], cityNames[j], "TRAIN", 
                                       random.nextInt(45) + 30, random.nextInt(20) + 10);
                }
            }
        }
        
        // Add some long-distance connections
        createTransportRoute(g, "City_A", "City_K", "TRAIN", 120, 35);
        createTransportRoute(g, "City_B", "City_P", "BUS", 90, 25);
        createTransportRoute(g, "City_E", "City_T", "TRAIN", 150, 45);
        
        // Style the graph
        g.setAttribute("ui.stylesheet",
            "node.bus { fill-color: #3498db; size: 20px; text-size: 12px; }" +
            "node.train { fill-color: #e74c3c; size: 20px; text-size: 12px; }" +
            "edge.bus { fill-color: #3498db; size: 2px; text-size: 11px; }" +
            "edge.train { fill-color: #e74c3c; size: 3px; text-size: 11px; }" +
            "edge.transfer { fill-color: #2ecc71; size: 1px; }" +
            "node { text-style: bold; }" +
            "edge { text-alignment: along; text-background-mode: plain; }"
        );
        
        return g;
    }
    
    private void createTransportRoute(Graph g, String city1, String city2, 
                                    String type, int minutes, int price) {
        String from = type + "_" + city1;
        String to = type + "_" + city2;
        String edgeId = from + "-" + to;
        
        Edge edge = g.addEdge(edgeId, from, to);
        edge.setAttribute("ui.label", minutes + "min (" + price + "KM)");
        edge.setAttribute("ui.class", type.toLowerCase());
    }

    public void updateGraph(Graph newGraph) {
        this.graph = newGraph;
        removeAll();
        initViewer();
        revalidate();
        repaint();
    }
}
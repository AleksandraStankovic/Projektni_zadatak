package view;

import javax.swing.JFrame;
import graph.CityGraph;
/**
 * A JFrame that displays a visual representation of a city graph.
 * 
 * <p>
 * The frame contains a CityGraphPanel that renders the nodes and edges of the provided city graph.
 * </p>
 */
public class CityGraphFrame extends JFrame {
    private static final long serialVersionUID = 1L;
    private CityGraphPanel graphPanel;

    public CityGraphFrame(CityGraph cityGraph) {
        setSize(1200, 1200);
        setTitle("City Graph Visualization");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        this.graphPanel = new CityGraphPanel(cityGraph);
        setContentPane(this.graphPanel);
    }

    public CityGraphPanel getGraphPanel() {
        return graphPanel;
    }
}

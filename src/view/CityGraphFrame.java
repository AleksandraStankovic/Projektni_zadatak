package view;

import javax.swing.JFrame;
import graph.CityGraph;

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

package view;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JComboBox;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.JScrollPane;
import javax.swing.Box;
import java.util.List;
import javax.swing.BorderFactory;

import java.awt.*;

import generator.TransportDataGenerator;
import controller.RouteController;
import graph.TransportGraph;
import model.OptimizationCriteria;
import model.RouteDetails;
import model.RouteSegment;


public class MainFrame extends JFrame {

	private static final long serialVersionUID = 1L;

	private JComboBox<String> cbPolaziste;
	private JComboBox<String> cbOdrediste;
	private JComboBox<String> cbKriterijum;
	private JLabel lblUkupno;
	private JTable table;

	private RouteController controller;
	

	String[] columns = { "Polazak", "Dolazak", "Tip", "Cijena" };
	

	private void updateTable(RouteDetails route) {
	    if (route == null) return;
	    
	    List<RouteSegment> segments = route.getSegments();
	    if (segments == null || segments.isEmpty()) {
	        table.setModel(new DefaultTableModel(new Object[][]{}, columns));
	        lblUkupno.setText("Nema dostupnih segmenata za ovu rutu.");
	        return;
	    }
	    
	    Object[][] data = new Object[segments.size()][columns.length];
	    
	    for (int i = 0; i < segments.size(); i++) {
	        RouteSegment seg = segments.get(i);

	        data[i][0] = seg.getFromStation() + (seg.getDepartureTime() != null ? " (" + seg.getDepartureTime() + ")" : "");
	        data[i][1] = seg.getToStation() + (seg.getArrivalTime() != null ? " (" + seg.getArrivalTime() + ")" : "");
	        data[i][2] = seg.getTransportType();
	        data[i][3] = seg.getFormattedPrice();
	    }
	    
	   
	    
	    table.setModel(new DefaultTableModel(data, columns));
	    
	    
	    lblUkupno.setText(String.format("Ukupno: %s | %s | %d presjedanja", 
	        route.getFormattedTotalTime(), 
	        route.getFormattedTotalCost(), 	
	        route.getTransferCount()));
	}
	



	public MainFrame(TransportDataGenerator.TransportData data) { 
																	

		setSize(900, 700);
		setTitle("Pronalazak rute");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);

		controller = new RouteController(data);
		TransportGraph transportGraph = new TransportGraph(data);



		JPanel mainPanel = new JPanel(new BorderLayout());
		mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		JPanel centerPanel = new JPanel(new BorderLayout());

		cbPolaziste = new JComboBox<>();
		cbOdrediste = new JComboBox<>();


	
		cbKriterijum = new JComboBox<>(new String[] { 
			    OptimizationCriteria.SHORTEST_TIME.getDisplayName(),
			    OptimizationCriteria.LOWEST_COST.getDisplayName(),
			    OptimizationCriteria.FEWEST_TRANSFERS.getDisplayName()
			});
		
		cbKriterijum.setPreferredSize(new Dimension(150, cbKriterijum.getPreferredSize().height));

		controller.initializeComboBoxes(cbPolaziste, cbOdrediste); 

		cbOdrediste.addActionListener(e -> controller.updateStartComboBox(cbPolaziste, cbOdrediste));
		cbPolaziste.addActionListener(e -> controller.updateEndComboBox(cbPolaziste, cbOdrediste));

		JButton btnPronadji = new JButton("Pronađi rutu");
		
		JButton btnDodatneRute = new JButton("Prikaži dodatne rute");
		JButton btnKupi = new JButton("Kupovina karte");

		JPanel topFormPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		topFormPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0), 
																													
																												
				topFormPanel.getBorder()));

		topFormPanel.add(new JLabel("Polazište:"));
		topFormPanel.add(cbPolaziste);
		topFormPanel.add(Box.createHorizontalStrut(20));

		topFormPanel.add(new JLabel("Odredište:"));
		topFormPanel.add(cbOdrediste);
		topFormPanel.add(Box.createHorizontalStrut(20));

		topFormPanel.add(new JLabel("Kriterijum:"));
		topFormPanel.add(cbKriterijum);
		topFormPanel.add(Box.createHorizontalStrut(20));

		topFormPanel.add(btnPronadji);

	
		Object[][] podaci = {}; 
		table = new JTable(podaci, columns);
		table.setBackground(new Color(240, 248, 255));


		btnDodatneRute.addActionListener(e -> {
		    String fromCity = (String) cbPolaziste.getSelectedItem();
		    String toCity   = (String) cbOdrediste.getSelectedItem();
		    String selectedCriteria = (String) cbKriterijum.getSelectedItem();
		    
		    OptimizationCriteria criteria = controller.getCriteriaFromDisplayText(selectedCriteria);

		 

		    AdditionalRoutesFrame additionalFrame = new AdditionalRoutesFrame(fromCity, toCity, criteria, controller);
		    additionalFrame.setVisible(true);  
		    

		});



        
		btnPronadji.addActionListener(e -> {
		    String fromCity = (String) cbPolaziste.getSelectedItem();
		    String toCity = (String) cbOdrediste.getSelectedItem();
		    String selectedCriteria = (String) cbKriterijum.getSelectedItem();

		    OptimizationCriteria criteria = controller.getCriteriaFromDisplayText(selectedCriteria);

		    if (fromCity.equals(toCity)) {
		        JOptionPane.showMessageDialog(this, "Odaberite različite gradove.", "Greška", JOptionPane.ERROR_MESSAGE);
		        return;
		    }

		    RouteDetails bestRoute = controller.findBestRoute(fromCity, toCity, criteria);

		    if (bestRoute == null) {
		        JOptionPane.showMessageDialog(this, "Nema dostupnih ruta između odabranih gradova.", "Informacija", JOptionPane.INFORMATION_MESSAGE);
		        table.setModel(new DefaultTableModel(new Object[][]{}, columns));
		        lblUkupno.setText("Nema dostupnih ruta između " + fromCity + " i " + toCity);
		    } else {
		        updateTable(bestRoute);
		    }
		});

        


        JScrollPane tableScroll = new JScrollPane(table);
        tableScroll.setPreferredSize(new Dimension(0, 200));

        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.add(tableScroll, BorderLayout.CENTER);

        lblUkupno = new JLabel(" Odaberite gradove i kriterijum za pretragu");
        lblUkupno.setFont(lblUkupno.getFont().deriveFont(Font.BOLD, 14f));

        JPanel labelContainer = new JPanel(new BorderLayout());
        labelContainer.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        labelContainer.add(lblUkupno, BorderLayout.CENTER);

        tablePanel.add(labelContainer, BorderLayout.SOUTH);
        centerPanel.add(tablePanel, BorderLayout.NORTH);

        // Create graph panel
        GraphPanel graphPanel = new GraphPanel(transportGraph);
        graphPanel.setBackground(Color.WHITE);
        centerPanel.add(graphPanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 200, 10));
        bottomPanel.add(btnDodatneRute);
        bottomPanel.add(btnKupi);

        mainPanel.add(topFormPanel, BorderLayout.PAGE_START);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        setContentPane(mainPanel);
    }
	
}

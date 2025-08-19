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

public class MainFrame extends JFrame {

	private static final long serialVersionUID = 1L;

	private JComboBox<String> cbPolaziste;
	private JComboBox<String> cbOdrediste;
	private JComboBox<String> cbKriterijum;
	private JLabel lblUkupno;
	private JTable table;

	private RouteController controller;
	
	
	private void updateTable(RouteDetails route) {
	    if (route == null || route.getSegments() == null) return;
	    
	    // Create data array for the table
	    Object[][] data = new Object[route.getSegments().size()][5];
	    for (int i = 0; i < route.getSegments().size(); i++) {
	        model.RouteSegment segment = route.getSegments().get(i);
	        data[i] = new Object[]{
	            segment.getDepartureTime(),
	            segment.getArrivalTime(),
	            segment.getTransportType(),
	            segment.getFormattedDuration(),
	            segment.getFormattedPrice()
	        };
	    }
	    
	    // Update table columns to include duration
	    String[] columns = {"Polazak", "Dolazak", "Tip", "Trajanje", "Cijena"};
	    table.setModel(new DefaultTableModel(data, columns));
	}

	public MainFrame(TransportDataGenerator.TransportData data) { // dakle, u ovo proslijedimo data, koji onda moramo da
																	// proslijedimo u controller

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
	            OptimizationCriteria.SHORTEST_TIME.toString(),
	            OptimizationCriteria.LOWEST_COST.toString(),
	            OptimizationCriteria.FEWEST_TRANSFERS.toString()
	        });
		
		cbKriterijum = new JComboBox<>(new String[] { "Najkraće vrijeme", "Najniža cijena", "Najmanje presjedanja" });
		
		cbKriterijum.setPreferredSize(new Dimension(150, cbKriterijum.getPreferredSize().height));

		controller.initializeComboBoxes(cbPolaziste, cbOdrediste); // trebace se mijenjati

		cbOdrediste.addActionListener(e -> controller.updateStartComboBox(cbPolaziste, cbOdrediste));
		cbPolaziste.addActionListener(e -> controller.updateEndComboBox(cbPolaziste, cbOdrediste));

		JButton btnPronadji = new JButton("Pronađi rutu");
		
		JButton btnDodatneRute = new JButton("Prikaži dodatne rute");
		JButton btnKupi = new JButton("Kupovina karte");

		JPanel topFormPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		topFormPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0), // Bottom
																													// padding
																													// of
																													// 20px
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

		// sada pravimo tabelu
		String[] kolone = { "Polazak", "Dolazak", "Tip", "Cijena" };// mozda ce trebati jos kolona ako se bude gledalo i
																	// vrijeme


//		// Replace your current table data with this expanded version
//		Object[][] podaci = { { "Sarajevo", "Mostar", "Autobus", "20 KM" }, { "Mostar", "Sarajevo", "Voz", "25 KM" },
//				{ "Banja Luka", "Tuzla", "Autobus", "15 KM" }, { "Tuzla", "Zenica", "Voz", "12 KM" },
//				{ "Zenica", "Sarajevo", "Autobus", "10 KM" }, { "Sarajevo", "Neum", "Autobus", "30 KM" },
//				{ "Neum", "Mostar", "Autobus", "18 KM" }, { "Mostar", "Dubrovnik", "Autobus", "35 KM" },
//				{ "Banja Luka", "Doboj", "Voz", "8 KM" }, { "Doboj", "Sarajevo", "Voz", "12 KM" },
//				{ "Tuzla", "Bijeljina", "Autobus", "10 KM" }, { "Bijeljina", "Zvornik", "Autobus", "7 KM" },
//				{ "Zenica", "Travnik", "Autobus", "9 KM" }, { "Travnik", "Jajce", "Autobus", "11 KM" },
//				{ "Jajce", "Banja Luka", "Autobus", "15 KM" }, { "Sarajevo", "Belgrade", "Voz", "40 KM" },
//				{ "Banja Luka", "Zagreb", "Voz", "35 KM" }, { "Mostar", "Split", "Autobus", "25 KM" },
//				{ "Tuzla", "Osijek", "Voz", "30 KM" }, { "Zenica", "Sarajevo", "Autobus", "10 KM" } };
//																										
//																										
//																										
//		table = new JTable(podaci, kolone);
//		table.setBackground(new Color(240, 248, 255));
		
		// In MainFrame constructor, replace the commented table code with:
		Object[][] podaci = {}; // Empty initial data
		table = new JTable(podaci, new String[]{"Polazak", "Dolazak", "Tip", "Trajanje", "Cijena"});
		table.setBackground(new Color(240, 248, 255));

	

		 // btnDodatneRute action listener
        btnDodatneRute.addActionListener(e -> {
            List<RouteDetails> topRoutes = controller.getTopRoutes();
            if (topRoutes != null && !topRoutes.isEmpty()) {
                AdditionalRoutesFrame frame = new AdditionalRoutesFrame(topRoutes);
                frame.setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "Nema pronađenih ruta.", "Informacija", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        
        
        
        // btnPronadji action listener - FIXED VERSION
        btnPronadji.addActionListener(e -> {
            String fromCity = (String) cbPolaziste.getSelectedItem();
            String toCity = (String) cbOdrediste.getSelectedItem();
            String selectedCriteria = (String) cbKriterijum.getSelectedItem();
            
            // Convert string to enum using the controller method
            OptimizationCriteria criteria = controller.getCriteriaFromDisplayText(selectedCriteria);
            
            if (fromCity.equals(toCity)) {
                JOptionPane.showMessageDialog(this, "Odaberite različite gradove.", "Greška", JOptionPane.ERROR_MESSAGE);
                return;
            }

            List<RouteDetails> routes = controller.findRoutes(fromCity, toCity, criteria);
            
            if (routes == null || routes.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Nema dostupnih ruta između odabranih gradova.", "Informacija", JOptionPane.INFORMATION_MESSAGE);
                // Clear table
                table.setModel(new DefaultTableModel(new Object[][]{}, kolone));
                lblUkupno.setText("Nema dostupnih ruta");
            } else {
                // Display the best route
                updateTable(routes.get(0));
                lblUkupno.setText(String.format("Ukupno: %s | %s | %d presjedanja", 
                    routes.get(0).getFormattedTotalTime(), 
                    routes.get(0).getFormattedTotalCost(), 
                    routes.get(0).getTransferCount()));
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

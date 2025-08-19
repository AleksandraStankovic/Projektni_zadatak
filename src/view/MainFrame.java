package view;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JComboBox;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JScrollPane;
import javax.swing.Box;
import javax.swing.BorderFactory;

import java.awt.*;

import generator.TransportDataGenerator;
import controller.RouteController;
import graph.TransportGraph;
import model.OptimizationCriteria;

public class MainFrame extends JFrame {

	private static final long serialVersionUID = 1L;

	private JComboBox<String> cbPolaziste;
	private JComboBox<String> cbOdrediste;
	private JComboBox<String> cbKriterijum;
	private JLabel lblUkupno;
	private JTable table;

	private RouteController controller;

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

		cbKriterijum = new JComboBox<>(new String[] { "Najkraće vrijeme", "Najniža cijena", "Najmanje presjedanja" });
		//sa combo boxom cbKriterijum = new JComboBox<>(OptimizationCriteria.values()); 
		//skontati da li je dobro tako 
		cbKriterijum.setPreferredSize(new Dimension(150, cbKriterijum.getPreferredSize().height));

		controller.initializeComboBoxes(cbPolaziste, cbOdrediste); // trebace se mijenjati

		cbOdrediste.addActionListener(e -> controller.updateStartComboBox(cbPolaziste, cbOdrediste));
		cbPolaziste.addActionListener(e -> controller.updateEndComboBox(cbPolaziste, cbOdrediste));

		JButton btnPronadji = new JButton("Pronađi rutu");
		//dodati action listener da se nesto desi kad se klikne ovo dugme
		JButton btnDodatneRute = new JButton("Prikaži dodatne rute");// ostali dugmići koji nam trebaju
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
		// bice potrebno dodati i vremena, kao i mehanizam za brojanje vremena i
		// brojanje trajanja

		// Replace your current table data with this expanded version
		Object[][] podaci = { { "Sarajevo", "Mostar", "Autobus", "20 KM" }, { "Mostar", "Sarajevo", "Voz", "25 KM" },
				{ "Banja Luka", "Tuzla", "Autobus", "15 KM" }, { "Tuzla", "Zenica", "Voz", "12 KM" },
				{ "Zenica", "Sarajevo", "Autobus", "10 KM" }, { "Sarajevo", "Neum", "Autobus", "30 KM" },
				{ "Neum", "Mostar", "Autobus", "18 KM" }, { "Mostar", "Dubrovnik", "Autobus", "35 KM" },
				{ "Banja Luka", "Doboj", "Voz", "8 KM" }, { "Doboj", "Sarajevo", "Voz", "12 KM" },
				{ "Tuzla", "Bijeljina", "Autobus", "10 KM" }, { "Bijeljina", "Zvornik", "Autobus", "7 KM" },
				{ "Zenica", "Travnik", "Autobus", "9 KM" }, { "Travnik", "Jajce", "Autobus", "11 KM" },
				{ "Jajce", "Banja Luka", "Autobus", "15 KM" }, { "Sarajevo", "Belgrade", "Voz", "40 KM" },
				{ "Banja Luka", "Zagreb", "Voz", "35 KM" }, { "Mostar", "Split", "Autobus", "25 KM" },
				{ "Tuzla", "Osijek", "Voz", "30 KM" }, { "Zenica", "Sarajevo", "Autobus", "10 KM" } };// dummy podaci,
																										// kasnije cemo
																										// ucitati prave
																										// ppodatke
		table = new JTable(podaci, kolone);
		table.setBackground(new Color(240, 248, 255));

		btnDodatneRute.addActionListener(e -> {
			// Primjer podataka za testiranje (inače ćeš povući iz logike rute)
			Object[][] top5Rute = { { "Sarajevo", "Mostar", "Autobus", "20 N.J", "Kupi kartu" },
					{ "Sarajevo", "Tuzla", "Voz", "18 N.J", "Kupi kartu" },
					{ "Mostar", "Banja Luka", "Autobus", "15 N.J", "Kupi kartu" },
					{ "Tuzla", "Banja Luka", "Voz", "25 N.J", "Kupi kartu" },
					{ "Sarajevo", "Banja Luka", "Autobus", "22 N.J", "Kupi kartu" } };

			AdditionalRoutesFrame frame = new AdditionalRoutesFrame(top5Rute);
			frame.setVisible(true);
		});
		
		
		
		//btn pronadji action listener
		
		btnPronadji.addActionListener(e-> {
			String fromCity = (String) cbPolaziste.getSelectedItem();
			String toCity = (String) cbOdrediste.getSelectedItem();
		//dodati za criteria i sve ostalo 
			
		}
		);

		JScrollPane tableScroll = new JScrollPane(table);
		tableScroll.setPreferredSize(new Dimension(0, 200));

		JPanel tablePanel = new JPanel(new BorderLayout());
		tablePanel.add(tableScroll, BorderLayout.CENTER);

		lblUkupno = new JLabel(" Ukupno: 2h 30min | 550 N.J");
		lblUkupno.setFont(lblUkupno.getFont().deriveFont(Font.BOLD, 18f)); // kasnije ćemo da mijenjamo.

		// container za dodavanje paddinga oko labela
		JPanel labelContainer = new JPanel(new BorderLayout());
		labelContainer.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
		labelContainer.add(lblUkupno, BorderLayout.CENTER);

		tablePanel.add(labelContainer, BorderLayout.SOUTH);

		centerPanel.add(tablePanel, BorderLayout.NORTH);

		// kreiramo graf
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

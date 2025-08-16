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

public class MainFrame extends JFrame {

	private static final long serialVersionUID = 1L;

	private JComboBox<String> cbPolaziste;
	private JComboBox<String> cbOdrediste;
	private JComboBox<String> cbKriterijum;
	private JLabel lblUkupno;
	private JTable table;

	private TransportDataGenerator.TransportData transportData;// u ovoj klasi sada imamo objekat koji ima u sebi
																// sacuvane sve podatke o transportu

	/// ovo nam mece trebati ovo je samo za testiranje da li podaci dobro parsiraju
	private void printParsedData() {
		if (transportData == null) {
			System.out.println("No transport data loaded!");
			return;
		}

		System.out.println("=== Stations ===");
		for (TransportDataGenerator.Station s : transportData.stations) {
			System.out.println("City: " + s.city + ", Bus: " + s.busStation + ", Train: " + s.trainStation);
		}

		System.out.println("\n=== Departures ===");
		for (TransportDataGenerator.Departure d : transportData.departures) {
			System.out.println(d.type + " | From: " + d.from + " | To: " + d.to + " | Time: " + d.departureTime
					+ " | Duration: " + d.duration + " | Price: " + d.price + " | MinTransfer: " + d.minTransferTime);
		}

		System.out.println("\n=== Country Map ===");
		for (int i = 0; i < transportData.countryMap.length; i++) {
			for (int j = 0; j < transportData.countryMap[i].length; j++) {
				System.out.print(transportData.countryMap[i][j] + " ");
			}
			System.out.println();
		}
	}

	private void initializeComboBoxes() {
		if (transportData == null || transportData.countryMap == null)
			return;

		int rows = transportData.countryMap.length;
		int cols = transportData.countryMap[0].length;
		String[] cities = new String[rows * cols];
		int index = 0;
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				cities[index++] = transportData.countryMap[i][j];
			}
		}

		cbPolaziste = new JComboBox<>();
		cbOdrediste = new JComboBox<>();

		cbPolaziste.setModel(new javax.swing.DefaultComboBoxModel<>(cities));
		cbOdrediste.setModel(new javax.swing.DefaultComboBoxModel<>(cities));

		cbOdrediste.setPreferredSize(new Dimension(150, cbOdrediste.getPreferredSize().height));
		cbPolaziste.setPreferredSize(new Dimension(150, cbPolaziste.getPreferredSize().height));

		cbKriterijum = new JComboBox<>(new String[] { "Najkraće vrijeme", "Najniža cijena", "Najmanje presjedanja" });
		cbKriterijum.setPreferredSize(new Dimension(150, cbKriterijum.getPreferredSize().height));
	}

	public MainFrame(TransportDataGenerator.TransportData data) {

		this.transportData = data;
		printParsedData();

		setSize(900, 700);
		setTitle("Pronalazak rute");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);

		JPanel mainPanel = new JPanel(new BorderLayout());
		mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		JPanel centerPanel = new JPanel(new BorderLayout());

		// Panel sa combo boxovima.

		initializeComboBoxes(); // trebace se mijenjati

		JButton btnPronadji = new JButton("Pronađi rutu");
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
		GraphPanel graphPanel = new GraphPanel();
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

package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.BorderFactory;
import java.awt.*;
import java.util.List;
import java.io.IOException;

import generator.TransportDataGenerator;
import controller.RouteController;
import graph.TransportGraph;
import graph.CityGraph;
import model.*;

import util.RacunUtil;

public class MainFrame extends JFrame {

	private static final long serialVersionUID = 1L;

	private JComboBox<String> cbPolaziste;
	private JComboBox<String> cbOdrediste;
	private JComboBox<String> cbKriterijum;
	private JLabel lblUkupno;
	private JTable table;
	private RouteDetails lastBestRoute;

	private RouteController controller;

	String[] columns = { "Polazak", "Dolazak", "Tip", "Cijena" };

	private void updateTable(RouteDetails route) {
		if (route == null)
			return;

		List<RouteSegment> segments = route.getSegments();
		if (segments == null || segments.isEmpty()) {
			table.setModel(new DefaultTableModel(new Object[][] {}, columns));
			lblUkupno.setText("Nema dostupnih segmenata za ovu rutu.");
			return;
		}

		Object[][] data = new Object[segments.size()][columns.length];

		for (int i = 0; i < segments.size(); i++) {
			RouteSegment seg = segments.get(i);

			data[i][0] = seg.getFromStation()
					+ (seg.getDepartureTime() != null ? " (" + seg.getDepartureTime() + ")" : "");
			data[i][1] = seg.getToStation() + (seg.getArrivalTime() != null ? " (" + seg.getArrivalTime() + ")" : "");
			data[i][2] = seg.getTransportType();
			data[i][3] = seg.getFormattedPrice();
		}

		table.setModel(new DefaultTableModel(data, columns));

		lblUkupno.setText(String.format("Ukupno: %s | %s | %d presjedanja", route.getFormattedTotalTime(),
				route.getFormattedTotalCost(), route.getTransferCount()));
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
		cbKriterijum = new JComboBox<>(new String[] { OptimizationCriteria.SHORTEST_TIME.getDisplayName(),
				OptimizationCriteria.LOWEST_COST.getDisplayName(),
				OptimizationCriteria.FEWEST_TRANSFERS.getDisplayName() });
		cbKriterijum.setPreferredSize(new Dimension(150, cbKriterijum.getPreferredSize().height));

		controller.initializeComboBoxes(cbPolaziste, cbOdrediste);
		cbPolaziste.addActionListener(e -> controller.updateEndComboBox(cbPolaziste, cbOdrediste));

		Dimension secondaryButtonSize = new Dimension(200, 40);
		Font buttonFont = new Font("Arial", Font.BOLD, 16);

		JButton btnPronadji = new JButton("Pronađi rutu");
		btnPronadji.setBackground(new Color(30, 144, 255));
		btnPronadji.setForeground(Color.WHITE);
		btnPronadji.setFocusPainted(false);
		btnPronadji.setPreferredSize(secondaryButtonSize);

		JButton btnDodatneRute = new JButton("Prikaži dodatne rute");
		btnDodatneRute.setBackground(new Color(139, 195, 74));
		btnDodatneRute.setForeground(Color.WHITE);
		btnDodatneRute.setFocusPainted(true);
		btnDodatneRute.setPreferredSize(secondaryButtonSize);

		JButton btnKupi = new JButton("Kupovina karte");
		btnKupi.setBackground(new Color(139, 195, 74));
		btnKupi.setForeground(Color.WHITE);
		btnKupi.setFocusPainted(true);
		btnKupi.setPreferredSize(secondaryButtonSize);

		JPanel topFormPanel = new JPanel(new GridBagLayout());
		topFormPanel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridy = 0;
		gbc.insets = new Insets(0, 10, 0, 10);

		gbc.gridx = 0;
		topFormPanel.add(new JLabel("Polazište:"), gbc);
		gbc.gridx = 1;
		topFormPanel.add(cbPolaziste, gbc);
		gbc.gridx = 2;
		topFormPanel.add(new JLabel("Odredište:"), gbc);
		gbc.gridx = 3;
		topFormPanel.add(cbOdrediste, gbc);
		gbc.gridx = 4;
		topFormPanel.add(new JLabel("Kriterijum:"), gbc);
		gbc.gridx = 5;
		topFormPanel.add(cbKriterijum, gbc);
		gbc.gridx = 6;
		topFormPanel.add(btnPronadji, gbc);

		Object[][] podaci = {};
		table = new JTable(podaci, columns);
		table.setBackground(new Color(240, 248, 255));
		JScrollPane tableScroll = new JScrollPane(table);
		tableScroll.setPreferredSize(new Dimension(0, 200));

		lblUkupno = new JLabel(" Odaberite gradove i kriterijum za pretragu");
		lblUkupno.setFont(lblUkupno.getFont().deriveFont(Font.BOLD, 14f));

		JPanel labelContainer = new JPanel(new BorderLayout());
		labelContainer.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
		labelContainer.add(lblUkupno, BorderLayout.CENTER);

		JPanel tablePanel = new JPanel(new BorderLayout());
		tablePanel.add(tableScroll, BorderLayout.CENTER);
		tablePanel.add(labelContainer, BorderLayout.SOUTH);
		centerPanel.add(tablePanel, BorderLayout.NORTH);

		JPanel graphButtonsPanel = new JPanel(new GridBagLayout()) {
			private static final long serialVersionUID = 1L;
			private Image backgroundImage = new ImageIcon(getClass().getResource("/resources/background.jpg"))
					.getImage();

			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
			}
		};
		graphButtonsPanel.setOpaque(false);

		JButton btnPrikaziGraf = new JButton("Prikaži graf stanica");
		JButton btnCityGraph = new JButton("Prikaži graf gradova");

		btnPrikaziGraf.setPreferredSize(new Dimension(200, 50));
		btnPrikaziGraf.setFont(buttonFont);
		btnPrikaziGraf.setBackground(new Color(30, 144, 255));
		btnPrikaziGraf.setForeground(Color.WHITE);
		btnPrikaziGraf.setFocusPainted(false);

		btnCityGraph.setPreferredSize(new Dimension(200, 50));
		btnCityGraph.setFont(buttonFont);
		btnCityGraph.setBackground(new Color(30, 144, 255));
		btnCityGraph.setForeground(Color.WHITE);
		btnCityGraph.setFocusPainted(false);

		btnPrikaziGraf.addActionListener(e -> {
			GraphFrame graphFrame = new GraphFrame(transportGraph);
			graphFrame.setVisible(true);
		});

		btnCityGraph.addActionListener(e -> {
			CityGraph cityGraph = new CityGraph(data);
			CityGraphFrame frame = new CityGraphFrame(cityGraph);
			frame.setVisible(true);
		});

		GridBagConstraints gbcGraph = new GridBagConstraints();
		gbcGraph.gridy = 0;
		gbcGraph.gridx = 0;
		gbcGraph.insets = new Insets(10, 10, 10, 10);
		graphButtonsPanel.add(btnPrikaziGraf, gbcGraph);

		gbcGraph.gridx = 1;
		graphButtonsPanel.add(btnCityGraph, gbcGraph);

		centerPanel.add(graphButtonsPanel, BorderLayout.CENTER);

		JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 100, 10));
		bottomPanel.add(btnDodatneRute);
		bottomPanel.add(btnKupi);

		btnPronadji.addActionListener(e -> {
			String fromCity = (String) cbPolaziste.getSelectedItem();
			String toCity = (String) cbOdrediste.getSelectedItem();
			String selectedCriteria = (String) cbKriterijum.getSelectedItem();
			OptimizationCriteria criteria = controller.getCriteriaFromDisplayText(selectedCriteria);

			if (fromCity.equals(toCity)) {
				JOptionPane.showMessageDialog(this, "Odaberite različite gradove.", "Greška",
						JOptionPane.ERROR_MESSAGE);
				return;
			}

			RouteDetails bestRoute = controller.findBestRoute(fromCity, toCity, criteria);
			this.lastBestRoute = bestRoute;

			if (bestRoute == null) {
				JOptionPane.showMessageDialog(this, "Nema dostupnih ruta između odabranih gradova.", "Informacija",
						JOptionPane.INFORMATION_MESSAGE);
				table.setModel(new DefaultTableModel(new Object[][] {}, columns));
				lblUkupno.setText("Nema dostupnih ruta između " + fromCity + " i " + toCity);
			} else {
				updateTable(bestRoute);
				GraphFrame graphFrame = new GraphFrame(transportGraph);
				graphFrame.getGraphPanel().highlightPath(new PathInfo(bestRoute.getPath()));
				graphFrame.setVisible(true);

			}
		});

		btnDodatneRute.addActionListener(e -> {
			String fromCity = (String) cbPolaziste.getSelectedItem();
			String toCity = (String) cbOdrediste.getSelectedItem();
			String selectedCriteria = (String) cbKriterijum.getSelectedItem();
			OptimizationCriteria criteria = controller.getCriteriaFromDisplayText(selectedCriteria);
			AdditionalRoutesFrame additionalFrame = new AdditionalRoutesFrame(fromCity, toCity, criteria, controller);
			additionalFrame.setVisible(true);
		});

		btnKupi.addActionListener(e -> {
			if (lastBestRoute == null) {
				JOptionPane.showMessageDialog(this, "Prvo pronađite rutu za koju želite kupiti kartu.", "Greška",
						JOptionPane.ERROR_MESSAGE);
				return;
			}
			Racun racun = new Racun(this.lastBestRoute);
			try {
				RacunUtil.sacuvajRacun(racun);
				JOptionPane.showMessageDialog(this, "Račun je generisan i sačuvan.\n\n" + racun.generisiRacun(),
						"Karta kupljena", JOptionPane.INFORMATION_MESSAGE);
			} catch (IOException ex) {
				JOptionPane.showMessageDialog(this, "Greška pri čuvanju računa: " + ex.getMessage(), "Greška",
						JOptionPane.ERROR_MESSAGE);
			}
		});

		mainPanel.add(topFormPanel, BorderLayout.PAGE_START);
		mainPanel.add(centerPanel, BorderLayout.CENTER);
		mainPanel.add(bottomPanel, BorderLayout.SOUTH);

		setContentPane(mainPanel);
	}
}

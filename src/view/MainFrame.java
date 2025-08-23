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
import javax.swing.ImageIcon;

import java.util.List;
import javax.swing.BorderFactory;
import util.RacunUtil;

import java.awt.*;

import generator.TransportDataGenerator;
import controller.RouteController;
import graph.TransportGraph;
import model.OptimizationCriteria;
import model.RouteDetails;
import model.RouteSegment;

import model.PathInfo;

import java.io.IOException;

import model.Racun;

public class MainFrame extends JFrame {

	private static final long serialVersionUID = 1L;

	private JComboBox<String> cbPolaziste;
	private JComboBox<String> cbOdrediste;
	private JComboBox<String> cbKriterijum;
	private JLabel lblUkupno;
	private JTable table;
	private RouteDetails lastBestRoute;
	//private Racun racun; 


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

		//cbOdrediste.addActionListener(e -> controller.updateStartComboBox(cbPolaziste, cbOdrediste));
		cbPolaziste.addActionListener(e -> controller.updateEndComboBox(cbPolaziste, cbOdrediste));
		
		Dimension SecondaryButtonSize = new Dimension(200, 40);

		JButton btnPronadji = new JButton("Pronađi rutu");
		btnPronadji.setBackground(new Color(30, 144, 255)); 
		btnPronadji.setForeground(Color.WHITE);
		btnPronadji.setFocusPainted(false);
		btnPronadji.setPreferredSize(SecondaryButtonSize);

		
		
		JButton btnDodatneRute = new JButton("Prikaži dodatne rute");
		btnDodatneRute.setBackground(new Color(139, 195, 74)); 
		btnDodatneRute.setForeground(Color.WHITE);
		btnDodatneRute.setFocusPainted(true);

		JButton btnKupi = new JButton("Kupovina karte");
		btnKupi.setBackground(new Color(139, 195, 74)); 
		btnKupi.setForeground(Color.WHITE);
		btnKupi.setFocusPainted(true);
		btnDodatneRute.setPreferredSize(SecondaryButtonSize);
		btnKupi.setPreferredSize(SecondaryButtonSize);
		
		
		

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
			String toCity = (String) cbOdrediste.getSelectedItem();
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
				JOptionPane.showMessageDialog(this, "Odaberite različite gradove.", "Greška",
						JOptionPane.ERROR_MESSAGE);
				return;
			}

			RouteDetails bestRoute = controller.findBestRoute(fromCity, toCity, criteria);
			this.lastBestRoute = bestRoute;//best route moramo da proslijedimo u evenet handler za kupovinu karte

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
		
		
	JButton btnPrikaziGraf = new JButton("Prikaži graf");
		
		btnPrikaziGraf.addActionListener (e->
		{
			GraphFrame graphFrame = new GraphFrame(transportGraph);//prosledjujemo graf
			graphFrame.setVisible(true);
		}
		);
		
		
		btnKupi.addActionListener(e->
		{
			if (lastBestRoute == null) {
		        JOptionPane.showMessageDialog(this, 
		            "Prvo pronađite rutu za koju želite kupiti kartu.", 
		            "Greška", 
		            JOptionPane.ERROR_MESSAGE);
		        return;
		    }
			
			  Racun racun = new Racun(this.lastBestRoute);
			  try {
			        // Create the receipt
			       // Racun racun = new Racun(this.lastBestRoute);
			        
			        // Save it using RacunUtil
			        RacunUtil.sacuvajRacun(racun);

			        // Optionally, show confirmation with the receipt
			        JOptionPane.showMessageDialog(this, 
			            "Račun je generisan i sačuvan.\n\n" + racun.generisiRacun(), 
			            "Karta kupljena", 
			            JOptionPane.INFORMATION_MESSAGE);

			    } catch (IOException ex) {
			        JOptionPane.showMessageDialog(this, 
			            "Greška pri čuvanju računa: " + ex.getMessage(), 
			            "Greška", 
			            JOptionPane.ERROR_MESSAGE);
			    }
			  

			  
			  
		}
		
		);

;

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
		
		
		// Panel for buttons with background color
		JPanel graphButtonsPanel = new JPanel(new GridBagLayout()) {
		    /**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			private Image backgroundImage = new ImageIcon(
				    getClass().getResource("/resources/background.jpg")
				).getImage();

		    // 🔼 adjust the path to your image file

		    @Override
		    protected void paintComponent(Graphics g) {
		        super.paintComponent(g);
		        // Draw image scaled to panel size
		        g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
		    }
		};
		
		graphButtonsPanel.setOpaque(false); 
		
		
		
		graphButtonsPanel.setBackground(new Color(220, 220, 220));

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.insets = new Insets(10, 10, 10, 10);

		graphButtonsPanel.add(btnPrikaziGraf, gbc);

		gbc.gridy = 1;
		
		centerPanel.add(graphButtonsPanel, BorderLayout.CENTER);

		Dimension buttonSize = new Dimension(200, 50);

		Font buttonFont = new Font("Arial", Font.BOLD, 16);//idk, dodati ovo myb u posebnu neku metodu idk....

		btnPrikaziGraf.setPreferredSize(buttonSize);
		btnPrikaziGraf.setFont(buttonFont);
		btnPrikaziGraf.setBackground(new Color(30, 144, 255)); 
		btnPrikaziGraf.setForeground(Color.WHITE);
		btnPrikaziGraf.setFocusPainted(false);

	


		JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 200, 10));
		bottomPanel.add(btnDodatneRute);
		bottomPanel.add(btnKupi);

		mainPanel.add(topFormPanel, BorderLayout.PAGE_START);
		mainPanel.add(centerPanel, BorderLayout.CENTER);
		mainPanel.add(bottomPanel, BorderLayout.SOUTH);

		setContentPane(mainPanel);
	}

}

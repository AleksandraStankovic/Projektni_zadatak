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

public class MainFrame extends JFrame {

	private static final long serialVersionUID = 1L;
	private int rows; //ovo mozda i nece trebati ovdje, tj vjerovatno nece trebati, al neka ga za sad. , lako cemo obrisati I guess
	private int cols;
	
	//idk ovo kasnije obrisati ovo za rows i cols, vjerovatno nece trebati 
	
	// atributi, koji će ustvari biti elementi koji će se ovjde koristiti.
	private JComboBox<String> cbPolaziste;// combo boxovi za izbor odredista i polazista i kriterijuma
	private JComboBox<String> cbOdrediste;
	private JComboBox<String> cbKriterijum;
	private JLabel lblUkupno;
	private JTable table;

	public MainFrame(int rows, int cols) {// konstruktor. Uzima vrijednosti rowa i columns od prvog prozora
		// ovdje sada definisemo sta se desava prilikom pokretanja prozora i definisemo
		// prikazivanje elemenata na njemu
		this.rows = rows;
		this.cols = cols;

		// samo za testiranje, kasnije cemo ove vrijednosti da proslijedimo za
		// generisanje grafa i generisanje stanica

		System.out.println("rows:" + rows);
		System.out.println("cols:" + cols);

		setSize(900, 700);
		setTitle("Pronalazak rute");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		// setLayout(new BorderLayout());

		// Create the main panel with BorderLayout //Main Panel u koji ce da ide sve
		// ostalo
		JPanel mainPanel = new JPanel(new BorderLayout());
		mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		// mainPanel.add(topPanel);//kreiramo top panel sa statistikom i dodamo ga u
		// main panel

		// centralni panel sa grafom i tabelom
		JPanel centerPanel = new JPanel(new BorderLayout());

		// Panel sa combo boxovima.
		cbPolaziste = new JComboBox<>(new String[] { "A_0_0", "B_0_1", "C_1_0" }); // ovdje ce kasnije ici drugaciji
																					// prikaz tj ici ce mozda neka druga
																					// metoda koja dodaje parsirane
																					// elemente
		cbPolaziste.setPreferredSize(new Dimension(150, cbPolaziste.getPreferredSize().height));
		cbOdrediste = new JComboBox<>(new String[] { "A_0_0", "B_0_1", "C_1_0" }); // moze poziv neke metode.
		cbOdrediste.setPreferredSize(new Dimension(150, cbOdrediste.getPreferredSize().height));
		// Trebalo bi da kad se izabere polaziste, da se ono izbrise iz liste odredista,
		// jer nema smisla da se trazi da se ide iz istog u isti grad
		cbKriterijum = new JComboBox<>(new String[] { "Najkraće vrijeme", "Najniža cijena", "Najmanje presjedanja" });
		cbKriterijum.setPreferredSize(new Dimension(150, cbKriterijum.getPreferredSize().height));

		// dodajemo dugmice za sta nam vec trebaju
		JButton btnPronadji = new JButton("Pronađi rutu");
		JButton btnDodatneRute = new JButton("Prikaži dodatne rute");// ostali dugmići koji nam trebaju
		JButton btnKupi = new JButton("Kupovina karte");

		// gore smo definisali dugmice i ostalo, ovdje sada sve dodamo u ovaj panel top
		// form panel
		JPanel topFormPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		topFormPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0), // Bottom
																													// padding
																													// of
																													// 20px
				topFormPanel.getBorder() // Keep any existing border
		));

		// ovako je napravljeno tako da imamo custom spacing izmedju komponetni, kasnije
		// moze jos da se popravi i guess...
		topFormPanel.add(new JLabel("Polazište:"));
		topFormPanel.add(cbPolaziste);
		topFormPanel.add(Box.createHorizontalStrut(20)); // 20px space

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
		//bice potrebno dodati i vremena, kao i mehanizam za brojanje vremena i brojanje trajanja

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
																										// trebace
																										// mijenjati i
																										// kolone
																										// treba isto
																										// tako da pise
																										// koliko je
																										// ukupno cijena
																										// i ukupno
																										// trajanje
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

		JScrollPane tableScroll = new JScrollPane(table);// dodamo novi scroll panel sa tabelom
		tableScroll.setPreferredSize(new Dimension(0, 200)); // Fixed height for table

		// centralni panel je za tabelu i za graf

		JPanel tablePanel = new JPanel(new BorderLayout());
		tablePanel.add(tableScroll, BorderLayout.CENTER);

		// dodavanje lbla za ukupnp

		lblUkupno = new JLabel(" Ukupno: 2h 30min | 550 N.J");
		lblUkupno.setFont(lblUkupno.getFont().deriveFont(Font.BOLD, 18f)); // kasnije ćemo da mijenjamo.

		// container za dodavanje paddinga oko labela
		JPanel labelContainer = new JPanel(new BorderLayout());
		labelContainer.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0)); // Top, left, bottom, right padding
		labelContainer.add(lblUkupno, BorderLayout.CENTER);

		// Add to the table panel
		tablePanel.add(labelContainer, BorderLayout.SOUTH);

		centerPanel.add(tablePanel, BorderLayout.NORTH);// u centralni panel dodamo onda ovaj scroll panel

		// kreiramo graf
		// Graph panel - will stretch to fill remaining space
		GraphPanel graphPanel = new GraphPanel();
		graphPanel.setBackground(Color.WHITE);
		centerPanel.add(graphPanel, BorderLayout.CENTER);// u centralni panel onda dodamo ovaj graph panel

//bottom panel sa dva dugmeta
		JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 200, 10));
		bottomPanel.add(btnDodatneRute);
		bottomPanel.add(btnKupi);

		mainPanel.add(topFormPanel, BorderLayout.PAGE_START);
		mainPanel.add(centerPanel, BorderLayout.CENTER);
		mainPanel.add(bottomPanel, BorderLayout.SOUTH);

		setContentPane(mainPanel);

	}
}

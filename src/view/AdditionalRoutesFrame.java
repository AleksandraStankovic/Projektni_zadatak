package view;
import model.Racun;
import util.RacunUtil;

import java.awt.*;//i guess da je ovo za action listeners

import javax.swing.*;

import java.awt.event.*;
import java.io.IOException;

import javax.swing.table.*;

public class AdditionalRoutesFrame extends JFrame {
	private static final long serialVersionUID = 1L;

	// private JTable tabela; //da li nam ovo treba ovjde ili je okej da
	// instanciramo samo kad budemo koristili
	// ne treba ovako jer kasnije necemo da mijenjamo nista na ovoj tabeli, vec samo
	// da koristimo za prikazivanje podataka i ruta koje se dobiju

	// konsturktor za ovu klasu
	public AdditionalRoutesFrame(Object[][] rutePodaci) {// uzimace listu objekata rutePodaci o rutama koje ce onda
															// kasnije da renderuje i da prikazuje
		// ovo je dvodimenzinonalni niz gdje svaki red predstavlja jednu rutu
		// u kolonama su pojedinacni podaci o rutama. Red objekata so i guess da ce
		// svaka ruta biti objekat.

		setTitle("Top 5 ruta");
		setSize(700, 400);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		// kolone tabele koje su niz stringova
		String[] kolone = { "Polazak", "Dolazak", "Tip", "Cijena", "Akcija" };// trebace nam mozda jos nesto tj trebati
																				// dok skontam, tj presjedanja ako se
																				// budu trazila
																				// idk...

		// sve ovo mozda moze da se doda u neki poseban panel
		JLabel lblHeading = new JLabel("Top 5 ruta");
		lblHeading.setAlignmentX(Component.CENTER_ALIGNMENT);
		lblHeading.setFont(lblHeading.getFont().deriveFont(Font.BOLD, 24f));

		JTable tabela = new JTable(rutePodaci, kolone)// uzima podatke i header
		{
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCellEditable(int row, int column) {
				return column == 4; // oznacimo da samo kolona akcija moze biti interkativna
			}
		};

		tabela.setRowHeight(30);
		DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
		centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
		centerRenderer.setVerticalAlignment(SwingConstants.CENTER);

		for (int i = 0; i < tabela.getColumnCount(); i++) {
			tabela.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
		}

		// Pretvori zadnju kolonu u dugme

		tabela.getColumn("Akcija").setCellRenderer(new ButtonRenderer());
		tabela.getColumn("Akcija").setCellEditor(new ButtonEditor(new JCheckBox(), tabela));

		JPanel mainPanel = new JPanel();

		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		JScrollPane scrollPane = new JScrollPane(tabela);
		scrollPane.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

		mainPanel.add(Box.createVerticalStrut(20)); // 20 pixels of vertical space
		mainPanel.add(lblHeading);
		mainPanel.add(scrollPane);

		setContentPane(mainPanel);

	}

	// Private inner class for button rendering in the tabela cell
	private class ButtonRenderer extends JButton implements TableCellRenderer {
		private static final long serialVersionUID = 1L;

		public ButtonRenderer() {
			setOpaque(true);
		}

		@Override
		public Component getTableCellRendererComponent(JTable tabela, Object value, boolean isSelected,
				boolean hasFocus, int row, int column) {
			setText((value == null) ? "Kupi kartu" : value.toString());
			return this;
		}
	}

	private class ButtonEditor extends DefaultCellEditor {
		private static final long serialVersionUID = 1L;
		protected JButton button;
		private String label;
		private boolean clicked;
		private JTable table;

		public ButtonEditor(JCheckBox checkBox, JTable table) {
			super(checkBox);
			this.table = table;
			button = new JButton();
			button.setOpaque(true);

			button.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					int row = table.getEditingRow(); // get current editing row
					if (row >= 0) {
						// Assuming "Polazak" is column 0 and "Dolazak" is column 1
						Object polazak = table.getValueAt(row, 0);
						Object dolazak = table.getValueAt(row, 1);

						JOptionPane.showMessageDialog(AdditionalRoutesFrame.this,
								"Karta kupljena za rutu " + polazak + " - " + dolazak);

					}
					fireEditingStopped();
				}
			});
		}

		@Override
		public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row,
				int column) {
			label = (value == null) ? "Kupi kartu" : value.toString();
			button.setText(label);
			clicked = true;
			return button;
		}

		//idk, ovdje moramo da promijenimo nesto da se ova vrijednost proslijedi ili idk, da se nesto pozove...
		@Override
		public Object getCellEditorValue() {
			//ako se klikne, uzimaju se vrijednosti iz tog reda i formira se objekat racun
			 if (clicked) {//ovo ovdje ce isto trebati drugacije, jer nama redovi nece biti jedna ruta, n ego ce jedna ruta ici u vise redova, tako da ce ovo morati malo drugacije, dugme kupi nece morati da bude u svakom redu
		            int row = table.getEditingRow();//ovo logika za generisanje racuna ce biti malo drugacija kod generisanja racuna one jedne rute, tj bice ista donekle sto se tice nekih info, ovdje cemo jedino mozda samo za krajnje odrednice, a tamo cemo za sve
		            String polazak = table.getValueAt(row, 0).toString();//idk, skontati da li racun ide samo za prvu ili za pocetnu stanicu ili ne. 
		            String odrediste = table.getValueAt(row, 1).toString();//logika za kupovinu ovuh ruta ce biti komplikovanija, jer je kupuje karta po redovima, a na glavnom prozoru imamo samo kupovinu karte za tu jednu rutu. 
		            String cijena = table.getValueAt(row, 3).toString();
		            String vrijeme = "2h 30min"; // Replace with actual time from route data
		            
		            Racun racun = new Racun(polazak, odrediste, cijena, vrijeme);
		            
		            try {
		                RacunUtil.sacuvajRacun(racun);
		                //JOptionPane.showMessageDialog(AdditionalRoutesFrame.this,
		                    //"Račun je sačuvan u folderu 'racuni'.");
		            } catch (IOException e) {
		                JOptionPane.showMessageDialog(AdditionalRoutesFrame.this,
		                    "Greška pri čuvanju računa: " + e.getMessage(), "Greška", JOptionPane.ERROR_MESSAGE);
		}
	}
			 clicked = false;
		        return label;
		}

}
}

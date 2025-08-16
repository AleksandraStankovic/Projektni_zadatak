package view;

import generator.TransportDataGenerator;
import generator.TransportDataParser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.Box;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.SwingConstants;
import javax.swing.JOptionPane;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class StartupFrame extends JFrame {
	private static final long serialVersionUID = 1L;
	private JTextField rowsField;
	private JTextField colsField;
	private JLabel lblProdato;
	private JLabel lblZarada;

	public StartupFrame() {
		setTitle("Unos dimenzija matrice");
		setSize(400, 400);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		setLayout(new BorderLayout(10, 10));

		JPanel topPanel = new JPanel();
		topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
		topPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
		JPanel centerPanel = new JPanel();
		centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));

		lblProdato = new JLabel("Prodato karata: 0", SwingConstants.CENTER);
		lblProdato.setFont(lblProdato.getFont().deriveFont(Font.BOLD, 18f));
		lblProdato.setAlignmentX(Component.CENTER_ALIGNMENT);

		lblZarada = new JLabel("Ukupna zarada: 0 N.J", SwingConstants.CENTER);
		lblZarada.setFont(lblZarada.getFont().deriveFont(Font.BOLD, 18f));
		lblZarada.setAlignmentX(Component.CENTER_ALIGNMENT);

		centerPanel.add(Box.createVerticalGlue());
		centerPanel.add(lblProdato);
		centerPanel.add(Box.createVerticalStrut(10));
		centerPanel.add(lblZarada);
		centerPanel.add(Box.createVerticalGlue());

		topPanel.add(centerPanel);
		JPanel inputPanel = new JPanel(new GridLayout(2, 2, 5, 5));
		inputPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));

		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
		JButton startButton = new JButton("Kreiraj matricu");

		// dodavanje lbl i polja za unos
		inputPanel.add(new JLabel("Broj redova:"));
		rowsField = new JTextField("");
		inputPanel.add(rowsField);

		inputPanel.add(new JLabel("Broj kolona:"));
		colsField = new JTextField("");// ovdje ce terbati biti unos a ne ovako definisano
		inputPanel.add(colsField);

		rowsField.addActionListener(e -> startButton.doClick());
		colsField.addActionListener(e -> startButton.doClick());
		getRootPane().setDefaultButton(startButton);

		startButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {// idk, ovdje negdje jos dodati to da pritiskom na enter se
														// aktivira ovo dugme
				try {
					int rows = Integer.parseInt(rowsField.getText());
					int cols = Integer.parseInt(colsField.getText());

					// Set dimensions in TransportDataGenerator
					TransportDataGenerator.setDimensions(rows, cols);

					// Generate and save the data
					TransportDataGenerator.generateAndSaveData();

					// parse data from json
					TransportDataGenerator.TransportData data = TransportDataParser.parse("transport_data.json");

					dispose();
					new MainFrame(data).setVisible(true);

				} catch (NumberFormatException ex) {
					JOptionPane.showMessageDialog(StartupFrame.this, "Unesite validne pozitivne cijele brojeve",
							"Greška pri unosu", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		buttonPanel.add(startButton);

		JPanel mainPanel = new JPanel(new BorderLayout());

		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		mainPanel.add(topPanel, BorderLayout.NORTH);
		mainPanel.add(inputPanel, BorderLayout.CENTER);
		mainPanel.add(buttonPanel, BorderLayout.SOUTH);

		setContentPane(mainPanel);

	}

}

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
import util.RacunUtil;
import java.io.IOException;

/**
 * The startup frame for initializing the transport matrix and displaying sales
 * statistics.
 * 
 * <p>
 * This frame allows the user to input the number of rows and columns for the
 * transport matrix, generate the transport data, and launch the main
 * application window. It also displays the total number of tickets sold and the
 * total revenue generated.
 * </p>
 * 
 * <p>
 * The frame contains:
 * <ul>
 * <li>Text fields for entering the number of rows and columns of the transport
 * matrix.</li>
 * <li>A button to generate the transport matrix and launch the
 * {@link MainFrame}.</li>
 * <li>Labels showing the total tickets sold and total revenue.</li>
 * </ul>
 * </p>
 * 
 * <p>
 * The transport data is generated using TransportDataGenerator and parsed using
 * TransportDataParser. Sales statistics are retrieved using RacunUtil.
 * </p>
 */

public class StartupFrame extends JFrame {
	private static final long serialVersionUID = 1L;
	private JTextField rowsField;
	private JTextField colsField;
	private JLabel lblUkupno;
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

		lblUkupno = new JLabel("Prodato karata: 0", SwingConstants.CENTER);
		lblUkupno.setFont(lblUkupno.getFont().deriveFont(Font.BOLD, 18f));
		lblUkupno.setAlignmentX(Component.CENTER_ALIGNMENT);

		lblZarada = new JLabel("Ukupna zarada: 0 N.J", SwingConstants.CENTER);
		lblZarada.setFont(lblZarada.getFont().deriveFont(Font.BOLD, 18f));
		lblZarada.setAlignmentX(Component.CENTER_ALIGNMENT);

		try {
			lblUkupno = new JLabel("Prodato karata: " + RacunUtil.getBrojKarata(), SwingConstants.CENTER);
			lblUkupno.setFont(lblUkupno.getFont().deriveFont(Font.BOLD, 18f));
			lblUkupno.setAlignmentX(Component.CENTER_ALIGNMENT);
		} catch (IOException ex) {
			lblUkupno = new JLabel("Prodato karata: 0", SwingConstants.CENTER);
			lblUkupno.setFont(lblUkupno.getFont().deriveFont(Font.BOLD, 18f));
			lblUkupno.setAlignmentX(Component.CENTER_ALIGNMENT);
			JOptionPane.showMessageDialog(this, "Greška pri učitavanju statistike karata", "I/O Greška",
					JOptionPane.ERROR_MESSAGE);
		}

		try {
			lblZarada = new JLabel("Ukupna zarada: " + RacunUtil.getUkupanPrihod() + " N.J.", SwingConstants.CENTER);
			lblZarada.setFont(lblUkupno.getFont().deriveFont(Font.BOLD, 18f));
			lblZarada.setAlignmentX(Component.CENTER_ALIGNMENT);

		} catch (IOException ex) {
			lblZarada = new JLabel("Prodato karata: 0", SwingConstants.CENTER);
			lblZarada.setFont(lblUkupno.getFont().deriveFont(Font.BOLD, 18f));
			lblZarada.setAlignmentX(Component.CENTER_ALIGNMENT);
			JOptionPane.showMessageDialog(this, "Greška pri učitavanju statistike karata", "I/O Greška",
					JOptionPane.ERROR_MESSAGE);
		}

		centerPanel.add(Box.createVerticalGlue());
		centerPanel.add(lblUkupno);
		centerPanel.add(Box.createVerticalStrut(10));
		centerPanel.add(lblZarada);
		centerPanel.add(Box.createVerticalGlue());

		topPanel.add(centerPanel);
		JPanel inputPanel = new JPanel(new GridLayout(2, 2, 5, 5));
		inputPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));
		Dimension buttonSize = new Dimension(200, 50);

		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
		JButton startButton = new JButton("Kreiraj matricu");
		startButton.setBackground(new Color(30, 144, 255));
		startButton.setForeground(Color.WHITE);
		startButton.setFocusPainted(true);
		startButton.setPreferredSize(buttonSize);

		inputPanel.add(new JLabel("Broj redova:"));
		rowsField = new JTextField("");
		inputPanel.add(rowsField);

		inputPanel.add(new JLabel("Broj kolona:"));
		colsField = new JTextField("");
		inputPanel.add(colsField);

		rowsField.addActionListener(e -> startButton.doClick());
		colsField.addActionListener(e -> startButton.doClick());
		getRootPane().setDefaultButton(startButton);

		startButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					int rows = Integer.parseInt(rowsField.getText().trim());
					int cols = Integer.parseInt(colsField.getText().trim());

					if (rows <= 0 || cols <= 0) {
						JOptionPane.showMessageDialog(StartupFrame.this,
								"Broj redova i kolona mora biti pozitivan cijeli broj", "Greška pri unosu",
								JOptionPane.ERROR_MESSAGE);
						return;
					}

					TransportDataGenerator.setDimensions(rows, cols);
					TransportDataGenerator.generateAndSaveData();
					TransportDataGenerator.TransportData data = TransportDataParser.parse("transport_data.json");

					dispose();
					new MainFrame(data).setVisible(true);

				} catch (NumberFormatException ex) {
					JOptionPane.showMessageDialog(StartupFrame.this, "Unesite validne pozitivne cijele brojeve",
							"Greška pri unosu", JOptionPane.ERROR_MESSAGE);
				} catch (IllegalArgumentException ex) {
					JOptionPane.showMessageDialog(StartupFrame.this, ex.getMessage(), "Greška pri unosu dimenzija",
							JOptionPane.ERROR_MESSAGE);
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

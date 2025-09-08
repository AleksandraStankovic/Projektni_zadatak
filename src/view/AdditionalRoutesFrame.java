package view;

import model.Racun;
import util.RacunUtil;

import java.awt.*;

import javax.swing.*;

import java.awt.event.*;
import java.io.IOException;

import javax.swing.table.*;
import java.util.List;
import model.RouteDetails;
import controller.RouteController;
import model.OptimizationCriteria;
import model.RouteSegment;
import java.util.HashMap;
import java.util.Map;

/**
 * Creates a new frame displaying the top K routes between two cities.
 * 
 * <p>
 * The frame shows a table with route segments including departure and arrival
 * stations, transport type, price, and a button to purchase a ticket. Each
 * route is color-coded for easier visualization. When the "Kupi kartu" button
 * is clicked, a receipt is generated and saved, and a message dialog displays
 * the ticket details.
 * </p>
 *
 * @param fromCity   the starting city of the route
 * @param toCity     the destination city of the route
 * @param criteria   the optimization criteria used to determine top routes
 * @param controller the controller used to fetch route data
 */

public class AdditionalRoutesFrame extends JFrame {
	private static final long serialVersionUID = 1L;

	private Map<Integer, RouteDetails> rowToRouteMap = new HashMap<>();

	private JTable table;

	String[] columns = { "Ruta", "Polazak", "Dolazak", "Tip", "Cijena", "Akcija" };

	public AdditionalRoutesFrame(String fromCity, String toCity, OptimizationCriteria criteria,
			RouteController controller) {

		setTitle("Top 5 ruta");
		setSize(700, 400);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		List<RouteDetails> routes = controller.findTopKRoutes(fromCity, toCity, criteria, 5);

		if (routes == null || routes.isEmpty()) {
			JOptionPane.showMessageDialog(this, "Nema dostupnih ruta između odabranih gradova.", "Informacija",
					JOptionPane.INFORMATION_MESSAGE);
			table = new JTable(new DefaultTableModel(new Object[][] {}, new String[] { "Informacija", "Vrijednost" }));
		} else {

			int totalRows = routes.stream().mapToInt(r -> r.getSegments().size()).sum();
			Object[][] data = new Object[totalRows][columns.length];

			int rowIndex = 0;
			for (int r = 0; r < routes.size(); r++) {
				RouteDetails route = routes.get(r);
				List<RouteSegment> segments = route.getSegments();
				for (RouteSegment seg : segments) {
					data[rowIndex][0] = "Ruta " + (r + 1);
					data[rowIndex][1] = seg.getFromStation()
							+ (seg.getDepartureTime() != null ? " (" + seg.getDepartureTime() + ")" : "");
					data[rowIndex][2] = seg.getToStation()
							+ (seg.getArrivalTime() != null ? " (" + seg.getArrivalTime() + ")" : "");
					data[rowIndex][3] = seg.getTransportType();
					data[rowIndex][4] = seg.getFormattedPrice();
					data[rowIndex][5] = "Kupi kartu";
					rowToRouteMap.put(rowIndex, route);
					rowIndex++;
				}
			}

			DefaultTableModel model = new DefaultTableModel(data, columns) {
				private static final long serialVersionUID = 1L;

				@Override
				public boolean isCellEditable(int row, int column) {
					return column == 5;
				}
			};

			table = new JTable(model);

			DefaultTableCellRenderer routeRenderer = new DefaultTableCellRenderer() {

				private static final long serialVersionUID = 1L;

				@Override
				public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
						boolean hasFocus, int row, int col) {
					Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);

					setHorizontalAlignment(SwingConstants.CENTER);
					setVerticalAlignment(SwingConstants.CENTER);

					try {
						int routeNumber = Integer.parseInt(table.getValueAt(row, 0).toString().replace("Ruta ", ""));
						Color[] colors = { new Color(230, 240, 255), new Color(255, 230, 240), new Color(240, 255, 230),
								new Color(255, 255, 200), new Color(240, 200, 255) };
						c.setBackground(colors[(routeNumber - 1) % colors.length]);
					} catch (Exception e) {
						c.setBackground(Color.WHITE);
					}

					return c;
				}
			};

			for (int i = 0; i < table.getColumnCount(); i++) {
				table.getColumnModel().getColumn(i).setCellRenderer(routeRenderer);
			}

			table.getColumn("Akcija").setCellRenderer(new ButtonRenderer());
			table.getColumn("Akcija").setCellEditor(new ButtonEditor(new JCheckBox(), table));

			table.setRowHeight(30);
		}

		JLabel lblHeading = new JLabel("Top 5 ruta");
		lblHeading.setAlignmentX(Component.CENTER_ALIGNMENT);
		lblHeading.setFont(lblHeading.getFont().deriveFont(Font.BOLD, 24f));

		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		JScrollPane scrollPane = new JScrollPane(table);
		scrollPane.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

		mainPanel.add(Box.createVerticalStrut(20));
		mainPanel.add(lblHeading);
		mainPanel.add(scrollPane);

		setContentPane(mainPanel);
	}

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
					int row = table.getEditingRow();
					if (row >= 0) {

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

		@Override

		public Object getCellEditorValue() {
			if (clicked) {
				int row = table.getEditingRow();
				RouteDetails route = rowToRouteMap.get(row);
				if (route != null) {
					Racun racun = new Racun(route);
					try {
						RacunUtil.sacuvajRacun(racun);

					} catch (IOException e) {

						System.err.println("Greška pri čuvanju računa: " + e.getMessage());
					}
				}
			}
			clicked = false;
			return label;
		}

	}
}

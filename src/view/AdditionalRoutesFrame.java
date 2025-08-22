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




public class AdditionalRoutesFrame extends JFrame {
	private static final long serialVersionUID = 1L;


    
    
	private JTable table;
    
	String[] columns = {"Ruta", "Polazak", "Dolazak", "Tip", "Cijena" , "Akcija"}; //dodati jos kolona za ukupnu cijenu i ukupno trajanje
    

//	private void updateTable(List<RouteDetails> routes) {
//	    if (routes == null || routes.isEmpty()) {
//	        table.setModel(new DefaultTableModel(new Object[][]{}, new String[]{"Informacija"}));
//	        
//	        return;
//	    }
//
//	    
//	    Object[][] data = new Object[Math.min(5, routes.size())][4];
//
//	    for (int i = 0; i < Math.min(5, routes.size()); i++) {
//	        RouteDetails route = routes.get(i);
//	        data[i][0] = route.getFormattedTotalTime();
//	        data[i][1] = route.getFormattedTotalCost();
//	        data[i][2] = route.getTransferCount();
//	    }
//
//	    table.setModel(new DefaultTableModel(data, columns));
//	   
//	}
	
	public AdditionalRoutesFrame(String fromCity, String toCity, OptimizationCriteria criteria,
            RouteController controller) {

setTitle("Top 5 ruta");
setSize(700, 400);
setLocationRelativeTo(null);
setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

List<RouteDetails> routes = controller.findTopKRoutes(fromCity, toCity, criteria, 5);

if (routes == null || routes.isEmpty()) {
JOptionPane.showMessageDialog(this, "Nema dostupnih ruta između odabranih gradova.", 
                     "Informacija", JOptionPane.INFORMATION_MESSAGE);
table = new JTable(new DefaultTableModel(new Object[][]{}, new String[]{"Informacija", "Vrijednost"}));
} else {

// Count total rows
int totalRows = routes.stream().mapToInt(r -> r.getSegments().size()).sum();
Object[][] data = new Object[totalRows][columns.length];

int rowIndex = 0;
for (int r = 0; r < routes.size(); r++) {
RouteDetails route = routes.get(r);
List<RouteSegment> segments = route.getSegments();
for (RouteSegment seg : segments) {
data[rowIndex][0] = "Ruta " + (r + 1);
data[rowIndex][1] = seg.getFromStation() + (seg.getDepartureTime() != null ? " (" + seg.getDepartureTime() + ")" : "");
data[rowIndex][2] = seg.getToStation() + (seg.getArrivalTime() != null ? " (" + seg.getArrivalTime() + ")" : "");
data[rowIndex][3] = seg.getTransportType();
data[rowIndex][4] = seg.getFormattedPrice();
data[rowIndex][5] = "Kupi kartu";
rowIndex++;
}
}

DefaultTableModel model = new DefaultTableModel(data, columns) {
private static final long serialVersionUID = 1L;

@Override
public boolean isCellEditable(int row, int column) {
return column == 5; // Only "Akcija" column editable
}
};

table = new JTable(model);

// Renderer for route colors + centered text
DefaultTableCellRenderer routeRenderer = new DefaultTableCellRenderer() {
/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

@Override
public Component getTableCellRendererComponent(JTable table, Object value,
                                          boolean isSelected, boolean hasFocus, int row, int col) {
Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);


setHorizontalAlignment(SwingConstants.CENTER);
setVerticalAlignment(SwingConstants.CENTER);


try {
   int routeNumber = Integer.parseInt(table.getValueAt(row, 0).toString().replace("Ruta ", ""));
   Color[] colors = {
       new Color(230, 240, 255),
       new Color(255, 230, 240),
       new Color(240, 255, 230),
       new Color(255, 255, 200),
       new Color(240, 200, 255)
   };
   c.setBackground(colors[(routeNumber - 1) % colors.length]);
} catch (Exception e) {
   c.setBackground(Color.WHITE);
}

return c;
}
};

// Apply renderer to all columns
for (int i = 0; i < table.getColumnCount(); i++) {
table.getColumnModel().getColumn(i).setCellRenderer(routeRenderer);
}

// Button column
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

	



//	private Object[] appendHiddenColumn(String[] columns) {
//		// TODO Auto-generated method stub
//		return null;
//	}





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
			
			 if (clicked) {//drugacije imlementirati ovu logiku za kupovinu racuna
		            int row = table.getEditingRow();
		            String polazak = table.getValueAt(row, 0).toString();
		            String odrediste = table.getValueAt(row, 1).toString();
		            String cijena = table.getValueAt(row, 3).toString();
		            String vrijeme = "2h 30min"; 
		            
		            
		            
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

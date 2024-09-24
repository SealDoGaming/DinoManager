import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableCellRenderer;

public class AttackCellRenderer extends DefaultTableCellRenderer {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {

	    //Cells are by default rendered as a JLabel.
	    JLabel cell = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);

	    //Get the status for the current row.
	    DinoTableModel tableModel = (DinoTableModel) table.getModel();
	    cell.setBackground(tableModel.getColorAt(row, col));
	    //System.out.println(row+","+col+" - "+tableModel.getColorAt(row, col));
	  //Return the JLabel which renders the cell.
	    return cell;
	    
	}
}

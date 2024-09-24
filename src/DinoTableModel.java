import java.awt.Color;
import java.util.ArrayList;

import javax.swing.table.AbstractTableModel;

public class DinoTableModel extends AbstractTableModel  {
    private String[] columnNames;
    private ArrayList<Object[]> data = new ArrayList<Object[]>();
    private ArrayList<Color[]> colorData = new ArrayList<Color[]>();;
    
    public DinoTableModel(String[] columnNames) {
    	this.columnNames = columnNames;
	}
    public DinoTableModel(String[] columnNames,ArrayList<Object[]> data) {
    	this.columnNames = columnNames;
    	this.data = data;
    	for(int i=0;i<data.size();i++) {
    		Color[] colorRow = new Color[data.get(i).length];
    		for(int j=0;j<colorRow.length;j++) {
    			colorRow[j] = DinoManager.defaultColor;
    		}
    		colorData.add(colorRow);
    	}
    	//System.out.println(data);
    	//System.out.println(colorData);
    }
    public void setData(ArrayList<Object[]> data) {
    	this.data = data;
    }
    public void addRow(Object[] newRow) {
    	data.add(newRow);
		Color[] colorRow = new Color[newRow.length];
		for(int j=0;j<colorRow.length;j++) {
			colorRow[j] = DinoManager.defaultColor;
		}
    	colorData.add(colorRow);
    	
    }
    public void removeRow() {
    	removeRow(data.size()-1);
    }
    public void removeRow(int index) {
    	data.remove(index);
    }
    public void setRow(int index,Object[] newRow) {
    	data.set(index,newRow);
    	Color[] colorRow = new Color[newRow.length];
		for(int j=0;j<colorRow.length;j++) {
			colorRow[j] = DinoManager.defaultColor;
		}
    	//System.out.print((String[])newRow);
		//System.out.print(colorRow);
    	colorData.add(colorRow);
    	
    }
    public void setColorAt(int row, int col,Color color) {
    	colorData.get(row)[col] = color;
    }
    public Object[] getRow(int row) {
    	return data.get(row);
    }
    public ArrayList<Object[]> getData() {
    	return data;
    }
    
    public int getColumnCount() {
        return columnNames.length;
    }

    public int getRowCount() {
        return data.size();
    }

    public String getColumnName(int col) {
        return columnNames[col];
    }

    public Object getValueAt(int row, int col) {
        return data.get(row)[col];
    }
    public Color getColorAt(int row, int col) {
    	if(colorData.size()==0) {
    		System.out.println("Whelp I tried");
    		return Color.ORANGE;
    	}
    	return colorData.get(row)[col];
    }

    public Class getColumnClass(int c) {
    	if(c==1) {
    		return Object.class;
    	}

        return getValueAt(0, c).getClass();
    }

    /*
     * Don't need to implement this method unless your table's
     * editable.
     */
    public boolean isCellEditable(int row, int col) {
        //Note that the data/cell address is constant,
        //no matter where the cell appears onscreen.
        if (col > 2) {
            return false;
        } else {
            return true;
        }
    }

    /*
     * Don't need to implement this method unless your table's
     * data can change.
     */
    
    public void setValueAt(Object value, int row, int col) {
    	//System.out.println(colorData);
    	if(col==1) {
    		boolean isInt;
    		try {
    			System.out.println(value);
    			value= Integer.valueOf((String)value);
    			isInt = true;
    		}catch(Exception e) {
    			isInt = false;
    		}
    		if(!isInt){
    			//System.out.println(value.getClass());
    			try {
        			String valueText = (String)value;
        			String sign = "";
        			if(valueText.contains("+")) {
        				sign = "+";
        			}else if(valueText.contains("-")) {
        				sign = "-";
        			}
        			String[] splitValue = valueText.split("\\"+sign);
        			int num1 = Integer.valueOf(splitValue[0]);
        			int num2 = Integer.valueOf(splitValue[1]);
        			if(sign=="+") {
        				value = num1+num2;
        			}else if(sign == "-") {
        				value = num1 - num2;
        			}else{
        				throw new Exception();
        			}
        		}catch(Exception e){
        			System.out.println("That's not a valid number");
        			System.out.println(e);
        			return;
        		}
    			
    		}
    		
    	}
        
    	
        data.get(row)[col] = value;
        fireTableCellUpdated(row, col);
        return;
    }
}

package window;

import java.awt.Component;

import javax.swing.*;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableModel;

/**
 * @author Mahdi, Jaberzadeh Ansari
 * @role This class contains functions for Docx2Fidus tab in GUI. The functions
 *       here are used for selecting items in map table.
 */
public class MapTableCellEditor extends AbstractCellEditor implements TableCellEditor {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5L;
	private JComboBox<String> editor; // the combo box that helps user to select
										// the mapped value
	private static boolean opened = false;
	/**
	 * possible format in FW
	 */
	private String[] values = { "Normal Text", "Bold Text", "Italic Text", "Title", "Subtitle", "Authors", "Abstract",
			"Keywords", "1st Headline", "2nd Headline", "3rd Headline", "4th Headline", "5th Headline", "6th Headline",
			"Code", "Hyperlink", "Numbered List", "Bulleted List", "Blockquote", "Footnote", "Citation", "Math",
			"Figure", "Caption" };

	/**
	 * Stops editing behaviour of the cell and store the selected value.
	 */
	final public void writeSelectedValueInCell() {
		this.stopCellEditing();
	}

	/**
	 * Creates a new Combobox with the array of values.
	 */
	public MapTableCellEditor() {
		editor = new JComboBox<String>(values);
		editor.addPopupMenuListener(new PopupMenuListener() {
			public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
				if (MapTableCellEditor.opened == true) {
					MapTableCellEditor.opened = false;
					writeSelectedValueInCell();
				}
			}

			public void popupMenuCanceled(PopupMenuEvent e) {
				MapTableCellEditor.opened = false;
			}

			public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
				MapTableCellEditor.opened = true;
			}
		});
	}

	/**
	 * Form the table. If a cell selected then makes a dropdown in that cell.
	 */
	@Override
	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int rowIndex,
			int colIndex) {
		TableModel model = table.getModel();
		// Set the model data of the table
		if (isSelected) {
			editor.setSelectedItem(value);
			model.setValueAt(value, rowIndex, colIndex);
		} else {
			String currentValue = model.getValueAt(rowIndex, colIndex).toString();
			editor.setSelectedItem(currentValue);
		}

		return editor;
	}

	/**
	 * Returns the selected value of the dropdown item in cell editor
	 */
	@Override
	public Object getCellEditorValue() {
		return editor.getSelectedItem();
	}
}

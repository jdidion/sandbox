package net.didion.pml.editor;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;

import net.didion.pml.PropertyModel;
import net.didion.pml.PropertyModelAdapter;
import net.didion.pml.editor.dialogs.BooleanEditorDialogFactory;
import net.didion.pml.editor.dialogs.DefaultEditorDialogFactory;
import net.didion.pml.editor.dialogs.FixedValueEditorDialogFactory;
import net.didion.pml.model.AllowedValues;
import net.didion.pml.model.Property;
import net.didion.pml.model.PropertyDescriptor;
import net.didion.pml.model.PropertyGroup;
import net.didion.pml.util.properties.Properties;
import net.didion.pml.util.swing.AbstractETableModel;
import net.didion.pml.util.swing.ETable;

public class ConstantsPanel extends JPanel {
	private static final JLabel EDIT_ICON = new JLabel(new ImageIcon("images/edit.gif"));
	private static final JLabel DISABLED_EDIT_ICON = new JLabel(new ImageIcon("images/edit_disabled.gif"));
    private static final JLabel VARIABLE_ICON = new JLabel(new ImageIcon("images/var.gif"));
	private static final JLabel DISABLED_VARIABLE_ICON = new JLabel(new ImageIcon("images/var_disabled.gif"));
	private static final List<EditorDialogFactory> EDITOR_DIALOGS = new ArrayList<EditorDialogFactory>();
	
	static {
		EDITOR_DIALOGS.add(new FixedValueEditorDialogFactory());
		EDITOR_DIALOGS.add(new BooleanEditorDialogFactory());
		EDITOR_DIALOGS.add(new DefaultEditorDialogFactory());
	}
	
	private final JFrame frame;
	private final PropertyModel model;
	private Component currentPropertyPanel;
	
	public ConstantsPanel(final JFrame parent, final PropertyModel model) {
		this.frame = parent;
		this.model = model;
		
		final PropertyDescriptor pd = model.getPropertyDescriptor();
		
		setLayout(new BorderLayout());
		
		if (pd.getGroup() != null && !pd.getGroup().isEmpty()) {
		    final JSplitPane split = new JSplitPane();
		    
		    final Map<String, List<Property>> groups = new HashMap<String, List<Property>>(pd.getGroup().size()+1);
			final Map<String, Component> propertyPanels = new HashMap<String, Component>();
			
			final Vector<String> groupNames = new Vector<String>(pd.getGroup().size()+1);
			for (PropertyGroup pg : pd.getGroup()) {
			    groupNames.add(pg.getName());
			    groups.put(pg.getName(), pg.getProperty());
			}
			Collections.sort(groupNames);
			if (pd.getProperty() != null && !pd.getProperty().isEmpty()) {
    			groupNames.insertElementAt(Properties.DEFAULT_GROUP_NAME, 0);
    			groups.put(Properties.DEFAULT_GROUP_NAME, pd.getProperty());
			}
			
			final JList groupList = new JList(groupNames);
			groupList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			groupList.addListSelectionListener(new ListSelectionListener() {
				public void valueChanged(ListSelectionEvent e) {
				    if (e.getValueIsAdjusting()) {
				        return;
				    }
				    final int[] selection = groupList.getSelectedIndices();
					final String pg = groupNames.get(selection[0]);
					Component panel = propertyPanels.get(pg);
					if (null == panel) {
						panel = createPropertyPanel(groups.get(pg));
						propertyPanels.put(pg, panel);
					}
					if (null != ConstantsPanel.this.currentPropertyPanel) {
					    split.remove(ConstantsPanel.this.currentPropertyPanel);
					}
					ConstantsPanel.this.currentPropertyPanel = panel;
					split.add(ConstantsPanel.this.currentPropertyPanel, JSplitPane.RIGHT);
					split.setDividerLocation(150);
				}
			});
			groupList.setSelectedIndex(0);
			
            split.add(new JScrollPane(groupList), JSplitPane.LEFT);
            split.setDividerLocation(150);
            add(split, BorderLayout.CENTER);
		}
		else {
		    this.currentPropertyPanel = createPropertyPanel(pd.getProperty());
			add(this.currentPropertyPanel, BorderLayout.CENTER);
		}
	}
	
	private Component createPropertyPanel(final List<Property> properties) {
	    final List<Property> expanded = new ArrayList<Property>(properties.size());
        addProperties(properties, expanded);
        
		final ETable table = new ETable(new PropertyTableModel(expanded));
		final TableColumnModel columns = table.getColumnModel();
        columns.getColumn(2).setMaxWidth(10);
        columns.getColumn(3).setMaxWidth(10);
		final TableCellRenderer defaultRenderer = table.getDefaultRenderer(JButton.class);
		table.setDefaultRenderer(Property.class, new TableCellRenderer() {
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, 
														   boolean hasFocus, int row, int column) {
				/*
			    if (value instanceof Component) {
					return (Component) value;
				}
				return defaultRenderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
				*/
			    final Property property = (Property) value;
			    switch (column) {
	            case 0:
	                final String name = (property.getDisplayName() != null) ? property.getDisplayName() : property.getName();
	                return defaultRenderer.getTableCellRendererComponent(table, name, isSelected, hasFocus, row, column);
	            case 1:
	                final String propertyValue;
	                if (ConstantsPanel.this.model.isVariable(property)) {
	                    propertyValue = "<variable>";
	                }
	                else {
	                    propertyValue = ConstantsPanel.this.model.getValue(property);
	                }
	                return defaultRenderer.getTableCellRendererComponent(table, propertyValue, isSelected, hasFocus, row, column);
	            case 2:
	                return ConstantsPanel.this.model.isEditable(property) ? EDIT_ICON : DISABLED_EDIT_ICON;
	            case 3:
	                // TODO: fix this limitation
	                return (ConstantsPanel.this.model.isEditable(property) && !ConstantsPanel.this.model.isParent(property)) ? 
	                    VARIABLE_ICON : DISABLED_VARIABLE_ICON;
	            default:
	                throw new RuntimeException("Invalid column: " + column);
	            }
			}
		});
		table.addMouseListener(new MouseAdapter() {
		    public void mouseClicked(MouseEvent e) {
	            final TableColumnModel columnModel = table.getColumnModel();
	            
	            final int column = columnModel.getColumnIndexAtX(e.getX());
	            if (column >= table.getColumnCount() || column < 2) {
	                return;
	            }
	       
	            final int row = e.getY() / table.getRowHeight();
	            if (row >= table.getRowCount() || row < 0) { 
	                return;
	            }
	    
	            final Property property = expanded.get(row);
	            switch (column) {
	            case 2: {
	                if (!ConstantsPanel.this.model.isEditable(property)) {
	                    return;
	                }
	                final String currentValue = ConstantsPanel.this.model.getValue(property);
	                final EditorDialogFactory dialog = createEditorDialog(property);
	                final String value = dialog.editSingleValue(ConstantsPanel.this.frame, property, currentValue);
	                if (null != value) {
	                    ConstantsPanel.this.model.setValue(property, value);
	                }
	                break;
	            }
	            case 3: {
	                // TODO: fix this limitation
	                if (!ConstantsPanel.this.model.isEditable(property) || ConstantsPanel.this.model.isParent(property)) {
	                    return;
	                }
	                final List<String> currentValues = (ConstantsPanel.this.model.isVariable(property)) ?
	                    ConstantsPanel.this.model.getVariableValues(property) : null;
	                final EditorDialogFactory dialog = createEditorDialog(property);
	                final List<String> values = dialog.editMultipleValues(
	                    ConstantsPanel.this.frame, property, currentValues);
	                if (null != values) {
	                    ConstantsPanel.this.model.setVariableValues(property, values);
	                }
	                break;
	            }
	            }
	        }
		});
		return new JScrollPane(table);
	}
	
	private void addProperties(List<Property> source, List<Property> dest) {
        for (Property property : source) {
            dest.add(property);
            if (property.getAllowedValues() != null && !property.getAllowedValues().isEmpty()) {
                for (AllowedValues av : property.getAllowedValues()) {
                    if (av.getProperty() != null && !av.getProperty().isEmpty()) {
                        addProperties(av.getProperty(), dest);
                    }
                }
            }
        }
    }
	
	private EditorDialogFactory createEditorDialog(Property property) {
		for (EditorDialogFactory dialog : EDITOR_DIALOGS) {
			if (dialog.edits(property)) {
				return dialog;
			}
		}
		throw new RuntimeException("No EditorDialog for property: " + property.getName());
	}
	
	private class PropertyTableModel extends AbstractETableModel {
		private final String[] COLUMN_NAMES = { "Name", "Value", "", "" };
		private final List<Property> properties;
		
		private PropertyTableModel(List<Property> properties) {
			this.properties = properties;
			// it's kinda wierd to put this here, but JTable doesn't have any public API for updating specific rows
			ConstantsPanel.this.model.addListener(new PropertyModelAdapter() {
	            @Override
	            public void propertyEnabled(Property property) {
	                final int index = PropertyTableModel.this.properties.indexOf(property);
	                if (index >= 0) {
	                    fireTableRowsUpdated(index, index);
	                }
	            }
	            
	            @Override
	            public void propertyDisabled(Property property) {
	                final int index = PropertyTableModel.this.properties.indexOf(property);
	                if (index >= 0) {
	                    fireTableRowsUpdated(index, index);
	                }
	            }           
	        });
		}
		
		public String getColumnName(int column) {
			return COLUMN_NAMES[column];
		}
		
		public int getColumnCount() {
			return COLUMN_NAMES.length;
		}

		public int getRowCount() {
			return this.properties.size();
		}

		public Object getValueAt(int rowIndex, int columnIndex) {
		    final Property property = this.properties.get(rowIndex);
			return property;
		    /*
			switch (columnIndex) {
			case 0:
				return (property.getDisplayName() != null) ? property.getDisplayName() : property.getName();
			case 1:
				if (ConstantsPanel.this.model.isVariable(property)) {
					return "<variable>";
				}
				else {
					return model.getValue(property);
				}
			case 2:
			    return ConstantsPanel.this.model.isEnabled(property) ? EDIT_ICON : DISABLED_EDIT_ICON;
			case 3:
			    // TODO: fix this limitation
			    return (ConstantsPanel.this.model.isEnabled(property) && !ConstantsPanel.this.model.isParent(property)) ? 
			        VARIABLE_ICON : DISABLED_VARIABLE_ICON;
			default:
			    throw new RuntimeException("Invalid column: " + columnIndex);
			}
			*/
		}

		@Override
		public Class<?> getColumnClass(int columnIndex) {
		    final Object value = getValueAt(0, columnIndex);
		    return (null == value) ? String.class : value.getClass();
		}

        @Override
        public boolean isEnabled(int row) {
            final Property p = this.properties.get(row);
            return ConstantsPanel.this.model.isEnabled(p);
        }
	}
}

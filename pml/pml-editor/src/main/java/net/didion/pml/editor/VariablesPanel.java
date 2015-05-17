package net.didion.pml.editor;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractListModel;
import javax.swing.ComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;

import net.didion.pml.PropertyModel;
import net.didion.pml.PropertyModelAdapter;
import net.didion.pml.model.Property;
import net.didion.pml.util.swing.AbstractETableModel;
import net.didion.pml.util.swing.ETable;

public class VariablesPanel extends JPanel {
    public VariablesPanel(JFrame parent, final PropertyModel model) {
        setLayout(new BorderLayout());
        
        final ETable table = new ETable(new PropertyTableModel(model));
        /*
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
        
                switch (column) {
                
                }
            }
        });
        */
        final JScrollPane scroll = new JScrollPane(table);
        //scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        add(scroll, BorderLayout.CENTER);
        
        final JPanel deletePanel = new JPanel();
        deletePanel.setAlignmentX(LEFT_ALIGNMENT);
        
        final JComboBox deleteChoice = new JComboBox(new PropertyComboBoxModel(model));
        final ListCellRenderer r = deleteChoice.getRenderer();
        deleteChoice.setRenderer(new ListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
                    boolean cellHasFocus) {
                final Property property = (Property) value;
                final String name = (null == property) ? null : 
                    (property.getDisplayName() == null) ? property.getName() : property.getDisplayName();
                return r.getListCellRendererComponent(list, name, index, isSelected, cellHasFocus);
            }
        });
        deletePanel.add(deleteChoice);
        
        final JButton deleteButton = new JButton("Delete");
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                model.removeVariable((Property) deleteChoice.getSelectedItem());
            }
        });
        deletePanel.add(deleteButton);
        
        add(deletePanel, BorderLayout.SOUTH);
    }
    
    private class PropertyTableModel extends AbstractETableModel {
        final PropertyModel propertyModel;
        
        private PropertyTableModel(PropertyModel propertyModel) {
            this.propertyModel = propertyModel;
            this.propertyModel.addListener(new PropertyModelAdapter() {
                @Override
                public void variablePropertyAdded(Property property, String oldValue, List<String> newValue) {
                    fireTableStructureChanged();
                }

                @Override
                public void variablePropertyRemoved(Property property, List<String> oldValue, String newValue) {
                    fireTableStructureChanged();
                }

                @Override
                public void propertyEnabled(Property property) {
                    fireTableStructureChanged();
                }

                @Override
                public void propertyDisabled(Property property) {
                    fireTableStructureChanged();
                }
            });
        }
        
        public int getColumnCount() {
            return this.propertyModel.getNumVariables();
        }
        
        public String getColumnName(int column) {
            final Property property = this.propertyModel.getVariableProperty(column); 
            return (property.getDisplayName() == null) ? property.getName() : property.getDisplayName();
        }
        
        public int getRowCount() {
            return this.propertyModel.getNumVariableCombinations();
        }

        public Object getValueAt(int rowIndex, int columnIndex) {
            return this.propertyModel.getVariableRow(rowIndex).get(columnIndex);
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            return String.class;
        }
    }
    
    private class PropertyComboBoxModel extends AbstractListModel implements ComboBoxModel {
        private PropertyModel propertyModel;
        private List<Property> variableProperties;
        private Property selectedProperty;
        
        public PropertyComboBoxModel(PropertyModel propertyModel) {
            this.propertyModel = propertyModel;
            this.variableProperties = new ArrayList<Property>(propertyModel.getVariableProperties());
            this.propertyModel.addListener(new PropertyModelAdapter() {
                @Override
                public void variablePropertyAdded(Property property, String oldValue, List<String> newValue) {
                    final int index = PropertyComboBoxModel.this.variableProperties.size();
                    PropertyComboBoxModel.this.variableProperties.add(property);
                    fireIntervalAdded(this, index, index);
                }

                @Override
                public void variablePropertyRemoved(Property property, List<String> oldValue, String newValue) {
                    final int index = PropertyComboBoxModel.this.variableProperties.indexOf(property);
                    PropertyComboBoxModel.this.variableProperties.remove(property);
                    fireIntervalRemoved(this, index, index);
                }
            });
        }

        @Override
        public Object getSelectedItem() {
            return this.selectedProperty;
        }

        @Override
        public void setSelectedItem(Object item) {
            this.selectedProperty = (Property) item;
        }

        @Override
        public Object getElementAt(int index) {
            return this.variableProperties.get(index);
        }

        @Override
        public int getSize() {
            return this.propertyModel.getNumVariables();
        }
    }
}

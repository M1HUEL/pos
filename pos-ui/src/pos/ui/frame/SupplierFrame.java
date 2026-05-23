package pos.ui.frame;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import pos.supplier.model.Supplier;
import pos.ui.controller.SupplierController;

public class SupplierFrame extends JFrame {

  private final SupplierController controller;

  private JTextField nameField;
  private JTextField contactNameField;
  private JTextField phoneField;
  private JTextField emailField;
  private JTextField addressField;
  private JCheckBox activeCheckBox;

  private JTextField searchField;
  private DefaultTableModel tableModel;
  private JTable table;
  private TableRowSorter<DefaultTableModel> sorter;
  private final List<String> rowSupplierIds = new ArrayList<>();

  private JButton saveButton;
  private JButton updateButton;
  private JButton deleteButton;

  private String editingSupplierId = null;

  public SupplierFrame(SupplierController controller) {
    this.controller = controller;
    initComponents();
    layoutComponents();
    configureFrame();
    refreshTable();
  }

  private void initComponents() {
    nameField = new JTextField(20);
    contactNameField = new JTextField(20);
    phoneField = new JTextField(20);
    emailField = new JTextField(20);
    addressField = new JTextField(20);
    activeCheckBox = new JCheckBox("Active", true);

    searchField = new JTextField(25);

    tableModel = new DefaultTableModel(
      new String[]{"Name", "Contact", "Phone", "Email", "Address", "Active"}, 0) {
      @Override
      public boolean isCellEditable(int row, int col) {
        return false;
      }
    };
    table = new JTable(tableModel);
    table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    table.setRowHeight(24);
    table.getColumnModel().getColumn(0).setPreferredWidth(150);
    table.getColumnModel().getColumn(1).setPreferredWidth(120);
    table.getColumnModel().getColumn(2).setPreferredWidth(100);
    table.getColumnModel().getColumn(3).setPreferredWidth(150);
    table.getColumnModel().getColumn(4).setPreferredWidth(150);
    table.getColumnModel().getColumn(5).setPreferredWidth(60);

    sorter = new TableRowSorter<>(tableModel);
    table.setRowSorter(sorter);

    table.getSelectionModel().addListSelectionListener(e -> {
      if (!e.getValueIsAdjusting()) {
        handleTableSelection();
      }
    });

    searchField.getDocument().addDocumentListener(new DocumentListener() {
      public void insertUpdate(DocumentEvent e) {
        applyFilter();
      }

      public void removeUpdate(DocumentEvent e) {
        applyFilter();
      }

      public void changedUpdate(DocumentEvent e) {
        applyFilter();
      }
    });

    saveButton = new JButton("Save Supplier");
    updateButton = new JButton("Update");
    deleteButton = new JButton("Delete");
    updateButton.setEnabled(false);
    deleteButton.setEnabled(false);

    saveButton.addActionListener(e -> handleSave());
    updateButton.addActionListener(e -> handleUpdate());
    deleteButton.addActionListener(e -> handleDelete());
  }

  private void layoutComponents() {
    setLayout(new BorderLayout(8, 8));
    ((JPanel) getContentPane()).setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    add(buildFormPanel(), BorderLayout.NORTH);
    add(buildTablePanel(), BorderLayout.CENTER);
    add(buildButtonPanel(), BorderLayout.SOUTH);
  }

  private JPanel buildFormPanel() {
    JPanel panel = new JPanel(new GridBagLayout());
    panel.setBorder(new TitledBorder("Supplier Information"));

    GridBagConstraints lbl = new GridBagConstraints();
    lbl.anchor = GridBagConstraints.EAST;
    lbl.insets = new Insets(6, 10, 6, 6);

    GridBagConstraints fld = new GridBagConstraints();
    fld.anchor = GridBagConstraints.WEST;
    fld.fill = GridBagConstraints.HORIZONTAL;
    fld.weightx = 1.0;
    fld.insets = new Insets(6, 0, 6, 10);
    fld.gridwidth = GridBagConstraints.REMAINDER;

    lbl.gridy = 0;
    panel.add(new JLabel("Name:"), lbl);
    fld.gridy = 0;
    panel.add(nameField, fld);
    lbl.gridy = 1;
    panel.add(new JLabel("Contact Name:"), lbl);
    fld.gridy = 1;
    panel.add(contactNameField, fld);
    lbl.gridy = 2;
    panel.add(new JLabel("Phone:"), lbl);
    fld.gridy = 2;
    panel.add(phoneField, fld);
    lbl.gridy = 3;
    panel.add(new JLabel("Email:"), lbl);
    fld.gridy = 3;
    panel.add(emailField, fld);
    lbl.gridy = 4;
    panel.add(new JLabel("Address:"), lbl);
    fld.gridy = 4;
    panel.add(addressField, fld);
    lbl.gridy = 5;
    panel.add(new JLabel("Status:"), lbl);
    fld.gridy = 5;
    panel.add(activeCheckBox, fld);

    return panel;
  }

  private JPanel buildTablePanel() {
    JPanel panel = new JPanel(new BorderLayout(4, 4));
    panel.setBorder(new TitledBorder("Suppliers"));

    JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
    searchPanel.add(new JLabel("Search:"));
    searchPanel.add(searchField);
    panel.add(searchPanel, BorderLayout.NORTH);

    JScrollPane scroll = new JScrollPane(table);
    scroll.setPreferredSize(new Dimension(700, 180));
    panel.add(scroll, BorderLayout.CENTER);

    return panel;
  }

  private JPanel buildButtonPanel() {
    JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 8));
    JButton clearButton = new JButton("Clear");
    clearButton.addActionListener(e -> handleClear());
    panel.add(clearButton);
    panel.add(deleteButton);
    panel.add(updateButton);
    panel.add(saveButton);
    return panel;
  }

  private void applyFilter() {
    String text = searchField.getText().trim();
    sorter.setRowFilter(text.isEmpty()
      ? null
      : RowFilter.regexFilter("(?i)" + Pattern.quote(text)));
  }

  private void handleTableSelection() {
    int viewRow = table.getSelectedRow();
    if (viewRow < 0) {
      editingSupplierId = null;
      updateButton.setEnabled(false);
      deleteButton.setEnabled(false);
      return;
    }
    int modelRow = table.convertRowIndexToModel(viewRow);
    editingSupplierId = rowSupplierIds.get(modelRow);
    nameField.setText((String) tableModel.getValueAt(modelRow, 0));
    contactNameField.setText((String) tableModel.getValueAt(modelRow, 1));
    phoneField.setText((String) tableModel.getValueAt(modelRow, 2));
    emailField.setText((String) tableModel.getValueAt(modelRow, 3));
    addressField.setText((String) tableModel.getValueAt(modelRow, 4));
    activeCheckBox.setSelected((Boolean) tableModel.getValueAt(modelRow, 5));
    updateButton.setEnabled(true);
    deleteButton.setEnabled(true);
  }

  private void handleSave() {
    String name = nameField.getText().trim();
    if (name.isEmpty()) {
      showError("Name is required.");
      return;
    }
    try {
      Supplier created = controller.createSupplier(
        name,
        contactNameField.getText().trim(),
        phoneField.getText().trim(),
        emailField.getText().trim(),
        addressField.getText().trim(),
        activeCheckBox.isSelected());
      JOptionPane.showMessageDialog(this,
        "Supplier '" + created.getName() + "' saved successfully.",
        "Supplier Saved", JOptionPane.INFORMATION_MESSAGE);
      handleClear();
      refreshTable();
    } catch (Exception e) {
      showError("Failed to save supplier: " + e.getMessage());
    }
  }

  private void handleUpdate() {
    if (editingSupplierId == null) {
      return;
    }
    String name = nameField.getText().trim();
    if (name.isEmpty()) {
      showError("Name is required.");
      return;
    }
    try {
      controller.updateSupplier(
        editingSupplierId,
        name,
        contactNameField.getText().trim(),
        phoneField.getText().trim(),
        emailField.getText().trim(),
        addressField.getText().trim(),
        activeCheckBox.isSelected());
      JOptionPane.showMessageDialog(this,
        "Supplier updated successfully.",
        "Supplier Updated", JOptionPane.INFORMATION_MESSAGE);
      handleClear();
      refreshTable();
    } catch (Exception e) {
      showError("Failed to update supplier: " + e.getMessage());
    }
  }

  private void handleDelete() {
    if (editingSupplierId == null) {
      return;
    }
    int confirm = JOptionPane.showConfirmDialog(this,
      "Delete supplier '" + nameField.getText().trim() + "'?",
      "Confirm Delete", JOptionPane.YES_NO_OPTION);
    if (confirm != JOptionPane.YES_OPTION) {
      return;
    }
    try {
      controller.deleteSupplier(editingSupplierId);
      handleClear();
      refreshTable();
    } catch (Exception e) {
      showError("Failed to delete supplier: " + e.getMessage());
    }
  }

  private void handleClear() {
    editingSupplierId = null;
    nameField.setText("");
    contactNameField.setText("");
    phoneField.setText("");
    emailField.setText("");
    addressField.setText("");
    activeCheckBox.setSelected(true);
    table.clearSelection();
    updateButton.setEnabled(false);
    deleteButton.setEnabled(false);
    nameField.requestFocus();
  }

  private void refreshTable() {
    tableModel.setRowCount(0);
    rowSupplierIds.clear();
    for (Supplier s : controller.getAllSuppliers()) {
      tableModel.addRow(new Object[]{
        s.getName(),
        s.getContactName(),
        s.getPhone(),
        s.getEmail(),
        s.getAddress(),
        s.getActive()
      });
      rowSupplierIds.add(s.getId());
    }
  }

  private void showError(String message) {
    JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
  }

  private void configureFrame() {
    setTitle("Suppliers");
    setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
    pack();
    setMinimumSize(new Dimension(750, 580));
    setLocationRelativeTo(null);
  }
}

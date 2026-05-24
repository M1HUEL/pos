package pos.ui.frame;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import pos.product.model.Product;
import pos.supplier.model.Supplier;
import pos.ui.controller.ProductController;
import pos.ui.listener.SupplierChangeListener;

public class ProductFrame extends JFrame implements SupplierChangeListener {

  private final ProductController controller;

  private JTextField skuField;
  private JTextField nameField;
  private JTextField descriptionField;
  private JTextField priceField;
  private JComboBox<Object> supplierCombo;
  private JCheckBox activeCheckBox;

  private JTextField searchField;
  private DefaultTableModel tableModel;
  private JTable table;
  private TableRowSorter<DefaultTableModel> sorter;
  private final List<String> rowProductIds = new ArrayList<>();

  private JButton saveButton;
  private JButton updateButton;
  private JButton deleteButton;

  private String editingProductId = null;

  public ProductFrame(ProductController controller) {
    this.controller = controller;
    initComponents();
    layoutComponents();
    configureFrame();
    refreshTable();
  }

  private void initComponents() {
    skuField = new JTextField(20);
    nameField = new JTextField(20);
    descriptionField = new JTextField(20);
    priceField = new JTextField("0.00", 20);
    activeCheckBox = new JCheckBox("Active", true);

    supplierCombo = new JComboBox<>();
    supplierCombo.setRenderer(new DefaultListCellRenderer() {
      @Override
      public Component getListCellRendererComponent(
        JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        if (value instanceof Supplier) {
          setText(((Supplier) value).getName());
        } else {
          setText("-- No Supplier --");
        }
        return this;
      }
    });
    loadSupplierCombo();

    searchField = new JTextField(25);

    tableModel = new DefaultTableModel(
      new String[]{"SKU", "Name", "Description", "Price", "Supplier", "Active"}, 0) {
      @Override
      public boolean isCellEditable(int row, int col) {
        return false;
      }
    };
    table = new JTable(tableModel);
    table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    table.setRowHeight(24);
    table.getColumnModel().getColumn(0).setPreferredWidth(80);
    table.getColumnModel().getColumn(1).setPreferredWidth(140);
    table.getColumnModel().getColumn(2).setPreferredWidth(180);
    table.getColumnModel().getColumn(3).setPreferredWidth(70);
    table.getColumnModel().getColumn(4).setPreferredWidth(120);
    table.getColumnModel().getColumn(5).setPreferredWidth(55);

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

    saveButton = new JButton("Save Product");
    updateButton = new JButton("Update");
    deleteButton = new JButton("Delete");
    updateButton.setEnabled(false);
    deleteButton.setEnabled(false);

    saveButton.addActionListener(e -> handleSave());
    updateButton.addActionListener(e -> handleUpdate());
    deleteButton.addActionListener(e -> handleDelete());
  }

  private void loadSupplierCombo() {
    supplierCombo.removeAllItems();
    supplierCombo.addItem(null);
    for (Supplier s : controller.getAllSuppliers()) {
      supplierCombo.addItem(s);
    }
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
    panel.setBorder(new TitledBorder("Product Information"));

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
    panel.add(new JLabel("SKU:"), lbl);
    fld.gridy = 0;
    panel.add(skuField, fld);
    lbl.gridy = 1;
    panel.add(new JLabel("Name:"), lbl);
    fld.gridy = 1;
    panel.add(nameField, fld);
    lbl.gridy = 2;
    panel.add(new JLabel("Description:"), lbl);
    fld.gridy = 2;
    panel.add(descriptionField, fld);
    lbl.gridy = 3;
    panel.add(new JLabel("Price ($):"), lbl);
    fld.gridy = 3;
    panel.add(priceField, fld);
    lbl.gridy = 4;
    panel.add(new JLabel("Supplier:"), lbl);
    fld.gridy = 4;
    panel.add(supplierCombo, fld);
    lbl.gridy = 5;
    panel.add(new JLabel("Status:"), lbl);
    fld.gridy = 5;
    panel.add(activeCheckBox, fld);

    return panel;
  }

  private JPanel buildTablePanel() {
    JPanel panel = new JPanel(new BorderLayout(4, 4));
    panel.setBorder(new TitledBorder("Products"));

    JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
    searchPanel.add(new JLabel("Search:"));
    searchPanel.add(searchField);
    panel.add(searchPanel, BorderLayout.NORTH);

    JScrollPane scroll = new JScrollPane(table);
    scroll.setPreferredSize(new Dimension(650, 180));
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
      editingProductId = null;
      updateButton.setEnabled(false);
      deleteButton.setEnabled(false);
      return;
    }
    int modelRow = table.convertRowIndexToModel(viewRow);
    editingProductId = rowProductIds.get(modelRow);
    skuField.setText((String) tableModel.getValueAt(modelRow, 0));
    nameField.setText((String) tableModel.getValueAt(modelRow, 1));
    descriptionField.setText((String) tableModel.getValueAt(modelRow, 2));
    priceField.setText(tableModel.getValueAt(modelRow, 3).toString());
    activeCheckBox.setSelected((Boolean) tableModel.getValueAt(modelRow, 5));

    String supplierName = (String) tableModel.getValueAt(modelRow, 4);
    supplierCombo.setSelectedIndex(0);
    for (int i = 1; i < supplierCombo.getItemCount(); i++) {
      Supplier s = (Supplier) supplierCombo.getItemAt(i);
      if (s.getName().equals(supplierName)) {
        supplierCombo.setSelectedIndex(i);
        break;
      }
    }
    updateButton.setEnabled(true);
    deleteButton.setEnabled(true);
  }

  private String getSelectedSupplierId() {
    Object selected = supplierCombo.getSelectedItem();
    return (selected instanceof Supplier) ? ((Supplier) selected).getId() : null;
  }

  private void handleSave() {
    String sku = skuField.getText().trim();
    String name = nameField.getText().trim();
    if (sku.isEmpty() || name.isEmpty()) {
      showError("SKU and Name are required.");
      return;
    }
    BigDecimal price;
    try {
      price = new BigDecimal(priceField.getText().trim());
    } catch (NumberFormatException e) {
      showError("Invalid price format.");
      return;
    }
    try {
      Product created = controller.createProduct(
        sku, name, descriptionField.getText().trim(),
        price, getSelectedSupplierId(), activeCheckBox.isSelected());
      JOptionPane.showMessageDialog(this,
        "Product '" + created.getName() + "' saved successfully.",
        "Product Saved", JOptionPane.INFORMATION_MESSAGE);
      handleClear();
      refreshTable();
    } catch (Exception e) {
      showError("Failed to save product: " + e.getMessage());
    }
  }

  private void handleUpdate() {
    if (editingProductId == null) {
      return;
    }
    String sku = skuField.getText().trim();
    String name = nameField.getText().trim();
    if (sku.isEmpty() || name.isEmpty()) {
      showError("SKU and Name are required.");
      return;
    }
    BigDecimal price;
    try {
      price = new BigDecimal(priceField.getText().trim());
    } catch (NumberFormatException e) {
      showError("Invalid price format.");
      return;
    }
    try {
      controller.updateProduct(
        editingProductId, sku, name, descriptionField.getText().trim(),
        price, getSelectedSupplierId(), activeCheckBox.isSelected());
      JOptionPane.showMessageDialog(this,
        "Product updated successfully.",
        "Product Updated", JOptionPane.INFORMATION_MESSAGE);
      handleClear();
      refreshTable();
    } catch (Exception e) {
      showError("Failed to update product: " + e.getMessage());
    }
  }

  private void handleDelete() {
    if (editingProductId == null) {
      return;
    }
    int confirm = JOptionPane.showConfirmDialog(this,
      "Delete product '" + nameField.getText().trim() + "'?",
      "Confirm Delete", JOptionPane.YES_NO_OPTION);
    if (confirm != JOptionPane.YES_OPTION) {
      return;
    }
    try {
      controller.deleteProduct(editingProductId);
      handleClear();
      refreshTable();
    } catch (Exception e) {
      showError("Failed to delete product: " + e.getMessage());
    }
  }

  private void handleClear() {
    editingProductId = null;
    skuField.setText("");
    nameField.setText("");
    descriptionField.setText("");
    priceField.setText("0.00");
    supplierCombo.setSelectedIndex(0);
    activeCheckBox.setSelected(true);
    table.clearSelection();
    updateButton.setEnabled(false);
    deleteButton.setEnabled(false);
    skuField.requestFocus();
  }

  private void refreshTable() {
    tableModel.setRowCount(0);
    rowProductIds.clear();
    for (Product p : controller.getAllProducts()) {
      String supplierName = "";
      if (p.getSupplierId() != null) {
        for (int i = 1; i < supplierCombo.getItemCount(); i++) {
          Supplier s = (Supplier) supplierCombo.getItemAt(i);
          if (s.getId().equals(p.getSupplierId())) {
            supplierName = s.getName();
            break;
          }
        }
      }
      tableModel.addRow(new Object[]{
        p.getSku(), p.getName(), p.getDescription(),
        p.getPrice(), supplierName, p.getActive()
      });
      rowProductIds.add(p.getId());
    }
  }

  private void showError(String message) {
    JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
  }

  private void configureFrame() {
    setTitle("Products");
    setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
    pack();
    setMinimumSize(new Dimension(720, 600));
    setLocationRelativeTo(null);
  }

  @Override
  public void onSuppliersChanged() {
    SwingUtilities.invokeLater(this::loadSupplierCombo);
  }
}

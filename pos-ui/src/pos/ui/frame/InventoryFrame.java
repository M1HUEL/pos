package pos.ui.frame;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import pos.inventory.model.StockItem;
import pos.product.model.Product;
import pos.ui.controller.InventoryController;
import pos.ui.listener.ProductChangeListener;

public class InventoryFrame extends JFrame implements ProductChangeListener {

  private final InventoryController controller;

  private JComboBox<Product> productCombo;
  private JSpinner stockSpinner;
  private JSpinner minStockSpinner;

  private JTextField searchField;
  private DefaultTableModel tableModel;
  private JTable table;
  private TableRowSorter<DefaultTableModel> sorter;
  private final List<String> rowProductIds = new ArrayList<>();

  private JButton initializeButton;
  private JButton updateButton;
  private JButton deleteButton;

  private String editingProductId = null;

  public InventoryFrame(InventoryController controller) {
    this.controller = controller;
    initComponents();
    layoutComponents();
    configureFrame();
    refreshTable();
  }

  private void initComponents() {
    productCombo = new JComboBox<>();
    productCombo.setRenderer(new DefaultListCellRenderer() {
      @Override
      public Component getListCellRendererComponent(
        JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        if (value instanceof Product) {
          Product p = (Product) value;
          setText(p.getName() + " — " + p.getSku());
        }
        return this;
      }
    });
    loadProductCombo();

    stockSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 99999, 1));
    minStockSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 99999, 1));

    searchField = new JTextField(25);

    tableModel = new DefaultTableModel(
      new String[]{"Product", "SKU", "Stock", "Min Stock"}, 0) {
      @Override
      public boolean isCellEditable(int row, int col) {
        return false;
      }
    };
    table = new JTable(tableModel);
    table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    table.setRowHeight(24);
    table.getColumnModel().getColumn(0).setPreferredWidth(200);
    table.getColumnModel().getColumn(1).setPreferredWidth(100);
    table.getColumnModel().getColumn(2).setPreferredWidth(80);
    table.getColumnModel().getColumn(3).setPreferredWidth(80);

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

    initializeButton = new JButton("Initialize Stock");
    updateButton = new JButton("Update");
    deleteButton = new JButton("Delete");
    updateButton.setEnabled(false);
    deleteButton.setEnabled(false);

    initializeButton.addActionListener(e -> handleInitialize());
    updateButton.addActionListener(e -> handleUpdate());
    deleteButton.addActionListener(e -> handleDelete());
  }

  private void loadProductCombo() {
    productCombo.removeAllItems();
    List<Product> products = controller.getAllProducts();
    if (products.isEmpty()) {
      productCombo.setEnabled(false);
      return;
    }
    for (Product p : products) {
      productCombo.addItem(p);
    }
    productCombo.setEnabled(true);
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
    panel.setBorder(new TitledBorder("Stock Information"));

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
    panel.add(new JLabel("Product:"), lbl);
    fld.gridy = 0;
    panel.add(productCombo, fld);
    lbl.gridy = 1;
    panel.add(new JLabel("Stock:"), lbl);
    fld.gridy = 1;
    panel.add(stockSpinner, fld);
    lbl.gridy = 2;
    panel.add(new JLabel("Min Stock:"), lbl);
    fld.gridy = 2;
    panel.add(minStockSpinner, fld);

    return panel;
  }

  private JPanel buildTablePanel() {
    JPanel panel = new JPanel(new BorderLayout(4, 4));
    panel.setBorder(new TitledBorder("Stock Records"));

    JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
    searchPanel.add(new JLabel("Search:"));
    searchPanel.add(searchField);
    panel.add(searchPanel, BorderLayout.NORTH);

    JScrollPane scroll = new JScrollPane(table);
    scroll.setPreferredSize(new Dimension(500, 180));
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
    panel.add(initializeButton);
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
      productCombo.setEnabled(true);
      initializeButton.setEnabled(true);
      updateButton.setEnabled(false);
      deleteButton.setEnabled(false);
      return;
    }
    int modelRow = table.convertRowIndexToModel(viewRow);
    editingProductId = rowProductIds.get(modelRow);
    stockSpinner.setValue((Integer) tableModel.getValueAt(modelRow, 2));
    minStockSpinner.setValue((Integer) tableModel.getValueAt(modelRow, 3));

    controller.findProductById(editingProductId).ifPresent(p -> {
      for (int i = 0; i < productCombo.getItemCount(); i++) {
        if (productCombo.getItemAt(i).getId().equals(p.getId())) {
          productCombo.setSelectedIndex(i);
          break;
        }
      }
    });

    productCombo.setEnabled(false);
    initializeButton.setEnabled(false);
    updateButton.setEnabled(true);
    deleteButton.setEnabled(true);
  }

  private void handleInitialize() {
    Product selectedProduct = (Product) productCombo.getSelectedItem();
    if (selectedProduct == null) {
      showError("No product selected.");
      return;
    }
    int stock = (Integer) stockSpinner.getValue();
    int minStock = (Integer) minStockSpinner.getValue();
    try {
      StockItem created = controller.initializeStock(selectedProduct.getId(), stock, minStock);
      JOptionPane.showMessageDialog(this,
        "Stock initialized for '" + selectedProduct.getName() + "'.\n"
        + "Stock: " + created.getStock() + " | Min Stock: " + created.getMinStock(),
        "Stock Initialized", JOptionPane.INFORMATION_MESSAGE);
      handleClear();
      refreshTable();
    } catch (Exception e) {
      showError("Failed to initialize stock: " + e.getMessage());
    }
  }

  private void handleUpdate() {
    if (editingProductId == null) {
      return;
    }
    int stock = (Integer) stockSpinner.getValue();
    int minStock = (Integer) minStockSpinner.getValue();
    try {
      controller.updateStock(editingProductId, stock, minStock);
      JOptionPane.showMessageDialog(this,
        "Stock updated successfully.",
        "Stock Updated", JOptionPane.INFORMATION_MESSAGE);
      handleClear();
      refreshTable();
    } catch (Exception e) {
      showError("Failed to update stock: " + e.getMessage());
    }
  }

  private void handleDelete() {
    if (editingProductId == null) {
      return;
    }
    int viewRow = table.getSelectedRow();
    String productName = (String) tableModel.getValueAt(
      table.convertRowIndexToModel(viewRow), 0);
    int confirm = JOptionPane.showConfirmDialog(this,
      "Delete stock record for '" + productName + "'?",
      "Confirm Delete", JOptionPane.YES_NO_OPTION);
    if (confirm != JOptionPane.YES_OPTION) {
      return;
    }
    try {
      controller.deleteStock(editingProductId);
      handleClear();
      refreshTable();
    } catch (Exception e) {
      showError("Failed to delete stock: " + e.getMessage());
    }
  }

  private void handleClear() {
    editingProductId = null;
    stockSpinner.setValue(0);
    minStockSpinner.setValue(0);
    table.clearSelection();
    productCombo.setEnabled(true);
    initializeButton.setEnabled(true);
    updateButton.setEnabled(false);
    deleteButton.setEnabled(false);
    if (productCombo.getItemCount() > 0) {
      productCombo.setSelectedIndex(0);
    }
  }

  private void refreshTable() {
    tableModel.setRowCount(0);
    rowProductIds.clear();
    for (StockItem item : controller.getAllStockItems()) {
      String productName = controller.findProductById(item.getProductId())
        .map(Product::getName).orElse("Unknown");
      String sku = controller.findProductById(item.getProductId())
        .map(Product::getSku).orElse("");
      tableModel.addRow(new Object[]{productName, sku, item.getStock(), item.getMinStock()});
      rowProductIds.add(item.getProductId());
    }
  }

  private void showError(String message) {
    JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
  }

  private void configureFrame() {
    setTitle("Inventory");
    setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
    pack();
    setMinimumSize(new Dimension(580, 540));
    setLocationRelativeTo(null);
  }

  @Override
  public void onProductsChanged() {
    SwingUtilities.invokeLater(this::loadProductCombo);
  }
}

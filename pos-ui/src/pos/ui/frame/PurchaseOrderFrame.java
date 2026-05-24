package pos.ui.frame;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
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
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import pos.product.model.Product;
import pos.purchase.model.PurchaseOrder;
import pos.purchase.model.PurchaseOrderItem;
import pos.purchase.model.PurchaseOrderStatus;
import pos.supplier.model.Supplier;
import pos.ui.controller.PurchaseOrderController;

public class PurchaseOrderFrame extends JFrame {

  private static final DateTimeFormatter DATE_FORMAT
    = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

  private final PurchaseOrderController controller;

  private JComboBox<Supplier> supplierCombo;
  private JComboBox<Product> productCombo;
  private JSpinner quantitySpinner;
  private JTextField unitCostField;

  private DefaultTableModel currentItemsModel;
  private JTable currentItemsTable;
  private JLabel totalCostLabel;

  private JTextField searchField;
  private DefaultTableModel ordersTableModel;
  private JTable ordersTable;
  private TableRowSorter<DefaultTableModel> sorter;
  private final List<String> rowOrderIds = new ArrayList<>();

  private JButton receiveButton;
  private JButton cancelOrderButton;

  public PurchaseOrderFrame(PurchaseOrderController controller) {
    this.controller = controller;
    initComponents();
    layoutComponents();
    configureFrame();
    refreshOrdersTable();
  }

  private void initComponents() {
    supplierCombo = new JComboBox<>();
    supplierCombo.setRenderer(new DefaultListCellRenderer() {
      @Override
      public Component getListCellRendererComponent(
        JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        if (value instanceof Supplier supplier) {
          setText(supplier.getName());
        }
        return this;
      }
    });

    productCombo = new JComboBox<>();
    productCombo.setRenderer(new DefaultListCellRenderer() {
      @Override
      public Component getListCellRendererComponent(
        JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        if (value instanceof Product p) {
          setText(p.getName() + " — " + p.getSku());
        }
        return this;
      }
    });

    loadCombos();

    quantitySpinner = new JSpinner(new SpinnerNumberModel(1, 1, 9999, 1));
    unitCostField = new JTextField("0.00", 8);

    currentItemsModel = new DefaultTableModel(
      new String[]{"Product", "Qty", "Unit Cost", "Total"}, 0) {
      @Override
      public boolean isCellEditable(int row, int col) {
        return false;
      }
    };
    currentItemsTable = new JTable(currentItemsModel);
    currentItemsTable.setRowHeight(24);
    currentItemsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    currentItemsTable.getColumnModel().getColumn(0).setPreferredWidth(220);
    currentItemsTable.getColumnModel().getColumn(1).setPreferredWidth(50);
    currentItemsTable.getColumnModel().getColumn(2).setPreferredWidth(90);
    currentItemsTable.getColumnModel().getColumn(3).setPreferredWidth(90);

    DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
    rightRenderer.setHorizontalAlignment(JLabel.RIGHT);
    for (int i = 1; i <= 3; i++) {
      currentItemsTable.getColumnModel().getColumn(i).setCellRenderer(rightRenderer);
    }

    totalCostLabel = new JLabel("$0.00");

    searchField = new JTextField(25);

    ordersTableModel = new DefaultTableModel(
      new String[]{"Order #", "Supplier", "Date", "Items", "Total", "Status"}, 0) {
      @Override
      public boolean isCellEditable(int row, int col) {
        return false;
      }
    };
    ordersTable = new JTable(ordersTableModel);
    ordersTable.setRowHeight(24);
    ordersTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    ordersTable.getColumnModel().getColumn(0).setPreferredWidth(280);
    ordersTable.getColumnModel().getColumn(1).setPreferredWidth(120);
    ordersTable.getColumnModel().getColumn(2).setPreferredWidth(120);
    ordersTable.getColumnModel().getColumn(3).setPreferredWidth(50);
    ordersTable.getColumnModel().getColumn(4).setPreferredWidth(80);
    ordersTable.getColumnModel().getColumn(5).setPreferredWidth(80);

    sorter = new TableRowSorter<>(ordersTableModel);
    ordersTable.setRowSorter(sorter);

    ordersTable.getSelectionModel().addListSelectionListener(e -> {
      if (!e.getValueIsAdjusting()) {
        handleOrderSelection();
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

    receiveButton = new JButton("Receive Order");
    cancelOrderButton = new JButton("Cancel Order");
    receiveButton.setEnabled(false);
    cancelOrderButton.setEnabled(false);

    receiveButton.addActionListener(e -> handleReceiveOrder());
    cancelOrderButton.addActionListener(e -> handleCancelOrder());
  }

  private void loadCombos() {
    supplierCombo.removeAllItems();
    for (Supplier s : controller.getAllSuppliers()) {
      supplierCombo.addItem(s);
    }

    productCombo.removeAllItems();
    for (Product p : controller.getAllProducts()) {
      productCombo.addItem(p);
    }
  }

  private void layoutComponents() {
    setLayout(new BorderLayout(8, 8));
    ((JPanel) getContentPane()).setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

    JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
      buildNewOrderPanel(), buildOrdersPanel());
    splitPane.setResizeWeight(0.5);

    add(splitPane, BorderLayout.CENTER);
    add(buildActionButtonPanel(), BorderLayout.SOUTH);
  }

  private JPanel buildNewOrderPanel() {
    JPanel panel = new JPanel(new BorderLayout(6, 6));
    panel.setBorder(new TitledBorder("New Purchase Order"));
    panel.add(buildOrderFormPanel(), BorderLayout.NORTH);
    panel.add(buildCurrentItemsPanel(), BorderLayout.CENTER);
    panel.add(buildCreateOrderPanel(), BorderLayout.SOUTH);
    return panel;
  }

  private JPanel buildOrderFormPanel() {
    JPanel panel = new JPanel(new GridBagLayout());

    GridBagConstraints lbl = new GridBagConstraints();
    lbl.anchor = GridBagConstraints.EAST;
    lbl.insets = new Insets(4, 8, 4, 4);

    GridBagConstraints fld = new GridBagConstraints();
    fld.anchor = GridBagConstraints.WEST;
    fld.insets = new Insets(4, 0, 4, 8);

    lbl.gridy = 0;
    panel.add(new JLabel("Supplier:"), lbl);
    fld.gridy = 0;
    panel.add(supplierCombo, fld);

    fld.gridwidth = GridBagConstraints.REMAINDER;
    lbl.gridy = 1;
    panel.add(new JLabel("Product:"), lbl);
    fld.gridy = 1;
    panel.add(productCombo, fld);
    fld.gridwidth = 1;

    lbl.gridy = 2;
    panel.add(new JLabel("Qty:"), lbl);
    fld.gridy = 2;
    panel.add(quantitySpinner, fld);

    lbl.gridy = 3;
    panel.add(new JLabel("Unit Cost ($):"), lbl);
    fld.gridy = 3;
    JButton addItemButton = new JButton("+ Add Item");
    addItemButton.addActionListener(e -> handleAddItem());
    JPanel unitCostPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
    unitCostPanel.add(unitCostField);
    unitCostPanel.add(addItemButton);
    panel.add(unitCostPanel, fld);

    return panel;
  }

  private JPanel buildCurrentItemsPanel() {
    JPanel panel = new JPanel(new BorderLayout(4, 4));
    panel.setBorder(new TitledBorder("Items"));

    JScrollPane scroll = new JScrollPane(currentItemsTable);
    scroll.setPreferredSize(new Dimension(680, 120));
    panel.add(scroll, BorderLayout.CENTER);

    JButton removeButton = new JButton("Remove Selected");
    removeButton.addActionListener(e -> handleRemoveItem());
    JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 2));
    btnRow.add(removeButton);
    panel.add(btnRow, BorderLayout.SOUTH);

    return panel;
  }

  private JPanel buildCreateOrderPanel() {
    JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 4));
    panel.add(new JLabel("Total Cost:"));
    panel.add(totalCostLabel);
    JButton clearButton = new JButton("Clear");
    clearButton.addActionListener(e -> handleClear());
    JButton createButton = new JButton("Create Order");
    createButton.addActionListener(e -> handleCreateOrder());
    panel.add(clearButton);
    panel.add(createButton);
    return panel;
  }

  private JPanel buildOrdersPanel() {
    JPanel panel = new JPanel(new BorderLayout(4, 4));
    panel.setBorder(new TitledBorder("Purchase Orders"));

    JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
    searchPanel.add(new JLabel("Search:"));
    searchPanel.add(searchField);
    panel.add(searchPanel, BorderLayout.NORTH);

    JScrollPane scroll = new JScrollPane(ordersTable);
    scroll.setPreferredSize(new Dimension(680, 150));
    panel.add(scroll, BorderLayout.CENTER);

    return panel;
  }

  private JPanel buildActionButtonPanel() {
    JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 8));
    panel.add(cancelOrderButton);
    panel.add(receiveButton);
    return panel;
  }

  private void applyFilter() {
    String text = searchField.getText().trim();
    sorter.setRowFilter(text.isEmpty()
      ? null
      : RowFilter.regexFilter("(?i)" + Pattern.quote(text)));
  }

  private void handleAddItem() {
    Product selectedProduct = (Product) productCombo.getSelectedItem();
    if (selectedProduct == null) {
      showError("No product selected.");
      return;
    }
    int quantity = (Integer) quantitySpinner.getValue();
    BigDecimal unitCost;
    try {
      unitCost = new BigDecimal(unitCostField.getText().trim());
    } catch (NumberFormatException e) {
      showError("Invalid unit cost.");
      return;
    }
    try {
      PurchaseOrderItem item = controller.buildItem(selectedProduct, quantity, unitCost);
      controller.addItem(item);
      refreshCurrentItemsTable();
      quantitySpinner.setValue(1);
      unitCostField.setText("0.00");
    } catch (Exception e) {
      showError(e.getMessage());
    }
  }

  private void handleRemoveItem() {
    int selected = currentItemsTable.getSelectedRow();
    if (selected < 0) {
      showError("Please select an item to remove.");
      return;
    }
    controller.removeItem(selected);
    refreshCurrentItemsTable();
  }

  private void handleCreateOrder() {
    Supplier selectedSupplier = (Supplier) supplierCombo.getSelectedItem();
    if (selectedSupplier == null) {
      showError("No supplier selected.");
      return;
    }
    if (controller.getCurrentItems().isEmpty()) {
      showError("Cannot create an order with no items.");
      return;
    }
    int confirm = JOptionPane.showConfirmDialog(this,
      "Create purchase order for '" + selectedSupplier.getName()
      + "' with total " + totalCostLabel.getText() + "?",
      "Confirm Order", JOptionPane.YES_NO_OPTION);
    if (confirm != JOptionPane.YES_OPTION) {
      return;
    }
    try {
      PurchaseOrder created = controller.createOrder(selectedSupplier);
      JOptionPane.showMessageDialog(this,
        "Purchase order " + created.getOrderNumber() + " created successfully.",
        "Order Created", JOptionPane.INFORMATION_MESSAGE);
      handleClear();
      refreshOrdersTable();
    } catch (Exception e) {
      showError("Failed to create order: " + e.getMessage());
    }
  }

  private void handleOrderSelection() {
    int viewRow = ordersTable.getSelectedRow();
    if (viewRow < 0) {
      receiveButton.setEnabled(false);
      cancelOrderButton.setEnabled(false);
      return;
    }
    int modelRow = ordersTable.convertRowIndexToModel(viewRow);
    String status = (String) ordersTableModel.getValueAt(modelRow, 5);
    boolean isPending = PurchaseOrderStatus.PENDING.name().equals(status);
    receiveButton.setEnabled(isPending);
    cancelOrderButton.setEnabled(isPending);
  }

  private void handleReceiveOrder() {
    int viewRow = ordersTable.getSelectedRow();
    if (viewRow < 0) {
      return;
    }
    int modelRow = ordersTable.convertRowIndexToModel(viewRow);
    String orderId = rowOrderIds.get(modelRow);
    int confirm = JOptionPane.showConfirmDialog(this,
      "Mark this order as received? Stock will be updated automatically.",
      "Confirm Receive", JOptionPane.YES_NO_OPTION);
    if (confirm != JOptionPane.YES_OPTION) {
      return;
    }
    try {
      controller.receiveOrder(orderId);
      JOptionPane.showMessageDialog(this,
        "Order received. Stock has been updated.",
        "Order Received", JOptionPane.INFORMATION_MESSAGE);
      refreshOrdersTable();
    } catch (Exception e) {
      showError("Failed to receive order: " + e.getMessage());
    }
  }

  private void handleCancelOrder() {
    int viewRow = ordersTable.getSelectedRow();
    if (viewRow < 0) {
      return;
    }
    int modelRow = ordersTable.convertRowIndexToModel(viewRow);
    String orderId = rowOrderIds.get(modelRow);
    int confirm = JOptionPane.showConfirmDialog(this,
      "Cancel this purchase order?",
      "Confirm Cancel", JOptionPane.YES_NO_OPTION);
    if (confirm != JOptionPane.YES_OPTION) {
      return;
    }
    try {
      controller.cancelOrder(orderId);
      refreshOrdersTable();
      receiveButton.setEnabled(false);
      cancelOrderButton.setEnabled(false);
    } catch (Exception e) {
      showError("Failed to cancel order: " + e.getMessage());
    }
  }

  private void handleClear() {
    controller.clearItems();
    currentItemsModel.setRowCount(0);
    totalCostLabel.setText("$0.00");
    quantitySpinner.setValue(1);
    unitCostField.setText("0.00");
  }

  private void refreshCurrentItemsTable() {
    currentItemsModel.setRowCount(0);
    for (PurchaseOrderItem item : controller.getCurrentItems()) {
      currentItemsModel.addRow(new Object[]{
        item.getProductName(),
        item.getQuantity(),
        "$" + item.getUnitCost(),
        "$" + item.getTotalCost()
      });
    }
    totalCostLabel.setText("$" + controller.calculateTotalCost());
  }

  private void refreshOrdersTable() {
    ordersTableModel.setRowCount(0);
    rowOrderIds.clear();
    for (PurchaseOrder order : controller.getAllOrders()) {
      ordersTableModel.addRow(new Object[]{
        order.getOrderNumber(),
        order.getSupplierName(),
        order.getDate() != null ? order.getDate().format(DATE_FORMAT) : "",
        order.getItems() != null ? order.getItems().size() : 0,
        "$" + order.getTotalCost(),
        order.getStatus() != null ? order.getStatus().name() : ""
      });
      rowOrderIds.add(order.getId());
    }
    receiveButton.setEnabled(false);
    cancelOrderButton.setEnabled(false);
  }

  private void showError(String message) {
    JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
  }

  private void configureFrame() {
    setTitle("Purchase Orders");
    setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
    pack();
    setMinimumSize(new Dimension(780, 680));
    setLocationRelativeTo(null);
  }
}

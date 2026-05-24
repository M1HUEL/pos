package pos.ui.frame;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.math.BigDecimal;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import pos.product.model.Product;
import pos.sale.model.PaymentMethod;
import pos.sale.model.Sale;
import pos.sale.model.SaleItem;
import pos.ui.controller.NewSaleController;
import pos.ui.listener.ProductChangeListener;

public class NewSaleFrame extends JFrame implements ProductChangeListener {

  private final NewSaleController controller;

  private JTextField saleNumberField;
  private JComboBox<PaymentMethod> paymentMethodCombo;

  private JComboBox<Product> productCombo;
  private JSpinner quantitySpinner;
  private JTextField itemDiscountField;

  private DefaultTableModel tableModel;
  private JTable itemsTable;

  private JLabel itemsTotalLabel;
  private JTextField saleDiscountField;
  private JLabel taxRateLabel;
  private JLabel taxAmountLabel;
  private JLabel totalLabel;

  public NewSaleFrame(NewSaleController controller) {
    this.controller = controller;
    initComponents();
    layoutComponents();
    configureFrame();
  }

  private void initComponents() {
    saleNumberField = new JTextField(controller.generateSaleNumber(), 30);
    saleNumberField.setEditable(false);

    paymentMethodCombo = new JComboBox<>(PaymentMethod.values());

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
    loadProducts();

    quantitySpinner = new JSpinner(new SpinnerNumberModel(1, 1, 9999, 1));
    itemDiscountField = new JTextField("0.00", 8);

    tableModel = new DefaultTableModel(
      new String[]{"Product", "Qty", "Unit Price", "Discount", "Subtotal"}, 0) {
      @Override
      public boolean isCellEditable(int row, int col) {
        return false;
      }
    };
    itemsTable = new JTable(tableModel);
    itemsTable.setRowHeight(26);
    itemsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    itemsTable.getColumnModel().getColumn(0).setPreferredWidth(250);
    itemsTable.getColumnModel().getColumn(1).setPreferredWidth(50);
    itemsTable.getColumnModel().getColumn(2).setPreferredWidth(100);
    itemsTable.getColumnModel().getColumn(3).setPreferredWidth(100);
    itemsTable.getColumnModel().getColumn(4).setPreferredWidth(100);

    DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
    rightRenderer.setHorizontalAlignment(JLabel.RIGHT);
    for (int i = 1; i <= 4; i++) {
      itemsTable.getColumnModel().getColumn(i).setCellRenderer(rightRenderer);
    }

    itemsTotalLabel = new JLabel("$0.00");
    taxRateLabel = new JLabel("Tax (0%):");
    taxAmountLabel = new JLabel("$0.00");
    totalLabel = new JLabel("$0.00");

    saleDiscountField = new JTextField(controller.getDefaultDiscount().toPlainString(), 8);
    saleDiscountField.addFocusListener(new FocusAdapter() {
      @Override
      public void focusLost(FocusEvent e) {
        updateTotals();
      }
    });
  }

  private void loadProducts() {
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
    add(buildSaleInfoPanel(), BorderLayout.NORTH);
    JPanel centerPanel = new JPanel(new BorderLayout(6, 6));
    centerPanel.add(buildProductInputPanel(), BorderLayout.NORTH);
    centerPanel.add(buildItemsTablePanel(), BorderLayout.CENTER);
    add(centerPanel, BorderLayout.CENTER);
    JPanel southPanel = new JPanel(new BorderLayout(6, 6));
    southPanel.add(buildTotalsPanel(), BorderLayout.CENTER);
    southPanel.add(buildButtonPanel(), BorderLayout.SOUTH);
    add(southPanel, BorderLayout.SOUTH);
  }

  private JPanel buildSaleInfoPanel() {
    JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 6));
    panel.setBorder(new TitledBorder("Sale Information"));
    panel.add(new JLabel("Sale #:"));
    panel.add(saleNumberField);
    panel.add(Box.createHorizontalStrut(16));
    panel.add(new JLabel("Payment Method:"));
    panel.add(paymentMethodCombo);
    return panel;
  }

  private JPanel buildProductInputPanel() {
    JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 6));
    panel.setBorder(new TitledBorder("Add Product"));
    panel.add(new JLabel("Product:"));
    panel.add(productCombo);
    panel.add(new JLabel("Qty:"));
    panel.add(quantitySpinner);
    panel.add(new JLabel("Item Discount ($):"));
    panel.add(itemDiscountField);
    JButton addButton = new JButton("+ Add");
    addButton.addActionListener(e -> handleAddItem());
    panel.add(addButton);
    return panel;
  }

  private JPanel buildItemsTablePanel() {
    JPanel panel = new JPanel(new BorderLayout(4, 4));
    panel.setBorder(new TitledBorder("Items"));
    JScrollPane scroll = new JScrollPane(itemsTable);
    scroll.setPreferredSize(new Dimension(720, 200));
    panel.add(scroll, BorderLayout.CENTER);
    JButton removeButton = new JButton("Remove Selected");
    removeButton.addActionListener(e -> handleRemoveItem());
    JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 4));
    btnRow.add(removeButton);
    panel.add(btnRow, BorderLayout.SOUTH);
    return panel;
  }

  private JPanel buildTotalsPanel() {
    JPanel panel = new JPanel(new GridBagLayout());
    panel.setBorder(new TitledBorder("Totals"));

    GridBagConstraints lbl = new GridBagConstraints();
    lbl.anchor = GridBagConstraints.EAST;
    lbl.insets = new Insets(4, 10, 4, 6);

    GridBagConstraints val = new GridBagConstraints();
    val.anchor = GridBagConstraints.WEST;
    val.insets = new Insets(4, 0, 4, 10);
    val.gridwidth = GridBagConstraints.REMAINDER;

    lbl.gridy = 0;
    panel.add(new JLabel("Items Total:"), lbl);
    val.gridy = 0;
    panel.add(itemsTotalLabel, val);

    lbl.gridy = 1;
    panel.add(new JLabel("Sale Discount ($):"), lbl);
    val.gridy = 1;
    panel.add(saleDiscountField, val);

    lbl.gridy = 2;
    panel.add(taxRateLabel, lbl);
    val.gridy = 2;
    panel.add(taxAmountLabel, val);

    GridBagConstraints sep = new GridBagConstraints();
    sep.gridy = 3;
    sep.gridwidth = GridBagConstraints.REMAINDER;
    sep.fill = GridBagConstraints.HORIZONTAL;
    sep.insets = new Insets(6, 10, 6, 10);
    panel.add(new JSeparator(), sep);

    lbl.gridy = 4;
    panel.add(new JLabel("TOTAL:"), lbl);
    val.gridy = 4;
    panel.add(totalLabel, val);

    return panel;
  }

  private JPanel buildButtonPanel() {
    JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 8));
    JButton clearButton = new JButton("Clear");
    clearButton.addActionListener(e -> handleClear());
    JButton completeButton = new JButton("Complete Sale");
    completeButton.addActionListener(e -> handleCompleteSale());
    panel.add(clearButton);
    panel.add(completeButton);
    return panel;
  }

  private void handleAddItem() {
    Product selectedProduct = (Product) productCombo.getSelectedItem();
    if (selectedProduct == null) {
      showError("No product selected.");
      return;
    }
    int quantity = (Integer) quantitySpinner.getValue();
    BigDecimal itemDiscount;
    try {
      itemDiscount = new BigDecimal(itemDiscountField.getText().trim());
    } catch (NumberFormatException e) {
      showError("Invalid item discount amount.");
      return;
    }
    try {
      SaleItem item = controller.buildItem(selectedProduct, quantity, itemDiscount);
      controller.addItem(item);
      refreshTable();
      quantitySpinner.setValue(1);
      itemDiscountField.setText("0.00");
    } catch (Exception e) {
      showError(e.getMessage());
    }
  }

  private void handleRemoveItem() {
    int selected = itemsTable.getSelectedRow();
    if (selected < 0) {
      showError("Please select an item to remove.");
      return;
    }
    controller.removeItem(selected);
    refreshTable();
  }

  private void handleCompleteSale() {
    if (controller.getCurrentItems().isEmpty()) {
      showError("Cannot complete a sale with no items.");
      return;
    }
    PaymentMethod paymentMethod = (PaymentMethod) paymentMethodCombo.getSelectedItem();
    BigDecimal saleDiscount;
    try {
      saleDiscount = new BigDecimal(saleDiscountField.getText().trim());
    } catch (NumberFormatException e) {
      showError("Invalid discount amount.");
      return;
    }
    int confirm = JOptionPane.showConfirmDialog(this,
      "Complete sale for " + totalLabel.getText()
      + " via " + paymentMethod.getDisplayName() + "?",
      "Confirm Sale", JOptionPane.YES_NO_OPTION);
    if (confirm != JOptionPane.YES_OPTION) {
      return;
    }
    try {
      Sale sale = controller.createSale(paymentMethod, saleDiscount);
      JOptionPane.showMessageDialog(this,
        "Sale " + sale.getSaleNumber() + " completed successfully!",
        "Sale Completed", JOptionPane.INFORMATION_MESSAGE);
      handleClear();
    } catch (Exception e) {
      showError("Failed to complete sale: " + e.getMessage());
    }
  }

  private void handleClear() {
    controller.clearItems();
    tableModel.setRowCount(0);
    saleDiscountField.setText(controller.getDefaultDiscount().toPlainString());
    saleNumberField.setText(controller.generateSaleNumber());
    updateTotals();
  }

  private void refreshTable() {
    tableModel.setRowCount(0);
    for (SaleItem item : controller.getCurrentItems()) {
      tableModel.addRow(new Object[]{
        item.getProductName(),
        item.getQuantity(),
        "$" + item.getUnitPrice(),
        "$" + item.getDiscountAmount(),
        "$" + item.getSubTotal()
      });
    }
    updateTotals();
  }

  private void updateTotals() {
    itemsTotalLabel.setText("$" + controller.calculateItemsTotal());
    taxRateLabel.setText("Tax (" + controller.getTaxRatePercent().toPlainString() + "%):");
    taxAmountLabel.setText("$" + controller.calculateTaxAmount());
    BigDecimal discount = BigDecimal.ZERO;
    try {
      discount = new BigDecimal(saleDiscountField.getText().trim());
    } catch (NumberFormatException ignored) {
    }
    totalLabel.setText("$" + controller.calculateTotal(discount));
  }

  private void showError(String message) {
    JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
  }

  private void configureFrame() {
    setTitle("New Sale");
    setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
    pack();
    setMinimumSize(new Dimension(780, 620));
    setLocationRelativeTo(null);
  }

  @Override
  public void onProductsChanged() {
    SwingUtilities.invokeLater(this::loadProducts);
  }
}

package pos.ui.frame;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.TitledBorder;
import pos.inventory.model.StockItem;
import pos.product.model.Product;
import pos.ui.controller.RegisterInventoryController;

public class RegisterInventoryFrame extends JFrame {

  private final RegisterInventoryController controller;

  private JComboBox<Product> productCombo;
  private JSpinner stockSpinner;
  private JSpinner minStockSpinner;

  public RegisterInventoryFrame(RegisterInventoryController controller) {
    this.controller = controller;
    initComponents();
    layoutComponents();
    configureFrame();
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
    loadProducts();

    stockSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 99999, 1));
    minStockSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 99999, 1));
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
    add(buildFormPanel(), BorderLayout.CENTER);
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
    panel.add(new JLabel("Initial Stock:"), lbl);
    fld.gridy = 1;
    panel.add(stockSpinner, fld);

    lbl.gridy = 2;
    panel.add(new JLabel("Min Stock:"), lbl);
    fld.gridy = 2;
    panel.add(minStockSpinner, fld);

    return panel;
  }

  private JPanel buildButtonPanel() {
    JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 8));
    JButton clearButton = new JButton("Clear");
    clearButton.addActionListener(e -> handleClear());
    JButton saveButton = new JButton("Initialize Stock");
    saveButton.addActionListener(e -> handleSave());
    panel.add(clearButton);
    panel.add(saveButton);
    return panel;
  }

  private void handleSave() {
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
    } catch (Exception e) {
      showError("Failed to initialize stock: " + e.getMessage());
    }
  }

  private void handleClear() {
    if (productCombo.getItemCount() > 0) {
      productCombo.setSelectedIndex(0);
    }
    stockSpinner.setValue(0);
    minStockSpinner.setValue(0);
  }

  private void showError(String message) {
    JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
  }

  private void configureFrame() {
    setTitle("Register Inventory");
    setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    pack();
    setLocationRelativeTo(null);
  }
}

package pos.ui.frame;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.math.BigDecimal;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import pos.product.model.Product;
import pos.ui.controller.RegisterProductController;

public class RegisterProductFrame extends JFrame {

  private final RegisterProductController controller;

  private JTextField skuField;
  private JTextField nameField;
  private JTextField descriptionField;
  private JTextField priceField;
  private JCheckBox activeCheckBox;

  public RegisterProductFrame(RegisterProductController controller) {
    this.controller = controller;
    initComponents();
    layoutComponents();
    configureFrame();
  }

  private void initComponents() {
    skuField = new JTextField(20);
    nameField = new JTextField(20);
    descriptionField = new JTextField(20);
    priceField = new JTextField("0.00", 20);
    activeCheckBox = new JCheckBox("Active", true);
  }

  private void layoutComponents() {
    setLayout(new BorderLayout(8, 8));
    ((JPanel) getContentPane()).setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    add(buildFormPanel(), BorderLayout.CENTER);
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
    panel.add(new JLabel("Status:"), lbl);
    fld.gridy = 4;
    panel.add(activeCheckBox, fld);

    return panel;
  }

  private JPanel buildButtonPanel() {
    JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 8));
    JButton clearButton = new JButton("Clear");
    clearButton.addActionListener(e -> handleClear());
    JButton saveButton = new JButton("Save Product");
    saveButton.addActionListener(e -> handleSave());
    panel.add(clearButton);
    panel.add(saveButton);
    return panel;
  }

  private void handleSave() {
    String sku = skuField.getText().trim();
    String name = nameField.getText().trim();
    String description = descriptionField.getText().trim();
    boolean active = activeCheckBox.isSelected();

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
      Product created = controller.createProduct(sku, name, description, price, active);
      JOptionPane.showMessageDialog(this,
        "Product '" + created.getName() + "' saved successfully.",
        "Product Saved", JOptionPane.INFORMATION_MESSAGE);
      handleClear();
    } catch (Exception e) {
      showError("Failed to save product: " + e.getMessage());
    }
  }

  private void handleClear() {
    skuField.setText("");
    nameField.setText("");
    descriptionField.setText("");
    priceField.setText("0.00");
    activeCheckBox.setSelected(true);
    skuField.requestFocus();
  }

  private void showError(String message) {
    JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
  }

  private void configureFrame() {
    setTitle("Register Product");
    setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    pack();
    setLocationRelativeTo(null);
  }
}

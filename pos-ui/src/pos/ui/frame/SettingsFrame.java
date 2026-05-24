package pos.ui.frame;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import pos.ui.controller.SaleConfigController;

public class SettingsFrame extends JFrame {

  private final SaleConfigController controller;

  private JTextField taxRateField;
  private JTextField defaultDiscountField;

  public SettingsFrame(SaleConfigController controller) {
    this.controller = controller;
    initComponents();
    layoutComponents();
    configureFrame();
    loadCurrentConfig();
  }

  private void initComponents() {
    taxRateField = new JTextField(10);
    defaultDiscountField = new JTextField(10);
  }

  private void layoutComponents() {
    setLayout(new BorderLayout(8, 8));
    ((JPanel) getContentPane()).setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    add(buildFormPanel(), BorderLayout.CENTER);
    add(buildButtonPanel(), BorderLayout.SOUTH);
  }

  private JPanel buildFormPanel() {
    JPanel panel = new JPanel(new GridBagLayout());
    panel.setBorder(new TitledBorder("Sale Configuration"));

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
    panel.add(new JLabel("Tax Rate (%):"), lbl);
    fld.gridy = 0;
    panel.add(taxRateField, fld);
    lbl.gridy = 1;
    panel.add(new JLabel("Default Discount ($):"), lbl);
    fld.gridy = 1;
    panel.add(defaultDiscountField, fld);

    return panel;
  }

  private JPanel buildButtonPanel() {
    JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 8));
    JButton saveButton = new JButton("Save");
    saveButton.addActionListener(e -> handleSave());
    panel.add(saveButton);
    return panel;
  }

  private void loadCurrentConfig() {
    taxRateField.setText(controller.getTaxRatePercent());
    defaultDiscountField.setText(controller.getDefaultDiscountAmount());
  }

  private void handleSave() {
    try {
      controller.updateConfig(
        taxRateField.getText().trim(),
        defaultDiscountField.getText().trim());
      JOptionPane.showMessageDialog(this,
        "Configuration saved successfully.",
        "Settings Saved", JOptionPane.INFORMATION_MESSAGE);
    } catch (Exception e) {
      JOptionPane.showMessageDialog(this,
        "Failed to save configuration: " + e.getMessage(),
        "Error", JOptionPane.ERROR_MESSAGE);
    }
  }

  private void configureFrame() {
    setTitle("Settings");
    setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
    pack();
    setMinimumSize(new Dimension(300, 200));
    setLocationRelativeTo(null);
  }
}

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
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
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
import pos.auth.model.Session;
import pos.ui.controller.UserController;
import pos.user.model.Role;
import pos.user.model.User;

public class UserManagementFrame extends JFrame {

  private final UserController controller;

  private JTextField usernameField;
  private JTextField fullNameField;
  private JComboBox<Role> roleCombo;
  private JCheckBox activeCheckBox;
  private JPasswordField passwordField;
  private JLabel passwordHintLabel;

  private JTextField searchField;
  private DefaultTableModel tableModel;
  private JTable table;
  private TableRowSorter<DefaultTableModel> sorter;
  private final List<String> rowUserIds = new ArrayList<>();

  private JButton saveButton;
  private JButton updateButton;
  private JButton deleteButton;

  private String editingUserId = null;

  public UserManagementFrame(UserController controller) {
    this.controller = controller;
    initComponents();
    layoutComponents();
    configureFrame();
    refreshTable();
  }

  private void initComponents() {
    usernameField = new JTextField(20);
    fullNameField = new JTextField(20);
    roleCombo = new JComboBox<>(Role.values());
    activeCheckBox = new JCheckBox("Active", true);
    passwordField = new JPasswordField(20);
    passwordHintLabel = new JLabel("Required for new users. Leave blank to keep current password.");

    searchField = new JTextField(25);

    tableModel = new DefaultTableModel(
      new String[]{"Username", "Full Name", "Role", "Active"}, 0) {
      @Override
      public boolean isCellEditable(int row, int col) {
        return false;
      }
    };
    table = new JTable(tableModel);
    table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    table.setRowHeight(24);
    table.getColumnModel().getColumn(0).setPreferredWidth(120);
    table.getColumnModel().getColumn(1).setPreferredWidth(180);
    table.getColumnModel().getColumn(2).setPreferredWidth(100);
    table.getColumnModel().getColumn(3).setPreferredWidth(60);

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

    saveButton = new JButton("Save User");
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
    panel.setBorder(new TitledBorder("User Information"));

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
    panel.add(new JLabel("Username:"), lbl);
    fld.gridy = 0;
    panel.add(usernameField, fld);
    lbl.gridy = 1;
    panel.add(new JLabel("Full Name:"), lbl);
    fld.gridy = 1;
    panel.add(fullNameField, fld);
    lbl.gridy = 2;
    panel.add(new JLabel("Role:"), lbl);
    fld.gridy = 2;
    panel.add(roleCombo, fld);
    lbl.gridy = 3;
    panel.add(new JLabel("Status:"), lbl);
    fld.gridy = 3;
    panel.add(activeCheckBox, fld);
    lbl.gridy = 4;
    panel.add(new JLabel("Password:"), lbl);
    fld.gridy = 4;
    panel.add(passwordField, fld);
    lbl.gridy = 5;
    panel.add(new JLabel(""), lbl);
    fld.gridy = 5;
    panel.add(passwordHintLabel, fld);

    return panel;
  }

  private JPanel buildTablePanel() {
    JPanel panel = new JPanel(new BorderLayout(4, 4));
    panel.setBorder(new TitledBorder("Users"));

    JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
    searchPanel.add(new JLabel("Search:"));
    searchPanel.add(searchField);
    panel.add(searchPanel, BorderLayout.NORTH);

    JScrollPane scroll = new JScrollPane(table);
    scroll.setPreferredSize(new Dimension(560, 180));
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
      ? null : RowFilter.regexFilter("(?i)" + Pattern.quote(text)));
  }

  private void handleTableSelection() {
    int viewRow = table.getSelectedRow();
    if (viewRow < 0) {
      editingUserId = null;
      passwordHintLabel.setText("Required for new users. Leave blank to keep current password.");
      updateButton.setEnabled(false);
      deleteButton.setEnabled(false);
      return;
    }
    int modelRow = table.convertRowIndexToModel(viewRow);
    editingUserId = rowUserIds.get(modelRow);
    usernameField.setText((String) tableModel.getValueAt(modelRow, 0));
    fullNameField.setText((String) tableModel.getValueAt(modelRow, 1));
    roleCombo.setSelectedItem(tableModel.getValueAt(modelRow, 2));
    activeCheckBox.setSelected((Boolean) tableModel.getValueAt(modelRow, 3));
    passwordField.setText("");
    passwordHintLabel.setText("Leave blank to keep current password.");

    String currentUserId = Session.getInstance().getCurrentUser().getId();
    boolean isSelf = editingUserId.equals(currentUserId);
    deleteButton.setEnabled(!isSelf);
    updateButton.setEnabled(true);
  }

  private void handleSave() {
    String username = usernameField.getText().trim();
    String fullName = fullNameField.getText().trim();
    String password = new String(passwordField.getPassword());
    Role role = (Role) roleCombo.getSelectedItem();

    if (username.isEmpty() || fullName.isEmpty()) {
      showError("Username and Full Name are required.");
      return;
    }
    if (password.isEmpty()) {
      showError("Password is required for new users.");
      return;
    }
    try {
      controller.createUser(username, fullName, role, activeCheckBox.isSelected(), password);
      JOptionPane.showMessageDialog(this,
        "User '" + username + "' created successfully.",
        "User Created", JOptionPane.INFORMATION_MESSAGE);
      handleClear();
      refreshTable();
    } catch (Exception e) {
      showError("Failed to create user: " + e.getMessage());
    }
  }

  private void handleUpdate() {
    if (editingUserId == null) {
      return;
    }
    String username = usernameField.getText().trim();
    String fullName = fullNameField.getText().trim();
    String password = new String(passwordField.getPassword());
    Role role = (Role) roleCombo.getSelectedItem();

    if (username.isEmpty() || fullName.isEmpty()) {
      showError("Username and Full Name are required.");
      return;
    }
    try {
      controller.updateUser(editingUserId, username, fullName, role, activeCheckBox.isSelected());
      if (!password.isEmpty()) {
        controller.changePassword(editingUserId, password);
      }
      JOptionPane.showMessageDialog(this,
        "User updated successfully.",
        "User Updated", JOptionPane.INFORMATION_MESSAGE);
      handleClear();
      refreshTable();
    } catch (Exception e) {
      showError("Failed to update user: " + e.getMessage());
    }
  }

  private void handleDelete() {
    if (editingUserId == null) {
      return;
    }
    int confirm = JOptionPane.showConfirmDialog(this,
      "Delete user '" + usernameField.getText().trim() + "'?",
      "Confirm Delete", JOptionPane.YES_NO_OPTION);
    if (confirm != JOptionPane.YES_OPTION) {
      return;
    }
    try {
      controller.deleteUser(editingUserId);
      handleClear();
      refreshTable();
    } catch (Exception e) {
      showError("Failed to delete user: " + e.getMessage());
    }
  }

  private void handleClear() {
    editingUserId = null;
    usernameField.setText("");
    fullNameField.setText("");
    roleCombo.setSelectedIndex(0);
    activeCheckBox.setSelected(true);
    passwordField.setText("");
    passwordHintLabel.setText("Required for new users. Leave blank to keep current password.");
    table.clearSelection();
    updateButton.setEnabled(false);
    deleteButton.setEnabled(false);
    usernameField.requestFocus();
  }

  private void refreshTable() {
    tableModel.setRowCount(0);
    rowUserIds.clear();
    for (User u : controller.getAllUsers()) {
      tableModel.addRow(new Object[]{
        u.getUsername(), u.getFullName(), u.getRole(), u.getActive()
      });
      rowUserIds.add(u.getId());
    }
  }

  private void showError(String message) {
    JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
  }

  private void configureFrame() {
    setTitle("User Management");
    setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
    pack();
    setMinimumSize(new Dimension(620, 580));
    setLocationRelativeTo(null);
  }
}

package pos.ui.frame;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.function.Consumer;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;
import pos.ui.controller.UserController;
import pos.user.model.User;

public class LoginFrame extends JFrame {

  private final UserController controller;
  private final Consumer<User> onSuccess;

  private JTextField usernameField;
  private JPasswordField passwordField;

  public LoginFrame(UserController controller, Consumer<User> onSuccess) {
    this.controller = controller;
    this.onSuccess = onSuccess;
    initComponents();
    layoutComponents();
    configureFrame();
  }

  private void initComponents() {
    usernameField = new JTextField(18);
    passwordField = new JPasswordField(18);
    passwordField.addActionListener(e -> handleLogin());
  }

  private void layoutComponents() {
    setLayout(new BorderLayout(10, 10));
    ((JPanel) getContentPane()).setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

    JLabel title = new JLabel("POS System", SwingConstants.CENTER);
    add(title, BorderLayout.NORTH);

    JPanel formPanel = new JPanel(new GridBagLayout());
    formPanel.setBorder(new TitledBorder("Login"));

    GridBagConstraints lbl = new GridBagConstraints();
    lbl.anchor = GridBagConstraints.EAST;
    lbl.insets = new Insets(8, 10, 8, 6);

    GridBagConstraints fld = new GridBagConstraints();
    fld.anchor = GridBagConstraints.WEST;
    fld.fill = GridBagConstraints.HORIZONTAL;
    fld.weightx = 1.0;
    fld.insets = new Insets(8, 0, 8, 10);
    fld.gridwidth = GridBagConstraints.REMAINDER;

    lbl.gridy = 0;
    formPanel.add(new JLabel("Username:"), lbl);
    fld.gridy = 0;
    formPanel.add(usernameField, fld);
    lbl.gridy = 1;
    formPanel.add(new JLabel("Password:"), lbl);
    fld.gridy = 1;
    formPanel.add(passwordField, fld);

    add(formPanel, BorderLayout.CENTER);

    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 8));
    JButton loginButton = new JButton("Login");
    loginButton.setPreferredSize(new Dimension(120, 32));
    loginButton.addActionListener(e -> handleLogin());
    buttonPanel.add(loginButton);
    add(buttonPanel, BorderLayout.SOUTH);
  }

  private void handleLogin() {
    String username = usernameField.getText().trim();
    String password = new String(passwordField.getPassword());
    if (username.isEmpty() || password.isEmpty()) {
      showError("Username and password are required.");
      return;
    }
    controller.authenticate(username, password).ifPresentOrElse(
      user -> {
        onSuccess.accept(user);
        dispose();
      },
      () -> {
        showError("Invalid credentials or inactive account.");
        passwordField.setText("");
        passwordField.requestFocus();
      }
    );
  }

  private void showError(String message) {
    JOptionPane.showMessageDialog(this, message, "Login Error", JOptionPane.ERROR_MESSAGE);
  }

  private void configureFrame() {
    setTitle("POS System — Login");
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    pack();
    setMinimumSize(new Dimension(320, 240));
    setLocationRelativeTo(null);
  }
}

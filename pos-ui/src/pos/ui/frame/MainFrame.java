package pos.ui.frame;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import pos.auth.model.Session;
import pos.inventory.model.StockItem;
import pos.product.model.Product;
import pos.ui.controller.InventoryController;
import pos.ui.controller.ProductController;
import pos.ui.controller.NewSaleController;
import pos.ui.controller.PurchaseOrderController;
import pos.ui.controller.SaleConfigController;
import pos.ui.controller.SaleHistoryController;
import pos.ui.controller.SupplierController;
import pos.ui.controller.UserController;

public class MainFrame extends JFrame {

  private final InventoryController inventoryController;
  private final Runnable onLogout;

  private final ProductFrame productFrame;
  private final InventoryFrame inventoryFrame;
  private final NewSaleFrame newSaleFrame;
  private final SaleHistoryFrame saleHistoryFrame;
  private final SupplierFrame supplierFrame;
  private final PurchaseOrderFrame purchaseOrderFrame;
  private final SettingsFrame settingsFrame;
  private final UserManagementFrame userManagementFrame;

  private JButton productsButton;
  private JButton inventoryButton;
  private JButton newSaleButton;
  private JButton saleHistoryButton;
  private JButton suppliersButton;
  private JButton purchaseOrderButton;
  private JButton settingsButton;
  private JButton userManagementButton;
  private JButton lowStockButton;

  public MainFrame(
    ProductController productController,
    InventoryController inventoryController,
    NewSaleController newSaleController,
    SaleHistoryController saleHistoryController,
    SupplierController supplierController,
    PurchaseOrderController purchaseOrderController,
    SaleConfigController saleConfigController,
    UserController userController,
    Runnable onLogout) {
    this.inventoryController = inventoryController;
    this.onLogout = onLogout;

    productFrame = new ProductFrame(productController);
    inventoryFrame = new InventoryFrame(inventoryController);
    newSaleFrame = new NewSaleFrame(newSaleController);
    saleHistoryFrame = new SaleHistoryFrame(saleHistoryController);
    supplierFrame = new SupplierFrame(supplierController);
    purchaseOrderFrame = new PurchaseOrderFrame(purchaseOrderController);
    settingsFrame = new SettingsFrame(saleConfigController);
    userManagementFrame = new UserManagementFrame(userController);

    productController.addChangeListener(inventoryFrame);
    productController.addChangeListener(newSaleFrame);
    productController.addChangeListener(purchaseOrderFrame);

    supplierController.addChangeListener(purchaseOrderFrame);
    supplierController.addChangeListener(productFrame);

    initComponents();
    applyRolePermissions();
    configureFrame();
  }

  private void initComponents() {
    setLayout(new BorderLayout(10, 10));
    ((JPanel) getContentPane()).setBorder(BorderFactory.createEmptyBorder(20, 40, 10, 40));

    JPanel headerPanel = new JPanel(new BorderLayout());
    String username = Session.getInstance().getCurrentUser().getFullName();
    String role = Session.getInstance().getCurrentUser().getRole().getDisplayName();
    JLabel userLabel = new JLabel(username + " (" + role + ")", SwingConstants.CENTER);
    JLabel title = new JLabel("POS System", SwingConstants.CENTER);
    headerPanel.add(title, BorderLayout.CENTER);
    headerPanel.add(userLabel, BorderLayout.SOUTH);
    add(headerPanel, BorderLayout.NORTH);

    productsButton = new JButton("Products");
    inventoryButton = new JButton("Inventory");
    newSaleButton = new JButton("New Sale");
    saleHistoryButton = new JButton("Sale History");
    suppliersButton = new JButton("Suppliers");
    purchaseOrderButton = new JButton("Purchase Orders");
    settingsButton = new JButton("Settings");
    userManagementButton = new JButton("User Management");
    lowStockButton = new JButton("Low Stock Alert");

    productsButton.addActionListener(e -> showFrame(productFrame));
    inventoryButton.addActionListener(e -> showFrame(inventoryFrame));
    newSaleButton.addActionListener(e -> showFrame(newSaleFrame));
    saleHistoryButton.addActionListener(e -> showFrame(saleHistoryFrame));
    suppliersButton.addActionListener(e -> showFrame(supplierFrame));
    purchaseOrderButton.addActionListener(e -> showFrame(purchaseOrderFrame));
    settingsButton.addActionListener(e -> showFrame(settingsFrame));
    userManagementButton.addActionListener(e -> showFrame(userManagementFrame));
    lowStockButton.addActionListener(e -> handleLowStockAlert());

    JPanel buttonPanel = new JPanel(new GridLayout(9, 1, 0, 8));
    buttonPanel.add(productsButton);
    buttonPanel.add(inventoryButton);
    buttonPanel.add(newSaleButton);
    buttonPanel.add(saleHistoryButton);
    buttonPanel.add(suppliersButton);
    buttonPanel.add(purchaseOrderButton);
    buttonPanel.add(settingsButton);
    buttonPanel.add(userManagementButton);
    buttonPanel.add(lowStockButton);
    add(buttonPanel, BorderLayout.CENTER);

    JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 4));
    JButton logoutButton = new JButton("Logout");
    logoutButton.addActionListener(e -> handleLogout());
    footerPanel.add(logoutButton);
    add(footerPanel, BorderLayout.SOUTH);
  }

  private void applyRolePermissions() {
    boolean isAdmin = Session.getInstance().isAdmin();
    boolean isManagerOrAbove = Session.getInstance().isManagerOrAbove();

    productsButton.setVisible(isManagerOrAbove);
    inventoryButton.setVisible(isManagerOrAbove);
    suppliersButton.setVisible(isManagerOrAbove);
    purchaseOrderButton.setVisible(isManagerOrAbove);
    lowStockButton.setVisible(isManagerOrAbove);
    settingsButton.setVisible(isAdmin);
    userManagementButton.setVisible(isAdmin);

    newSaleButton.setVisible(true);
    saleHistoryButton.setVisible(true);
  }

  private void showFrame(JFrame frame) {
    frame.setVisible(true);
    frame.toFront();
  }

  private void handleLogout() {
    int confirm = JOptionPane.showConfirmDialog(this,
      "Are you sure you want to logout?",
      "Confirm Logout", JOptionPane.YES_NO_OPTION);
    if (confirm != JOptionPane.YES_OPTION) {
      return;
    }
    dispose();
    onLogout.run();
  }

  private void handleLowStockAlert() {
    java.util.List<StockItem> lowStock = inventoryController.getLowStockItems();
    if (lowStock.isEmpty()) {
      JOptionPane.showMessageDialog(this,
        "All products have sufficient stock.",
        "Stock Status", JOptionPane.INFORMATION_MESSAGE);
      return;
    }
    StringBuilder message = new StringBuilder("Products with low stock:\n\n");
    for (StockItem item : lowStock) {
      String name = inventoryController.findProductById(item.getProductId())
        .map(Product::getName).orElse("Unknown");
      message.append("• ").append(name)
        .append("  —  Stock: ").append(item.getStock())
        .append("  /  Min: ").append(item.getMinStock())
        .append("\n");
    }
    JOptionPane.showMessageDialog(this,
      message.toString(), "Low Stock Alert", JOptionPane.WARNING_MESSAGE);
  }

  private void configureFrame() {
    setTitle("POS System");
    setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    addWindowListener(new java.awt.event.WindowAdapter() {
      @Override
      public void windowClosing(java.awt.event.WindowEvent e) {
        handleLogout();
      }
    });
    setMinimumSize(new Dimension(280, 580));
    pack();
    setLocationRelativeTo(null);
  }
}

package pos.ui.frame;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import pos.inventory.model.StockItem;
import pos.product.model.Product;
import pos.ui.controller.InventoryController;
import pos.ui.controller.ProductController;
import pos.ui.controller.NewSaleController;
import pos.ui.controller.PurchaseOrderController;
import pos.ui.controller.SaleConfigController;
import pos.ui.controller.SaleHistoryController;
import pos.ui.controller.SupplierController;

public class MainFrame extends JFrame {

  private final InventoryController inventoryController;

  private final ProductFrame productFrame;
  private final InventoryFrame inventoryFrame;
  private final NewSaleFrame newSaleFrame;
  private final SaleHistoryFrame saleHistoryFrame;
  private final SupplierFrame supplierFrame;
  private final PurchaseOrderFrame purchaseOrderFrame;
  private final SettingsFrame settingsFrame;

  public MainFrame(
    ProductController productController,
    InventoryController inventoryController,
    NewSaleController newSaleController,
    SaleHistoryController saleHistoryController,
    SupplierController supplierController,
    PurchaseOrderController purchaseOrderController,
    SaleConfigController saleConfigController) {
    this.inventoryController = inventoryController;

    productFrame = new ProductFrame(productController);
    inventoryFrame = new InventoryFrame(inventoryController);
    newSaleFrame = new NewSaleFrame(newSaleController);
    saleHistoryFrame = new SaleHistoryFrame(saleHistoryController);
    supplierFrame = new SupplierFrame(supplierController);
    purchaseOrderFrame = new PurchaseOrderFrame(purchaseOrderController);
    settingsFrame = new SettingsFrame(saleConfigController);

    productController.addChangeListener(inventoryFrame);
    productController.addChangeListener(newSaleFrame);
    productController.addChangeListener(purchaseOrderFrame);

    supplierController.addChangeListener(purchaseOrderFrame);
    supplierController.addChangeListener(productFrame);

    initComponents();
    configureFrame();
  }

  private void initComponents() {
    setLayout(new BorderLayout(10, 10));
    ((JPanel) getContentPane()).setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

    JLabel title = new JLabel("POS System", SwingConstants.CENTER);
    add(title, BorderLayout.NORTH);

    JPanel buttonPanel = new JPanel(new GridLayout(8, 1, 0, 10));

    JButton productsButton = new JButton("Products");
    JButton inventoryButton = new JButton("Inventory");
    JButton newSaleButton = new JButton("New Sale");
    JButton saleHistoryButton = new JButton("Sale History");
    JButton suppliersButton = new JButton("Suppliers");
    JButton purchaseOrderButton = new JButton("Purchase Orders");
    JButton settingsButton = new JButton("Settings");
    JButton lowStockButton = new JButton("Low Stock Alert");

    productsButton.addActionListener(e -> showFrame(productFrame));
    inventoryButton.addActionListener(e -> showFrame(inventoryFrame));
    newSaleButton.addActionListener(e -> showFrame(newSaleFrame));
    saleHistoryButton.addActionListener(e -> showFrame(saleHistoryFrame));
    suppliersButton.addActionListener(e -> showFrame(supplierFrame));
    purchaseOrderButton.addActionListener(e -> showFrame(purchaseOrderFrame));
    settingsButton.addActionListener(e -> showFrame(settingsFrame));
    lowStockButton.addActionListener(e -> handleLowStockAlert());

    buttonPanel.add(productsButton);
    buttonPanel.add(inventoryButton);
    buttonPanel.add(newSaleButton);
    buttonPanel.add(saleHistoryButton);
    buttonPanel.add(suppliersButton);
    buttonPanel.add(purchaseOrderButton);
    buttonPanel.add(settingsButton);
    buttonPanel.add(lowStockButton);

    add(buttonPanel, BorderLayout.CENTER);
  }

  private void showFrame(JFrame frame) {
    frame.setVisible(true);
    frame.toFront();
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
      message.toString(),
      "Low Stock Alert", JOptionPane.WARNING_MESSAGE);
  }

  private void configureFrame() {
    setTitle("POS System");
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setMinimumSize(new Dimension(280, 540));
    pack();
    setLocationRelativeTo(null);
  }
}

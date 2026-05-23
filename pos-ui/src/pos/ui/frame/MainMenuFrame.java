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
import pos.ui.controller.RegisterInventoryController;
import pos.ui.controller.RegisterProductController;
import pos.ui.controller.RegisterSaleController;
import pos.ui.controller.SalesHistoryController;

public class MainMenuFrame extends JFrame {

  private final RegisterProductController productController;
  private final RegisterInventoryController inventoryController;
  private final RegisterSaleController saleController;
  private final SalesHistoryController salesHistoryController;

  public MainMenuFrame(
    RegisterProductController productController,
    RegisterInventoryController inventoryController,
    RegisterSaleController saleController,
    SalesHistoryController salesHistoryController) {
    this.productController = productController;
    this.inventoryController = inventoryController;
    this.saleController = saleController;
    this.salesHistoryController = salesHistoryController;
    initComponents();
    configureFrame();
  }

  private void initComponents() {
    setLayout(new BorderLayout(10, 10));
    ((JPanel) getContentPane()).setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

    JLabel title = new JLabel("POS System", SwingConstants.CENTER);
    add(title, BorderLayout.NORTH);

    JPanel buttonPanel = new JPanel(new GridLayout(5, 1, 0, 10));

    JButton productsButton = new JButton("Products");
    JButton inventoryButton = new JButton("Inventory");
    JButton registerSaleButton = new JButton("Register Sale");
    JButton viewSalesButton = new JButton("View Sales");
    JButton lowStockButton = new JButton("Low Stock Alert");

    productsButton.addActionListener(e
      -> new RegisterProductFrame(productController).setVisible(true));
    inventoryButton.addActionListener(e
      -> new RegisterInventoryFrame(inventoryController).setVisible(true));
    registerSaleButton.addActionListener(e
      -> new RegisterSaleFrame(saleController).setVisible(true));
    viewSalesButton.addActionListener(e
      -> new SalesHistoryFrame(salesHistoryController).setVisible(true));
    lowStockButton.addActionListener(e -> handleLowStockAlert());

    buttonPanel.add(productsButton);
    buttonPanel.add(inventoryButton);
    buttonPanel.add(registerSaleButton);
    buttonPanel.add(viewSalesButton);
    buttonPanel.add(lowStockButton);

    add(buttonPanel, BorderLayout.CENTER);
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
    setMinimumSize(new Dimension(280, 360));
    pack();
    setLocationRelativeTo(null);
  }
}

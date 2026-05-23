package pos.app;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import org.bson.Document;
import pos.infrastructure.mongodb.MongoIndexInitializer;
import pos.infrastructure.mongodb.repository.InventoryMongoRepository;
import pos.infrastructure.mongodb.repository.ProductMongoRepository;
import pos.infrastructure.mongodb.repository.SaleMongoRepository;
import pos.inventory.listener.InventoryEventListener;
import pos.inventory.repository.InventoryRepository;
import pos.inventory.service.InventoryService;
import pos.inventory.service.InventoryServiceImpl;
import pos.inventory.validation.InventoryValidator;
import pos.product.repository.ProductRepository;
import pos.product.service.ProductServiceImpl;
import pos.product.validation.ProductValidator;
import pos.sale.repository.SaleRepository;
import pos.sale.service.SaleService;
import pos.sale.service.SaleServiceImpl;
import pos.sale.validation.SaleValidator;
import pos.ui.controller.InventoryController;
import pos.ui.controller.NewSaleController;
import pos.ui.controller.ProductController;
import pos.ui.controller.SaleHistoryController;
import pos.ui.frame.MainFrame;

public class App {

  public static void main(String[] args) {
    applyLookAndFeel();

    AppConfig config = new AppConfig();

    MongoClient mongoClient = connectToDatabase(config.getMongoUri());

    if (mongoClient == null) {
      return;
    }

    MongoDatabase database = mongoClient.getDatabase(config.getMongoDatabase());

    new MongoIndexInitializer(database).initialize();

    registerShutdownHook(mongoClient);

    ProductRepository productRepository = new ProductMongoRepository(database);
    InventoryRepository inventoryRepository = new InventoryMongoRepository(database);
    SaleRepository saleRepository = new SaleMongoRepository(database);

    ProductValidator productValidator = new ProductValidator();
    InventoryValidator inventoryValidator = new InventoryValidator();
    SaleValidator saleValidator = new SaleValidator();

    ProductServiceImpl productService = new ProductServiceImpl(productRepository, productValidator);

    InventoryService inventoryService = new InventoryServiceImpl(inventoryRepository, inventoryValidator, productService);

    SaleService saleService = new SaleServiceImpl(saleRepository, saleValidator, inventoryService);

    productService.addListener(new InventoryEventListener(inventoryRepository));

    NewSaleController newSaleController = new NewSaleController(saleService, productService);
    ProductController productController = new ProductController(productService);
    InventoryController inventoryController = new InventoryController(inventoryService, productService);
    SaleHistoryController saleHistoryController = new SaleHistoryController(saleService);

    SwingUtilities.invokeLater(()
      -> new MainFrame(
        productController,
        inventoryController,
        newSaleController,
        saleHistoryController
      ).setVisible(true)
    );
  }

  private static MongoClient connectToDatabase(String mongoUri) {
    try {
      MongoClient client = MongoClients.create(mongoUri);
      client.getDatabase("admin").runCommand(new Document("ping", 1));
      return client;
    } catch (Exception e) {
      JOptionPane.showMessageDialog(null,
        "Could not connect to the database.\n" + e.getMessage(),
        "Connection Error", JOptionPane.ERROR_MESSAGE);
      return null;
    }
  }

  private static void registerShutdownHook(MongoClient mongoClient) {
    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
      mongoClient.close();

      System.out.println("MongoDB connection closed.");
    }));
  }

  private static void applyLookAndFeel() {
    try {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    } catch (ClassNotFoundException | IllegalAccessException | InstantiationException | UnsupportedLookAndFeelException e) {
      // ...
    }
  }
}

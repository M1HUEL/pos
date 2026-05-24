package pos.app;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import org.bson.Document;
import pos.auth.model.Session;
import pos.infrastructure.mongodb.MongoIndexInitializer;
import pos.infrastructure.mongodb.repository.InventoryMongoRepository;
import pos.infrastructure.mongodb.repository.ProductMongoRepository;
import pos.infrastructure.mongodb.repository.PurchaseOrderMongoRepository;
import pos.infrastructure.mongodb.repository.SaleConfigMongoRepository;
import pos.infrastructure.mongodb.repository.SaleMongoRepository;
import pos.infrastructure.mongodb.repository.SupplierMongoRepository;
import pos.infrastructure.mongodb.repository.UserMongoRepository;
import pos.inventory.listener.InventoryEventListener;
import pos.inventory.repository.InventoryRepository;
import pos.inventory.service.InventoryService;
import pos.inventory.service.InventoryServiceImpl;
import pos.inventory.validation.InventoryValidator;
import pos.product.repository.ProductRepository;
import pos.product.service.ProductServiceImpl;
import pos.product.validation.ProductValidator;
import pos.purchase.repository.PurchaseOrderRepository;
import pos.purchase.service.PurchaseOrderService;
import pos.purchase.service.PurchaseOrderServiceImpl;
import pos.purchase.validation.PurchaseOrderValidator;
import pos.sale.config.repository.SaleConfigRepository;
import pos.sale.config.service.SaleConfigService;
import pos.sale.config.service.SaleConfigServiceImpl;
import pos.sale.config.validation.SaleConfigValidator;
import pos.sale.repository.SaleRepository;
import pos.sale.service.SaleService;
import pos.sale.service.SaleServiceImpl;
import pos.sale.validation.SaleValidator;
import pos.supplier.repository.SupplierRepository;
import pos.supplier.service.SupplierService;
import pos.supplier.service.SupplierServiceImpl;
import pos.supplier.validation.SupplierValidator;
import pos.ui.controller.InventoryController;
import pos.ui.controller.NewSaleController;
import pos.ui.controller.ProductController;
import pos.ui.controller.PurchaseOrderController;
import pos.ui.controller.SaleConfigController;
import pos.ui.controller.SaleHistoryController;
import pos.ui.controller.SupplierController;
import pos.ui.controller.UserController;
import pos.ui.frame.LoginFrame;
import pos.ui.frame.MainFrame;
import pos.user.model.Role;
import pos.user.model.User;
import pos.user.repository.UserRepository;
import pos.user.service.UserService;
import pos.user.service.UserServiceImpl;
import pos.user.validation.UserValidator;

public class App {

  private ProductController productController;
  private InventoryController inventoryController;
  private NewSaleController newSaleController;
  private SaleHistoryController saleHistoryController;
  private SupplierController supplierController;
  private PurchaseOrderController purchaseOrderController;
  private SaleConfigController saleConfigController;
  private UserController userController;

  public static void main(String[] args) {
    Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(null, "Unexpected error: " + throwable.getMessage(), "Error", JOptionPane.ERROR_MESSAGE)));

    new App().start();
  }

  private void start() {
    applyLookAndFeel();

    AppConfig config = new AppConfig();

    MongoClient mongoClient = connectToDatabase(config.getMongoUri());

    if (mongoClient == null) {
      return;
    }

    MongoDatabase database = mongoClient.getDatabase(config.getMongoDatabase());

    new MongoIndexInitializer(database).initialize();

    registerShutdownHook(mongoClient);

    initializeDependencies(database);

    createDefaultAdminIfNeeded();

    SwingUtilities.invokeLater(this::showLogin);
  }

  private void initializeDependencies(MongoDatabase database) {
    ProductRepository productRepository = new ProductMongoRepository(database);
    InventoryRepository inventoryRepository = new InventoryMongoRepository(database);
    SaleRepository saleRepository = new SaleMongoRepository(database);
    SupplierRepository supplierRepository = new SupplierMongoRepository(database);
    PurchaseOrderRepository purchaseOrderRepository = new PurchaseOrderMongoRepository(database);
    SaleConfigRepository saleConfigRepository = new SaleConfigMongoRepository(database);
    UserRepository userRepository = new UserMongoRepository(database);

    ProductValidator productValidator = new ProductValidator();
    InventoryValidator inventoryValidator = new InventoryValidator();
    SaleValidator saleValidator = new SaleValidator();
    SupplierValidator supplierValidator = new SupplierValidator();
    PurchaseOrderValidator purchaseOrderValidator = new PurchaseOrderValidator();
    SaleConfigValidator saleConfigValidator = new SaleConfigValidator();
    UserValidator userValidator = new UserValidator();

    ProductServiceImpl productService = new ProductServiceImpl(productRepository, productValidator);
    InventoryService inventoryService = new InventoryServiceImpl(inventoryRepository, inventoryValidator, productService);
    SaleService saleService = new SaleServiceImpl(saleRepository, saleValidator, inventoryService);
    SupplierService supplierService = new SupplierServiceImpl(supplierRepository, supplierValidator);
    PurchaseOrderService purchaseOrderService = new PurchaseOrderServiceImpl(purchaseOrderRepository, purchaseOrderValidator, inventoryService, supplierService);
    SaleConfigService saleConfigService = new SaleConfigServiceImpl(saleConfigRepository, saleConfigValidator);
    UserService userService = new UserServiceImpl(userRepository, userValidator);

    productService.addListener(new InventoryEventListener(inventoryRepository));

    productController = new ProductController(productService, supplierService);
    inventoryController = new InventoryController(inventoryService, productService);
    newSaleController = new NewSaleController(saleService, productService, saleConfigService);
    saleHistoryController = new SaleHistoryController(saleService);
    supplierController = new SupplierController(supplierService);
    purchaseOrderController = new PurchaseOrderController(purchaseOrderService, supplierService, productService);
    saleConfigController = new SaleConfigController(saleConfigService);
    userController = new UserController(userService);
  }

  private void createDefaultAdminIfNeeded() {
    if (userController.getAllUsers().isEmpty()) {
      User admin = new User();
      admin.setUsername("admin");
      admin.setFullName("Administrator");
      admin.setRole(Role.ADMIN);
      admin.setActive(true);

      userController.createUser("admin", "Administrator", Role.ADMIN, true, "admin123");

      System.out.println("Default admin created — username: admin, password: admin123");
    }
  }

  private void showLogin() {
    new LoginFrame(userController, this::onLoginSuccess).setVisible(true);
  }

  private void onLoginSuccess(User user) {
    Session.getInstance().login(user);

    showMain();
  }

  private void showMain() {
    MainFrame mainFrame = new MainFrame(productController, inventoryController, newSaleController, saleHistoryController, supplierController, purchaseOrderController, saleConfigController, userController, this::onLogout);

    mainFrame.setVisible(true);
  }

  private void onLogout() {
    Session.getInstance().logout();

    SwingUtilities.invokeLater(this::showLogin);
  }

  private static MongoClient connectToDatabase(String mongoUri) {
    try {
      MongoClient client = MongoClients.create(mongoUri);

      client.getDatabase("admin").runCommand(new Document("ping", 1));

      return client;
    } catch (Exception e) {
      JOptionPane.showMessageDialog(null, "Could not connect to the database.\n" + e.getMessage(), "Connection Error", JOptionPane.ERROR_MESSAGE);

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

package pos.ui.frame;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import pos.sale.model.Sale;
import pos.sale.model.SaleItem;
import pos.sale.model.SaleStatus;
import pos.ui.controller.SaleHistoryController;

public class SaleHistoryFrame extends JFrame {

  private static final DateTimeFormatter DATE_FORMAT
    = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

  private final SaleHistoryController controller;

  private JTextField searchField;
  private DefaultTableModel salesTableModel;
  private JTable salesTable;
  private TableRowSorter<DefaultTableModel> sorter;
  private final List<Sale> rowSales = new ArrayList<>();

  private DefaultTableModel itemsTableModel;
  private JTable itemsTable;

  private JButton cancelButton;

  public SaleHistoryFrame(SaleHistoryController controller) {
    this.controller = controller;
    initComponents();
    layoutComponents();
    configureFrame();
    refreshTable();
  }

  private void initComponents() {
    searchField = new JTextField(30);

    salesTableModel = new DefaultTableModel(
      new String[]{"Sale #", "Date", "Payment", "Discount", "Tax", "Total", "Status"}, 0) {
      @Override
      public boolean isCellEditable(int row, int col) {
        return false;
      }
    };
    salesTable = new JTable(salesTableModel);
    salesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    salesTable.setRowHeight(24);
    salesTable.getColumnModel().getColumn(0).setPreferredWidth(280);
    salesTable.getColumnModel().getColumn(1).setPreferredWidth(120);
    salesTable.getColumnModel().getColumn(2).setPreferredWidth(80);
    salesTable.getColumnModel().getColumn(3).setPreferredWidth(80);
    salesTable.getColumnModel().getColumn(4).setPreferredWidth(80);
    salesTable.getColumnModel().getColumn(5).setPreferredWidth(90);
    salesTable.getColumnModel().getColumn(6).setPreferredWidth(90);

    sorter = new TableRowSorter<>(salesTableModel);
    salesTable.setRowSorter(sorter);

    salesTable.getSelectionModel().addListSelectionListener(e -> {
      if (!e.getValueIsAdjusting()) {
        handleSaleSelection();
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

    itemsTableModel = new DefaultTableModel(
      new String[]{"Product", "Qty", "Unit Price", "Discount", "Subtotal"}, 0) {
      @Override
      public boolean isCellEditable(int row, int col) {
        return false;
      }
    };
    itemsTable = new JTable(itemsTableModel);
    itemsTable.setRowHeight(24);
    itemsTable.getColumnModel().getColumn(0).setPreferredWidth(200);
    itemsTable.getColumnModel().getColumn(1).setPreferredWidth(50);
    itemsTable.getColumnModel().getColumn(2).setPreferredWidth(90);
    itemsTable.getColumnModel().getColumn(3).setPreferredWidth(90);
    itemsTable.getColumnModel().getColumn(4).setPreferredWidth(90);

    cancelButton = new JButton("Cancel Sale");
    cancelButton.setEnabled(false);
    cancelButton.addActionListener(e -> handleCancelSale());
  }

  private void layoutComponents() {
    setLayout(new BorderLayout(8, 8));
    ((JPanel) getContentPane()).setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

    JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
    searchPanel.add(new JLabel("Search:"));
    searchPanel.add(searchField);
    add(searchPanel, BorderLayout.NORTH);

    JPanel salesPanel = new JPanel(new BorderLayout());
    salesPanel.setBorder(new TitledBorder("Sales"));
    salesPanel.add(new JScrollPane(salesTable), BorderLayout.CENTER);

    JPanel itemsPanel = new JPanel(new BorderLayout());
    itemsPanel.setBorder(new TitledBorder("Items"));
    itemsPanel.add(new JScrollPane(itemsTable), BorderLayout.CENTER);

    JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, salesPanel, itemsPanel);
    splitPane.setResizeWeight(0.6);
    add(splitPane, BorderLayout.CENTER);

    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 8));
    buttonPanel.add(cancelButton);
    add(buttonPanel, BorderLayout.SOUTH);
  }

  private void handleSaleSelection() {
    int viewRow = salesTable.getSelectedRow();
    if (viewRow < 0) {
      itemsTableModel.setRowCount(0);
      cancelButton.setEnabled(false);
      return;
    }
    int modelRow = salesTable.convertRowIndexToModel(viewRow);
    Sale sale = rowSales.get(modelRow);

    cancelButton.setEnabled(SaleStatus.COMPLETED == sale.getStatus());

    itemsTableModel.setRowCount(0);
    if (sale.getItems() != null) {
      for (SaleItem item : sale.getItems()) {
        itemsTableModel.addRow(new Object[]{
          item.getProductName(),
          item.getQuantity(),
          "$" + item.getUnitPrice(),
          "$" + item.getDiscountAmount(),
          "$" + item.getSubTotal()
        });
      }
    }
  }

  private void handleCancelSale() {
    int viewRow = salesTable.getSelectedRow();
    if (viewRow < 0) {
      return;
    }
    int modelRow = salesTable.convertRowIndexToModel(viewRow);
    Sale sale = rowSales.get(modelRow);

    int confirm = JOptionPane.showConfirmDialog(this,
      "Cancel sale " + sale.getSaleNumber() + "?",
      "Confirm Cancellation", JOptionPane.YES_NO_OPTION);
    if (confirm != JOptionPane.YES_OPTION) {
      return;
    }

    try {
      controller.cancelSale(sale.getId());
      refreshTable();
      itemsTableModel.setRowCount(0);
      cancelButton.setEnabled(false);
    } catch (Exception e) {
      JOptionPane.showMessageDialog(this,
        "Failed to cancel sale: " + e.getMessage(),
        "Error", JOptionPane.ERROR_MESSAGE);
    }
  }

  private void applyFilter() {
    String text = searchField.getText().trim();
    sorter.setRowFilter(text.isEmpty()
      ? null
      : RowFilter.regexFilter("(?i)" + Pattern.quote(text)));
  }

  private void refreshTable() {
    salesTableModel.setRowCount(0);
    rowSales.clear();
    for (Sale sale : controller.getAllSales()) {
      salesTableModel.addRow(new Object[]{
        sale.getSaleNumber(),
        sale.getDateTime() != null ? sale.getDateTime().format(DATE_FORMAT) : "",
        sale.getPaymentMethod() != null ? sale.getPaymentMethod().getDisplayName() : "",
        "$" + sale.getDiscountAmount(),
        "$" + sale.getTaxAmount(),
        "$" + sale.getTotalAmount(),
        sale.getStatus() != null ? sale.getStatus().name() : ""
      });
      rowSales.add(sale);
    }
  }

  private void configureFrame() {
    setTitle("Sales");
    setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    setMinimumSize(new Dimension(780, 560));
    pack();
    setLocationRelativeTo(null);
  }
}

package pos.ui.controller;

import java.util.List;
import pos.sale.model.Sale;
import pos.sale.service.SaleService;

public class SaleHistoryController {

  private final SaleService saleService;

  public SaleHistoryController(SaleService saleService) {
    this.saleService = saleService;
  }

  public List<Sale> getAllSales() {
    return saleService.getAllSales();
  }

  public void cancelSale(String id) {
    saleService.cancelSale(id);
  }
}

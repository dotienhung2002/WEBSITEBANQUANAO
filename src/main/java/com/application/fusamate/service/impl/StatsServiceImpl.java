package com.application.fusamate.service.impl;

import com.application.fusamate.dto.stats.HumanStatsDto;
import com.application.fusamate.dto.stats.OrderStatusDto;
import com.application.fusamate.dto.stats.ProductStatusDto;
import com.application.fusamate.dto.stats.StatusCountStatsDto;
import com.application.fusamate.entity.stats.AnnualReport;
import com.application.fusamate.entity.stats.CustomerGrowthReport;
import com.application.fusamate.entity.stats.TopSalesCustomer;
import com.application.fusamate.entity.stats.TopSalesProduct;
import com.application.fusamate.repository.*;
import com.application.fusamate.service.StatsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class StatsServiceImpl implements StatsService {

    private final EmployeeRepository employeeRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;
    private final MadeInRepository madeInRepository;
    private final OrdersRepository ordersRepository;
    private final AnnualReportRepository annualReportRepository;
    private final CustomerGrowthReportRepository customerGrowthReportRepository;
    private final TopSalesCustomerRepository topSalesCustomerRepository;
    private final TopSalesProductRepository topSalesProductRepository;

    @Override
    public StatusCountStatsDto getStatusCountStats() {
        HumanStatsDto employeeStats = new HumanStatsDto();
        employeeStats.setActive(employeeRepository.countByStatus(1));
        employeeStats.setInactive(employeeRepository.countByStatus(0));

        HumanStatsDto customerStats = new HumanStatsDto();
        customerStats.setActive(customerRepository.countByStatus(1));
        customerStats.setInactive(customerRepository.countByStatus(0));

        ProductStatusDto productStats = new ProductStatusDto();
        productStats.setActive(productRepository.countByStatus(1));
        productStats.setInactive(productRepository.countByStatus(-1));
        productStats.setOutOfStock(productRepository.countByStatus(0));
        ProductStatusDto.CategoryStatusDto categoryStats = new ProductStatusDto.CategoryStatusDto();
        categoryStats.setActive(categoryRepository.countByStatus(1));
        categoryStats.setInactive(categoryRepository.countByStatus(0));
        ProductStatusDto.BrandStatusDto brandStats = new ProductStatusDto.BrandStatusDto();
        brandStats.setActive(brandRepository.countByStatus(1));
        brandStats.setInactive(brandRepository.countByStatus(0));
        productStats.setCategoryStats(categoryStats);
        productStats.setBrandStats(brandStats);
        productStats.setAllMadeIns((long) madeInRepository.findAll().size());

        OrderStatusDto orderStats = new OrderStatusDto();
        orderStats.setWaitConfirm(ordersRepository.countByStatus(0));
        orderStats.setConfirmed(ordersRepository.countByStatus(1));
        orderStats.setWaitPrepare(ordersRepository.countByStatus(2));
        orderStats.setShipping(ordersRepository.countByStatus(3));
        orderStats.setShipped(ordersRepository.countByStatus(4));
        orderStats.setCancelled(ordersRepository.countByStatus(5));
        orderStats.setPaid(ordersRepository.countByPaymentStatus(true));
        orderStats.setUnpaid(ordersRepository.countByPaymentStatus(false));

        StatusCountStatsDto statusCountStats = new StatusCountStatsDto();
        statusCountStats.setEmployeeStats(employeeStats);
        statusCountStats.setCustomerStats(customerStats);
        statusCountStats.setProductStatusDto(productStats);
        statusCountStats.setOrderStatusDto(orderStats);

        return statusCountStats;
    }

    @Override
    public List<AnnualReport> getAnnualReport() {
        annualReportRepository.deleteAll();
        return annualReportRepository.saveAll(ordersRepository.revenueByMonthYear());
    }

    @Override
    public List<CustomerGrowthReport> getCustomerGrowthReport() {
        customerGrowthReportRepository.deleteAll();
        return customerGrowthReportRepository.saveAll(customerRepository.getCustomerGrowthReport());
    }

    @Override
    public List<TopSalesCustomer> getTopSalesCustomers(Integer year, Integer month) {
        if (year == null && month != null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Phải chọn năm nếu như chọn tháng");
        topSalesCustomerRepository.deleteAll();
        if (year == null && month == null)
            return topSalesCustomerRepository.saveAll(ordersRepository.topSalesCustomers());
        if (year != null && month == null)
            return topSalesCustomerRepository.saveAll(ordersRepository.topSalesCustomersByYear(year));
        return topSalesCustomerRepository.saveAll(ordersRepository.topSalesCustomersByYearAndMonth(year, month));
    }

    @Override
    public List<TopSalesProduct> getTopSalesProducts(Integer year, Integer month) {
        if (year == null && month != null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Phải chọn năm nếu như chọn tháng");
        topSalesProductRepository.deleteAll();
        if (year == null && month == null)
            return topSalesProductRepository.saveAll(topSalesProductRepository.getTopSalesProduct());
        if (year != null && month == null)
            return topSalesProductRepository.saveAll(topSalesProductRepository.getTopSalesProductByYear(year));
        return topSalesProductRepository.saveAll(topSalesProductRepository.getTopSalesProductByYearAndMonth(year, month));
    }
}

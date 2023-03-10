package com.application.fusamate.service;

import com.application.fusamate.dto.stats.StatusCountStatsDto;
import com.application.fusamate.entity.stats.AnnualReport;
import com.application.fusamate.entity.stats.CustomerGrowthReport;
import com.application.fusamate.entity.stats.TopSalesCustomer;
import com.application.fusamate.entity.stats.TopSalesProduct;

import java.util.List;

public interface StatsService {

    StatusCountStatsDto getStatusCountStats();

    List<AnnualReport> getAnnualReport();

    List<CustomerGrowthReport> getCustomerGrowthReport();

    List<TopSalesCustomer> getTopSalesCustomers(Integer year, Integer month);

    List<TopSalesProduct> getTopSalesProducts(Integer year, Integer month);

}

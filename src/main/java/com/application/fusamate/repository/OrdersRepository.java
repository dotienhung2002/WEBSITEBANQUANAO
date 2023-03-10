package com.application.fusamate.repository;

import com.application.fusamate.entity.Orders;
import com.application.fusamate.entity.stats.AnnualReport;
import com.application.fusamate.entity.stats.TopSalesCustomer;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrdersRepository extends JpaRepository<Orders, Long> {
    long countByStatus(@NonNull int status);

    long countByPaymentStatus(@NonNull Boolean paymentStatus);
    List<Orders> findByEmail(@NonNull String email);

    @Query("select new AnnualReport(year(o.createdAt), month(o.createdAt), " +
            "sum(o.totalPayment), count(o), count(distinct o.email), count(distinct o.name)) " +
            "from Orders o where o.status = 4 and o.paymentStatus = true " +
            "group by year(o.createdAt), month(o.createdAt)")
    List<AnnualReport> revenueByMonthYear();

    @Query("SELECT new TopSalesCustomer(YEAR(o.createdAt), MONTH(o.createdAt), o.email, SUM(o.totalPayment))" +
            "from Orders o " +
            "where YEAR(o.createdAt) = ?1 and MONTH(o.createdAt) = ?2 and o.status = 4 and o.paymentStatus = true " +
            "group by 1,2,3 " +
            "ORDER by SUM(o.totalPayment) desc")
    List<TopSalesCustomer> topSalesCustomersByYearAndMonth(@NonNull Integer year, @NonNull Integer month);

    @Query("SELECT new TopSalesCustomer(YEAR(o.createdAt), MONTH(o.createdAt), o.email, SUM(o.totalPayment))" +
            "from Orders o " +
            "where YEAR(o.createdAt) = ?1 and o.status = 4 and o.paymentStatus = true " +
            "group by 1,2,3 " +
            "ORDER by SUM(o.totalPayment) desc")
    List<TopSalesCustomer> topSalesCustomersByYear(@NonNull Integer year);

    @Query("SELECT new TopSalesCustomer(YEAR(o.createdAt), MONTH(o.createdAt), o.email, SUM(o.totalPayment))" +
            "from Orders o " +
            "where o.status = 4 and o.paymentStatus = true " +
            "group by 1,2,3 " +
            "ORDER by SUM(o.totalPayment) desc")
    List<TopSalesCustomer> topSalesCustomers();

}
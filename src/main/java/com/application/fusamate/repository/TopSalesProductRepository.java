package com.application.fusamate.repository;

import com.application.fusamate.entity.stats.TopSalesProduct;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TopSalesProductRepository extends JpaRepository<TopSalesProduct, Long> {

    @Query("SELECT new TopSalesProduct(YEAR(o.createdAt), MONTH(o.createdAt), p.id, p.name, SUM(sp.quantity), SUM(sp.subPrice)) \n" +
            "from Product p join ProductDetail pd on p.id = pd.product.id \n" +
            "\t\t\t\tjoin StatsProduct sp on sp.productDetail.id = pd.id\n" +
            "\t\t\t\tjoin OrderDetail od on od.id = sp.orderDetail.id\n" +
            "\t\t\t\tjoin Orders o on o.id = od.orders.id\n" +
            "WHERE YEAR(o.createdAt) = ?1 and MONTH(o.createdAt) = ?2 and o.status = 4 and o.paymentStatus = true\n" +
            "group by 1,2,3,4\n" +
            "ORDER BY SUM(sp.quantity) desc")
    List<TopSalesProduct> getTopSalesProductByYearAndMonth(@NonNull Integer year, @NonNull Integer month);

    @Query("SELECT new TopSalesProduct(YEAR(o.createdAt), MONTH(o.createdAt), p.id, p.name, SUM(sp.quantity), SUM(sp.subPrice)) \n" +
            "from Product p join ProductDetail pd on p.id = pd.product.id \n" +
            "\t\t\t\tjoin StatsProduct sp on sp.productDetail.id = pd.id\n" +
            "\t\t\t\tjoin OrderDetail od on od.id = sp.orderDetail.id\n" +
            "\t\t\t\tjoin Orders o on o.id = od.orders.id\n" +
            "WHERE YEAR(o.createdAt) = ?1 and o.status = 4 and o.paymentStatus = true\n" +
            "group by 1,2,3,4\n" +
            "ORDER BY SUM(sp.quantity) desc")
    List<TopSalesProduct> getTopSalesProductByYear(@NonNull Integer year);

    @Query("SELECT new TopSalesProduct(YEAR(o.createdAt), MONTH(o.createdAt), p.id, p.name, SUM(sp.quantity), SUM(sp.subPrice)) \n" +
            "from Product p join ProductDetail pd on p.id = pd.product.id \n" +
            "\t\t\t\tjoin StatsProduct sp on sp.productDetail.id = pd.id\n" +
            "\t\t\t\tjoin OrderDetail od on od.id = sp.orderDetail.id\n" +
            "\t\t\t\tjoin Orders o on o.id = od.orders.id\n" +
            "WHERE o.status = 4 and o.paymentStatus = true\n" +
            "group by 1,2,3,4\n" +
            "ORDER BY SUM(sp.quantity) desc")
    List<TopSalesProduct> getTopSalesProduct();

}
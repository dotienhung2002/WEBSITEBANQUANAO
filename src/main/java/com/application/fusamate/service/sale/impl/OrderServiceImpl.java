package com.application.fusamate.service.sale.impl;

import com.application.fusamate.dto.UpdateOrdersDto;
import com.application.fusamate.dto.sale.OrdersSaleDto;
import com.application.fusamate.entity.*;
import com.application.fusamate.model.OrderSearchCriteriaModel;
import com.application.fusamate.repository.*;
import com.application.fusamate.repository.criteria.OrderCriteriaRepository;
import com.application.fusamate.service.VoucherService;
import com.application.fusamate.service.sale.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {
    private final OrdersRepository ordersRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final CartGeneralRepository cartGeneralRepository;
    private final CartProductRepository cartProductRepository;
    private final StatsProductRepository statsProductRepository;
    private final ProductRepository productRepository;
    private final VoucherRepository voucherRepository;
    private final ColorRepository colorRepository;
    private final SizeRepository sizeRepository;
    private final ProductDetailRepository productDetailRepository;
    private final OrderCriteriaRepository orderCriteriaRepository;
    private final CustomerVoucherRecordRepository customerVoucherRecordRepository;
    private final VoucherService voucherService;

    @Override
    public Page<Orders> getOrders(OrderSearchCriteriaModel model) {
        return orderCriteriaRepository.findAllWithFilters(model);
    }

    @Override
    @Transactional
    public ResponseEntity<?> payCart(OrdersSaleDto ordersSaleDto) {
        CartGeneral cartGeneral = cartGeneralRepository.findById(ordersSaleDto.getCartId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không truy cập được giỏ hàng"));
        // TODO: check validate cart
        ResponseEntity<?> validateCart = checkValidCart(cartGeneral);
        if (validateCart != null) return validateCart;

        // Set input values for Orders
        Orders newOrders = new Orders();
        newOrders.setName(ordersSaleDto.getName());
        newOrders.setEmail(ordersSaleDto.getEmail());
        newOrders.setPhone(ordersSaleDto.getPhone());
        newOrders.setAddress(ordersSaleDto.getAddress() + ", " + ordersSaleDto.getWard() + ", "
                + ordersSaleDto.getDistrict() + ", " + ordersSaleDto.getProvince());
        newOrders.setWard(ordersSaleDto.getWard());
        newOrders.setDistrict(ordersSaleDto.getDistrict());
        newOrders.setProvince(ordersSaleDto.getProvince());
        newOrders.setShipType(ordersSaleDto.getShipType());
        newOrders.setPaymentType(ordersSaleDto.getPaymentType());
        newOrders.setPaymentStatus(false);
        newOrders.setShipCost(ordersSaleDto.getShipCost());
        newOrders.setNote(ordersSaleDto.getNote());

        // Save new Orders
        Orders addedOrders = ordersRepository.save(newOrders);

        // TODO: Apply voucher
        addedOrders.setVoucher(0);
        if (!ordersSaleDto.getListVoucher().isEmpty()) {
            ordersSaleDto.getListVoucher().forEach(voucherDto -> {

                if (!customerVoucherRecordRepository.findByEmailLikeIgnoreCaseAndCodeLikeIgnoreCase(ordersSaleDto.getEmail(), voucherDto.getCode()).isEmpty())
                    throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Bạn đã sử dụng voucher này do mỗi voucher chỉ áp dụng cho 1 khách hàng");

                // Apply voucher for anonymous
                if (voucherService.getUsableVoucherForAnonymous().contains(voucherRepository.findByCodeLikeIgnoreCase(ordersSaleDto.getListVoucher().get(0).getCode()))) {
                    Voucher validVoucher = voucherRepository.findByCodeLikeIgnoreCase(ordersSaleDto.getListVoucher().get(0).getCode());
                    addedOrders.setVoucher(addedOrders.getVoucher() + validVoucher.getMoney());
                    validVoucher.setSlot(validVoucher.getSlot() - 1);
                    validVoucher.setActive(validVoucher.getSlot() > 0 ? 1 : 0);
                    validVoucher.setOrders(addedOrders);
                    voucherRepository.save(validVoucher);
                } else {
                    // Apply voucher for logged-in user
                    List<Voucher> validVouchers = voucherService.getUsableVouchersByEmail(ordersSaleDto.getEmail())
                            .stream().filter(voucher -> voucher.getCode().equalsIgnoreCase(voucherDto.getCode()))
                            .collect(Collectors.toList());

                    if (validVouchers.isEmpty())
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Mã voucher không hợp lệ");

                    Voucher validVoucher = validVouchers.get(0);
                    addedOrders.setVoucher(addedOrders.getVoucher() + validVoucher.getMoney());
                    validVoucher.setSlot(validVoucher.getSlot() - 1);
                    validVoucher.setActive(validVoucher.getSlot() > 0 ? 1 : 0);
                    validVoucher.setOrders(addedOrders);
                    voucherRepository.save(validVoucher);
                }

                CustomerVoucherRecord customerVoucherRecord = new CustomerVoucherRecord();
                customerVoucherRecord.setCode(voucherDto.getCode());
                customerVoucherRecord.setEmail(ordersSaleDto.getEmail());
                customerVoucherRecordRepository.save(customerVoucherRecord);
            });
        }
        addedOrders.setVoucher((double) Math.round(addedOrders.getVoucher() * 100) / 100);

        ordersRepository.save(addedOrders);

        // Set input values for OrderDetail
        List<OrderDetail> orderDetailList = new ArrayList<>();
        cartGeneral.getCartProducts().forEach(cartItem -> {
            OrderDetail newOrderDetail = new OrderDetail();
            newOrderDetail.setName(cartItem.getProductDetail().getProduct().getName());
            if (!cartItem.getProductDetail().getProduct().getListProductImage().isEmpty())
                newOrderDetail.setImage(cartItem.getProductDetail().getProduct().getListProductImage().get(0).getImage());
            newOrderDetail.setVariant(colorRepository.findById(cartItem.getProductDetail().getColor().getId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Màu sắc không tồn tại")).getName() + " | " +
                    sizeRepository.findById(cartItem.getProductDetail().getSize().getId())
                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Kích cỡ không tồn tại")).getName());
            newOrderDetail.setQuantity(cartItem.getQuantity());
            newOrderDetail.setListedPrice(cartItem.getProductDetail().getPromotionPrice());
            newOrderDetail.setSubPrice(cartItem.getQuantity() * cartItem.getProductDetail().getPromotionPrice());
            newOrderDetail.setOrders(newOrders);
            orderDetailRepository.save(newOrderDetail);
            orderDetailList.add(newOrderDetail);

            // save order-included items records for stats
            StatsProduct newStatsProduct = new StatsProduct();
            newStatsProduct.setQuantity(newOrderDetail.getQuantity());
            newStatsProduct.setListedPrice(newOrderDetail.getListedPrice());
            newStatsProduct.setSubPrice(newOrderDetail.getSubPrice());
            newStatsProduct.setProductDetail(cartItem.getProductDetail());
            newStatsProduct.setOrderDetail(newOrderDetail);
            statsProductRepository.save(newStatsProduct);
        });

        // Set Order Detail list for saved Orders
        addedOrders.setListOrderDetail(orderDetailList);

        // Set totalPrice and totalPayment for saved Orders
        addedOrders.setTotalPrice((double) Math.round(
                addedOrders.getListOrderDetail().stream()
                        .mapToDouble(OrderDetail::getSubPrice).sum() * 100) / 100);
        addedOrders.setTotalPayment((double) Math.round(
                (addedOrders.getTotalPrice() - addedOrders.getVoucher() + addedOrders.getShipCost()) * 100) / 100);

        // save again saved orders
        ordersRepository.save(addedOrders);

        // update products
        cartGeneral.getCartProducts().forEach(this::updateProduct);

        // delete cart
        cartProductRepository.deleteByCartGeneral(cartGeneral);
        cartGeneralRepository.deleteById(ordersSaleDto.getCartId());

        return ResponseEntity.ok().body(addedOrders);
    }

    @Override
    public ResponseEntity<?> cancelOrder(Long id) {
        Orders orders = ordersRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Đơn hàng không tồn tại"));
        if (orders.getStatus() >= 3)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Đơn hàng hiện không thể hủy");
        orders.setStatus(5);
        ordersRepository.save(orders);
        return ResponseEntity.ok().build();
    }

    @Override
    public Orders getOrdersById(Long id) {
        return ordersRepository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @Override
    public Orders updateOrdersById(UpdateOrdersDto updateOrdersDto, Long id) {
        Orders updateOrders = ordersRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        updateOrders.setName(updateOrdersDto.getName());
        updateOrders.setPhone(updateOrdersDto.getPhone());
        updateOrders.setAddress(updateOrdersDto.getAddress());
        updateOrders.setNote(updateOrdersDto.getNote());
        if (updateOrdersDto.getStatus() < updateOrders.getStatus())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Trạng thái cập nhật không hợp lệ");
        updateOrders.setStatus(updateOrdersDto.getStatus() > updateOrders.getStatus()
                ? updateOrdersDto.getStatus() : updateOrders.getStatus());
        if (updateOrders.getStatus() >= 1)
            updateOrders.setConfirmedAt(new Date());
        // Delete if order is cancelled
        if (updateOrders.getStatus() == 5) {
            updateOrders.getListOrderDetail().forEach(orderDetail -> {
                // Cập nhật lại số lượng sản phẩm cho đơn hàng bị hủy
                List<StatsProduct> statsProducts = statsProductRepository.findByOrderDetail_Id(orderDetail.getId());
                statsProducts.forEach(statsProduct -> {
                    ProductDetail productDetail = statsProduct.getProductDetail();
                    productDetail.setAvailAmount(productDetail.getAvailAmount() + statsProduct.getQuantity());
                    productDetailRepository.save(productDetail);
                });
                // Xoá các bản thống kê của đơn hàng bị hủy
                statsProductRepository.deleteAll(statsProducts);
            });
        }
        updateOrders.setPaymentStatus(updateOrdersDto.getPaymentStatus());
        updateOrders.setUpdatedAt(new Date());
        updateOrders.setUpdatedBy(SecurityContextHolder.getContext().getAuthentication().getName());

        return ordersRepository.save(updateOrders);
    }



    @Override
    public Orders cancelOrdersById(UpdateOrdersDto updateOrdersDto, Long id) {
        Orders updateOrders = ordersRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        updateOrders.setName(updateOrdersDto.getName());
        updateOrders.setPhone(updateOrdersDto.getPhone());
        updateOrders.setAddress(updateOrdersDto.getAddress());
        updateOrders.setNote(updateOrdersDto.getNote());
        updateOrders.setStatus(updateOrdersDto.getStatus() );

        // Delete if order is cancelled
        if (updateOrders.getStatus() == 5) {
            updateOrders.getListOrderDetail().forEach(orderDetail -> {
                // Cập nhật lại số lượng sản phẩm cho đơn hàng bị hủy
                List<StatsProduct> statsProducts = statsProductRepository.findByOrderDetail_Id(orderDetail.getId());
                statsProducts.forEach(statsProduct -> {
                    ProductDetail productDetail = statsProduct.getProductDetail();
                    productDetail.setAvailAmount(productDetail.getAvailAmount() + statsProduct.getQuantity());
                    productDetailRepository.save(productDetail);
                });
                // Xoá các bản thống kê của đơn hàng bị hủy
                statsProductRepository.deleteAll(statsProducts);
            });
        }
        updateOrders.setPaymentStatus(updateOrdersDto.getPaymentStatus());
        updateOrders.setUpdatedAt(new Date());
        updateOrders.setUpdatedBy(SecurityContextHolder.getContext().getAuthentication().getName());

        return ordersRepository.save(updateOrders);
    }


    @Override
    public List<Orders> getSaleOrdersByEmail(String email) {
        return ordersRepository.findByEmail(email);
    }

    @Transactional
    public void updateProduct(CartProduct cartItem) {
        ProductDetail productDetail = productDetailRepository
                .findByProduct_IdAndColor_IdAndSize_Id(cartItem.getProductDetail().getProduct().getId(),
                        cartItem.getProductDetail().getColor().getId(),
                        cartItem.getProductDetail().getSize().getId());

        productDetail.setAvailAmount(productDetail.getAvailAmount() - cartItem.getQuantity());
        productDetail.setStatus(productDetail.getAvailAmount() > 0 ? 1 : 0);
        productDetailRepository.save(productDetail);

        Product product = productDetail.getProduct();
        product.setTotalAmount(productDetailRepository.
                sumAvailAmountByProductId(product.getId()));
        product.setStatus(product.getTotalAmount() > 0 ? 1 : 0);
        productRepository.save(product);
    }

    private ResponseEntity<?> checkValidCart(CartGeneral cartGeneral) {
        Map validateCart = new HashMap<>();
        // check whether product detail exists or not based on id
        List<CartProduct> invalidProductList = cartGeneral.getCartProducts()
                .stream().filter(cartProduct -> productDetailRepository.findById(cartProduct.getProductDetail().getId()).isEmpty())
                .collect(Collectors.toList());
        if (!invalidProductList.isEmpty()) {
            validateCart.put("message", "Trong giỏ hàng có chứa sản phẩm không tồn tại, " +
                    "vui lòng loại bỏ sản phẩm để hoàn thành thanh toán");
            validateCart.put("list", invalidProductList);
            return ResponseEntity.badRequest().body(validateCart);
        }
        // check whether product is available or not
        invalidProductList = cartGeneral.getCartProducts()
                .stream().filter(cartProduct -> cartProduct.getProductDetail().getProduct().getAvailable() != 1)
                .collect(Collectors.toList());
        if (!invalidProductList.isEmpty()) {
            validateCart.put("message", "Trong giỏ hàng có chứa sản phẩm không tồn tại, " +
                    "vui lòng loại bỏ sản phẩm để hoàn thành thanh toán");
            validateCart.put("list", invalidProductList);
            return ResponseEntity.badRequest().body(validateCart);
        }
        // check whether product has status == 1 or not
        invalidProductList = cartGeneral.getCartProducts()
                .stream().filter(cartProduct -> cartProduct.getProductDetail().getProduct().getStatus() != 1)
                .collect(Collectors.toList());
        if (!invalidProductList.isEmpty()) {
            validateCart.put("message", "Trong giỏ hàng có chứa sản phẩm hết hàng hoặc đã ngừng kinh doanh, " +
                    "vui lòng loại bỏ sản phẩm để hoàn thành thanh toán");
            validateCart.put("list", invalidProductList);
            return ResponseEntity.badRequest().body(validateCart);
        }
        // check whether product detail has status == 1 or not
        invalidProductList = cartGeneral.getCartProducts()
                .stream().filter(cartProduct -> cartProduct.getProductDetail().getStatus() != 1)
                .collect(Collectors.toList());
        if (!invalidProductList.isEmpty()) {
            validateCart.put("message", "Trong giỏ hàng có chứa sản phẩm hết hàng hoặc đã ngừng kinh doanh, " +
                    "vui lòng loại bỏ sản phẩm để hoàn thành thanh toán");
            validateCart.put("list", invalidProductList);
            return ResponseEntity.badRequest().body(validateCart);
        }
        // check whether product detail is in stock or not
        invalidProductList = cartGeneral.getCartProducts()
                .stream().filter(cartProduct -> cartProduct.getProductDetail().getAvailAmount() <= 0)
                .collect(Collectors.toList());
        if (!invalidProductList.isEmpty()) {
            validateCart.put("message", "Trong giỏ hàng có chứa sản phẩm hết hàng hoặc đã ngừng kinh doanh, " +
                    "vui lòng loại bỏ sản phẩm để hoàn thành thanh toán");
            validateCart.put("list", invalidProductList);
            return ResponseEntity.badRequest().body(validateCart);
        }
        // check whether the amount of product detail meets user's need
        invalidProductList = cartGeneral.getCartProducts()
                .stream().filter(cartProduct -> cartProduct.getProductDetail().getAvailAmount() < cartProduct.getQuantity())
                .collect(Collectors.toList());
        if (!invalidProductList.isEmpty()) {
            validateCart.put("message", "Trong giỏ hàng có chứa sản phẩm không còn đủ số lượng tồn mà bạn thêm vào giỏ hàng, " +
                    "vui lòng loại bỏ sản phẩm để hoàn thành thanh toán");
            validateCart.put("list", invalidProductList);
            return ResponseEntity.badRequest().body(validateCart);
        }
        return null;
    }
}

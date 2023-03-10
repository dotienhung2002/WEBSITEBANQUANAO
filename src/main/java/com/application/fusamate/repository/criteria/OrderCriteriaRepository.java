package com.application.fusamate.repository.criteria;

import com.application.fusamate.entity.Orders;
import com.application.fusamate.model.OrderSearchCriteriaModel;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Repository
public class OrderCriteriaRepository {

    private final EntityManager entityManager;

    private final CriteriaBuilder criteriaBuilder;

    public OrderCriteriaRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
        this.criteriaBuilder = entityManager.getCriteriaBuilder();
    }

    public Page<Orders> findAllWithFilters(OrderSearchCriteriaModel orderSearchCriteriaModel) {
        CriteriaQuery<Orders> criteriaQuery = criteriaBuilder.createQuery(Orders.class);
        Root<Orders> ordersRoot = criteriaQuery.from(Orders.class);

        Predicate predicate = getPredicate(orderSearchCriteriaModel, ordersRoot);
        criteriaQuery.where(predicate);

        setOrder(orderSearchCriteriaModel, criteriaQuery, ordersRoot);

        TypedQuery<Orders> typedQuery = entityManager.createQuery(criteriaQuery);
        typedQuery.setFirstResult(orderSearchCriteriaModel.getPageNumber() * orderSearchCriteriaModel.getPageSize());
        typedQuery.setMaxResults(orderSearchCriteriaModel.getPageSize());

        Pageable pageable = getPageable(orderSearchCriteriaModel);

        long orderCount = getOrderCount(predicate);

        return new PageImpl<>(typedQuery.getResultList(), pageable, orderCount);
    }

    private Predicate getPredicate(OrderSearchCriteriaModel orderSearchCriteriaModel,
                                   Root<Orders> ordersRoot) {
        List<Predicate> predicates = new ArrayList<>();

        if (Objects.nonNull(orderSearchCriteriaModel.getStatus()))
            predicates.add(criteriaBuilder.equal(ordersRoot.get("status"), orderSearchCriteriaModel.getStatus()));

        if (Objects.nonNull(orderSearchCriteriaModel.getPaymentStatus()))
            predicates.add(criteriaBuilder.equal(ordersRoot.get("paymentStatus"), orderSearchCriteriaModel.getPaymentStatus()));

        if (Objects.nonNull(orderSearchCriteriaModel.getName()))
            predicates.add(criteriaBuilder.like(ordersRoot.get("name"),
                    "%" + orderSearchCriteriaModel.getName() + "%"));

        if (Objects.nonNull(orderSearchCriteriaModel.getPhone()))
            predicates.add(criteriaBuilder.like(ordersRoot.get("phone"),
                    "%" + orderSearchCriteriaModel.getName() + "%"));

        if (Objects.nonNull(orderSearchCriteriaModel.getEmail()))
            predicates.add(criteriaBuilder.like(ordersRoot.get("email"),
                    "%" + orderSearchCriteriaModel.getName() + "%"));

        if (Objects.nonNull(orderSearchCriteriaModel.getCreatedAt()))
            predicates.add(criteriaBuilder.equal(ordersRoot.get("createdAt"),
                    orderSearchCriteriaModel.getCreatedAt()));

        if (Objects.nonNull(orderSearchCriteriaModel.getUpdatedAt()))
            predicates.add(criteriaBuilder.equal(ordersRoot.get("updatedAt"),
                    orderSearchCriteriaModel.getUpdatedAt()));

        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    }

    private void setOrder(OrderSearchCriteriaModel orderSearchCriteriaModel,
                          CriteriaQuery<Orders> criteriaQuery,
                          Root<Orders> ordersRoot) {
        if (Objects.nonNull(orderSearchCriteriaModel.getSortDirection())) {
            if (orderSearchCriteriaModel.getSortDirection().equals(Sort.Direction.ASC))
                criteriaQuery.orderBy(criteriaBuilder.asc(
                        ordersRoot.get(orderSearchCriteriaModel.getSortBy())));
            else
                criteriaQuery.orderBy(criteriaBuilder.desc(
                        ordersRoot.get(orderSearchCriteriaModel.getSortBy())));
        } else
            criteriaQuery.orderBy(criteriaBuilder.desc(ordersRoot.get("createdAt")));
    }

    private Pageable getPageable(OrderSearchCriteriaModel orderSearchCriteriaModel) {
        Sort sort = Sort.by(orderSearchCriteriaModel.getSortDirection(), orderSearchCriteriaModel.getSortBy());
        return PageRequest.of(orderSearchCriteriaModel.getPageNumber(), orderSearchCriteriaModel.getPageSize(), sort);
    }

    private long getOrderCount(Predicate predicate) {
        CriteriaQuery<Long> countQuery = criteriaBuilder.createQuery(Long.class);
        Root<Orders> countRoot = countQuery.from(Orders.class);
        countQuery.select(criteriaBuilder.count(countRoot)).where(predicate);
        return entityManager.createQuery(countQuery).getSingleResult();
    }
}

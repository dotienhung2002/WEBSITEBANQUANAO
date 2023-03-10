package com.application.fusamate.repository.criteria;

import com.application.fusamate.entity.ProductDetail;
import com.application.fusamate.model.ProductDetailSearchCriteriaModel;
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
public class ProductDetailCriteriaRepository {

    private final EntityManager entityManager;

    private final CriteriaBuilder criteriaBuilder;

    public ProductDetailCriteriaRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
        this.criteriaBuilder = entityManager.getCriteriaBuilder();
    }

    public Page<ProductDetail> findAllWithFilters(Long productId, ProductDetailSearchCriteriaModel productDetailSearchCriteriaModel) {
        CriteriaQuery<ProductDetail> criteriaQuery = criteriaBuilder.createQuery(ProductDetail.class);
        Root<ProductDetail> productRoot = criteriaQuery.from(ProductDetail.class);

        Predicate predicate = getPredicate(productId, productDetailSearchCriteriaModel, productRoot);
        criteriaQuery.where(predicate);

        setOrder(productDetailSearchCriteriaModel, criteriaQuery, productRoot);

        TypedQuery<ProductDetail> typedQuery = entityManager.createQuery(criteriaQuery);
        typedQuery.setFirstResult(productDetailSearchCriteriaModel.getPageNumber() * productDetailSearchCriteriaModel.getPageSize());
        typedQuery.setMaxResults(productDetailSearchCriteriaModel.getPageSize());

        Pageable pageable = getPageable(productDetailSearchCriteriaModel);

        long ProductCount = getProductDetailCount(predicate);

        return new PageImpl<>(typedQuery.getResultList(), pageable, ProductCount);
    }

    private Predicate getPredicate(Long productId, ProductDetailSearchCriteriaModel productDetailSearchCriteriaModel,
                                   Root<ProductDetail> productDetailRoot) {
        List<Predicate> predicates = new ArrayList<>();

        predicates.add(criteriaBuilder.equal(productDetailRoot.get("product").get("id"), productId));

        if (Objects.nonNull(productDetailSearchCriteriaModel.getMadeIn().getId()))
            predicates.add(criteriaBuilder.equal(productDetailRoot.get("madeIn").get("id"),
                    productDetailSearchCriteriaModel.getMadeIn().getId()));

        if (Objects.nonNull(productDetailSearchCriteriaModel.getSize().getId())) {
            predicates.add(
                    criteriaBuilder.equal(productDetailRoot.get("size").get("id"),
                            productDetailSearchCriteriaModel.getSize().getId())
            );
        }

        if (Objects.nonNull(productDetailSearchCriteriaModel.getColor().getId())) {
            predicates.add(
                    criteriaBuilder.equal(productDetailRoot.get("color").get("id"),
                            productDetailSearchCriteriaModel.getColor().getId())
            );
        }

        if (Objects.nonNull(productDetailSearchCriteriaModel.getStartPrice())
                && Objects.nonNull(productDetailSearchCriteriaModel.getEndPrice())) {
            predicates.add(
                    criteriaBuilder.between(productDetailRoot.get("originPrice"),
                            productDetailSearchCriteriaModel.getStartPrice(),
                            productDetailSearchCriteriaModel.getEndPrice())
            );
        }

        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    }

    private void setOrder(ProductDetailSearchCriteriaModel productDetailSearchCriteriaModel,
                          CriteriaQuery<ProductDetail> criteriaQuery,
                          Root<ProductDetail> productRoot) {
        if (productDetailSearchCriteriaModel.getSortDirection().equals(Sort.Direction.ASC))
            criteriaQuery.orderBy(criteriaBuilder.asc(
                    productRoot.get(productDetailSearchCriteriaModel.getSortBy())));
        else
            criteriaQuery.orderBy(criteriaBuilder.desc(
                    productRoot.get(productDetailSearchCriteriaModel.getSortBy())));
    }

    private Pageable getPageable(ProductDetailSearchCriteriaModel productDetailSearchCriteriaModel) {
        Sort sort = Sort.by(productDetailSearchCriteriaModel.getSortDirection(), productDetailSearchCriteriaModel.getSortBy());
        return PageRequest.of(productDetailSearchCriteriaModel.getPageNumber(), productDetailSearchCriteriaModel.getPageSize(), sort);
    }

    private long getProductDetailCount(Predicate predicate) {
        CriteriaQuery<Long> countQuery = criteriaBuilder.createQuery(Long.class);
        Root<ProductDetail> countRoot = countQuery.from(ProductDetail.class);
        countQuery.select(criteriaBuilder.count(countRoot)).where(predicate);
        return entityManager.createQuery(countQuery).getSingleResult();
    }
}

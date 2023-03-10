package com.application.fusamate.repository.criteria;

import com.application.fusamate.entity.Product;
import com.application.fusamate.model.ProductSearchCriteriaModel;
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
public class ProductCriteriaRepository {

    private final EntityManager entityManager;

    private final CriteriaBuilder criteriaBuilder;

    public ProductCriteriaRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
        this.criteriaBuilder = entityManager.getCriteriaBuilder();
    }

    public Page<Product> findAllWithFilters(ProductSearchCriteriaModel productSearchCriteriaModel) {
        CriteriaQuery<Product> criteriaQuery = criteriaBuilder.createQuery(Product.class);
        Root<Product> productRoot = criteriaQuery.from(Product.class);

        Predicate predicate = getPredicate(productSearchCriteriaModel, productRoot);
        criteriaQuery.where(predicate);

        setOrder(productSearchCriteriaModel, criteriaQuery, productRoot);

        TypedQuery<Product> typedQuery = entityManager.createQuery(criteriaQuery);
        typedQuery.setFirstResult(productSearchCriteriaModel.getPageNumber() * productSearchCriteriaModel.getPageSize());
        typedQuery.setMaxResults(productSearchCriteriaModel.getPageSize());

        Pageable pageable = getPageable(productSearchCriteriaModel);

        long ProductCount = getProductCount(predicate);

        return new PageImpl<>(typedQuery.getResultList(), pageable, ProductCount);
    }

    private Predicate getPredicate(ProductSearchCriteriaModel ProductSearchCriteriaModel,
                                   Root<Product> productRoot) {
        List<Predicate> predicates = new ArrayList<>();

        if (Objects.nonNull(ProductSearchCriteriaModel.getName()))
            predicates.add(criteriaBuilder.like(productRoot.get("name"),
                    "%" + ProductSearchCriteriaModel.getName() + "%"));

        if (Objects.nonNull(ProductSearchCriteriaModel.getGender())) {
            predicates.add(
                    criteriaBuilder.equal(productRoot.get("gender"),
                            ProductSearchCriteriaModel.getGender())
            );
        }

        if (Objects.nonNull(ProductSearchCriteriaModel.getBrand().getId())) {
            predicates.add(
                    criteriaBuilder.equal(productRoot.get("brand").get("id"),
                            ProductSearchCriteriaModel.getBrand().getId())
            );
        }

        if (Objects.nonNull(ProductSearchCriteriaModel.getCategory().getId())) {
            predicates.add(
                    criteriaBuilder.equal(productRoot.get("category").get("id"),
                            ProductSearchCriteriaModel.getCategory().getId())
            );
        }

        if (Objects.nonNull(ProductSearchCriteriaModel.getMadeIn().getId())) {
            predicates.add(
                    criteriaBuilder.equal(productRoot.get("madeIn").get("id"),
                            ProductSearchCriteriaModel.getMadeIn().getId())
            );
        }

        if (Objects.nonNull(ProductSearchCriteriaModel.getStatus())) {
            predicates.add(
                    criteriaBuilder.equal(productRoot.get("status"),
                            ProductSearchCriteriaModel.getStatus())
            );
        }

        if (Objects.nonNull(ProductSearchCriteriaModel.getAvailable())) {
            predicates.add(
                    criteriaBuilder.equal(productRoot.get("available"),
                            ProductSearchCriteriaModel.getAvailable())
            );
        }

        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    }

    private void setOrder(ProductSearchCriteriaModel ProductSearchCriteriaModel,
                          CriteriaQuery<Product> criteriaQuery,
                          Root<Product> productRoot) {
        if (ProductSearchCriteriaModel.getSortDirection().equals(Sort.Direction.ASC))
            criteriaQuery.orderBy(criteriaBuilder.asc(
                    productRoot.get(ProductSearchCriteriaModel.getSortBy())));
        else
            criteriaQuery.orderBy(criteriaBuilder.desc(
                    productRoot.get(ProductSearchCriteriaModel.getSortBy())));
    }

    private Pageable getPageable(ProductSearchCriteriaModel productSearchCriteriaModel) {
        Sort sort = Sort.by(productSearchCriteriaModel.getSortDirection(), productSearchCriteriaModel.getSortBy());
        return PageRequest.of(productSearchCriteriaModel.getPageNumber(), productSearchCriteriaModel.getPageSize(), sort);
    }

    private long getProductCount(Predicate predicate) {
        CriteriaQuery<Long> countQuery = criteriaBuilder.createQuery(Long.class);
        Root<Product> countRoot = countQuery.from(Product.class);
        countQuery.select(criteriaBuilder.count(countRoot)).where(predicate);
        return entityManager.createQuery(countQuery).getSingleResult();
    }
}

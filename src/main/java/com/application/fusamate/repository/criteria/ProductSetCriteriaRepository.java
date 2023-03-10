package com.application.fusamate.repository.criteria;

import com.application.fusamate.entity.ProductSet;
import com.application.fusamate.model.ProductSetSearchCriteriaModel;
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
public class ProductSetCriteriaRepository {

    private final EntityManager entityManager;

    private final CriteriaBuilder criteriaBuilder;

    public ProductSetCriteriaRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
        this.criteriaBuilder = entityManager.getCriteriaBuilder();
    }

    public Page<ProductSet> findAllWithFilters(ProductSetSearchCriteriaModel productSetSearchCriteria) {
        CriteriaQuery<ProductSet> criteriaQuery = criteriaBuilder.createQuery(ProductSet.class);
        Root<ProductSet> productSetRoot = criteriaQuery.from(ProductSet.class);

        Predicate predicate = getPredicate(productSetSearchCriteria, productSetRoot);
        criteriaQuery.where(predicate);

        setOrder(productSetSearchCriteria, criteriaQuery, productSetRoot);

        TypedQuery<ProductSet> typedQuery = entityManager.createQuery(criteriaQuery);
        typedQuery.setFirstResult(productSetSearchCriteria.getPageNumber() * productSetSearchCriteria.getPageSize());
        typedQuery.setMaxResults(productSetSearchCriteria.getPageSize());

        Pageable pageable = getPageable(productSetSearchCriteria);

        long productSetCount = getProductSetCount(predicate);

        return new PageImpl<>(typedQuery.getResultList(), pageable, productSetCount);
    }

    private Predicate getPredicate(ProductSetSearchCriteriaModel productSetSearchCriteria,
                                   Root<ProductSet> productSetRoot) {
        List<Predicate> predicates = new ArrayList<>();

        if (Objects.nonNull(productSetSearchCriteria.getName()))
            predicates.add(criteriaBuilder.like(productSetRoot.get("name"),
                    "%" + productSetSearchCriteria.getName() + "%"));

        if(Objects.nonNull(productSetSearchCriteria.getStatus())){
            predicates.add(
                    criteriaBuilder.equal(productSetRoot.get("status"),
                            productSetSearchCriteria.getStatus())
            );
        }

        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    }

    private void setOrder(ProductSetSearchCriteriaModel productSetSearchCriteria,
                          CriteriaQuery<ProductSet> criteriaQuery,
                          Root<ProductSet> productSetRoot) {
        if (productSetSearchCriteria.getSortDirection().equals(Sort.Direction.ASC))
            criteriaQuery.orderBy(criteriaBuilder.asc(
                    productSetRoot.get(productSetSearchCriteria.getSortBy())));
        else
            criteriaQuery.orderBy(criteriaBuilder.desc(
                    productSetRoot.get(productSetSearchCriteria.getSortBy())));
    }

    private Pageable getPageable(ProductSetSearchCriteriaModel productSetSearchCriteria) {
        Sort sort = Sort.by(productSetSearchCriteria.getSortDirection(), productSetSearchCriteria.getSortBy());
        return PageRequest.of(productSetSearchCriteria.getPageNumber(), productSetSearchCriteria.getPageSize(), sort);
    }

    private long getProductSetCount(Predicate predicate) {
        CriteriaQuery<Long> countQuery = criteriaBuilder.createQuery(Long.class);
        Root<ProductSet> countRoot = countQuery.from(ProductSet.class);
        countQuery.select(criteriaBuilder.count(countRoot)).where(predicate);
        return entityManager.createQuery(countQuery).getSingleResult();
    }
}

package com.application.fusamate.repository.criteria;

import com.application.fusamate.entity.PromotionProduct;
import com.application.fusamate.model.PromotionProductSearchCriteriaModel;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
public class PromotionProductCriteriaRepository {
    private final EntityManager entityManager;
    private final CriteriaBuilder criteriaBuilder;

    public PromotionProductCriteriaRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
        this.criteriaBuilder = entityManager.getCriteriaBuilder();
    }

    public PageImpl<PromotionProduct> findAllWithFilters(PromotionProductSearchCriteriaModel promotionProductSearchCriteriaModel) {
        CriteriaQuery<PromotionProduct> criteriaQuery = criteriaBuilder.createQuery(PromotionProduct.class);
        Root<PromotionProduct> promotionProductRoot = criteriaQuery.from(PromotionProduct.class);
        Predicate predicate = getPredicate(promotionProductSearchCriteriaModel, promotionProductRoot);
        criteriaQuery.where(predicate);
        setOrder(promotionProductSearchCriteriaModel, criteriaQuery, promotionProductRoot);
        TypedQuery<PromotionProduct> typedQuery = entityManager.createQuery(criteriaQuery);
        typedQuery.setFirstResult(promotionProductSearchCriteriaModel.getPageNumber() * promotionProductSearchCriteriaModel.getPageSize());
        typedQuery.setMaxResults(promotionProductSearchCriteriaModel.getPageSize());
        Pageable pageable = getPageable(promotionProductSearchCriteriaModel);
        long promotionProductsCount = getPromotionProductsCount(predicate);
        return new PageImpl<>(typedQuery.getResultList(), pageable, promotionProductsCount);
    }

    private Predicate getPredicate(PromotionProductSearchCriteriaModel promotionProductSearchCriteriaModel,
                                   Root<PromotionProduct> promotionProductRoot) {
        List<Predicate> predicates = new ArrayList<>();
        if (Objects.nonNull(promotionProductSearchCriteriaModel.getName())) {
            predicates.add(criteriaBuilder.like(promotionProductRoot.get("name"),
                    "%" + promotionProductSearchCriteriaModel.getName() + "%"));
        }

        if (Objects.nonNull(promotionProductSearchCriteriaModel.getPercentageRange())) {
            switch (promotionProductSearchCriteriaModel.getPercentageRange()) {
                case 1:
                    predicates.add(criteriaBuilder.lessThan(promotionProductRoot.get("percentage"),10));
                    break;
                case 2:
                    predicates.add(criteriaBuilder.greaterThanOrEqualTo(promotionProductRoot.get("percentage"),10));
                    predicates.add(criteriaBuilder.lessThan(promotionProductRoot.get("percentage"),30));
                    break;
                case 3:
                    predicates.add(criteriaBuilder.greaterThanOrEqualTo(promotionProductRoot.get("percentage"),30));
                    predicates.add(criteriaBuilder.lessThan(promotionProductRoot.get("percentage"),50));
                    break;
                default:
                    predicates.add(criteriaBuilder.greaterThan(promotionProductRoot.get("percentage"),50));
            }
        }

        if(Objects.nonNull(promotionProductSearchCriteriaModel.getStatus())){
            predicates.add(
                    criteriaBuilder.equal(promotionProductRoot.get("status"),
                            promotionProductSearchCriteriaModel.getStatus())
            );
        }

        if (Objects.nonNull(promotionProductSearchCriteriaModel.getProduct().getName())) {
            predicates.add(criteriaBuilder.like(promotionProductRoot.get("product").get("name"),
                    "%" + promotionProductSearchCriteriaModel.getProduct().getName() + "%"));
        }

        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    }

    private void setOrder(PromotionProductSearchCriteriaModel promotionProductSearchCriteriaModel,
                          CriteriaQuery<PromotionProduct> criteriaQuery,
                          Root<PromotionProduct> promotionProductRoot) {
        if (promotionProductSearchCriteriaModel.getSortDirection().equals(Sort.Direction.ASC)) {
            criteriaQuery.orderBy(criteriaBuilder.asc(promotionProductRoot.get(promotionProductSearchCriteriaModel.getSortBy())));
        } else {
            criteriaQuery.orderBy(criteriaBuilder.desc(promotionProductRoot.get(promotionProductSearchCriteriaModel.getSortBy())));
        }
    }

    private Pageable getPageable(PromotionProductSearchCriteriaModel promotionProductSearchCriteriaModel) {
        Sort sort = Sort.by(promotionProductSearchCriteriaModel.getSortDirection(), promotionProductSearchCriteriaModel.getSortBy());
        return PageRequest.of(promotionProductSearchCriteriaModel.getPageNumber(), promotionProductSearchCriteriaModel.getPageSize(), sort);
    }

    private long getPromotionProductsCount(Predicate predicate) {
        CriteriaQuery<Long> countQuery = criteriaBuilder.createQuery(Long.class);
        Root<PromotionProduct> countRoot = countQuery.from(PromotionProduct.class);
        countQuery.select(criteriaBuilder.count(countRoot)).where(predicate);
        return entityManager.createQuery(countQuery).getSingleResult();
    }
}

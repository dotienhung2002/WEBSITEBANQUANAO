package com.application.fusamate.repository.criteria;

import com.application.fusamate.entity.PromotionCategory;
import com.application.fusamate.model.PromotionCategorySearchCriteriaModel;
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
public class PromotionCategoryCriteriaRepository {
    private final EntityManager entityManager;
    private final CriteriaBuilder criteriaBuilder;

    public PromotionCategoryCriteriaRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
        this.criteriaBuilder = entityManager.getCriteriaBuilder();
    }

    public PageImpl<PromotionCategory> findAllWithFilters(PromotionCategorySearchCriteriaModel promotionCategorySearchCriteriaModel) {
        CriteriaQuery<PromotionCategory> criteriaQuery = criteriaBuilder.createQuery(PromotionCategory.class);
        Root<PromotionCategory> promotionCategoryRoot = criteriaQuery.from(PromotionCategory.class);
        Predicate predicate = getPredicate(promotionCategorySearchCriteriaModel, promotionCategoryRoot);
        criteriaQuery.where(predicate);
        setOrder(promotionCategorySearchCriteriaModel, criteriaQuery, promotionCategoryRoot);
        TypedQuery<PromotionCategory> typedQuery = entityManager.createQuery(criteriaQuery);
        typedQuery.setFirstResult(promotionCategorySearchCriteriaModel.getPageNumber() * promotionCategorySearchCriteriaModel.getPageSize());
        typedQuery.setMaxResults(promotionCategorySearchCriteriaModel.getPageSize());
        Pageable pageable = getPageable(promotionCategorySearchCriteriaModel);
        long promotionCategoriesCount = getPromotionCategoriesCount(predicate);
        return new PageImpl<>(typedQuery.getResultList(), pageable, promotionCategoriesCount);

    }

    private Predicate getPredicate(PromotionCategorySearchCriteriaModel promotionCategorySearchCriteriaModel,
                                   Root<PromotionCategory> promotionCategoryRoot) {
        List<Predicate> predicates = new ArrayList<>();
        
        if (Objects.nonNull(promotionCategorySearchCriteriaModel.getName())) {
            predicates.add(criteriaBuilder.like(promotionCategoryRoot.get("name"),
                    "%" + promotionCategorySearchCriteriaModel.getName() + "%"));
        }

        if (Objects.nonNull(promotionCategorySearchCriteriaModel.getPercentageRange())) {
            switch (promotionCategorySearchCriteriaModel.getPercentageRange()) {
                case 1:
                    predicates.add(criteriaBuilder.lessThan(promotionCategoryRoot.get("percentage"),10));
                    break;
                case 2:
                    predicates.add(criteriaBuilder.greaterThanOrEqualTo(promotionCategoryRoot.get("percentage"),10));
                    predicates.add(criteriaBuilder.lessThan(promotionCategoryRoot.get("percentage"),30));
                    break;
                case 3:
                    predicates.add(criteriaBuilder.greaterThanOrEqualTo(promotionCategoryRoot.get("percentage"),30));
                    predicates.add(criteriaBuilder.lessThan(promotionCategoryRoot.get("percentage"),50));
                    break;
                default:
                    predicates.add(criteriaBuilder.greaterThan(promotionCategoryRoot.get("percentage"),50));
            }
        }

        if(Objects.nonNull(promotionCategorySearchCriteriaModel.getStatus())){
            predicates.add(
                    criteriaBuilder.equal(promotionCategoryRoot.get("status"),
                            promotionCategorySearchCriteriaModel.getStatus())
            );
        }

        if (Objects.nonNull(promotionCategorySearchCriteriaModel.getCategory().getName())) {
            predicates.add(criteriaBuilder.like(promotionCategoryRoot.get("category").get("name"),
                    "%" + promotionCategorySearchCriteriaModel.getCategory().getName() + "%"));
        }

        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    }

    private void setOrder(PromotionCategorySearchCriteriaModel promotionCategorySearchCriteriaModel,
                          CriteriaQuery<PromotionCategory> criteriaQuery,
                          Root<PromotionCategory> promotionCategoryRoot) {
        if (promotionCategorySearchCriteriaModel.getSortDirection().equals(Sort.Direction.ASC)) {
            criteriaQuery.orderBy(criteriaBuilder.asc(promotionCategoryRoot.get(promotionCategorySearchCriteriaModel.getSortBy())));
        } else {
            criteriaQuery.orderBy(criteriaBuilder.desc(promotionCategoryRoot.get(promotionCategorySearchCriteriaModel.getSortBy())));
        }
    }

    private Pageable getPageable(PromotionCategorySearchCriteriaModel promotionCategorySearchCriteriaModel) {
        Sort sort = Sort.by(promotionCategorySearchCriteriaModel.getSortDirection(), promotionCategorySearchCriteriaModel.getSortBy());
        return PageRequest.of(promotionCategorySearchCriteriaModel.getPageNumber(), promotionCategorySearchCriteriaModel.getPageSize(), sort);
    }

    private long getPromotionCategoriesCount(Predicate predicate) {
        CriteriaQuery<Long> countQuery = criteriaBuilder.createQuery(Long.class);
        Root<PromotionCategory> countRoot = countQuery.from(PromotionCategory.class);
        countQuery.select(criteriaBuilder.count(countRoot)).where(predicate);
        return entityManager.createQuery(countQuery).getSingleResult();
    }
}

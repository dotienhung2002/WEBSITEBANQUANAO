package com.application.fusamate.repository.criteria;

import com.application.fusamate.entity.Category;
import com.application.fusamate.model.CategorySearchCriteriaModel;
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
public class CategoryCriteriaRepository {

    private final EntityManager entityManager;

    private final CriteriaBuilder criteriaBuilder;

    public CategoryCriteriaRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
        this.criteriaBuilder = entityManager.getCriteriaBuilder();
    }

    public Page<Category> findAllWithFilters(CategorySearchCriteriaModel categorySearchCriteriaModel) {
        CriteriaQuery<Category> criteriaQuery = criteriaBuilder.createQuery(Category.class);
        Root<Category> categoryRoot = criteriaQuery.from(Category.class);

        Predicate predicate = getPredicate(categorySearchCriteriaModel, categoryRoot);
        criteriaQuery.where(predicate);

        setOrder(categorySearchCriteriaModel, criteriaQuery, categoryRoot);

        TypedQuery<Category> typedQuery = entityManager.createQuery(criteriaQuery);
        typedQuery.setFirstResult(categorySearchCriteriaModel.getPageNumber() * categorySearchCriteriaModel.getPageSize());
        typedQuery.setMaxResults(categorySearchCriteriaModel.getPageSize());

        Pageable pageable = getPageable(categorySearchCriteriaModel);

        long categoryCount = getCategoryCount(predicate);

        return new PageImpl<>(typedQuery.getResultList(), pageable, categoryCount);
    }


    private Predicate getPredicate(CategorySearchCriteriaModel categorySearchCriteriaModel,
                                   Root<Category> categoryRoot) {
        List<Predicate> predicates = new ArrayList<>();

        if (Objects.nonNull(categorySearchCriteriaModel.getName()))
            predicates.add(criteriaBuilder.like(categoryRoot.get("name"),
                    "%" + categorySearchCriteriaModel.getName() + "%"));

//        if (categorySearchCriteriaModel.getStatus() >= 0) {
//            predicates.add(criteriaBuilder.equal(categoryRoot.get("status"),
//                    categorySearchCriteriaModel.getStatus()));
//        }
        if(Objects.nonNull(categorySearchCriteriaModel.getStatus())){
            predicates.add(
                    criteriaBuilder.equal(categoryRoot.get("status"),
                            categorySearchCriteriaModel.getStatus())
            );
        }




        if (Objects.nonNull(categorySearchCriteriaModel.getProductSet().getId() )) {
            predicates.add(criteriaBuilder.equal(categoryRoot.get("productSet").get("id"),
                    categorySearchCriteriaModel.getProductSet().getId()));
        }

        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    }

    private void setOrder(CategorySearchCriteriaModel categorySearchCriteriaModel,
                          CriteriaQuery<Category> criteriaQuery,
                          Root<Category> categoryRoot) {
        if (categorySearchCriteriaModel.getSortDirection().equals(Sort.Direction.ASC))
            criteriaQuery.orderBy(criteriaBuilder.asc(
                    categoryRoot.get(categorySearchCriteriaModel.getSortBy())));
        else
            criteriaQuery.orderBy(criteriaBuilder.desc(
                    categoryRoot.get(categorySearchCriteriaModel.getSortBy())));
    }

    private Pageable getPageable(CategorySearchCriteriaModel categorySearchCriteriaModel) {
        Sort sort = Sort.by(categorySearchCriteriaModel.getSortDirection(), categorySearchCriteriaModel.getSortBy());
        return PageRequest.of(categorySearchCriteriaModel.getPageNumber(), categorySearchCriteriaModel.getPageSize(), sort);
    }

    private long getCategoryCount(Predicate predicate) {
        CriteriaQuery<Long> countQuery = criteriaBuilder.createQuery(Long.class);
        Root<Category> countRoot = countQuery.from(Category.class);
        countQuery.select(criteriaBuilder.count(countRoot)).where(predicate);
        return entityManager.createQuery(countQuery).getSingleResult();
    }
}

package com.application.fusamate.repository.criteria;

import com.application.fusamate.entity.Customer;
import com.application.fusamate.entity.MadeIn;
import com.application.fusamate.model.CustomerSearchCriteriaModel;
import com.application.fusamate.model.MadeInSearchCriteriaModel;
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
public class MadeInCriteriaRepository {
    private final EntityManager entityManager;
    private final CriteriaBuilder criteriaBuilder;
    public MadeInCriteriaRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
        this.criteriaBuilder = entityManager.getCriteriaBuilder();
    }

    public Page<MadeIn> findAllWithFilters(MadeInSearchCriteriaModel madeInSearchCriteriaModel) {
        CriteriaQuery<MadeIn> criteriaQuery = criteriaBuilder.createQuery(MadeIn.class);
        Root<MadeIn> madeInRoot = criteriaQuery.from(MadeIn.class);
        Predicate predicate = getPredicate(madeInSearchCriteriaModel, madeInRoot);
        criteriaQuery.where(predicate);
        setOrder(madeInSearchCriteriaModel, criteriaQuery, madeInRoot);
        TypedQuery<MadeIn> typedQuery = entityManager.createQuery(criteriaQuery);
        typedQuery.setFirstResult(madeInSearchCriteriaModel.getPageNumber() * madeInSearchCriteriaModel.getPageSize());
        typedQuery.setMaxResults(madeInSearchCriteriaModel.getPageSize());
        Pageable pageable = getPageable(madeInSearchCriteriaModel);
        long madeInCount = getMadeInsCount(predicate);
        return new PageImpl<>(typedQuery.getResultList(), pageable, madeInCount);
    }

    private Predicate getPredicate(MadeInSearchCriteriaModel madeInSearchCriteriaModel,
                                   Root<MadeIn> madeInRoot) {
        List<Predicate> predicates = new ArrayList<>();
        if (Objects.nonNull(madeInSearchCriteriaModel.getName())) {
            predicates.add(criteriaBuilder.like(madeInRoot.get("name"), "%" + madeInSearchCriteriaModel.getName() + "%"));
        }
        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    }

    private void setOrder(MadeInSearchCriteriaModel madeInSearchCriteriaModel,
                          CriteriaQuery<MadeIn> criteriaQuery,
                          Root<MadeIn> madeInRoot) {
        if (madeInSearchCriteriaModel.getSortDirection().equals(Sort.Direction.ASC)) {
            criteriaQuery.orderBy(criteriaBuilder.asc(madeInRoot.get(madeInSearchCriteriaModel.getSortBy())));
        } else {
            criteriaQuery.orderBy(criteriaBuilder.desc(madeInRoot.get(madeInSearchCriteriaModel.getSortBy())));
        }
    }

    private Pageable getPageable(MadeInSearchCriteriaModel madeInSearchCriteriaModel) {
        Sort sort = Sort.by(madeInSearchCriteriaModel.getSortDirection(), madeInSearchCriteriaModel.getSortBy());
        return PageRequest.of(madeInSearchCriteriaModel.getPageNumber(), madeInSearchCriteriaModel.getPageSize(), sort);
    }

    private long getMadeInsCount(Predicate predicate) {
        CriteriaQuery<Long> countQuery = criteriaBuilder.createQuery(Long.class);
        Root<MadeIn> countMadeIn = countQuery.from(MadeIn.class);
        countQuery.select(criteriaBuilder.count(countMadeIn)).where(predicate);
        return entityManager.createQuery(countQuery).getSingleResult();
    }
}

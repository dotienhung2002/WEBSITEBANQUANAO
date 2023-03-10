package com.application.fusamate.repository.criteria;

import com.application.fusamate.entity.Size;
import com.application.fusamate.model.SizeSearchCriteriaModel;
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
public class SizeCriteriaRepository {
    private final EntityManager entityManager;
    private final CriteriaBuilder criteriaBuilder;
    public SizeCriteriaRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
        this.criteriaBuilder = entityManager.getCriteriaBuilder();
    }

    public Page<Size> findAllWithFilters(SizeSearchCriteriaModel sizeSearchCriteriaModel){
        CriteriaQuery<Size> criteriaQuery = criteriaBuilder.createQuery(Size.class);
        Root<Size> sizeRoot = criteriaQuery.from(Size.class);
        Predicate predicate = getPredicate(sizeSearchCriteriaModel, sizeRoot);
        criteriaQuery.where(predicate);
        setOrder(sizeSearchCriteriaModel, criteriaQuery, sizeRoot);
        TypedQuery<Size> typedQuery = entityManager.createQuery(criteriaQuery);
        typedQuery.setFirstResult(sizeSearchCriteriaModel.getPageNumber() * sizeSearchCriteriaModel.getPageSize());
        typedQuery.setMaxResults(sizeSearchCriteriaModel.getPageSize());
        Pageable pageable = getPageable(sizeSearchCriteriaModel);
        long sizesCount = getSizesCount(predicate);
        return new PageImpl<>(typedQuery.getResultList(), pageable, sizesCount);
    }
    private Predicate getPredicate(SizeSearchCriteriaModel sizeSearchCriteriaModel,
                                   Root<Size> sizeRoot) {
        List<Predicate> predicates = new ArrayList<>();
        if(Objects.nonNull(sizeSearchCriteriaModel.getName())){
            predicates.add(criteriaBuilder.like(sizeRoot.get("name"), "%" + sizeSearchCriteriaModel.getName() + "%"));
        }


        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    }

    private void setOrder(SizeSearchCriteriaModel sizeSearchCriteriaModel,
                          CriteriaQuery<Size>sizeCriteriaQuery,
                          Root<Size> sizeRoot) {
        if(sizeSearchCriteriaModel.getSortDirection().equals(Sort.Direction.ASC)){
            sizeCriteriaQuery.orderBy(criteriaBuilder.asc(sizeRoot.get(sizeSearchCriteriaModel.getSortBy())));
        } else {
            sizeCriteriaQuery.orderBy(criteriaBuilder.desc(sizeRoot.get(sizeSearchCriteriaModel.getSortBy())));
        }
    }

    private Pageable getPageable(SizeSearchCriteriaModel sizeSearchCriteriaModel) {
        Sort sort = Sort.by(sizeSearchCriteriaModel.getSortDirection(), sizeSearchCriteriaModel.getSortBy());
        return PageRequest.of(sizeSearchCriteriaModel.getPageNumber(),sizeSearchCriteriaModel.getPageSize(), sort);
    }

    private long getSizesCount(Predicate predicate) {
        CriteriaQuery<Long> countQuery = criteriaBuilder.createQuery(Long.class);
        Root<Size> countRoot = countQuery.from(Size.class);
        countQuery.select(criteriaBuilder.count(countRoot)).where(predicate);
        return entityManager.createQuery(countQuery).getSingleResult();
    }
}

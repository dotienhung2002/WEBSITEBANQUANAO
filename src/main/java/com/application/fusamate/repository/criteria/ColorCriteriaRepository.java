package com.application.fusamate.repository.criteria;

import com.application.fusamate.entity.Color;
import com.application.fusamate.model.ColorSearchCriteriaModel;
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
public class ColorCriteriaRepository {
    private final EntityManager entityManager;
    private final CriteriaBuilder criteriaBuilder;
    public ColorCriteriaRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
        this.criteriaBuilder = entityManager.getCriteriaBuilder();
    }

    public Page<Color> findAllWithFilters(ColorSearchCriteriaModel colorSearchCriteriaModel){
        CriteriaQuery<Color> criteriaQuery = criteriaBuilder.createQuery(Color.class);
        Root<Color> colorRoot = criteriaQuery.from(Color.class);
        Predicate predicate = getPredicate(colorSearchCriteriaModel, colorRoot);
        criteriaQuery.where(predicate);
        setOrder(colorSearchCriteriaModel, criteriaQuery, colorRoot);
        TypedQuery<Color> typedQuery = entityManager.createQuery(criteriaQuery);
        typedQuery.setFirstResult(colorSearchCriteriaModel.getPageNumber() * colorSearchCriteriaModel.getPageSize());
        typedQuery.setMaxResults(colorSearchCriteriaModel.getPageSize());
        Pageable pageable = getPageable(colorSearchCriteriaModel);
        long colorsCount = getColorsCount(predicate);
        return new PageImpl<>(typedQuery.getResultList(), pageable, colorsCount);
    }
    private Predicate getPredicate(ColorSearchCriteriaModel colorSearchCriteriaModel,
                                   Root<Color> colorRoot) {
        List<Predicate> predicates = new ArrayList<>();
        if(Objects.nonNull(colorSearchCriteriaModel.getName())){
            predicates.add(criteriaBuilder.like(colorRoot.get("name"), "%" + colorSearchCriteriaModel.getName() + "%"));
        }


        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    }

    private void setOrder(ColorSearchCriteriaModel colorSearchCriteriaModel,
                          CriteriaQuery<Color> colorCriteriaQuery,
                          Root<Color> colorRoot) {
        if(colorSearchCriteriaModel.getSortDirection().equals(Sort.Direction.ASC)){
            colorCriteriaQuery.orderBy(criteriaBuilder.asc(colorRoot.get(colorSearchCriteriaModel.getSortBy())));
        } else {
            colorCriteriaQuery.orderBy(criteriaBuilder.desc(colorRoot.get(colorSearchCriteriaModel.getSortBy())));
        }
    }

    private Pageable getPageable(ColorSearchCriteriaModel colorSearchCriteriaModel) {
        Sort sort = Sort.by(colorSearchCriteriaModel.getSortDirection(), colorSearchCriteriaModel.getSortBy());
        return PageRequest.of(colorSearchCriteriaModel.getPageNumber(),colorSearchCriteriaModel.getPageSize(), sort);
    }

    private long getColorsCount(Predicate predicate) {
        CriteriaQuery<Long> countQuery = criteriaBuilder.createQuery(Long.class);
        Root<Color> countRoot = countQuery.from(Color.class);
        countQuery.select(criteriaBuilder.count(countRoot)).where(predicate);
        return entityManager.createQuery(countQuery).getSingleResult();
    }
}

package com.application.fusamate.repository.criteria;

import com.application.fusamate.entity.Brand;
import com.application.fusamate.model.BrandSearchCriteriaModel;
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
public class BrandCriteriaRepository {

    private final EntityManager entityManager;

    private final CriteriaBuilder criteriaBuilder;

    public BrandCriteriaRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
        this.criteriaBuilder = entityManager.getCriteriaBuilder();
    }

    public Page<Brand> findAllWithFilters(BrandSearchCriteriaModel brandSearchCriteriaModel) {
        CriteriaQuery<Brand> criteriaQuery = criteriaBuilder.createQuery(Brand.class);
        Root<Brand> brandRoot = criteriaQuery.from(Brand.class);

        Predicate predicate = getPredicate(brandSearchCriteriaModel, brandRoot);
        criteriaQuery.where(predicate);

        setOrder(brandSearchCriteriaModel, criteriaQuery, brandRoot);

        TypedQuery<Brand> typedQuery = entityManager.createQuery(criteriaQuery);
        typedQuery.setFirstResult(brandSearchCriteriaModel.getPageNumber() * brandSearchCriteriaModel.getPageSize());
        typedQuery.setMaxResults(brandSearchCriteriaModel.getPageSize());

        Pageable pageable = getPageable(brandSearchCriteriaModel);

        long brandCount = getBrandCount(predicate);

        return new PageImpl<>(typedQuery.getResultList(), pageable, brandCount);
    }

    private Predicate getPredicate(BrandSearchCriteriaModel brandSearchCriteriaModel,
                                   Root<Brand> brandRoot) {
        List<Predicate> predicates = new ArrayList<>();

        if (Objects.nonNull(brandSearchCriteriaModel.getName()))
            predicates.add(criteriaBuilder.like(brandRoot.get("name"),
                    "%" + brandSearchCriteriaModel.getName() + "%"));

//        if (brandSearchCriteriaModel.getStatus() >= 0) {
//            predicates.add(
//                    criteriaBuilder.equal(brandRoot.get("status"),
//                            brandSearchCriteriaModel.getStatus())
//            );
//        }

        if(Objects.nonNull(brandSearchCriteriaModel.getStatus())){
            predicates.add(
                    criteriaBuilder.equal(brandRoot.get("status"),
                            brandSearchCriteriaModel.getStatus())
            );
        }

        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    }

    private void setOrder(BrandSearchCriteriaModel brandSearchCriteriaModel,
                          CriteriaQuery<Brand> criteriaQuery,
                          Root<Brand> brandRoot) {
        if (brandSearchCriteriaModel.getSortDirection().equals(Sort.Direction.ASC))
            criteriaQuery.orderBy(criteriaBuilder.asc(
                    brandRoot.get(brandSearchCriteriaModel.getSortBy())));
        else
            criteriaQuery.orderBy(criteriaBuilder.desc(
                    brandRoot.get(brandSearchCriteriaModel.getSortBy())));
    }

    private Pageable getPageable(BrandSearchCriteriaModel brandSearchCriteriaModel) {
        Sort sort = Sort.by(brandSearchCriteriaModel.getSortDirection(), brandSearchCriteriaModel.getSortBy());
        return PageRequest.of(brandSearchCriteriaModel.getPageNumber(), brandSearchCriteriaModel.getPageSize(), sort);
    }

    private long getBrandCount(Predicate predicate) {
        CriteriaQuery<Long> countQuery = criteriaBuilder.createQuery(Long.class);
        Root<Brand> countRoot = countQuery.from(Brand.class);
        countQuery.select(criteriaBuilder.count(countRoot)).where(predicate);
        return entityManager.createQuery(countQuery).getSingleResult();
    }
}

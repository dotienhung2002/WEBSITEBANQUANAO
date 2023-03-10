package com.application.fusamate.repository.criteria;

import com.application.fusamate.entity.Voucher;
import com.application.fusamate.model.VoucherSearchCriteriaModel;
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
public class VoucherCriteriaRepository {

    private final EntityManager entityManager;

    private final CriteriaBuilder criteriaBuilder;

    public VoucherCriteriaRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
        this.criteriaBuilder = entityManager.getCriteriaBuilder();
    }

    public Page<Voucher> findAllWithFilters(VoucherSearchCriteriaModel voucherSearchCriteriaModel) {
        CriteriaQuery<Voucher> criteriaQuery = criteriaBuilder.createQuery(Voucher.class);
        Root<Voucher> voucherRoot = criteriaQuery.from(Voucher.class);

        Predicate predicate = getPredicate(voucherSearchCriteriaModel, voucherRoot);
        criteriaQuery.where(predicate);

        setOrder(voucherSearchCriteriaModel, criteriaQuery, voucherRoot);

        TypedQuery<Voucher> typedQuery = entityManager.createQuery(criteriaQuery);
        typedQuery.setFirstResult(voucherSearchCriteriaModel.getPageNumber() * voucherSearchCriteriaModel.getPageSize());
        typedQuery.setMaxResults(voucherSearchCriteriaModel.getPageSize());

        Pageable pageable = getPageable(voucherSearchCriteriaModel);

        long voucherCount = getVoucherCount(predicate);

        return new PageImpl<>(typedQuery.getResultList(), pageable, voucherCount);
    }

    private Predicate getPredicate(VoucherSearchCriteriaModel voucherSearchCriteriaModel,
                                   Root<Voucher> voucherRoot) {
        List<Predicate> predicates = new ArrayList<>();

        if (Objects.nonNull(voucherSearchCriteriaModel.getCustomer().getEmail()))
            predicates.add(criteriaBuilder.like(voucherRoot.get("customer").get("email"),
                    "%" + voucherSearchCriteriaModel.getCustomer().getEmail() + "%"));

        if (Objects.nonNull(voucherSearchCriteriaModel.getStartDate()))
            predicates.add(criteriaBuilder.greaterThanOrEqualTo(voucherRoot.get("startDate"),
                    voucherSearchCriteriaModel.getStartDate()));

        if (Objects.nonNull(voucherSearchCriteriaModel.getEndDate()))
            predicates.add(criteriaBuilder.lessThanOrEqualTo(voucherRoot.get("endDate"),
                    voucherSearchCriteriaModel.getEndDate()));

        if (Objects.nonNull(voucherSearchCriteriaModel.getMoneyRange())) {
            switch (voucherSearchCriteriaModel.getMoneyRange()) {
                case 1:
                    predicates.add(criteriaBuilder.lessThanOrEqualTo(voucherRoot.get("money"),100000));
                    break;
                case 2:
                    predicates.add(criteriaBuilder.greaterThan(voucherRoot.get("money"),100000));
                    predicates.add(criteriaBuilder.lessThanOrEqualTo(voucherRoot.get("money"),500000));
                    break;
                case 3:
                    predicates.add(criteriaBuilder.greaterThan(voucherRoot.get("money"),500000));
                    break;
                default:
                    predicates.add(criteriaBuilder.greaterThan(voucherRoot.get("money"),0));
            }
        }

        if (Objects.nonNull(voucherSearchCriteriaModel.getActive()))
            predicates.add(criteriaBuilder.equal(voucherRoot.get("active"),
                    voucherSearchCriteriaModel.getActive()));

        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    }

    private void setOrder(VoucherSearchCriteriaModel voucherSearchCriteriaModel,
                          CriteriaQuery<Voucher> criteriaQuery,
                          Root<Voucher> voucherRoot) {
        if (voucherSearchCriteriaModel.getSortDirection().equals(Sort.Direction.ASC))
            criteriaQuery.orderBy(criteriaBuilder.asc(
                    voucherRoot.get(voucherSearchCriteriaModel.getSortBy())));
        else
            criteriaQuery.orderBy(criteriaBuilder.desc(
                    voucherRoot.get(voucherSearchCriteriaModel.getSortBy())));
    }

    private Pageable getPageable(VoucherSearchCriteriaModel voucherSearchCriteriaModel) {
        Sort sort = Sort.by(voucherSearchCriteriaModel.getSortDirection(), voucherSearchCriteriaModel.getSortBy());
        return PageRequest.of(voucherSearchCriteriaModel.getPageNumber(), voucherSearchCriteriaModel.getPageSize(), sort);
    }

    private long getVoucherCount(Predicate predicate) {
        CriteriaQuery<Long> countQuery = criteriaBuilder.createQuery(Long.class);
        Root<Voucher> countRoot = countQuery.from(Voucher.class);
        countQuery.select(criteriaBuilder.count(countRoot)).where(predicate);
        return entityManager.createQuery(countQuery).getSingleResult();
    }
}

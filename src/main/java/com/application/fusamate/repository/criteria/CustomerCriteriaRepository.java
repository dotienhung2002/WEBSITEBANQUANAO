package com.application.fusamate.repository.criteria;

import com.application.fusamate.entity.Customer;
import com.application.fusamate.model.CustomerSearchCriteriaModel;
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
public class CustomerCriteriaRepository {
    private final EntityManager entityManager;
    private final CriteriaBuilder criteriaBuilder;

    public CustomerCriteriaRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
        this.criteriaBuilder = entityManager.getCriteriaBuilder();
    }

    public Page<Customer> findAllWithFilters(CustomerSearchCriteriaModel customerSearchCriteriaModel) {
        CriteriaQuery<Customer> criteriaQuery = criteriaBuilder.createQuery(Customer.class);
        Root<Customer> employeeRoot = criteriaQuery.from(Customer.class);
        Predicate predicate = getPredicate(customerSearchCriteriaModel, employeeRoot);
        criteriaQuery.where(predicate);
        setOrder(customerSearchCriteriaModel, criteriaQuery, employeeRoot);
        TypedQuery<Customer> typedQuery = entityManager.createQuery(criteriaQuery);
        typedQuery.setFirstResult(customerSearchCriteriaModel.getPageNumber() * customerSearchCriteriaModel.getPageSize());
        typedQuery.setMaxResults(customerSearchCriteriaModel.getPageSize());
        Pageable pageable = getPageable(customerSearchCriteriaModel);
        long employeesCount = getEmployeesCount(predicate);
        return new PageImpl<>(typedQuery.getResultList(), pageable, employeesCount);
    }

    private Predicate getPredicate(CustomerSearchCriteriaModel customerSearchCriteriaModel,
                                   Root<Customer> customerRoot) {
        List<Predicate> predicates = new ArrayList<>();
        if (Objects.nonNull(customerSearchCriteriaModel.getName())) {
            predicates.add(criteriaBuilder.like(customerRoot.get("name"), "%" + customerSearchCriteriaModel.getName() + "%"));
        }
        if (Objects.nonNull(customerSearchCriteriaModel.getPhone())) {
            predicates.add(
                    criteriaBuilder.like(customerRoot.get("phone"),
                            "%" + customerSearchCriteriaModel.getPhone() + "%")
            );
        }
        if (Objects.nonNull(customerSearchCriteriaModel.getEmail())) {
            predicates.add(
                    criteriaBuilder.like(customerRoot.get("email"),
                            "%" + customerSearchCriteriaModel.getEmail() + "%")
            );
        }
//        if(customerSearchCriteriaModel.getStatus()>=0){
//            predicates.add(
//                    criteriaBuilder.equal(customerRoot.get("status"),
//                            customerSearchCriteriaModel.getStatus())
//            );
//        }

        if(Objects.nonNull(customerSearchCriteriaModel.getStatus())){
            predicates.add(
                    criteriaBuilder.equal(customerRoot.get("status"),
                            customerSearchCriteriaModel.getStatus())
            );
        }
        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    }

    private void setOrder(CustomerSearchCriteriaModel customerSearchCriteriaModel,
                          CriteriaQuery<Customer> criteriaQuery,
                          Root<Customer> employeeRoot) {
        if (customerSearchCriteriaModel.getSortDirection().equals(Sort.Direction.ASC)) {
            criteriaQuery.orderBy(criteriaBuilder.asc(employeeRoot.get(customerSearchCriteriaModel.getSortBy())));
        } else {
            criteriaQuery.orderBy(criteriaBuilder.desc(employeeRoot.get(customerSearchCriteriaModel.getSortBy())));
        }
    }

    private Pageable getPageable(CustomerSearchCriteriaModel customerSearchCriteriaModel) {
        Sort sort = Sort.by(customerSearchCriteriaModel.getSortDirection(), customerSearchCriteriaModel.getSortBy());
        return PageRequest.of(customerSearchCriteriaModel.getPageNumber(), customerSearchCriteriaModel.getPageSize(), sort);
    }

    private long getEmployeesCount(Predicate predicate) {
        CriteriaQuery<Long> countQuery = criteriaBuilder.createQuery(Long.class);
        Root<Customer> countRoot = countQuery.from(Customer.class);
        countQuery.select(criteriaBuilder.count(countRoot)).where(predicate);
        return entityManager.createQuery(countQuery).getSingleResult();
    }

}


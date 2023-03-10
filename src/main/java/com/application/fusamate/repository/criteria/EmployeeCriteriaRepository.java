package com.application.fusamate.repository.criteria;

import com.application.fusamate.dto.EmployeeDto;
import com.application.fusamate.entity.Employee;
import com.application.fusamate.model.EmployeeSearchCriteriaModel;
import org.springframework.beans.BeanUtils;
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
public class EmployeeCriteriaRepository {
    private final EntityManager entityManager;
    private final CriteriaBuilder criteriaBuilder;
    public EmployeeCriteriaRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
        this.criteriaBuilder = entityManager.getCriteriaBuilder();
    }

    public Page<Employee> findAllWithFilters(EmployeeSearchCriteriaModel employeeSearchCriteria){
        CriteriaQuery<Employee> criteriaQuery = criteriaBuilder.createQuery(Employee.class);
        Root<Employee> employeeRoot = criteriaQuery.from(Employee.class);
        Predicate predicate = getPredicate(employeeSearchCriteria, employeeRoot);
        criteriaQuery.where(predicate);
        setOrder(employeeSearchCriteria, criteriaQuery, employeeRoot);
        TypedQuery<Employee> typedQuery = entityManager.createQuery(criteriaQuery);
        typedQuery.setFirstResult(employeeSearchCriteria.getPageNumber() * employeeSearchCriteria.getPageSize());
        typedQuery.setMaxResults(employeeSearchCriteria.getPageSize());
        Pageable pageable = getPageable(employeeSearchCriteria);
        long employeesCount = getEmployeesCount(predicate);
        return new PageImpl<>(typedQuery.getResultList(), pageable, employeesCount);
    }
    private Predicate getPredicate(EmployeeSearchCriteriaModel employeeSearchCriteria,
                                   Root<Employee> employeeRoot) {
        List<Predicate> predicates = new ArrayList<>();
        if(Objects.nonNull(employeeSearchCriteria.getName())){
            predicates.add(criteriaBuilder.like(employeeRoot.get("name"), "%" + employeeSearchCriteria.getName() + "%"));
        }
        if(Objects.nonNull(employeeSearchCriteria.getUsername())){
            predicates.add(criteriaBuilder.like(employeeRoot.get("username"), "%" + employeeSearchCriteria.getUsername() + "%"));
        }
        if(Objects.nonNull(employeeSearchCriteria.getPhone())){
            predicates.add(
                    criteriaBuilder.like(employeeRoot.get("phone"),
                            "%" + employeeSearchCriteria.getPhone() + "%")
            );
        }
        if(Objects.nonNull(employeeSearchCriteria.getEmail())){
            predicates.add(
                    criteriaBuilder.like(employeeRoot.get("email"),
                            "%" + employeeSearchCriteria.getEmail() + "%")
            );
        }
        if(Objects.nonNull(employeeSearchCriteria.getIdentityCard())){
            predicates.add(
                    criteriaBuilder.like(employeeRoot.get("identityCard"),
                            "%" + employeeSearchCriteria.getIdentityCard() + "%")
            );
        }
//        if(employeeSearchCriteria.getStatus()>=0){
//            predicates.add(
//                    criteriaBuilder.equal(employeeRoot.get("status"),
//                             employeeSearchCriteria.getStatus())
//            );
//        }
        if(Objects.nonNull(employeeSearchCriteria.getStatus())){
            predicates.add(
                    criteriaBuilder.equal(employeeRoot.get("status"),
                            employeeSearchCriteria.getStatus())
            );
        }
        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    }

    private void setOrder(EmployeeSearchCriteriaModel employeeSearchCriteria,
                          CriteriaQuery<Employee> criteriaQuery,
                          Root<Employee> employeeRoot) {
        if(employeeSearchCriteria.getSortDirection().equals(Sort.Direction.ASC)){
            criteriaQuery.orderBy(criteriaBuilder.asc(employeeRoot.get(employeeSearchCriteria.getSortBy())));
        } else {
            criteriaQuery.orderBy(criteriaBuilder.desc(employeeRoot.get(employeeSearchCriteria.getSortBy())));
        }
    }

    private Pageable getPageable(EmployeeSearchCriteriaModel employeeSearchCriteria) {
        Sort sort = Sort.by(employeeSearchCriteria.getSortDirection(), employeeSearchCriteria.getSortBy());
        return PageRequest.of(employeeSearchCriteria.getPageNumber(),employeeSearchCriteria.getPageSize(), sort);
    }

    private long getEmployeesCount(Predicate predicate) {
        CriteriaQuery<Long> countQuery = criteriaBuilder.createQuery(Long.class);
        Root<Employee> countRoot = countQuery.from(Employee.class);
        countQuery.select(criteriaBuilder.count(countRoot)).where(predicate);
        return entityManager.createQuery(countQuery).getSingleResult();
    }
}

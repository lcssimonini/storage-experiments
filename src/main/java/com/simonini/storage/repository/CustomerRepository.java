package com.simonini.storage.repository;

import com.simonini.storage.domain.Customer;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository
    extends PagingAndSortingRepository<Customer, Long>, JpaSpecificationExecutor<Customer> {}

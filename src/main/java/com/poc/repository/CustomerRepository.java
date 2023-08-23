package com.poc.repository;

import org.springframework.data.repository.CrudRepository;

import com.poc.domain.Customer;

public interface CustomerRepository extends CrudRepository<Customer, Long> {

}
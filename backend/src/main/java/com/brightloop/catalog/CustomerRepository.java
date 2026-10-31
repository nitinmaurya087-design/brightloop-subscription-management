package com.brightloop.catalog;
import org.springframework.data.domain.Page;import org.springframework.data.domain.Pageable;import org.springframework.data.jpa.repository.JpaRepository;
public interface CustomerRepository extends JpaRepository<Customer,Long>{Page<Customer> findByFullNameContainingIgnoreCaseOrEmailContainingIgnoreCase(String name,String email,Pageable pageable);java.util.Optional<Customer> findByEmailIgnoreCase(String email);boolean existsByEmailIgnoreCase(String email);boolean existsByEmailIgnoreCaseAndIdNot(String email,Long id);}

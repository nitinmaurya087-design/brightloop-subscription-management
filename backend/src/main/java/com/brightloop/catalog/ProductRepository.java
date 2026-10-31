package com.brightloop.catalog;
import org.springframework.data.jpa.repository.JpaRepository;public interface ProductRepository extends JpaRepository<Product,Long>{long countByStatus(String status);}

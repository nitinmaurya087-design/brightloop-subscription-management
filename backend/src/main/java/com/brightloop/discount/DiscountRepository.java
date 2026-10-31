package com.brightloop.discount;import java.util.Optional;import org.springframework.data.jpa.repository.JpaRepository;
public interface DiscountRepository extends JpaRepository<Discount,Long>{Optional<Discount> findByCodeIgnoreCase(String code);boolean existsByCodeIgnoreCase(String code);boolean existsByCodeIgnoreCaseAndIdNot(String code,Long id);}

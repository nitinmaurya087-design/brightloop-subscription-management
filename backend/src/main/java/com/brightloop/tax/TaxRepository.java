package com.brightloop.tax;import java.time.LocalDate;import java.util.List;import org.springframework.data.jpa.repository.JpaRepository;
public interface TaxRepository extends JpaRepository<Tax,Long>{List<Tax> findByActiveTrueAndEffectiveStartLessThanEqualAndEffectiveEndGreaterThanEqualOrActiveTrueAndEffectiveStartIsNullAndEffectiveEndIsNull(LocalDate date,LocalDate date2);}

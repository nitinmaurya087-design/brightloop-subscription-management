package com.brightloop.plan;
import org.springframework.data.jpa.repository.JpaRepository;public interface PlanRepository extends JpaRepository<Plan,Long>{long countByStatus(String status);}

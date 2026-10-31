package com.brightloop.invoice;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
public interface InvoiceLineRepository extends JpaRepository<InvoiceLine,Long>{
 @Query("select p.name, coalesce(sum(l.quantity),0), coalesce(sum(l.lineTotal),0) from InvoiceLine l join l.invoice i join l.product p where i.issueDate between :from and :to group by p.id,p.name order by sum(l.lineTotal) desc")
 List<Object[]> productPerformance(LocalDate from,LocalDate to);
}

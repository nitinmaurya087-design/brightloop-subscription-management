package com.brightloop.report;

import com.brightloop.catalog.*;
import com.brightloop.discount.*;
import com.brightloop.invoice.*;
import com.brightloop.payment.*;
import com.brightloop.plan.*;
import com.brightloop.subscription.*;
import java.math.BigDecimal;
import java.time.*;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReportService {
    private final CustomerRepository customers;
    private final ProductRepository products;
    private final PlanRepository plans;
    private final SubscriptionRepository subscriptions;
    private final InvoiceRepository invoices;
    private final InvoiceLineRepository invoiceLines;
    private final PaymentRepository payments;
    private final DiscountRepository discounts;

    public ReportService(CustomerRepository customers, ProductRepository products, PlanRepository plans,
                         SubscriptionRepository subscriptions, InvoiceRepository invoices,
                         InvoiceLineRepository invoiceLines, PaymentRepository payments, DiscountRepository discounts) {
        this.customers=customers; this.products=products; this.plans=plans; this.subscriptions=subscriptions;
        this.invoices=invoices; this.invoiceLines=invoiceLines; this.payments=payments; this.discounts=discounts;
    }

    public record Summary(long totalCustomers,long activeSubscriptions,long totalProducts,long totalPlans,
                          BigDecimal mrr,BigDecimal arr,BigDecimal revenueCollected,BigDecimal outstanding,
                          long overdueInvoices,long upcomingRenewals) {}
    public record MonthlyRevenue(String month,BigDecimal collected) {}
    public record PlanSubscriptions(String name,long subscriptions) {}
    public record Activity(String type,String title,String detail,Instant occurredAt) {}
    public record Metric(String name,long count,BigDecimal amount) {}
    public record ProductPerformance(String product,long units,BigDecimal invoiced) {}
    public record DiscountUsage(String code,String name,int usageCount,Integer maxUses,boolean active) {}

    @Transactional(readOnly=true)
    public Summary summary() {
        BigDecimal mrr=subscriptions.calculateMrr();
        return new Summary(customers.count(),subscriptions.countByStatus("ACTIVE"),products.countByStatus("ACTIVE"),
                plans.countByStatus("ACTIVE"),mrr,mrr.multiply(new BigDecimal("12.00")),payments.totalCollected(),
                invoices.outstandingTotal(),invoices.countByOutstandingGreaterThanAndDueDateBefore(BigDecimal.ZERO,LocalDate.now()),
                subscriptions.countByStatusAndNextBillingDateBetween("ACTIVE",LocalDate.now(),LocalDate.now().plusDays(30)));
    }

    @Transactional(readOnly=true)
    public List<MonthlyRevenue> monthly(LocalDate from,LocalDate to) {
        DateRange range=range(from,to);
        Instant startAt=range.from.atStartOfDay(ZoneOffset.UTC).toInstant();
        Instant endAt=range.to.plusDays(1).atStartOfDay(ZoneOffset.UTC).minusNanos(1).toInstant();
        Map<YearMonth,BigDecimal> data=new TreeMap<>();
        for(YearMonth m=YearMonth.from(range.from);!m.isAfter(YearMonth.from(range.to));m=m.plusMonths(1)) data.put(m,BigDecimal.ZERO);
        for(Payment p:payments.findByPaymentDateBetween(startAt,endAt)) if("COMPLETED".equals(p.getStatus())) {
            YearMonth m=YearMonth.from(p.getPaymentDate().atZone(ZoneOffset.UTC)); data.merge(m,p.getAmount(),BigDecimal::add);
        }
        return data.entrySet().stream().map(e->new MonthlyRevenue(e.getKey().toString(),e.getValue())).toList();
    }

    @Transactional(readOnly=true)
    public List<PlanSubscriptions> activeByPlan() {
        return subscriptions.activeSubscriptionsByPlan().stream().map(r->new PlanSubscriptions((String)r[0],((Number)r[1]).longValue())).toList();
    }

    @Transactional(readOnly=true)
    public List<Activity> activity() {
        List<Activity> result=new ArrayList<>();
        subscriptions.findAll().forEach(s->result.add(new Activity("SUBSCRIPTION","Subscription started",s.getCustomer().getFullName()+" · "+s.getPlan().getName(),s.getCreatedAt())));
        invoices.findAll().forEach(i->result.add(new Activity("INVOICE","Invoice "+i.getInvoiceNumber()+" generated",i.getCustomer().getFullName()+" · INR "+i.getTotal(),i.getIssueDate().atStartOfDay(ZoneOffset.UTC).toInstant())));
        payments.findAll().forEach(p->result.add(new Activity("PAYMENT","Payment received",p.getCustomer().getFullName()+" · INR "+p.getAmount(),p.getPaymentDate())));
        return result.stream().sorted(Comparator.comparing(Activity::occurredAt).reversed()).limit(8).toList();
    }

    @Transactional(readOnly=true)
    public List<InvoiceDtos.Response> overdue() {
        return invoices.findByOutstandingGreaterThanAndDueDateBefore(BigDecimal.ZERO,LocalDate.now()).stream().map(InvoiceDtos.Response::of).toList();
    }

    @Transactional(readOnly=true)
    public List<Metric> subscriptionReport() {
        Map<String,Long> counts=new TreeMap<>();
        subscriptions.findAll().forEach(s->counts.merge(s.getStatus(),1L,Long::sum));
        return counts.entrySet().stream().map(e->new Metric(e.getKey(),e.getValue(),BigDecimal.ZERO)).toList();
    }

    @Transactional(readOnly=true)
    public List<Metric> invoiceReport() {
        Map<String,List<Invoice>> grouped=new TreeMap<>();
        invoices.findAll().forEach(i->grouped.computeIfAbsent(i.getStatus(),key->new ArrayList<>()).add(i));
        return grouped.entrySet().stream().map(e->new Metric(e.getKey(),e.getValue().size(),e.getValue().stream().map(Invoice::getTotal).reduce(BigDecimal.ZERO,BigDecimal::add))).toList();
    }

    @Transactional(readOnly=true)
    public List<Metric> paymentReport(LocalDate from,LocalDate to) {
        DateRange range=range(from,to);
        Instant start=range.from.atStartOfDay(ZoneOffset.UTC).toInstant();
        Instant end=range.to.plusDays(1).atStartOfDay(ZoneOffset.UTC).minusNanos(1).toInstant();
        return payments.totalsByMethod(start,end).stream().map(r->new Metric((String)r[0],((Number)r[1]).longValue(),(BigDecimal)r[2])).toList();
    }

    @Transactional(readOnly=true)
    public List<ProductPerformance> productReport(LocalDate from,LocalDate to) {
        DateRange range=range(from,to);
        return invoiceLines.productPerformance(range.from,range.to).stream().map(r->new ProductPerformance((String)r[0],((Number)r[1]).longValue(),(BigDecimal)r[2])).toList();
    }

    @Transactional(readOnly=true)
    public List<DiscountUsage> discountReport() {
        return discounts.findAll().stream().map(d->new DiscountUsage(d.getCode(),d.getName(),d.getUsageCount(),d.getMaxUses(),d.isActive())).toList();
    }

    @Transactional(readOnly=true)
    public List<Metric> customerReport() {
        Map<String,Long> counts=new TreeMap<>();
        customers.findAll().forEach(c->counts.merge(c.getStatus(),1L,Long::sum));
        return counts.entrySet().stream().map(e->new Metric(e.getKey(),e.getValue(),BigDecimal.ZERO)).toList();
    }

    private DateRange range(LocalDate from,LocalDate to) {
        LocalDate end=to==null?LocalDate.now():to;
        LocalDate start=from==null?end.minusMonths(11).with(TemporalAdjusters.firstDayOfMonth()):from;
        if(start.isAfter(end)) throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.BAD_REQUEST,"Start date must not be after end date");
        return new DateRange(start,end);
    }
    private record DateRange(LocalDate from,LocalDate to) {}
}

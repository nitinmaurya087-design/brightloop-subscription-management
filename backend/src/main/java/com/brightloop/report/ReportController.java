package com.brightloop.report;
import java.time.LocalDate;import java.util.stream.Collectors;import org.springframework.format.annotation.DateTimeFormat;import org.springframework.security.access.prepost.PreAuthorize;import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/reports") @PreAuthorize("hasAnyRole('ADMIN','STAFF')") public class ReportController{
 private final ReportService reports;public ReportController(ReportService r){reports=r;}
 @GetMapping("/summary") public ReportService.Summary summary(){return reports.summary();}
 @GetMapping("/plans") public java.util.List<ReportService.PlanSubscriptions> plans(){return reports.activeByPlan();}
 @GetMapping("/activity") public java.util.List<ReportService.Activity> activity(){return reports.activity();}
 @GetMapping("/revenue") public java.util.List<ReportService.MonthlyRevenue> revenue(@RequestParam(required=false) @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) LocalDate from,@RequestParam(required=false) @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) LocalDate to){return reports.monthly(from,to);}
 @GetMapping(value="/revenue.csv",produces="text/csv") public String csv(@RequestParam(required=false) @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) LocalDate from,@RequestParam(required=false) @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) LocalDate to){return "month,collected\n"+reports.monthly(from,to).stream().map(r->r.month()+","+r.collected()).collect(Collectors.joining("\n"))+"\n";}
 @GetMapping("/overdue") public java.util.List<com.brightloop.invoice.InvoiceDtos.Response> overdue(){return reports.overdue();}
 @GetMapping("/subscriptions") public java.util.List<ReportService.Metric> subscriptions(){return reports.subscriptionReport();}
 @GetMapping("/customers") public java.util.List<ReportService.Metric> customers(){return reports.customerReport();}
 @GetMapping("/invoices") public java.util.List<ReportService.Metric> invoices(){return reports.invoiceReport();}
 @GetMapping("/payments") public java.util.List<ReportService.Metric> payments(@RequestParam(required=false) @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) LocalDate from,@RequestParam(required=false) @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) LocalDate to){return reports.paymentReport(from,to);}
 @GetMapping("/products") public java.util.List<ReportService.ProductPerformance> products(@RequestParam(required=false) @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) LocalDate from,@RequestParam(required=false) @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) LocalDate to){return reports.productReport(from,to);}
 @GetMapping("/discounts") public java.util.List<ReportService.DiscountUsage> discounts(){return reports.discountReport();}
}

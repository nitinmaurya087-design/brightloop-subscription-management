package com.brightloop.billing;
import com.brightloop.invoice.BillingService;import org.slf4j.Logger;import org.slf4j.LoggerFactory;import org.springframework.scheduling.annotation.Scheduled;import org.springframework.stereotype.Component;
@Component public class RecurringBillingScheduler {
 private static final Logger log=LoggerFactory.getLogger(RecurringBillingScheduler.class);private final BillingService billing;public RecurringBillingScheduler(BillingService b){billing=b;}
 @Scheduled(cron="${brightloop.billing.cron:0 15 2 * * *}") public void execute(){int count=billing.runDueBilling();if(count>0)log.info("Recurring billing created {} invoices",count);}
}

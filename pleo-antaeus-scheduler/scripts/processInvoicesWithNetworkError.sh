echo "$(date): Reprocessing invoices with Network error" >> /var/log/cron.log 2>&1
curl -X POST http://pleo-antaeus:7000/rest/v1/billing/retry >> /var/log/cron.log 2>&1
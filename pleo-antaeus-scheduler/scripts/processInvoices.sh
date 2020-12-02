echo "$(date): Processing invoices" >> /var/log/cron.log 2>&1
curl -X POST http://pleo-antaeus:7000/rest/v1/billing >> /var/log/cron.log 2>&1
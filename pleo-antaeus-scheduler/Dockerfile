FROM ubuntu:latest

# Install cron
RUN apt-get update
RUN apt-get install cron

# Add crontab file in the cron directory
ADD crontab /etc/cron.d/simple-cron

# Add shell script and grant execution rights
ADD scripts/* /scripts/
RUN chmod +x /scripts/*

# Give execution rights on the cron job
RUN chmod 0644 /etc/cron.d/simple-cron

# Create the log file to be able to run tail
ARG logFile=var/log/cron.log
RUN touch $logFile

# Install curl for interacting with anteus
RUN apt-get update && apt-get install -y curl

# Installing timezones
RUN apt-get install -y tzdata
ENV TZ Europe/Copenhagen

# Run the command on container startup
CMD cron && tail -f $logFile
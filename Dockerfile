FROM danhixon/backup-base

MAINTAINER Vincent Dubois <dubois.vct@free.fr>

RUN apt-get update && apt-get install -y rsync expect iputils-ping

RUN backup generate:model --trigger daily_backup

RUN mkdir -p /root/Backup/models
ADD config.rb /root/Backup/config.rb
ADD database_backup.rb /root/Backup/models/database_backup.rb

RUN mkdir -p /root/backup-data
VOLUME ["/root/backup-data"]
CMD /bin/ping postgres -c 10;/usr/local/bin/backup perform -t database_backup

# encoding: utf-8

##
# Backup Generated: daily_backup
# Once configured, you can run the backup with the following command:
#
# $ backup perform -t daily_backup [-c <path_to_configuration_file>]
#
# For more information about Backup's components, see the documentation at:
# http://meskyanichi.github.io/backup
#
Model.new(:database_backup, 'Backup de tracker') do
  database PostgreSQL do |db|
    # To dump all databases, set `db.name = :all` (or leave blank)
    db.name               = :all
    db.username           = "tracker"
    db.password           = "tracker"
    db.host               = "postgres"
    db.port               = 5432
    db.socket             = "/tmp/pg.sock"
    # When dumping all databases, `skip_tables` and `only_tables` are ignored.
    db.skip_tables        = []
    db.only_tables        = []
    db.additional_options = []
  end
  store_with FTP do |server|
    server.username     = 'dubois.vct'
    server.password     = 'nk7jdz9z'
    server.ip           = 'ftpperso.free.fr'
    server.port         = 21
    server.path         = '~/tracker-backups/'
    server.keep         = 5
    server.passive_mode = false
  end

  notify_by Mail
end

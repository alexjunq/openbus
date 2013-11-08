# Installing Apache Storm in Cluster

---

Reference: 

* <a href="http://www.michael-noll.com/tutorials/running-multi-node-storm-cluster/">Running a Multi-Node Storm Cluster</a>

---


## It should be installed Zookeper

<a name="Install-Storm"></a>

## Install Storm cluster (0.8.2)  

### Donwload: 

    zeromq-2.1.7-1.el6.x86_64.rpm
    jzmq-2.1.0.el6.x86_64.rpm
    storm-0.8.2.zip

### Commands install

    $ sudo yum install zeromq-2.1.7-1.el6.x86_64.rpm
    $ sudo yum install jzmq-2.1.0.el6.x86_64.rpm
    $ sudo groupadd -g 53001 storm
    $ sudo useradd -u 53001 -g 53001 -d /home/storm/ -s /bin/bash storm -c "Storm service account"
    $ sudo chmod 700 /home/storm/
    $ sudo chage -I -1 -E -1 -m -1 -M -1 -W -1 -E -1 storm
    $ cd /usr/local/
    $ sudo unzip /home/juliansimon/download/storm-0.8.2.zip
    $ sudo chown -R storm:storm storm-0.8.2
    $ sudo ln -s storm-0.8.2 storm

### Dir app storm (only Nimbus)

    $ sudo mkdir -p /home/storm/app/storm/
    $ sudo chown -R storm:storm  /home/storm/app/storm/
    $ sudo chmod 750  /home/storm/app/storm/

    $ cd /usr/local/storm/conf/
    $ sudo cp storm.yaml storm.yaml.ori
	$ sudo vim storm.yaml
#----------------------------------
     storm.zookeeper.servers:
      - "vmlbcnimbusl01"
     nimbus.host: "vmlbcnimbusl01"
     nimbus.childopts: "-Xmx1024m -Djava.net.preferIPv4Stack=true"
     ui.childopts: "-Xmx768m -Djava.net.preferIPv4Stack=true"
     supervisor.childopts: "-Djava.net.preferIPv4Stack=true"
     worker.childopts: "-Xmx768m -Djava.net.preferIPv4Stack=true"
#----------------------------------

#-------- 
Only Nimbus
 
    storm.local.dir: "/home/storm/app/storm" 

    drpc.servers:
    - "vmlbcnimbusl01"

#####################

## Start manually machine nimbus1

    $ sudo su - storm
    $ cd /usr/local/storm
    $ nohup bin/storm nimbus &

## Start manually machine nimbus ui

    $ nohup bin/storm ui &

## Start manually machine nimbus server drpc

    $ nohup bin/storm drpc &

## Create a link simbolic to the logs (not required if installed supervisord)

    $ ln -s /usr/local/storm/logs/nimbus.log /var/log/storm/nimbus.log
    $ ln -s /usr/local/storm/logs/ui.log /var/log/storm/ui.log
    $ ln -s /usr/local/storm/logs/drpc.log /var/log/storm/drpc.log
    
http://nimbus1:8080/

## Start manually machine supervisor

    $ sudo su - storm
    $ cd /usr/local/storm
    $ nohup bin/storm supervisor &


## Install supervisord (optional but recommended)

    $ sudo rpm -Uhv epel-release-6-8.noarch.rpm
    $ sudo yum install supervisor
    $ sudo chkconfig supervisord on

## Recommended: secure supervisord configuration file (may contain user credentials)

    $ sudo chmod 600 /etc/supervisord.conf

## Create log directories for Storm

    $ sudo mkdir -p /var/log/storm
    $ sudo chown -R storm:storm /var/log/storm

## Create a link simbolic (not required if installed supervisord)

    $ ln -s /usr/local/storm/logs/supervisor.log /var/log/storm/supervisor.log

## Verification

    $ /usr/local/storm/bin/storm jar target/openbus-test-0.0.2-jar-with-dependencies.jar openbus.processor.topology.TridentWordCount wordCounter

## Kill

    $ /usr/local/storm/bin/storm kill wordCounter

## Verification with Trident

    $ /usr/local/storm/bin/storm jar target/openbus-test-0.0.2-jar-with-dependencies.jar openbus.processor.topology.TridentRedSocialTest RedSocial

## Kill

    $ /usr/local/storm/bin/storm kill RedSocial

## Master node: Nimbus and UI daemons

    $ vim /etc/supervisord.conf


#--------------------
[program:storm-nimbus]
command=/usr/local/storm/bin/storm nimbus
user=storm
autostart=true
autorestart=true
startsecs=10
startretries=999
log_stdout=true
log_stderr=true
logfile=/var/log/storm/nimbus.out
logfile_maxbytes=20MB
logfile_backups=10

[program:storm-ui]
command=/usr/local/storm/bin/storm ui
user=storm
autostart=true
autorestart=true
startsecs=10
startretries=999
log_stdout=true
log_stderr=true
logfile=/var/log/storm/ui.out
logfile_maxbytes=20MB
logfile_backups=10
#--------------------

#Slave nodes: Supervisor daemons
    $ vim /etc/supervisord.conf

#--------------------
[program:storm-supervisor]
command=/usr/local/storm/bin/storm supervisor
user=storm
autostart=true
autorestart=true
startsecs=10
startretries=999
log_stdout=true
log_stderr=true
logfile=/var/log/storm/supervisor.out
logfile_maxbytes=20MB
logfile_backups=10
#--------------------

    $ sudo service supervisord start
sudo supervisorctl status

# Stop
    $ sudo service supervisord stop


`sudo service tomcat9 start`
`./mvnw clean package`
`cd /var/lib/tomcat9/webapps`
`cp /home/anna/uni/sem6/distributed_systems/priceapp/target/priceapp-0.0.1-SNAPSHOT.war /var/lib/tomcat9/webapps`
`chmod a+rwx priceapp-0.0.1-SNAPSHOT.war`
    
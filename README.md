# Creating microservice using Spring Boot, Consul, Zuul with Spring Security and ELK Stack

Spring framework provides set of libraries for creating micro services in Java.

-   Zuul - gateway service that provides dynamic routing, monitoring, resiliency, security, and more
-   Consul - service registration and discovery
-   Sleuth - distributed tracing via logs
-   Zipkin - distributed tracing system with request visualization
-   ELK Stack - We are using Logstash, Elastics Search and Kibana to index logs
-   Logstash - this is used for logs aggregation & processing, it process the logs and send it to elastic search for indexing
-   FileBeat - this is used to ship service logs to logstash


# Components used in this example

 - **gateway** - this service that provides dynamic routing, monitoring, resiliency, security, and more. In this service spring security is implemented with JWT Token, it acts as a central authentication mechanism for microservices
 
 - **student** - its a microservice, where Hystrix circuit breaker is implemented
 
 - **order** - its a microservices
 
 - **micro** - its a maven module
 
 - **mongo** - mondodb is used for storing user credentials
 
 - **consul** - consul is used for service discovery and registration
 
 - **nginx** - this is used as a reverse proxy for load balancing
 
 - **elastic search** - used for indexing logs
 
 - **Kibana** - used for logs analysis and visiualization
 
 - **logstash** - it collect the logs from filebeat
 
 - **filebeat** - it ships the logs to logstash for processing
 
 
 

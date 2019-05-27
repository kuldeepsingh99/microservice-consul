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
 
# Running this example

## Docker container for File Beat & ELK Stack

### ELK Architecture
![alt text](https://github.com/kuldeepsingh99/microservice-consul/blob/master/images/elk.png "ELK")

#### Pull Images from docker hub registry

docker pull docker.elastic.co/elasticsearch/elasticsearch:6.5.1

docker pull docker.elastic.co/kibana/kibana:6.5.2

docker pull docker.elastic.co/logstash/logstash:6.5.2

docker pull docker.elastic.co/beats/filebeat:6.5.4

#### Start the containers

- ```docker run -d -it --net=mynetwork --name es -p 9200:9200 -e "discovery.type=single-node" docker.elastic.co/elasticsearch/elasticsearch:6.5.1```

- ```docker run -d -it --net=mynetwork --name kibanak --link es:elasticsearch -p 5601:5601 docker.elastic.co/kibana/kibana:6.5.2```

  - here we need to link kibana with elastic search

- ```docker run -d -it --net=mynetwork --name logm -p 5044:5044 -vvv -e xpack.monitoring.enabled=true -e xpack.monitoring.elasticsearch.hosts=192.168.99.100:9200 docker.elastic.co/logstash/logstash:6.5.2```

  - important thing to note here is that xpack monitoring should be enabled and xpack host should be elastic server
  - we also need to make sure that logstash.conf should be present in the (/usr/share/logstash/pipeline) location with following content
    ```
    input {
       beats {
         port => 5044
       }
    }
    output {
      stdout {
        codec => rubydebug
      }
      elasticsearch {
        hosts => ["192.168.99.100:9200"]
      }
    }
    ```
  logstash is accepting beat input in 5044 and it send the events to elastic search
  
- ```docker run -d -it --net=mynetwork --name filebeatms -p 12201:12201 -v /tmp:/usr/share/filebeat/logs/ docker.elastic.co/beats/filebeat:6.5.4```

  - here we need to mount the volume, so that container's and filebeat share the same location
  - we needto make sure this file is present (**filebeat.yml**) location:- **/usr/share/filebeat**
    ```
    filebeat:
       prospectors:
         -
           paths:
             - /usr/share/filebeat/logs/*-json.log
           input_type: log
           multiline.pattern: '^[0-9]{4}-[0-9]{2}-[0-9]{2}'
           multiline.negate: true
           multiline.match: after

     output:
       logstash:
         hosts: ["192.168.99.100:5044"]
    ```
    here we can see that filebeat is reading from the shared location and its sending the log event to logstash for processing

once all the four container's are started we would be able to see the container list
    
![alt text](https://github.com/kuldeepsingh99/microservice-consul/blob/master/images/containerelk.png "Container")

## Project Architecture
    

# Creating microservice using Spring Boot, Consul, Zuul with Spring Security and ELK Stack in Docker Containers

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

1. [Elastic Search](https://www.elastic.co/) is a distributed, JSON-based search and analytics engine designed for horizontal scalability, maximum reliability, and easy management.
2. [Logstash](https://www.elastic.co/products/logstash) is a dynamic data collection pipeline with an extensible plugin ecosystem and strong Elasticsearch synergy.
3. [Kibana](https://www.elastic.co/products/kibana) gives the visualization of data through a UI.
4. [Beat](https://www.elastic.co/products/beats) is used to ship logs from microservice to logstash

![alt text](https://github.com/kuldeepsingh99/microservice-consul/blob/master/images/elk.png "ELK")

#### Pull Images from docker hub registry

```docker pull docker.elastic.co/elasticsearch/elasticsearch:6.5.1```

```docker pull docker.elastic.co/kibana/kibana:6.5.2```

```docker pull docker.elastic.co/logstash/logstash:6.5.2```

```docker pull docker.elastic.co/beats/filebeat:6.5.4```

#### Start the containers

- ```docker run -d -it --net=mynetwork --name es -p 9200:9200 -e "discovery.type=single-node" docker.elastic.co/elasticsearch/elasticsearch:6.5.1```

- ```docker run -d -it --net=mynetwork --name kibanak --link es:elasticsearch -p 5601:5601 docker.elastic.co/kibana/kibana:6.5.2```

  - here we need to link kibana with elastic search

- ```docker run -d -it --net=mynetwork --name logm -p 5044:5044 -vvv -e xpack.monitoring.enabled=true -e xpack.monitoring.elasticsearch.hosts=192.168.99.100:9200 docker.elastic.co/logstash/logstash:6.5.2```

  - important thing to note here is that xpack monitoring should be enabled and xpack host should be elastic server
  - we also need to make sure that **logstash.conf** should be present in **(/usr/share/logstash/pipeline)** location with following content
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
  - **logstash.yml (/usr/share/logstash/config)**
    ```    
    xpack.monitoring.elasticsearch.url: http://192.168.99.100:9200
    xpack.monitoring.enabled: true
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

![alt text](https://github.com/kuldeepsingh99/microservice-consul/blob/master/images/msarc.png "Microservice")

**Netflix Zuul**, Student and order container are registered in **consul** for service discovery. Here **NGINX** we are using as a reverse proxy.

Some features of API Gateway
- **microservice authentication and security** in the gateway layer to protect the actual services
- **Dynamic Routing** can route requests to different backend clusters as needed, Internally, Zuul uses Netflix Ribbon to look up for all instances of the service from the service discovery (Consul)

Student microservice is also has the capabality of load balancing to order service

    ```
    @Bean
    @LoadBalanced
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
    ```
[Hystrix](https://github.com/Netflix/Hystrix) Circuit breaker patterns is also implement, It is generally required to enable fault tolerance in the application where some underlying service is down/throwing error permanently, we need to fall back to different path of program execution automatically. This is related to distributed computing style of Eco system using lots of underlying Microservices

   ```
   @HystrixCommand(fallbackMethod = "callStudentServiceAndGetData_Fallback")
    public Order callOrderService()
    {
		      LOG.info("Calling order service.");
        Order response = restTemplate.getForObject("http://order-service/getorder", Order.class);
        return response;
    }
  ```

![alt text](https://github.com/kuldeepsingh99/microservice-consul/blob/master/images/consul.JPG "Consul")


### Starting the microservice containers

Open Docker terminal and go inside micro project (its a maven module, which will compile order projecs)

- run this command to create jar
   ```mvn clean package```
   
- run this command to build container
   ```docker-compose build```
   
- start the container
  ```docker-compose up -d```

- check all the container has started 
  ```docker ps```

- run this command to scale container
  ```docker-compose scale gateway=2```

  *Here gateway is the name of the service*
  
![alt text](https://github.com/kuldeepsingh99/microservice-consul/blob/master/images/container.JPG "All Container") 


### Register/Login and Access Service

##### Register user
![alt text](https://github.com/kuldeepsingh99/microservice-consul/blob/master/images/register.jpg "Register User") 

##### Login User
![alt text](https://github.com/kuldeepsingh99/microservice-consul/blob/master/images/login.JPG "Login User") 

##### Access service
![alt text](https://github.com/kuldeepsingh99/microservice-consul/blob/master/images/api.JPG "Access Service") 

##### View Logs (Kibana)
![alt text](https://github.com/kuldeepsingh99/microservice-consul/blob/master/images/kibana.JPG "Kibana") 





    

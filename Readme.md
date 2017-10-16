# Metrics Receiver [![Build Status](https://travis-ci.org/comtihon/metric_receiver.svg?branch=master)](https://travis-ci.org/comtihon/metric_receiver)
Service for receiving metrics from devices' sensors.  
Takes all metrics and puts them in kafka.

## Run
Ensure that [Kafka](https://kafka.apache.org/) is accessible before running the service.  
Access urls is specified in application.properties `spring.kafka.bootstrap-servers`.

### In docker

    sudo ./gradlew build buildDocker -x test -x test_integration
    sudo docker run -p 8080:8080 -t com.metric.assessor

### In OS
    
    ./gradlew bootRun
    
## Testing

    ./gradlew check

## Protocol
POST __/api/v1/sensors/{uuid}/measurements__ report sensor's temperature
Where:  
`uuid` is the sensor's uuid.  
BODY:  
    
    {
        "temperature" : <temperature>
    }
Where:  
`temperature` is a value of your sensor's temperature (double).  
Responce:  

    {
        "result" : true,
        "response" : "OK"
    }

# Jio.com API Mocking

## Download Latest Specmatic Release
To download the latest release of Specmatic, use the following command: 
```shell

curl -L -f -o ./src/test/resources/specmatic/specmatic.jar https://github.com/specmatic/specmatic/releases/latest/download/specmatic.jar
```

## Start Mock Server
To start the mock server for Jio.com API recordings, use the following command:

```shell

java -jar ./src/test/resources/specmatic/specmatic.jar stub  jio_com_api_spec.yaml 
```

## Test the Mock Server
You can test the mock server using curl or any API testing tool. Here are some example curl commands:

### Prepaid Recharge
```shell

curl "http://localhost:9000/api/jio-recharge-service/recharge/mobility/number/1111111111"
```
```shell

curl "http://localhost:9000/api/jio-recharge-service/recharge/plans/serviceId/1111111111" -H "Cookie: JioSessionID=160a6005"
```
```shell

curl -X POST "http://localhost:9000/api/jio-recharge-service/recharge/buy" \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -H "User-Agent: Mozilla/5.0 " \
  -H "Cookie: JioSessionID=160a6005" \
  -d '{
    "planKey": "1018982#1Z0002#Z0003#Z0006RECHARGE1cb20",
    "selectedService": "1111111111"
  }'
```
```shell

curl -X POST "http://localhost:9000/api/jio-recharge-service/recharge/pay" \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -H "User-Agent: Mozilla/5.0" \
  -H "Cookie: JioSessionID=160a6005" \
  -d '{"addonPlanKeys":[],"flexiTopupFlow":false,"servicePlanList":[{"planKey":"1018982#1Z0002#Z0003#Z0006RECHARGE1cb20","quantity":1,"serviceId":"1111111111"}]}'
```

### Postpaid Bill Payment
```shell

curl "http://localhost:9000/api/jio-recharge-service/recharge/mobility/number/2222222222"
```
```shell

curl "http://localhost:9000/api/jio-paybill-service/paybill/submitDetail/2222222222/50?rechargeType=fetchBill&serviceType=mobility"
```
#### High Amount Postpaid Bill Payment - Rejected
```shell

curl "http://localhost:9000/api/jio-paybill-service/paybill/submitDetail/2222222222/5000?rechargeType=fetchBill&serviceType=mobility"
```

### Invalid Phone number (400 Bad Request)
```shell

curl "http://localhost:9000/api/jio-recharge-service/recharge/mobility/number/9999999999"
```

## Simulate Network Delay
```shell

curl "http://localhost:9000/api/jio-recharge-service/recharge/mobility/number/3333333333"
```

```shell

curl "http://localhost:9000/api/jio-paybill-service/paybill/submitDetail/3333333333/50?rechargeType=fetchBill&serviceType=mobility"
```
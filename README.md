# Getting Started

### Reference Documentation

For further reference, please consider the following sections:

* [Official Gradle documentation](https://docs.gradle.org)
* [Spring Boot Gradle Plugin Reference Guide](https://docs.spring.io/spring-boot/4.0.6/gradle-plugin)
* [Create an OCI image](https://docs.spring.io/spring-boot/4.0.6/gradle-plugin/packaging-oci-image.html)

### Additional Links

These additional references should also help you:

* [Gradle Build Scans – insights for your project's build](https://scans.gradle.com#gradle)

### Running The Application

This application is a standard spring-boot Gradle application and
so can be run locally using `./gradlew bootRun`

The application exposes a simple API which can be accessed using cURL.

To create a new account:
`curl -X POST http://localhost:8080/v1/accounts/create`

This is a pre-requisite for other requests as only a valid account ID
can have transactions performed against it, otherwise the application
will return 400 on all requests.

To deposit money:
```
curl -X POST -H "Content-Type: application/json" http://localhost:8080/v1/accounts/{accountId}/deposit 
-d "{
  \"transactionId\": \"79bc1cd9-01f7-4881-97b5-8e38f84d7ee8\",
  \"amount\": 10
}"
```
This will return the transaction ID of a successful transaction.

A valid deposit must have:
 - A valid account ID (UUID) existing in the system
 - A unique transaction ID (UUID)
 - A value between 0 - 10,000,000 (exclusive)
 - A value with no more than 2 decimal places

Any violation of these constraints will result in a 400 response.
In the case that a transaction ID is repeated, no processing will take
place and the provided transaction ID will be returned directly.

To withdraw money:
```
curl -X POST -H "Content-Type: application/json" http://localhost:8080/v1/accounts/{accountId}/withdraw 
-d "{
  \"transactionId\": \"79bc1cd9-01f7-4881-92b5-8b38f54d7ee8\",
  \"amount\": 10
}"
```
This will return the transaction ID of a successful transaction.

A valid withdrawal must have:
 - A valid account ID (UUID) existing in the system
 - A unique transaction ID (UUID)
 - A value between 0 - 10,000,000 (exclusive)
 - A value with no more than 2 decimal places
 - A value less than or equal to the current account balance

Any violation of these constraints will result in a 400 response.
In the case that a transaction ID is repeated, no processing will take
place and the provided transaction ID will be returned directly.

To check account balances:
`curl -X GET http://localhost:8080/v1/accounts/{accountId}/balance`

This will return a single value, which is the current balance of the
account queried.

A valid balance check must have:
 - A valid account ID (UUID) existing in the system

For any invalid account IDs a 400 response will be returned.

To check transaction history:
`curl -X GET http://localhost:8080/v1/accounts/{accountId}/transaction-history`

This will return a list of all transactions against the account starting
with the latest transaction.

A valid transaction history lookup must have:
- A valid account ID (UUID) existing in the system

For any invalid account IDs a 400 response will be returned.


# TargetCaseStudy - RetailAPI

## Intro
MyRetail is an implementation to retrieve/aggregate product details using the productId. Application is built using Spring boot and MongoDB technologies.

## Database
start the instance of MongoDb on your system using 

```
mongod --config /usr/local/etc/mongod.conf
```

Once you have the running instance of Mongo, save the following test date file and run the following command to import test date into the collection.

```
mongoimport --collection products --drop --jsonArray --file sample-data.json
```

## Documentation
`http://localhost:8080/swagger-ui.html# `
App implements Swagger for documentation.

## Security
I have enabled security for the application which would allow any user to get the product details but only authorized user with ADMIN role to update product price. 

Please use following credentials when updating product price.

username: admin
password: password

## Build
```
./gradlew build
```

## Run
```
./gradlew bootrun
```

## Tests 
```
./gradlew test
```
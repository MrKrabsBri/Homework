# Homework
Homework task

# Client's Service Order Document api

## Features
- [x] Auto-generated classes via JAXB to handle SOAP requests alongside entity classes for persistence
- [x] SOAP request, response generation/handling
- [x] CRUD operations
- [x] Validator for field validation
- [x] XML Transformation according to rules
- [x] Error Handling
- [ ] Authentication

NOTES: 

Data is validated both at the XML Schema (XSD) level and in Java code.
Due to the current flow, some requests do not reach the custom Java validation I implemented.
In the XSD file, certain fields are currently commented out; these are currently replaced with type="tns:NonEmptyString" to enforce stricter schema-level validation. Using "type="xs:string" minOccurs="0"/>" would trigger custom validation and produce cleaner SOAP responses,  as task required, particularly for create or update operations where mandatory fields are missing or empty, which the XSD validation detects.
GET operations currently return SOAP responses as objects. I left it like this for easier testing in soapUI. In a production scenario, I would implement full SOAP response messages similar to the CREATE, UPDATE, and DELETE operations.

## Technologies
- Java 17
- JAXB
- Spring WS
- Spring Boot
- Spring Data JPA
- H2 Database
- SoapUI

## H2 usage
  H2 console can be accessed by [http://localhost:8082/h2-console/ ](http://localhost:8082/h2-console/)
  Database access requires credentials set through environment variables. When running locally, you must provide them manually as shown below:
  DB_USERNAME=root (or anything)
  DB_PASSWORD=password (or anything)

## WSDL Access
WSDL Access via http://localhost:8082/ws/OrderDocumentService.wsdl 

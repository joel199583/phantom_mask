# Response
## A. Required Information
### A.1. Requirement Completion Rate
- [x] List all pharmacies open at a specific time and on a day of the week if requested.
  - Implemented at `GET /store/open` API.
- [x] List all masks sold by a given pharmacy, sorted by mask name or price.
  - Implemented at `GET /store/maskinfo` API.
- [x] List all pharmacies with more or less than x mask products within a price range.
  - Implemented at `GET /store/price-range-masktype-count` API.
- [x] The top x users by total transaction amount of masks within a date range.
  - Implemented at `GET /user/top-transactions` API.
- [x] The total number of masks and dollar value of transactions within a date range.
  - Implemented at `GET /user/top-transactions` API.
- [x] Search for pharmacies or masks by name, ranked by relevance to the search term.
  - Implemented at `GET /store/search-store` .
- [x] Process a user purchases a mask from a pharmacy, and handle all relevant data changes in an atomic transaction.
  - Implemented at `POST /user/buy-mask/{userId}` API.
  
### A.2. API Document
Import [kdan.postman.json](./kdan.postman.json) json file to Postman.

Swagger UI : [https://029d3f8f3c13.ngrok.app/swagger-ui/index.html#](https://029d3f8f3c13.ngrok.app/swagger-ui/index.html#)

### A.3. Import Data Commands
Automatically handle when starting the app.

## B. Bonus Information

>  If you completed the bonus requirements, please fill in your task below.
### B.1. Test Coverage Report

### B.2. Dockerized

```bash
docker pull joel0803/kdan-demo:0.0.1
```

### B.3. Demo Site Url
The demo site is ready on [Joel's Demo](https://029d3f8f3c13.ngrok.app); you can try any APIs on this demo site.

## C. Other Information

### C.1. ERD
[schema](./kdan-schema.sql)

[ERD](./ERD.jpg)

### C.2. Technical Document

- --

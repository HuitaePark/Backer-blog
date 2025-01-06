# 📚 스프링부트 - 계좌 API 시스템

## 🌟 소개

> 계좌 생성, 조회, 해지, 거래, 거래 취소, 거래 조회 기능을 제공하는 계좌 API 서비스

## 💻 기술 스택

- Java 11
- Spring Boot 2.7.13
- Spring Boot Data JPA
- Spring Boot Validation
- Redisson
- Embedded Redis
- MySQL
- Git
<details>
## 🗂 API 명세서

### 에러

예외가 발생했을 때, 본문에 해당 문제를 기술한 JSON 객체가 담겨있습니다.

| Path           | Type     | Description |
|----------------|----------|-------------|
| `errorCode`    | `String` | 에러 코드       |
| `errorMessage` | `String` | 에러 메세지      |

예를 들어, 계좌 생성시에 사용자를 찾을 수 없다면 다음과 같은 응답을 받게 됩니다.

``` http request
HTTP/1.1 400 
Content-Type: application/json
Transfer-Encoding: chunked
Date: Fri, 25 Aug 2023 11:31:06 GMT
Connection: close

{
  "errorCode": "USER_NOT_FOUND",
  "errorMessage": "사용자가 없습니다."
}
```

### 계좌

> 계좌 리소스는 계좌 등록, 해지, 조회를 할 때 사용됩니다.

#### 계좌 등록

`POST` 요청을 사용해서 새 계좌를 등록할 수 있습니다.

##### Request fields

| Path             | Type   | Description |
|------------------|--------|-------------|
| `userId`         | `Long` | 유저ID        |
| `initialBalance` | `Long` | 초기 계좌 잔액    |

##### Example request

``` http request
POST http://localhost:8080/account
Content-Type: application/json

{
  "userId": 1,
  "initialBalance": "1000"
}
```

##### Response fields

| Path            | Type            | Description |
|-----------------|-----------------|-------------|
| `userId`        | `Long`          | 유저ID        |
| `accountNumber` | `String`        | 계좌 번호       |
| `registeredAt`  | `LocalDateTime` | 계좌 생성 일자    |

##### Example response

``` http request
HTTP/1.1 200 
Content-Type: application/json
Transfer-Encoding: chunked
Date: Fri, 25 Aug 2023 11:38:57 GMT
Keep-Alive: timeout=60
Connection: keep-alive

{
  "userId": 1,
  "accountNumber": "1234",
  "registeredAt": "2023-01-01 12:00:00"
}
```

#### 계좌 해지

`DELETE` 요청을 사용해서 기존 계좌를 해지할 수 있습니다.

##### Request fields

| Path            | Type     | Description |
|-----------------|----------|-------------|
| `userId`        | `Long`   | 유저ID        |
| `accountNumber` | `String` | 계좌 번호       |

##### Example request

``` http request
DELETE http://localhost:8080/account
Content-Type: application/json

{
  "userId": 1,
  "accountNumber": "1000"
}
```

##### Response fields

| Path             | Type            | Description |
|------------------|-----------------|-------------|
| `userId`         | `Long`          | 유저ID        |
| `accountNumber`  | `String`        | 계좌 번호       |
| `unRegisteredAt` | `LocalDateTime` | 계좌 해지 일자    |

##### Example response

``` http request
HTTP/1.1 200 
Content-Type: application/json
Transfer-Encoding: chunked
Date: Fri, 25 Aug 2023 11:38:57 GMT
Keep-Alive: timeout=60
Connection: keep-alive

{
  "userId": 1,
  "accountNumber": "1234",
  "unRegisteredAt": "2023-01-01 12:00:00"
}
```

#### 계좌 목록 조회

`GET` 요청을 사용해서 사용자 계좌 목록을 조회할 수 있습니다.

#### Path parameters

> /account?user_id={user_id}

##### Request fields

| Path      | Type   | Description |
|-----------|--------|-------------|
| `user_id` | `Long` | 유저ID        |

##### Response fields

| Path            | Type     | Description |
|-----------------|----------|-------------|
| `accountNumber` | `String` | 계좌 번호       |
| `balance`       | `Long`   | 계좌 잔액       |

##### Example response

``` http request
HTTP/1.1 200 
Content-Type: application/json
Transfer-Encoding: chunked
Date: Fri, 25 Aug 2023 11:38:57 GMT
Keep-Alive: timeout=60
Connection: keep-alive

{
  [
    {
      "accountNumber": 1234,
      "balance": "1000"
    }
  ]
}
```

#### 계좌 조회

`GET` 요청을 사용해서 사용자 계좌를 조회할 수 있습니다.

#### Path parameters

> /account/{id}

##### Request fields

| Path | Type   | Description |
|------|--------|-------------|
| `id` | `Long` | 계좌ID        |

##### Response fields

| Path             | Type            | Description |
|------------------|-----------------|-------------|
| `accountUser`    | `String`        | 계좌 보유 유저명   |
| `accountNumber`  | `String`        | 계좌 번호       |
| `accountStatus`  | `String`        | 계좌 상태      |
| `balance`        | `Long`          | 계좌 잔액      |
| `registeredAt`   | `LocalDateTime` | 계좌 생성 일자    |
| `unRegisteredAt` | `LocalDateTime` | 계좌 해지 일자    |

##### Example response

``` http request
HTTP/1.1 200 
Content-Type: application/json
Transfer-Encoding: chunked
Date: Fri, 25 Aug 2023 11:38:57 GMT
Keep-Alive: timeout=60
Connection: keep-alive

{
  "accountUser": {
    "name": "홍길동"
  },
  "accountNumber": "1234",
  "accountStatus": "IN_USE",
  "balance": 1000,
  "registeredAt": "2023-01-01 12:00:00",
  "unRegisteredAt": "2023-01-01 12:00:00",
}
```

### 거래

> 거래 리소스는 잔액 사용, 잔액 사용 취소, 거래 확인을 할 때 사용됩니다.

#### 잔액 사용

`POST` 요청을 사용해서 잔액 사용을 등록할 수 있습니다.

##### Request fields

| Path            | Type     | Description |
|-----------------|----------|-------------|
| `userId`        | `Long`   | 유저ID        |
| `accountNumber` | `String` | 계좌 번호       |
| `amount`        | `Long`   | 금액          |

##### Example request

``` http request
POST http://localhost:8080/transaction/use
Content-Type: application/json

{
  "userId": 1,
  "accountNumber": "1234",
  "amount": 1000
}
```

##### Response fields

| Path                | Type            | Description |
|---------------------|-----------------|-------------|
| `accountNumber`     | `String`        | 계좌번호        |
| `transactionResult` | `String`        | 성공/실패       |
| `transactionId`     | `String`        | 거래ID        |
| `amount`            | `Long`          | 금액          |
| `transactedAt`      | `LocalDateTime` | 거래 일자       |

##### Example response

``` http request
HTTP/1.1 200 
Content-Type: application/json
Transfer-Encoding: chunked
Date: Fri, 25 Aug 2023 11:38:57 GMT
Keep-Alive: timeout=60
Connection: keep-alive

{
  "accountNumber": "1234",
  "transactionResult": "S",
  "transactionId": "1",
  "amount": 1000,
  "transactedAt": "2023-01-01 12:00:00"
}
```

#### 잔액 사용 취소

`POST` 요청을 사용해서 잔액 사용 취소를 등록할 수 있습니다.

##### Request fields

| Path            | Type     | Description |
|-----------------|----------|-------------|
| `transactionId` | `String` | 거래ID        |
| `accountNumber` | `String` | 계좌 번호       |
| `amount`        | `Long`   | 금액          |

##### Example request

``` http request
POST http://localhost:8080/transaction/cancel
Content-Type: application/json

{
  "transactionId": "1",
  "accountNumber": "1234",
  "amount": 1000
}
```

##### Response fields

| Path                | Type            | Description |
|---------------------|-----------------|-------------|
| `accountNumber`     | `String`        | 계좌번호        |
| `transactionResult` | `String`        | 성공/실패       |
| `transactionId`     | `String`        | 거래ID        |
| `amount`            | `Long`          | 금액          |
| `transactedAt`      | `LocalDateTime` | 거래 일자       |

##### Example response

``` http request
HTTP/1.1 200 
Content-Type: application/json
Transfer-Encoding: chunked
Date: Fri, 25 Aug 2023 11:38:57 GMT
Keep-Alive: timeout=60
Connection: keep-alive

{
  "accountNumber": "1234",
  "transactionResult": "S",
  "transactionId": "1",
  "amount": 1000,
  "transactedAt": "2023-01-01 12:00:00"
}
```

#### 계좌 조회

`GET` 요청을 사용해서 사용자 계좌를 조회할 수 있습니다.

#### Path parameters

> /transaction/{transactionId}

##### Request fields

| Path            | Type     | Description |
|-----------------|----------|-------------|
| `transactionId` | `String` | 거래ID        |

##### Response fields

| Path                | Type            | Description |
|---------------------|-----------------|-------------|
| `accountNumber`     | `String`        | 계좌 번호       |
| `transactionType`   | `String`        | 사용/취소       |
| `transactionResult` | `String`        | 성공/실패       |
| `transactionId`     | `String`        | 거래ID        |
| `amount`            | `Long`          | 금액          |
| `transactedAt`      | `LocalDateTime` | 거래 일자       |

##### Example response

``` http request
HTTP/1.1 200 
Content-Type: application/json
Transfer-Encoding: chunked
Date: Fri, 25 Aug 2023 11:38:57 GMT
Keep-Alive: timeout=60
Connection: keep-alive

{
  "accountNumber": "1234",
  "transactionType": "USE",
  "transactionResult": "S",
  "transactionId": "1",
  "amount": 1000,
  "transactedAt": "2023-01-01 12:00:00",
}
```
</details>

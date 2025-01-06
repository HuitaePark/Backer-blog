# 📚 뒷배 - 게시판 API 시스템

## 🌟 소개

> 게시판에서 관련된 로직들을 구현한 API 입니다.

## 💻 기술 스택

- Java 17
- Spring Boot 3.4.1
- Spring Boot Data JPA
- Redis
- MySQL
- Git

## 🗂 API 명세서
<details>
  <summary>회원 인증</summary>
  
### 회원 가입 및 로그인

> 뒷배 게시판에 회원가입과 로그인을 할수 있습니다

#### 회원 등록

`POST` 요청을 사용해서 새 계정정보를 등록할 수 있습니다.

##### Request fields

| Path             | Type   | Description |
|------------------|--------|-------------|
| `username`         | `String` | 유저ID        |
| `password` | `String` | 비밀번호   |
| `passwordCheck` | `String` | 비밀번호 확인   |
| `name`         | `String` | 유저닉네임        |
##### Example request

``` http request
POST http://localhost:8080/session/join
Content-Type: application/json

{
  "username": "string",
  "password": "string",
  "passwordCheck": "string",
  "name": "string"
}
```

##### Example response

``` http request
HTTP/1.1 201 
회원 가입이 완료되었습니다.
```

#### 로그인

`POST` 요청을 사용해서 로그인이 가능합니다.<br>
로그인에 성공하면 세션을 얻습니다.

##### Request fields

| Path            | Type     | Description |
|-----------------|----------|-------------|
| `username`        | `String`   | 유저ID   |
| `password` | `String` | 비밀 번호       |

##### Example request

``` http request
POST http://localhost:8080/session/login
Content-Type: application/json

{
  "username": "string",
  "password": "string"
}
```


##### Example response

``` http request
HTTP/1.1 200 
로그인 성공
```

#### 로그아웃

`POST` 요청을 사용해서 로그아웃이 가능합니다.

##### Example request

``` http request
POST http://localhost:8080/session/logout
Content-Type: application/json

```
##### Example response

``` http request
HTTP/1.1 200 
로그아웃 성공
```
</details>
<details>
  <summary>게시물 관리</summary>
  
### 게시물 관리

> 뒷배 게시판에 게시물 작성 및 수정,삭제와 상세를 보거나 리스트로 볼수 있습니다.

#### 게시물 등록

`POST` 요청을 사용해서 새 게시물을 등록할수 있습니다.<br>
유저 닉네임과 작성 시간은 자동으로 등록되며 <br>
카테고리 번호는 QUESTION(1), INFORMATION(2),HUMOR(3), FREE(4) 입니다.

##### Request fields

| Path             | Type   | Description |
|------------------|--------|-------------|
| `title`         | `String` | 게시물 제목   |
| `content` | `String` | 게시물 내용   |
| `category_id` | `Int` | 카테고리 번호   |

##### Example request

``` http request
POST http://localhost:8080/post
Content-Type: application/json

{
  "title": "string",
  "content": "string",
  "category_id": "1"
}
```

##### Example response

``` http request
HTTP/1.1 201
게시물 작성을 성공하였습니다
```
#### 게시물 삭제

`DELETE` 요청을 사용해서 게시물 번호에 맞는 게시물을 삭제합니다.

##### Example request

``` http request
DELETE http://localhost:8080/post/{post_id}
Content-Type: application/json
```

##### Example response

``` http request
HTTP/1.1 204 
게시물 삭제를 삭제를 성공하였습니다.
```
#### 게시물 수정

`PATCH` 요청을 사용해서 게시물 수정을 할수 있습니다.<br>
 카테고리와 콘텐츠, 카테고리를 수정할수 있습니다.

##### Request fields

| Path             | Type   | Description |
|------------------|--------|-------------|
| `title`         | `String` | 게시물 제목   |
| `content` | `String` | 게시물 내용   |
| `category_id` | `Int` | 카테고리 번호   |

##### Example request

``` http request
PATCH http://localhost:8080/post
Content-Type: application/json

{
  "title": "string",
  "content": "string",
  "category_id": "1"
}
```


##### Example response

``` http request
HTTP/1.1 200 
게시물 수정을 성공하였습니다.
```
#### 게시물 상세보기

`GET` 요청을 사용해서 게시물 상세정보와 달린 댓글을 봅니다.

##### Example request

``` http request
GET http://localhost:8080/post/view/{post_id}
Content-Type: application/json
```

##### 게시물 응답 필드
| Path             | Type   | Description |
|------------------|--------|-------------|
| `id`         | `Long` | 게시물 아이디   |
| `title`         | `String` | 게시물 제목   |
| `content` | `String` | 게시물 내용   |
| `name` | `String` | 작성 유저 닉네임   |
| `category_id` | `int` | 카테고리 번호   |
| `create_date` | `LocalDateTime` | 작성시간   |
##### 댓글 응답 필드
| Path             | Type   | Description |
|------------------|--------|-------------|
| `id`         | `Long` | 댓글 아이디   |
| `content` | `String` | 댓글 내용   |
| `memberId` | `Long` | 작성 유저 아이디   |
| `postId` | `Long` | 게시물 아이디   |



##### Example response

``` http response
HTTP/1.1 200 
{
  "id": 0,
  "title": "string",
  "content": "string",
  "name": "string",
  "category_id": "QUESTION",
  "create_date": "2025-01-06T02:33:07.759Z",
  "comments": [
    {
      "id": 0,
      "content": "string",
      "memberId": 0,
      "postId": 0
    }
  ]
}
```
#### 게시물 목록 조회

`GET` 요청을 사용해서 게시물 목록을 원하는 정렬방식으로 봅니다.
##### Request fields
keyword (string, optional): 검색 키워드.

category (string, optional): 게시물 카테고리 ID.

page (integer, optional): 페이지 번호 (기본값: 1).

size (integer, optional): 페이지 크기 (기본값: 20).

sort (string, optional): 정렬 기준 (예: create_date,asc).

##### Example request

``` http request
GET http://localhost:8080/post/list?keword={keyword}
&category={category}
&page={page}
&size={size}
&sort={sort}
Content-Type: application/json
```


##### 최상위 필드

| Path               | Type               | Description                          |
|--------------------|--------------------|--------------------------------------|
| `totalPages`       | `int`              | 전체 페이지 수                       |
| `totalElements`    | `int`              | 전체 요소 수                         |
| `first`            | `boolean`          | 첫 번째 페이지 여부                  |
| `last`             | `boolean`          | 마지막 페이지 여부                   |
| `size`             | `int`              | 페이지당 요소 수                     |
| `number`           | `int`              | 현재 페이지 번호                     |
| `numberOfElements` | `int`              | 현재 페이지의 요소 수                |
| `empty`            | `boolean`          | 콘텐츠가 비어있는지 여부             |
| `content`          | `List<Content>`    | 게시물 목록                          |
| `sort`             | `Sort`             | 정렬 정보                            |
| `pageable`         | `Pageable`         | 페이징 정보                          |

##### `content` 배열 내 각 항목

| Path         | Type                | Description      |
|--------------|---------------------|------------------|
| `create_date` | `String (ISO 8601)` | 작성일자         |
| `post_id`    | `int`               | 게시물 ID        |
| `name`       | `String`            | 작성자 닉네임    |
| `title`      | `String`            | 게시물 제목      |
| `content`    | `String`            | 게시물 내용      |


##### `sort` 객체

| Path       | Type      | Description                     |
|------------|-----------|---------------------------------|
| `empty`    | `boolean` | 정렬 정보가 비어있는지 여부      |
| `unsorted` | `boolean` | 정렬되지 않았는지 여부          |
| `sorted`   | `boolean` | 정렬되었는지 여부                |


##### `pageable` 객체

| Path         | Type        | Description                         |
|--------------|-------------|-------------------------------------|
| `offset`     | `int`       | 페이지의 오프셋                      |
| `sort`       | `Sort`      | 정렬 정보                           |
| `pageSize`   | `int`       | 페이지당 요소 수                     |
| `pageNumber` | `int`       | 현재 페이지 번호                     |
| `paged`      | `boolean`   | 페이징 여부                          |
| `unpaged`    | `boolean`   | 페이징이 적용되지 않았는지 여부        |


##### `Sort` 객체 (공통)

| Path        | Type      | Description                      |
|-------------|-----------|----------------------------------|
| `empty`     | `boolean` | 정렬 정보가 비어있는지 여부         |
| `unsorted`  | `boolean` | 정렬되지 않았는지 여부             |
| `sorted`    | `boolean` | 정렬되었는지 여부                   |

##### Example response

``` http response
HTTP/1.1 200 
{
  "totalPages": 0,
  "totalElements": 0,
  "first": true,
  "last": true,
  "size": 0,
  "content": [
    {
      "create_date": "2025-01-06T03:51:37.675Z",
      "post_id": 0,
      "name": "string",
      "title": "string",
      "content": "string"
    }
  ],
  "number": 0,
  "sort": {
    "empty": true,
    "unsorted": true,
    "sorted": true
  },
  "numberOfElements": 0,
  "pageable": {
    "offset": 0,
    "sort": {
      "empty": true,
      "unsorted": true,
      "sorted": true
    },
    "pageSize": 0,
    "pageNumber": 0,
    "paged": true,
    "unpaged": true
  },
  "empty": true
}
```
</details>
<details>
  <summary>댓글 관리</summary>
  
  ### 댓글 관리

> 뒷배 게시판에 댓글 작성 및 수정, 삭제를 합니다.

#### 댓글 등록

`POST` 요청을 사용해서 새 댓글을 등록합니다.

##### Request fields

| Path             | Type   | Description |
|------------------|--------|-------------|
| `id`         | `Long` | 댓글 아이디   |
| `content` | `String` | 댓글 내용   |
| `memberId` | `Long` | 멤버 아이디   |
| `postId` | `Long` | 게시물 아이디   |
##### Example request

``` http request
POST http://localhost:8080/comment
Content-Type: application/json

{
  "id": 0,
  "content": "string",
  "memberId": 0,
  "postId": 0
}
```

##### Response fields

##### Example response

``` http response
HTTP/1.1 201 
댓글 작성을 성공하였습니다
```
#### 댓글 수정

`PATCH` 요청을 사용해서 댓글 수정을 할수 있습니다.<br>
멤버 아이디와 게시물 아이디는 원본 댓글과 똑같이 넣어야 합니다. 

##### Request fields

| Path             | Type   | Description |
|------------------|--------|-------------|
| `id`         | `Long` | 댓글 아이디   |
| `content` | `String` | 댓글 내용   |
| `memberId` | `Long` | 멤버 아이디   |
| `postId` | `Long` | 게시물 아이디   |
##### Example request

``` http request
PATCH http://localhost:8080/comment/{comment_id}
Content-Type: application/json
{
  "id": 0,
  "content": "string",
  "memberId": 0,
  "postId": 0
}

```
##### Example response

``` http request
HTTP/1.1 200 
댓글 수정을 성공하였습니다.
```
#### 댓글 삭제

`DELETE` 요청을 사용해서 댓글을 삭제합니다.

##### Request fields

##### Example request

``` http request
DELETE http://localhost:8080//comment/{comment_id}
Content-Type: application/json
```
##### Example response

``` http request
HTTP/1.1 204 
댓글 삭제를 성공하였습니다.
```
</details>

<details>
  <summary>회원 정보 관리</summary>

  ### 회원 가입 및 로그인

> 뒷배 게시판에 가입한 유저의 회원 정보 조회와 수정이 가능합니다.

#### 회원 정보 보기

`GET` 요청을 사용해서 게시물 상세정보와 달린 댓글을 봅니다.<br>
현재 세션의 유저와 요청한 유저가 다를경우 403에러가 발생합니다.

##### Example request

``` http request
GET http://localhost:8080/post/view/{post_id}
Content-Type: application/json
```
##### 응답 필드
| Path             | Type   | Description |
|------------------|--------|-------------|
| `id`         | `Long` | 유저 번호   |
| `username` | `String` | 유저 아이디   |
| `user_role` | `String` | 유저 권한  |
| `name` | `String` | 유저 닉네임  |

##### Example response

``` http response
HTTP/1.1 200 
{
  "id": 0,
  "username": "string",
  "user_role": "USER",
  "name": "string"
}
```

#### 회원정보 수정

`PATCH` 요청을 사용해서 닉네임과 비밀번호 수정을 할 수 있습니다.<br>

##### Request fields

| Path             | Type   | Description |
|------------------|--------|-------------|
| `password`         | `String` | 회원 비밀번호   |
| `name` | `String` | 회원 닉네임   |

##### Example request

``` http request
PATCH http://localhost:8080/member/{id}
Content-Type: application/json
{
  "password": "string",
  "name": "string"
}

```
#### 회원 정보 보기

`GET` 요청을 사용해서 현재 회원의 작성한 게시글과 댓글을 봅니다.

##### Example request

``` http request
GET http://localhost:8080/member/{id}/activity
Content-Type: application/json
```
##### 응답 필드
| Path          | Type            | Description         |
|---------------|-----------------|---------------------|
| `postId`      | `Long`          | 게시물 ID           |
| `title`       | `String`        | 게시물 제목         |
| `content`     | `String`        | 게시물 내용         |
| `category`    | `String`        | 게시물 카테고리     |
| `createdDate` | `LocalDateTime` | 게시물 작성일자     |

<br>

| Path        | Type     | Description         |
|-------------|----------|---------------------|
| `commentId` | `Long`   | 댓글 ID             |
| `content`   | `String` | 댓글 내용           |
| `postId`    | `Long`   | 연관된 게시물 ID    |

##### Example response

``` http response
HTTP/1.1 200 OK
{
  "posts": [
    {
      "postId": 0,
      "title": "string",
      "content": "string",
      "category": "string",
      "createdDate": "2025-01-06T06:46:00.209Z"
    }
  ],
  "comments": [
    {
      "commentId": 0,
      "content": "string",
      "postId": 0
    }
  ]
}
```
  
</details>

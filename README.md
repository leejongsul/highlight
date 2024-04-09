# 하이라이트 피드 API

MySQL 8.1 버전을 사용했습니다.

## DB Schema

### Users

* `id`: Primary key
* `name`: 이름
* `username`: 유저네임
* `createdAt`: User 생성 시간

```mysql
create table Users
(
    id         bigint auto_increment
        primary key,
    created_at datetime(6) not null,
    name       varchar(50) not null,
    username   varchar(50) not null
);
```

### Pages

* `id`: Primary key
* `userId`: index
* `url`: Page URL
* `title`: Page title
* `scope`: index 공개 범위
    * public, mentioned, private
* `mentioned`: 맨션한 userIds
* `createdAt`: 최초로 하이라이트한 시간

```mysql
create table Pages
(
    id         bigint auto_increment
        primary key,
    created_at datetime(6)  not null,
    user_id    bigint       not null,
    url        varchar(256) not null,
    title      varchar(256) not null,
    scope      varchar(10)  not null
);

create index Pages_scope
    on Pages (scope);

create index Pages_user_id
    on Pages (user_id);
```

### Mentions

* `id`: Primary key
* `pageId`: index
* `mentionUserId`: index

```mysql
create table Mentions
(
    id              bigint auto_increment
        primary key,
    created_at      datetime(6) not null,
    page_id         bigint      not null,
    mention_user_id bigint      not null
);

create index Mentions_mention_user_id
    on Mentions (mention_user_id);

create index Mentions_page_id
    on Mentions (page_id);
```

### Highlights

* `id`: Primary key
* `pageId`: index
* `color`: 하이라이트된 색상
* `text`: 하이라이트된 텍스트

```mysql
create table Highlights
(
    id         bigint auto_increment
        primary key,
    created_at datetime(6) not null,
    page_id    bigint      not null,
    color      varchar(10) not null,
    text       text        not null
);

create index Highlights_page_id
    on Highlights (page_id);


```

## 쿼리 플랜

100만 건의 더미 데이터를 생성하고 EXPLAIN 명령어를 사용한 결과 입니다.

### 최초 쿼리 플랜

| id | select\_type       | table    | partitions | type            | possible\_keys                                 | key                | key\_len | ref                      | rows | filtered | Extra                            |
|:---|:-------------------|:---------|:-----------|:----------------|:-----------------------------------------------|:-------------------|:---------|:-------------------------|:-----|:---------|:---------------------------------|
| 1  | PRIMARY            | Pages    | null       | index           | Pages\_user\_id,Pages\_scope                   | PRIMARY            | 8        | null                     | 3    | 100      | Using where; Backward index scan |
| 1  | PRIMARY            | Users    | null       | eq\_ref         | PRIMARY                                        | PRIMARY            | 8        | highlight.Pages.user\_id | 1    | 100      | null                             |
| 2  | DEPENDENT SUBQUERY | Mentions | null       | index\_subquery | Mentions\_page\_id,Mentions\_mention\_user\_id | Mentions\_page\_id | 8        | func                     | 1    | 4.72     | Using where                      |

### 페이징 쿼리 플랜

| id | select\_type | table    | partitions | type    | possible\_keys                                 | key                         | key\_len | ref                      | rows   | filtered | Extra                            |
|:---|:-------------|:---------|:-----------|:--------|:-----------------------------------------------|:----------------------------|:---------|:-------------------------|:-------|:---------|:---------------------------------|
| 1  | PRIMARY      | Pages    | null       | range   | PRIMARY,Pages\_user\_id,Pages\_scope           | PRIMARY                     | 8        | null                     | 497115 | 100      | Using where; Backward index scan |
| 1  | PRIMARY      | Users    | null       | eq\_ref | PRIMARY                                        | PRIMARY                     | 8        | highlight.Pages.user\_id | 1      | 100      | null                             |
| 2  | SUBQUERY     | Mentions | null       | ref     | Mentions\_page\_id,Mentions\_mention\_user\_id | Mentions\_mention\_user\_id | 8        | const                    | 3315   | 100      | null                             |

### 하이라이트 쿼리 플랜

| id | select\_type | table      | partitions | type | possible\_keys       | key                  | key\_len | ref   | rows | filtered | Extra |
|:---|:-------------|:-----------|:-----------|:-----|:---------------------|:---------------------|:---------|:------|:-----|:---------|:------|
| 1  | SIMPLE       | Highlights | null       | ref  | Highlights\_page\_id | Highlights\_page\_id | 8        | const | 5    | 100      | null  |

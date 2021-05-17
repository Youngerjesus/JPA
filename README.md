# JPA(Java Persistent API)

***

## 소개

JPA 는 자바 진영의 ORM 기술 표준이다. 

ORM(Object Relational Mapping) 이란 객체는 객체대로 관계형 DB는 관계형 DB 대로 설계하고 중간에서 매핑 역할을 수행하는 걸 말한다. 
 
JPA 는 그러므로 Java Application 과 JDBC 사이에서 동작한다고 보면 된다. 

***

## 왜 JPA 를 사용해야 하는가 

#### SQL 중심적인 개발에서 객체 중심 적인 개발

JPA 가 등장하기 전에는 객체를 바꾸거나, 추가하는 이런 작업들에 관련해서 SQL 문을 짜야한다.

하지만 JPA 가 등장한 이후로 부터는 객체를 수정하면 그에 알맞게 SQL 쿼리가 나가도록 하는 역할을 JPA 가 수행하므로 객체 중심의 개발이 되었다.

#### 생산성 증가 

매 객체마다 CRUD 작업을 SQL 문으로 만들어줘야 하는데 JPA 를 쓰면 단순화 할 수 있다.

- Create: `jpa.persist(member)`

- Read: `Member member = jpa.find(memberId)` 

- Update: `member.setName("변경할 이름")`

- Delete: `jpa.remove(member)`


#### 패러다임의 불일치 

JPA 가 없다면 객체를 객체답게 모델링 할수록 SQL 문을 매핑해야 하는 작업이 늘어나게 된다. 

하지만 JPA 를 쓴다면 객체를 `List` 와 같은 자바 컬렉션에 넣어주기만 한다면 알아서 DB 에 저장하는 SQL 문으로 바뀌므로 이러한 문제를 해결해준다.


#### 성능

##### 1차 캐시와 동일성 보장 

같은 트랜잭션 안에서는 영속성 컨택스트에서 관리하는 캐싱을 통해서 Entity 를 관리한다. 

그러므로 같은 Entity 를 조회할 때 계속해서 데이터베이스에 접근하지 않는다. 
```java
Long memberId = 1;
Member findMember = jpa.find(Member.class, memberId);  // SQL 문을 통해 Database 에서 직접 가지고온다.  
Member findMember2 = jpa.find(Member.class, memberId); // 영속성 컨텍스트에서 1차 캐시를 반환한다.  
``` 

##### 트랜잭션을 지원하는 쓰기 지연 (Transactional Write-behind)

트랜잭션을 커밋할 때까지 SQL 문을 모아서 한번에 보낸다. 즉 여러번 SQL 문을 보내지 않는다. 이는 JDBC BATCH SQL 기능을 사용해서 가능하다. 

```java
transaction.begin(); 

em.persist(memberA); 
em.persist(memberB); 
em.persist(memberC); // 여기까지 SQL 문을 오운다.

transaction.commit(); // 이때 모아놨던 SQL 문을 한번에 날린다. 
``` 

##### 즉시 로딩과 지연 로딩 

즉시 로딩은 FK로 연결된 객체를 한번에 조회하는 걸 말하고 지연 로딩은 객체가 실제로 연관된 객체를 사용할 때 SQL 문을 날려서 가지고 오는 걸 말한다. 

다음과 같은 Java 코드를 실행한다고 가정해보자.

```java
Member member = em.find(Member.class, memberId); 
Team team = member.getTeam(); 
String teamName = team.getName(); 
```

##### 변경 감지 (Dirty Checking)

영속성 컨택스트에서 관리하는 객체가 변경한 경우 자동으로 업데이트 쿼리를 날려주는 걸 말한다. 

트랜잭션을 커밋하는 순간 1차 캐시에 있는 스냅샷과 기존 객체를 비교해서 차이점이 있다면 자동으로 업데이트 SQL 을 만들어서 Flush 한다.

Flush 란 영속성 컨택스트의 변경 내용을 DB에 반영하는 걸 말한다.   

***

## JPA 구동 방식 

1. application.yml 파일을 조회해서 설정에 맞게 DB를 구성한다.

2. 매번 데이터베이스 커넥션을 생성해주는 EntityManagerFactory 를 그 후 생성하고 데이터베이스에 접근해야 하는 요청이라면 여기서 EntityManager 를 생성해준다. 

3. 요청이 완료될 때(트랜잭션이 끝날 때) EntityManger 를 소멸시킨다.



## 상속관계 매핑 

관계형 데이터베이스에는 상속 관계가 없다. 

하지만 슈퍼타입 서브타입 관계라는 모델링 기법이 객체 상속과 유사하다. 

이를 이용해 객체의 상속 구조와 DB의 슈퍼타입 서브타입 관계를 매핑시킨다. 
  
슈퍼타입 서브타입 관계를 모델링 하는 기법은 크게 3가지 모델이 있다.

- 조인 전략 

  - 서브 타입에 있는 ID를 FK로 슈퍼 타입을 조회할 수 있다. 그러므로 공통된 속성을 슈퍼 타입에 넣고 구별 되는 속성을 서브 타입에 넣는다.  
  
  - 슈퍼 타입과 서브 타입애 총 두번의 Insert 쿼리가 나간다.
  
  - 가장 정규화된 방식으로 볼 수 있다.   

- 단일 테이블 전략 

  - 서브 타입에 있는 컬럼들을 모두 한 테이블에 다 넣는다. 구별 할 떈 타입 칼럼을 통해 구별한다. 
  
-  


## 프록시와 연관관계 


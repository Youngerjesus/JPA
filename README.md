# JPA(Java Persistent API)

***

## 목차 

[1. JPA 란](#JPA-란) <br/>
[2. 왜 JPA 를 사용해야 하는가](#왜-JPA-를-사용해야-하는가) <br/>
[3. JPA 구동 방식](#JPA-구동-방식) <br/>
[4. Entity 매핑](#Entity-매핑) <br/>  
[5. 상속관계 매핑](#상속관계-매핑) <br/> 
[6. MappedSuperclass](#MappedSuperclass) <br/> 
[7. 프록시와 연관관계](#프록시와-연관관계) <br/> 
[8. 즉시 로딩과 지연 로딩](#즉시-로딩과-지연-로딩) <br/> 

***

## JPA 란

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


***

## Entity 매팡

#### 객체와 테이블 매핑

- `@Entity` 가 붙은 클래스는 JPA가 관리하고 DB 테이블과 매핑된다. Entity 의 경우 기본 생성자가 필수이고 final 과 같은 키워드가 있으면 안된다. 

- `@Table` 에노테이션을 통해 Entity 와 매핑할 테이블을 지정할 수 있다. 기본 값은 Entity의 이름이다.  

#### Field 와 Column 매핑

Entity 의 Field 와 DB의 Column 을 매핑할 때 사용하는 여러 에노테이션과 속성이 있다.

예제는 다음과 같다. 

```java
import javax.persistence.*;import java.time.LocalDateTime;import java.util.Date;

@Entity
@Table(name = "MEMBER")
public class Member{
    @Id @GeneratedValue
    private Long id; 
    
    @Column(name = "name", nullable = false)
    private String username;
    
    private Integer age; 
    
    @Enumerated(EnumType.STRING)
    private RoleType roleType; 

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;
    
    private LocalDateTime lastModifiedDate;
    
    @Lob
    private String description;

    @Transient
    private int temp;
}
```

#### @Column

|          속성          |                  설명                   |           기본값           |
| :--------------------: | :-------------------------------------: | :------------------------: |
|          name          |   Field와 매핑할 테이블의 Column 이름   |     객체의 Field 이름      |
| Insertable / updatable |          등록 / 변경 가능 여부          |            true            |
|     nullable(DDL)      |        Null 값 허용 여부를 결정         |            true            |
|      unique(DDL)       | 하나의 Column에 unique 제약 조건을 설정 |                            |
| columnDefinition(DDL)  |       DB Column 정보를 직접 입력        |                            |
|      length(DDL)       | String 타입의 문자 길이 제약 조건 설정  |            255             |
|   precision / scale    |   BigDecimal 타입의 표현 정도를 설정    | precision = 19 / scale = 2 |


#### @Temporal

자바 날짜 타입을 매핑할 때 사용한다. 

최근에는 `LocalDate` 나 `LocalDateTime` 타입을 사용하면 하이버네이트가 지원해주기 떄문에 이 에노테이션을 생략할 수 있다.

| 속성  | 설명                                                         |
| :---: | :----------------------------------------------------------- |
| value | - TemporalType.DATA: 날짜로 DB의 `date` 와 매핑된다. (ex. 2021-02-21)<br />- TemporalType.TIME: 시간으로 DB의 `time` 와 매핑된다. (ex. 08:55:42) <br />- TemporalType.TIMESTAMP: 날짜와 시간으로 DB의 `timestampe` 와 매핑된다. (ex. 2021-01-04 08:55:42) |


#### @Enumerated

자바 `enum` 타입을 매핑할 때 사용한다. 다만 미래에 추가될 요소를 대비해서 공간을 더 쓰더라도 `EnumType.STRING` 을 사용하는 걸 추천한다.

| 속성  | 설명                                                         |      기본값       |
| :---: | :----------------------------------------------------------- | :---------------: |
| value | - EnumType.ORIGINAL: `enum` 순서를 DB에 저장한다 <br />- EnumType.STRING: `enum` 이름을 DB에 저장한다. | EnumType.ORIGINAL |


#### @Lob

DB의 BLOB, CLOB 타입과 매핑된다. 이 에노테이션에는 별도로 지정할 수 있는 속성이 없다. 

매핑하는 Field 타입이 문자라면 CLOB 이고 나머지는 BLOB 으로 매핑된다.

#### @Transient

주로 메모리상에서 임시로 어떤 값을 보관하고 싶은 경우에 매핑은 하고 싶지 않은 경우에 Field 위에다 이 에노테이션을 붙이면 된다. 

***
 


***

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
  
  - 단순화된 구조 떄문에 조회 성능이 빠르지만 null 인 칼럼이 많아서 테이블 크기가 커지고 정규화 문제가 생길 수 있다. 
  
- TABLE PER CLASS 전략 

  - 구현 클래스마다 테이블을 만드는 전략이다. 직접 사용하는 건 권장하지 않는다. 


##### Example
```java

@Entity
@Inheritance(strategy = Inheritance.JOINED)
@DiscriminatorColumn
public abstract class Item{
    @Id @GeneratedValue
    private Long id;
    
    private String name;
    private int price;
    private int stockQuantity;
}

@Entity
@DiscriminatorValue(value = "Album")
public class Album extends Item {
    private String artist;
    private String etc;
}

@Entity
@DiscriminatorValue(value = "Book")
public class Book extends Item {
    private String author;
    private String isbn;
}

@Entity
@DiscriminatorValue(value = "Movie")
public class Movie extends Item {
    private String director;
    private String actor;
}
```

- `@Inheritance` 에노테이션에 설정하는 전략에 따라 상속 매핑 전략을 선택할 수 있다. 

- `@DiscriminatorColumn` 을 부모 Entity 에 설정해서 어떤 자식 Entity 를 나타내는지 알 수 있도록 하는 `DTYPE` 을 설정할 수 있다. 

***

## @MappedSuperclass

공통적인 매핑 정보가 필요할 때 부모 클래스에 이를 넣고 자식 클래스는 이 정보를 가져와서 사용하도록 쓸 수 있다.  

##### Example

````java
public class BaseEntity{
    private String createdBy; 
    private LocalDateTime createdDate;
    private String lastModifiedBy; 
    private LocalDateTime lastModifiedDate;   
}

@Entity
public class Member extends BaseEntity{...}
````

***

## 프록시와 연관관계 

JPA의 즉시 로딩과 지연 로딩을 이해하기 위해서는 프록시를 이해해야 한다. 

#### 프록시 

프록시 클래스는 실제 클래스를 상속 받아서 만들어지며 겉 모양이 같다. 

다만 실제 값이 필요할 때까지 DB 조회를 미루는 방식으로 한 Entity 와 연관된 다른 Entity 를 모두 가져올 필요가 없을 때 프록시를 사용한다. 

프록시 객체는 실제 객체의 참조를 보관해서 애플리케이션에서 프록시 객체를 호출하면 프록시 객체는 실제 객체의 메소드를 호출한다. 

아래는 프록시 객체를 활용한 예제다.

```java
Team team = new Team();
team.setName("teamA");

Member member = new Member();
member.setName("memberA");
member.setTeam(team);

em.persist(team);
em.persist(member);

em.flush();		// SQL 보냄
em.clear();		// 영속성 Context 1차 캐시 초기화

Member referMember = em.getReference(Member.class, member.getId());		// 프록시 객체 가져옴
System.out.println("referMember = " + referMember.getId() + ": " + referMember.getName());
```

- `em.getReference()` 를 통해서 프록시 객체를 가지고온다. 

- `referMember.getId()` 는 기존에 메모리에 있던 값이기 때문에 DB에 조회를 날리지 않는다. 

- `referMember.getName()` 을 실행할 때 실제 DB에서 값을 가져온다. 


프록시의 특징은 다음과 같다.

- 프록시 객체는 처음 사용할 때 한 번만 초기화 된다.

- 프록시 객체를 초기화 할 떈 프록시 객체가 실제 Entity 로 바뀌는 게 아닌 참조를 통해서 동작한다.

- 프록시 객체는 원본 Entity 를 상속받는 상태이므로 타입 체크시 `==` 대신 `instance of` 을 사용하는 것이 좋다.

  - 영속성 컨택스트에 이미 Entity 가 캐싱되어 있다면 `em.getReference()` 하더라도 실제 Entity 가 반환된다.
  
  - 영속성 컨택스트가 관리하는 객체가 아니라면 (if detached) 라면 이때 프록시 객체를 초기화 하려면 `LazyInitializationException` 예외가 발생한다.

***

## 즉시 로딩과 지연 로딩

Member Entity 와 Team Entity 가 있고 이 관계는 N:1 이라고 가정해보자.

비즈니스 로직 상에서 Member 를 조회하는데 Team 까지 같이 조회가 된다면 성능상에서 손해다. 

그러므로 이를 지연 로딩으로 가져오도록 설정해놓는게 낫다. 그러면 연관되어 있는 객체를 프록시로 되어있고 실제 사용이 필요한 시점에 쿼리를 날려서 가지고 올 것이다.

실무에서는 가급적 지연 로딩만 사용하는게 권장된다. JPA 에서 `ManyToOne` 과 `OneToMany` 는 기본 값이 즉시 로딩이므로 지연 로딩을 써서 설정하는게 좋다.

***
 


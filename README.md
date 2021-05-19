# JPA(Java Persistent API)

***

## 목차 

[1. JPA 란](#JPA-란) <br/>
[2. 왜 JPA 를 사용해야 하는가](#왜-JPA-를-사용해야-하는가) <br/>
[3. JPA 구동 방식](#JPA-구동-방식) <br/>
[4. Entity 매핑](#Entity-매핑) 
[5. 기본 Key 매핑](#기본-Key-매핑) <br/> 
[6. 상속관계 매핑](#상속관계-매핑) <br/> 
[7. MappedSuperclass](#@MappedSuperclass) <br/> 
[8. 프록시와 연관관계](#프록시와-연관관계) <br/> 
[9. 즉시 로딩과 지연 로딩](#즉시-로딩과-지연-로딩) <br/>
[10. 영속성 전이](#영속성-전이) <br/> 
[11. 고아 객체](#고아-객체) <br/> 
[12. 값 타입](#값-타입) <br/> 
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

## Entity 매핑

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
 
## 기본 Key 매핑 

Entity 를 식별할 수 있는 Key 를 매핑할 때 사용할 수 있는 에노테이션으로 (@Id, @GeneratedValue) 를 쓰는 전략이 있다. 

기본 키 직접 매핑 방법은 직접 Id 를 할당해주기 위해서 @Id 만 쓰고 @GeneratedValue 를 쓰지 않는 방법이 있고 

자동으로 ID 를 생성하는 방법으로 @GeneratedValue 에는 여러가지 전략이 있다.

- `GenerationType.IDENTITY`  
  
  - 기본 키 생성을 데이터베이스에 위임한다. MySQL 에 AUTO_INCREMENT 와 같다. 
  
  - 다만 여기서 문제는 해당 Entity 를 DB에 저장하기 전까지 기본 Key를 알 수 없다는 점이다. JPA는 트랜잭션을 commit 하기 전까지 1차 캐시에 Entity를 보관하는데 IDENTITY 전략으로 Key가 자동 생성 된다면
  
  commit 전까지 키를 알 수 없어서 1차 캐시를 적극적으로 활용할 수 없다. (1차 캐시는 Id를 키로 Entity를 매핑하기 떄문에) 따라서 이 전략으로 설정한 경우 예외적으로 persist() 메소드를 호출한다면 바로 SQL문을 내보내서 Key를 받아온다.
  
- `GenerationType.SEQUENCE`
  
  - Oracle 데이터베이스에서 사용하는 방법이다 이 전략도 DB에서 관리하므로 DB에 직접 가야지 Id 값을 나와야 한다. 차이점은 insert 쿼리를 날리는게 아니라 Sequence 를 얻기 위해서 DB에 접근한다.
  
  - 그리고 또 다른 차이점은 @SequenceGenerator 에 있는 allocationSize 인데. 이는 Sequence 값을 매번 가지고 오는게 아니라 이 사이즈 만큼 값을 가지고 오고 메모리에 저장해둔다. 그리고 이 값을 다 쓰면 새로 DB에 접근해서 가지고 오는 방식을 적용한다. 
    
  - 예제는 다음과 같다. 
  
```java
  
@Entity
@SequenceGenerator(
        name = "MEMBER_SEQ_GENERATOR",
        sequenceName = "MEMBER_SEQ",
        initialValue = 1,
        allocationSize = 1
)
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "MEMBER_SEQ_GENERATOR")
    Long id;

    String name;
} 
```

- `GenerationType.TABLE`

  - 키 생성 전용 테이블을 하나 만들어서 데이터베이스 시퀸스를 흉내내는 전략 
  
  - 장점으로는 모든 데이터베이스에 적용이 가능하지만 테이블을 별도로 사용하니까 성능으로는 안나온다.   

- `GenerationType.AUTO`

  - DB에 맞춰서 자동으로 생성해주는 것 Oracle 이면 SEQUENCE 로 MySQL 이면 IDENTITY 로 만들어준다. 

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
@MappedSuperclass
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
 
## 영속성 전이 

특정 엔터티를 영속 상태로 만들 때 연관된 엔터티도 함께 영속 상태로 만들고 싶을 때 사용한다.

영속성 전이는 연관관계를 매핑하는 것과 아무 관련이 없다. 엔터티를 영속화할 때 연관된 엔터티도 함께 영속화하는 편리함만 제공해준다. 

영속성 전이는 하나의 부모가 자식들을 관리할 때 의미가 있다. 게시판에 하나의 글이 첨부 파일을 관리하는 경우 하지만 관리하는 곳이 여러 곳이 있다면 쓰면 안된다.  

단일 엔터티에 완전히 종속적일때 사용한다. 

종류는 다음과 같다. 

- CascadeType.All

- CascadeType.PERSIST

- CascadeType 

 
```java
import javax.persistence.CascadeType;import javax.persistence.OneToMany;

@OneToMany(mappedBy = "parent", cascade = CascadeType.PERSIST) 
```

***

## 고아 객체

부모 엔터티와 연관관계가 끊어진 자식 엔터리를 자동으로 삭제하는 기능이다. 

영속성 전이와 고아 객체의 두 개념을 모두 사용하면 부모 Entity를 통해서 자식의 생명주기를 관리할 수 있게 되어 도메인 주도 설계의 Aggregate Root 개념을 구현할 때 유용하다. 

```java
import javax.persistence.CascadeType;import javax.persistence.OneToMany;

@OneToMany(mappedBy = "parent", cascade = CascadeType.PERSIST, orphanRemoval = true) 
```

***

## 값 타입

JPA 는 엔터티 타입과 값 타입으로 구별한다. 

값 타입은 복잡한 객체 세상을 조금이라도 단순하려고 만든 개념이다. 

##### 엔터티 타입

- @Entity 로 정의하는 객체를 말하며 데이터가 변해도 추적이 가능하다. Id를 통해서 

##### 값 타입

-  int, Integer, String 처럼 단순 값으로 사용하는 자바 타입이나 객체를 말하고 식별자가 없다. 그러므로 변경시에 추적이 불가능하다.


그리고 값 타입에는 크게 3가지 종류가 있다. 기본 값 타입, 임베디드 타입, 컬렉션 값 타입 이렇게 있다.

- ##### 기본 값 타입

  - 자바의 기본 primitive type 을 말하거나 Wrapper Class, String 같은 걸 말한다. 
  
  - 생명주기는 엔터티에 의존하다. 
 
- ##### 임베디드 타입(embedded type)

  - 자바의 기본 커스텀 클래스 같은 걸 말한다.  
  
  - 사용 방법은 다음과 같다. 
  
    - `@Embeddable` : 값 타입을 정의하는 곳에 표시한다.
    
    - `@Embedded` : 값 타입을 사용하는 곳에 표시한다.
    
    - 값 타입은 기본적인 생성자가 필수적이다.
    
  - 값 타입을 이용하면 연관성이 있는 걸 모을 수 있으니까 응집도가 높다. 그리고 값 타입만 사용하는 유의미한 메소드를 만들 수 있다.
  
  - 임베디드 타입을 포함한 모든 값 타입은 값 타입을 소유한 엔터티 생명주기에 의존하다. 즉 엔터티가 지워지면 값 타입도 같이 지워진다.
   
  - 임베디드 타입에 다른 엔터티를 넣어서 사용한는 것도 가능하다.
  
  - 주의할 점은 같은 임베디드 타입을 두개 이상 사용하면 에러가 난다. 에러를 해결하기 위해서는 `@AttributeOverrides` 를 이용해서 컬럼명 속성 이름을 재정의하면 된다.
  
  - 그리고 임베디드 타입 같은 값 타입을 여러 엔터티에서 공유하면 위험하다. 값을 복사하는게 아니라 레퍼런스를 복사하는거니까. 
  
  - 값 타입을 비교할 떈 `equals()` 메소드와 `hashCode()` 메소드를 재정의해야한다. == 은 레퍼런스를 비교하는거니까. 
  
- ##### 컬렉션 값 타입(collection value type) 

  - 자바의 컬렉션 타입의 클래스들을 말한다. 
  
  - 컬렉션을 저장하기 위해서는 별도의 테이블로 빼야한다. `@OneToMany` 와의 차이점은 엔터티의 라이프 사이클에 의존하게 되기 않을까 
  
  - 값 타입 컬렉션은 지연 로딩 전략을 사용한다. 
  
  - 값 타입 컬렉션은 영속성 전이 + 고아 객체 제거 기능을 필수로 가진다고 생각하면 된다.
  
  - 값 타입을 사용할 땐 객체를 참조해서 바꾸면 사이드 이펙트가 일어날 수 있기 떄문에 변경할 땐 늘 새로운 객체를 넣어주도록 하자. 
  값 타입에는 `set()` 메소드를 private 으로 하자. 즉 불변 객체로 설정하도록 
   

***


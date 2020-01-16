## sl4bv

### 介绍
说明：sl4bv中bv的全称为Bean Validator

目的：基于hibernate-validator实现针对Bean的通用校验

> 备注：
> 
> Hibernate Validator 是 Bean Validation 的参考实现 . 
> 
> Hibernate Validator 提供了 JSR 303 规范中所有内置 constraint 的实现，除此之外还有一些附加的 constraint。
>
> 参见官网：https://jcp.org/en/jsr/detail?id=303


### 一、Bean Validation 中内置的 constraint

约束注解名称		约束注解说明
---------------------------------------------------
@Null			验证对象是否为空

@NotNull		验证对象是否为非空

@AssertTrue		验证 Boolean 对象是否为 true

@AssertFalse	验证 Boolean 对象是否为 false

@Min			验证 Number 和 String 对象是否大等于指定的值

@Max			验证 Number 和 String 对象是否小等于指定的值

@DecimalMin		验证 Number 和 String 对象是否大等于指定的值，小数存在精度

@DecimalMax		验证 Number 和 String 对象是否小等于指定的值，小数存在精度

@Size			验证对象（Array,Collection,Map,String）长度是否在给定的范围之内

@Digits			验证 Number 和 String 的构成是否合法 【验证是否是符合指定格式的数字,限制必须为一个小数，且整数部分的位数不能超过integer，小数部分的位数不能超过fraction】

@Past			验证 Date 和 Calendar 对象是否在当前时间之前

@Future			验证 Date 和 Calendar 对象是否在当前时间之后

@Pattern		验证 String 对象是否符合正则表达式的规则


### 二、Hibernate Validator 附加的 constraint

约束注解名称		约束注解说明
---------------------------------------------------
@Email        被注释的元素必须是电子邮箱地址

@Length       被注释的字符串的大小必须在指定的范围内

@NotEmpty     被注释的字符串的必须非空

@Range        被注释的元素必须在合适的范围内


### 三、自定义Validator 附加的 constraint

Constraint    详细信息
---------------------------------------------------

注：

1.多值约束（Multiple Constraints）

> 对于同一个目标元素，在进行约束注解声明时可以同时使用不同的属性达到对该目标元素进行多值验证的目的。
> 实现多值约束只需要在定义约束注解的同时定义一个 List（@interface List{}）。
> 使用该约束注解时，Bean Validation 将 value 数组里面的每一个元素都处理为一个普通的约束注解，
> 并对其进行验证，所有约束条件均符合时才会验证通过。

```java
@@Pattern.List(value = {
            @Pattern(regexp = yyyyMMddHHmmss, message = "startDate格式必须为yyyyMMddHHmmss"),
            @Pattern(regexp = yyyy_MM_dd_HH_mm_ss, message = "startDate格式必须为yyyy_MM_dd_HH_mm_ss") })
```
            

2.Object Graph 验证

> Bean Validation 规范同样支持 Object Graph 的验证。
> 
> Object Graph 即为对象的拓扑结构，如对象之间的引用关系。
> 
> 如果类 A 引用类 B，则在对类 A 的实例进行约束验证时也需要对类 B 的实例进行约束验证，这就是验证的级联性。
> 
> 当对 Java 语言中的集合、数组等类型进行验证时也需要对该类型的每一个元素进行验证。
> 
> 完成级联验证的方式就是使用 @Valid 注解.


3.组

> 组定义了约束的子集。
> 
> 对于一个给定的 Object Graph 结构，有了组的概念，则无需对该 Object Graph 中所有的约束进行验证，只需要对该组定义的一个子集进行验证即可。
> 完成组别验证需要在约束声明时进行组别的声明，否则使用默认的组 Default.class.[组使用接口的方式进行定义.]
> 需要注意的是：组也有继承的属性。对某一组别进行约束验证的时候，也会对其所继承的基类进行验证。
> 
```java
 public interface GroupA {  } 
```
> 
> 组可以进行隐式定义，其好处是可以不必在约束声明的时候显式声明组别属性.
> 
```java
 public interface Animal { 
     @NotEmpty String getName(); 
     @NotEmpty String getOwnerName(); 
 } 
```
 
4.组序列

> 默认情况下，不同组别的约束验证是无序的，然而在某些情况下，约束验证的顺序却很重要
> 
> 例子：某个组的验证比较耗时，CPU 和内存的使用率相对比较大，最优的选择是将其放在最后进行验证。因此，在进行组验证的时候尚需提供一种有序的验证方式，这就提出了组序列的概念
> 
> 一个组可以定义为其他组的序列，使用它进行验证的时候必须符合该序列规定的顺序。在使用组序列验证的时候，如果序列前边的组验证失败，则后面的组将不再给予验证。

```java
public interface GroupA {  } 

public interface GroupB {  } 

@GroupSequence({Default.class, GroupA.class, GroupB.class}) 

public interface Group {  } 
```
 
5.穿透验证器

> 穿透验证器主要适用于 JPA 规范，JPA 规范提供一种惰性连接属性，允许实体对象的某些字段被延迟加载，
> 
> 这些被延迟加载的字段需要 JPA 从底层数据库中获取。Bean Validation 规范通过 TraversableResolver 接口来控制这类字段的存取性。
> 
> 在实际使用中需要先调用该接口中的 isReachable() 方法，如果返回 true，则证明该属性是可存取的，方可进行属性的约束验证。
> 
> 同样，在进行级联验证时，也需要首先确定所引用的字段或者属性的可存取性方可进行约束的级联验证。

6.Bean Validation 规范接口及其可扩展的实现

> Bean Validation 规范允许用户定制个性化的约束验证，并给出了 4 大类接口供扩展使用。
> 
> 本章将结合 Bean Validation 规范的参考实现 Hibernate Validator4.0 进行说明。

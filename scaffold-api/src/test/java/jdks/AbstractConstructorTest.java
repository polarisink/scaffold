package jdks;

abstract class Animal {

  Animal() {
    System.out.println("抽象类Animal无参构造器"); //此处执行前会默认执行super()
  }

  Animal(int a) {
    System.out.println("抽象类Animal有参构造器");
  }
}

/**
 * 虽然不能直接实例化抽象类，但子类init时自身的cinit会被调用
 */
public class AbstractConstructorTest extends Animal {

  AbstractConstructorTest() {
    System.out.println("子类horse无参构造器"); //此处执行前会默认执行super()
  }

  AbstractConstructorTest(int h) {
    super(3);
    System.out.println("子类horse有参构造器");
  }

  public static void main(String[] args) {
    AbstractConstructorTest h = new AbstractConstructorTest();
    System.out.println("---------------------");
    Animal h2 = new AbstractConstructorTest(6);
    //		Animal h3 = new Animal(); //无法编译，抽象类不可实例化
  }
}
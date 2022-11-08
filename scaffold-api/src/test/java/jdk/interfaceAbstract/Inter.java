package jdk.interfaceAbstract;

/**
 * @author aries
 * @date 2022/11/8
 */
public interface Inter {

  default void sing() {
    System.out.println("inter sing...");
  }

  void jump();
}

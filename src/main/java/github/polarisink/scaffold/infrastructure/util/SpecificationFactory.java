package github.polarisink.scaffold.infrastructure.util;

import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collection;

public final class SpecificationFactory {
   /**
    * 模糊查询，匹配对应字段
    */
   public static Specification containsLike(String attribute, String value) {
      return (root, query, cb) -> cb.like(root.get(attribute), "%" + value + "%");
   }

   /**
    * 某字段的值等于 value 的查询条件
    */
   public static Specification equal(String attribute, Object value) {
      return (root, query, cb) -> cb.equal(root.get(attribute), value);
   }

   /**
    * 获取对应属性的值所在区间
    */
   public static Specification isBetween(String attribute, int min, int max) {
      return (root, query, cb) -> cb.between(root.get(attribute), min, max);
   }

   public static Specification isBetween(String attribute, double min, double max) {
      return (root, query, cb) -> cb.between(root.get(attribute), min, max);
   }

   public static Specification isBetween(String attribute, LocalDateTime min, LocalDateTime max) {
      return (root, query, cb) -> cb.between(root.get(attribute), min, max);
   }

   /**
    * 通过属性名和集合实现 in 查询
    */
   public static Specification in(String attribute, Collection c) {
      return (root, query, cb) -> root.get(attribute).in(c);
   }

   /**
    * 通过属性名构建大于等于 Value 的查询条件
    */
   public static Specification greaterThan(String attribute, BigDecimal value) {
      return (root, query, cb) -> cb.greaterThan(root.get(attribute), value);
   }

   public static Specification greaterThan(String attribute, Long value) {
      return (root, query, cb) -> cb.greaterThan(root.get(attribute), value);
   }
}
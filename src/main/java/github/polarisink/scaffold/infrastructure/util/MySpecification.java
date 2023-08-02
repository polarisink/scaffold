package github.polarisink.scaffold.infrastructure.util;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.CollectionUtils;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * 扩展Specification
 *
 * @param <T>
 */
public class MySpecification<T> implements Specification<T> {
    /**
     * 属性分隔符
     */
    private static final String PROPERTY_SEPARATOR = ".";
    /**
     * and条件组
     */
    List<Cnd> andConditions = new ArrayList<>();
    /**
     * or条件组
     */
    List<Cnd> orConditions = new ArrayList<>();
    /**
     * 排序条件组
     */
    List<Order> orders = new ArrayList<>();

    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> cq, CriteriaBuilder cb) {
        Predicate restrictions = cb.and(getAndPredicates(root, cb));
        restrictions = cb.and(restrictions, getOrPredicates(root, cb));
        cq.orderBy(getOrders(root, cb));
        return restrictions;
    }

    public MySpecification<T> and(Cnd... conditions) {
        Collections.addAll(andConditions, conditions);
        return this;
    }

    public MySpecification<T> or(Collection<Cnd> conditions) {
        orConditions.addAll(conditions);
        return this;
    }

    public MySpecification<T> desc(String property) {
        this.orders.add(Order.desc(property));
        return this;
    }

    public MySpecification<T> asc(String property) {
        this.orders.add(Order.asc(property));
        return this;
    }

    private Predicate getAndPredicates(Root<T> root, CriteriaBuilder cb) {
        Predicate restrictions = cb.conjunction();
        for (Cnd condition : andConditions) {
            if (condition == null) {
                continue;
            }
            Path<?> path = this.getPath(root, condition.property);
            if (path == null) {
                continue;
            }
            switch (condition.operator) {
                case eq -> {
                    if (condition.value != null) {
                        if (String.class.isAssignableFrom(path.getJavaType()) && condition.value instanceof String) {
                            if (!((String) condition.value).isEmpty()) {
                                restrictions = cb.and(restrictions, cb.equal(path, condition.value));
                            }
                        } else {
                            restrictions = cb.and(restrictions, cb.equal(path, condition.value));
                        }
                    }
                }
                case ge -> {
                    if (Number.class.isAssignableFrom(path.getJavaType()) && condition.value instanceof Number) {
                        restrictions = cb.and(restrictions, cb.ge((Path<Number>) path, (Number) condition.value));
                    }
                }
                case gt -> {
                    if (Number.class.isAssignableFrom(path.getJavaType()) && condition.value instanceof Number) {
                        restrictions = cb.and(restrictions, cb.gt((Path<Number>) path, (Number) condition.value));
                    }
                }
                case lt -> {
                    if (Number.class.isAssignableFrom(path.getJavaType()) && condition.value instanceof Number) {
                        restrictions = cb.and(restrictions, cb.lt((Path<Number>) path, (Number) condition.value));
                    }
                }
                case ne -> {
                    if (condition.value != null) {
                        if (String.class.isAssignableFrom(path.getJavaType()) && condition.value instanceof String && !((String) condition.value).isEmpty()) {
                            restrictions = cb.and(restrictions, cb.notEqual(path, condition.value));
                        } else {
                            restrictions = cb.and(restrictions, cb.notEqual(path, condition.value));
                        }
                    }
                }
                case isNotNull -> restrictions = cb.and(restrictions, path.isNotNull());
            }
        }
        return restrictions;
    }

    private Predicate getOrPredicates(Root<T> root, CriteriaBuilder cb) {
        // 相同的逻辑 Need TODO
        return null;
    }

    private List<javax.persistence.criteria.Order> getOrders(Root<T> root, CriteriaBuilder cb) {
        List<javax.persistence.criteria.Order> orderList = new ArrayList<>();
        if (root == null || CollectionUtils.isEmpty(orders)) {
            return orderList;
        }
        for (Order order : orders) {
            if (order == null) {
                continue;
            }
            String property = order.getProperty();
            Sort.Direction direction = order.getDirection();
            Path<?> path = this.getPath(root, property);
            if (path == null || direction == null) {
                continue;
            }
            switch (direction) {
                case ASC -> orderList.add(cb.asc(path));
                case DESC -> orderList.add(cb.desc(path));
            }
        }
        return orderList;
    }

    /**
     * 获取Path
     *
     * @param path         Path
     * @param propertyPath 属性路径
     * @return Path
     */
    private <X> Path<X> getPath(Path<?> path, String propertyPath) {
        if (path == null || StringUtils.isEmpty(propertyPath)) {
            return (Path<X>) path;
        }
        String property = StringUtils.substringBefore(propertyPath, PROPERTY_SEPARATOR);
        return getPath(path.get(property), StringUtils.substringAfter(propertyPath, PROPERTY_SEPARATOR));
    }

    /**
     * 条件
     */
    public static class Cnd {
        Operator operator;
        String property;
        Object value;

        public Cnd(String property, Operator operator, Object value) {
            this.operator = operator;
            this.property = property;
            this.value = value;
        }

        /**
         * 相等
         *
         * @param property
         * @param value
         * @return
         */
        public static Cnd eq(String property, Object value) {
            return new Cnd(property, Operator.eq, value);
        }

        /**
         * 不相等
         *
         * @param property
         * @param value
         * @return
         */
        public static Cnd ne(String property, Object value) {
            return new Cnd(property, Operator.ne, value);
        }
    }

    /**
     * 排序
     */
    @Getter
    @Setter
    public static class Order {
        private String property;
        private Sort.Direction direction;

        /**
         * 构造方法
         *
         * @param property  属性
         * @param direction 方向
         */
        public Order(String property, Sort.Direction direction) {
            this.property = property;
            this.direction = direction;
        }

        /**
         * 返回递增排序
         *
         * @param property 属性
         * @return 递增排序
         */
        public static Order asc(String property) {
            return new Order(property, Sort.Direction.ASC);
        }

        /**
         * 返回递减排序
         *
         * @param property 属性
         * @return 递减排序
         */
        public static Order desc(String property) {
            return new Order(property, Sort.Direction.DESC);
        }
    }

    /**
     * 运算符
     */
    @Getter
    public enum Operator {
        /**
         * 等于
         */
        eq(" = "),
        /**
         * 不等于
         */
        ne(" != "),
        /**
         * 大于
         */
        gt(" > "),
        /**
         * 小于
         */
        lt(" < "),
        /**
         * 小于等于
         */
        le(" < "),
        /**
         * 大于等于
         */
        ge(" >= "),
        /**
         * 不为Null
         */
        isNotNull(" is not NULL ");

        Operator(String operator) {
            this.operator = operator;
        }

        private final String operator;
    }
}
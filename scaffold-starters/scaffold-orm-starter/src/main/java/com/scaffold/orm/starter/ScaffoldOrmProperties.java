package com.scaffold.orm.starter;

import com.baomidou.mybatisplus.annotation.DbType;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Scaffold ORM 配置。
 *
 * @param pagination 分页配置
 */
@ConfigurationProperties(prefix = "scaffold.orm")
public record ScaffoldOrmProperties(Pagination pagination) {

    public ScaffoldOrmProperties {
        pagination = pagination == null ? new Pagination(null, null, null, null) : pagination;
    }

    /**
     * @param enabled  是否启用 MyBatis-Plus 分页插件
     * @param dbType   数据库类型，为空时根据当前数据源动态识别
     * @param overflow 页码超出总页数时是否回到第一页
     * @param maxLimit 单页最大记录数，默认为 500
     */
    public record Pagination(Boolean enabled, DbType dbType, Boolean overflow, Long maxLimit) {

        public Pagination {
            enabled = enabled == null || enabled;
            overflow = overflow != null && overflow;
            maxLimit = maxLimit == null ? 500L : maxLimit;
        }
    }
}

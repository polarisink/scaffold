# RBAC 模块

RBAC 业务能力已拆为公共数据库层和两套认证实现，便于按项目技术栈选择。

```text
scaffold-module/
├── scaffold-module-rbac-data                    # 公共数据库层
├── scaffold-module-rbac-security-servlet        # Spring Security Servlet 完整 RBAC
├── scaffold-module-rbac-sa-token-servlet        # Sa-Token Servlet 完整 RBAC
├── scaffold-module-rbac-auth-security-webflux   # Spring Security WebFlux 认证模块
└── scaffold-module-rbac-auth-sa-webflux         # Sa-Token WebFlux 认证模块
```

## 模块说明

### scaffold-module-rbac-data

公共数据库层，包含：

- `SysUser`、`SysRole`、`SysMenu`、`SysUserRole`、`SysRoleMenu`
- MyBatis Plus mapper
- `RbacAccountService`，提供用户名密码校验和角色编码查询

该模块不绑定 Spring Security 或 Sa-Token，可被 Servlet 与 WebFlux 认证服务复用。

### scaffold-module-rbac-security-servlet

Spring Security Servlet 版本，依赖：

- `scaffold-module-rbac-data`
- `scaffold-starter-spring-security-servlet`

适合普通 Spring Boot MVC 项目使用。

### scaffold-module-rbac-sa-token-servlet

Sa-Token Servlet 版本，依赖：

- `scaffold-module-rbac-data`
- `scaffold-starter-sa-token-servlet`

适合希望使用 Sa-Token 的 Spring Boot MVC 项目。

### scaffold-module-rbac-auth-security-webflux

Spring Security WebFlux 认证模块，依赖：

- `scaffold-module-rbac-data`
- `scaffold-starter-spring-security-webflux`

只提供认证相关接口，不复制用户、角色、菜单管理 CRUD。

### scaffold-module-rbac-auth-sa-webflux

Sa-Token WebFlux 认证模块，依赖：

- `scaffold-module-rbac-data`
- `scaffold-starter-sa-token-webflux`

只提供认证相关接口，不复制用户、角色、菜单管理 CRUD。

## 接口

Servlet 完整 RBAC 模块提供：

- `POST /auth/login`
- `GET /auth/logout`
- `/user/**`
- `/role/**`
- `/menu/**`

WebFlux 认证模块提供：

- `POST /auth/login`
- `POST /auth/logout`
- `GET /auth/token-info`

## 选择建议

- 项目要求 Spring Security：引入 `scaffold-module-rbac-security-servlet`
- 项目要求 Sa-Token：引入 `scaffold-module-rbac-sa-token-servlet`
- 微服务 Spring Security WebFlux 认证服务：引入 `scaffold-module-rbac-auth-security-webflux`
- 微服务 Sa-Token WebFlux 认证服务：引入 `scaffold-module-rbac-auth-sa-webflux`

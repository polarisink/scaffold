# RBAC 模块

RBAC 业务能力已拆为公共管理 module 和两套认证实现，便于按项目技术栈选择。

```text
scaffold-module/
├── scaffold-module-rbac-data                    # 数据模型与公共管理用例
├── scaffold-module-rbac-security                # Spring Security Servlet 完整 RBAC
└── scaffold-module-rbac-sa-token                # Sa-Token Servlet 完整 RBAC
```

## 模块说明

### scaffold-module-rbac-data

公共 RBAC module，包含：

- `SysUser`、`SysRole`、`SysMenu`、`SysUserRole`、`SysRoleMenu`
- MyBatis Plus mapper
- `RbacAccountService`，提供用户名密码校验和角色编码查询
- 唯一的 `SysUserService` 用户管理实现
- `RbacCurrentUser` 与 `RbacSessionRevoker` seam

该模块不绑定 Spring Security 或 Sa-Token，可被不同 Servlet 认证实现复用。

### scaffold-module-rbac-security

Spring Security Servlet 版本，依赖：

- `scaffold-module-rbac-data`
- `scaffold-spring-security-starter`

本模块提供 Spring Security 的当前用户与会话失效 adapter，以及登录、登出流程。

适合普通 Spring Boot MVC 项目使用。

### scaffold-module-rbac-sa-token

Sa-Token Servlet 版本，依赖：

- `scaffold-module-rbac-data`
- `scaffold-sa-token-starter`

本模块提供 Sa-Token 的当前用户与会话失效 adapter，以及登录、登出流程。

适合希望使用 Sa-Token 的 Spring Boot MVC 项目。

## 接口

Servlet 完整 RBAC 模块提供：

- `POST /auth/login`
- `POST /auth/logout`
- `/user/**`
- `/role/**`
- `/menu/**`

## 选择建议

- 项目要求 Spring Security：引入 `scaffold-module-rbac-security-servlet`
- 项目要求 Sa-Token：引入 `scaffold-module-rbac-sa-token-servlet`

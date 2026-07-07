-- scaffold-biz 基础 RBAC 数据。
-- 本脚本在 Hibernate 完成表结构更新后执行，并通过业务唯一键保证可重复执行。

-- 默认组织
INSERT INTO sys_org
(parent_id, org_name, org_code, sort, deleted, gmt_created, gmt_modified)
SELECT 0, '总部', 'HEADQUARTERS', 0, 0, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1
                  FROM sys_org
                  WHERE org_code = 'HEADQUARTERS'
                    AND deleted = 0);

-- 一级菜单
INSERT INTO sys_menu
(parent_id, menu_name, path, menu_type, menu_url, menu_icon_url, sort_no, deleted, gmt_created, gmt_modified)
SELECT 0,
       '仪表盘',
       '/dashboard',
       0,
       NULL,
       'lucide:layout-dashboard',
       -1,
       0,
       NOW(),
       NOW()
WHERE NOT EXISTS (SELECT 1
                  FROM sys_menu
                  WHERE path = '/dashboard'
                    AND deleted = 0);

INSERT INTO sys_menu
(parent_id, menu_name, path, menu_type, menu_url, menu_icon_url, sort_no, deleted, gmt_created, gmt_modified)
SELECT 0,
       '系统管理',
       '/system',
       0,
       NULL,
       'lucide:settings-2',
       10,
       0,
       NOW(),
       NOW()
WHERE NOT EXISTS (SELECT 1
                  FROM sys_menu
                  WHERE path = '/system'
                    AND deleted = 0);

-- 仪表盘子菜单
INSERT INTO sys_menu
(parent_id, menu_name, path, menu_type, menu_url, menu_icon_url, sort_no, deleted, gmt_created, gmt_modified)
SELECT parent.id,
       '分析页',
       '/dashboard/analytics',
       1,
       'dashboard:analytics:view',
       'lucide:area-chart',
       0,
       0,
       NOW(),
       NOW()
FROM sys_menu parent
WHERE parent.path = '/dashboard'
  AND parent.deleted = 0
  AND NOT EXISTS (SELECT 1
                  FROM sys_menu
                  WHERE path = '/dashboard/analytics'
                    AND deleted = 0)
LIMIT 1;

INSERT INTO sys_menu
(parent_id, menu_name, path, menu_type, menu_url, menu_icon_url, sort_no, deleted, gmt_created, gmt_modified)
SELECT parent.id,
       '工作台',
       '/dashboard/workspace',
       1,
       'dashboard:workspace:view',
       'carbon:workspace',
       10,
       0,
       NOW(),
       NOW()
FROM sys_menu parent
WHERE parent.path = '/dashboard'
  AND parent.deleted = 0
  AND NOT EXISTS (SELECT 1
                  FROM sys_menu
                  WHERE path = '/dashboard/workspace'
                    AND deleted = 0)
LIMIT 1;

-- 系统管理子菜单
INSERT INTO sys_menu
(parent_id, menu_name, path, menu_type, menu_url, menu_icon_url, sort_no, deleted, gmt_created, gmt_modified)
SELECT parent.id,
       '用户管理',
       '/system/user',
       1,
       'system:user:view',
       'lucide:users',
       0,
       0,
       NOW(),
       NOW()
FROM sys_menu parent
WHERE parent.path = '/system'
  AND parent.deleted = 0
  AND NOT EXISTS (SELECT 1
                  FROM sys_menu
                  WHERE path = '/system/user'
                    AND deleted = 0)
LIMIT 1;

INSERT INTO sys_menu
(parent_id, menu_name, path, menu_type, menu_url, menu_icon_url, sort_no, deleted, gmt_created, gmt_modified)
SELECT parent.id,
       '角色管理',
       '/system/role',
       1,
       'system:role:view',
       'lucide:badge-check',
       10,
       0,
       NOW(),
       NOW()
FROM sys_menu parent
WHERE parent.path = '/system'
  AND parent.deleted = 0
  AND NOT EXISTS (SELECT 1
                  FROM sys_menu
                  WHERE path = '/system/role'
                    AND deleted = 0)
LIMIT 1;

INSERT INTO sys_menu
(parent_id, menu_name, path, menu_type, menu_url, menu_icon_url, sort_no, deleted, gmt_created, gmt_modified)
SELECT parent.id,
       '组织管理',
       '/system/org',
       1,
       'system:org:view',
       'lucide:network',
       20,
       0,
       NOW(),
       NOW()
FROM sys_menu parent
WHERE parent.path = '/system'
  AND parent.deleted = 0
  AND NOT EXISTS (SELECT 1
                  FROM sys_menu
                  WHERE path = '/system/org'
                    AND deleted = 0)
LIMIT 1;

INSERT INTO sys_menu
(parent_id, menu_name, path, menu_type, menu_url, menu_icon_url, sort_no, deleted, gmt_created, gmt_modified)
SELECT parent.id,
       '系统配置',
       '/system/config',
       1,
       'system:config:view',
       'lucide:sliders-horizontal',
       30,
       0,
       NOW(),
       NOW()
FROM sys_menu parent
WHERE parent.path = '/system'
  AND parent.deleted = 0
  AND NOT EXISTS (SELECT 1
                  FROM sys_menu
                  WHERE path = '/system/config'
                    AND deleted = 0)
LIMIT 1;

INSERT INTO sys_menu
(parent_id, menu_name, path, menu_type, menu_url, menu_icon_url, sort_no, deleted, gmt_created, gmt_modified)
SELECT parent.id,
       '字典管理',
       '/system/dict',
       1,
       'system:dict:view',
       'lucide:book-key',
       35,
       0,
       NOW(),
       NOW()
FROM sys_menu parent
WHERE parent.path = '/system'
  AND parent.deleted = 0
  AND NOT EXISTS (SELECT 1
                  FROM sys_menu
                  WHERE path = '/system/dict'
                    AND deleted = 0)
LIMIT 1;

INSERT INTO sys_menu
(parent_id, menu_name, path, menu_type, menu_url, menu_icon_url, sort_no, deleted, gmt_created, gmt_modified)
SELECT parent.id,
       '菜单管理',
       '/system/menu',
       1,
       'system:menu:view',
       'lucide:list-tree',
       40,
       0,
       NOW(),
       NOW()
FROM sys_menu parent
WHERE parent.path = '/system'
  AND parent.deleted = 0
  AND NOT EXISTS (SELECT 1
                  FROM sys_menu
                  WHERE path = '/system/menu'
                    AND deleted = 0)
LIMIT 1;

-- 日志管理目录及子菜单
INSERT INTO sys_menu
(parent_id, menu_name, path, menu_type, menu_url, menu_icon_url, sort_no, deleted, gmt_created, gmt_modified)
SELECT parent.id,
       '日志管理',
       '/system/log',
       0,
       NULL,
       'lucide:scroll-text',
       50,
       0,
       NOW(),
       NOW()
FROM sys_menu parent
WHERE parent.path = '/system'
  AND parent.deleted = 0
  AND NOT EXISTS (SELECT 1 FROM sys_menu WHERE path = '/system/log' AND deleted = 0)
LIMIT 1;

INSERT INTO sys_menu
(parent_id, menu_name, path, menu_type, menu_url, menu_icon_url, sort_no, deleted, gmt_created, gmt_modified)
SELECT parent.id,
       '操作日志',
       '/system/log/operation',
       1,
       'system:log:operation:view',
       'lucide:clipboard-list',
       0,
       0,
       NOW(),
       NOW()
FROM sys_menu parent
WHERE parent.path = '/system/log'
  AND parent.deleted = 0
  AND NOT EXISTS (SELECT 1 FROM sys_menu WHERE path = '/system/log/operation' AND deleted = 0)
LIMIT 1;

INSERT INTO sys_menu
(parent_id, menu_name, path, menu_type, menu_url, menu_icon_url, sort_no, deleted, gmt_created, gmt_modified)
SELECT parent.id,
       '登录日志',
       '/system/log/login',
       1,
       'system:log:login:view',
       'lucide:log-in',
       10,
       0,
       NOW(),
       NOW()
FROM sys_menu parent
WHERE parent.path = '/system/log'
  AND parent.deleted = 0
  AND NOT EXISTS (SELECT 1 FROM sys_menu WHERE path = '/system/log/login' AND deleted = 0)
LIMIT 1;

-- 开发工具
INSERT INTO sys_menu
(parent_id, menu_name, path, menu_type, menu_url, menu_icon_url, sort_no, deleted, gmt_created, gmt_modified)
SELECT 0,
       '开发工具',
       '/tool',
       0,
       NULL,
       'lucide:wrench',
       20,
       0,
       NOW(),
       NOW()
WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE path = '/tool' AND deleted = 0);

INSERT INTO sys_menu
(parent_id, menu_name, path, menu_type, menu_url, menu_icon_url, sort_no, deleted, gmt_created, gmt_modified)
SELECT parent.id,
       '代码生成器',
       '/tool/codegen',
       1,
       'tool:codegen:view',
       'lucide:blocks',
       0,
       0,
       NOW(),
       NOW()
FROM sys_menu parent
WHERE parent.path = '/tool'
  AND parent.deleted = 0
  AND NOT EXISTS (SELECT 1 FROM sys_menu WHERE path = '/tool/codegen' AND deleted = 0)
LIMIT 1;

-- 代码生成器按钮/API 权限。menu_type=2 仅作为权限码，不参与前端路由。
INSERT INTO sys_menu
(parent_id, menu_name, path, menu_type, menu_url, menu_icon_url, sort_no, deleted, gmt_created, gmt_modified)
SELECT page.id,
       permission.menu_name,
       CONCAT('/tool/codegen/', permission.action_code),
       2,
       CONCAT('tool:codegen:', permission.action_code),
       NULL,
       permission.sort_no,
       0,
       NOW(),
       NOW()
FROM sys_menu page
         CROSS JOIN (SELECT 'create' action_code, '新建生成配置' menu_name, 0 sort_no
                     UNION ALL
                     SELECT 'import', '导入数据库表', 10
                     UNION ALL
                     SELECT 'update', '修改生成配置', 20
                     UNION ALL
                     SELECT 'delete', '删除生成配置', 30
                     UNION ALL
                     SELECT 'download', '下载生成代码', 40) permission
WHERE page.path = '/tool/codegen'
  AND page.deleted = 0
  AND NOT EXISTS (SELECT 1
                  FROM sys_menu existing
                  WHERE existing.menu_url = CONCAT('tool:codegen:', permission.action_code)
                    AND existing.deleted = 0);

-- 管理员角色
INSERT INTO sys_role
    (role_name, role_code, description, deleted, gmt_created, gmt_modified)
SELECT '管理员', 'admin', '拥有全部后台路由权限的系统管理员', 0, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1
                  FROM sys_role
                  WHERE role_code = 'admin'
                    AND deleted = 0);

-- 默认管理员，用户名和初始密码均为 admin。
INSERT INTO sys_user
(username, password, org_id, status, deleted, gmt_created, gmt_modified)
SELECT 'admin',
       '{bcrypt}$2a$10$nFuSB2Gcl8e3WbwA6gt38.8NQXZ0EffEztRoAlbRtXXb6eDtjCgl2',
       org.id,
       1,
       0,
       NOW(),
       NOW()
FROM sys_org org
WHERE org.org_code = 'HEADQUARTERS'
  AND org.deleted = 0
  AND NOT EXISTS (SELECT 1
                  FROM sys_user
                  WHERE username = 'admin'
                    AND deleted = 0)
LIMIT 1;

-- 仅修复尚未分配组织的既有管理员，不覆盖人工调整过的组织。
UPDATE sys_user user
    JOIN sys_org org ON org.org_code = 'HEADQUARTERS' AND org.deleted = 0
SET user.org_id       = org.id,
    user.gmt_modified = NOW()
WHERE user.username = 'admin'
  AND user.deleted = 0
  AND (user.org_id IS NULL OR user.org_id = 0);

-- 管理员用户角色关系
INSERT INTO sys_user_role
    (user_id, role_id, deleted, gmt_created, gmt_modified)
SELECT user.id, role.id, 0, NOW(), NOW()
FROM sys_user user
         JOIN sys_role role ON role.role_code = 'admin' AND role.deleted = 0
WHERE user.username = 'admin'
  AND user.deleted = 0
  AND NOT EXISTS (SELECT 1
                  FROM sys_user_role relation
                  WHERE relation.user_id = user.id
                    AND relation.role_id = role.id
                    AND relation.deleted = 0);

-- 管理员角色拥有全部有效菜单；后续新增 SQL 菜单也会在启动时自动授权。
INSERT INTO sys_role_menu
    (role_id, menu_id, deleted, gmt_created, gmt_modified)
SELECT role.id, menu.id, 0, NOW(), NOW()
FROM sys_role role
         CROSS JOIN sys_menu menu
WHERE role.role_code = 'admin'
  AND role.deleted = 0
  AND menu.deleted = 0
  AND NOT EXISTS (SELECT 1
                  FROM sys_role_menu relation
                  WHERE relation.role_id = role.id
                    AND relation.menu_id = menu.id
                    AND relation.deleted = 0);

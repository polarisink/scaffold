package com.scaffold.rbac.service;

import cn.hutool.core.util.StrUtil;
import com.mzt.logapi.beans.CodeVariableType;
import com.mzt.logapi.beans.LogRecord;
import com.mzt.logapi.beans.Operator;
import com.mzt.logapi.service.ILogRecordService;
import com.mzt.logapi.service.IOperatorGetService;
import com.scaffold.base.util.ServletUtils;
import com.scaffold.rbac.entity.SysLoginLog;
import com.scaffold.rbac.entity.SysOperateLog;
import com.scaffold.rbac.mapper.SysLoginLogMapper;
import com.scaffold.rbac.mapper.SysOperateLogMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;

@Slf4j
@Primary
@Service
@RequiredArgsConstructor
public class RbacLogRecordService implements ILogRecordService {

    public static final String ACTION_LOGIN = "LOGIN";
    public static final String ACTION_LOGOUT = "LOGOUT";

    private final SysOperateLogMapper operateLogMapper;
    private final SysLoginLogMapper loginLogMapper;
    private final ObjectProvider<IOperatorGetService> operatorServiceProvider;

    private static String shortText(String value, int length) {
        return value == null ? null : StrUtil.sub(value, 0, length);
    }

    /**
     * 保存 bizlog-sdk 的业务操作日志。
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void record(LogRecord record) {
        try {
            boolean fail = record.isFail();
            SysOperateLog entity = new SysOperateLog();
            entity.setTitle(shortText(record.getType(), 100));
            entity.setBusinessType(shortText(record.getSubType(), 50));
            entity.setBizNo(shortText(record.getBizNo(), 100));
            entity.setOperator(shortText(record.getOperator(), 100));
            String actionText = shortText(record.getAction(), 500);
            if (fail) {
                entity.setErrorMsg(actionText);
            } else {
                entity.setAction(actionText);
            }
            entity.setStatus(!fail);
            entity.setExtra(record.getExtra());
            entity.setParam(record.getExtra());
            if (!fail) {
                entity.setResult(record.getBizNo());
            }
            Map<CodeVariableType, Object> codeVariable = record.getCodeVariable();
            if (codeVariable != null) {
                Object className = codeVariable.get(CodeVariableType.ClassName);
                Object methodName = codeVariable.get(CodeVariableType.MethodName);
                if (className != null || methodName != null) {
                    entity.setMethod(StrUtil.format("{}.{}", className, methodName));
                }
            }
            if (record.getCreateTime() != null) {
                entity.setGmtCreated(LocalDateTime.ofInstant(record.getCreateTime().toInstant(), ZoneId.systemDefault()));
            }
            fillServletRequest(entity);
            operateLogMapper.insert(entity);
        } catch (Exception exception) {
            log.error("保存 bizlog 操作日志失败", exception);
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void recordLogin(Long userId, String username, String action, boolean success, String message, String ip, String userAgent) {
        try {
            HttpServletRequest request = ServletUtils.getRequest();
            SysLoginLog entity = new SysLoginLog();
            entity.setUserId(userId);
            entity.setUsername(shortText(username, 64));
            entity.setAction(shortText(action, 20));
            entity.setStatus(success);
            entity.setMessage(shortText(message, 500));
            entity.setIp(shortText(StrUtil.blankToDefault(ip, request == null ? null : ServletUtils.getClientIP(request)), 64));
            entity.setUserAgent(shortText(StrUtil.blankToDefault(userAgent, request == null ? null : request.getHeader("User-Agent")), 500));
            loginLogMapper.insert(entity);
        } catch (Exception exception) {
            log.error("保存登录日志失败", exception);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<LogRecord> queryLog(String bizKey, String type) {
        return toLogRecords(operateLogMapper.findByBizNoAndType(bizKey, type, null));
    }

    @Override
    @Transactional(readOnly = true)
    public List<LogRecord> queryLogByBizNo(String bizNo, String type, String subType) {
        return toLogRecords(operateLogMapper.findByBizNoAndType(bizNo, type, subType));
    }

    private List<LogRecord> toLogRecords(List<SysOperateLog> entities) {
        return entities.stream().map(entity -> LogRecord.builder().id(entity.getId()).type(entity.getTitle()).subType(entity.getBusinessType()).bizNo(entity.getBizNo()).operator(entity.getOperator()).action(entity.getAction()).fail(!Boolean.TRUE.equals(entity.getStatus())).extra(entity.getExtra()).build()).toList();
    }

    private void fillServletRequest(SysOperateLog entity) {
        HttpServletRequest request = ServletUtils.getRequest();
        if (request == null) {
            return;
        }
        entity.setRequestMethod(shortText(request.getMethod(), 16));
        entity.setUrl(shortText(request.getRequestURI(), 255));
        entity.setIp(shortText(ServletUtils.getClientIP(request), 64));
    }

    private String resolveOperator(String eventUsername) {
        if (StrUtil.isNotBlank(eventUsername)) {
            return eventUsername;
        }
        IOperatorGetService service = operatorServiceProvider.getIfAvailable();
        Operator operator = service == null ? null : service.getUser();
        return operator == null ? null : operator.getOperatorId();
    }
}

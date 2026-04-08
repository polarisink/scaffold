package com.scaffold.bizlog.component;

import com.mzt.logapi.beans.LogRecord;
import com.mzt.logapi.service.ILogRecordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class CustomLogRecordService implements ILogRecordService {

    /**
     * 日志核心保存方法
     */
    @Override
    public void record(LogRecord logRecord) {
        // 调试阶段：控制台打印日志
        log.info("=====================业务日志=====================");
        log.info("操作人：{}", logRecord.getOperator());
        log.info("业务类型：{}", logRecord.getType());
        log.info("业务ID：{}", logRecord.getBizNo());
        log.info("操作内容：{}", logRecord.getAction());
        log.info("操作时间：{}", logRecord.getCreateTime());
        log.info("==================================================");

        // 生产环境：对接数据库，实现日志入库
        // BizLogEntity logEntity = buildLogEntity(logRecord);
        // bizLogMapper.insert(logEntity);
    }

    @Override
    public List<LogRecord> queryLog(String bizNo, String type) {
        return List.of();
    }

    @Override
    public List<LogRecord> queryLogByBizNo(String bizNo, String type, String subType) {
        return List.of();
    }
}
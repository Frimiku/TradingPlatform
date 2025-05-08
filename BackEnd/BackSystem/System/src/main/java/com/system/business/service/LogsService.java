package com.system.business.service;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.system.business.entity.Logs;
import com.system.business.mapper.LogsMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LogsService extends ServiceImpl<LogsMapper, Logs> {
    private LogsMapper logsMapper;

    @Autowired
    public LogsService(LogsMapper logsMapper) {
        this.logsMapper = logsMapper;
    }
}

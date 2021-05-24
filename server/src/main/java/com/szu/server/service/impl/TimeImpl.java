package com.szu.server.service.impl;
/*
 * @Author 郭学胤
 * @University 深圳大学
 * @Description
 * @Date 2021/5/24 12:17
 */

import com.szu.common.service.TimeService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TimeImpl implements TimeService {


    @Override
    public String getDateTime() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}

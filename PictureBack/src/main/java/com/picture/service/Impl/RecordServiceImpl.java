package com.picture.service.Impl;

import com.picture.dao.RecordMapper;
import com.picture.domain.Record;
import com.picture.service.RecordService;
import com.picture.utils.DateUtil;
import com.picture.utils.IpUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

@Service
public class RecordServiceImpl implements RecordService {

    @Resource
    RecordMapper recordMapper;
    @Resource
    IpUtil ipUtil;
    @Resource
    DateUtil dateUtil;

    /**
     * 添加记录
     * @param operation
     * @param
     * @param number
     * @param userId
     * @return
     */
    @Override
    public boolean addRecord(HttpServletRequest req,String operation, Integer number, Integer userId) {
        Date date = new Date();
        Record record = new Record();
        record.setDate(date);
        record.setOperation(operation);
        record.setNumber(number);
        record.setIpv4(ipUtil.getIpAddress(req));
        record.setUserId(userId);
        recordMapper.addRecord(record);
        if(record.getId()!=null){
            return true;
        }
        return false;
    }

}

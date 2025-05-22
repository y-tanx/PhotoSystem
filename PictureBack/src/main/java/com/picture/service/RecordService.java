package com.picture.service;
import com.picture.domain.Record;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.util.List;

@Service
public interface RecordService {
    /**
     * 添加记录
     *
     * @param operation
     * @param
     * @param number
     * @param userId
     * @return
     */

    boolean addRecord(HttpServletRequest req, String operation, Integer number, Integer userId);
}
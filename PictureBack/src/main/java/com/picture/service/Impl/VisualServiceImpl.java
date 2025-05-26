package com.picture.service.Impl;

import com.alibaba.fastjson.JSONObject;
import com.picture.dao.ImageMapper;
import com.picture.dao.VisualMapper;
import com.picture.domain.VO.VisualDataVO;
import com.picture.domain.VisualDataSite;
import com.picture.domain.VisualDataType;
import com.picture.service.VisualService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
public class VisualServiceImpl implements VisualService {
    @Resource
    private VisualMapper visualMapper;
    @Resource
    private ImageMapper imageMapper;

    @Override
    public VisualDataVO selectTypeSite(Integer userId) {
        List<VisualDataType> visualTypes = visualMapper.selectVisualDataType(userId);
        List<VisualDataSite> visualSites = visualMapper.selectVisualDataSite(userId);

        List<String> imageSites = new ArrayList<String>();
        List<Integer> SiteNumbers = new ArrayList<>();
        for (VisualDataSite visualDataSite : visualSites) {
            imageSites.add(visualDataSite.getImageSite());
            SiteNumbers.add(visualDataSite.getNumber());
        }
        return new VisualDataVO(visualTypes, imageSites, SiteNumbers);
    }

    @Override
    public JSONObject selectAllImageInfo(Integer userId) {
        JSONObject jsonObject = new JSONObject();
        Integer imageTotalNumber = imageMapper.selectImageCount(userId);
        Integer imageTotalSize = visualMapper.selectImageTotalSize(userId);
        jsonObject.put("imageSumSize", ((float)imageTotalSize/1024/1024));
        jsonObject.put("imageSumNumber", imageTotalNumber);
        return jsonObject;
    }
}

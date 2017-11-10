package com.jishi.reservation.service;

import com.jishi.reservation.dao.mapper.AndroidVersionMapper;
import com.jishi.reservation.dao.models.AndroidVersion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by sloan on 2017/11/9.
 */

@Service
public class VersionService {

    @Autowired
    AndroidVersionMapper androidVersionMapper;

    public AndroidVersion checkUpdateForAndroid() {

        return androidVersionMapper.checkUpdateForAndroid();

    }
}

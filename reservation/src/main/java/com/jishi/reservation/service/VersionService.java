package com.jishi.reservation.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jishi.reservation.dao.mapper.AndroidVersionMapper;
import com.jishi.reservation.dao.models.AndroidVersion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by sloan on 2017/11/9.
 */

@Service
public class VersionService {

    @Autowired
    AndroidVersionMapper androidVersionMapper;

    public AndroidVersion checkUpdateForAndroid() {

        AndroidVersion androidVersion = androidVersionMapper.checkUpdateForAndroid();

        Gson gson = new Gson();

        List<String> list = gson.fromJson(androidVersion.getUpdateContent(),
                new TypeToken<List<String>>() {
                }.getType());
        androidVersion.setContentList(list);
        androidVersion.setUpdateContent(null);
        return androidVersion;
    }
}

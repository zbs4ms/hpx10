package com.jishi.reservation.service.support;

import com.doraemon.base.redis.RedisOperation;
import com.google.common.base.Preconditions;
import com.jishi.reservation.dao.mapper.DepartmentMapper;
import com.jishi.reservation.dao.mapper.ManagerMapper;
import com.jishi.reservation.dao.models.Department;
import com.jishi.reservation.dao.models.Manager;
import com.jishi.reservation.service.enumPackage.EnableEnum;
import com.jishi.reservation.util.Helpers;
import com.us.base.util.MD5Encryption;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * Created by zbs on 2017/8/10.
 */
@Service
@Log4j
public class ManagerService {

    @Autowired
    ManagerMapper managerMapper;


    @Autowired
    private RedisOperation redisOperation;

    //保存登陆信息
    public final static String ADD_TOKEN = ""
            + " local token = redis.call('get', KEYS[1]); "
            + " if token then "
            + "     redis.call('del',token); "
            + " end "
            + " redis.call('set',KEYS[1],KEYS[2]); "
            + " redis.call('set',KEYS[2],KEYS[1]); "
            + " return 1 ";

    public Manager findAccountByAccount(String account) {

        Manager manager =  managerMapper.findAccountByUserName(account);
        Preconditions.checkNotNull(account,"该账户不存在，请检查账号");
        return  manager;
    }

    public String loginByAccountAndPwd(Long id) throws Exception {

        String token = createToken(id);
        List<String> keys = new ArrayList<String>();
        keys.add(String.valueOf(id));
        keys.add(token);
        Preconditions.checkState(Integer.valueOf(String.valueOf(redisOperation.usePool().eval(ADD_TOKEN,keys,new ArrayList<String>()))) == 1,"保存登陆信息失败.");
        //设置token过期时间
        redisOperation.usePool().expire(token,28800);
        redisOperation.usePool().expire(String.valueOf(id),28800);
        return token;
    }


    /**
     * 创建token值
     * @param user
     * @return
     * @throws Exception
     */
    private static String createToken(Long user) throws Exception {
        Random r = new Random((new Date().getTime()));
        return MD5Encryption.getMD5(user + (new Date()).getTime() + String.valueOf(r.nextInt(100000000)));
    }
}

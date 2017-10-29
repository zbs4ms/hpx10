package com.jishi.reservation.service;

import com.jishi.reservation.dao.mapper.IMAccountMapper;
import com.jishi.reservation.dao.models.Account;
import com.jishi.reservation.dao.models.Doctor;
import com.jishi.reservation.dao.models.IMAccount;
import com.jishi.reservation.otherService.im.neteasy.IMClientNeteasy;
import com.jishi.reservation.otherService.im.neteasy.model.IMUser;
import com.jishi.reservation.util.Constant;
import com.jishi.reservation.util.Helpers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by liangxiong on 2017/10/27.
 */
@Service
@Slf4j
public class IMAccountService {
    @Autowired
    private AccountService accountService;

    @Autowired
    private DoctorService doctorService;

    @Autowired
    private IMAccountMapper imAccountMapper;

    private IMClientNeteasy imClientNeteasy = IMClientNeteasy.getInstance(
            Constant.IM_NETEASY_APPKEY, Constant.IM_NETEASY_APPSECRET);

    /**
     * @description 获取普通用户im的账号，没有im账户则创建，没有token则更新
     * @param accId 用户id
     * @throws Exception
    **/
    public IMAccount getUserIMAccount(Long accId) throws Exception {
        IMAccount imAccount = imAccountMapper.selectByAccountId(accId);
        if (imAccount == null) {
            log.info("用户没有创建IM账户，现在创建。本地系统accId：" + accId);
            return createUserByAccountId(accId);
        } else if (imAccount.getImToken() == null || imAccount.getImToken().isEmpty()) {
            log.info("用户im token为空，刷新token。本地系统accId：" + accId);
            imAccount.setImToken(refreshToken(imAccount));
        }
        log.info("用户accId：" + accId + " im token" + imAccount.getImToken());
        return imAccount;
    }

    /**
     * @description 获取医生im的账号，没有im账户则创建，没有token则更新
     * @param doctorId 本地系统医生id
     * @throws Exception
    **/
    public IMAccount getDoctorIMAccount(Long doctorId) throws Exception {
        IMAccount imAccount = imAccountMapper.selectByDoctorId(doctorId);
        if (imAccount == null) {
            log.info("医生没有创建IM账户，现在创建。本地系统doctorId：" + doctorId);
            return createUserByDoctorId(doctorId);
        } else if (imAccount.getImToken() == null || imAccount.getImToken().isEmpty()) {
            log.info("医生im token为空，刷新token。本地系统doctorId：" + doctorId);
          imAccount.setImToken(refreshToken(imAccount));
        }
        log.info("医生doctorId：" + doctorId + " im token" + imAccount.getImToken());
        return imAccount;
    }

    /**
     * @description 刷新im的token
     * @param accId 用户id
     * @throws Exception
    **/
    public String refreshUserToken(Long accId) throws Exception {
        if (accId == null) {
            log.error("refreshUserToken：用户accId不能为空");
            return null;
        }
        IMAccount imAccount = imAccountMapper.selectByAccountId(accId);
        String token = refreshToken(imAccount);
        log.info("用户accId：" + accId + " im token" + token);
        return token;
    }

    /**
     * @description 刷新im的token
     * @param doctorId 医生doctorId
     * @throws Exception
     **/
    public String refreshDoctorToken(Long doctorId) throws Exception {
        if (doctorId == null) {
          log.error("refreshUserToken：医生doctorId不能为空");
          return null;
        }
        IMAccount imAccount = imAccountMapper.selectByDoctorId(doctorId);
        String token = refreshToken(imAccount);
        log.info("医生doctorId：" + doctorId + " im token" + token);
        return token;
    }

    private String refreshToken(IMAccount imAccount) throws Exception {
        String token = imClientNeteasy.getUserOperation().refreshToken(imAccount.getImAccId());
        imAccount.setImToken(token);
        imAccountMapper.updateByPrimaryKey(imAccount);
        return token;
    }

    /**
     * @description 创建IM账户
     * @param accId 普通用户id
     * @throws
     **/
    public IMAccount createUserByAccountId(Long accId) throws Exception {
        log.info("创建普通用户IM账户，accId" + accId);
        return createUser(accId,null, null);
    }

    /**
     * @description 创建IM账户
     * @param doctorId 医生本地系统id
     * @throws Exception
     **/
    public IMAccount createUserByDoctorId(Long doctorId) throws Exception {
        log.info("创建医生IM账户，doctorId" + doctorId);
        return createUser(null,doctorId, null);
    }

    /**
     * @description 创建IM账户
     * @param doctorHisId 医生his系统id
     * @throws Exception
     **/
    public IMAccount createUserByDoctorHisId(String doctorHisId) throws Exception {
        log.info("创建医生IM账户，doctorHisId" + doctorHisId);
        return createUser(null, null, doctorHisId);
    }

    /**
     * @description 创建IM账户 参数传一个
     * @param accId 普通用户id
     * @param doctorId 医生本地系统id
     * @param doctorHisId 医生his系统id
     * @throws Exception
    **/
    private IMAccount createUser(Long accId, Long doctorId, String doctorHisId) throws Exception {
        if (accId == null && doctorId == null && Helpers.isNullOrEmpty(doctorHisId)) {
            log.error("accId doctorId doctorHisId不能全为空");
            return null;
        }

        IMAccount imAccount = new IMAccount();
        IMUser imUser = new IMUser();
        if (accId != null) {
            Account account = accountService.queryAccountById(accId);
            if (account == null) {
                log.error("未找到用户：" + accId);
                return null;
            }
            imUser.setName(account.getNick());
            imUser.setMobile(account.getPhone());
            imUser.setEmail(account.getEmail());
            imUser.setIcon(account.getHeadPortrait());
            imAccount.setAccountId(accId);
            imAccount.setType(0); //普通用户
        } else if (doctorId != null) {
            List<Doctor> doctorList = doctorService.queryDoctor(doctorId, doctorHisId, null, null, null, 0);
            if (doctorList == null || doctorList.size() != 1) {
                log.error("医生列表为空或大于1：doctorId " + doctorId + " doctorHisId " +  doctorHisId);
                return null;
            }
            Doctor doctor = doctorList.iterator().next();
            imUser.setName(doctor.getName());
            imUser.setIcon(doctor.getHeadPortrait());
            imUser.setSign(doctor.getAbout());
            imAccount.setDoctorId(doctorId);
            imAccount.setDoctorHisId(doctorHisId);
            imAccount.setType(1); //医生
        }
        IMUser resUser = imClientNeteasy.getUserOperation().createUser(imUser);
        log.info("网易云信创建账号成功:  imaccid" + resUser.getAccid() + " token: " + resUser.getToken());

        imAccount.setImAccId(resUser.getAccid());
        imAccount.setImToken(resUser.getToken());
        imAccountMapper.insert(imAccount);

        return imAccount;
    }
}

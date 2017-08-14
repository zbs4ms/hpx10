package com.jishi.reservation.service;

import com.doraemon.base.redis.RedisOperation;
import com.google.common.base.Preconditions;
import com.jishi.reservation.dao.mapper.AccountMapper;
import com.jishi.reservation.dao.models.Account;
import com.jishi.reservation.service.enumPackage.EnableEnum;
import com.jishi.reservation.util.Helpers;
import com.jishi.reservation.util.NewRandomUtil;
import com.us.base.util.MD5Encryption;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;
import java.util.List;

/**
 * Created by zbs on 2017/8/10.
 */
@Service
@Log4j
public class AccountService {

    @Autowired
    AccountMapper accountMapper;
    @Autowired
    RedisOperation redisOperation;

    @Value("constant.dynamic_code_key")
    public String dynamic_code_key;


    /**
     * 发送手机动态码
     *
     * @param phone
     * @return
     * @throws Exception
     */
    public String sendDynamicCode(String phone) throws Exception {
        log.info("发送手机动态登陆码!");
        String code = NewRandomUtil.getRandomNum(6);
        redisOperation.set(dynamic_code_key + "_" + phone, code);
        redisOperation.expire(dynamic_code_key + "_" + phone,5 * 60);
        return code;
    }

    /**
     * 采用手机进行动态码登陆和注册(如果已经注册就走登陆,如果没有注册,先注册再登陆)
     *
     * @param phone
     * @param dynamicCode
     * @return
     * @throws Exception
     */
    public void loginOrRegisterThroughPhone(String phone, String dynamicCode) throws Exception {
        log.info("账号采用手机进行登陆: phone:" + phone + " dynamicCode:" + dynamicCode);
        String code = redisOperation.get(dynamic_code_key + "_" + phone);
        if (!dynamicCode.equals(code))
            throw new Exception("登陆失败!");
        List<Account> account = queryAccount(null, phone, null);
        if (account.size() == 0)
            addAccount(phone, phone, null, phone, phone, null);
    }

    /**
     * 新增账号 (如果存在就修改)
     *
     * @param account
     * @param passwd
     * @param headPortrait
     * @param nick
     * @param phone
     * @param email
     * @throws NoSuchAlgorithmException
     */
    public void addAccount(String account, String passwd, String headPortrait, String nick, String phone, String email) throws Exception {
        log.info("账号注册 account:" + account + " passwd:" + passwd + " phone:" + phone);
        if (Helpers.isNullOrEmpty(account) || Helpers.isNullOrEmpty(passwd))
            throw new Exception("账号密码不能为空!");
        List<Account> queryAccount = queryAccount(null, phone, null);
        if (queryAccount.size() != 0)
            throw new Exception("该手机号已经注册!");
        Account insertAccount = new Account();
        insertAccount.setAccount(account);
        insertAccount.setPasswd(MD5Encryption.getMD5(passwd));
        insertAccount.setHeadPortrait(headPortrait);
        insertAccount.setNick(nick);
        insertAccount.setPhone(phone);
        insertAccount.setEmail(email);
        insertAccount.setEnable(EnableEnum.EFFECTIVE.getCode());
        accountMapper.insert(insertAccount);
    }

    /**
     * 修改账号基本信息
     *
     * @param accountId
     * @param nick
     * @param headPortrait
     * @param email
     */
    public void modifyAccountInfo(Long accountId, String passwd, String nick, String headPortrait, String email, String phone,Integer enable) throws Exception {
        log.info("修改账号信息 accountId:" + accountId + " nicke:" + nick + " headPortrait" + headPortrait + " email:" + email);
        if (Helpers.isNullOrEmpty(accountId) && Helpers.isNullOrEmpty(phone))
            throw new Exception("账号ID和手机号不能同时为空.");
        List<Account> queryAccountList = queryAccount(accountId, phone, null);
        if (queryAccountList.size() == 0)
            throw new Exception("没有查到该账号");
        Account newAccount = new Account();
        newAccount.setId(queryAccountList.get(0).getId());
        newAccount.setNick(nick);
        newAccount.setHeadPortrait(headPortrait);
        newAccount.setEmail(email);
        newAccount.setPasswd(passwd == null ? null : MD5Encryption.getMD5(passwd));
        newAccount.setPhone(phone);
        newAccount.setEnable(enable);
        Preconditions.checkState(accountMapper.updateByPrimaryKeySelective(newAccount) == 1,"更新失败!");
    }

    /**
     * 修改绑定的手机
     *
     * @param accountId
     * @param phone
     */
    public void modifyAccountPhone(Long accountId, String phone) throws Exception {
        List<Account> queryAccountList = queryAccount(accountId, null, null);
        if (queryAccountList.size() == 0)
            throw new Exception("没有查到该账号");
        Account newAccount = new Account();
        newAccount.setId(queryAccountList.get(0).getId());
        newAccount.setPhone(phone);
        Preconditions.checkState(accountMapper.updateByPrimaryKeySelective(newAccount) == 1,"更新失败!");
    }

    /**
     * 修改密码
     * @param accountId
     * @param phone
     * @param oldPasswd
     * @param newPasswd
     * @throws Exception
     */
    public void modifyAccountPasswd(Long accountId, String phone,String oldPasswd, String newPasswd) throws Exception {
        if (Helpers.isNullOrEmpty(accountId) && Helpers.isNullOrEmpty(phone))
            throw new Exception("账号ID和手机号不能同时为空.");
        if (Helpers.isNullOrEmpty(oldPasswd) || Helpers.isNullOrEmpty(newPasswd))
            throw new Exception("老密码和新密码都不能为空.");
        List<Account> queryAccountList = queryAccount(accountId, phone, null);
        if (queryAccountList.size() == 0)
            throw new Exception("没有查到该账号,或密码错误.");
        if(!queryAccountList.get(0).getPasswd().equals(MD5Encryption.getMD5(oldPasswd)))
            throw new Exception("没有查到该账号,或密码错误.");
        modifyAccountInfo(accountId, newPasswd, null, null, null, phone,null);
    }

    /**
     * 查询账号信息
     *
     * @param accountId
     * @param phone
     * @return
     */
    public List<Account> queryAccount(Long accountId, String phone, Integer enable) {
        log.info("查询账号信息 accountId:" + accountId + " phone:" + phone + " enable" + enable);
        if (Helpers.isNullOrEmpty(accountId) && Helpers.isNullOrEmpty(phone))
            return null;
        Account queryAccount = new Account();
        queryAccount.setId(accountId);
        queryAccount.setPhone(phone);
        queryAccount.setEnable(enable);
        return accountMapper.select(queryAccount);
    }

    /**
     * 查询全部账号信息 -- 只显示有效的
     *
     * @return
     */
    public List<Account> queryAllEffectiveAccount() {
        log.info("查询全部有效账号信息");
        Account queryAccount = new Account();
        queryAccount.setEnable(EnableEnum.EFFECTIVE.getCode());
        return accountMapper.select(queryAccount);
    }

    /**
     * 查询全部账号信息 -- 有效的无效的都返回
     *
     * @return
     */
    public List<Account> queryAllAccount() {
        log.info("查询全部账号信息");
        return accountMapper.selectAll();
    }

    /**
     * 让一个账号失效
     *
     * @param accountId
     * @param phone
     * @return
     */
    public void failureAccount(Long accountId, String phone) throws Exception {
        log.info("失效账号 accountId:" + accountId + " phone:" + phone);
        if (Helpers.isNullOrEmpty(accountId) && Helpers.isNullOrEmpty(phone))
            throw new Exception("账号ID和手机不能同时为空.");
        if(queryAccount(accountId, phone, null).size() == 0)
            throw new Exception("没有查到该账号");
        modifyAccountInfo(accountId,null,null,null,null,phone,EnableEnum.INVALID.getCode());
    }
}
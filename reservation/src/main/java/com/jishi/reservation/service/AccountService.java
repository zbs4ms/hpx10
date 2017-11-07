package com.jishi.reservation.service;

import com.alibaba.fastjson.JSONObject;
import com.doraemon.base.redis.RedisOperation;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.base.Preconditions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jishi.reservation.controller.protocol.AccountDetailVO;
import com.jishi.reservation.controller.protocol.BridAndMzh;
import com.jishi.reservation.controller.protocol.DiaryContentVO;
import com.jishi.reservation.controller.protocol.LoginData;
import com.jishi.reservation.dao.mapper.*;
import com.jishi.reservation.dao.models.*;
import com.jishi.reservation.service.enumPackage.EnableEnum;

import com.jishi.reservation.service.enumPackage.SmsEnum;
import com.jishi.reservation.service.his.HisUserManager;
import com.jishi.reservation.service.his.bean.PatientsList;
import com.jishi.reservation.service.support.AliDayuSupport;
import com.jishi.reservation.util.Constant;
import com.jishi.reservation.util.Helpers;
import com.jishi.reservation.util.NewRandomUtil;
import com.us.base.util.MD5Encryption;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

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
    @Autowired
    AliDayuSupport dayuSupport;

    @Autowired
    HisUserManager hisUserManager;

    @Autowired
    IdentityInfoMapper identityInfoMapper;
    @Autowired
    CredentialsMapper credentialsMapper;

    @Autowired
    PatientInfoMapper patientInfoMapper;

    @Autowired
    DiaryMapper diaryMapper;

    @Autowired
    DiaryScanMapper diaryScanMapper;

    @Autowired
    DiaryLikedMapper diaryLikedMapper;





    //保存登陆信息
    public final static String ADD_TOKEN = ""
            + " local token = redis.call('get', KEYS[1]); "
//            + " if token then "
//            + "     redis.call('del',token); "
//            + " end "
            + " redis.call('set',KEYS[1],KEYS[2]); "
            + " redis.call('set',KEYS[2],KEYS[1]); "
            + " return 1 ";

    //注销用户
    public final static String DEL_TOKEN = ""
            + " local token = redis.call('get',KEYS[1]); "
            + " redis.call('del',token); "
            + " redis.call('del',KEYS[1]); "
            + " return 1 ";


    /**
     * 发送登陆/注册手机动态码
     *
     * @param phone
     * @return
     * @throws Exception
     */
    public String sendDynamicCode(String phone,String prefix,String templateCode) throws Exception {
        log.info("发送手机动态验证码!"+prefix);
        String code = NewRandomUtil.getRandomNum(6);
        log.info("redis key:"+prefix + "_" + phone+",value:"+code);
        redisOperation.set(prefix + "_" + phone, code);
        redisOperation.expire(prefix + "_" + phone,5 * 60);
        dayuSupport.sendynamicCode(phone, code,templateCode);
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
    public LoginData loginOrRegisterThroughPhone(String phone, String  prefix,String dynamicCode) throws Exception {
        Account accountLogin;
        if(!Constant.TEST_PHONE.contains(phone)){
            log.info("账号采用手机进行登陆: phone:" + phone + " dynamicCode:" + dynamicCode);
            String code = redisOperation.get(prefix + "_" + phone);

            if (!dynamicCode.equals(code))
                return null;
            List<Account> account = queryAccount(null, phone, null);

            if (account.size() == 0){
                accountLogin = addAccount(phone, phone, Constant.DEFAULT_AVATAR, phone, phone, null);

            }else {
                accountLogin = account.get(0);
            }

            //删除已核对的验证码
            redisOperation.del(prefix + "_" + phone);
            log.info("删除已核对的验证码："+prefix + "_" + phone);


        }else {
            //如果是测试账号
            List<Account> accounts = queryAccount(null, phone, null);
            accountLogin = accounts.get(0);

        }
        String token = login(phone);
        LoginData loginData = new LoginData();
        loginData.setToken(token);
        loginData.setHeadPortrait(accountLogin.getHeadPortrait());
        loginData.setNickname(accountLogin.getNick());
        loginData.setTelephone(accountLogin.getPhone());
        loginData.setPushId(accountLogin.getPushId());
        loginData.setAccountId(accountLogin.getId());

        return loginData;


    }


    public boolean checkOriginalPhoneCode(String phone, String prefix,String dynamicCode) throws Exception {
        log.info("账号进行换绑操作: oldPhone:" + phone + " dynamicCode:" + dynamicCode);
        String code = redisOperation.get(prefix + "_" + phone);
        log.info("原验证码："+code);
        if (!dynamicCode.equals(code)){
            log.info("验证码验证失败..");
            return  false;
        }else {
            redisOperation.del(prefix + "_" + phone);
            log.info("删除已核对的验证码："+prefix + "_" + phone);
            return true;
        }

    }


    public boolean changePhone(String originalPhone, String prefix, String newPhone, String dynamicCode) throws Exception {
        log.info("账号进行换绑操作: oldPhone:" + originalPhone + "newPhone:" + newPhone + " dynamicCode:" + dynamicCode);
        String code = redisOperation.get(prefix + "_" + newPhone);
        if (!dynamicCode.equals(code)) {
            return false;
        } else {
            redisOperation.del(prefix + "_" + newPhone);
            log.info("删除已核对的验证码：" + prefix + "_" + newPhone);

            Account account = accountMapper.queryByTelephone(originalPhone);
            account.setPhone(newPhone);
            account.setAccount(newPhone);
            return accountMapper.updateByPrimaryKeySelective(account) == 1;
        }
    }

    /**
     * 创建token值
     * @param phone
     * @return
     * @throws Exception
     */
    private String login(String phone) throws Exception {

        Account account = accountMapper.queryByTelephone(phone);

        String token = Constant.TOKEN_HEADER + createToken(account.getId());
        List<String> keys = new ArrayList<String>();
        keys.add(String.valueOf(account.getId()));
        keys.add(token);
        Preconditions.checkState(Integer.valueOf(String.valueOf(redisOperation.eval(ADD_TOKEN,keys,new ArrayList<String>()))) == 1,"保存登陆信息失败.");
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
    public Account addAccount(String account, String passwd, String headPortrait, String nick, String phone, String email) throws Exception {
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
        insertAccount.setPushId("hpx10_"+NewRandomUtil.getRandomNum(6));
        insertAccount.setEnable(EnableEnum.EFFECTIVE.getCode());
        accountMapper.insertReturnId(insertAccount);

        return insertAccount;
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

    public Account loginByTelephoneAndPassword(String accountInput, String password) throws NoSuchAlgorithmException {

//        Account account = new Account();
//        account.setPasswd(MD5Encryption.getMD5(password));
//        account.setAccount(accountInput);
        String md5 = MD5Encryption.getMD5(password);
        Account queryAccount = accountMapper.selectByAccountAndPassword(accountInput,MD5Encryption.getMD5(password));

        log.info("查询结果"+ JSONObject.toJSONString(queryAccount));
        return queryAccount;
    }


    public Long returnIdByToken(HttpServletRequest request) throws Exception {
        String token = request.getHeader(Constant.TOKEN);
        log.info("token："+token);
        if(token == null || "".equals(token) || "null".equals(token)){
            log.info("token為空...");
            return -1L;

        }
        return redisOperation.usePool().get(token)!=null?
                Long.valueOf(redisOperation.usePool().get(token)) : Long.valueOf(-1);


    }

    public void logout(String token) throws Exception {
        List<String> keys = new ArrayList<String>();
        keys.add(token);
        Preconditions.checkState(Integer.valueOf(String.valueOf(redisOperation.eval(DEL_TOKEN,keys,new ArrayList<String>()))) == 1,"注销用户登陆信息失败.");

    }


    public Account queryAccountByTelephone(String phone) {

        return accountMapper.queryByTelephone(phone);
    }

    public Account queryAccountById(Long accountId) {

        return accountMapper.queryById(accountId);
    }

//    /**
//     * 连接了his系统的注册，如果证件号已存在，就返回相应信息；不然就注册，然后返回相应信息；
//     * 如果手机号已存在，但是证件号不存在，那就把证件号和手机号绑定，最多不超过5个
//     * @param idNumber 证件号
//     * @param idNumberType 证件类型
//     * @param name 姓名
//     * @param phone 手机号
//     * @return
//     */
//    @Transactional
//    public LoginData registerWithHis(String idNumber, String idNumberType, String name, String phone) throws Exception {
//
//
//        PatientsList patientsList = hisUserManager.getUserInfoByCode(idNumber, idNumberType);
//        //如果查询不到，就去注册
//        BridAndMzh bridAndMzh = null ;
//        if(patientsList == null){
//            // todo 现在返回的是假的BRID
//            bridAndMzh = hisUserManager.addUserInfo(idNumber, idNumberType, name, phone);
//        }else {
//            bridAndMzh = new BridAndMzh();
//            List<com.jishi.reservation.service.his.bean.PatientsList.Credentials> jzkList = patientsList.getJzkList();
//            bridAndMzh.setBRID(jzkList.get(0).getBRID());
//            bridAndMzh.setMZH(jzkList.get(0).getMZH());
//        }
//
//        List<IdentityInfo> list = new ArrayList<>();
//        //看在我们的系统中，该电话是否存在
//        Account account = accountMapper.queryByTelephone(phone);
//        Account newAccount = new Account();
//
//        //不存在就注册
//        if(account == null){
//            //账号主表
//            newAccount.setAccount(phone);
//            newAccount.setPhone(phone);
//            newAccount.setHeadPortrait(Constant.DEFAULT_AVATAR);
//            newAccount.setPushId("hpx10_"+NewRandomUtil.getRandomNum(6));
//            newAccount.setNick(phone);
//            newAccount.setEnable(EnableEnum.EFFECTIVE.getCode());
//            newAccount.setPasswd(Constant.DEFAULT_PASSWORD);
//
//            accountMapper.insertReturnId(newAccount);
//
//
//
//        }else {
//            //存在就进行绑定,最多不超过5个
//            list = identityInfoMapper.queryByAccountId(account.getId());
//            Preconditions.checkState(list.size() > Constant.MAX_BINDING_NUM,"同一个账号最多只能绑定5个账号");
//        }
//
//        //判断该就诊人是否已经和该账号已经关联了
//        Preconditions.checkState(identityInfoMapper.queryByAccountIdAndIdentity(account==null?newAccount.getId():account.getId(),idNumber) != null,
//                "病人已经和该账号关联");
//
//        //就诊人表
//        IdentityInfo newIdentityInfo = new IdentityInfo();
//        newIdentityInfo.setAccountId(account==null?newAccount.getId():account.getId());
//        newIdentityInfo.setEnable(EnableEnum.EFFECTIVE.getCode());
//        newIdentityInfo.setIdentityCode(idNumber);
//        newIdentityInfo.setBrId(bridAndMzh.getBRID());
//        identityInfoMapper.insertReturnId(newIdentityInfo);
//        list.add(newIdentityInfo);
//
//        //就诊卡info表
//        Credentials newCredentials = new Credentials();
//        newCredentials.setBrId(bridAndMzh.getBRID());
//        newCredentials.setEnable(EnableEnum.EFFECTIVE.getCode());
//        newCredentials.setIdNumber(idNumber);
//        newCredentials.setMzh(bridAndMzh.getMZH());
//        newCredentials.setIdType(idNumberType);
//        credentialsMapper.insertReturnId(newCredentials);
//
//        LoginData loginData = new LoginData();
//        List<Credentials> credentialsList = new ArrayList<>();
//        if(patientsList != null && patientsList.getJzkList()!=null){
//            List<com.jishi.reservation.service.his.bean.Credentials> jzkList = patientsList.getJzkList();
//            for (com.jishi.reservation.service.his.bean.Credentials credentials : jzkList) {
//                Credentials param = new Credentials();
//                param.setIdType(credentials.getIdType());
//                param.setMzh(credentials.getMZH());
//                param.setBrId(credentials.getBRID());
//                param.setIdNumber(credentials.getIdNumber());
//
//                credentialsList.add(param);
//            }
//        }
//        loginData.setCredentialsList(credentialsList);
//        loginData.setIdentityInfoList(list);
//        return loginData;
//    }

    public LoginData queryInfo(String accountStr) {

        Account account = accountMapper.queryByTelephone(accountStr);
        Preconditions.checkNotNull(account,"该手机号没有对应的用户信息");
        List<IdentityInfo> identityList = identityInfoMapper.queryByAccountId(account.getId());
        List<Credentials> credentialsList = new ArrayList<>();
        for (IdentityInfo identityInfo : identityList) {
            Credentials credentials = credentialsMapper.queryByBrid(identityInfo.getBrId());
            credentialsList.add(credentials);
        }

        LoginData loginData = new LoginData();
        loginData.setIdentityInfoList(identityList);
        loginData.setCredentialsList(credentialsList);

        return loginData;
    }

    public PageInfo<Account> queryAccountPage(String key, Integer startPage, Integer pageSize) {

        PageHelper.startPage(startPage,pageSize).setOrderBy("id desc");
        List<Account> list = accountMapper.queryCondition(key);
        for (Account account : list) {
            account.setPasswd(null);
        }
        return new PageInfo<>(list);
    }

    public AccountDetailVO queryUserDetail(Long accountId) {

        AccountDetailVO vo = new AccountDetailVO();
        Account account = accountMapper.queryById(accountId);
        account.setPasswd(null);
        vo.setAccount(account);
        Gson gson = new Gson();

        List<PatientInfo> patientInfoList =  patientInfoMapper.queryByAccountId(accountId);
        vo.setPatientInfoList(patientInfoList);
        List<Diary> diaryList =  diaryMapper.queryByAccountId(accountId);
        for (Diary diary : diaryList) {
            diary.setScanNum(diaryScanMapper.queryCountByDiaryId(diary.getId()));
            diary.setLikedNum(diaryLikedMapper.queryCountByDiaryId(diary.getId()));

            String content = diary.getContent();



            List<DiaryContentVO> contentList = gson.fromJson(content,
                    new TypeToken<List<DiaryContentVO>>() {
                    }.getType());
            diary.setContentVOList(contentList);

        }
        vo.setDiaryList(diaryList);

        return vo;

    }
}

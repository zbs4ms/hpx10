package com.jishi.reservation.worker;

import com.jishi.reservation.dao.mapper.AccountMapper;
import com.jishi.reservation.dao.models.Account;
import com.jishi.reservation.dao.models.Doctor;
import com.jishi.reservation.dao.models.PatientInfo;
import com.jishi.reservation.dao.models.Register;
import com.jishi.reservation.service.DoctorService;
import com.jishi.reservation.service.OutpatientQueueService;
import com.jishi.reservation.service.PatientInfoService;
import com.jishi.reservation.service.RegisterService;
import com.jishi.reservation.service.jinxin.bean.QueueCurrentNumber;
import com.jishi.reservation.service.jinxin.bean.QueueDetail;
import com.jishi.reservation.service.support.JpushSupport;
import com.jishi.reservation.controller.protocol.OutpatientQueueDetailVO;
import com.jishi.reservation.worker.model.PushData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by liangxiong on 2017/11/10.
 */
@Component
@Slf4j
public class OutpatientQueueWorker {


    @Autowired
    public RegisterService registerService;

    @Autowired
    private JpushSupport jpushSupport;

    @Autowired
    private AccountMapper accountMapper;

    @Autowired
    private PatientInfoService patientInfoService;

    @Autowired
    private OutpatientQueueService outpatientQueueService;


    // 根据查询的已修改的医生门诊号序进行推送
    //@Scheduled(cron = "0 0/1 8-22 * * ? ")
    public void doWork() throws Exception {
        List<QueueCurrentNumber> queueCurrentNumberList = outpatientQueueService.queryModifiedVisitCurrentNum();
        if (queueCurrentNumberList == null || queueCurrentNumberList.isEmpty()) {
            return;
        }
        log.info("=================OutpatientQueueWorker begin=====================");
        log.info("BeginTime: " + new Date() + "QueueCurrentNumber size: " + queueCurrentNumberList.size());
        for (QueueCurrentNumber currentNumber : queueCurrentNumberList) {
            List<OutpatientQueueDetailVO> queueDetailList = outpatientQueueService.queryQueueByDoctorHisId(currentNumber.getDoctorHisId());
            if (queueDetailList == null || queueDetailList.isEmpty()) {
                continue;
            }
            for (OutpatientQueueDetailVO detail : queueDetailList) {
                PatientInfo patientInfo = patientInfoService.queryByBrIdAndAccountId(detail.getBrId(),detail.getAccountId());
                if (patientInfo == null) {
                    log.info("当前病人未添加, brid: " + detail.getBrId());
                    continue;
                }
                Account account = accountMapper.queryById(patientInfo.getAccountId());
                if (account == null) {
                    log.warn("当前病人账号为空, brid: " + detail.getBrId());
                    continue;
                }
                String pushMessage = PushData.create().msgType(PushData.PushDataMsgTypeDef.PUSH_DATA_OUTPATIENT_QUEUE).content(detail).toJSON();
                log.info("accountId: " + account.getId() + " msg: " + pushMessage);
                jpushSupport.sendPushAsyn(account.getPushId(), pushMessage);
            }
        }
        log.info("EndTime: " + new Date());
        log.info("=================OutpatientQueueWorker End=====================");
    }

    //  测试推送接口
    //@Scheduled(cron = "0 0/1 8-22 * * ? ")
    public void doWorkTest() throws Exception {
        List<OutpatientQueueDetailVO> queueDetailList = generateTestData();
        if (queueDetailList == null || queueDetailList.isEmpty()) {
          return;
        }
        log.info("=================OutpatientQueueWorker begin=====================");
        log.info("BeginTime: " + new Date());
        for (OutpatientQueueDetailVO detail : queueDetailList) {

            PatientInfo patientInfo = patientInfoService.queryByBrIdAndAccountId(detail.getBrId(),detail.getAccountId());
            Account account = accountMapper.queryById(patientInfo.getAccountId());
            String pushMessage = PushData.create().msgType(PushData.PushDataMsgTypeDef.PUSH_DATA_OUTPATIENT_QUEUE).content(detail).toJSON();
            log.info("accountId: " + account.getId() + " msg: " + pushMessage);
            jpushSupport.sendPush(account.getPushId(), pushMessage);
        }
        log.info("EndTime: " + new Date());
        log.info("=================OutpatientQueueWorker End=====================");
    }

    // 生成测试数据
    private List<OutpatientQueueDetailVO> generateTestData() throws Exception {
        List<Long> accIds = Arrays.asList(24L, 21L, 30L);
        List<Register> registerList = new ArrayList<>();
        for (Long accId : accIds) {
            List<Register> data = registerService.queryRegister(null, accId, 2, 0);
            //每个账号取两条数据
            if (data != null && !data.isEmpty()) {
                if (data.size() > 2) {
                    registerList.addAll(data.subList(data.size() - 2, data.size()));
                } else {
                    registerList.addAll(data);
                }
            }
        }
        List<OutpatientQueueDetailVO> detailList = new ArrayList<OutpatientQueueDetailVO>();
        for (Register register : registerList) {
            OutpatientQueueDetailVO vo = new OutpatientQueueDetailVO();
            vo.setBrId(register.getBrId());
            vo.setDepartId(Long.valueOf(register.getDepartmentId()));
            vo.setDepartName(register.getDepartment());
            //vo.setDoctorId(register.getDoctorId());
            vo.setDoctorHisId(register.getDoctorId());
            vo.setDoctorName(register.getDoctorName());
            vo.setDoctorTitle("主任医师");
            vo.setName(register.getPatientName());
            vo.setQueueeMinder("您预约的门诊正在排队，请注意！");
            vo.setQueueInfo("您预约的门诊正在排队，请注意！");
            vo.setRegisterDate(register.getCreateTime());
            vo.setRegisterType("普通");
            vo.setCurrentNum(0);
            vo.setQueueNum(Integer.parseInt(register.getHm()));
            vo.setAccountId(register.getAccountId());

            vo.setCurrentNum(0);
            vo.setQueueNum(12);
            vo.setNeedWaitNum(6);
            vo.setStatus(1);

            detailList.add(vo);
        }
        return detailList;
    }
}

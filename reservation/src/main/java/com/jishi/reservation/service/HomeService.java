package com.jishi.reservation.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.base.Preconditions;
import com.jishi.reservation.controller.base.Paging;
import com.jishi.reservation.dao.mapper.BannerMapper;
import com.jishi.reservation.dao.models.Banner;
import com.jishi.reservation.service.enumPackage.EnableEnum;
import com.jishi.reservation.util.Helpers;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zbs on 2017/8/10.
 */
@Service
@Log4j
public class HomeService {

    @Autowired
    BannerMapper bannerMapper;

    /**
     * 增加banner
     * @param bannerUrl
     * @param orderNumbe
     */
    public void addBanner(String bannerUrl, String orderNumbe) {
        log.info("增加banner"+" url:"+bannerUrl+" orderNumber:"+orderNumbe);
        if(Helpers.isNull(bannerUrl) || Helpers.isNull(orderNumbe))
            return;
        Banner addBanner = new Banner();
        addBanner.setBannerUrl(bannerUrl);
        addBanner.setOrderNumbe(orderNumbe);
        addBanner.setEnable(EnableEnum.EFFECTIVE.getCode());
        bannerMapper.insert(addBanner);
    }

    /**
     * 修改banner
     * @param bannerId
     * @param bannerUrl
     * @param orderNumbe
     */
    public void modifyBanner(Long bannerId, String bannerUrl, String orderNumbe) {
        log.info("修改banner:"+bannerId+" url:"+bannerUrl+" orderNumber:"+orderNumbe);
        if(Helpers.isNull(bannerId))
            return;
        Banner oldBanner = bannerMapper.selectByPrimaryKey(bannerId);
        if (!Helpers.isNull(oldBanner)) {
            Banner newBanner = new Banner();
            newBanner.setId(bannerId);
            newBanner.setOrderNumbe(Helpers.isNullOrEmpty(orderNumbe) ? oldBanner.getOrderNumbe() : orderNumbe);
            newBanner.setBannerUrl(Helpers.isNullOrEmpty(bannerUrl) ? oldBanner.getBannerUrl() : bannerUrl);
            Preconditions.checkState(bannerMapper.updateByPrimaryKeySelective(newBanner) ==1,"更新失败");
        }
    }

    /**
     * 查询banner
     * @param bannerId
     * @return
     */
    public List<Banner> queryBanner(Long bannerId,Integer enable) {
        log.info("查询banner:"+bannerId);
        Banner queryBanner = new Banner();
        queryBanner.setEnable(enable);
        queryBanner.setId(bannerId);
        return bannerMapper.select(queryBanner);
    }

    /**
     * 查询banner,分页
     * @param bannerId
     * @param paging
     * @return
     */
    public PageInfo<Banner> queryBannerPageInfo(Long bannerId,Integer enable, Paging paging){
        if(paging != null)
            PageHelper.startPage(paging.getPageNum(),paging.getPageSize(),paging.getOrderBy());
        return new PageInfo(queryBanner(bannerId,enable));
    }

    /**
     * 删除单个banner
     * @param bannerId
     */
    public void deleteBanner(Long bannerId) {
        log.info("删除banner:"+bannerId);
        if(Helpers.isNull(bannerId))
            return;
        bannerMapper.deleteByPrimaryKey(bannerId);
    }
}

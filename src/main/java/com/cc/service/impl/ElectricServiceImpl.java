package com.cc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cc.domain.parm.LiverElectricParm;
import com.cc.domain.pojo.Electric;
import com.cc.domain.pojo.LiverAndHouse;
import com.cc.domain.parm.ElectricParm;
import com.cc.mapper.ElectricMapper;
import com.cc.mapper.LiverAndHouseMapper;
import com.cc.service.ElectricService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * 电费管理ServiceImpl层
 */
@Service
public class ElectricServiceImpl extends ServiceImpl<ElectricMapper, Electric> implements ElectricService {

    @Autowired
    private LiverAndHouseMapper liverAndHouseMapper;

    // 新增电费记录
    @Override
    public boolean saveElectric(Electric electric) {
        //根据房屋id查询业主
        LiverAndHouse liverAndHouse = getLiverAndHouseByHouseId(electric.getHouseId());
        if (liverAndHouse != null){
            //查询出的业主id设置到电费里
            electric.setLiverId(liverAndHouse.getLiverId());
            // 设置缴费时间
            electric.setElectricTime(new Date());
            //保存数据
            return baseMapper.insert(electric) >0;
        }
        return false;
    }

    // 编辑电费记录
    @Override
    public boolean updateElectric(Electric electric) {
        //根据房屋id查询业主
        LiverAndHouse houseByHouseId = getLiverAndHouseByHouseId(electric.getHouseId());
        if (houseByHouseId != null) {
            //查询出的业主id设置到电费里
            electric.setLiverId(houseByHouseId.getLiverId());
            //修改数据
            return baseMapper.updateById(electric) > 0;
        }
        return false;
    }

    // 删除电费记录
    @Override
    public boolean removeElectricById(Integer electricId) {
        //如果已经缴费，就不能删除
        QueryWrapper<Electric> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(Electric::getElectricId, electricId)
                .eq(Electric::getElectricStatus, "0"); // 仅删除未缴费的记录
        return baseMapper.delete(wrapper) > 0;
    }

    // 分页查询电费记录
    @Override
    public IPage<Electric> getList(ElectricParm electricParm) {
        validatePageParams(electricParm);
        IPage<Electric> page = new Page<>(electricParm.getCurrentPage(), electricParm.getPageSize());
        return baseMapper.getElectricList(page,electricParm.getLiverName(), electricParm.getHouseNum());
    }

    // 业主电费记录查询
    @Override
    public IPage<Electric> getLiverElectricList(LiverElectricParm liverElectricParm) {
        validatePageParams(liverElectricParm);
        IPage<Electric> page = new Page<>(liverElectricParm.getCurrentPage(), liverElectricParm.getPageSize());
        QueryWrapper<Electric> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(Electric::getLiverId, liverElectricParm.getLiverId());
        return baseMapper.selectPage(page, wrapper);
    }


    private LiverAndHouse getLiverAndHouseByHouseId(Integer houseId){
        QueryWrapper<LiverAndHouse> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(LiverAndHouse::getHouseId, houseId)
               .eq(LiverAndHouse::getLiverHouseStatus, "0");
        return liverAndHouseMapper.selectOne(wrapper);
    }
    // 私有方法：校验分页参数
    private void validatePageParams(Object param){
        if(param instanceof ElectricParm || param instanceof  LiverElectricParm){
            Long currentPage = ((ElectricParm) param).getCurrentPage();
            Long pageSize = ((ElectricParm) param).getPageSize();
            if(currentPage <1 || pageSize <1){
                throw new IllegalArgumentException("分页参数不合法");
            }
        }
    }

}

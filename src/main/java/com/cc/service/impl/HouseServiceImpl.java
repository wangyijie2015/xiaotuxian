package com.cc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cc.domain.pojo.House;
import com.cc.domain.parm.HouseParm;
import com.cc.mapper.HouseMapper;
import com.cc.service.HouseService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 房屋service实现类
 */
@Service
public class HouseServiceImpl extends ServiceImpl<HouseMapper, House> implements HouseService {

    //分页查询
    @Override
    public IPage<House> getList(HouseParm parm) {
        //构造分页对象
        return baseMapper.getList(new Page<>(parm.getCurrentPage(), parm.getPageSize()),
                parm.getBuildingName(), parm.getUnitName(), parm.getHouseNum(), parm.getHouseStatus());
    }

    //根据单元id获取房屋列表
    @Override
    public List getHouseList(House house) {
        //构造查询条件
        if(house == null || house.getUnitId() == null){
            return new ArrayList<>();
        }
        return baseMapper.selectList(new QueryWrapper<House>()
                .lambda()
                .eq(House::getUnitId,house.getUnitId()));
    }

    //删除
    @Override
    public boolean removeHouse(Integer houseId) {
        //构造查询条件
        return baseMapper.delete(new QueryWrapper<House>()
                .lambda()
                .eq(House::getHouseId,houseId)
                .eq(House::getHouseStatus,"0")) >0;
    }

    //入住率饼形图
    @Override
    public Map<String, Object> getHouseLiveStatus() {
        //返回结果容器
        Map<String,Object> resultMap = new HashMap<>();
        //示例容器
        List<String> houseStatus = new ArrayList<>();
        houseStatus.add("入住");
        houseStatus.add("未入住");

        resultMap.put("houseStatus",houseStatus);
        //数据容器
        List<Map<String,Object>> houseCount = new ArrayList<>();

        long notUseCount = baseMapper.selectCount(new QueryWrapper<House>().lambda().eq(House::getHouseStatus, "0"));
        houseCount.add(createStatusMap("未入住", notUseCount));

        // 查询入住的数量
        long useCount = baseMapper.selectCount(new QueryWrapper<House>().lambda().eq(House::getHouseStatus, "1"));
        houseCount.add(createStatusMap("入住", useCount));

        resultMap.put("houseCount", houseCount);
        return resultMap;
    }

    //小区人数统计饼形图
    @Override
    public Map<String, Object> getAllLiverCount() {
        //返回结果容器
        Map<String,Object> resultMap = new HashMap<>();
        //查询每栋楼宇的住户的数量
        List<Map<String,Object>> liverByBuildCount = baseMapper.getLiverCount();
        resultMap.put("liverByBuildCount",liverByBuildCount);
        //示例容器
        List<String> liverBuildList = new ArrayList<>();
        //小区所有的楼宇
        for (Map<String, Object> map : liverByBuildCount) {
            String name = (String) map.get("name");
            liverBuildList.add(name);
        }
        resultMap.put("liverBuildList",liverBuildList);
        return resultMap;
    }

    private Map<String,Object> createStatusMap(String name,long value){
        HashMap<String, Object> statusMap  = new HashMap<>();
        statusMap.put("name", name);
        statusMap.put("value", value);
        return statusMap;
    }

}

package com.cityfruit.warclock.service.impl;

import com.cityfruit.warclock.consts.ConstMap;
import com.cityfruit.warclock.entity.WarClockEntity;
import com.cityfruit.warclock.param.CreateOrUpdateCaseParam;
import com.cityfruit.warclock.play.AudioPlayRedio;
import com.cityfruit.warclock.repository.WarClockRepository;
import com.cityfruit.warclock.service.WarClockService;
import com.cityfruit.warclock.vo.ResultVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author TongTong
 * @date 2019/12/24
 */
@Service
@Slf4j
public class WarClockServiceImpl implements WarClockService {

    @Autowired
    private WarClockRepository warClockRepository;


    @Override
    public Map<String, Object> getLiveSafeTime() {
        //获取加载数据时间
        List<WarClockEntity> warClockEntities = warClockRepository.getUnDealCase();

        if (warClockEntities == null || warClockEntities.size() == 0) {
            //等于null的时候有两种情况 1.服务初始化从没发生过live问题 这种情况 初始化数据库数据 2.说明 live 环境问题都已经处理这个时候需要计算 安全时间
            List<WarClockEntity> warClockEntityList = warClockRepository.findAll();
            if (warClockEntityList == null || warClockEntityList.size() == 0) {
                //初始化数据
                initDataBase();
                return getSafeTime();
            } else {
                return getSafeTime();
            }
        } else {
            return getSafeTime();
        }

    }

    @Override
    public String createOrUpdateCases(CreateOrUpdateCaseParam param) throws IOException {
        //查询根据群组查询最新的一条记录然后判断状态
        WarClockEntity warClockEntity = warClockRepository.getNewCase(param.getTeam());
        Long now = System.currentTimeMillis();
        if (warClockEntity != null) {
            //判断状态
            if (param.getInput().contains("ok")) {
                AudioPlayRedio.stopPlay();
                AudioPlayRedio.okPlay();
                warClockEntity.setFinishTs(now);
                warClockEntity.setState(3);
                warClockRepository.saveAndFlush(warClockEntity);
            }
            if (param.getInput().contains("liver")) {
                AudioPlayRedio.startPlay();
                if (ObjectUtils.isEmpty(warClockEntity.getFinishTs())) {
                    log.info("创建一条脏数据 有一个case 没有完成");
                }
                WarClockEntity clockEntity = getInitClockEntity(param);
                warClockRepository.saveAndFlush(clockEntity);
            }
            if (param.getInput().contains("peach")) {
                AudioPlayRedio.stopPlay();
                warClockEntity.setStartDealTs(now);
                warClockEntity.setState(2);
                warClockRepository.saveAndFlush(warClockEntity);
            }
        } else {
            WarClockEntity clockEntity = getInitClockEntity(param);
            warClockRepository.saveAndFlush(clockEntity);
        }
        return "创建成功";
    }

    public WarClockEntity getInitClockEntity(CreateOrUpdateCaseParam param) {
        WarClockEntity warClockEntity = new WarClockEntity();
        Long now = System.currentTimeMillis();
        warClockEntity.setCreateTs(now);
        warClockEntity.setTeam(param.getTeam());
        warClockEntity.setState(1);
        warClockEntity.setLevel(param.getInput());
        warClockEntity.setCaseName(param.getCaseName());
        return warClockEntity;
    }

    public Map<String, Object> getSafeTime() {
        Map<String, Object> result = new HashMap<>();
        List<WarClockEntity> warClockEntities = warClockRepository.getLastDealCase();
        List<ResultVo> resultVoList = new ArrayList<ResultVo>();
        for (WarClockEntity warClockEntity : warClockEntities) {
            ResultVo resultVo = new ResultVo();
            resultVo.setTeam(warClockEntity.getTeam());
            resultVo.setState(ConstMap.stateMap.get(warClockEntity.getState()));
            if (warClockEntity.getState() == 2 || warClockEntity.getState() == 1) {
                resultVo.setDays("00");
                resultVo.setHours("00");
                resultVo.setMinutes("00");
            } else {
                Long intervalTime = System.currentTimeMillis() - warClockEntity.getFinishTs();
                Integer day = Math.toIntExact(intervalTime / 1000 / 60 / 60 / 24);
                resultVo.setDays(day + "");
                Integer hour = Math.toIntExact((intervalTime - day * 24 * 60 * 60 * 1000) / 1000 / 60 / 60);
                resultVo.setHours(hour + "");
                Integer minute = Math.toIntExact((intervalTime - day * 24 * 60 * 60 * 1000 - hour * 60 * 60 * 1000) / 1000 / 60);
                resultVo.setMinutes(minute + "");
            }
            resultVoList.add(resultVo);
        }
        result.put("data", resultVoList);
        return result;
    }

    public void initDataBase() {
        //如果数据库中不存在数据进行初始化
        WarClockEntity initWarClockEntity = new WarClockEntity();
        initWarClockEntity.setCaseName("初始化数据");
        initWarClockEntity.setCreateTs(System.currentTimeMillis());
        initWarClockEntity.setFinishTs(System.currentTimeMillis());
        initWarClockEntity.setLevel("level0");
        initWarClockEntity.setStartDealTs(System.currentTimeMillis());
        initWarClockEntity.setTeam("friday");
        initWarClockEntity.setState(3);
        warClockRepository.saveAndFlush(initWarClockEntity);
    }

}

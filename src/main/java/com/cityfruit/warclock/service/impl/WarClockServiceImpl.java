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
                resultVo.setSeconds("00");
            } else {
                Long intervalTime = System.currentTimeMillis() - warClockEntity.getFinishTs();
                Integer day = Math.toIntExact(intervalTime / 1000 / 60 / 60 / 24);
                resultVo.setDays(day + "");
                Integer hour = Math.toIntExact((intervalTime - day * 24 * 60 * 60 * 1000) / 1000 / 60 / 60);
                resultVo.setHours(hour + "");
                Integer minute = Math.toIntExact((intervalTime - day * 24 * 60 * 60 * 1000 - hour * 60 * 60 * 1000) / 1000 / 60);
                resultVo.setMinutes(minute + "");
                Integer second = Math.toIntExact((intervalTime - day * 24 * 60 * 60 * 1000 - hour * 60 * 60 * 1000 - minute * 60 * 1000) / 1000);
                resultVo.setSeconds(second + "");
            }
            resultVoList.add(resultVo);
        }
        result.put("data", resultVoList);
        return result;
    }

    /**
     * 获取最长稳定时长
     *
     * @return 各团队最长稳定时长
     */
    @Override
    public Map<String, Object> getLongestSafeTime() {
        Map<String, Object> result = new HashMap<>(7);
        List<ResultVo> resultVoList = new ArrayList<>();
        //获取加载数据时间
        List<WarClockEntity> warClockEntities = warClockRepository.getUnDealCase();
        // 是否需要初始化数据库数据
        if (warClockEntities == null || warClockEntities.size() == 0) {
            //等于null的时候有两种情况 1.服务初始化从没发生过live问题 这种情况 初始化数据库数据 2.说明 live 环境问题都已经处理这个时候需要计算 安全时间
            List<WarClockEntity> warClockEntityList = warClockRepository.findAll();
            if (warClockEntityList.size() == 0) {
                //初始化数据
                initDataBase();
            }
        }
        // 获取所有团队
        List<String> teamList = getTeams();
        for (String team : teamList) {
            ResultVo resultVo = getLongestSafeTimeByTeam(team);
            resultVoList.add(resultVo);
        }
        result.put("data", resultVoList);
        return result;
    }

    /**
     * 获取团队
     *
     * @return 团队名称 List
     */
    public List<String> getTeams() {
        List<WarClockEntity> warClockEntities = warClockRepository.getLastDealCase();
        List<String> teamList = new ArrayList<>();
        for (WarClockEntity warClockEntity : warClockEntities) {
            teamList.add(warClockEntity.getTeam());
        }
        return teamList;
    }

    /**
     * 根据团队名称获取最长稳定时长
     *
     * @param team 团队名称
     * @return resultVo
     */
    public ResultVo getLongestSafeTimeByTeam(String team) {
        List<WarClockEntity> warClockEntities = warClockRepository.getAllByTeam(team);
        // Live OK 最长持续时间
        long longestSafeTime = 0;
        // Live 当前状态为 OK
        assert warClockEntities != null;
        if (warClockEntities.get(0).getState() == 3) {
            // 获取当前持续时长，遍历历史 case 寻找最长持续时长
            for (int i = 0; i < warClockEntities.size(); i++) {
                // 当前持续时长
                if (i == 0) {
                    longestSafeTime = System.currentTimeMillis() - warClockEntities.get(i).getFinishTs();
                }
                // 历史 case 距离下一次 Live 状态为 OK 的时长，并且过滤掉为解决的 case
                else if (warClockEntities.get(i).getState() == 3){
                    long stableMilliseconds = warClockEntities.get(i - 1).getCreateTs() - warClockEntities.get(i).getFinishTs();
                    // 判断历史 Live OK 持续时长是否为最长持续时长
                    if (stableMilliseconds > longestSafeTime) { longestSafeTime = stableMilliseconds; }
                }
            }
        }
        // Live 当前状态不 OK
        else {
            // 遍历历史 case 寻找最长持续时长
            for (int i = 1; i < warClockEntities.size(); i++) {
                // 历史 case 距离下一次 Live 状态为 OK 的时长，并且过滤掉为解决的 case
                if (warClockEntities.get(i).getState() == 3) {
                    long stableMilliseconds = warClockEntities.get(i - 1).getCreateTs() - warClockEntities.get(i).getFinishTs();
                    // 判断历史 Live OK 持续时长是否为最长持续时长
                    if (stableMilliseconds > longestSafeTime) { longestSafeTime = stableMilliseconds; }
                }
            }
        }
        ResultVo resultVo = new ResultVo();
        resultVo.setTeam(team);
        Integer day = Math.toIntExact(longestSafeTime / 1000L / 60L / 60L / 24L);
        resultVo.setDays(day + "");
        Integer hour = Math.toIntExact((longestSafeTime - day * 24L * 60L * 60L * 1000L) / 1000L / 60L / 60L);
        resultVo.setHours(hour + "");
        Integer minute = Math.toIntExact((longestSafeTime - day * 24L * 60L * 60L * 1000L - hour * 60L * 60L * 1000L) / 1000L / 60L);
        resultVo.setMinutes(minute + "");
        Integer second = Math.toIntExact((longestSafeTime - day * 24L * 60L * 60L * 1000L - hour * 60L * 60L * 1000L - minute * 60L * 1000L) / 1000L);
        resultVo.setSeconds(second + "");
        return resultVo;
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

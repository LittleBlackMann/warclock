package com.cityfruit.warclock.repository;

import com.cityfruit.warclock.entity.WarClockEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author TongTong
 * @date 2019/12/24
 */
@Repository
public interface WarClockRepository extends JpaRepository<WarClockEntity, Integer> {

    @Query(value = "select w1.* from  warclock w1 inner join (select id,  max(create_ts) maxts from warclock where create_ts is not null and (start_deal_ts is null or finish_ts is null) group by team) as w2 on w1.create_ts = w2.maxts", nativeQuery = true)
    public List<WarClockEntity> getUnDealCase();


    @Query(value = "select w1.* from  warclock w1 inner join (select id,  max(create_ts) maxts from warclock where create_ts is not null) as w2 on w1.create_ts = w2.maxts", nativeQuery = true)
    public List<WarClockEntity> getLastDealCase();


    @Query(value = "select a.* from warclock a inner join (select id ,max(create_ts) maxts from warclock where team=?1 ) as b on a.create_ts = b.maxts\n", nativeQuery = true)
    public WarClockEntity getNewCase(String team);
}

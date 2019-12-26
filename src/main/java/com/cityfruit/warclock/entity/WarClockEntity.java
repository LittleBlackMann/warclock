package com.cityfruit.warclock.entity;

import lombok.Data;
import org.springframework.boot.autoconfigure.domain.EntityScan;

import javax.persistence.*;

/**
 * @author TongTong
 * @date 2019/12/24
 */
@Entity
@Data
@Table(name = "warclock", schema = "develop")
public class WarClockEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "team")
    private String team;

    @Column(name = "case_name")
    private String caseName;

    @Column(name = "level")
    private String level;

    @Column(name = "create_ts")
    private Long CreateTs;

    @Column(name = "start_deal_ts")
    private Long startDealTs;

    @Column(name = "finish_ts")
    private Long finishTs;

    @Column(name = "state")
    private Integer state;
}

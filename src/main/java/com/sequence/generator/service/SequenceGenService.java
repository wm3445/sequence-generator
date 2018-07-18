package com.sequence.generator.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author wangmeng
 *
 * create_primary 表结构为
 * id   stub
 * 主键  唯一索引
 * 利用mysql REPLACE INTO create_primary (stub) VALUES ( ? ) 特性
 * 每次匹配stub发现重复 会先删除历史记录，在添加一条新纪录，达到类似原地主键递增效果
 * 这样每台机器只操作自己的这条记录，不干扰其他机器。
 * 设置号段间隔为1000，生成号段 n*1000 -> (n+1)*1000
 * 存储到内存中，利用Atomic包中AtomicLong递增id
 * 如果达到最大id则从数据库重新获取号段，存储到内存中。
 *
 * 缺点：服务器重启会丢失部分id
 *
 */
@Service
public class SequenceGenService implements CommandLineRunner {

    @Autowired
    JdbcTemplate jdbcTemplate;

    ConcurrentMap<String, AtomicLong> currentId = new ConcurrentHashMap<>();

    ConcurrentMap<String, AtomicLong> currentMaxId = new ConcurrentHashMap<>();

    private static int segment = 1000;

    static final String lock = "";

    private void init() {
        List<Map<String, Object>> maps = jdbcTemplate.queryForList("select id,stub from create_primary");
        for (Map<String, Object> map : maps) {
            int machineId = Integer.valueOf(map.get("id") + "");
            String ip = map.get("stub") + "";
            AtomicLong atomic = new AtomicLong();
            AtomicLong atomicMax = new AtomicLong();
            atomic.set(machineId * segment);
            atomicMax.set((long) ((machineId + 1) * segment));
            currentId.put(ip, atomic);
            currentMaxId.put(ip, atomicMax);
        }
        System.out.println("-=-=-=-=-=初始化-=-=-=-=-=");
    }

    public void initNum(String ip) {
        System.out.println("-=-=-=-=-=-initNum-=-=-=-=-=");
        int machineId = 0;
        String sql = " REPLACE INTO create_primary (stub) VALUES ( ? )";
        int update = jdbcTemplate.update(sql, new Object[]{ip});
        if (update > 0) {
            List<Map<String, Object>> queryForList = jdbcTemplate.queryForList(" select id from create_primary where stub = ? ", new Object[]{ip});
            if (queryForList.size() > 0) {
                machineId = Integer.valueOf(queryForList.get(0).get("id") + "");
                long uid = machineId * segment;
                AtomicLong atomic = new AtomicLong();
                atomic.set(uid);
                AtomicLong atomicMax = new AtomicLong();
                atomicMax.set((long) ((machineId + 1) * segment));
                currentId.put(ip, atomic);
                currentMaxId.put(ip, atomicMax);
            }
        }
    }

    public String doWork(String ip) {
        Long uid = 0L;
        synchronized (lock) {
            uid = currentId.get(ip).get();
            Long maxUid = currentMaxId.get(ip).get();
            while (uid >= maxUid) {
                initNum(ip);
                uid = currentId.get(ip).get();
                maxUid = currentMaxId.get(ip).get();
            }
            uid = currentId.get(ip).incrementAndGet();
        }
        return uid.toString();
    }

    @Override
    public void run(String... args) throws Exception {
        init();
    }
}

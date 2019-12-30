package com.legenda.lee.study_drools6.studydrools.loader.impl;


import com.legenda.lee.study_drools6.studydrools.loader.DrlLoader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;


@Slf4j
@Service
public class DrlLoaderImpl implements DrlLoader {

    @Override
    public Map<String, String> loadDrlMap() {
        log.info("start to load drls from path ...");

        // 此处目前是写死初始化的drl，后面会是自动生成的drl文件和加载
        Map<String, String> drlMap = new HashMap<>();
        drlMap.put("rule_package_1", "drls/rule_package_1.drl");
        // drlMap.put("rule_package_2", "drls/rule_package_2");
        // drlMap.put("rule_package_3", "drls/rule_package_3");

        return drlMap;
    }

}

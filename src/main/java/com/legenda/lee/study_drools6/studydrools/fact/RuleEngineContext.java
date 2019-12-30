
package com.legenda.lee.study_drools6.studydrools.fact;

import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Legenda-Lee
 * @date 2019-12-27 14:31:28
 * @description
 */
@Data
public class RuleEngineContext {

    private Map<String, Object> inputRiskFeatureKeyValue = new HashMap<>();

    private Map<String, Object> outputRiskFeatureKeyValue = new HashMap<>();


    private List<String> logList = new ArrayList<>();


    public void insertLog(String log) {
        this.logList.add(log);
    }

}
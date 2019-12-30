package com.legenda.lee.studydrools2.loader.impl;

import com.netease.wyxd.xiezhi.common.enums.RiskModelRuleTypeEnum;
import com.netease.wyxd.xiezhi.common.enums.RiskModelTecTypeEnum;
import com.netease.wyxd.xiezhi.domain.common.model.rule.SubRiskModel;
import com.netease.wyxd.xiezhi.engine.paramset.BaseParamSet;
import com.netease.wyxd.xiezhi.engine2.kernel.DrlLoader;
import com.netease.wyxd.xiezhi.service.common.riskmodel.RiskModelService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Slf4j
public class DrlModelLoader implements DrlLoader{



    @Override
    public Map<String, String> loadDrlMap() {
        log.info("start to load drls from database ...");
        List<SubRiskModel> riskModelList = riskModelService.findAllModels();
        Map<String,String> map = new HashMap<>();
        if (CollectionUtils.isNotEmpty(riskModelList)) {
            for (SubRiskModel riskModel : riskModelList) {
                Collection<BaseParamSet> act = null;
                Map<String,String> ext = new HashMap<>();
                if (riskModel.getTecType().equals(RiskModelTecTypeEnum.DECISION_TABLE)){
                    if (StringUtils.isNotEmpty(riskModel.getRhsMethod())){
                        ext.put("modelRHS",riskModel.getRhsMethod());
                    }
                    act = engineRuleLoader.loadRule(RiskModelRuleTypeEnum.DECISION_TABLE_RULE,riskModel.getId(),ext);
                }
                if (riskModel.getTecType().equals(RiskModelTecTypeEnum.POINT_TABLE)){
                    if (StringUtils.isNotEmpty(riskModel.getRhsMethod())){
                        ext.put("modelRHS",riskModel.getRhsMethod());
                    }
                    act = engineRuleLoader.loadRule(RiskModelRuleTypeEnum.POINT_TABLE_RULE,riskModel.getId(),ext);
                }
                if (CollectionUtils.isNotEmpty(act)){
                    String drl = drlCompiler.drlCompile(act);
                    if (StringUtils.isNotEmpty(drl)) {
                        map.put(riskModel.getCode() + "-" + riskModel.getModelVersion(), drl);
                    }else{
                        throw new IllegalArgumentException("drools file can not be null");
                    }
                }
            }
        }
        return map;
    }

}

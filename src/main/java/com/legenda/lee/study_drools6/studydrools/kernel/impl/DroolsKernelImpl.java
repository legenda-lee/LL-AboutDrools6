package com.legenda.lee.study_drools6.studydrools.kernel.impl;

import com.legenda.lee.study_drools6.studydrools.fact.RuleEngineContext;
import com.legenda.lee.study_drools6.studydrools.kernel.RuleEngineKernel;
import com.legenda.lee.study_drools6.studydrools.loader.DrlLoader;
import lombok.extern.slf4j.Slf4j;
import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderError;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.io.ResourceFactory;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.rule.FactHandle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;


/**
 * @author Legenda-Lee(lee.legenda@gmail.com)
 * @date 2019-12-30 10:36:37
 * @description
 */
@Slf4j
@Service(value = "droolsKernel")
public class DroolsKernelImpl implements RuleEngineKernel {

    @Autowired
    private DrlLoader drlLoader;


    private final ConcurrentMap<String, KnowledgeBase> kieBaseContainer = new ConcurrentHashMap<>();


    @PostConstruct
    @Override
    public void start() {
        log.info("start kernel(drools)");

        refreshAll();

        log.info("kernel(drools) is started");
    }

    @Override
    public void stop() {
        log.info("stop kernel(drools)");

        if (!CollectionUtils.isEmpty(kieBaseContainer.entrySet())) {
            for (Map.Entry<String, KnowledgeBase> entry : kieBaseContainer.entrySet()) {
                kieBaseContainer.remove(entry.getKey());
            }
        }

        log.info("kernel(drools) is stopped");
    }


    @Override
    public void invoke(String rulePackageId, RuleEngineContext ruleEngineContext) {
        log.info("executing package [id={}]", rulePackageId);


        if (kieBaseContainer.isEmpty()) {
            log.error("rule engine not initialized yet");
            return;
        }
        if (kieBaseContainer.get("rule_package_" + rulePackageId) == null) {
            log.error("rulePackage[id={}] has not registered yet, call refresh(rulePackageId) to register model into engine", rulePackageId);
            return;
        }
        try {
            KnowledgeBase kieBase = kieBaseContainer.get("rule_package_" + rulePackageId);
            StatefulKnowledgeSession kieSession = kieBase.newStatefulKnowledgeSession();
            FactHandle factHandle = kieSession.insert(ruleEngineContext);
            kieSession.fireAllRules();
            kieSession.retract(factHandle);
            kieSession.dispose();

            System.out.println(ruleEngineContext);
        } catch (Exception e) {
            log.error("Drools evaluation failed due to ", e);
        }

        log.info("rulePackage[id={}] is finished", rulePackageId);
    }

    /**
     * @author Legenda-Lee(lee.legenda@gmail.com)
     * @date 2019-12-30 10:33:09
     * @description 刷新一个规则包的规则
     */
    @Override
    public void refresh(String rulePackageId) {
        // KnowledgeBase kieBase = initKieBase(drlLoader.loadDrl(rulePackageId));
        // if (kieBase == null) {
        //     return;
        // }
        // kieBaseContainer.replace(rulePackageId, kieBase);
        // kieBaseContainer.putIfAbsent(rulePackageId, kieBase);
    }

    /**
     * @author Legenda-Lee(lee.legenda@gmail.com)
     * @date 2019-12-30 10:33:11
     * @description 加载/刷新所有规则包的规则
     */
    @Override
    public void refreshAll() {
        Map<String, String> modelMaps = drlLoader.loadDrlMap();
        if (modelMaps != null && !CollectionUtils.isEmpty(modelMaps.entrySet())) {
            for (Map.Entry<String, String> modelMap : modelMaps.entrySet()) {
                log.info("start to compile RulePackage : [{}]", modelMap.getKey());
                KnowledgeBase kieBase = initKieBase(modelMap.getValue());
                if (kieBase == null) {
                    return;
                }
                kieBaseContainer.put(modelMap.getKey(), kieBase);
            }
        }
    }

    /**
     * @author Legenda-Lee(lee.legenda@gmail.com)
     * @date 2019-12-30 13:04:50
     * @description
     */
    private KnowledgeBase initKieBase(String rulePackageStr) {
        if (StringUtils.isEmpty(rulePackageStr)) {
            log.error("rulePackageStr string can't be null, please check again");
            return null;
        }
        KnowledgeBase kBase = KnowledgeBaseFactory.newKnowledgeBase();
        KnowledgeBuilder kBuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kBuilder.add(ResourceFactory.newClassPathResource(rulePackageStr, DroolsKernelImpl.class), ResourceType.DRL);
        if (kBuilder.hasErrors()) {
            StringBuilder buff = new StringBuilder();
            for (KnowledgeBuilderError err : kBuilder.getErrors()) {
                buff.append("error: [").append(err.getMessage()).append("],desc:[").append(err).append("]");
            }
            log.error("rule explain errors: {}", buff.toString());
            return null;
        }
        kBase.addKnowledgePackages(kBuilder.getKnowledgePackages());
        return kBase;
    }


}
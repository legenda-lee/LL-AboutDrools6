package com.legenda.lee.studydrools2.kernel.impl;

import com.legenda.lee.studydrools2.kernel.RuleEngineKernel;
import com.netease.wyxd.common.metric.Metrics;
import com.netease.wyxd.xiezhi.engine2.RiskModelResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderError;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.io.ResourceFactory;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.rule.FactHandle;

import java.io.StringReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;


/**
 * @author Legenda-Lee(lida@lianlianpay.com)
 * @date 2019-12-30 10:36:37
 * @description
 */
@Slf4j
public class DroolsKernelImpl implements RuleEngineKernel {

    private DrlLoader drlLoader;

    private final ConcurrentMap<String, KnowledgeBase> kieBaseContainer = new ConcurrentHashMap<>();


    public DroolsKernelImpl(DrlLoader drlLoader) {
        this.drlLoader = drlLoader;
    }

    @Override
    public void start() {
        log.info("start kernel(drools)");

        long ms = Metrics.timer(this::refreshAll);

        log.info("kernel (drools) is started in {}ms", ms);
    }

    @Override
    public void stop() {
        log.info("stop kernel(drools)");
        long ms = Metrics.timer(() -> {
            if (CollectionUtils.isNotEmpty(kieBaseContainer.entrySet())) {
                for (Map.Entry<String, KnowledgeBase> entry : kieBaseContainer.entrySet()) {
                    kieBaseContainer.remove(entry.getKey());
                }
            }
        });
        log.info("kernel(drools) is stopped in {}ms", ms);
    }

    @Override
    public void invoke(String modelId, FactInfo fact, RiskModelResult<?> result) {
        log.info("executing model [id={}]", modelId);

        long ms = Metrics.timer("kernel.drools.model", () -> {
            RiskExecuteResultModel r = (RiskExecuteResultModel) result;
            if (kieBaseContainer.isEmpty()) {
                log.error("rule engine not initialized yet");
                throw new KernelException(modelId, KernelConstants.DROOLS_ENGINE_NOT_STARTED);
            }
            if (kieBaseContainer.get(modelId) == null) {
                log.error("Model has not registered yet, call refresh(model) to register model into engine");
                throw new KernelException(modelId, KernelConstants.MODEL_NOT_REGISTERED);
            }
            try {
                KnowledgeBase kieBase = kieBaseContainer.get(modelId);
                StatefulKnowledgeSession kieSession = kieBase.newStatefulKnowledgeSession();
                FactHandle factHandle = kieSession.insert(fact);
                FactHandle factHandle1 = kieSession.insert(r);
                kieSession.fireAllRules();
                kieSession.retract(factHandle);
                kieSession.retract(factHandle1);
                kieSession.dispose();
            } catch (Exception e) {
                log.error("Drools evaluation failed due to ", e);
                throw new KernelException(modelId, KernelConstants.SYSTEM_ERROR, e);
            }
        });

        log.info("model [id={}] is finished in {}ms", modelId, ms);
    }

    @Override
    public void refresh(String modelId) {
        KnowledgeBase kieBase = initKieBase(drlLoader.loadDrl(modelId));
        if (kieBase == null) {
            throw new KernelException(modelId, KernelConstants.DRL_COMPILE_ERROR);
        }
        kieBaseContainer.replace(modelId, kieBase);
        kieBaseContainer.putIfAbsent(modelId, kieBase);
    }

    @Override
    public void refreshAll() {
        Map<String, String> modelMaps = drlLoader.loadDrlMap();
        if (modelMaps != null && CollectionUtils.isNotEmpty(modelMaps.entrySet())) {
            for (Map.Entry<String, String> modelMap : modelMaps.entrySet()) {
                log.info("start to compile model : [{}]",modelMap.getKey());
                KnowledgeBase kieBase = initKieBase(modelMap.getValue());
                if (kieBase == null) {
                    throw new KernelException(modelMap.getKey(), KernelConstants.DRL_COMPILE_ERROR);
                }
                kieBaseContainer.put(modelMap.getKey(), kieBase); // override
            }
        }
    }

    private KnowledgeBase initKieBase(String modelStr) {
        if (StringUtils.isEmpty(modelStr)) {
            log.error("model string can't be null, please check again");
            return null;
        }
        KnowledgeBase kBase = KnowledgeBaseFactory.newKnowledgeBase();
        KnowledgeBuilder kBuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kBuilder.add(ResourceFactory.newReaderResource(new StringReader(modelStr)), ResourceType.DRL);
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

    public DrlLoader getDrlLoader() {
        return drlLoader;
    }

    public void setDrlLoader(DrlLoader drlLoader) {
        this.drlLoader = drlLoader;
    }

    public static void main(String[] args) {
        HashMap map=new HashMap();
        HashSet set=new HashSet();

    }
}
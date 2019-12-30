
package com.legenda.lee.studydrools2;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.RuleBase;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.io.impl.ClassPathResource;
import org.drools.runtime.StatefulKnowledgeSession;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Legenda-Lee
 * @date 2019-12-27 14:56
 * @description
 * @since 1.0.0
 */
public class Test {


    public static void main(String[] args) {
        KnowledgeBuilder kb= KnowledgeBuilderFactory.newKnowledgeBuilder();
        kb.add(new ClassPathResource("E:\\prjects\\GitHub\\LL-AboutDrools\\target\\classes\\Rule_01.drl"), ResourceType.DRL);
        Collection collection=kb.getKnowledgePackages();
        KnowledgeBase knowledgeBase= KnowledgeBaseFactory.newKnowledgeBase();

        knowledgeBase.addKnowledgePackages(collection);
        StatefulKnowledgeSession statefulSession=knowledgeBase.newStatefulKnowledgeSession();


        Map<String, Object> inputRiskFeatureKeyValue = new HashMap<>();
        inputRiskFeatureKeyValue.put("input_feature_xxx", 0.6);
        inputRiskFeatureKeyValue.put("input_feature_yyy", "连连");

        RuleEngineContext context = new RuleEngineContext();
        context.setInputRiskFeatureKeyValue(inputRiskFeatureKeyValue);

        System.out.println("执行规则引擎开始");
        statefulSession.insert(context);
        statefulSession.fireAllRules();
        statefulSession.dispose();
        System.out.println("执行规则引擎结束.");

        System.out.println("执行结果：" + context.getOutputRiskFeatureKeyValue());
        System.out.println("执行日志：" + context.getLogList());
    }



}

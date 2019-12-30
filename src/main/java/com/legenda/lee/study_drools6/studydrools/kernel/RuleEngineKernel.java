package com.legenda.lee.study_drools6.studydrools.kernel;


import com.legenda.lee.study_drools6.studydrools.fact.RuleEngineContext;

/**
 * @author Legenda-Lee(lee.legenda@gmail.com)
 * @date 2019-12-30 10:32:50
 * @description
 */
public interface RuleEngineKernel {

    /**
     * @author Legenda-Lee(lee.legenda@gmail.com)
     * @date 2019-12-30 10:32:57
     * @description
     */
    void start();

    /**
     * @author Legenda-Lee(lee.legenda@gmail.com)
     * @date 2019-12-30 10:33:01
     * @description
     */
    void stop();

    /**
     * @author Legenda-Lee(lee.legenda@gmail.com)
     * @date 2019-12-30 10:33:06
     * @description
     *
     * @param rulePackageId 规则包id
     * @return ruleEngineContext 规则执行上下文
     */
    void invoke(String rulePackageId, RuleEngineContext ruleEngineContext);

    /**
     * @author Legenda-Lee(lee.legenda@gmail.com)
     * @date 2019-12-30 10:33:09
     * @description 刷新一个规则包的规则
     */
    void refresh(String rulePackageId);

    /**
     * @author Legenda-Lee(lee.legenda@gmail.com)
     * @date 2019-12-30 10:33:11
     * @description 刷新所有规则包的规则
     */
    void refreshAll();

}

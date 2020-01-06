package com.legenda.lee.study_drools6.controller;

import com.legenda.lee.study_drools6.studydrools.fact.RuleEngineContext;
import com.legenda.lee.study_drools6.studydrools.kernel.RuleEngineKernel;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author Legenda-Lee
 * @date 2019-12-26 16:29
 * @description
 * @since 1.0.0
 */
@RestController
@RequestMapping("/home")
public class HomeController {

    @Resource(name = "droolsKernel")
    RuleEngineKernel droolsKernel;


    @RequestMapping(value = "/index", method = RequestMethod.GET)
    @ResponseBody
    public String home() {
        return "Hello World";
    }

    @RequestMapping(value = "/run/{rulePackageId}", method = RequestMethod.GET)
    @ResponseBody
    public String rule(@PathVariable String rulePackageId) {
        // 规则执行上下文
        Map<String, Object> inputRiskFeatureKeyValue = new HashMap<>();
        inputRiskFeatureKeyValue.put("input_feature_xxx", 0.4);
        inputRiskFeatureKeyValue.put("input_feature_yyy", "哈哈");
        inputRiskFeatureKeyValue.put("address1", "杭州");

        List<String> addressList  = new ArrayList<>();
        addressList.add("杭州");
        addressList.add("上海");

        RuleEngineContext context = new RuleEngineContext();
        context.setInputRiskFeatureKeyValue(inputRiskFeatureKeyValue);
        context.setAddressList(addressList);

        droolsKernel.invoke(rulePackageId, context);

        return "yes";
    }

    @RequestMapping(value = "/refreshAll", method = RequestMethod.GET)
    @ResponseBody
    public String refreshAll() {
        droolsKernel.refreshAll();
        return "yes";
    }

}

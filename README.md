## LL-AboutDrools6

- 0、使用的是drools的6.4.0.Final版本。
- 1、drl文件统一放置在resources/drls文件夹下。
- 2、一个规则包对应一个drl文件，drl文件名为：rule_package_规则包ID
- 3、系统初始化加载各个drl文件到内存。
- 4、当规则包下有规则进行修改时，可以触发加载该规则包的刷新重加载。

##测试方式：其中1为规则包ID
- http://localhost:8080/home/run/1
- http://localhost:8080/home/run/refreshAll
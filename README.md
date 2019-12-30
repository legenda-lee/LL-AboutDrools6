## LL-AboutDrools6

- 0、使用的是drools的6.4.0.Final版本。
- 1、drl文件统一放置在resources/drls文件夹下。
- 2、一个规则包对应一个drl文件，drl文件名为：rule_package_规则包ID
- 3、系统初始化加载各个drl文件到内存。
- 4、当规则包下有规则进行修改时，可以触发加载该规则包的刷新重加载。

## 测试方式
- rule_package_1.drl 测试的是比较操作符 >、 >=、 <、 <=、 = =、 !=以及调用外部util类的方法，util一般用来格式化特征指标值。
- 测试方式：http://localhost:8080/home/run/1
- -----------------------------------------------------------------------
- rule_package_2.drl 测试的是比较操作符contains、 not contains。
- 测试方式：http://localhost:8080/home/run/2
- -----------------------------------------------------------------------
- rule_package_3.drl 测试的是比较操作符memberof、not memberof。
- 测试方式：http://localhost:8080/home/run/3
- -----------------------------------------------------------------------
- rule_package_4.drl 测试的是比较操作符matches、not matches。
- 测试方式：http://localhost:8080/home/run/4
- -----------------------------------------------------------------------
- 刷新加载所有规则包drl文件的入口
- http://localhost:8080/home/run/refreshAll
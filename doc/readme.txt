#20180524
添加了cjShop-common-config工程,
本工程中删除:resources.properties, public_system.properties, 和db.properties.
改为从cjShop-common-config工程打成的jar包中引入资源文件.


#20180523
rest服务的生产者,通过dubbo暴露服务
服务类: com/cj/core/facade/ItemFacadeImpl.java
配置文件: spring/spring-dubbo-provider.xml
         properties/public_system.properties
启动类: DubboProvider.java



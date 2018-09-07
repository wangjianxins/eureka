Eureka
=====

# 个人博客

-------

![](http://www.yunai.me/images/common/wechat_mp.jpeg)

> 🙂🙂🙂关注**微信公众号：【芋艿的后端小屋】**有福利：  
> 1. RocketMQ / MyCAT / Sharding-JDBC **所有**源码分析文章列表  
> 2. RocketMQ / MyCAT / Sharding-JDBC **中文注释源码 GitHub 地址**  
> 3. 您对于源码的疑问每条留言**都**将得到**认真**回复。**甚至不知道如何读源码也可以请教噢**。  
> 4. **新的**源码解析文章**实时**收到通知。**每周更新一篇左右**。

-------

# 联系方式

* QQ : 7685413
* 微信 : wangwenbin-server
* 掘金-Java交流群 : 217878901
* 知识星球：![知识星球](http://www.iocoder.cn/images/Architecture/2017_12_29/01.png)

* 熔断器 **Hystrix**
    * [《Hystrix 源码解析 —— 调试环境搭建》](http://www.iocoder.cn/Hystrix/build-debugging-environment?github&1608)
    * [《Hystrix 源码解析 —— 执行命令方式》](http://www.iocoder.cn/Hystrix/command-execute-mode?github&1608)
    * [《Hystrix 源码解析 —— 执行结果缓存》](http://www.iocoder.cn/Hystrix/command-execute-result-cache?github&1608)
    * [《Hystrix 源码解析 —— 命令执行（一）之正常执行逻辑》](http://www.iocoder.cn/Hystrix/command-execute-first-run?github&1608)
    * [《Hystrix 源码解析 —— 命令执行（二）之执行隔离策略》](http://www.iocoder.cn/Hystrix/command-execute-second-isolation-strategy?github&1608)
    * [《Hystrix 源码解析 —— 命令执行（三）之执行超时》](http://www.iocoder.cn/Hystrix/command-execute-third-timeout?github&1608)
    * [《Hystrix 源码解析 —— 请求执行（四）之失败回退逻辑》](http://www.iocoder.cn/Hystrix/command-execute-fourth-fallback?github&1608)
    * [《Hystrix 源码解析 —— 命令合并执行》](http://www.iocoder.cn/Hystrix/command-collapser-execute?github&1608)
    * [《Hystrix 源码解析 —— 断路器 HystrixCircuitBreaker》](http://www.iocoder.cn/Hystrix/circuit-breaker?github&1608)

[![Build Status](https://netflixoss.ci.cloudbees.com/job/NetflixOSS/job/eureka/job/eureka-snapshot/badge/icon)](https://netflixoss.ci.cloudbees.com/job/NetflixOSS/job/eureka/job/eureka-snapshot/)

Eureka is a REST (Representational State Transfer) based service that is primarily used in the AWS cloud for locating services for the purpose of load balancing and failover of middle-tier servers.

At Netflix, Eureka is used for the following purposes apart from playing a critical part in mid-tier load balancing.

* For aiding Netflix Asgard - an open source service which makes cloud deployments easier, in  
    + Fast rollback of versions in case of problems avoiding the re-launch of 100's of instances which 
      could take a long time.
    + In rolling pushes, for avoiding propagation of a new version to all instances in case of problems.

* For our cassandra deployments to take instances out of traffic for maintenance.

* For our memcached caching services to identify the list of nodes in the ring.

* For carrying other additional application specific metadata about services for various other reasons.


Building
----------
The build requires java8 because of some required libraries that are java8 (servo), but the source and target compatibility are still set to 1.7.


Support
----------
[Eureka Google Group](https://groups.google.com/forum/?fromgroups#!forum/eureka_netflix)


Documentation
--------------
Please see [wiki](https://github.com/Netflix/eureka/wiki) for detailed documentation.

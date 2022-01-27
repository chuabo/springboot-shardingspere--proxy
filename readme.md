
# 本项目基于 Spring Boot 2.1.5 使用sharding-sphere-proxy + Mybatis-Plus 实现分库分表

##sharding-sphere-proxy(4.1.0版本)配置
server.yaml
authentication:
  users:
    root:
      password: root
    sharding:
      password: sharding
      authorizedSchemas: sharding_db

props:
  max.connections.size.per.query: 1
  acceptor.size: 16  # The default value is available processors count * 2.
  executor.size: 16  # Infinite by default.
  proxy.frontend.flush.threshold: 128  # The default value is 128.
    # LOCAL: Proxy will run with LOCAL transaction.
    # XA: Proxy will run with XA transaction.
    # BASE: Proxy will run with B.A.S.E transaction.
  proxy.transaction.type: LOCAL
  proxy.opentracing.enabled: false
  proxy.hint.enabled: false
  query.with.cipher.column: true
  #sql.show: false
  sql.show: true
  allow.range.query.with.inline.sharding: false

config-sharding.yaml配置
schemaName: sharding_db
dataSources:
  ds_0:
    url: jdbc:mysql://127.0.0.1:3306/ds0?serverTimezone=UTC&useSSL=false
    username: root
    password: root
    connectionTimeoutMilliseconds: 30000
    idleTimeoutMilliseconds: 60000
    maxLifetimeMilliseconds: 1800000
    maxPoolSize: 50
  ds_1:
    url: jdbc:mysql://127.0.0.1:3306/ds1?serverTimezone=UTC&useSSL=false
    username: root
    password: root
    connectionTimeoutMilliseconds: 30000
    idleTimeoutMilliseconds: 60000
    maxLifetimeMilliseconds: 1800000
    maxPoolSize: 50

shardingRule:
  tables:
    user:
      actualDataNodes: ds_${0..1}.user_${0..1}
      tableStrategy:
        inline:
          shardingColumn: id
          algorithmExpression: user_${id % 2}
      keyGenerator:
        type: SNOWFLAKE
        column: id
#   t_order_item:
#     actualDataNodes: ds_${0..1}.t_order_item_${0..1}
#     tableStrategy:
#       inline:
#         shardingColumn: order_id
#         algorithmExpression: t_order_item_${order_id % 2}
#     keyGenerator:
#       type: SNOWFLAKE
#       column: order_item_id
  bindingTables:
    #- t_order,t_order_item
    - user
  defaultDatabaseStrategy:
    inline:
      shardingColumn: id
      algorithmExpression: ds_${id % 2}
  defaultTableStrategy:
    none:

## 创建数据库ds0
CREATE DATABASE IF NOT EXISTS `ds0` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci */; 
USE `ds0`; SET NAMES utf8mb4; SET FOREIGN_KEY_CHECKS = 0; 
-- ---------------------------- 
-- Table structure for user_0 -- 
---------------------------- 
DROP TABLE IF EXISTS `user_0`; 
CREATE TABLE `user_0` ( `id` int(11) NOT NULL, `name` varchar(255) DEFAULT NULL, `age` int(11) DEFAULT NULL, PRIMARY KEY (`id`) ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4; 
-- ---------------------------- 
-- Table structure for user_1 -- 
---------------------------- 
DROP TABLE IF EXISTS `user_1`; 
CREATE TABLE `user_1` ( `id` int(11) NOT NULL, `name` varchar(255) DEFAULT NULL, `age` int(11) DEFAULT NULL, PRIMARY KEY (`id`) ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4; 
SET FOREIGN_KEY_CHECKS = 1;

## 创建数据库ds1
CREATE DATABASE IF NOT EXISTS `ds1` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci */; 
USE `ds1`; SET NAMES utf8mb4; SET FOREIGN_KEY_CHECKS = 0; 
-- ---------------------------- 
-- Table structure for user_0 -- 
---------------------------- 
DROP TABLE IF EXISTS `user_0`; 
CREATE TABLE `user_0` ( `id` int(11) NOT NULL, `name` varchar(255) DEFAULT NULL, `age` int(11) DEFAULT NULL, PRIMARY KEY (`id`) ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4; 
-- ---------------------------- 
-- Table structure for user_1 -- 
---------------------------- 
DROP TABLE IF EXISTS `user_1`; 
CREATE TABLE `user_1` ( `id` int(11) NOT NULL, `name` varchar(255) DEFAULT NULL, `age` int(11) DEFAULT NULL, PRIMARY KEY (`id`) ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4; 
SET FOREIGN_KEY_CHECKS = 1;

## 分库策略：根据id,偶数保存到数据库ds0,奇数保存到数据库ds01
ds$->{id % 2}

## 分片策略：
根据age,偶数保存到表user_0,奇数保存到user_1表
user_$->{age % 2}表示user表根据id模2，而分成2张表，表名称为user_0到user_1

## 根据id,偶数保存到表user_0,奇数保存到user_1表
user$->{id % 2} 表示user表根据id模2，而分成2张表，表名称为user0到user_1

## 添加测试数据
http://localhost:8080/insert?id=1&name=lhd&age=12    
http://localhost:8080/insert?id=2&name=lhd&age=13    
http://localhost:8080/insert?id=3&name=lhd&age=14    
http://localhost:8080/insert?id=4&name=lhd&age=15

## 查询测试数据
http://localhost:8080/select
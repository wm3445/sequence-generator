# sequence-generator


/**
 * @author wangmeng
 *
 * create_primary 表结构为
 * id   stub
 * 主键  唯一索引
 * 利用mysql REPLACE INTO create_primary (stub) VALUES ( ? ) 特性
 * 每次匹配stub发现重复 会先删除历史记录，在添加一条新纪录，达到类似原地主键递增效果
 * 这样每台机器只操作自己的这条记录，不干扰其他机器。
 * 设置号段间隔为1000，生成号段 n*1000 -> (n+1)*1000
 * 存储到内存中，利用Atomic包中AtomicLong递增id
 * 如果达到最大id则从数据库重新获取号段，存储到内存中。
 *
 * 缺点：服务器重启会丢失部分id
 *
 */

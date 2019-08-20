package com.one.redis;
import java.text.SimpleDateFormat; import java.util.ArrayList; import java.util.Date; import java.util.List;
import org.apache.commons.lang3.StringUtils; import org.apache.commons.lang3.time.DateUtils; import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import redis.clients.jedis.BitOP; import redis.clients.jedis.Jedis; import redis.clients.jedis.JedisPool; import redis.clients.jedis.Pipeline;
/**
Redis实现统计网站访问人数的功能
现在想要统计某一网站的累积访问用户人数和日均活跃人数（连续多少天访问该网站的人数），可以通过Redis来实现类似功能。
笔者使用的数据结构是Redis中的bitmap，其在大数据量下的空间占用量很小。大概思路就是每一位用户都是bitmap中的一位，为1就代表其访问了，为0就代表没访问。比如说现在有5位用户，第1、3位用户访问了，而2、4、5没访问，如果以索引位置作为其userId的话，那么bitmap存储的就是10100。
累计用户的key设置为“totalKey”，其值为到今天为止所有用户访问的信息，为1就代表其访问过该网站，为0就代表该用户直到今天都没有访问过该网站；日均活跃人数的key设置为“activeKey:[当前的日期]”，比如说2019年5月31日的日均活跃人数key为“activeKey:20190531”，2019年5月30日的日均活跃人数key为“activeKey:20190530”，等等。所以如果要统计日均活跃人数的话，只要将这几个key做交集就可以了（因为只有都为1，相与后结果才为1，如果有一个为0，相与后结果就不是1），然后统计交集结果的1的个数，结果即为统计值。
实现代码如下所示，在main函数中模拟了用户访问的情况。在2019年5月31日有userId为0到14一共15个人访问该网站，而在2019年5月30日有userId为6到14一共9个人访问过该网站：
统计累计和日均活跃用户人数
*/ 
public class RedisBitmapCounter {
/**
ip地址 / 
private static final String IP_ADDRESS = "192.168.253.129"; 
/*
端口号 / 
private static final int PORT = 6379; 
/*
jedis客户端 / 
private Jedis jedis;
/*
累计用户人数key / 
private static final String TOTAL_KEY = "totalKey";
/*
日均活跃用户人数key */ 
private static final String ACTIVE_KEY = "activeKey:";
public Counter() { 
GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig(); 
poolConfig.setMaxTotal(50); 
poolConfig.setMaxIdle(50); 
poolConfig.setMaxWaitMillis(1000); 
JedisPool jedisPool = new JedisPool(poolConfig, IP_ADDRESS, PORT); 
jedis = jedisPool.getResource(); 
}
/**
更新累计和日均活跃用户人数
@param userId 用户id
@param time 当前日期 */ 
private void updateUser(long userId, String time) { 
if (StringUtils.isBlank(time)) { 
SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd"); 
time = sdf.format(new Date()); } 
Pipeline pipeline = jedis.pipelined(); 
pipeline.setbit(TOTAL_KEY, userId, true); 
pipeline.setbit(ACTIVE_KEY + time, userId, true); 
pipeline.syncAndReturnAll(); 
}
/**
获取累计用户人数
@return 累计用户人数 
*/ private Long getTotalUserCount() { 
Pipeline pipeline = jedis.pipelined(); 
pipeline.bitcount(TOTAL_KEY); 
List totalKeyCountList = pipeline.syncAndReturnAll(); 
return (Long) totalKeyCountList.get(0); 
} 
/**
获取指定天数内的日均活跃人数
@param dayNum 指定天数
@return */ 
private Long getActiveUserCount(int dayNum) { 
if (dayNum < 1) { 
return (long) 0; 
} 
List pastDaysKey = new ArrayList<>(); 
SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd"); 
StringBuilder sb = new StringBuilder(); 
for (int i = 0; i < dayNum; i++) { 
//保存距今dayNum天数的key的集合 
sb.append(ACTIVE_KEY).append(sdf.format(DateUtils.addDays(new Date(), -i))); 
pastDaysKey.add(sb.toString()); 
sb.delete(0, sb.length()); 
} 
if (pastDaysKey.isEmpty()) { 
return (long) 0; 
} 
String lastDaysKey = "last" + dayNum + "DaysActive"; 
Pipeline pipeline = jedis.pipelined(); 
pipeline.bitop(BitOP.AND, lastDaysKey, pastDaysKey.toArray(new String[pastDaysKey.size()])); 
pipeline.bitcount(lastDaysKey); 
//设置过期时间为5分钟 
pipeline.expire(lastDaysKey, 300); 
List activeKeyCountList = pipeline.syncAndReturnAll(); 
return (Long) activeKeyCountList.get(1);
} 
public static void main(String[] args) { 
Counter c = new Counter(); 
for (int i = 0; i < 15; i++) { 
c.updateUser(i, "20190531"); 
} 
for (int i = 6; i < 15; i++) { 
c.updateUser(i, "20190530"); 
} 
System.out.println("累计用户数：" + c.getTotalUserCount());
System.out.println("两天内的活跃人数：" + c.getActiveUserCount(2));
} 
} 

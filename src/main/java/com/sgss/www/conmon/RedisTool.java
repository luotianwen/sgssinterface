package com.sgss.www.conmon;

import com.alibaba.druid.support.logging.Log;
import com.alibaba.druid.support.logging.LogFactory;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.redis.serializer.FstSerializer;
import redis.clients.jedis.*;
import redis.clients.jedis.params.geo.GeoRadiusParam;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author 罗天文
 * @date 2018-04-17
 * redis配置
 */
public class RedisTool {


  private static Log logger = LogFactory.getLog(RedisTool.class);
  private static JedisPool jedisPool = null;


  /**
   * 构建redis连接池
   *

   * @return JedisPool
   */
  public static JedisPool getPool() {
    if (jedisPool == null) {

      JedisPoolConfig config = new JedisPoolConfig();

      config.setMaxIdle(200);

      config.setTestOnBorrow(true);
      config.setTestOnReturn(true);
      //pool = new JedisPool( );
      jedisPool = new JedisPool(config, PropKit.get("redis.host"), PropKit.getInt("redis.port"), PropKit.getInt("redis.timeout"), PropKit.get("redis.password"));
    }
    return jedisPool;
  }

  /**
   * 检查key是否存在
   */
  public static boolean exists(String key) {


    try (Jedis jedis = getPool().getResource()) {
      return jedis.exists(key);

    } catch (Exception ex) {
      ex.printStackTrace();
      throw ex;
    }

  }
  protected static  byte[] keyToBytes(Object key) {
    return FstSerializer.me.keyToBytes(key.toString());
  }
  protected static  byte[] valueToBytes(Object value) {
    return FstSerializer.me.valueToBytes(value);
  }
  public static Object valueFromBytes(byte[] bytes) {
    return FstSerializer.me.valueFromBytes(bytes);
  }
  /**
   * 插入key-val键值数据
   */
  public static String setObject(Object key, Object value) {


    String var4 = null;
    try (Jedis jedis = getPool().getResource()) {
      var4 = jedis.set(keyToBytes(key), valueToBytes(value));

    } catch (Exception ex) {
      ex.printStackTrace();
      throw ex;
    }

    return var4;
  }
/*
  *//**
   * 插入key-val键值数据
   *//*
  public static void set(String key, String val) {

    long start = System.currentTimeMillis();
    try (Jedis jedis = getPool().getResource()) {
      jedis.set(key, val);

    } catch (Exception ex) {
      ex.printStackTrace();
      throw ex;
    }
    logger.info("set method time:" + (System.currentTimeMillis() - start) + "ms");
  }*/
  /**
   * 查看过期时间
   */
  public static Long ttl(String key) {

    try (Jedis jedis = getPool().getResource()) {
      return jedis.ttl(keyToBytes(key));
    } catch (Exception ex) {
      ex.printStackTrace();
      throw ex;
    }

  }

  /**
   * 向名称为key的hash中添加元素field
   */
  public static void hset(String key,String field,String value) {
    try (Jedis jedis = getPool().getResource()) {
      jedis.hset(keyToBytes(key), keyToBytes(field), valueToBytes(value));
    } catch (Exception ex) {
      ex.printStackTrace();
      throw ex;
    }
  }

  /**
   * 返回名称为key的hash中field对应的value
   * @return
   */
  public static String hgetDict(String key) {
    try (Jedis jedis = getPool().getResource()) {
      return  hget(key, "label");
    } catch (Exception ex) {
      ex.printStackTrace();
      throw ex;
    }
  }
  /**
   * 返回名称为key的hash中field对应的value
   * @return
   */
  public static String hgetRecruitCategory(String id) {
    try (Jedis jedis = getPool().getResource()) {
      return  hget("recruitCategory:"+id, "name");
    } catch (Exception ex) {
      ex.printStackTrace();
      throw ex;
    }
  }
  /**
   * 返回名称为key的hash中field对应的value
   * @return
   */
  public static String hgetRecruitIndustry(String id) {
    try (Jedis jedis = getPool().getResource()) {
      return  hget("recruitIndustry:"+id, "name");
    } catch (Exception ex) {
      ex.printStackTrace();
      throw ex;
    }
  }
  /**
   * 返回名称为key的hash中field对应的value
   * @return
   */
  public static String hgetArea(String id) {
    try (Jedis jedis = getPool().getResource()) {
      return  hget("area:"+id, "name");
    } catch (Exception ex) {
      ex.printStackTrace();
      throw ex;
    }
  }

  /**
   * 返回名称为key的hash中field对应的value
   * @return
   */
  public static String hget(String key,String field) {
    try (Jedis jedis = getPool().getResource()) {
      return (String) valueFromBytes(jedis.hget(keyToBytes(key), keyToBytes(field)));
    } catch (Exception ex) {
      ex.printStackTrace();
      throw ex;
    }
  }
  public static Object getObject(String key) {

    if(!RedisTool.exists(key)){
      return null;
    }
    Object var3 = null;
    try {
      try (Jedis jedis = getPool().getResource()) {
      var3 = valueFromBytes(jedis.get(keyToBytes(key)));

      } catch (Exception ex) {
        ex.printStackTrace();
      }
    } finally {

    }

    return var3;
  }
  /**
   * 获取key-val数据

  public static String get(String key) {
    long start = System.currentTimeMillis();
    try (Jedis jedis = getPool().getResource()) {
      String val = jedis.get(key);
      logger.info("get method time:" + (System.currentTimeMillis() - start) + "ms");
      return val;
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    return "";
  } */

  /**
   * integer类型自增
   *
   * @param key
   * @return
   */
  public static Long incr(String key) {
    long start = System.currentTimeMillis();
    try (Jedis jedis = getPool().getResource()) {

      logger.info("incrKey method time:" + (System.currentTimeMillis() - start) + "ms");
      return jedis.incr(keyToBytes(key));
    } catch (Exception ex) {
      ex.printStackTrace();
      return null;
    }
  }

  /**
   * integer类型按照给定数字增加
   *
   * @param key
   * @param incrValue
   * @return
   */
  public static Long incrBy(String key, long incrValue) {
    long start = System.currentTimeMillis();
    try (Jedis jedis = getPool().getResource()) {
      String val = jedis.get(key);
      logger.info("incrBy method time:" + (System.currentTimeMillis() - start) + "ms");
      return jedis.incrBy(key, incrValue);
    } catch (Exception ex) {
      ex.printStackTrace();
      return null;
    }
  }

  /**
   * 对象空闲时间
   *
   * @param key
   * @return
   */
  public static Long objectIdletime(String key) {
    long start = System.currentTimeMillis();
    try (Jedis jedis = getPool().getResource()) {
      System.out.println("key:" + jedis.get(key));
      logger.info("objectIdletime method time:" + (System.currentTimeMillis() - start) + "ms");
      return 0L;
    } catch (Exception ex) {
      ex.printStackTrace();
      return null;
    }
  }

  /**
   * 往list中插入数据，对值不排重
   */
  public static long lpush(String key, String jsonObject) {
    long start = System.currentTimeMillis();
    try (Jedis jedis = getPool().getResource()) {
      long rv = jedis.lpush(key, jsonObject);
      logger.info("lpush string method time:" + (System.currentTimeMillis() - start) + "ms");
      return rv;
    } catch (Exception ex) {
      ex.printStackTrace();
      return 0;
    }
  }

  /**
   * 删除count个key的list中值为value的元素
   * @param key
   * @param count
   * @param value
   * @return
   */
  public static long lrem(String key, long count, String value) {
    try (Jedis jedis = getPool().getResource()) {
      long l = jedis.lrem(key, count, value);
      return l;
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    return 0;
  }

  /**
   * 往集合中插入数据，对值有排重

  public static long sadd(String key, String val) {
    try (Jedis jedis = getPool().getResource()) {
      return jedis.sadd(key, val);
    } catch (Exception ex) {
      ex.printStackTrace();
//			throw ex;
      return 0;
    }

  } */
  /**
   * 插入key-val键值数据
   */
  public static void sadd(Object key, Object value) {



    try (Jedis jedis = getPool().getResource()) {
      jedis.sadd(keyToBytes(key), valueToBytes(value));
    } catch (Exception ex) {
      ex.printStackTrace();
      throw ex;
    }


  }

  /**
   * 获取某集合中的成员
   */
  public static Set<byte[]> getSmembers(String key) {
    try (Jedis jedis = getPool().getResource()) {
      return jedis.smembers(keyToBytes(key));
    } catch (Exception ex) {
      ex.printStackTrace();
//			throw ex;
      return new HashSet<byte[]>();
    }
  }
  /**
   * 获取某集合中的成员

  public static Set<String> smembers(String key) {
    try (Jedis jedis = getPool().getResource() ) {
      return jedis.smembers(key);
    } catch (Exception ex) {
      ex.printStackTrace();
//			throw ex;
      return new HashSet<String>();
    }
  }*/

  /**
   * 往有序集合中插入数据，对值有排重-对分值进行了排序
   */
  public static long zadd(String key, double score, String val) {
    try (Jedis jedis = getPool().getResource()) {
      return jedis.zadd(key, score, val);
    } catch (Exception ex) {
      ex.printStackTrace();
//			throw ex;
      return 0;
    }
  }

  /**
   * 获取根据分值倒序的区间数据
   */
  public static Set<String> zrevrange(String key, long start, long end) {
    try (Jedis jedis = getPool().getResource()) {
      return jedis.zrevrange(key, start, end);
    } catch (Exception ex) {
      ex.printStackTrace();
//			throw ex;
      return new HashSet<String>();
    }
  }

  /**
   * 设置key的过期时间
   */
  public static long expire(String key, int seconds) {
    long start = System.currentTimeMillis();
    try (Jedis jedis = getPool().getResource()) {
      long rv = jedis.expire(key, seconds);
      logger.info("expire method time:" + (System.currentTimeMillis() - start) + "ms");
      return rv;
    } catch (Exception ex) {
      ex.printStackTrace();
      return 0;
    }
  }

  /**
   * 获取指定元素权重
   *
   * @param key
   * @param seconds
   * @return
   */
  public static double zscore(String key, String seconds) {
    try (Jedis jedis = getPool().getResource()) {
      Double rv = jedis.zscore(key, seconds);
      if (rv == null) {
        rv = 0.0;
      }
      return rv;
    } catch (Exception ex) {
      ex.printStackTrace();
      return 0;
    }
  }

  /**
   * 删除key
   * @param key
   * @return
   */
  public static long del(String key) {
    try (Jedis jedis = getPool().getResource()) {
      return jedis.del(key);
    } catch (Exception ex) {
      ex.printStackTrace();
      return 0;
    }
  }

  public static long lpush(String key, List<String> records) {
    long rv = 0;
    if (null == records || records.size() == 0) {
      return rv;
    }
    long start = System.currentTimeMillis();
    try (Jedis jedis = getPool().getResource()) {
      rv = jedis.lpush(key, records.toArray(new String[0]));
    } catch (Exception ex) {
      ex.printStackTrace();
      return 0;
    }
    logger.info("lpush string[] method time:" + (System.currentTimeMillis() - start) + "ms");
    return rv;
  }

  /**
   * 给名称为key的list中index位置的元素赋值
   * @param key
   * @param index
   * @param value
   */
  public static void lset(String key,long index,String value) {
    try (Jedis jedis = getPool().getResource()) {
      jedis.lset(key, index, value);
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }
  private static final String LOCK_SUCCESS = "OK";
  private static final String SET_IF_NOT_EXIST = "NX";
  private static final String SET_WITH_EXPIRE_TIME = "PX";

  /**
   * 尝试获取分布式锁
   * @param key 锁
   * @param requestId 请求标识
   * @param expireTime 超期时间
   * @return 是否获取成功
   */
  public static boolean tryGetDistributedLock( String key, String requestId, int expireTime) {
    String result =null;

    try (Jedis jedis = getPool().getResource()) {
      result = jedis.set(key, requestId, SET_IF_NOT_EXIST, SET_WITH_EXPIRE_TIME, expireTime);
    } catch (Exception ex) {
      ex.printStackTrace();
      throw ex;
    }
    if (LOCK_SUCCESS.equals(result)) {
      return true;
    }
    return false;

  }
  private static final Long RELEASE_SUCCESS = 1L;

  /**
   * 释放分布式锁

   * @param lockKey 锁
   * @param requestId 请求标识
   * @return 是否释放成功
   */
  public static boolean releaseDistributedLock( String lockKey, String requestId) {
    Object result=null;
    try (Jedis jedis = getPool().getResource()) {
      String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
        result = jedis.eval(script, Collections.singletonList(lockKey), Collections.singletonList(requestId));
    } catch (Exception ex) {
      ex.printStackTrace();
      throw ex;
    }
    if (RELEASE_SUCCESS.equals(result)) {
      return true;
    }
    return false;

  }
  /**
     * 插入key-val键值数据
     */
    public static String setexObject(Object key, int seconds, Object value) {


        String var4 = null;
        try (Jedis jedis = getPool().getResource()) {
            var4 = jedis.setex(keyToBytes(key),seconds, valueToBytes(value));

        } catch (Exception ex) {
            ex.printStackTrace();
            throw ex;
        }

        return var4;
    }
  /**
   * 设置过期时间
   * @param key
   * @param seconds
   * @param value
   */
  public static void setex(String key, int seconds, String value) {
    try (Jedis jedis = getPool().getResource()) {
      jedis.setex(key,seconds,value);
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }
  /**
   * 返回名称为key的list的长度
   * @param key
   * @return
   */
  public static long llen(String key) {
    try (Jedis jedis = getPool().getResource()) {
      return jedis.llen(key);
    } catch (Exception ex) {
      ex.printStackTrace();
      return 0;
    }
  }

  /**
   * 返回名称为key的list中start至end之间的元素
   * @param key
   * @param start
   * @param end
   * @return
   */
  public static List<String> lrange(String key,long start,long end) {
    try (Jedis jedis = getPool().getResource()) {
      return jedis.lrange(key, start, end);
    } catch (Exception ex) {
      ex.printStackTrace();
      return null;
    }
  }
  /**
   * Redis Hmget 命令用于返回哈希表中，一个或多个给定字段的值。
   如果指定的字段不存在于哈希表，那么返回一个 nil 值。
   * @param key
   * @param item
   * @return 一个包含多个给定字段关联值的表，表值的排列顺序和指定字段的请求顺序一样。
   */
  public static  List<String> hmget(String key, String... item) {
    Jedis jedis = jedisPool.getResource();
    List<String> result = jedis.hmget(key, item);
    jedis.close();
    return result;
  }
  /**
   * Redis Zrevrangebyscore 返回有序集中指定分数区间内的所有的成员。有序集成员按分数值递减(从大到小)的次序排列。
   具有相同分数值的成员按字典序的逆序(reverse lexicographical order )排列。
   除了成员按分数值递减的次序排列这一点外， ZREVRANGEBYSCORE 命令的其他方面和 ZRANGEBYSCORE 命令一样。
   * @param key
   * @param max
   * @param min
   * @param offset
   * @param count
   * @return 指定区间内，带有分数值(可选)的有序集成员的列表。
   */
  public static  Set<String> zrevrangebyscore(String key, String max, String min, int offset, int count){
    Jedis jedis = jedisPool.getResource();
    Set<String> result = (Set<String>) jedis.zrevrangeByScore(key, max, min, offset, count);
    jedis.close();
    return result;
  }
  public static void main(String[] args) {
    PropKit.use("a_little_config.txt");
    for  ( int  i =  1 ; i <=  100 ; i+=10) {
      // 初始化CommentId索引 SortSet
      RedisTool.zadd("topicId", 1, "commentId"+i);
      // 初始化Comment数据 Hash
      RedisTool.hset("Comment_Key","commentId"+i, "comment content"+i);
    }
    // 倒序取 从0条开始取 5条 Id 数据
    Set<String> sets = RedisTool.zrevrangebyscore("topicId", "80", "1", 4, 5);
    String[] items = new String[]{};
    System.out.println(sets.toString());
    // 根据id取comment数据
    List<String> list = RedisTool.hmget("Comment_Key", sets.toArray(items));
    for(String str : list){
      System.out.println(str);
    }
  /*  RedisTool.set("test1", "123");

    String r = RedisTool.get("test155656");
    System.out.println("=="+r+"=="+(r == null));*/
//		RedisTool.set("test1", "123");
//
//		String r = RedisTool.get("test1");
//
//		System.out.println(r);
//		RedisTool.set("int", "nihao");
//		System.out.println(RedisTool.incr("int"));
//		System.out.println(RedisTool.incr("int"));
//		System.out.println(RedisTool.incr("int"));
//		System.out.println("===========================");
//		try {
//			Thread.sleep(5);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//			System.out.println("exception===");
//		}
//		System.out.println("objectIdletime:" + RedisTool.objectIdletime("int"));
//		System.out.println("===========================");
//		System.out.println("incrBy 3:" + RedisTool.incrBy("int", 3));
//		System.out.println("incrBy 5:" + RedisTool.incrBy("int", 5));
  }

  public static String lindex(String key) {
    long start = System.currentTimeMillis();
    String value = null;
    try (Jedis jedis = getPool().getResource()) {
      value = jedis.lindex(key, 0);
    } catch (Exception ex) {
      ex.printStackTrace();
      throw ex;
    }
    return value;
  }


  public static String rpop(String key) {
    try (Jedis jedis = getPool().getResource()) {
      return jedis.rpop(key);
    } catch (Exception ex) {
      ex.printStackTrace();
      return null;
    }
  }


  public static long zcard(String key) {
    try (Jedis jedis = getPool().getResource()) {
      return jedis.zcard(key);
    } catch (Exception ex) {
      ex.printStackTrace();
      return 0;
    }
  }




  public static Set<Tuple> zrangeWithScores(String key, long start, long end) {
    try (Jedis jedis = getPool().getResource()) {
      return jedis.zrangeWithScores(key,start,end);
    } catch (Exception ex) {
      ex.printStackTrace();
      return null;
    }
  }

  /**
   * geo 增加数据
   * @param key
   * @param longitude
   * @param latitude
   * @param member
   */
  public static void geoadd(String key, Double longitude, Double latitude, String member) {
    try (Jedis jedis = getPool().getResource()) {
        jedis.geoadd(key,longitude,latitude,member);
    } catch (Exception ex) {
      ex.printStackTrace();

    }
  }

  public static List<GeoRadiusResponse> georadius(String key, double longitude, double latitude, int dis, GeoUnit km, GeoRadiusParam param) {
    try (Jedis jedis = getPool().getResource()) {
      return jedis.georadius(key,longitude,latitude,dis,km,param);
    } catch (Exception ex) {
      ex.printStackTrace();
     return null;
    }
  }


}

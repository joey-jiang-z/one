/**
 * <p>Title: RedisLimitRateWithLUA.java</p>
 * <p>Description: </p>
 * @author jiangzheng
 * @date 2019年7月9日 下午4:19:07
 * @version 1.0
 */
package com.one.limit;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.apache.commons.io.FileUtils;

import redis.clients.jedis.Jedis;

/**
 * @Description: TODO
 * @author jiangzheng
 * @date 2019年7月9日 下午4:19:07
 */
public class RedisLimitRateWithLUA {
	static String luaScript = null; 
	static{
		File luaFile = null;
		try {
			luaFile = new File(RedisLimitRateWithLUA.class.getResource("/").toURI().getPath() + "limit.lua");
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		try {
			luaScript = FileUtils.readFileToString(luaFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public static void main(String[] args) {
		final CountDownLatch latch = new CountDownLatch(1);

		for (int i = 0; i < 7; i++) {
			new Thread(new Runnable() {
				public void run() {
					try {
						latch.await();
						System.out.println("请求是否被执行：" + accquire());
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}).start();

		}

		latch.countDown();
	}

	public static boolean accquire() throws IOException, URISyntaxException {
		Jedis jedis = new Jedis("127.0.0.1");
//		File luaFile = new File(RedisLimitRateWithLUA.class.getResource("/").toURI().getPath() + "limit.lua");
//		String luaScript = FileUtils.readFileToString(luaFile);

		String key = "ip:" + System.currentTimeMillis() / 1000; // 当前秒
		String limit = "5"; // 最大限制
		List<String> keys = new ArrayList<String>();
		keys.add(key);
		List<String> args = new ArrayList<String>();
		args.add(limit);
		Long result = (Long) (jedis.eval(luaScript, keys, args)); // 执行lua脚本，传入参数
		return result == 1;
	}
}

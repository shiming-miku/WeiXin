package com.shiming.weixin;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@RunWith(SpringRunner.class)
@SpringBootTest
public class WeixinApplicationTests {

	@Test
	public void contextLoads() {
		System.out.println('s');
		Map<String, AtomicInteger> map = new HashMap<>();
		AtomicInteger a = new AtomicInteger();
		System.out.println("a地址:" + System.identityHashCode(a));
		map.put("a", a);
		for (int i = 0; i < 50; i++) {
			AtomicInteger d = map.get("a");
			AtomicInteger e = d;
			d.incrementAndGet();
			System.out.println("for循环:d" + System.identityHashCode(d));
			System.out.println("for循环:e" + System.identityHashCode(e));
		}
		System.out.println(System.identityHashCode(a));
	}

}

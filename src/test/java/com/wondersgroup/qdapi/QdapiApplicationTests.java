package com.wondersgroup.qdapi;

import com.alibaba.fastjson.JSON;
import com.wondersgroup.framwork.dao.CommonJdbcUtils;
import com.wondersgroup.framwork.dao.bo.Page;
import com.wondersgroup.qdapi.dto.Ac01VO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
public class QdapiApplicationTests {

	@Test
	public void contextLoads() {
		Page page=new Page<Ac01VO>(1,10);
		CommonJdbcUtils.queryPageList(page,"select * from ac01",Ac01VO.class);
		System.out.printf(JSON.toJSONString(page.getResultList()));
	}
	@Test
	public void contextLoads_object() {

		String str=CommonJdbcUtils.queryObject("select max(aac003) from ac01 where aac147=?",String.class,"372922198607031436");
		System.out.printf("str:"+str);
	}
	@Test
	public void contextLoads_count() {

		Long lo=CommonJdbcUtils.queryCount("select * from aa08 ");
		System.out.printf("lo:"+lo);
	}
	@Test
	public void contextLoads_map() {
		Map map=CommonJdbcUtils.queryMap("select * from aa08");
		System.out.println("map:"+ JSON.toJSONString(map));
		System.out.println("map:"+ map.get("CAE458"));
	}
}

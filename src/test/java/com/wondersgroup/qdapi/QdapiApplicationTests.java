package com.wondersgroup.qdapi;

import com.alibaba.fastjson.JSON;
import com.wondersgroup.framwork.dao.CommonJdbcUtils;
import com.wondersgroup.framwork.dao.bo.Page;
import com.wondersgroup.qdapi.bo.User;
import com.wondersgroup.qdapi.dto.Ac01VO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
		Map map=CommonJdbcUtils.queryMap("select * from app_user");
		System.out.println("map:"+ JSON.toJSONString(map));
		System.out.println("map:"+ map.get("CAE458"));
	}
	@Test
	public void contextLoads_insert() {
		User user=new User();
		user.setName("薛凤彪");
		user.setAge(30);
		user.setTime(new Date());
		CommonJdbcUtils.insert(user);
		System.out.println(JSON.toJSON(user));
	}
	@Test
	public void contextLoads_update() {
		User user=new User();
		user.setName("xxxxx");
		user.setId(4);
		user.setAge(30);
		user.setTime(new Date());
		CommonJdbcUtils.updateSelect(user);
	}
	@Test
	public void contextLoads_batch() {
		List<User> users=new ArrayList<User>();
		User user=null;
		for (int i=0;i<10;i++) {
			user=new User();
			user.setName("薛凤彪xue"+(i+6));
			users.add(user);
		}
		CommonJdbcUtils.insertBatch(users);
		System.out.println("-----"+JSON.toJSONString(users));
	}
}

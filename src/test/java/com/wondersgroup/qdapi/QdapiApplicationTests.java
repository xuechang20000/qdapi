package com.wondersgroup.qdapi;

import com.alibaba.fastjson.JSON;
import com.wondersgroup.framwork.dao.CommonJdbcUtils;
import com.wondersgroup.framwork.dao.bo.Page;
import com.wondersgroup.framwork.dao.utils.ClassUtils;
import com.wondersgroup.qdapi.bo.User;
import com.wondersgroup.qdapi.dto.Ac01VO;
import com.wondersgroup.qdapi.dto.TestSpDto;
import com.wondersgroup.qdapi.dto.TestSpList;
import com.wondersgroup.qdapi.service.CommonService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.ApplicationContextTestUtils;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
public class QdapiApplicationTests {
	@Autowired
	CommonService service;
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
		//user.setTime(new Date());
		CommonJdbcUtils.insert(user);
		System.out.println(JSON.toJSON(user));
	}
	@Test
	public void contextLoads_update() {
		User user=new User();
		user.setId(20390705);
		user.setAge(16);
		CommonJdbcUtils.updateSelect(user);
	}
	@Test
	public void contextLoads_batch() {
		List<User> users=new ArrayList<User>();
		User user=null;
		for (int i=0;i<10;i++) {
			user=new User();
			//user.setName("薛凤彪xue"+(i+20390705));
			//user.setAge(i+10);
			user.setId(20390705+i);
			user.setTime(new Date());
			users.add(user);
		}
		CommonJdbcUtils.updateBatchBySelect(users);
		System.out.println("-----"+JSON.toJSONString(users));
	}
	@Test
	public  void contextLoads_spDto(){
		TestSpDto testSpDto=new TestSpDto();
		testSpDto.setId(3);
		testSpDto.setName("薛");
		Map map=ClassUtils.generateSpDto(testSpDto);
		System.out.println(JSON.toJSONString(map));
		//CommonJdbcUtils.callProcedure(testSpDto,"sp_xxx");
		CommonJdbcUtils.callProcedure(testSpDto);
		System.out.println(JSON.toJSONString(testSpDto));
	}
	@Test
	public  void contextLoads_spList(){
		TestSpList testSpList=new TestSpList();
		testSpList.setId(4);
		testSpList.setName("薛mcfeng");
		CommonJdbcUtils.callProcedure(testSpList,"spk_test.sp_xxx_list");
		System.out.println(JSON.toJSONString(testSpList));
	}
	@Test
	public  void queryPsnInfo(){
		List<Ac01VO> list=service.queryPsnInfo("372922198607031436");
		System.out.println(JSON.toJSONString(list));
	}
}

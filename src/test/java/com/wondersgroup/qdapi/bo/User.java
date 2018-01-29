package com.wondersgroup.qdapi.bo;

import com.wondersgroup.framwork.dao.annotation.Id;
import com.wondersgroup.framwork.dao.annotation.Sequence;
import com.wondersgroup.framwork.dao.annotation.Table;

import java.util.Date;

@Table(name="test_xue")
public class User {
    private Integer id;
    private String name;
    private Integer age;
    private Date time;

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    @Id
    @Sequence(sequencename = "seq_id")
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

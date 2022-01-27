package com.tlsoft.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tlsoft.entity.User;

import java.util.List;

/**
 * @Classname UserService
 * @Description 用户服务类
 * @Author chb
 * @Date 20220127
 * @Version 1.0
 */
public interface UserService extends IService<User> {

    /**
     * 保存用户信息
     * @param entity
     * @return
     */
    @Override
    boolean save(User entity);

    /**
     * 查询全部用户信息
     * @return
     */
    List<User> getUserList();
}
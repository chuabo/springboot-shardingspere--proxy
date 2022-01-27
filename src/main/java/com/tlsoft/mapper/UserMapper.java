package com.tlsoft.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tlsoft.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * user dao层
 * @author chb
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

}
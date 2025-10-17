package io.github.aicyi.example.service.impl;

import io.github.aicyi.commons.lang.type.BooleanType;
import io.github.aicyi.commons.util.id.IdGenerator;
import io.github.aicyi.example.dao.mapper.base.UserMapper;
import io.github.aicyi.example.domain.UserQuery;
import io.github.aicyi.example.domain.entity.base.User;
import io.github.aicyi.example.domain.entity.base.UserExample;
import io.github.aicyi.example.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * @author Mr.Min
 * @description 用户服务实现
 * @date 15:06
 **/
@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;

    @Override
    public void save(User user) {
        user.setId(IdGenerator.generateId());
        user.setDeleted(BooleanType.FALSE);
        user.setVersion(0);
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        userMapper.insertSelective(user);
    }

    @Override
    public User getById(Long id) {
        return userMapper.selectByPrimaryKey(id);
    }

    @Override
    public List<User> list(UserQuery query) {
        UserExample userExample = new UserExample();
        UserExample.Criteria criteria = userExample.createCriteria();
        if (query == null) {
            return Collections.emptyList();
        }
        if (StringUtils.isNotBlank(query.getMobileEq())) {
            criteria.andMobileEqualTo(query.getMobileEq());
        }
        if (StringUtils.isNotBlank(query.getIdCardEq())) {
            criteria.andIdCardEqualTo(query.getIdCardEq());
        }
        if (Objects.nonNull(query.getBirthdayStart())) {
            criteria.andBirthdayGreaterThanOrEqualTo(query.getBirthdayStart());
        }
        if (Objects.nonNull(query.getBirthdayEnd())) {
            criteria.andBirthdayLessThanOrEqualTo(query.getBirthdayEnd());
        }
        return userMapper.selectByExample(userExample);
    }

    @Override
    public Page<User> pagingList(Pageable pageable, UserQuery query) {
        return null;
    }
}

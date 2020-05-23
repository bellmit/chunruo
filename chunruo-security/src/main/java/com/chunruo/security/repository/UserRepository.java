package com.chunruo.security.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.chunruo.core.base.GenericRepository;
import com.chunruo.security.model.User;

/**
 * 用户
 * @author chunruo
 */
@Repository("userRepository")
public interface UserRepository extends GenericRepository<User, Long> {

    @Query("from User u where u.username=:username")
    public User getUserByName(@Param("username") String username);
    
    @Query("from User u where u.groupId in(:groupIdList)")
    public List<User> getUserListByGroupIdList(@Param("groupIdList")List<Long> groupIdList);
}

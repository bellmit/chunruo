package com.chunruo.security.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.chunruo.core.base.GenericRepository;
import com.chunruo.security.model.Role;

/**
 * 用户角色
 * @author chunruo
 */
@Repository("roleRepository")
public interface RoleRepository extends GenericRepository<Role, Long> {
	
	@Query("from Role where upper(name)=:name")
	List<Role> getRoleByName(@Param("name")String name);
}

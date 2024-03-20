package com.module.project.repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import com.module.project.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    @Query(value = "select to_char(DATE_TRUNC('month', create_date),'MM/YYYY') as month, count(*) as times\n" +
            "from tb_user\n" +
            "where create_date >= :from and create_date <= :to\n" +
            "group by DATE_TRUNC('month', create_date)", nativeQuery = true)
    List<Object> getNumberUserByMonth(@Param(value = "from") Date from,
                                                 @Param(value = "to") Date to);

    List<User> findAllByIdIn(List<Long> ids);

}

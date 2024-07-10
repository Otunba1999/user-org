package com.otunba.repository;

import com.otunba.models.Organisation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Set;

public interface OrganisationRepository extends JpaRepository<Organisation, String> {

    @Query("SELECT CASE WHEN COUNT(o) > 0 THEN true ELSE false END " +
            "FROM Organisation o " +
            "JOIN o.users u1 " +
            "JOIN o.users u2 " +
            "WHERE u1.userId = :requesterId AND u2.userId = :userId")
    boolean areUserInSameOrganisation(
            @Param("requesterId") String requesterId, @Param("userId") String userId);

    @Query("SELECT o FROM Organisation o JOIN o.users u WHERE u.email = :email")
    Set<Organisation> findByUserEmail(@Param("email") String email);
}

package com.splitbills.splitbills_backend.repository;

import com.splitbills.splitbills_backend.model.Group;
import com.splitbills.splitbills_backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {
    List<Group> findByMembersContaining(User user);
}

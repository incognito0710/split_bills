package com.splitbills.splitbills_backend.controller;

import com.splitbills.splitbills_backend.model.Group;
import com.splitbills.splitbills_backend.model.User;
import com.splitbills.splitbills_backend.repository.GroupRepository;
import com.splitbills.splitbills_backend.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/groups")
public class GroupController {

    private final GroupRepository groupRepository;
    private final UserRepository userRepository;

    public GroupController(GroupRepository groupRepository, UserRepository userRepository) {
        this.groupRepository = groupRepository;
        this.userRepository = userRepository;
    }

    // Create group by logged-in user
    @PostMapping
    public ResponseEntity<?> createGroup(@RequestBody Group group, Authentication authentication) {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email).orElseThrow();
        group.getMembers().add(user); // creator is member
        groupRepository.save(group);
        return ResponseEntity.ok(group);
    }

    // Add member to group (optional: secure to admin only)
    @PostMapping("/{groupId}/addMember/{userId}")
    public ResponseEntity<?> addMember(@PathVariable Long groupId, @PathVariable Long userId) {
        Group group = groupRepository.findById(groupId).orElseThrow();
        User user = userRepository.findById(userId).orElseThrow();
        group.getMembers().add(user);
        groupRepository.save(group);
        return ResponseEntity.ok(group);
    }

    // Get groups of logged-in user
    @GetMapping("/my")
    public ResponseEntity<?> getMyGroups(Authentication authentication) {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email).orElseThrow();
        return ResponseEntity.ok(groupRepository.findByMembersContaining(user));
    }
}

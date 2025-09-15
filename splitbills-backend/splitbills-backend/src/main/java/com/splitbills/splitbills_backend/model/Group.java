package com.splitbills.splitbills_backend.model;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;
import com.splitbills.splitbills_backend.model.User;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "user_groups")   // "groups" reserved word hai, isliye "user_groups"
public class Group {

    @NotBlank(message = "Group name is mandatory")
    @Column(unique = true)
    private String name;  // e.g., "Goa Trip", "Flat Rent" etc.

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Many-to-Many relation with Users
    @ManyToMany
    @JoinTable(
            name = "group_members", // join table banega
            joinColumns = @JoinColumn(name = "group_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> members = new HashSet<>();

    // --- Getters and Setters ---
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public Set<User> getMembers() {
        return members;
    }
    public void setMembers(Set<User> members) {
        this.members = members;
    }
}

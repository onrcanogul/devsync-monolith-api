package com.api.devsync.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Repository {
    @Id
    private Long id;
    private String name;
    private String fullName;
    private String htmlUrl;
    private String visibility;
    private String language;
    private String description;
    private String defaultBranch;
    private String ownerLogin;
    private Long ownerId;
}
package com.nhh203.userservice.model.entity;


import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.annotations.NaturalId;


@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "roles")
@Accessors(fluent = true)
@With

public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false, updatable = false)
    private Long id;


    @Enumerated(EnumType.STRING)
    @NaturalId
    @Column(name = "roleName", length = 60)
    private RoleName name;
}
package com.example.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Token {

    @Id
    @GeneratedValue
    public Long id;
    @Column(unique = true)
    public String token;
    public boolean revoked;

    @ManyToOne
    @JoinColumn(name = "user_id")
    public User user;
}

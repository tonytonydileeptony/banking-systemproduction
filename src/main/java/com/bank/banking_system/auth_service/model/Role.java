package com.bank.banking_system.auth_service.model;

import jakarta.persistence.*;

@Entity
@Table(name = "roles")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    private enum Type{
        ADMIN, USER;
    }  // ROLE_USER, ROLE_ADMIN

}
package com.ratelimit.usage.model;

import jakarta.persistence.*;

@Entity
@Table(name = "usage")
public class Usage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String userId;

    private String requestId;
}

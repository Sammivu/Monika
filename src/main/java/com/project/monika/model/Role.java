//package com.project.monika.model;
//
//import jakarta.persistence.*;
//import lombok.AllArgsConstructor;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//import lombok.Setter;
//
//import java.util.Set;
//
//@Entity
//@Table
//@Getter
//@Setter
//@AllArgsConstructor
//@NoArgsConstructor
//public class Role {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @Column(unique = true, nullable = false)
//    private String name;
//
//    @ManyToMany(mappedBy = "roles")
//    private Set<User> users;
//}

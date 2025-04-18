package com.votacao.desafio.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.br.CPF;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Data
@Entity
@Builder
@Table(name = "associados")
@EntityListeners(AuditingEntityListener.class)
@AllArgsConstructor
@NoArgsConstructor
public class Associate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nome", nullable = false, length = 100)
    private String name;

    @CPF
    @Column(name = "cpf", nullable = false, unique = true, length = 11)
    private String cpf;

    @Email
    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;

    @CreatedDate
    @Column(name = "data_cadastro")
    private LocalDateTime createdAt;
}
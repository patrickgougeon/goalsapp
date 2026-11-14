package br.com.metas.api.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "itens_checklist")
@Getter
@Setter
@NoArgsConstructor
public class ItemChecklist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "meta_id", nullable = false)
    private Meta meta;

    @Column(nullable = false, length = 200)
    private String descricao;

    @Column(nullable = false)
    private boolean concluido;

    @Column(nullable = false)
    private int ordem;

    private LocalDateTime concluidoEm;

    public void marcarConcluido(boolean concluido) {
        this.concluido = concluido;
        this.concluidoEm = concluido ? LocalDateTime.now() : null;
    }
}

package br.com.metas.api.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "metas")
@Getter
@Setter
@NoArgsConstructor
public class Meta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(nullable = false, length = 120)
    private String titulo;

    @Column(length = 500)
    private String descricao;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Categoria categoria;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Prioridade prioridade;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusMeta status = StatusMeta.EM_ANDAMENTO;

    private LocalDate dataLimite;

    @Column(nullable = false, updatable = false)
    private LocalDateTime criadaEm;

    private LocalDateTime concluidaEm;

    @OneToMany(mappedBy = "meta", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("ordem ASC, id ASC")
    private List<ItemChecklist> itens = new ArrayList<>();

    @PrePersist
    void prePersist() {
        criadaEm = LocalDateTime.now();
    }

    public int calcularProgresso() {
        if (itens.isEmpty()) {
            return status == StatusMeta.CONCLUIDA ? 100 : 0;
        }
        long concluidos = itens.stream().filter(ItemChecklist::isConcluido).count();
        return (int) Math.round(concluidos * 100.0 / itens.size());
    }

    public void alterarStatus(StatusMeta novoStatus) {
        this.status = novoStatus;
        this.concluidaEm = novoStatus == StatusMeta.CONCLUIDA ? LocalDateTime.now() : null;
    }

    /**
     * Conclui a meta automaticamente quando todos os itens do checklist forem marcados
     * e a reabre quando algum item for desmarcado.
     */
    public void sincronizarStatusComChecklist() {
        if (status == StatusMeta.CANCELADA || itens.isEmpty()) {
            return;
        }
        boolean todosConcluidos = itens.stream().allMatch(ItemChecklist::isConcluido);
        if (todosConcluidos && status == StatusMeta.EM_ANDAMENTO) {
            alterarStatus(StatusMeta.CONCLUIDA);
        } else if (!todosConcluidos && status == StatusMeta.CONCLUIDA) {
            alterarStatus(StatusMeta.EM_ANDAMENTO);
        }
    }

    public void adicionarItem(ItemChecklist item) {
        item.setMeta(this);
        itens.add(item);
    }

    public boolean isAtrasada() {
        return status == StatusMeta.EM_ANDAMENTO
                && dataLimite != null
                && dataLimite.isBefore(LocalDate.now());
    }
}

package br.com.metas.api.repository;

import br.com.metas.api.entity.Categoria;
import br.com.metas.api.entity.Meta;
import br.com.metas.api.entity.StatusMeta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MetaRepository extends JpaRepository<Meta, Long> {

    @Query("""
            SELECT m FROM Meta m
            WHERE m.usuario.id = :usuarioId
              AND (:status IS NULL OR m.status = :status)
              AND (:categoria IS NULL OR m.categoria = :categoria)
            ORDER BY m.criadaEm DESC
            """)
    List<Meta> buscarPorUsuario(@Param("usuarioId") Long usuarioId,
                                @Param("status") StatusMeta status,
                                @Param("categoria") Categoria categoria);
}

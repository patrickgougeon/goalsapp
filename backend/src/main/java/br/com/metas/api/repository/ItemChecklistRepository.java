package br.com.metas.api.repository;

import br.com.metas.api.entity.ItemChecklist;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemChecklistRepository extends JpaRepository<ItemChecklist, Long> {
}

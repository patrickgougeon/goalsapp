package br.com.metas.api;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class FluxoMetasIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void deveCadastrarMetaConcluirChecklistEGerarDesempenho() throws Exception {
        String usuario = mockMvc.perform(post("/api/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nome": "Ana", "email": "ana@email.com", "senha": "senha123"}
                                """))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        Integer usuarioId = JsonPath.read(usuario, "$.id");

        mockMvc.perform(post("/api/usuarios/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email": "ana@email.com", "senha": "errada"}
                                """))
                .andExpect(status().isUnauthorized());

        String meta = mockMvc.perform(post("/api/usuarios/{id}/metas", usuarioId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "titulo": "Ler 2 livros",
                                  "categoria": "ESTUDOS",
                                  "prioridade": "ALTA",
                                  "itens": [{"descricao": "Livro 1"}, {"descricao": "Livro 2"}]
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.itens", hasSize(2)))
                .andExpect(jsonPath("$.progresso").value(0))
                .andReturn().getResponse().getContentAsString();
        Integer metaId = JsonPath.read(meta, "$.id");
        Integer item1 = JsonPath.read(meta, "$.itens[0].id");
        Integer item2 = JsonPath.read(meta, "$.itens[1].id");

        mockMvc.perform(patch("/api/itens/{id}/alternar", item1)).andExpect(status().isOk());
        mockMvc.perform(get("/api/metas/{id}", metaId))
                .andExpect(jsonPath("$.progresso").value(50))
                .andExpect(jsonPath("$.status").value("EM_ANDAMENTO"));

        mockMvc.perform(patch("/api/itens/{id}/alternar", item2)).andExpect(status().isOk());
        mockMvc.perform(get("/api/metas/{id}", metaId))
                .andExpect(jsonPath("$.progresso").value(100))
                .andExpect(jsonPath("$.status").value("CONCLUIDA"));

        mockMvc.perform(get("/api/usuarios/{id}/desempenho", usuarioId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.metasConcluidas").value(1))
                .andExpect(jsonPath("$.pontuacao").value(100))
                .andExpect(jsonPath("$.perfil").value("EXEMPLAR"));
    }

    @Test
    void deveDisponibilizarDocumentacaoOpenApi() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.info.title").value("Metas API"))
                .andExpect(jsonPath("$.paths['/api/usuarios/{id}/desempenho']").exists());
    }

    @Test
    void deveRetornar404ParaMetaInexistente() throws Exception {
        mockMvc.perform(get("/api/metas/{id}", 9999))
                .andExpect(status().isNotFound());
    }

    @Test
    void deveValidarCamposObrigatorios() throws Exception {
        mockMvc.perform(post("/api/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detalhes", hasSize(3)));
    }
}

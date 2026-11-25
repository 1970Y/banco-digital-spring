package br.com.dio.banco;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(properties = {"demo.venilton-password=teste-venilton", "demo.maria-password=teste-maria"})
@AutoConfigureMockMvc
class BancoApiTest {
    @Autowired MockMvc mvc;

    @Test void acessoSemSenha() throws Exception {
        mvc.perform(get("/contas")).andExpect(status().isUnauthorized());
    }

    @Test void titularidadeETransferencia() throws Exception {
        mvc.perform(get("/contas/3").with(httpBasic("venilton", "teste-venilton")))
            .andExpect(status().isForbidden());
        mvc.perform(post("/contas/3/depositos").with(httpBasic("venilton", "teste-venilton"))
            .contentType(MediaType.APPLICATION_JSON).content("{\"valor\":100}"))
            .andExpect(status().isForbidden());
        mvc.perform(post("/contas/1/depositos").with(httpBasic("venilton", "teste-venilton"))
            .contentType(MediaType.APPLICATION_JSON).content("{\"valor\":100}"))
            .andExpect(status().isOk()).andExpect(jsonPath("$.saldo").value(100));
        mvc.perform(post("/transferencias").with(httpBasic("maria", "teste-maria"))
            .contentType(MediaType.APPLICATION_JSON).content("{\"origem\":1,\"destino\":3,\"valor\":10}"))
            .andExpect(status().isForbidden());
        mvc.perform(post("/transferencias").with(httpBasic("venilton", "teste-venilton"))
            .contentType(MediaType.APPLICATION_JSON).content("{\"origem\":1,\"destino\":3,\"valor\":25}"))
            .andExpect(status().isOk()).andExpect(jsonPath("$.saldo").value(75));
        mvc.perform(get("/contas/3").with(httpBasic("maria", "teste-maria")))
            .andExpect(status().isOk()).andExpect(jsonPath("$.saldo").value(25));
        mvc.perform(post("/contas/1/saques").with(httpBasic("venilton", "teste-venilton"))
            .contentType(MediaType.APPLICATION_JSON).content("{\"valor\":1000}"))
            .andExpect(status().isBadRequest());
    }

    @Test void valoresInvalidos() throws Exception {
        mvc.perform(post("/contas/1/depositos").with(httpBasic("venilton", "teste-venilton"))
            .contentType(MediaType.APPLICATION_JSON).content("{\"valor\":-1}"))
            .andExpect(status().isBadRequest());
        mvc.perform(post("/contas/1/depositos").with(httpBasic("venilton", "teste-venilton"))
            .contentType(MediaType.APPLICATION_JSON).content("{\"valor\":1.234}"))
            .andExpect(status().isBadRequest());
    }
}

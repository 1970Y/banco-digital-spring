package br.com.dio.banco.api;

import br.com.dio.banco.domain.Conta;
import br.com.dio.banco.service.BancoService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
public class BancoController {
    private final BancoService banco;
    public BancoController(BancoService banco) { this.banco = banco; }

    public record ValorRequest(@NotNull @DecimalMin("0.01") @Digits(integer = 12, fraction = 2) BigDecimal valor) {}
    public record TransferenciaRequest(int origem, int destino,
        @NotNull @DecimalMin("0.01") @Digits(integer = 12, fraction = 2) BigDecimal valor) {}
    public record ContaResponse(int agencia, int numero, String tipo, String titular, BigDecimal saldo) {
        static ContaResponse de(Conta conta) {
            return new ContaResponse(conta.getAgencia(), conta.getNumero(), conta.getTipo(), conta.getCliente().nome(), conta.getSaldo());
        }
    }

    @GetMapping("/contas")
    public List<ContaResponse> contas(Principal principal) {
        return banco.minhasContas(principal.getName()).stream().map(ContaResponse::de).toList();
    }
    @GetMapping("/contas/{numero}")
    public ContaResponse conta(@PathVariable int numero, Principal principal) {
        return ContaResponse.de(banco.minhaConta(numero, principal.getName()));
    }
    @PostMapping(path = "/contas/{numero}/depositos", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ContaResponse deposito(@PathVariable int numero, @Valid @RequestBody ValorRequest dados, Principal principal) {
        return ContaResponse.de(banco.depositar(numero, dados.valor(), principal.getName()));
    }
    @PostMapping(path = "/contas/{numero}/saques", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ContaResponse saque(@PathVariable int numero, @Valid @RequestBody ValorRequest dados, Principal principal) {
        return ContaResponse.de(banco.sacar(numero, dados.valor(), principal.getName()));
    }
    @PostMapping(path = "/transferencias", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ContaResponse transferencia(@Valid @RequestBody TransferenciaRequest dados, Principal principal) {
        return ContaResponse.de(banco.transferir(dados.origem(), dados.destino(), dados.valor(), principal.getName()));
    }
}

package br.com.dio.banco.service;

import br.com.dio.banco.domain.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@Service
public class BancoService {
    private final Map<Integer, Conta> contas = new ConcurrentHashMap<>();

    public BancoService() {
        Cliente venilton = new Cliente("venilton", "Venilton");
        Cliente maria = new Cliente("maria", "Maria");
        contas.put(1, new ContaCorrente(1, 1, venilton));
        contas.put(2, new ContaPoupanca(1, 2, venilton));
        contas.put(3, new ContaCorrente(1, 3, maria));
    }

    public List<Conta> minhasContas(String usuario) {
        return contas.values().stream().filter(c -> c.getCliente().usuario().equals(usuario))
            .sorted(java.util.Comparator.comparingInt(Conta::getNumero)).toList();
    }

    public Conta minhaConta(int numero, String usuario) {
        Conta conta = buscar(numero);
        exigirTitular(conta, usuario);
        return conta;
    }

    private Conta buscar(int numero) {
        Conta conta = contas.get(numero);
        if (conta == null) throw new IllegalArgumentException("Conta não encontrada");
        return conta;
    }

    private void exigirTitular(Conta conta, String usuario) {
        if (!conta.getCliente().usuario().equals(usuario)) throw new AccessDeniedException("Conta não pertence ao usuário");
    }

    public synchronized Conta depositar(int numero, BigDecimal valor, String usuario) {
        Conta conta = minhaConta(numero, usuario);
        conta.depositar(valor);
        return conta;
    }

    public synchronized Conta sacar(int numero, BigDecimal valor, String usuario) {
        Conta conta = minhaConta(numero, usuario);
        conta.sacar(valor);
        return conta;
    }

    public synchronized Conta transferir(int origem, int destino, BigDecimal valor, String usuario) {
        Conta remetente = minhaConta(origem, usuario);
        Conta destinatario = buscar(destino);
        if (origem == destino) throw new IllegalArgumentException("Origem e destino devem ser diferentes");
        BigDecimal quantia = Conta.validarValor(valor);
        remetente.transferir(quantia, destinatario);
        return remetente;
    }
}

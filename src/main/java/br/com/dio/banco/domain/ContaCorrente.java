package br.com.dio.banco.domain;

public final class ContaCorrente extends Conta {
    public ContaCorrente(int agencia, int numero, Cliente cliente) { super(agencia, numero, cliente); }
    @Override public String getTipo() { return "CORRENTE"; }
}

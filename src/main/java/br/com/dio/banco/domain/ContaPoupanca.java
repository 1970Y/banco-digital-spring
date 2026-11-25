package br.com.dio.banco.domain;

public final class ContaPoupanca extends Conta {
    public ContaPoupanca(int agencia, int numero, Cliente cliente) { super(agencia, numero, cliente); }
    @Override public String getTipo() { return "POUPANCA"; }
}

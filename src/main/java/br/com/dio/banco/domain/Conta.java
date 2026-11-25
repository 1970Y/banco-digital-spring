package br.com.dio.banco.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;

public abstract class Conta implements IConta {
    private final int agencia;
    private final int numero;
    private final Cliente cliente;
    private BigDecimal saldo = new BigDecimal("0.00");

    protected Conta(int agencia, int numero, Cliente cliente) {
        this.agencia = agencia;
        this.numero = numero;
        this.cliente = cliente;
    }

    public static BigDecimal validarValor(BigDecimal valor) {
        if (valor == null || valor.signum() <= 0 || valor.scale() > 2) {
            throw new IllegalArgumentException("O valor deve ser positivo e ter no máximo duas casas decimais");
        }
        return valor.setScale(2, RoundingMode.UNNECESSARY);
    }

    @Override public synchronized void sacar(BigDecimal valor) {
        valor = validarValor(valor);
        if (saldo.compareTo(valor) < 0) throw new IllegalArgumentException("Saldo insuficiente");
        saldo = saldo.subtract(valor);
    }

    @Override public synchronized void depositar(BigDecimal valor) {
        saldo = saldo.add(validarValor(valor));
    }

    @Override public void transferir(BigDecimal valor, IConta destino) {
        if (destino == null || destino == this) throw new IllegalArgumentException("Destino inválido");
        BigDecimal quantia = validarValor(valor);
        sacar(quantia);
        destino.depositar(quantia);
    }

    @Override public synchronized BigDecimal getSaldo() { return saldo; }
    public int getAgencia() { return agencia; }
    public int getNumero() { return numero; }
    public Cliente getCliente() { return cliente; }
    public abstract String getTipo();
}

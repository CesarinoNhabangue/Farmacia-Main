package farmacia.produto;


import java.io.Serializable;

public class Produto implements Serializable {
	private static final long serialVersionUID = 1L;
	private String nome;
    private double preco;
    private int quantidade;
    private int vendidos;

    public Produto(String nome, double preco, int quantidade) {
        this.nome = (nome != null) ? nome : "";
        this.preco = preco;
        this.quantidade = quantidade;
        this.vendidos = 0;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = (nome != null) ? nome : "";
    }

    public double getPreco() {
        return preco;
    }

    public void setPreco(double preco) {
        this.preco = preco;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }

    public int getVendidos() {
        return vendidos;
    }

    public void incrementarVendidos(int quantidade) {
        this.vendidos += quantidade;
    }

    @Override
    public String toString() {
        return "Produto{" +
                "nome='" + nome + '\'' +
                ", preco=" + preco +
                ", quantidade=" + quantidade +
                ", vendidos=" + vendidos +
                '}';
    }
}

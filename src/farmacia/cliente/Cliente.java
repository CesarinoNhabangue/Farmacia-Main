package farmacia.cliente;


import java.io.Serializable;

public class Cliente implements Serializable {

	private static final long serialVersionUID = 1L;
	private String nome;
    private String id;

    public Cliente(String nome, String id) {
        this.nome = (nome != null) ? nome : "";
        this.id = (id != null) ? id : "";
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = (nome != null) ? nome : "";
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = (id != null) ? id : "";
    }

    @Override
    public String toString() {
        return "Cliente{" +
                "nome='" + nome + '\'' +
                ", id='" + id + '\'' +
                '}';
    }
}

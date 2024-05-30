package farmacia.main;


import farmacia.cliente.Cliente;
import farmacia.produto.Produto;
import farmacia.venda.Venda;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Farmacia {
    private List<Cliente> clientes;
    private List<Produto> produtos;
    private List<Venda> vendas;

    public Farmacia() {
        clientes = new ArrayList<>();
        produtos = new ArrayList<>();
        vendas = new ArrayList<>();
        carregarDados();
        if (produtos.isEmpty()) {
            inicializarProdutos();
        }
    }

    private void inicializarProdutos() {
        produtos.add(new Produto("Paracetamol", 100, 50));
        produtos.add(new Produto("Ibuprofeno", 80, 50));
        produtos.add(new Produto("Creme para as maos", 300, 50));
        produtos.add(new Produto("Preservativos (Prudence)", 20, 50));
        produtos.add(new Produto("Caixa de Mascaras", 300, 50));
        produtos.add(new Produto("Teste de Gravidez", 150, 50));
        produtos.add(new Produto("Repelente", 100, 50));
        produtos.add(new Produto("Antibiotico", 200, 50));
        produtos.add(new Produto("Termometro", 700, 50));
        produtos.add(new Produto("Liquido Antisseptico (Dettol)", 40, 50));
        salvarDados();
    }

    public void adicionarCliente(Cliente cliente) {
        clientes.add(cliente);
        System.out.println("Cliente adicionado: " + cliente);
        logOperacao("Adicionar Cliente: " + cliente);
        salvarDados();
        mostrarClientes();
    }

    public void atualizarCliente(String id, Cliente novoCliente) {
        for (Cliente cliente : clientes) {
            if (cliente.getId().equals(id)) {
                cliente.setNome(novoCliente.getNome());
                cliente.setId(novoCliente.getId());
                logOperacao("Atualizar Cliente: " + id + " para " + novoCliente);
                salvarDados();
                System.out.println("Cliente atualizado: " + novoCliente);
                mostrarClientes();
                return;
            }
        }
        System.out.println("Cliente nao encontrado.");
    }

    public void removerCliente(String id) {
        boolean removido = clientes.removeIf(cliente -> cliente.getId().equals(id));
        if (removido) {
            System.out.println("Cliente removido: " + id);
            logOperacao("Remover Cliente: " + id);
            salvarDados();
            mostrarClientes();
        } else {
            System.out.println("Cliente nao encontrado.");
        }
    }

    public void adicionarProduto(Produto produto) {
        produtos.add(produto);
        System.out.println("Produto adicionado: " + produto);
        logOperacao("Adicionar Produto: " + produto);
        salvarDados();
        mostrarProdutos();
    }

    public void atualizarProduto(String nome, Produto novoProduto) {
        for (Produto produto : produtos) {
            if (produto.getNome().equals(nome)) {
                produto.setNome(novoProduto.getNome());
                produto.setPreco(novoProduto.getPreco());
                produto.setQuantidade(novoProduto.getQuantidade());
                logOperacao("Atualizar Produto: " + nome + " para " + novoProduto);
                salvarDados();
                System.out.println("Produto atualizado: " + novoProduto);
                mostrarProdutos();
                return;
            }
        }
        System.out.println("Produto nao encontrado.");
    }

    public void removerProduto(String nome) {
        boolean removido = produtos.removeIf(produto -> produto.getNome().equals(nome));
        if (removido) {
            System.out.println("Produto removido: " + nome);
            logOperacao("Remover Produto: " + nome);
            salvarDados();
            mostrarProdutos();
        } else {
            System.out.println("Produto nao encontrado.");
        }
    }

    public void realizarVenda(String id, String nomeProduto, int quantidade) {
        Cliente cliente = clientes.stream()
                .filter(c -> c.getId() != null && c.getId().equals(id))
                .findFirst()
                .orElse(null);

        Produto produto = produtos.stream()
                .filter(p -> p.getNome() != null && p.getNome().equals(nomeProduto))
                .findFirst()
                .orElse(null);

        if (cliente == null) {
            System.out.println("Cliente nao encontrado.");
            return;
        }

        if (produto == null) {
            System.out.println("Produto nao encontrado.");
            return;
        }

        if (produto.getQuantidade() < quantidade) {
            System.out.printf("O produto %s que esta a tentar vender nao tem disponibilidade em stock ou a quantidade pretendida nao esta disponivel, apenas tem %d.\n", nomeProduto, produto.getQuantidade());
            return;
        }

        double valorTotal = produto.getPreco() * quantidade ; 
        Venda venda = new Venda(cliente, produto, quantidade, valorTotal);
        vendas.add(venda);
        produto.setQuantidade(produto.getQuantidade() - quantidade);
        produto.incrementarVendidos(quantidade);
        logOperacao("Realizar Venda: " + id + " comprou " + quantidade + "x " + nomeProduto + " por " + valorTotal);
        System.out.printf("Venda realizada: Cliente %s comprou %d unidades de %s por %.2fMtn (com IVA incluido).\n", cliente.getNome(), quantidade, nomeProduto, valorTotal);
        salvarDados();
    }

    public void exibirContaCorrente(String idCliente) {
        Cliente cliente = clientes.stream()
                .filter(c -> c.getId().equals(idCliente))
                .findFirst()
                .orElse(null);

        if (cliente == null) {
            System.out.println("Cliente nao encontrado.");
            return;
        }

        List<Venda> vendasCliente = vendas.stream()
                .filter(v -> v.getCliente().equals(cliente))
                .collect(Collectors.toList());

        if (vendasCliente.isEmpty()) {
            System.out.println("Nenhuma venda encontrada para este cliente.");
            return;
        }

        System.out.println("Conta corrente do cliente " + cliente.getNome() + ":");
        double total = 0;
        for (Venda venda : vendasCliente) {
            System.out.println(venda);
            total += venda.getValorTotal();
        }
        System.out.printf("Total gasto: %.2fMtn\n", total);
    }

    public void emitirRelatorios() {
        System.out.println("Relatorios:");
        System.out.println("Produtos mais vendidos:");
        produtos.stream()
                .sorted(Comparator.comparingInt(Produto::getVendidos).reversed())
                .forEach(produto -> System.out.printf("%s - Vendidos: %d, Receita: %.2fMtn\n", produto.getNome(), produto.getVendidos(), produto.getVendidos() * produto.getPreco() * 0.17));

        System.out.println("\nReceita total por cliente:");
        clientes.forEach(cliente -> {
            List<Venda> vendasCliente = vendas.stream()
                    .filter(venda -> venda.getCliente().equals(cliente))
                    .collect(Collectors.toList());
            double total = vendasCliente.stream().mapToDouble(Venda::getValorTotal).sum();
            System.out.printf("Cliente: %s - Receita Total: %.2fMtn\n", cliente.getNome(), total);
        });
    }

    private void logOperacao(String operacao) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("operacoes.log", true))) {
            writer.write(new Date() + " - " + operacao + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void salvarDados() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("farmacia.dat"))) {
            oos.writeObject(clientes);
            oos.writeObject(produtos);
            oos.writeObject(vendas);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void carregarDados() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("farmacia.dat"))) {
            clientes = (List<Cliente>) ois.readObject();
            produtos = (List<Produto>) ois.readObject();
            vendas = (List<Venda>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            clientes = new ArrayList<>();
            produtos = new ArrayList<>();
            vendas = new ArrayList<>();
        }
    }

    private void mostrarClientes() {
        System.out.println("Clientes cadastrados:");
        clientes.forEach(System.out::println);
    }

    private void mostrarProdutos() {
        System.out.println("Produtos cadastrados:");
        produtos.forEach(System.out::println);
    }

    public static void main(String[] args) {
        Farmacia farmacia = new Farmacia();
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("===================Menu===================:");
            System.out.println("1. Clientes");
            System.out.println("2. Produtos");
            System.out.println("3. Inventarios");
            System.out.println("4. Vendas");
            System.out.println("5. Relatorios");
            System.out.println("6. Sair");
            System.out.println("=============================================");
            System.out.print("Escolha uma opcao: ");
           
            int opcao = scanner.nextInt();
            scanner.nextLine();

            switch (opcao) {
                case 1:
                    farmacia.mostrarClientes();
                    System.out.println("");
                    System.out.println("1.1 Criar Cliente");
                    System.out.println("1.2 Atualizar Cliente");
                    System.out.println("1.3 Remover Cliente");
                    System.out.println("1.4 Ver Conta Corrente do Cliente");
                    System.out.print("Escolha uma opcao: ");
                    System.out.println("");
                    int opcaoCliente = scanner.nextInt();
                    scanner.nextLine();

                    if (opcaoCliente == 1) {
                        System.out.print("Nome: ");
                        String nome = scanner.nextLine();
                        System.out.print("ID: ");
                        String id = scanner.nextLine();
                        Cliente cliente = new Cliente(nome, id);
                        farmacia.adicionarCliente(cliente);
                    } else if (opcaoCliente == 2) {
                        System.out.print("ID do cliente a ser atualizado: ");
                        String id = scanner.nextLine();
                        System.out.print("Novo nome: ");
                        String nome = scanner.nextLine();
                        System.out.print("Novo ID: ");
                        String novoId = scanner.nextLine();
                        Cliente novoCliente = new Cliente(nome, novoId);
                        farmacia.atualizarCliente(id, novoCliente);
                    } else if (opcaoCliente == 3) {
                        System.out.print("ID do cliente a ser removido: ");
                        String id = scanner.nextLine();
                        farmacia.removerCliente(id);
                    } else if (opcaoCliente == 4) {
                        System.out.print("ID do cliente: ");
                        String id = scanner.nextLine();
                        farmacia.exibirContaCorrente(id);
                    }
                    break;
                case 2:
                    farmacia.mostrarProdutos();
                    System.out.println("");
                    System.out.println("2.1 Criar Produto");
                    System.out.println("2.2 Atualizar Produto");
                    System.out.println("2.3 Remover Produto");
                    System.out.print("Escolha uma opcao: ");
                    System.out.println("");
                    int opcaoProduto = scanner.nextInt();
                    scanner.nextLine();

                    if (opcaoProduto == 1) {
                        System.out.print("Nome: ");
                        String nome = scanner.nextLine();
                        System.out.print("Preco: ");
                        double preco = scanner.nextDouble();
                        System.out.print("Quantidade: ");
                        int quantidade = scanner.nextInt();
                        Produto produto = new Produto(nome, preco, quantidade);
                        farmacia.adicionarProduto(produto);
                    } else if (opcaoProduto == 2) {
                        System.out.print("Nome do produto a ser atualizado: ");
                        String nome = scanner.nextLine();
                        System.out.print("Novo nome: ");
                        String novoNome = scanner.nextLine();
                        System.out.print("Novo preco: ");
                        double preco = scanner.nextDouble();
                        System.out.print("Nova quantidade: ");
                        int quantidade = scanner.nextInt();
                        Produto novoProduto = new Produto(novoNome, preco, quantidade);
                        farmacia.atualizarProduto(nome, novoProduto);
                    } else if (opcaoProduto == 3) {
                        System.out.print("Nome do produto a ser removido: ");
                        String nome = scanner.nextLine();
                        farmacia.removerProduto(nome);
                    }
                    break;
                case 3:
                    System.out.println("Inventario:");
                    farmacia.produtos.forEach(System.out::println);
                    break;
                case 4:
                    System.out.print("ID do cliente: ");
                    String id = scanner.nextLine();
                    System.out.print("Nome do produto: ");
                    String nomeProduto = scanner.nextLine();
                    System.out.print("Quantidade: ");
                    int quantidade = scanner.nextInt();
                    farmacia.realizarVenda(id, nomeProduto, quantidade);
                    break;
                case 5:
                    farmacia.emitirRelatorios();
                    break;
                case 6:
                    System.exit(0);
                    break;
                default:
                    System.out.println("Opcao invalida.");
            }
        }
    }
}

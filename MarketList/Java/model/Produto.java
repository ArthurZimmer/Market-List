package com.example.marketlist.model;

public class Produto {
    private int id;
    private String descricao;
    private double quantidade;
    private UnidadeMedida unidade;
    private double preco;
    private Categoria categoria;
    private boolean comprado;

    public enum UnidadeMedida {
        UN("Unidade"),
        DZ("Dúzia"),
        ML("Ml"),
        L("L"),
        KG("Kg"),
        G("G"),
        CAIXA("Caixa"),
        EMBALAGEM("Embalagem"),
        GALAO("Galão"),
        GARRAFA("Garrafa"),
        LATA("Lata"),
        PACOTE("Pacote");

        private final String descricao;

        UnidadeMedida(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }
    }

    public enum Categoria {
        BEBIDAS("Bebidas"),
        CARNES("Carnes"),
        COMIDAS_PRONTAS("Comidas Prontas e Congeladas"),
        FARMACIA("Farmácia"),
        FRIOS_LEITES("Frios, Leites e Derivados"),
        FRUTAS_OVOS("Frutas, ovos e verduras"),
        HIGIENE_PESSOAL("Higiene Pessoal"),
        IMPORTADOS("Importados"),
        LIMPEZA("Limpeza"),
        MERCEARIA("Mercearia"),
        PADARIA("Padaria e Sobremesas"),
        SAUDE_BELEZA("Saúde e Beleza"),
        SEM_CATEGORIA("Sem Categoria"),
        TEMPEROS("Temperos");

        private final String descricao;

        Categoria(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }
    }

    // construtores
    public Produto() {}

    public Produto(String descricao, double quantidade, UnidadeMedida unidade,
                   double preco, Categoria categoria) {
        this.descricao = descricao;
        this.quantidade = quantidade;
        this.unidade = unidade;
        this.preco = preco;
        this.categoria = categoria;
        this.comprado = false;
    }

    // getters e setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public void setNome(String nome) {
        this.descricao = nome;
    }

    public double getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(double quantidade) {
        this.quantidade = quantidade;
    }

    public UnidadeMedida getUnidade() {
        return unidade;
    }

    public void setUnidade(UnidadeMedida unidade) {
        this.unidade = unidade;
    }

    public double getPreco() {
        return preco;
    }

    public void setPreco(double preco) {
        this.preco = preco;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public boolean isComprado() {
        return comprado;
    }

    public void setComprado(boolean comprado) {
        this.comprado = comprado;
    }

    // calcular o valor total do produto
    public double getValorTotal() {
        return preco * quantidade;
    }


    // metodos adicionais
    public double getValorUnitario() {
        return preco;
    }

    public void setValorUnitario(double valorUnitario) {
        this.preco = valorUnitario;
    }
}

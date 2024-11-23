package com.example.marketlist;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.marketlist.adapter.ListaComprasAdapter;
import com.example.marketlist.database.MarketListDbHelper;
import com.example.marketlist.model.Produto;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.example.marketlist.adapter.SwipeToDeleteCallback;

public class MainActivity extends AppCompatActivity {
    private MarketListDbHelper dbHelper;
    private List<Produto> produtos = new ArrayList<>();
    private ListaComprasAdapter adapter;
    private RecyclerView recyclerView;
    private TextView txtValorTotal, txtValorSelecionados;
    private ExtendedFloatingActionButton btnAdicionarProduto;
    private ItemTouchHelper itemTouchHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inicializarComponentes();
        configuraMarketListDbHelper();
        configurarRecyclerView();
        atualizarValores();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK) {
            carregarProdutos();
        }
    }

    private void inicializarComponentes() {
        recyclerView = findViewById(R.id.recycler_lista_compras);
        txtValorTotal = findViewById(R.id.txt_valor_total);
        txtValorSelecionados = findViewById(R.id.txt_valor_selecionados);
        btnAdicionarProduto = findViewById(R.id.btn_adicionar_produto);
        btnAdicionarProduto.setOnClickListener(v -> abrirTelaAdicionarProduto());
    }

    private void configuraMarketListDbHelper() {
        dbHelper = new MarketListDbHelper(this);
    }

    private void carregarProdutos() {
        Map<Produto.Categoria, List<Produto>> produtosPorCategoria = agruparProdutosPorCategoria();

        if (adapter != null) {
            adapter.atualizarLista(produtosPorCategoria);
        } else {
            adapter = new ListaComprasAdapter(
                    produtosPorCategoria,
                    this::abrirEdicaoProduto,
                    this::atualizarValoresSelecionados,
                    this::exibirModalDeConfirmacao
            );
            recyclerView.setAdapter(adapter);
            configurarSwipeToDelete();
        }

        atualizarValores();
    }

    private void configurarRecyclerView() {
        Map<Produto.Categoria, List<Produto>> produtosPorCategoria = agruparProdutosPorCategoria();

        adapter = new ListaComprasAdapter(
                produtosPorCategoria,
                this::abrirEdicaoProduto,
                this::atualizarValoresSelecionados,
                this::exibirModalDeConfirmacao
        );

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        configurarSwipeToDelete();
    }

    private void exibirModalDeConfirmacao(Produto produto) {
    }

    private void configurarSwipeToDelete() {
        Drawable deleteIcon = ContextCompat.getDrawable(this, R.drawable.ic_trash);
        SwipeToDeleteCallback swipeToDeleteCallback = new SwipeToDeleteCallback(adapter, deleteIcon) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                Object item = adapter.getItemAtPosition(position);

                if (item instanceof Produto) {
                    Produto produto = (Produto) item;
                    // abre o modal pra deletar
                    exibirModalDeConfirmacao(produto, position);
                }
            }
        };

        itemTouchHelper = new ItemTouchHelper(swipeToDeleteCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    private void deletarProduto(Produto produto) {
        boolean isDeleted = dbHelper.deleteProduto(produto.getId());

        if (isDeleted) {
            // atualiza a lista usando o método existente
            Map<Produto.Categoria, List<Produto>> produtosPorCategoria = agruparProdutosPorCategoria();
            adapter.atualizarLista(produtosPorCategoria);

            // atualiza os valores
            atualizarValores();

            Toast.makeText(MainActivity.this, "Produto excluído!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(MainActivity.this, "Erro ao excluir o produto", Toast.LENGTH_SHORT).show();
        }
    }

    private void deletarProdutoAlternativo(Produto produto) {
        boolean isDeleted = dbHelper.deleteProduto(produto.getId());

        if (isDeleted) {
            // atualiza a lista interna do adapter
            Map<Produto.Categoria, List<Produto>> produtosPorCategoria = agruparProdutosPorCategoria();
            adapter.atualizarLista(produtosPorCategoria);

            // atualiza os valores totais
            atualizarValores();

            Toast.makeText(MainActivity.this, "Produto excluído!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(MainActivity.this, "Erro ao excluir o produto", Toast.LENGTH_SHORT).show();
        }
    }

    private Map<Produto.Categoria, List<Produto>> agruparProdutosPorCategoria() {
        MarketListDbHelper dbHelper = new MarketListDbHelper(this);
        List<Produto> produtos = dbHelper.getAllProdutos();

        if (produtos == null) {
            produtos = new ArrayList<>();
        }

        Map<Produto.Categoria, List<Produto>> produtosPorCategoria = new HashMap<>();
        for (Produto produto : produtos) {
            Produto.Categoria categoria = produto.getCategoria();
            if (!produtosPorCategoria.containsKey(categoria)) {
                produtosPorCategoria.put(categoria, new ArrayList<>());
            }
            produtosPorCategoria.get(categoria).add(produto);
        }

        return produtosPorCategoria;
    }

    private void abrirEdicaoProduto(Produto produto) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(R.layout.bottom_sheet_edit_produto);

        TextInputEditText editNomeProduto = bottomSheetDialog.findViewById(R.id.edit_nome_produto);
        TextInputEditText editQuantidadeProduto = bottomSheetDialog.findViewById(R.id.edit_quantidade_produto);
        TextInputEditText editPrecoProduto = bottomSheetDialog.findViewById(R.id.edit_preco_produto);
        AutoCompleteTextView spinnerUnidade = bottomSheetDialog.findViewById(R.id.spinner_unidade_produto);
        AutoCompleteTextView spinnerCategoria = bottomSheetDialog.findViewById(R.id.spinner_categoria_produto);
        Button btnSalvarEdicao = bottomSheetDialog.findViewById(R.id.btn_salvar_edicao);

        // preenche com os dados q existem
        editNomeProduto.setText(produto.getDescricao());
        editQuantidadeProduto.setText(String.valueOf(produto.getQuantidade()));
        editPrecoProduto.setText(String.valueOf(produto.getPreco()));

        // ocnfigura spinner de unidade
        List<String> unidadeDescricoes = new ArrayList<>();
        for (Produto.UnidadeMedida unidade : Produto.UnidadeMedida.values()) {
            unidadeDescricoes.add(unidade.getDescricao());
        }
        ArrayAdapter<String> unidadeAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                unidadeDescricoes
        );
        spinnerUnidade.setAdapter(unidadeAdapter);
        spinnerUnidade.setText(produto.getUnidade().getDescricao(), false);

        // configura spinner de categoria
        List<String> categoriaDescricoes = new ArrayList<>();
        for (Produto.Categoria categoria : Produto.Categoria.values()) {
            categoriaDescricoes.add(categoria.getDescricao());
        }
        ArrayAdapter<String> categoriaAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                categoriaDescricoes
        );
        spinnerCategoria.setAdapter(categoriaAdapter);
        spinnerCategoria.setText(produto.getCategoria().getDescricao(), false);

        btnSalvarEdicao.setOnClickListener(v -> {
            // valida campos
            if (camposValidos(editNomeProduto, editQuantidadeProduto, editPrecoProduto)) {
                // atualiza produto
                produto.setDescricao(editNomeProduto.getText().toString());
                produto.setQuantidade(Double.parseDouble(editQuantidadeProduto.getText().toString()));
                produto.setPreco(Double.parseDouble(editPrecoProduto.getText().toString()));

                // define unidade
                for (Produto.UnidadeMedida unidade : Produto.UnidadeMedida.values()) {
                    if (unidade.getDescricao().equals(spinnerUnidade.getText().toString())) {
                        produto.setUnidade(unidade);
                        break;
                    }
                }

                // define categoria
                for (Produto.Categoria categoria : Produto.Categoria.values()) {
                    if (categoria.getDescricao().equals(spinnerCategoria.getText().toString())) {
                        produto.setCategoria(categoria);
                        break;
                    }
                }

                // atualiza no banco de dados
                dbHelper.updateProduto(produto);

                // recarrega a lista
                carregarProdutos();
                atualizarValores();

                bottomSheetDialog.dismiss();
                Toast.makeText(this, "Produto atualizado!", Toast.LENGTH_SHORT).show();
            }
        });

        bottomSheetDialog.show();
    }

    private void atualizarValoresSelecionados(Produto produto, boolean isChecked) {
        produto.setComprado(isChecked);
        dbHelper.updateProduto(produto);

        // post pra atualização ser via layout
        recyclerView.post(() -> {
            atualizarValores();
            adapter.notifyDataSetChanged();
        });
    }

    private void atualizarValores() {
        double valorTotal = calcularValorTotal();
        double valorSelecionados = calcularValorSelecionados();

        Log.d("MainActivity", "Valor total: " + valorTotal);
        Log.d("MainActivity", "Valor selecionados: " + valorSelecionados);

        txtValorTotal.setText(getString(R.string.valor_total, valorTotal));
        txtValorSelecionados.setText(getString(R.string.valor_selecionados, valorSelecionados));
    }

    // boolean para os campos
    private boolean camposValidos(@NonNull TextInputEditText... campos) {
        for (TextInputEditText campo : campos) {
            if (campo.getText().toString().trim().isEmpty()) {
                campo.setError("Campo obrigatório");
                return false;
            }
        }
        return true;
    }

    // usa o método direto do banco
    // calcula valor da lista total
    private double calcularValorTotal() {
        return dbHelper.getValorTotal();
    }

    // calcula o valor dos selecionados
    private double calcularValorSelecionados() {
        double total = 0;
        List<Produto> produtos = dbHelper.getAllProdutos();
        for (Produto produto : produtos) {
            if (produto.isComprado()) {
                total += produto.getValorTotal();
            }
        }
        return total;
    }

    // abrir a tela de add prod
    private void abrirTelaAdicionarProduto() {
        Intent intent = new Intent(MainActivity.this, AdicionarProdutoActivity.class);
        startActivityForResult(intent, 1);
    }

    // exibir o modal de confirmação
    private void exibirModalDeConfirmacao(Produto produto, int position) {
        new AlertDialog.Builder(this)
                .setTitle("Confirmar Exclusão")
                .setMessage("Você tem certeza que deseja excluir este item?")
                .setPositiveButton("Sim", (dialog, which) -> {
                    deletarProduto(produto);
                })
                .setNegativeButton("Não", (dialog, which) -> {
                    // se o usuário cancelar, restaura o item na lista
                    adapter.notifyItemChanged(position);
                    dialog.dismiss();
                })
                .setOnCancelListener(dialog -> {
                    // se o diálog for cancelado, tb restaura o item
                    adapter.notifyItemChanged(position);
                })
                .create()
                .show();
    }
}



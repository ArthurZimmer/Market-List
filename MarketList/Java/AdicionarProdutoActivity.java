package com.example.marketlist;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.example.marketlist.database.MarketListDbHelper;
import com.example.marketlist.model.Produto;

public class AdicionarProdutoActivity extends AppCompatActivity {
    private AutoCompleteTextView spinnerCategoriaProduto;
    private AutoCompleteTextView spinnerUnidadeProduto;
    private Button btnSalvarProduto;
    private EditText editNomeProduto, editPrecoProduto, editQuantidadeProduto;
    private MarketListDbHelper dbHelper;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adicionar_produto);

        inicializarComponentes();
        configurarToolbar();
        configurarSpinnerCategoria();
        configurarSpinnerUnidade();
        configurarBotaoSalvar();
    }

    // inicializa
    private void inicializarComponentes() {
        editNomeProduto = findViewById(R.id.edit_nome_produto);
        editPrecoProduto = findViewById(R.id.edit_preco_produto);
        editQuantidadeProduto = findViewById(R.id.edit_quantidade_produto);
        spinnerCategoriaProduto = findViewById(R.id.spinner_categoria_produto);
        spinnerUnidadeProduto = findViewById(R.id.spinner_unidade_produto);
        btnSalvarProduto = findViewById(R.id.btn_salvar_produto);
        toolbar = findViewById(R.id.toolbar);
        dbHelper = new MarketListDbHelper(this);
    }

    // config toolbar
    private void configurarToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    // config spinner categoria
    private void configurarSpinnerCategoria() {
        List<String> categoriaDescricoes = new ArrayList<>();
        for (Produto.Categoria categoria : Produto.Categoria.values()) {
            categoriaDescricoes.add(categoria.getDescricao());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                categoriaDescricoes
        );
        spinnerCategoriaProduto.setAdapter(adapter);
    }

    // config spinner unidade
    private void configurarSpinnerUnidade() {
        List<String> unidadeDescricoes = new ArrayList<>();
        for (Produto.UnidadeMedida unidade : Produto.UnidadeMedida.values()) {
            unidadeDescricoes.add(unidade.getDescricao());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                unidadeDescricoes
        );
        spinnerUnidadeProduto.setAdapter(adapter);
        spinnerUnidadeProduto.setText(adapter.getItem(0), false); // Define UN como padrão
    }

    // botao salvar
    private void configurarBotaoSalvar() {
        btnSalvarProduto.setOnClickListener(v -> {
            String nome = editNomeProduto.getText().toString();
            String precoString = editPrecoProduto.getText().toString();
            String quantidadeString = editQuantidadeProduto.getText().toString();
            String descricaoCategoriaSelecionada = spinnerCategoriaProduto.getText().toString();
            String descricaoUnidadeSelecionada = spinnerUnidadeProduto.getText().toString();

            Produto.Categoria categoria = null;
            Produto.UnidadeMedida unidade = null;

            // encontra a categoria selecionada
            for (Produto.Categoria cat : Produto.Categoria.values()) {
                if (cat.getDescricao().equals(descricaoCategoriaSelecionada)) {
                    categoria = cat;
                    break;
                }
            }

            // encontra a unidade selecionada
            for (Produto.UnidadeMedida un : Produto.UnidadeMedida.values()) {
                if (un.getDescricao().equals(descricaoUnidadeSelecionada)) {
                    unidade = un;
                    break;
                }
            }

            // verifica se os campos estao vazios
            if (nome.isEmpty() || precoString.isEmpty() || quantidadeString.isEmpty() ||
                    categoria == null || unidade == null) {
                Toast.makeText(this, "Preencha todos os campos!", Toast.LENGTH_SHORT).show();
            } else {
                try {
                    double preco = Double.parseDouble(precoString);
                    double quantidade = Double.parseDouble(quantidadeString);

                    // cria o objeto Produto
                    Produto produto = new Produto(nome, quantidade, unidade, preco, categoria);

                    // tenta adicionar o produto
                    long id = dbHelper.insertProduto(produto);
                    if (id != -1) {
                        Toast.makeText(this, "Produto Adicionado com Sucesso!", Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK);
                        finish();
                    } else {
                        Toast.makeText(this, "Erro ao adicionar o produto.", Toast.LENGTH_SHORT).show();
                    }
                } catch (NumberFormatException e) {
                    Toast.makeText(this, "Por favor, insira números válidos!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
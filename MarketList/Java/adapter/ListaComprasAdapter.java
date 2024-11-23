package com.example.marketlist.adapter;

import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.marketlist.R;
import com.example.marketlist.model.Produto;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListaComprasAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    private List<Object> listaAgrupada;
    private Map<Produto.Categoria, List<Produto>> produtosPorCategoria;
    private OnItemClickListener listener;
    private OnItemCheckListener checkListener;
    private OnItemDeleteListener deleteListener;

    public interface OnItemClickListener {
        void onItemClick(Produto produto);
    }

    public interface OnItemCheckListener {
        void onItemCheck(Produto produto, boolean isChecked);
    }

    public interface OnItemDeleteListener {
        void onItemDelete(Produto produto);
    }

    public Object getItemAtPosition(int position) {
        return listaAgrupada.get(position);
    }

    public OnItemDeleteListener getDeleteListener() {
        return deleteListener;
    }

    public void atualizarLista(Map<Produto.Categoria, List<Produto>> novosProdutosPorCategoria) {
        this.produtosPorCategoria = novosProdutosPorCategoria;
        agruparProdutos();
        notifyDataSetChanged();
    }

    public Map<Produto.Categoria, List<Produto>> getProdutosPorCategoria() {
        return produtosPorCategoria;
    }

    public ListaComprasAdapter(Map<Produto.Categoria, List<Produto>> produtosPorCategoria, OnItemClickListener listener, OnItemCheckListener checkListener, OnItemDeleteListener deleteListener) {
        this.produtosPorCategoria = produtosPorCategoria;
        this.listener = listener;
        this.checkListener = checkListener;
        this.deleteListener = deleteListener;
        agruparProdutos();
    }

    private void agruparProdutos() {
        listaAgrupada = new ArrayList<>();
        for (Produto.Categoria categoria : Produto.Categoria.values()) {
            List<Produto> produtosCategoria = produtosPorCategoria.get(categoria);
            if (produtosCategoria != null && !produtosCategoria.isEmpty()) {
                listaAgrupada.add(categoria);
                listaAgrupada.addAll(produtosCategoria);
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (listaAgrupada.get(position) instanceof Produto.Categoria) {
            return TYPE_HEADER;
        }
        return TYPE_ITEM;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        if (viewType == TYPE_HEADER) {
            View view = inflater.inflate(R.layout.item_categoria_header, parent, false);
            return new CategoriaViewHolder(view);
        } else {
            View view = inflater.inflate(R.layout.item_produto, parent, false);
            return new ProdutoViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof CategoriaViewHolder) {
            Produto.Categoria categoria = (Produto.Categoria) listaAgrupada.get(position);
            ((CategoriaViewHolder) holder).bind(categoria);
        } else if (holder instanceof ProdutoViewHolder) {
            Produto produto = (Produto) listaAgrupada.get(position);
            ((ProdutoViewHolder) holder).bind(produto);
        }
    }

    @Override
    public int getItemCount() {
        return listaAgrupada.size();
    }

    class CategoriaViewHolder extends RecyclerView.ViewHolder {
        TextView txtCategoria;

        CategoriaViewHolder(@NonNull View itemView) {
            super(itemView);
            txtCategoria = itemView.findViewById(R.id.txt_categoria);
        }

        void bind(Produto.Categoria categoria) {
            txtCategoria.setText(categoria.getDescricao());
        }
    }

    class ProdutoViewHolder extends RecyclerView.ViewHolder {
        TextView txtDescricao, txtQuantidade, txtPreco;
        CheckBox chkComprado;
        Button btnDelete;

        ProdutoViewHolder(@NonNull View itemView) {
            super(itemView);
            txtDescricao = itemView.findViewById(R.id.txt_descricao_produto);
            txtQuantidade = itemView.findViewById(R.id.txt_quantidade);
            txtPreco = itemView.findViewById(R.id.txt_preco);
            chkComprado = itemView.findViewById(R.id.chk_comprado);
            btnDelete = itemView.findViewById(R.id.btn_delete);
        }

        void bind(Produto produto) {
            txtDescricao.setText(produto.getDescricao());
            txtQuantidade.setText(String.format("%s %s",
                    produto.getQuantidade(),
                    produto.getUnidade().getDescricao()));
            txtPreco.setText(String.format("R$ %.2f", produto.getPreco()));

            // remove o listener anterior p evitar chamadas múltiplas
            chkComprado.setOnCheckedChangeListener(null);
            chkComprado.setChecked(produto.isComprado());

            // atualiza a aparência inicial
            atualizarAparenciaProduto(produto.isComprado());

            chkComprado.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (checkListener != null) {
                    // tualiza a UI primeiro
                    atualizarAparenciaProduto(isChecked);
                    // depois notificar a mudança
                    itemView.post(() -> {
                        checkListener.onItemCheck(produto, isChecked);
                    });
                }
            });

            btnDelete.setOnClickListener(v -> {
                if (deleteListener != null) {
                    deleteListener.onItemDelete(produto);
                }
            });

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemClick(produto);
                }
            });
        }

        private void atualizarAparenciaProduto(boolean isChecked) {
            int flags = isChecked ? Paint.STRIKE_THRU_TEXT_FLAG : 0;
            txtDescricao.setPaintFlags(flags);
            txtQuantidade.setPaintFlags(flags);
            txtPreco.setPaintFlags(flags);
        }
    }
}
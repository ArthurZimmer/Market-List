package com.example.marketlist.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.marketlist.model.Produto;
import java.util.ArrayList;
import java.util.List;

public class MarketListDbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "market_list.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_NAME = "produtos";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_DESCRICAO = "descricao";
    private static final String COLUMN_QUANTIDADE = "quantidade";
    private static final String COLUMN_UNIDADE = "unidade";
    private static final String COLUMN_PRECO = "preco";
    private static final String COLUMN_CATEGORIA = "categoria";
    private static final String COLUMN_COMPRADO = "comprado";

    public MarketListDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableQuery = "CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_DESCRICAO + " TEXT, " +
                COLUMN_QUANTIDADE + " REAL, " +
                COLUMN_UNIDADE + " TEXT, " +
                COLUMN_PRECO + " REAL, " +
                COLUMN_CATEGORIA + " TEXT, " +
                COLUMN_COMPRADO + " INTEGER)";
        db.execSQL(createTableQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    // insert
    public long insertProduto(Produto produto) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_DESCRICAO, produto.getDescricao());
        values.put(COLUMN_QUANTIDADE, produto.getQuantidade());
        values.put(COLUMN_UNIDADE, produto.getUnidade().name());
        values.put(COLUMN_PRECO, produto.getPreco());
        values.put(COLUMN_CATEGORIA, produto.getCategoria().name());
        values.put(COLUMN_COMPRADO, produto.isComprado() ? 1 : 0);
        return db.insert(TABLE_NAME, null, values);
    }

    // select
    public List<Produto> getAllProdutos() {
        List<Produto> produtos = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                Produto produto = new Produto();
                produto.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)));
                produto.setDescricao(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRICAO)));
                produto.setQuantidade(cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_QUANTIDADE)));
                produto.setUnidade(Produto.UnidadeMedida.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_UNIDADE))));
                produto.setPreco(cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_PRECO)));
                produto.setCategoria(Produto.Categoria.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CATEGORIA))));
                produto.setComprado(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_COMPRADO)) == 1);
                produtos.add(produto);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return produtos;
    }

    // update
    public void updateProduto(Produto produto) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_DESCRICAO, produto.getDescricao());
        values.put(COLUMN_QUANTIDADE, produto.getQuantidade());
        values.put(COLUMN_UNIDADE, produto.getUnidade().name());
        values.put(COLUMN_PRECO, produto.getPreco());
        values.put(COLUMN_CATEGORIA, produto.getCategoria().name());
        values.put(COLUMN_COMPRADO, produto.isComprado() ? 1 : 0);
        db.update(TABLE_NAME, values, COLUMN_ID + " = ?", new String[] { String.valueOf(produto.getId()) });
    }

    // delete
    public boolean deleteProduto(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, "id = ?", new String[]{String.valueOf(id)});
        db.close();
        return true;
    }

    // calcula valor total
    public double getValorTotal() {
        double valorTotal = 0.0;
        List<Produto> produtos = getAllProdutos();
        for (Produto produto : produtos) {
            valorTotal += produto.getPreco() * produto.getQuantidade();
        }
        return valorTotal;
    }
}

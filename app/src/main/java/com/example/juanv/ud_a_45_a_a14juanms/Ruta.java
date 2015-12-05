package com.example.juanv.ud_a_45_a_a14juanms;

/**
 * Created by juanv on 30/11/2015.
 */
public class Ruta {
    private String nome;
    private String descricion;

    public Ruta(String nome, String descricion) {
        this.nome = nome;
        this.descricion = descricion;
    }
    public Ruta(){

    }

    public String getNome(){
        return this.nome;

    }
    public String getDescricion(){
        return this.descricion;

    }
    public void setNome(String nome){
        this.nome=nome;
    }
    public void setDescricion(String descricion){
        this.descricion=descricion;
    }

    @Override
    public String toString(){
        return "Nome="+this.nome+" Descricion="+this.descricion;
    }
}

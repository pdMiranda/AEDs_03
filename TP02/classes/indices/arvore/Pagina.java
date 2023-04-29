/** Pacotes **/
package TP02.classes.indices.arvore;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/** Classe Pagina **/
public class Pagina {
    /* Atributos */
    private boolean folha; // indica se a pagina eh folha ou nao
    private int nMax, // qtd max de chaves
                n, // qtd atual de chaves 
                tamPag; // tamanho fixo da pagina (bytes)
    private long posArq; // posicao da pagina em arquivo
    private int[] chaves;
    private long[] enderecos;
    private Pagina[] filhas;

    /* Getters e Setters */ 
    public boolean isFolha() {
        return folha;
    }
    public void setFolha(boolean folha) {
        this.folha = folha;
    }
    public int getN() {
        return n;
    }
    public void setN(int n) {
        this.n = n;
    }
    public int getTamPag() {
        return tamPag;
    }
    public long getPosArq() {
        return posArq;
    }
    public void setPosArq(long posArq) {
        this.posArq = posArq;
    }
    public int getChave(int i) {
        return chaves[i];
    }
    public void setChave(int i, int chave) {
        this.chaves[i] = chave;
    }
    public long getEndereco(int i) {
        return enderecos[i];
    }
    public void setEndereco(int i, long endereco) {
        this.enderecos[i] = endereco;
    }
    public Pagina getFilha(int i) {
        return filhas[i];
    }
    public void setFilha(int i, Pagina filha) {
        this.filhas[i] = filha;
    }   

    /* Construtores */
    public Pagina(int nMax, boolean folha){
        this(nMax, folha, -1);
    }
    public Pagina(int nMax, boolean folha, long posArq) {
        this.folha = folha;
        this.nMax = nMax;
        this.n = 0;
        
        // tamanho da pagina = folha + n + (nMax) chaves e enderecos + (nMax+1) filhas
        this.tamPag = 1 + Integer.BYTES + (nMax * (Integer.BYTES+Long.BYTES)) + ((nMax+1) * Long.BYTES); 
        this.posArq = posArq;

        this.chaves = new int[nMax];
        this.enderecos = new long[nMax];
        for(int i = 0; i < nMax; i++){
            this.chaves[i] = -1;
            this.enderecos[i] = -1;
        }

        this.filhas = new Pagina[nMax+1];
        for(int i = 0; i < (nMax+1); i++){
            this.filhas[i] = null;
        }
    }

    /* Metodos */
        /* Basicos */
    /**
     * @return atributos da classe como string
     */  
    @Override
    public String toString() {
        String str = "" + posArq + " | ";
        
        if(folha) str += "*";
        else str += "-";

        str += " | " + n;

        int i = 0;
        while(i < n){ // espaco de chaves e enderecos preenchidos 
            if(filhas[i] != null) str += " | " + filhas[i].getPosArq();
            else str += " | -1";

            str += " | " + chaves[i] + " | " + enderecos[i];
            i++;
        }
        if(filhas[i] != null) str += " | " + filhas[i].getPosArq();
        else str += " | -1";

        str += " |";

        return str;
    }
    /**
     * Converte objeto da classe para um array de bytes, escrevendo seus atributos
     * @return Byte array do objeto
     */
    public byte[] toByteArray() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        
        try{
            // escreve se pagina eh folha ou nao
            if(folha) dos.writeByte('*');
            else dos.writeByte(' '); 

            // escreve qtd de chaves na pagina
            dos.writeInt(n);

            // escreve ponteiros p/filhas e pares chave/endereco 
            int i = 0;
            while(i < nMax){
                if(filhas[i] == null) 
                    dos.writeLong(-1);
                else 
                    dos.writeLong(filhas[i].getPosArq());

                dos.writeInt(chaves[i]);
                dos.writeLong(enderecos[i]);
                i++;
            }
            if(filhas[i] == null) 
                dos.writeLong(-1);
            else 
                dos.writeLong(filhas[i].getPosArq());
        } catch(IOException ioe){
            System.err.println(ioe.getMessage());
        }
        
        return baos.toByteArray();
    }
    /**
     * Converte um array de bytes para os atributos da classe, atribuindo
     * ao objeto corrente
     * @param byteArray array de bytes de um objeto
     */
    public void fromByteArray(byte[] ba) {
        ByteArrayInputStream bais = new ByteArrayInputStream(ba);
        DataInputStream dis = new DataInputStream(bais);
        
        try{
            // le se pagina eh folha ou nao
            byte bFolha = dis.readByte();
            if(bFolha == '*') folha = true;
            else folha = false; 

            // le qtd de chaves na pagina
            n = dis.readInt();

            // le ponteiros p/filhas e pares chave/endereco 
            int i = 0;
            long posFilha = -1;
            while(i < nMax){
                posFilha = dis.readLong();
                if(posFilha == -1) 
                    filhas[i] = null;
                else 
                    filhas[i] = new Pagina(nMax, true, posFilha);

                chaves[i] = dis.readInt();
                enderecos[i] = dis.readLong();
                i++;
            }
            posFilha = dis.readLong();
            if(posFilha == -1) 
                filhas[i] = null;
            else 
                filhas[i] = new Pagina(nMax, true, posFilha);
        } catch(IOException ioe){
            System.err.println(ioe.getMessage());
        }
    }
        /* Manipulacao da Arvore */
    /**
     * Remove par chave/endereco da pagina, movendo outros pares  
     * @param chave identificador a ser removido
     */
    public void remover(int chave) {
        int i = pesquisar(chave);
        
        for(int j = i; j < (nMax-1); j++){
            chaves[j] = chaves[j+1]; 
            enderecos[j] = enderecos[j+1]; 
        }
        chaves[nMax-1] = -1;
        enderecos[nMax-1] = -1;
        n--;
    }
    /**
     * Insere chave na pagina, procurando posicao de forma a manter ordenada
     * @param chave identificador a ser inserido
     * @param endereco posicao em arquivo da chave
     */
    public void inserir(int chave, long endereco) {
        int i = n - 1; 
        // copia chaves maiores p/proximas posicoes
        while(i >= 0 && chave < chaves[i]){
            chaves[i+1] = chaves[i];
            enderecos[i+1] = enderecos[i];
            i--;
        }
        // achou posicao de insercao da chave
        chaves[i+1] = chave;
        enderecos[i+1] = endereco; 
        n++;
    }
    /**
     * Pesquisa chave na pagina, retornando sua posicao na pagina ou -1 se nao achar
     * @param chave identificador a ser pesquisado 
     * @return int posicao da chave
     */
    public int pesquisar(int chave) {
        int i = 0;

        boolean achou = false;
        while(i < n && !achou){
            if(chave == chaves[i]) achou = true;
            else i++;
        }
        if(!achou) i = -1;

        return i;
    }
    /**
     * Achar em qual pagina filha deve-se inserir a chave, retornando sua posicao 
     * @param chave identificador a ser inserido
     * @return int posicao da pagina filha
     */
    public int acharPosFilha(int chave) {
        int i = n - 1; 
        while(i >= 0 && chave < chaves[i]){
            i--;
        }
        i++; // posicao da pagina filha

        return i;
    }
}

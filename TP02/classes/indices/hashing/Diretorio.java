/** Pacotes **/
package TP02.classes.indices.hashing;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/** Classe Diretorio **/
public class Diretorio {
    /* Atributos */
    private int pGlobal; // profundidade global
    private long[] enderecos; // array de enderecos dos buckets

    /* Getters e Setters */
    public int getPGlobal() {
        return this.pGlobal;
    }
    public long getEndereco(int i) {
        return this.enderecos[i];
    }
    public void setEndereco(int i, long enderecoBucket) {
        this.enderecos[i] = enderecoBucket;
    }

    /* Construtores */
    public Diretorio() {
        this(1);
    }
    public Diretorio(int pGlobal) {
        this.pGlobal = pGlobal; // profundidade comeca em 1
        this.enderecos = new long[ (int)Math.pow(2, pGlobal) ];
        
        for(int i = 0; i < this.enderecos.length; i++){
            this.enderecos[i] = -1; 
        }
    }
    
    /* Metodos */
        /* Basicos */
    /**
     * @return atributos da classe como string
     */  
    @Override
    public String toString() {
        String str = "pg = " + pGlobal;
        
        int n = (int) Math.pow(2, pGlobal);
        for(int i = 0; i < n; i++){
            str += "\n" + i + ": " + enderecos[i];
        }

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
            dos.writeInt(this.pGlobal);
            
            for(int i = 0; i < this.enderecos.length; i++){
                dos.writeLong(this.enderecos[i]);
            }
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
            pGlobal = dis.readInt();
            
            int n = (int) Math.pow(2, pGlobal);
            enderecos = new long[n];
            
            for(int i = 0; i < n; i++){
                enderecos[i] = dis.readLong();
            }
        } catch(IOException ioe){
            System.err.println(ioe.getMessage());
        }
    }
        /* Manipulacao do Hashing */
    /**
     * Aumenta profundidade global do diretorio em 1, dobrando tamanho do diretorio
     * e copiando ponteiros da primeira metade pra segunda metade
     */
    public void aumentarP() {
        // copia enderecos atuais em um array tmp
        int tam = (int)Math.pow(2, pGlobal);
        long[] enderecosTmp = new long[tam];
        for(int i = 0; i < tam; i++){
            enderecosTmp[i] = enderecos[i];
        }

        pGlobal++; // aumenta pGlobal
        
        // faz novo array 
        enderecos = new long[ (int)Math.pow(2, pGlobal) ];
        for(int i = 0; i < tam; i++){
            enderecos[i] = enderecosTmp[i];
            enderecos[i + tam] = enderecosTmp[i];
        }
    }
}

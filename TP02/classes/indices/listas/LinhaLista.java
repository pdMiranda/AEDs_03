/** Pacotes **/
package TP02.classes.indices.listas;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/** Classe LinhaLista **/
public class LinhaLista {
    /* Atributos */
    private String termo;
    private ArrayList<Long> ocorrencias;

    /* Getters e Setters */
    public String getTermo() {
        return termo;
    }
    public void setTermo(String termo) {
        this.termo = termo;
    }
    public int getSize() {
        return ocorrencias.size();
    }
    public ArrayList<Long> getOcorrencias() {
        return ocorrencias;
    }
    public long getOcorrencia(int i) {
        return ocorrencias.get(i);
    }
    public void setOcorrencia(int i, long ocorrencia) {
        ocorrencias.add(i, ocorrencia);
    }
    public void delOcorrencia(int i) {
        this.ocorrencias.remove(i);
    }

    /* Construtores */
    public LinhaLista(){
        this("");  
    }
    public LinhaLista(String termo){
        this.termo = termo;
        this.ocorrencias = new ArrayList<Long>();
    }
    
    /* Metodos */
        /* Basicos */
    /**
     * @return atributos da classe como string
     */  
    @Override
    public String toString() {
        String str = "" + termo + " | n = " + ocorrencias.size();

        for(int i = 0; i < ocorrencias.size(); i++){
            str += " | " + ocorrencias.get(i);
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
            dos.writeUTF(termo);
            dos.writeInt(ocorrencias.size());
            
            for(int i = 0; i < ocorrencias.size(); i++){
                dos.writeLong(ocorrencias.get(i));
            }
        } catch(IOException ioe){
            System.err.println(ioe.getMessage());
        }
        
        return baos.toByteArray();
    }
}

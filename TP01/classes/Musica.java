/** Pacotes **/
package TP01.classes;
import java.util.Date;
import java.util.ArrayList;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.io.IOException;

/** Classe Musica **/
public class Musica {
    /* Atributos */
    protected int ID;
    protected int duration_ms;
    protected Date release_date;
    protected String track_id, // string de tamanho fixo (22) 
                     name;    // string de tamanho variável
    protected ArrayList<String> artists;

    /* Getters e Setters */ 
    public int getID() {
        return this.ID;
    }
    public void setID(int ID) {
        this.ID = ID;
    }
    public int getDuration_ms() {
        return this.duration_ms;
    }
    public void setDuration_ms(int duration_ms) {
        this.duration_ms = duration_ms;
    }
    public Date getRelease_date() {
        return this.release_date;
    }
    public void setRelease_date(Date release_date) {
        this.release_date = release_date;
    }
    public String getTrack_id() {
        return this.track_id;
    }
    public void setTrack_id(String track_id) {
        this.track_id = track_id;
    }
    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public ArrayList<String> getArtists() {
        return this.artists;
    }
    public void setArtists(ArrayList<String> artists) {
        this.artists = artists;
    }

    /* Construtores */  
    public Musica() { 
        this(-1, -1, new Date(), "", "", new ArrayList<String>());
    }
    public Musica( int ID, int duration_ms, Date release_date, String track_id, 
                   String name, ArrayList<String> artists ) {
        this.ID = ID;
        this.duration_ms = duration_ms;
        this.release_date = release_date;
        this.track_id = track_id;
        this.name = name;
        this.artists = artists;
    }

    /* Métodos */
        /* Sobreescritos */
    /**
     * @return atributos da classe Musica como string
     */
    @Override
    public String toString() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return "ID: "+this.ID+
               "\nDuration_ms: "+this.duration_ms+
               "\nRelease_date: "+sdf.format(this.release_date)+
               "\nTrack_id: "+this.track_id+
               "\nName: "+this.name+
               "\nArtists: "+this.artists.toString();
    }
    /**
     * @return objeto clone do objeto corrente
     */
    @Override
    public Musica clone() {
        Musica clone = new Musica( this.ID, this.duration_ms, this.release_date, 
                                   this.track_id, this.name, this.artists );
        
        return clone;
    }
        /* Manipulação de bytes */
    /**
     * Converte objeto da classe para um array de bytes, escrevendo todos os atributos
     * e a quantidade de elementos na lista de artistas
     * @return Byte array do objeto
     */
    public byte[] toByteArray() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        
        try{
            dos.writeInt(this.ID);
            dos.writeInt(this.duration_ms);
            dos.writeUTF(sdf.format(this.release_date)); 
            dos.writeUTF(this.track_id); 
            dos.writeUTF(this.name);
           
            dos.writeInt(artists.size()); // qtd de artistas na lista
            for(String artist : this.artists){
                dos.writeUTF(artist);
            }
        } catch(IOException ioe){
            System.err.println("Erro ao escrever atributo como byte");
            ioe.printStackTrace();
        }

        return baos.toByteArray();
    }
    /**
     * Converte um array de bytes para os atributos da classe Musica, atribuindo
     * ao objeto corrente
     * @param byteArray array de bytes de um objeto Musica
     */
    public void fromByteArray(byte byteArray[]) {
        ByteArrayInputStream bais = new ByteArrayInputStream(byteArray);
        DataInputStream dis = new DataInputStream(bais);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        try{
            this.ID = dis.readInt();
            this.duration_ms = dis.readInt();

            try{
                this.release_date = sdf.parse(dis.readUTF()); 
            } catch(ParseException pe){

            }
    
            this.track_id = dis.readUTF();
            this.name = dis.readUTF();
    
            int num_artists = dis.readInt(); // qtd de artistas na lista
            for(int i = 0; i < num_artists; i++){
                artists.add(dis.readUTF());
            }
        } catch(IOException ioe){

        }
    }
        /* Database */
    /**
     * Faz o parse de uma linha do arquivo CSV, atribuindo valores ao objeto corrente da 
     * classe Musica a partir dos atributos lidos e separados
     * @param line String de uma linha do CSV 
     */
    public void parseCSV(String line) {
        final int TAM = line.length();
        int index = 0;

        // Strings p/salvar cada atributo
        String durationString = "", dateString = "", artistsString = "";
        this.track_id = this.name = ""; // inicializa atributos do tipo String
        
        Boolean found = false;
     
        /* Procura atributo duration_ms */
        while(!found){
            
            if(line.charAt(index) != ','){ // add caracteres na string
                durationString += line.charAt(index);
            } else{ // achou fim
                found = true;
            }
            index++; // próximo index
       
        }
        this.duration_ms = Integer.parseInt(durationString); // transforma em int
        
        /* Procura atributo release_date */
        found = false;
        while(!found){
            
            if(line.charAt(index) != ','){ // add caracteres na string
                dateString += line.charAt(index);
            } else{ // achou fim
                found = true;
            }
            index++; // próximo index
       
        }
        // Cria formato de data e transforma string em Date
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try{
            this.release_date = sdf.parse(dateString);
        } catch(ParseException pe){
            System.err.println("Erro ao fazer parse da data");
            pe.printStackTrace();
        }
    
        /* Procura atributo track_id */
        found = false;
        while(!found){
            
            if(line.charAt(index) != ','){ // add caracteres na string
                this.track_id += line.charAt(index);
            } else{ // achou fim
                found = true;
            }
            index++; // próximo index
       
        }
    
        /* Procura atributo name */
        found = false;
        if(line.charAt(index) == '\"'){ // verifica se nome está entre aspas => contém vírgula
            index++; // pula a " abrindo

            while(!found){

                if(line.charAt(index) != '\"'){ // add caracteres na string
                    this.name += line.charAt(index);
                } else{ // achou fim
                    found = true;
                }
                index++; // próximo index

            }
            index++; // pula a vírgula      
        }
        else{ // parse normal pela vírgula
            while(!found){
                
                if(line.charAt(index) != ','){ // add caracteres na string
                    this.name += line.charAt(index);
                } else{ // achou fim
                    found = true;
                }
                index++; // próximo index
        
            }
        }
    
        /* Procura atributo artists */
        while(index < TAM){ // vai até o fim da linha
            if( line.charAt(index) != '[' && line.charAt(index) != ']' && 
                line.charAt(index) != '\'' && line.charAt(index) != '\"' ){
                    
                artistsString += line.charAt(index); // add caracteres na string
            }
            index++; // próximo index
        }        
        // Add artistas separados por vírgula ao ArrayList
        String[] artistsArray = artistsString.split(", ");
        for(int j = 0; j < artistsArray.length; j++){
            this.artists.add(artistsArray[j]);
        }
    } 
} 

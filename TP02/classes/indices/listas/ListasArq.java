/** Pacotes **/
package TP02.classes.indices.listas;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.File;

/** Classe ListaArq **/
public class ListasArq {
    /* Atributos */
    private final String pathNomes = "TP02/data/listaNomes.db", // paths dos arquivos
                         pathArtistas = "TP02/data/listaArtistas.db"; 
    private RandomAccessFile rafNomes, // arquivos 
                             rafArtistas;

    /* Construtor */
    public ListasArq() {
        // abrir ou criar arquivos 
        try{
            rafNomes = new RandomAccessFile(pathNomes, "rw");
            rafArtistas = new RandomAccessFile(pathArtistas, "rw");

            // cria arquivo se nao existir
            if(!exists()){
                rafNomes.seek(0);
                rafArtistas.seek(0);
                
                // qtd de linhas = 0
                rafNomes.writeInt(0);
                rafArtistas.writeInt(0);
            }
        } catch(FileNotFoundException fnfe){
            System.err.println(fnfe.getMessage());
        } catch(IOException ioe){
            System.err.println(ioe.getMessage());
        }
    }

    /* Metodos */
        /* Manipulacao dos arquivos */
    /**
     * Verifica se arquivos ja existem 
     * @return true se sim, false se nao
     */
    public boolean exists() {
        boolean existem = false;

        try{
            if(rafNomes.length() != 0 && rafArtistas.length() != 0)
                existem = true;
        } catch(IOException ioe){
            System.err.println(ioe.getMessage());
        }

        return existem;
    }
    /**
     * Fecha arquivos RandomAcessFile
     */
    public void close() {
        try{
            rafNomes.close();
            rafArtistas.close();
        } catch(IOException ioe){
            System.err.println("Erro ao fechar arquivos");
            ioe.printStackTrace();
        }
    }
    /**
     * Deleta arquivos RandomAcessFile 
     * @return true se conseguir deletar, false caso contrario
     */
    public boolean deleteFiles() {
        boolean sucesso = false;
        
        File dir = new File(pathNomes);
        File bucket = new File(pathArtistas);
        
        if(dir.delete() && bucket.delete())
            sucesso = true;

        return sucesso;
    }
        /* Funcoes auxiliares */
    /**
     * Imprime lista invertida de nomes do arquivo
     */
    public void printNomes() {
        ListaInvertida listaNomes = leListaNomes();
        listaNomes.print();
    }
    /**
     * Imprime lista invertida de artistas do arquivo
     */
    public void printArtistas() {
        ListaInvertida listaArtistas = leListaArtistas();
        listaArtistas.print();
    }
    /**
     * Le lista invertida de nomes do arquivo
     * @return ListaInvertida de nomes
     */
    private ListaInvertida leListaNomes() {
        ListaInvertida listaNomes = new ListaInvertida();
        
        try{
            // abre arquivo
            rafNomes = new RandomAccessFile(pathNomes, "rw");

            // le lista de nomes
            byte[] byteArray = new byte[(int)rafNomes.length()];
            rafNomes.seek(0);
            rafNomes.read(byteArray);
            listaNomes.fromByteArray(byteArray);
        } catch(FileNotFoundException fnfe){
            System.err.println(fnfe.getMessage());
        } catch(IOException ioe){
            System.err.println(ioe.getMessage());
        }

        return listaNomes;
    }
    /**
     * Le lista invertida de artistas do arquivo
     * @return ListaInvertida de artistas
     */
    private ListaInvertida leListaArtistas() {
        ListaInvertida listaArtistas = new ListaInvertida();
        
        try{
            // abre arquivo
            rafArtistas = new RandomAccessFile(pathArtistas, "rw");

            // le lista de artistas
            byte[] byteArray = new byte[(int)rafArtistas.length()];
            rafArtistas.seek(0);
            rafArtistas.read(byteArray);
            listaArtistas.fromByteArray(byteArray);
        } catch(FileNotFoundException fnfe){
            System.err.println(fnfe.getMessage());
        } catch(IOException ioe){
            System.err.println(ioe.getMessage());
        }

        return listaArtistas;
    }
    /**
     * Escreve lista invertida de nomes no arquivo
     * @param listaNomes ListaInvertida de nomes
     */
    private void escreveListaNomes(ListaInvertida listaNomes) {
        try{
            // abre arquivos
            rafNomes = new RandomAccessFile(pathNomes, "rw");

            // escreve lista de nomes
            byte[] byteArray = listaNomes.toByteArray();
            rafNomes.seek(0);
            rafNomes.write(byteArray);
        } catch(FileNotFoundException fnfe){
            System.err.println(fnfe.getMessage());
        } catch(IOException ioe){
            System.err.println(ioe.getMessage());
        }
    }
    /**
     * Escreve lista invertida de artistas no arquivo
     * @param listaArtistas ListaInvertida de artistas
     */
    private void escreveListaArtistas(ListaInvertida listaArtistas) {
        try{
            // abre arquivo
            rafArtistas = new RandomAccessFile(pathArtistas, "rw");

            // escreve lista de artistas
            byte[] byteArray = listaArtistas.toByteArray();
            rafArtistas.seek(0);
            rafArtistas.write(byteArray);
        } catch(FileNotFoundException fnfe){
            System.err.println(fnfe.getMessage());
        } catch(IOException ioe){
            System.err.println(ioe.getMessage());
        }
            
    }
        /* Operacoes nas listas */
    /**
     * Pesquisa string nas listas invertidas, de acordo com o tipo de pesquisa desejado
     * @param query String a ser pesquisada nas listas
     * @param tipo int indicando qual pesquisa deve ser feita (nome, artistas ou os dois)
     * @return ArrayList<Long> enderecos dos registros em que houve ocorrencia dos termos pesquisados
     */
    public ArrayList<Long> pesquisar(String query, String nome, String artista, int tipo) {
        ArrayList<Long> enderecos = new ArrayList<Long>();

        if(tipo == 1){ // pesquisa por nome
            ListaInvertida listaNomes = leListaNomes();

            if(listaNomes.getSize() != 0)
                enderecos = listaNomes.pesquisar(query);    
            else
                System.err.println("Lista de nomes esta vazia, nao foi possivel pesquisar.");
        } else if(tipo == 2){ // pesquisa por artista
            ListaInvertida listaArtistas = leListaArtistas();
            
            if(listaArtistas.getSize() != 0)
                enderecos = listaArtistas.pesquisar(query);            
            else
                System.err.println("Lista de artistas esta vazia, nao foi possivel pesquisar.");
        } else if(tipo == 3){ // pesquisa por nome e artista
            ListaInvertida listaNomes = leListaNomes();
            ListaInvertida listaArtistas = leListaArtistas();
            
            if(listaNomes.getSize() != 0 && listaArtistas.getSize() != 0){
                ArrayList<Long> endNomes = listaNomes.pesquisar(nome);
                ArrayList<Long> endArtistas = listaArtistas.pesquisar(artista);

                // copia enderecos iguais (AND das duas pesquisas)
                int i = 0, j = 0;
                while(i < endNomes.size() && j < endArtistas.size()){
                    if(endNomes.get(i).equals(endArtistas.get(j))){
                        enderecos.add(endNomes.get(i));    
                        i++; j++;
                    } else if(endNomes.get(i).compareTo(endArtistas.get(j)) < 0){
                        i++;
                    } else{
                        j++;
                    }
                }
            } else{
                System.err.println("Listas invertidas estao vazias, nao foi possivel pesquisar.");
            }
        }

        return enderecos;
    }
    /**
     * Cria termos nas listas invertidas, a partir do nome da musica e dos artistas, separando
     * string por espaco e inserindo nas listas os termos e o endereco da sua ocorrencia
     * @param nome String do nome da musica
     * @param artistas ArrayList<String> de artistas da musica
     * @param endereco long posicao no arquivo de dados da musica
     * @return true se conseguir criar termos nas listas, false se nao
     */
    public boolean create(String nome, ArrayList<String> artistas, long endereco) {
        boolean sucesso = true;
        
        if(!createNome(nome, endereco) || !createArtistas(artistas, endereco))
            sucesso = false;

        return sucesso;
    }
    /**
     * Cria termos na lista invertida de nomes, a partir do nome da musica, separando
     * string por espaco e inserindo nas listas os termos e o endereco da sua ocorrencia
     * @param nome String do nome da musica
     * @param endereco long posicao no arquivo de dados da musica
     * @return true se conseguir criar termos nas listas, false se nao
     */
    public boolean createNome(String nome, long endereco) {
        boolean sucesso = true;
        
        ListaInvertida listaNomes = leListaNomes();
        
        // insere novos termos na lista de nomes
        String[] termosNomes = nome.split(" ");
        for(int i = 0; i < termosNomes.length; i++){
            sucesso = listaNomes.inserir(termosNomes[i], endereco);
        }

        escreveListaNomes(listaNomes);

        return sucesso;
    }
    /**
     * Cria termos na lista invertida de artistas, a partir dos artistas da musica, separando
     * string por espaco e inserindo nas listas os termos e o endereco da sua ocorrencia
     * @param artistas ArrayList<String> de artistas da musica
     * @param endereco long posicao no arquivo de dados da musica
     * @return true se conseguir criar termos nas listas, false se nao
     */
    public boolean createArtistas(ArrayList<String> artistas, long endereco) {
        boolean sucesso = true;
        
        ListaInvertida listaArtistas = leListaArtistas();
        
        // insere novos termos na lista de artistas
        for(int i = 0; i < artistas.size(); i++){
            String[] termosArtistas = artistas.get(i).split(" ");
            for(int j = 0; j < termosArtistas.length; j++){
                sucesso = listaArtistas.inserir(termosArtistas[j], endereco);
            }
        }

        escreveListaArtistas(listaArtistas);

        return sucesso;
    }
    /**
     * Remove termos das listas invertidas, que tem sua ocorrencia no registro do endereco 
     * passado
     * @param endereco long posicao no arquivo de dados da musica
     * @return true se conseguir deletar termos, false caso contrario
     */
    public boolean delete(long endereco) {
        boolean sucesso = true;
        
        if(!deleteNome(endereco) || !deleteArtistas(endereco))
            sucesso = false;

        return sucesso;
    } 
    /**
     * Remove termos da lista invertida de nomes, que tem sua ocorrencia no registro do 
     * endereco passado
     * @param endereco long posicao no arquivo de dados da musica
     * @return true se conseguir deletar termos, false caso contrario
     */
    public boolean deleteNome(long endereco) {
        boolean sucesso = true;
        
        ListaInvertida listaNomes = leListaNomes();
        
        System.out.println("Removendo endereco " + endereco + " da lista invertida de nomes.");

        // remove termos da lista
        if(listaNomes.getSize() != 0)
            sucesso = listaNomes.remover(endereco);
        else
            System.err.println("Lista de nomes esta vazia, nao foi possivel remover.");
        
        escreveListaNomes(listaNomes);

        return sucesso;
    } 
    /**
     * Remove termos da lista invertida de artistas, que tem sua ocorrencia no registro do 
     * endereco passado
     * @param endereco long posicao no arquivo de dados da musica
     * @return true se conseguir deletar termos, false caso contrario
     */
    public boolean deleteArtistas(long endereco) {
        boolean sucesso = true;
        
        ListaInvertida listaArtistas = leListaArtistas();
        
        System.out.println("Removendo endereco " + endereco + " da lista invertida de artistas.");

        // remove termos da lista
        if(listaArtistas.getSize() != 0)
            sucesso = listaArtistas.remover(endereco);
        else
            System.err.println("Lista de artistas esta vazia, nao foi possivel remover.");
        
        escreveListaArtistas(listaArtistas);

        return sucesso;
    } 
}

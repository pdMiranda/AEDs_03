/** Pacotes **/
package TP02;
import TP02.classes.CRUD;
import TP02.classes.Musica;
import TP02.classes.indices.arvore.ArvoreArq;
import TP02.classes.indices.hashing.HashEstendido;
import TP02.classes.indices.listas.ListasArq;
import java.util.ArrayList;
import java.util.Date;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/* Classe TP02 (Main) */
public class TP02 {
    /* Atributos */
    public static BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    
    /* Metodos */
        /* Main */
    /**
     * Fornece um menu com opcoes para carregar o arquivo da base de dados (CSV),
     * realizar operações do CRUD, alterando arquivos de dados e de indices
     */
    public static void main(String[] args) {
        try{
            // arquivo CSV (base de dados)
            String basePath = "TP02/data/spotify.csv";
            BufferedReader fr = new BufferedReader(new FileReader(basePath));
            
            // arquivo RAF (registros em bytes) 
            CRUD arquivo = new CRUD();

            int tamBase = 1000; // tamanho da base csv
            int opc = -1; // opcao do menu

            // abre indices
            HashEstendido hash = new HashEstendido((int)(0.05 * tamBase));
            ArvoreArq arvore = new ArvoreArq();
            ListasArq listas = new ListasArq();

            do{
                opc = menu();

                // executa tarefas do menu de CRUD
                switch(opc){ 
                      case 0: { // Carga do CSV
                        System.out.println("\n**Fazendo carga inicial**");

                        String line; // linha do CSV
                        boolean sucesso = true;
                        // lê (tamBase) musicas (linhas) do CSV, faz parse e cria registros
                        for(int i = 0; i < tamBase; i++){
                            line = fr.readLine();
                            Musica musica = new Musica();
                            musica.parseCSV(line);
                            
                            if(!arquivo.create(musica, tamBase))
                                sucesso = false; 
                        }
                        System.out.println("Base de dados carregada. "+tamBase+" registros criados.");

                        if(sucesso)
                            System.out.println("Arquivos de indice carregados com sucesso.");
                        else
                            System.out.println("Erro ao carregar arquivos de indice!");

                        break;
                    } case 1: { // Create
                        System.out.println("\n**Criando musica**");
                        
                        Musica msc = lerMusica();
    
                        if(arquivo.create(msc, tamBase))
                            System.out.println("Arquivos de indice atualizados com sucesso.");
                        else
                            System.out.println("Erro ao atualizar arquivos de indice!");
                        
                        System.out.println("\n" + msc);
                                                
                        break;
                    } case 2: { // Read
                        System.out.println("\n**Lendo musica**");
                        
                        System.out.print("ID da musica a ser lida: ");
                        int readID = Integer.parseInt(br.readLine());
                        int indice = lerIndice();
                        
                        Musica msc = arquivo.read(readID, tamBase, indice);
                        if(msc != null)
                            System.out.println("\n" + msc);
                        else
                            System.out.println("Musica buscada nao encontrada.");
                        
                        break;
                    } case 3: { // Update
                        System.out.println("\n**Atualizando musica**");
                        
                        System.out.print("ID da musica que deve ser alterada: ");
                        int updateID = Integer.parseInt(br.readLine());
                        int indice = lerIndice();
                        
                        Musica msc = arquivo.read(updateID, tamBase, indice);
                        if(msc != null){
                            System.out.println("\n" + msc);
                            Musica nova = lerAtualizacao(msc);
                                                    
                            if(arquivo.update(nova, tamBase, indice))
                                System.out.println("Musica atualizada com sucesso.");
                            else
                                System.out.println("Erro ao atualizar musica.");
                        } else{
                            System.out.println("Musica a ser atualizada nao encontrada.");
                        }

                        break;
                    } case 4: { // Delete
                        System.out.println("\n**Deletando musica**");
                        
                        System.out.print("ID da musica que deve ser deletada: ");   
                        int deleteID = Integer.parseInt(br.readLine());     
                        int indice = lerIndice();

                        if(arquivo.delete(deleteID, tamBase, indice))
                            System.out.println("Musica removida com sucesso.");
                        else
                            System.out.println("Erro ao remover musica.");

                        break;
                    } case 5: { // Indices
                        int opc2 = -1;

                        do{ 
                            opc2 = subMenu();

                            switch(opc2){
                                  case 0: { // Sai do menu interno
                                    System.out.println("\n**Retornando ao menu anterior...**");
                                    break;
                                } case 1: { // Mostrar Arvore B
                                    System.out.println("\n**Arvore B**");

                                    arvore.print();

                                    break;
                                } case 2: { // Mostrar Hashing Estendido
                                    System.out.println("\n**Hashing Estendido**");

                                    hash.print();

                                    break;
                                } case 3: { // Mostrar Lista invertida de nomes 
                                    System.out.println("\n**Lista invertida de nomes**");

                                    listas.printNomes();

                                    break;
                                } case 4: { // Mostrar Lista invertida de artistas
                                    System.out.println("\n**Lista invertida de artistas**");

                                    listas.printArtistas();

                                    break;
                                } case 5: { // Listas invertidas 
                                    System.out.println("\n**Listas invertidas**");

                                    int pesq = lerPesquisa();
                                    System.out.print("Query de pesquisa: ");
                                    String query = "", nome = "", artista = "";
                                    
                                    if(pesq == 3){
                                        System.out.print("\nNome: ");
                                        nome = br.readLine(); 
                                        System.out.print("\nArtista: ");
                                        artista = br.readLine();
                                    } else{
                                        query = br.readLine();
                                    }

                                    ArrayList<Long> ocorrencias = listas.pesquisar(query, nome, artista, pesq);

                                    if(ocorrencias.size() == 0){
                                        System.out.println("Nenhum resultado encontrado para essa pesquisa.");
                                    } else{
                                        for(int i = 0; i < ocorrencias.size(); i++){
                                            Musica msc = arquivo.readPos(ocorrencias.get(i));
                                            if(msc != null){
                                                System.out.println("\nEndereco " + ocorrencias.get(i));
                                                System.out.println(msc);
                                            } else{
                                                System.out.println("Musica pesquisada nao encontrada.");
                                            }
                                        }
                                    }

                                    break;
                                }
                            }
                        } while(opc2 != 0);

                        break;
                    } case 6: { // Fecha arquivos e encerra programa
                        System.out.println("\n**Encerrando programa**");
                        
                        if(arquivo.exists()) arquivo.close();
                        if(hash.exists()) hash.close();
                        if(arvore.exists()) arvore.close();
                        if(listas.exists()) listas.close();
                        
                        break;
                    } case 7: { // Deleta arquivos
                        System.out.println("\n**Deletando arquivos**");
                        
                        if(arquivo.exists()){
                            if(arquivo.deleteFile())
                                System.out.println("Arquivo de dados deletado com sucesso.");
                            else
                                System.out.println("Erro ao deletar arquivo de dados.");
                        }
                        
                        if(hash.exists()){
                            if(hash.deleteFiles())
                                System.out.println("Arquivos de hash deletados com sucesso.");
                            else
                                System.out.println("Erro ao deletar arquivos de hash.");
                        }

                        if(arvore.exists()){
                            if(arvore.deleteFile())
                                System.out.println("Arquivo da arvore B deletado com sucesso.");
                            else
                                System.out.println("Erro ao deletar arquivo da arvore B.");
                        }

                        if(listas.exists()){
                            if(listas.deleteFiles())
                                System.out.println("Arquivos de listas invertidas deletados com sucesso.");
                            else
                                System.out.println("Erro ao deletar arquivos de listas invertidas.");
                        }

                        break;
                    }
                }
            } while(opc != 6);
          
            fr.close();
            br.close();
        } catch(IOException ioe){
            System.err.println("Erro de leitura na funcao principal");
            ioe.printStackTrace();
        }
    }
        /* Menus */
    /**
     * Mostra menu principal e solicita ao usuario qual opcao ele deseja executar
     * @return int opcao lida
     */
    public static int menu() {
        System.out.println("\nMenu principal - TP02");
        System.out.println("Escolha uma das opcoes:");
        System.out.println("0 - Carga do arquivo");
        System.out.println("1 - Create");
        System.out.println("2 - Read");
        System.out.println("3 - Update");
        System.out.println("4 - Delete");
        System.out.println("5 - Indices");
        System.out.println("6 - Fechar programa");
        System.out.println("7 - Deletar arquivos");
        
        int opc = -1;
        boolean invalido = false;
     
        try{
            do{
                System.out.print("-> ");
                opc = Integer.parseInt(br.readLine());
                invalido = (opc < 0) || (opc > 7);
                if(invalido) System.out.println("Opcao invalida! Digite novamente");
            } while(invalido);
        } catch(IOException ioe){
            System.err.println("Erro ao ler opcao do menu");
            ioe.printStackTrace();
        }

        return opc;
    }
    /**
     * Mostra submenu na tela e solicita ao usuario qual opcao ele deseja executar (Indices)
     * @return int oocao lida
     */
    public static int subMenu() {
        System.out.println("\nMenu secundario - Indexacao");
        System.out.println("Escolha uma das opcoes:");
        System.out.println("0 - Voltar ao menu principal");
        System.out.println("1 - Mostrar Arvore B");
        System.out.println("2 - Mostrar Hashing Estendido");
        System.out.println("3 - Mostrar Lista invertida de nomes");
        System.out.println("4 - Mostrar Lista invertida de artistas");
        System.out.println("5 - Pesquisar usando Listas invertidas");
        
        int opc = -1;
        boolean invalido = false;
     
        try{
            do{
                System.out.print("-> ");
                opc = Integer.parseInt(br.readLine());
                invalido = (opc < 0) || (opc > 6);
                if(invalido) System.out.println("Opcao invalida! Digite novamente");
            } while(invalido);
        } catch(IOException ioe){
            System.err.println("Erro ao ler opcao do submenu");
            ioe.printStackTrace();
        }

        return opc;
    }
        /* Leituras */
    /**
     * Solicita ao usuario que escolha qual tipo de pesquisa deseja fazer nas listas 
     * invertidas, retornando valor indicando escolha
     * @return int pesquisa escolhida
     */
    public static int lerPesquisa() {
        System.out.println("Pesquisar por:");
        System.out.println("1 - Nomes\n2 - Artistas\n3 - Ambos");
       
        int pesq = 0;
        boolean invalido = false;
     
        try{
            do{
                System.out.print("-> ");
                pesq = Integer.parseInt(br.readLine());
                invalido = (pesq < 1) || (pesq > 3);
                if(invalido) System.out.println("Opcao invalida! Digite novamente");
            } while(invalido);
        } catch(IOException ioe){
            System.err.println("Erro ao ler opcao de pesquisa em listas");
            ioe.printStackTrace();
        }

        return pesq;
    }
    /**
     * Solicita ao usuario que escolha qual indice usar para fazer pesquisa, retornando
     * valor indicando escolha
     * @return int indice escolhido
     */
    public static int lerIndice() {
        System.out.println("Pesquisar usando qual indice?");
        System.out.println("1 - Arvore B\n2 - Hashing Estendido");
        
        int indice = 0;        
        boolean invalido = false;
     
        try{
            do{
                System.out.print("-> ");
                indice = Integer.parseInt(br.readLine());
                invalido = (indice != 1) && (indice != 2);
                if(invalido) System.out.println("Opcao invalida! Digite novamente");
            } while(invalido);
        } catch(IOException ioe){
            System.err.println("Erro ao ler opcao de pesquisa em indice");
            ioe.printStackTrace();
        }

        return indice;
    }
    /**
     * Solicita ao usuario que digite os atributos da musica, criando uma instancia
     * e retornando o objeto criado
     * @return objeto da musica lida
     */
    public static Musica lerMusica() {   
        int duration_ms = -1;
        String track_id = "", name = "";
        Date release_date = new Date();
        ArrayList<String> artists = new ArrayList<String>();

        try{
            // Lê duration_ms
            System.out.print("Duration_ms [inteiro]: ");
            duration_ms = Integer.parseInt(br.readLine());

            // Lê release_date
            System.out.print("Release_date [yyyy-MM-dd]: ");
            String stringDate = br.readLine();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");      
            try{
                release_date = sdf.parse(stringDate);
            } catch(ParseException pe){
                System.err.println("Erro ao fazer parse da data");
                pe.printStackTrace();
            }

            // Lê track_id
            System.out.print("Track_id [22 caracteres]: ");
            track_id = br.readLine();

            // Lê name
            System.out.print("Name: ");
            name = br.readLine();

            // Lê artists
            System.out.println("Artists [FIM quando terminar]:");
            String line = br.readLine();
            while( !(line.equals("FIM")) ){
                artists.add(line);
                line = br.readLine();
            }
        } catch(IOException ioe){
            System.err.println("Erro ao ler atributo da musica");
            ioe.printStackTrace();
        }
        
        Musica msc = new Musica(-1, duration_ms, release_date, track_id, name, artists);

        return msc;
    }
    /**
     * Le o atributo que o usuario deseja alterar da musica e retorna um objeto novo
     * com a alteracao do atributo feita 
     * @param atual objeto Musica atual
     * @return nova objeto Musica alterado
     */
    public static Musica lerAtualizacao(Musica atual) {
        System.out.println("\nQual atributo deseja alterar?");
        System.out.println("0 - Duration_ms");
        System.out.println("1 - Release_date");
        System.out.println("2 - Track_id");
        System.out.println("3 - Name");
        System.out.println("4 - Artists");
        
        int opc = -1;
        boolean invalido =false;
        Musica nova = atual.clone();
        
        try{
            do{
                System.out.print("-> ");
                opc = Integer.parseInt(br.readLine());
                invalido = (opc < 0) || (opc > 4);
                if(invalido) System.out.println("Opcao invalida, digite novamente");
            } while(invalido);

            switch(opc){
                case 0: {
                    System.out.print("\nDuration_ms [inteiro]: ");
                    nova.setDuration_ms(Integer.parseInt(br.readLine()));    

                    break;
                } case 1: {
                    System.out.print("\nRelease_date [yyyy-MM-dd]: ");
                    String stringDate = br.readLine();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");      
                    try{
                        nova.setRelease_date(sdf.parse(stringDate));
                    } catch(ParseException pe){
                        System.err.println("Erro ao fazer parse da data");
                        pe.printStackTrace();
                    }

                    break;
                } case 2: {
                    System.out.print("\nTrack_id [22 caracteres]: ");
                    nova.setTrack_id(br.readLine());
                    
                    break;
                } case 3: {
                    System.out.print("\nName: ");
                    nova.setName(br.readLine());
                    
                    break;
                } case 4: {
                    System.out.println("\nArtists [FIM quando terminar]:");
                    String line = br.readLine();
                    ArrayList<String> artists = new ArrayList<String>();
                    while( !(line.equals("FIM")) ){
                        artists.add(line);
                        line = br.readLine();
                    }
                    nova.setArtists(artists);
                    
                    break;
                }
            }

        } catch(IOException ioe){
            System.err.println("Erro de leitura na atualizacao da musica");
            ioe.printStackTrace();
        }

        return nova;
    }
}

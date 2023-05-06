
/** Pacotes **/
package TP03;
import TP03.Classes.Musica;
import TP03.Classes.CRUD;
import TP03.Classes.Compress.Huffman;
import TP03.Classes.Compress.LZW;

import java.util.ArrayList;
import java.util.Date;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/* Classe TP03 (Main) */
public class TP03 {
    /* Atributos */
    public static BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

    /* Métodos */
    /* Main */
    /**
     * Fornece um menu com opcoes para carregar o arquivo da base de dados (CSV),
     * realizar operações do CRUD e fazer ordenações por intercalação balanceada
     */
    public static void main(String[] args) {
        try {
            // arquivo CSV (base de dados)
            String basePath = "TP03/Data/spotify.csv";
            BufferedReader fr = new BufferedReader(new FileReader(basePath));

            // arquivo RAF (registros em bytes)
            CRUD arquivo = new CRUD("TP03/Data/arquivo.db");
            String dbFile = "TP03/Data/arquivo.db";

            int opc = -1; // opcao do menu

            boolean comprime = false;
            int vercoes = 0;

            do {
                opc = menu();

                // executa tarefas do menu de CRUD
                switch (opc) {
                    case 0: { // Carga do CSV
                        System.out.println("\n**Fazendo carga inicial**");

                        String line; // linha do CSV
                        // lê 10000 musicas (linhas) do CSV, faz parse e cria registros
                        for (int i = 0; i < 1000; i++) {
                            line = fr.readLine();
                            Musica musica = new Musica();
                            musica.parseCSV(line);
                            arquivo.create(musica);
                        }
                        System.out.println("Base de dados carregada. 1000 registros criados.");

                        break;
                    }
                    case 1: { // Create
                        System.out.println("\n**Criando musica**");

                        Musica msc = lerMusica();
                        arquivo.create(msc);

                        System.out.println("\n" + msc);

                        break;
                    }
                    case 2: { // Read
                        System.out.println("\n**Lendo musica**");
                        System.out.print("ID da musica a ser lida: ");

                        int readID = Integer.parseInt(br.readLine());
                        Musica msc = arquivo.read(readID);
                        if (msc != null)
                            System.out.println("\n" + msc);

                        break;
                    }
                    case 3: { // Update
                        System.out.println("\n**Atualizando musica**");
                        System.out.print("ID da musica que deve ser alterada: ");

                        int updateID = Integer.parseInt(br.readLine());
                        Musica msc = arquivo.read(updateID);
                        if (msc != null) {
                            System.out.println("\n" + msc);
                            Musica nova = lerAtualizacao(msc);

                            if (arquivo.update(nova))
                                System.out.println("Musica atualizada com sucesso");
                            else
                                System.out.println("Erro ao atualizar musica");
                        } else {
                            System.out.println("Musica a ser atualizada nao encontrada");
                        }

                        break;
                    }
                    case 4: { // Delete
                        System.out.println("\n**Deletando musica**");
                        System.out.print("ID da musica que deve ser deletada: ");

                        int deleteID = Integer.parseInt(br.readLine());

                        if (arquivo.delete(deleteID))
                            System.out.println("Musica removida com sucesso");
                        else
                            System.out.println("Erro ao remover musica");

                        break;
                    }
                    case 5: { // Compressao (submenu)
                        int opc2 = -1;

                        do {
                            opc2 = subMenu();

                            switch (opc2) {
                         

                                case 0: { // Sai do menu interno
                                    System.out.println("\n**Retornando ao menu anterior...**");
                                    break;
                                }
                                case 1: {
                                    System.out.println("\n**Comprimir X numeros de arquivos**");

                                    System.out.print("\nDigite o numero de vercoes: ");
                                    vercoes = Integer.parseInt(br.readLine());

                                    System.out.println("\n**Comprimindo Huffman**");
                                    Huffman huffman = new Huffman();
                                    long timeHuffman = huffman.EncodeFinal(dbFile, vercoes);
                                    System.out.println("tempo de execucao: " + timeHuffman + "ms");

                                    System.out.println("\n**Comprimindo LZW**");
                                    LZW lzw = new LZW();
                                    long timeLZW = lzw.EncodeFinal(dbFile, vercoes);
                                    System.out.println("tempo de execucao: " + timeLZW + "ms");

                                    System.out.print("\n**Arquivos comprimidos com sucesso**");
                                    if(timeHuffman > timeLZW){
                                        System.out.println("\n**LZW foi mais rapido**");
                                    }else if (timeLZW > timeHuffman){
                                        System.out.println("\n**Huffman foi mais rapido**");
                                    }else{
                                        System.out.println("\n**Os dois foram iguais**");
                                    }
                                    comprime = true;
                                    break;
                                    
                                }
                                case 2:{
                                    if(comprime){
                                        System.out.println("\n**Descomprimir X numeros de arquivos**");

                                        System.out.println("\nDescomprimindo Huffman");
                                        Huffman huffman = new Huffman();
                                        long timeHuffman =  huffman.DecodeFinal(dbFile, vercoes);
                                        System.out.println("tempo de execucao: " + timeHuffman + "ms");

                                        System.out.println("\nDescomprimindo LZW");
                                        LZW lzw = new LZW();
                                        long timeLZW = lzw.DecodeFinal(dbFile, vercoes);
                                        System.out.println("tempo de execucao: " + timeLZW + "ms");

                                        System.out.println("\n**Arquivos descomprimidos com sucesso**");
                                        if(timeHuffman > timeLZW){
                                            System.out.println("**LZW foi mais rapido**");
                                        }else if (timeLZW > timeHuffman){
                                            System.out.println("**Huffman foi mais rapido**");
                                        }else{
                                            System.out.println("\n**Os dois foram iguais**");
                                        }
                                    }else{
                                        System.out.println("\n**Comprima primeiro**");
                                    }
                                    break;
                                }
                                case 3: {
                                    System.out.println("**Delatando arquivos**");

                                    Huffman huffman = new Huffman();
                                    huffman.DeleteAllFiles(dbFile, vercoes);

                                    LZW lzw = new LZW();
                                    lzw.DeleteAllFiles(dbFile, vercoes);

                                    System.out.println("**Arquivos deletados com sucesso**");
                                    comprime = false;
                                    break;
                                }
                            }
                        } while (opc2 != 0);

                        break;
                    }
                    case 6: { // Fecha arquivo e encerra programa
                        System.out.println("\n**Encerrando programa**");
                        arquivo.close();
                        break;
                    }
                    case 7: { // Deleta arquivo
                        System.out.println("\n**Deletando arquivo de registros**");

                        if (arquivo.deleteFile())
                            System.out.println("Arquivo deletado com sucesso");
                        else
                            System.out.println("Erro ao deletar arquivo");

                        break;
                    }
                }
            } while (opc != 6);

            fr.close();
            br.close();
        } catch (FileNotFoundException fnfe) {
            System.err.println("Arquivo CSV da base de dados nao encontrado");
            fnfe.printStackTrace();
        } catch (IOException ioe) {
            System.err.println("Erro de leitura na funcao principal");
            ioe.printStackTrace();
        }
    }

    /* Menus */
    /**
     * Mostra menu principal e solicita ao usuário qual opção ele deseja executar
     * 
     * @return int opção lida
     */
    public static int menu() {
        System.out.println("\nMenu principal - TP03");
        System.out.println("Escolha uma das opcoes:");
        System.out.println("0 - Carga do arquivo");
        System.out.println("1 - Create");
        System.out.println("2 - Read");
        System.out.println("3 - Update");
        System.out.println("4 - Delete");
        System.out.println("5 - Compressao");
        System.out.println("6 - Fechar programa");
        System.out.println("7 - Deletar arquivo");

        int opc = -1;
        boolean invalido = false;

        try {
            do {
                System.out.print("-> ");
                opc = Integer.parseInt(br.readLine());
                invalido = (opc < 0) || (opc > 7);
                if (invalido)
                    System.out.println("Opcao invalida! Digite novamente");
            } while (invalido);
        } catch (IOException ioe) {
            System.err.println("Erro ao ler opcao do menu");
            ioe.printStackTrace();
        }

        return opc;
    }

    /**
     * Mostra submenu na tela e solicita ao usuário qual opção ele deseja executar
     * (Ordenacao)
     * 
     * @return int opção lida
     */
    public static int subMenu() {
        System.out.println("\nMenu secundario - Compressao");
        System.out.println("Escolha uma das opcoes:");
        System.out.println("0 - Voltar ao menu principal");
        System.out.println("1 - Comprimir X arquivos");
        System.out.println("2 - Descomprimir X arquivos");
        System.out.println("3 - Deletar arquivos");

        int opc = -1;
        boolean invalido = false;

        try {
            do {
                System.out.print("-> ");
                opc = Integer.parseInt(br.readLine());
                invalido = (opc < 0) || (opc > 3);
                if (invalido)
                    System.out.println("Opcao invalida! Digite novamente");
            } while (invalido);
        } catch (IOException ioe) {
            System.err.println("Erro ao ler opcao do submenu");
            ioe.printStackTrace();
        }

        return opc;
    }

    /* Leituras */
    /**
     * Solicita ao usuário que digite os atributos da musica, criando uma instância
     * e retornando o objeto criado
     * 
     * @return objeto da musica lida
     */
    public static Musica lerMusica() {
        int duration_ms = -1;
        String track_id = "", name = "";
        Date release_date = new Date();
        ArrayList<String> artists = new ArrayList<String>();

        try {
            // Lê duration_ms
            System.out.print("Duration_ms [inteiro]: ");
            duration_ms = Integer.parseInt(br.readLine());

            // Lê release_date
            System.out.print("Release_date [yyyy-MM-dd]: ");
            String stringDate = br.readLine();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            try {
                release_date = sdf.parse(stringDate);
            } catch (ParseException pe) {
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
            while (!(line.equals("FIM"))) {
                artists.add(line);
                line = br.readLine();
            }
        } catch (IOException ioe) {
            System.err.println("Erro ao ler atributo da musica");
            ioe.printStackTrace();
        }

        Musica msc = new Musica(-1, duration_ms, release_date, track_id, name, artists);

        return msc;
    }

    /**
     * Lê o atributo que o usuário deseja alterar da música e retorna um objeto novo
     * com a alteração do atributo feita
     * 
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
        boolean invalido = false;
        Musica nova = atual.clone();

        try {
            do {
                System.out.print("-> ");
                opc = Integer.parseInt(br.readLine());
                invalido = (opc < 0) || (opc > 4);
                if (invalido)
                    System.out.println("Opcao invalida, digite novamente");
            } while (invalido);

            switch (opc) {
                case 0: {
                    System.out.print("\nDuration_ms [inteiro]: ");
                    nova.setDuration_ms(Integer.parseInt(br.readLine()));

                    break;
                }
                case 1: {
                    System.out.print("\nRelease_date [yyyy-MM-dd]: ");
                    String stringDate = br.readLine();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    try {
                        nova.setRelease_date(sdf.parse(stringDate));
                    } catch (ParseException pe) {
                        System.err.println("Erro ao fazer parse da data");
                        pe.printStackTrace();
                    }

                    break;
                }
                case 2: {
                    System.out.print("\nTrack_id [22 caracteres]: ");
                    nova.setTrack_id(br.readLine());

                    break;
                }
                case 3: {
                    System.out.print("\nName: ");
                    nova.setName(br.readLine());

                    break;
                }
                case 4: {
                    System.out.println("\nArtists [FIM quando terminar]:");
                    String line = br.readLine();
                    ArrayList<String> artists = new ArrayList<String>();
                    while (!(line.equals("FIM"))) {
                        artists.add(line);
                        line = br.readLine();
                    }
                    nova.setArtists(artists);

                    break;
                }
            }

        } catch (IOException ioe) {
            System.err.println("Erro de leitura na atualizacao da musica");
            ioe.printStackTrace();
        }

        return nova;
    }
}

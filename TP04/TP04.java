/** Pacotes **/
package TP04;
import TP04.Classes.Musica;
import TP04.Classes.CRUD;
import TP04.Classes.Busca.KMP;
import TP04.Classes.Busca.BoyerMoore;
import TP04.Classes.Busca.ShiftAnd;
import TP04.Classes.Busca.RabinKarp;

import java.util.ArrayList;
import java.util.Date;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/* Classe TP04 (Main) */
public class TP04 {
    /* Atributos */
    public static BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

    /* Métodos */
        /* Main */
    /**
     * Fornece um menu com opcoes para carregar o arquivo da base de dados (CSV),
     * realizar operações do CRUD e fazer busca de casamento de padroes
     */
    public static void main(String[] args) {
        try {
            // arquivo CSV (base de dados)
            String basePath = "TP04/Data/spotify.csv";
            BufferedReader fr = new BufferedReader(new FileReader(basePath));

            // arquivo RAF (registros em bytes)
            CRUD arquivo = new CRUD();
          
            int tamBase = 1000; // tamanho da base csv
            int opc = -1; // opcao do menu

            do {
                opc = menu();

                // executa tarefas do menu de CRUD
                switch (opc) {
                    case 0: { // Carga do CSV
                        System.out.println("\n**Fazendo carga inicial**");

                        String line; // linha do CSV
                        // lê tamBase musicas (linhas) do CSV, faz parse e cria registros
                        for (int i = 0; i < tamBase; i++) {
                            line = fr.readLine();
                            Musica musica = new Musica();
                            musica.parseCSV(line);
                            arquivo.create(musica);
                        }
                        System.out.println("Base de dados carregada. "+tamBase+" registros criados.");

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
                        else
                            System.out.println("Musica buscada nao encontrada.");

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
                    case 5: { // Casamento de padroes
                        System.out.println("\n**Casamento de padroes**");
                        arquivo.toText();

                        // ler padrao (string)
                        System.out.print("Padrao a ser buscado: ");
                        String padrao = br.readLine();

                        // converte arquivo de bytes para texto
                        

                        // executar tds os algoritmos e imprimir comparacoes/tempo
                        System.out.println("\n******************************** KMP ********************************");
                        KMP.searchPattern(arquivo.getPathTxt(), padrao);
                        System.out.println("\n***************************** Shift-And *****************************");
                        ShiftAnd.searchPattern(arquivo.getPathTxt(), padrao);
                        System.out.println("\n**************************** Boyer-Moore ***************************");
                        BoyerMoore.searchPattern(arquivo.getPathTxt(), padrao);
                        System.out.println("\n**************************** Rabin-Karp ***************************");
                        RabinKarp.searchPattern(arquivo.getPathTxt(), padrao);

                        System.out.print("\nDeseja excluir arquivo texto [S|N]? ");
                        String excluir = br.readLine();
                        if( Character.toUpperCase(excluir.charAt(0)) == 'S' ){
                            // deleta arquivo txt
                            arquivo.deleteTxt();
                        } 

                        break;
                    }
                    case 6: { // Fecha arquivo e encerra programa
                        System.out.println("\n**Encerrando programa**");
                        if(arquivo.exists()) arquivo.close();
                        break;
                    }
                    case 7: { // Deleta arquivo
                        System.out.println("\n**Deletando arquivo de registros**");

                        if(arquivo.exists() && arquivo.deleteFile())
                            System.out.println("Arquivo de dados deletado com sucesso.");
                        else
                            System.out.println("Erro ao deletar arquivo de dados.");

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
     * @return int opção lida
     */
    public static int menu() {
        System.out.println("\nMenu principal - TP04");
        System.out.println("Escolha uma das opcoes:");
        System.out.println("0 - Carga do arquivo");
        System.out.println("1 - Create");
        System.out.println("2 - Read");
        System.out.println("3 - Update");
        System.out.println("4 - Delete");
        System.out.println("5 - Casamento de padroes");
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
        /* Leituras */
    /**
     * Solicita ao usuário que digite os atributos da musica, criando uma instância
     * e retornando o objeto criado
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

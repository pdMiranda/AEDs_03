package TP04.Classes.Busca;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RabinKarp {

    private static int comp = 0;
    
    /**
     * @param path  String -- caminho do arquivo
     * @param pattern  String -- padrão a ser buscado
     */
    public static void searchPattern(String path, String pattern) {
        int count = 0;  // contador de ocorrências do padrão na linha
        long start = 0; // tempo de inicio
        long end = 0;  // tempo de fim

        try {
            BufferedReader br = new  BufferedReader(new FileReader(path));

            String line;
            int lineNumber = 1;

            start = System.currentTimeMillis();

            while((line = br.readLine()) != null) {  // le o arquivo linha por linha
                List<Integer> indices = search(line, pattern, pattern.hashCode());  // busca o padrão na linha
                if(!indices.isEmpty()) {  // se a lista de índices não estiver vazia, o padrão foi encontrado
                    System.out.println("Padrao \"" + pattern + "\" achado na linha " + lineNumber + ", posicao(es): " + indices);
                    count += indices.size();
                      
                }
                lineNumber++;
            }
            
            end = System.currentTimeMillis();

            br.close();
        } catch (FileNotFoundException fnfe) {
            System.err.println("Arquivo txt para casamento de padroes nao encontrado");
            fnfe.printStackTrace();
        } catch (IOException ioe) {
            System.err.println("Erro de leitura/escrita no casamento de padroes (RabinKarp)");
            ioe.printStackTrace();
        }


        if(count > 0){
            System.out.println("Padrao \"" + pattern + "\" encontrado " + count + " vezes");
        } else {
            System.out.println("Padrao \"" + pattern + "\" nao encontrado");
        }

        System.out.println("Comparacoes: " + comp);
        System.out.println("Tempo de execucao: " + (end-start) + " ms");

    }

    /**
     * @param line  String -- linha para ser buscada
     * @param pattern  String -- padrão a ser buscado
     * @param patternHash  int -- hash do padrão
     * @return List<Integer> -- lista de índices das ocorrências do padrão na linha
     */
    public static List<Integer> search(String line, String pattern , int patternHash){
        List<Integer> indices = new ArrayList<>();  // lista de índices das ocorrências do padrão na linha
        int patternLength = pattern.length();  // tamanho do padrão
        int textLength = line.length();  // tamanho do texto 
        for (int i = 0; i <= textLength - patternLength; i++) {  // percorre o texto
            String substring = line.substring(i, i + patternLength);  // extrai a substring de tamanho patternLength
            comp++;
            if (patternHash == substring.hashCode() && pattern.equals(substring)) {  // se o hash da substring for igual ao hash do padrão e a substring for igual ao padrão
                indices.add(i + 1);  // adiciona o índice da ocorrência na lista
            }
        }
        return indices;
    }
}


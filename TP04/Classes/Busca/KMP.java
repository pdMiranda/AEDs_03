package TP04.Classes.Busca;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class KMP {   
    private static int comp = 0; // contador de comparações
    
    /**
     * Funcao principal
     * @param path String -- caminho do arquivo
     * @param pattern String -- padrão a ser buscado
     */
    public static void searchPattern(String path, String pattern) {
        int count = 0; // contar quantas vezes o padrão foi encontrado
        long start = 0, end = 0; // tempo de inicio e fim

        try{
            // ler o arquivo
            BufferedReader reader = new BufferedReader(new FileReader(path));
            
            String line;
            int lineNumber = 1;
            int aux = 0;

            start = System.currentTimeMillis(); // marca inicio
            int[] prefix = prefix(pattern);  // tabela de prefixos
            
            while ((line = reader.readLine()) != null) { // para cada linha do arquivo
                // busca o padrão na linha
                List<Integer> indexes = new ArrayList<Integer>();  // lista de índices do padrão na linha
                int index = search(line, pattern, prefix);  // índice do padrão na linha
                aux = index;
                while (index >= 0) {  // enquanto o índice do padrão for maior ou igual a 0  
                    count++;  // incrementa o contador
                    indexes.add(aux + 1);  // adiciona o índice do padrão na lista de índices
                    line = line.substring(index + 1);  // linha a partir da posição index + 1
                    index = search(line, pattern, prefix);  // índice do padrão na linha a partir da posição index + 1
                    aux += index + 1;
                }

                if(indexes.size() > 0){
                    System.out.println("Padrao \"" + pattern + "\" achado na linha " + lineNumber + ", posicao(es) " + indexes + ".");
                }

                lineNumber++;
            }
            
            end = System.currentTimeMillis(); // marca fim

            reader.close();
        } catch(FileNotFoundException fnfe){
            System.err.println("Arquivo txt para casamento de padroes nao encontrado");
            fnfe.printStackTrace();
        } catch(IOException ioe){
            System.err.println("Erro de leitura/escrita no casamento de padroes (KMP)");
            ioe.printStackTrace();
        }

        if(count > 0){ // padrao encontrado
            System.out.println("Encontrados " + count + " padroes \"" + pattern + "\".");
        } else{ // padrao nao encontrado
            System.out.println("Padrao nao encontrado.");
        }

        // imprime qtd de comparacoes e tempo de execucao
        System.out.println("Comparacoes: " + comp);
        System.out.println("Tempo de execucao: " + (end-start) + " ms");
    }
    /**
     * @param pattern String -- padrão a ser buscado
     * @return tabela de prefixos
     */
    private static int[] prefix(String pattern) { 
        int m = pattern.length();
        int[] prefix = new int[m]; //Criando o vetor de prefixos 
        prefix[0] = -1;  // o primeiro elemento da tabela de prefixos é -1
        int k = -1;  // k é o índice do padrão
        for (int i = 1; i < m; i++) {
            while (k >= 0 && pattern.charAt(k + 1) != pattern.charAt(i)) {  // enquanto o índice do padrão for maior ou igual a 0 e o caractere do padrão na posição k + 1 for diferente do caractere do padrão na posição i
                k = prefix[k];
            }
            if (pattern.charAt(k + 1) == pattern.charAt(i)) {  // se o caractere do padrão na posição k + 1 for igual ao caractere do padrão na posição i
                k++;
            }
            prefix[i] = k; // o elemento da tabela de prefixos na posição i é igual a k
        }
        return prefix;
    }   
    /**
     * @param text String -- texto para ser buscado
     * @param pattern String -- padrão a ser buscado
     * @return  índice da primeira ocorrência do padrão no texto, ou -1 se não for encontrado
     */ 
    private static int search(String text, String pattern, int[] prefix) {
        int n = text.length();
        int m = pattern.length();

        // procura pelo padrão no texto
        int i = 0;  // índice do texto
        int j = -1;  // índice do padrão
        while (i < n && j < m - 1) {  // enquanto o índice do texto for menor que o tamanho do texto e o índice do padrão for menor que o tamanho do padrão - 1
            comp++;
            if (text.charAt(i) == pattern.charAt(j + 1)) {  // se o caractere do texto na posição i for igual ao caractere do padrão na posição j + 1
                i++;  
                j++;
            } else if (j >= 0) {  // se o índice do padrão for maior ou igual a 0 o índice do padrão é igual ao elemento da tabela de prefixos na posição j
                j = prefix[j];  
            } else {  // se o índice do padrão for menor que 0
                i++;  
            }
        }

        if (j == m - 1) {  // se o índice do padrão for igual ao tamanho do padrão - 1
            return i - m;  // retorna o índice do texto menos o tamanho do padrão
        } else {  // se o índice do padrão for diferente do tamanho do padrão - 1
            return -1;
        }
    }
}

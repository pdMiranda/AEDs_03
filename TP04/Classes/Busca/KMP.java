package TP04.Classes.Busca;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class KMP {    

    /**
     * @param text String -- texto para ser buscado
     * @param pattern String -- padrão a ser buscado
     * @return  índice da primeira ocorrência do padrão no texto, ou -1 se não for encontrado
     */
    public static int search(String text, String pattern) {
        int n = text.length();
        int m = pattern.length();

        // cria a tabela de prefixos
        int[] prefix = new int[m];
        prefix[0] = -1;  // o primeiro elemento da tabela de prefixos é -1
        int k = -1;  // k é o índice do prefixo
        for (int i = 1; i < m; i++) {  // para cada elemento da tabela de prefixos
            while (k >= 0 && pattern.charAt(k + 1) != pattern.charAt(i)) {  // enquanto k for maior ou igual a 0 e o caractere do padrão na posição k + 1 for diferente do caractere do padrão na posição i
                k = prefix[k];
            }
            if (pattern.charAt(k + 1) == pattern.charAt(i)) {  // se o caractere do padrão na posição k + 1 for igual ao caractere do padrão na posição i
                k++;
            }
            prefix[i] = k;  // o elemento da tabela de prefixos na posição i é igual a k
        }

        // procura pelo padrão no texto
        int i = 0;  // índice do texto
        int j = -1;  // índice do padrão
        while (i < n && j < m - 1) {  // enquanto o índice do texto for menor que o tamanho do texto e o índice do padrão for menor que o tamanho do padrão - 1
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
    
    /**
     * @param path String -- caminho do arquivo
     * @param pattern  String -- padrão a ser buscado
     * @throws IOException -- exceção de entrada e saída
     */
    public void BuscaAll(String path, String pattern) throws IOException {
        int count = 0; // contar quantas vezes o padrão foi encontrado
        // ler o arquivo
        BufferedReader reader = new BufferedReader(new FileReader(path));
        String line;
        int lineNumber = 1;
        while ((line = reader.readLine()) != null) { // para cada linha do arquivo
            // search for the pattern in each line
            int index = search(line, pattern);  // índice do padrão na linha
            while (index >= 0) {  // enquanto o índice do padrão for maior ou igual a 0  
                count++;  // incrementa o contador
                System.out.println("Padrao \"" + pattern + "\" achado na linha " + lineNumber + ", posicao " + index + ".");
                index = search(line.substring(index + 1), pattern);  // índice do padrão na linha a partir da posição index + 1
            }
            lineNumber++;
        }
        reader.close();

        if (count > 0) { // padrao encontrado
            System.out.println("Achados " + count + " padroes \"" + pattern + "\".");
        } else { // padrao nao encontrado
            System.out.println("Padrao nao encontrado.");
        }
    }
}

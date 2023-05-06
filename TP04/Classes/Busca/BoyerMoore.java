package TP04.Classes.Busca;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class BoyerMoore {
    
    /**
     * @param text  texto para ser buscado
     * @param pattern  padrão a ser buscado
     * @return  índice da primeira ocorrência do padrão no texto, ou -1 se não for encontrado
     */
    public static int search(String text, String pattern) {
        int n = text.length();  // tamanho do texto
        int m = pattern.length();  // tamanho do padrão

        //cria o a tabela de caracteres ruins
        int[] badChar = new int[256];
        for (int i = 0; i < 256; i++) {
            badChar[i] = m;
        }
        for (int i = 0; i < m - 1; i++) {
            badChar[pattern.charAt(i)] = m - i - 1;
        }

        // procura pelo padrão no texto
        int i = m - 1;  // índice do texto
        int j = m - 1;  // índice do padrão
        while (i < n && j >= 0) {  // enquanto o índice do texto for menor que o tamanho do texto e o índice do padrão for maior ou igual a 0
            if (text.charAt(i) == pattern.charAt(j)) {  // se os caracteres forem iguais decrementa os índices
                i--;  
                j--;  
            } else {  // se os caracteres forem diferentes, o índice do texto é incrementado de acordo com a tabela de caracteres ruins
                i += badChar[text.charAt(i)];
                j = m - 1;
            }
        }

        if (j < 0) {  // se o índice do padrão for menor que 0, o padrão foi encontrado
            return i + 1; 
        } else {  // se não, o padrão não foi encontrado
            return -1;
        }
    }

    /**
     * @param path String -- caminho do arquivo de texto
     * @param pattern  String -- padrão a ser buscado
     * @throws IOException -- exceção de entrada e saída
     */
    public static void searchAll(String path, String pattern) throws IOException{
        int count = 0; // contar o numero de ocorrencias

        // le o arquivo de texto
        BufferedReader br = new BufferedReader(new FileReader(path));
        String line;
        int lineNumber = 1;
        while ((line = br.readLine()) != null) {
            // procura pelo padrao em cada linha
            int index = search(line, pattern);  // procura pelo padrão na linha
            while (index >= 0) {  // enquanto o índice for maior ou igual a 0
                count++;
                System.out.println("Padrao \"" + pattern + "\" achado na linha " + lineNumber + ", posicao " + index + ".");
                index = search(line.substring(index + 1), pattern);  // procura pelo padrão na linha a partir do índice + 1
            }
            lineNumber++;
        }
        br.close();

        if(count > 0){
            System.out.println("Encontrado " + count + " correspondências do padrão \"" + pattern + "\".");
        }
        else{
            System.out.println("Padrão não encontrado.");
        }
    }
}

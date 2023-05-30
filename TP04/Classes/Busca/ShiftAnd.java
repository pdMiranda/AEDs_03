package TP04.Classes.Busca;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ShiftAnd {
    /**
     * @param line String -- linha para ser buscada
     * @param pattern String -- padrão a ser buscado
     * @return lista de índices das ocorrências do padrão na linha
     */
    public static List<Integer> search(String line, String pattern) {
        List<Integer> indices = new ArrayList<>();  // lista de índices das ocorrências do padrão na linha
        int patternLength = pattern.length();  // tamanho do padrão
        int[] masks = new int[256];  // máscaras de bits
        int state = 0;  // estado

        //Processando o padrão
        for (int i = 0; i < patternLength; i++) {
            masks[pattern.charAt(i)] |= (1 << i);  // seta o bit i da máscara do caractere
        }

        // Etapa de procura scaneia a linha e da udate do satedo baseado na mascara de bits
        for (int i = 0; i < line.length(); i++) {
            state = ((state << 1) | 1) & masks[line.charAt(i)]; // shift left 1 bit, add 1 para a direita, e mask
            if ((state & (1 << (patternLength - 1))) != 0) {  // se o bit mais a esquerda for 1, o padrão foi encontrado
                indices.add(i - patternLength + 1);
            }
        }

        return indices;
    }

    /**
     * @param path String -- caminho do arquivo
     * @param pattern String -- padrão a ser buscado
     * @throws IOException -- exceção de entrada e saída
     */
    public static void searchAll(String path, String pattern) throws IOException {
        int count = 0;

        BufferedReader br = new BufferedReader(new FileReader(path));
        String line;
        int lineNumber = 1;
        while ((line = br.readLine()) != null) {  // le o arquivo linha por linha
            List<Integer> indices = search(line, pattern);  // busca o padrão na linha
            if (!indices.isEmpty()) {
                for(int i : indices){  // imprime os índices das ocorrências do padrão na linha
                    System.out.println("Padrao \"" +pattern +"\" achado na linha " + lineNumber + ", posicao: " + i);
                }
                count++;
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


package TP04.Classes.Busca;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class ShiftAnd {
    /**
     * Funcao principal
     * @param path String -- caminho do arquivo
     * @param pattern String -- padrão a ser buscado
     */
    public static void searchPattern(String path, String pattern) {
        int count = 0; // contar quantas vezes o padrão foi encontrado
        
        try{
            // ler o arquivo
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
        } catch(FileNotFoundException fnfe){
            System.err.println("Arquivo txt para casamento de padroes nao encontrado");
            fnfe.printStackTrace();
        } catch(IOException ioe){
            System.err.println("Erro de leitura/escrita no casamento de padroes (ShiftAnd)");
            ioe.printStackTrace();
        }

        if(count > 0){ // padrao encontrado
            System.out.println("Encontrados " + count + " padroes \"" + pattern + "\".");
        } else{ // padrao nao encontrado
            System.out.println("Padrao nao encontrado.");
        }     
    }
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
}


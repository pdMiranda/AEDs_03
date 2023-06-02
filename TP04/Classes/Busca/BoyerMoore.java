package TP04.Classes.Busca;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

public class BoyerMoore {
    /**
     * Funcao principal: chama funcoes para criar vetores de deslocamento por caractere
     * ruim e sufixo bom, percorre arquivo linha por linha fazendo casamento com padrao e
     * imprime total de comparacoes, qtd de ocorrencias encontradas e tempo de execucao
     * @param path String caminho do arquivo texto
     * @param pattern String padrao a ser buscado
     */
    public static void searchPattern(String path, String pattern) {
        int comp = 0, count = 0; // comparacoes e qtd de padroes encontrados
        long start = 0, end = 0; // tempo de inicio e fim
        
        start = System.currentTimeMillis(); // marca inicio
        
        int p = pattern.length(); // tamanho do padrao
        // cria vetores de deslocamento por caractere ruim e sufixo bom
        int badChar[][] = createBadChar(pattern, p);
        int goodSuffix[] = createGoodSuffix(pattern, p); 

        try{
            // abre arquivo txt para leitura
            BufferedReader br = new BufferedReader(new FileReader(path));

            // linha do texto buscado
            String line = ""; 
            int linhaAtual = 1;
            
            // faz casamento de padroes para cada linha do arquivo
            while( (line = br.readLine()) != null ){
                // posicoes da linha onde encontrou padrao
                ArrayList<Integer> posicoes = new ArrayList<Integer>();
                int tam = line.length(); // tamanho da linha
                int shift = 0; // quanto deslocou no texto

                // repete ate padrao nao caber mais 
                // ultimo posicao da linha possivel = (tam-p)+1
                while( shift <= (tam-p) ){
                    int i = p-1; // ultima pos do padrao
                    
                    // busca pos do char do padrao diferente do char do texto
                    while(i >= 0 && pattern.charAt(i) == line.charAt(shift+i)){
                        comp++; // conta comparacoes realizadas 
                        i--; // anda pra esquerda no padrao
                    }

                    if(i < 0){ // achou padrao: foi p/esquerda na ultima comparacao (i=-1)
                        count++; // conta ocorrencia do padrao
                        posicoes.add(shift+1); // salva posicao onde achou padrao na linha

                        // desloca 1 posicao para procurar proxima ocorrencia
                        shift += 1;
                    } else{ // encontrou letra diferente
                        comp++; // conta ultima comparacao realizada

                        // calcula o deslocamento: por caractere ruim ou por sufixo bom (pega maior) 
                        shift += Math.max( goodSuffix[i], calcBadCharShift(i, badChar, (int)line.charAt(shift+i)) );   
                    }
                }

                if(!posicoes.isEmpty()){
                    // imprime posicoes onde encontrou o padrao na linha
                    System.out.println("Padrao \"" + pattern + "\" achado na linha " + linhaAtual + ", posicao(es) " + posicoes + ".");
                }

                linhaAtual++; // add contador da linha
            }

            end = System.currentTimeMillis(); // marca fim

            br.close();
        } catch(FileNotFoundException fnfe){
            System.err.println("Arquivo txt para casamento de padroes nao encontrado");
            fnfe.printStackTrace();
        } catch(IOException ioe){
            System.err.println("Erro de leitura/escrita no casamento de padroes (BoyerMoore)");
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
     * Procura letra do texto (caractere ruim) no vetor de deslocamento. Se letra estiver
     * no padrao, pega valor do index, se nao eh igual a -1. Depois calcula shift e retorna
     * valor positivo de deslocamento
     * @param i int posicao do padrao onde ocorreu falha
     * @param badChar int[][] vetor com letras do padrao (ASCII) e seus indexes
     * @param letter int valor ASCII do char do texto em que houve falha
     * @return
     */
    private static int calcBadCharShift(int i, int[][] badChar, int letter) {
        // faz pesquisa binaria para encontrar char (vetor ordenado)
        boolean achou = false;
        int baixo = (badChar.length-1), cima = 0, meio = 0;

        while(cima <= baixo && !achou){
            meio = (cima + baixo) / 2;

            if(letter == badChar[meio][0]) achou = true;
            else if(letter > badChar[meio][0]) cima = meio + 1;
            else baixo = meio - 1;
        }

        // encontra valor do index da letra
        int index = -1;
        if(achou) index = badChar[meio][1];
        
        // calcula shift
        int shift = i - index;
        if(shift < 0) shift = 1;
        
        return shift;
    }
    /**
     * Preenche vetor de deslocamento por caractere ruim, colocando todas as letras do
     * padrao e suas ocorrencias mais a direita (exceto ultima posicao)
     * @param pattern String padrao buscado
     * @param p int tamanho do padrao
     * @return int[][] badChar
     */
    private static int[][] createBadChar(String pattern, int p) {
        // arrays de letras e indexes do padrao
        ArrayList<Integer> indexes = new ArrayList<Integer>();
        ArrayList<Integer> letters = new ArrayList<Integer>();
        
        // coloca todas as letras diferentes (a partir da penultima posicao)
        // e seus indexes mais a direita, nos arrays
        for(int i = p-2; i >= 0; i--){
            // array ainda nao contem letra do padrao
            if(!letters.contains((int)pattern.charAt(i))){
                letters.add((int)pattern.charAt(i));
                indexes.add(i);
            }
        }
        
        int n = letters.size(); // qtd de letras diferentes
        // matriz de deslocamento (linha = letra | coluna = index)
        int[][] badChar = new int[n][2]; 

        // ordena letras (selection sort) e coloca em ordem no vetor de deslocamento
        for(int i = 0; i < (n-1); i++){
            int menor = i;

            for(int j = (i+1); j < n; j++){ // acha menor
                if(letters.get(j) < letters.get(menor)) menor = j;
            }

            // swap (letra e index)
            int tmpL = letters.get(i);
            int tmpI = indexes.get(i);
            letters.set(i, letters.get(menor));
            indexes.set(i, indexes.get(menor));
            letters.set(menor, tmpL);
            indexes.set(menor, tmpI);

            // salva no vetor
            badChar[i][0] = letters.get(i);
            badChar[i][1] = indexes.get(i);
        }

        // salva ultima letra (maior)
        badChar[n-1][0] = letters.get(n-1);
        badChar[n-1][1] = indexes.get(n-1);

        return badChar;
    }
    /**
     * Preenche vetor de deslocamento por sufixo bom, verificando as tres perguntas para 
     * saber quanto deslocar: se o sufixo eh precedido por uma letra diferente em outra 
     * parte do padrao, se ele eh prefixo ou se parte eh prefixo.
     * @param pattern String padrao buscado
     * @param p int tamanho do padrao
     * @return int[] goodSuffix
     */
    private static int[] createGoodSuffix(String pattern, int p) {
        int goodSuffix[] = new int[p]; // vetor com tamanho do padrao

        goodSuffix[p-1] = 1; // ultima posicao eh sempre = 1
        // percorre o padrao, verificando sufixos que comecam em [i]
        for(int i = p-1; i > 0; i--){
            // salva sufixo e a letra antes dele
            String suffix = pattern.substring(i, p);
            char letraAntes = pattern.charAt(i-1);
            
            int j = i-1,             // anda pra esquerda do sufixo
                t = suffix.length(); // tamanho do sufixo
            boolean found = false; // indica se achou algum dos casos
            while(j > 0 && !found){  
                // faz substring de msm tamanho do sufixo e pega letra antes dela
                String sub = pattern.substring(j, j+t);
                char letra = pattern.charAt(j-1);

                // Caso 1: sufixo aparece precedido por outra letra no padrao
                if(sub.equals(suffix) && letra != letraAntes){
                    goodSuffix[i-1] = i-j; // quanto tem que deslocar p/"encaixar"
                    found = true;
                } else{ // nao entrou no caso 1 ainda: vai para esquerda
                    j--;
                }
            }

            if(!found){ // nao entra no caso 1
                // salva prefixo da palavra (msm tamanho do sufixo)
                String prefix = pattern.substring(0, t);
                
                // Caso 2: sufixo (todo) eh prefixo do padrao
                if(suffix.equals(prefix)){ 
                    goodSuffix[i-1] = i; // deslocamento necessario 
                    found = true;
                } else{ // nao entra no caso 2
                    // diminui o tamanho do prefixo, tentando encontrar parte igual
                    while(t > 1 && !found){
                        t--;
                        prefix = pattern.substring(0, t); // pega parte do prefixo

                        // Caso 3: parte do sufixo eh prefixo
                        if(suffix.contains(prefix)){ 
                            // deslocamento: posicao no sufixo que contem o inicio 
                            // do prefixo + posicao do sufixo no padrao
                            goodSuffix[i-1] = i + suffix.indexOf(prefix.charAt(0));
                            found = true;
                        }
                    }

                    // nao entrou em nenhum caso: pula padrao todo
                    if(!found) goodSuffix[i-1] = p;
                }
            }
        }

        return goodSuffix;
    }
}

package TP05.Classes.Criptografia;
import TP05.Classes.Musica;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;

public class Colunas {
    private static final String path = "TP05/Data/arquivoColunas.db";
    private static final String chave = "FERIAS";

    /**
     * Cria um ArrayList com a ordem das letras da chave
     * @return ArrayList<Integer>
     */
    public static ArrayList<Integer> ordenaChave() {
        int n = chave.length(); // tamanho dos arrays
        // ordem de cada letra da chave
        ArrayList<Integer> chaveOrdenadaPos = new ArrayList<Integer>(n);
        // array para guardar a chave que sera ordenada
        char[] chaveOrdenada = chave.toCharArray();

        // ordena chave (selection sort)
        for(int i = 0; i < (n-1); i++){
            // procura menor            
            int menor = i;
            for(int j = i+1; j < n; j++){
                if(chaveOrdenada[j] < chaveOrdenada[menor]) 
                    menor = j;
            }
            
            // swap
            char temp = chaveOrdenada[i];
            chaveOrdenada[i] = chaveOrdenada[menor];
            chaveOrdenada[menor] = temp;
        }

        // salva posicoes das letras ordenadas
        for(int i = 0; i < n; i++){
            for(int j = 0; j < n; j++){
                if(chave.charAt(i) == chaveOrdenada[j])
                    chaveOrdenadaPos.add(i, j);
            }
        }

        return chaveOrdenadaPos;
    }
    /**
     * Criptografa o campo track_id da musica, setando ele cifrado no final
     * @param musica Musica a ser criptografada
     */
    public static void encrypt(Musica musica) {
        // ordem na qual as colunas da matriz seram lidas
        ArrayList<Integer> chaveOrdenadaPos = ordenaChave();
        
        String palavra = musica.getTrack_id(), // palavra a cifrar
               palavraCifrada = "";            // resultado da cifra
      
        // qtd de linhas e colunas
        int col = chave.length(),
            lin = (int) Math.ceil((double) palavra.length() / col);
        char[][] matriz = new char[lin][col]; // matriz de cifragem

        // coloca letras da palavra na matriz, preenchendo as linhas na ordem
        int n = 0;
        for(int i = 0; i < lin; i++){
            for(int j = 0; j < col; j++){
                if(n < palavra.length()){ // ainda tem letras da palavra
                    matriz[i][j] = palavra.charAt(n);
                } else{ // acabaram as letras, preeenche com espacos a ultima linha
                    matriz[i][j] = ' ';
                }

                n++;
            }
        }
        
        // le colunas da matriz usando a ordem alfabetica da chave
        for(int t = 0; t < chave.length(); t++){
            int j = chaveOrdenadaPos.indexOf(t), // coluna
                i = 0; // linha
            while(i < lin){
                palavraCifrada += matriz[i][j]; // concatena palavra cifrada
                i++;
            }            
        }

        // seta track_id cifrado
        musica.setTrack_id(palavraCifrada);
    }
    /**
     * Descriptografa o campo track_id da musica, setando ele descifrado no final
     * @param musica Musica a ser descriptografada
     */
    public static void decrypt(Musica musica) {
        // ordem na qual as colunas da matriz seram lidas
        ArrayList<Integer> chaveOrdenadaPos = ordenaChave();
        
        String palavraCifrada = musica.getTrack_id(), // palavra a descifrar
        palavra = "";                         // resultado da descifragem
      
        // qtd de linhas e colunas
        int col = chave.length(),
            lin = palavraCifrada.length() / col;
        char[][] matriz = new char[lin][col]; // matriz de cifragem

        // escreve nas colunas da matriz usando a ordem alfabetica da chave
        int n = 0; // contador da palavra cifrada
        for(int t = 0; t < chave.length(); t++){
            int j = chaveOrdenadaPos.indexOf(t), // coluna
                i = 0; // linha
            while(i < lin){
                // coloca char da palavra na matriz
                matriz[i][j] = palavraCifrada.charAt(n);
                i++;
                n++;
            }        
        }
        
        // le letras da matriz, em ordem, resultando na palavra
        for(int i = 0; i < lin; i++){
            for(int j = 0; j < col; j++){
                palavra += matriz[i][j]; // concatena letras na palavra
            }
        }
        
        // seta track_id descifrado
        musica.setTrack_id(palavra);
    }
    /**
     * Cria um registro de uma musica, lendo o último ID registrado para setar o ID atual,
     * atualizando o valor ao final 
     * @param obj Musica a ser registrada no arquivo 
     */
    public static void create(Musica obj) {
        int ultimoID = -1;
        byte[] objectData;
        long pos;

        try{
            RandomAccessFile arq = new RandomAccessFile(path, "rw");

            if(arq.length() == 0){
                arq.seek(0);
                arq.writeInt(0); // ultimoID inicial
            }
            arq.seek(0); // início do arquivo
            
            // lê ID do último registro em arquivo (0 se estiver vazio)
            ultimoID = arq.readInt();
            ultimoID++;
            obj.setID(ultimoID);

            // criptografa campo track_id da Musica
            encrypt(obj);

            // cria registro como array de bytes do objeto
            objectData = obj.toByteArray();
            pos = arq.length();
            arq.seek(pos); // fim do arquivo
            
            arq.writeByte(' '); // lapide
            arq.writeInt(objectData.length); // tamanho do registro (bytes)
            arq.write(objectData);

            arq.seek(0); // início do arquivo
            arq.writeInt(ultimoID);

            arq.close();
        } catch(FileNotFoundException fnfe){
            System.err.println("Arquivo .db de colunas nao encontrado");
            fnfe.printStackTrace();
        } catch(IOException ioe){
            System.err.println("Erro de leitura/escrita ao criar registro no arquivo");
            ioe.printStackTrace();
        }
    }
    /**
     * Percorre o arquivo procurando pelo ID da musica que se quer ler, quando encontra
     * lê o registro em bytes e converte para um objeto Musica, que é retornado
     * @param ID da musica a ser lida
     * @return objeto Musica lido do arquivo
     */
    public static Musica read(int ID) {
        Musica obj = null;
       
        try{
            RandomAccessFile arq = new RandomAccessFile(path, "rw");
            boolean found = false;
            long pos, arqLen = arq.length();
            byte lapide;
            int regSize, regID;

            // posiciona ponteiro no início, pula cabeçalho e salva posição
            arq.seek(0); 
            arq.skipBytes(Integer.BYTES);
            pos = arq.getFilePointer(); 

            while(!found && pos != arqLen){
                // lê primeiros dados
                lapide = arq.readByte();
                regSize = arq.readInt();
                regID = arq.readInt();

                if(regID == ID){ // verifica se registro é o procurado
                    found = true;

                    if(lapide == ' '){ // lapide falsa => registro não excluído
                        // retorna para posição do ID
                        arq.seek(pos + 1 + Integer.BYTES); 

                        // lê registro em bytes e converte para objeto 
                        byte[] data = new byte[regSize];
                        arq.read(data);
                        obj = new Musica();
                        obj.fromByteArray(data);

                        System.out.println("Musica Criptografada: \"" + obj + "\"");
                        decrypt(obj);
                    } else{
                        System.err.println("Registro pesquisado ja foi excluido");
                    }
                } else{ // pula bytes de parte do registro (ID já foi pulado)
                    arq.skipBytes(regSize - Integer.BYTES);
                }
                
                pos = arq.getFilePointer(); // início do próximo registro (lápide)
            }
            
            arq.close();
        } catch(FileNotFoundException fnfe){
            System.err.println("Arquivo .db de colunas nao encontrado");
            fnfe.printStackTrace();
        } catch(IOException ioe){
            System.err.println("Erro de leitura/escrita ao ler registro no arquivo");
            ioe.printStackTrace();
        }

        return obj;
    }
    /**
     * Atualiza um registro de musica, procurando registro antigo e comparando seus 
     * tamanhos. Se o novo registro for menor ou maior que o antigo, deleta o antigo e
     * cria o novo no fim do arquivo, se forem de mesmo tamanho apenas escreve no lugar
     * @param objNovo nova musica a ser registrada
     */
    public static boolean update(Musica objNovo) {
        boolean found = false;

        try{
            RandomAccessFile arq = new RandomAccessFile(path, "rw");
            long pos, arqLen = arq.length();
            byte lapide;
            byte[] objectData;
            int regSize, regSizeNovo, regID;

            // posiciona ponteiro no início, pula cabeçalho e salva posição
            arq.seek(0); 
            arq.skipBytes(Integer.BYTES);
            pos = arq.getFilePointer(); 

            while(!found && pos != arqLen){
                // lê primeiros dados
                lapide = arq.readByte();
                regSize = arq.readInt();
                regID = arq.readInt();

                if(regID == objNovo.getID()){ // verifica se registro é o procurado
                    if(lapide == ' '){ // lapide falsa => registro não excluído
                        found = true;
                   
                        // retorna para posição do ID
                        arq.seek(pos + 1 + Integer.BYTES); 

                        // criptografa objeto novo
                        encrypt(objNovo);

                        // cria registro como array de bytes do objeto novo
                        objectData = objNovo.toByteArray();
                        regSizeNovo = objectData.length;

                        if(regSizeNovo == regSize){ // mesmo tamanho => OK
                            arq.write(objectData);
                        } else{ // maior ou menor => delete + create
                            arq.seek(pos); // retorna para posição da lápide
                            arq.writeByte('*');
                            create(objNovo);
                            System.out.println("Novo ID da musica: "+objNovo.getID());
                        }
                    } else{
                        System.err.println("Registro pesquisado ja foi excluido");
                        pos = arqLen;
                    }
                } else{ // pula bytes de parte do registro (ID já foi pulado)
                    arq.skipBytes(regSize - Integer.BYTES);
                }
                
                pos = arq.getFilePointer(); // início do próximo registro (lápide)
            }

            arq.close();
        } catch(FileNotFoundException fnfe){
            System.err.println("Arquivo .db de colunas nao encontrado");
            fnfe.printStackTrace();
        } catch(IOException ioe){
            System.err.println("Erro de leitura/escrita ao atualizar registro no arquivo");
            ioe.printStackTrace();
        }

        return found;
    }
    /**
     * Deleta um registro do arquivo, procurando a musica que se deseja deletar pelo
     * ID e marcando sua lapide como verdadeira (*) quando encontrar
     * @param ID da musica a ser deletada
     */
    public static boolean delete(int ID) {
        boolean found = false;
        
        try{
            RandomAccessFile arq = new RandomAccessFile(path, "rw");
            long pos, arqLen = arq.length();
            byte lapide;
            int regSize, regID;

            // posiciona ponteiro no início, pula cabeçalho e salva posição
            arq.seek(0); 
            arq.skipBytes(Integer.BYTES);
            pos = arq.getFilePointer(); 

            while(!found && pos != arqLen){
                // lê primeiros dados
                lapide = arq.readByte();
                regSize = arq.readInt();

                if(lapide == ' '){ // lapide falsa => registro não excluído
                    regID = arq.readInt();

                    if(regID == ID){ // verifica se registro é o procurado 
                        arq.seek(pos); // retorna para posição da lápide
                        arq.writeByte('*');
                        found = true;
                    } else{ // pula bytes de parte do registro (ID já foi pulado)
                        arq.skipBytes(regSize - Integer.BYTES);
                    }
                } else{ // pula bytes do registro inteiro
                    arq.skipBytes(regSize); 
                }
                
                pos = arq.getFilePointer(); // início do próximo registro (lápide)
            }    
            
            arq.close();
        } catch(FileNotFoundException fnfe){
            System.err.println("Arquivo .db de colunas nao encontrado");
            fnfe.printStackTrace();
        } catch(IOException ioe){
            System.err.println("Erro de leitura/escrita ao deletar registro no arquivo");
            ioe.printStackTrace();
        }

        return found;
    }
}

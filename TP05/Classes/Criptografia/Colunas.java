package TP05.Classes.Criptografia;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

import TP05.Classes.Musica;

public class Colunas {
    private static final String path = "TP05/Data/arquivoColunas.db";
    private static final String key = "COLUNAS";

    public static void encrypt(Musica musica) {
        String palavra = musica.getTrack_id();
        int col = key.length(),
            lin = Math.round((float) palavra.length() / col);
        char[][] matriz = new char[lin][col];

        int n = 0;
        for(int i = 0; i < lin && n < palavra.length(); i++){
            for(int j = 0; j < col; j++){
                matriz[i][j] = palavra.charAt(n);
                n++;
            }
        }
    }

    public static void decrypt(Musica musica) {
        
    }
        /* CRUD */
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

            arq.seek(0); // início do arquivo

            // lê ID do último registro em arquivo (0 se estiver vazio)
            ultimoID = arq.readInt();
            ultimoID++;
            obj.setID(ultimoID);

            // criptografa campo track_id da Musica com diferentes algoritmos
            Ceaser.create(obj);
            Vigenere.create(obj);
            Colunas.create(obj);

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

package TP05.Classes.Criptografia;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

import TP05.Classes.Musica;

public class Vigenere {

    private static final String key = "AEDsIII"; // chave para criptografia/descritografia

    /**
     * Criptografa uma string usando a cifra de Vigenere
     * 
     * @param original string a ser criptografada
     * @param key      chave para criptografia
     * @return string criptografada
     */
    private static String encrypt(String original, String key) {
        StringBuilder ciphertext = new StringBuilder();
        int keyIndex = 0;
        for (char c : original.toCharArray()) {
            int shift = key.charAt(keyIndex) % 256;
            char shifted = (char) ((c + shift) % 256);
            ciphertext.append(shifted);
            keyIndex = (keyIndex + 1) % key.length();
        }
        return ciphertext.toString();
    }

    /**
     * Descriptografa uma string usando a cifra de Vigenere
     * 
     * @param ciphertext string a ser descriptografada
     * @param key        chave para descriptografia
     * @return string descriptografada
     */
    private static String decrypt(String ciphertext) {
        StringBuilder plaintext = new StringBuilder();
        int keyIndex = 0;
        for (char c : ciphertext.toCharArray()) {
            int shift = key.charAt(keyIndex) % 256;
            char shifted = (char) ((c - shift + 256) % 256);
            plaintext.append(shifted);
            keyIndex = (keyIndex + 1) % key.length();
        }
        return plaintext.toString();
    }

    /**
     * @param obj objeto a ser criptografado
     */
    public static void encrypt(Musica obj) {
        
        obj.setTrack_id(encrypt(obj.getTrack_id(), key));
    }

    /**
     * @param obj objeto a ser descriptografado
     */
    public static void decrypt(Musica obj) {
        obj.setTrack_id(decrypt(obj.getTrack_id()));
    }

    /**
     * Cria um registro de Musica no arquivo
     * @param obj  objeto a ser criado no arquivo
     */
    public static void create(Musica obj) {
        int ultimoID = -1;
        byte[] objectData;
        long pos;

        
        try {
            RandomAccessFile arq = new RandomAccessFile("TP05/Data/arquivoVigenere.db", "rw");
            if(arq.length() == 0) {arq.seek(0); arq.writeInt(0);} // cabeçalho
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
            System.err.println("Arquivo .db de vigenere nao encontrado");
            fnfe.printStackTrace();
        } catch (IOException ioe) {
            System.err.println("Erro de leitura/escrita ao criar registro no arquivo");
            ioe.printStackTrace();
        }
    }

    /**
     * Lê um registro de Musica do arquivo
     * @param ID  ID do registro a ser lido
     * @return  objeto Musica lido do arquivo
     */
    public static Musica read(int ID) {
        Musica obj = null;
        
        try {
            RandomAccessFile arq = new RandomAccessFile("TP05/Data/arquivoVigenere.db", "rw");
            boolean found = false;
            long pos, arqLen = arq.length();
            byte lapide;
            int regSize, regID;

            // posiciona ponteiro no início, pula cabeçalho e salva posição
            arq.seek(0);
            arq.skipBytes(Integer.BYTES);
            pos = arq.getFilePointer();

            while (!found && pos != arqLen) {
                // lê primeiros dados
                lapide = arq.readByte();
                regSize = arq.readInt();
                regID = arq.readInt();

                if (regID == ID) { // verifica se registro é o procurado
                    found = true;

                    if (lapide == ' ') { // lapide falsa => registro não excluído
                        // retorna para posição do ID
                        arq.seek(pos + 1 + Integer.BYTES);

                        // lê registro em bytes e converte para objeto
                        byte[] data = new byte[regSize];
                        arq.read(data);
                        obj = new Musica();
                        
                        obj.fromByteArray(data);
                        
                        // descriptografa campo track_id da Musica
                        System.out.println("Musica Criptografada: \"" + obj + "\"");
                        decrypt(obj);

                    } else {
                        System.err.println("Registro pesquisado ja foi excluido");
                    }
                } else { // pula bytes de parte do registro (ID já foi pulado)
                    arq.skipBytes(regSize - Integer.BYTES);
                }

                pos = arq.getFilePointer(); // início do próximo registro (lápide)
            }
            arq.close();
        } catch(FileNotFoundException fnfe){
            System.err.println("Arquivo .db de vigenere nao encontrado");
            fnfe.printStackTrace();
        } catch (IOException ioe) {
            System.err.println("Erro de leitura/escrita ao ler registro no arquivo");
            ioe.printStackTrace();
        }
        
        return obj;
    }

    /**
     * Atualiza um registro de Musica no arquivo
     * @param objNovo  objeto Musica a ser atualizado no arquivo
     * @return  true se atualizado com sucesso, false se não encontrado
     */
    public static boolean update(Musica objNovo) {
        boolean found = false;
        
        // criptografa objeto novo
        encrypt(objNovo);
        
        try {
            RandomAccessFile arq = new RandomAccessFile("TP05/Data/arquivoVigenere.db", "rw");
            long pos, arqLen = arq.length();
            byte lapide;
            byte[] objectData;
            int regSize, regSizeNovo, regID;

            // posiciona ponteiro no início, pula cabeçalho e salva posição
            arq.seek(0);
            arq.skipBytes(Integer.BYTES);
            pos = arq.getFilePointer();

            while (!found && pos != arqLen) {
                // lê primeiros dados
                lapide = arq.readByte();
                regSize = arq.readInt();
                regID = arq.readInt();

                if (regID == objNovo.getID()) { // verifica se registro é o procurado
                    if (lapide == ' ') { // lapide falsa => registro não excluído
                        found = true;

                        // retorna para posição do ID
                        arq.seek(pos + 1 + Integer.BYTES);

                        // cria registro como array de bytes do objeto novo
                        objectData = objNovo.toByteArray();
                        regSizeNovo = objectData.length;

                        if (regSizeNovo == regSize) { // mesmo tamanho => OK
                            arq.write(objectData);
                        } else { // maior ou menor => delete + create
                            arq.seek(pos); // retorna para posição da lápide
                            arq.writeByte('*');
                            create(objNovo);
                            System.out.println("Novo ID da musica: " + objNovo.getID());
                        }
                    } else {
                        System.err.println("Registro pesquisado ja foi excluido");
                        pos = arqLen;
                    }
                } else { // pula bytes de parte do registro (ID já foi pulado)
                    arq.skipBytes(regSize - Integer.BYTES);
                }

                pos = arq.getFilePointer(); // início do próximo registro (lápide)
            }
            arq.close();
        } catch(FileNotFoundException fnfe){
            System.err.println("Arquivo .db de vigenere nao encontrado");
            fnfe.printStackTrace();
        } catch (IOException ioe) {
            System.err.println("Erro de leitura/escrita ao atualizar registro no arquivo");
            ioe.printStackTrace();
        }
        
        return found;
    }

    /**
     * Exclui um registro de Musica do arquivo
     * @param ID ID do registro a ser excluído
     * @return  true se excluído com sucesso, false se não encontrado
     */
    public static boolean delete(int ID) {
        boolean found = false;
        
        try {
            RandomAccessFile arq = new RandomAccessFile("TP05/Data/arquivoVigenere.db", "rw");
            long pos, arqLen = arq.length();
            byte lapide;
            int regSize, regID;

            // posiciona ponteiro no início, pula cabeçalho e salva posição
            arq.seek(0);
            arq.skipBytes(Integer.BYTES);
            pos = arq.getFilePointer();

            while (!found && pos != arqLen) {
                // lê primeiros dados
                lapide = arq.readByte();
                regSize = arq.readInt();

                if (lapide == ' ') { // lapide falsa => registro não excluído
                    regID = arq.readInt();

                    if (regID == ID) { // verifica se registro é o procurado
                        arq.seek(pos); // retorna para posição da lápide
                        arq.writeByte('*');
                        found = true;
                    } else { // pula bytes de parte do registro (ID já foi pulado)
                        arq.skipBytes(regSize - Integer.BYTES);
                    }
                } else { // pula bytes do registro inteiro
                    arq.skipBytes(regSize);
                }

                pos = arq.getFilePointer(); // início do próximo registro (lápide)
            }
            arq.close();
        } catch(FileNotFoundException fnfe){
            System.err.println("Arquivo .db de vigenere nao encontrado");
            fnfe.printStackTrace();
        } catch (IOException ioe) {
            System.err.println("Erro de leitura/escrita ao deletar registro no arquivo");
            ioe.printStackTrace();
        }

        return found;
    }
}
package TP05.Classes.Criptografia;

import java.io.IOException;
import java.io.RandomAccessFile;

import TP05.Classes.Musica;

public class Vigenere {

    private static final String key = "AEDs_III"; // chave para criptografia/descritografia

    /**
     * Criptografa uma string usando a cifra de Vigenere
     * 
     * @param original string a ser criptografada
     * @param key      chave para criptografia
     * @return string criptografada
     */
    private static String encrypt(String original, String key) {
        StringBuilder ciphertext = new StringBuilder(); // string criptografada
        int keyIndex = 0; // indice da chave
        for (char c : original.toCharArray()) { // para cada caractere da string original
            if (Character.isLetter(c)) {
                int shift = key.charAt(keyIndex) - 'a'; // deslocamento
                char shifted = (char) (((c - 'a' + shift) % 26) + 'a'); // caractere deslocado
                ciphertext.append(shifted); // adiciona caractere deslocado a string criptografada
                keyIndex = (keyIndex + 1) % key.length(); // atualiza indice da chave
            } else {
                ciphertext.append(c); // adiciona caractere a string criptografada
            }
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
    private static String decrypt(String ciphertext, String key) {
        StringBuilder original = new StringBuilder(); // string descriptografada
        int keyIndex = 0; // indice da chave
        for (char c : ciphertext.toCharArray()) { // para cada caractere da string criptografada
            if (Character.isLetter(c)) {
                int shift = key.charAt(keyIndex) - 'a'; // deslocamento
                char shifted = (char) (((c - 'a' - shift + 26) % 26) + 'a'); // caractere deslocado
                original.append(shifted); // adiciona caractere deslocado a string descriptografada
                keyIndex = (keyIndex + 1) % key.length(); // atualiza indice da chave
            } else {
                original.append(c); // adiciona caractere a string descriptografada
            }
        }
        return original.toString();

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
        obj.setTrack_id(decrypt(obj.getTrack_id(), key));
    }

    /**
     * Cria uma musica
     * 
     * @param obj Musica a ser criptografada
     * @throws IOException se houver erro de leitura/escrita
     */
    public void create(Musica obj) throws IOException {
        int ultimoID = -1;
        byte[] objectData;
        long pos;

        RandomAccessFile arq = new RandomAccessFile("TP05/Data/arquivoVigenere.db", "rw");

        try {
            arq.seek(0); // início do arquivo

            // lê ID do último registro em arquivo (0 se estiver vazio)
            ultimoID = arq.readInt();
            ultimoID++;
            obj.setID(ultimoID);

            // criptografa campo track_id da Musica com diferentes algoritmos

            Vigenere.encrypt(obj);

            // cria registro como array de bytes do objeto
            objectData = obj.toByteArray();
            pos = arq.length();
            arq.seek(pos); // fim do arquivo

            arq.writeByte(' '); // lapide
            arq.writeInt(objectData.length); // tamanho do registro (bytes)
            arq.write(objectData);

            arq.seek(0); // início do arquivo
            arq.writeInt(ultimoID);
        } catch (IOException ioe) {
            System.err.println("Erro de leitura/escrita ao criar registro no arquivo");
            ioe.printStackTrace();
        }
        arq.close();
    }

    /**
     * Atualiza uma Musica
     * 
     * @param ID ID da Musica a ser lida
     * @return Musica lida
     * @throws IOException se houver erro de leitura/escrita
     */
    public Musica read(int ID) throws IOException {
        Musica obj = null;
        RandomAccessFile arq = new RandomAccessFile("TP05/Data/arquivoVigenere.db", "rw");

        try {
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

                        // descriptografa campo track_id da Musica com diferentes algoritmos

                        Vigenere.decrypt(obj);

                    } else {
                        System.err.println("Registro pesquisado ja foi excluido");
                    }
                } else { // pula bytes de parte do registro (ID já foi pulado)
                    arq.skipBytes(regSize - Integer.BYTES);
                }

                pos = arq.getFilePointer(); // início do próximo registro (lápide)
            }
        } catch (IOException ioe) {
            System.err.println("Erro de leitura/escrita ao ler registro no arquivo");
            ioe.printStackTrace();
        }
        arq.close();
        return obj;
    }

    /**
     * Atualiza uma Musica
     * 
     * @param objNovo Musica a ser atualizada
     * @return true se atualizado, false se não encontrado
     * @throws IOException se houver erro de leitura/escrita
     */
    public boolean update(Musica objNovo) throws IOException {
        boolean found = false;
        RandomAccessFile arq = new RandomAccessFile("TP05/Data/arquivoVigenere.db", "rw");
        encrypt(objNovo);

        try {
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
        } catch (IOException ioe) {
            System.err.println("Erro de leitura/escrita ao atualizar registro no arquivo");
            ioe.printStackTrace();
        }
        arq.close();
        return found;
    }

    /**
     * Exclui uma Musica
     * 
     * @param ID ID da Musica a ser excluída
     * @return true se excluído, false se não encontrado
     * @throws IOException se houver erro de leitura/escrita
     */
    public boolean delete(int ID) throws IOException {
        RandomAccessFile arq = new RandomAccessFile("TP05/Data/arquivoVigenere.db", "rw");
        boolean found = false;

        try {
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
        } catch (IOException ioe) {
            System.err.println("Erro de leitura/escrita ao deletar registro no arquivo");
            ioe.printStackTrace();
        }
        arq.close();
        return found;
    }

}
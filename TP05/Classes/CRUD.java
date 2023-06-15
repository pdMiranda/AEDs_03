/** Pacotes **/
package TP05.Classes;
import TP05.Classes.Criptografia.Ceaser;
import TP05.Classes.Criptografia.Colunas;
import TP05.Classes.Criptografia.Vigenere;

import java.io.RandomAccessFile;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.EOFException;
import java.io.File;
import java.io.FileWriter;

/** Classe CRUD **/
public class CRUD {
    /* Atributos */
    private RandomAccessFile arq;
    private final String path = "TP05/Data/arquivo.db";
    private final String pathTxt = "TP05/Data/arquivo.txt"; 

    /* Getters */
    public String getPathArq(){
        return path;
    }
    public String getPathTxt(){
        return pathTxt;
    }

    /* Construtores */
    public CRUD() {
        // abrir ou criar arquivo
        try{
            arq = new RandomAccessFile(path, "rw");
            
            // cria arquivo se nao existir
            if(!exists()){
                arq.seek(0);
                arq.writeInt(0); // ultimoID inicial
            }
        } catch(FileNotFoundException fnfe){
            System.err.println(fnfe.getMessage());
        } catch(IOException ioe){
            System.err.println(ioe.getMessage());
        }
    }

    /* Métodos */
        /* Manipulação do arquivo */
    /**
     * Verifica se arquivo ja existe 
     * @return true se sim, false se nao
     */
    public boolean exists() {
        boolean existe = false;

        try{
            if(arq.length() != 0)
                existe = true;
        } catch(IOException ioe){
            System.err.println(ioe.getMessage());
        }

        return existe;
    }
    /**
     * Fecha arquivo RandomAcessFile
     */
    public void close() {
        try{
            arq.close();
        } catch(IOException ioe){
            System.err.println("Erro ao fechar arquivo");
            ioe.printStackTrace();
        }
    }
    /**
     * Deleta arquivo RandomAcessFile 
     * @return true se conseguir deletar, false caso contrario
     */
    public boolean deleteFile() {
        boolean sucesso = false;
        
        File file = new File(this.path);
        
        if(file.delete())
            sucesso = true;

        return sucesso;
    }
    /**
     * Deleta arquivo texto
     */
    public void deleteTxt() {
        // abre arquivo texto
        File txt = new File(pathTxt);
        
        // verifica se arquivo esta preenchido
        if(txt.length() != 0){
            txt.delete(); // deleta arquivo
        }
    }
    /**
     * Converte arquivo de bytes para texto, lendo apenas musicas validas 
     */
    public void toText() {
        if(exists()){
            String text = ""; // arquivo em texto
            
            try{
                long pos, arqLen = arq.length();
                byte lapide;
                int regSize;

                // posiciona ponteiro no inicio, pula cabecalho e salva posicao
                arq.seek(Integer.BYTES); 
                pos = arq.getFilePointer();
                
                while(pos != arqLen){
                    // le primeiros dados
                    lapide = arq.readByte();
                    regSize = arq.readInt();
                    
                    if(lapide == ' '){ // lapide falsa => registro nao excluido
                        // le registro em bytes e converte para objeto 
                        byte[] data = new byte[regSize];
                        arq.read(data);
                        Musica obj = new Musica();
                        obj.fromByteArray(data);
                        
                        // concatena Musica como string ao texto atual 
                        text = text + obj.toString() + "\n";
                    } else{
                        arq.skipBytes(regSize); // pula registro
                    }
                    
                    pos = arq.getFilePointer(); // inicio do próximo registro (lapide)
                }
            } catch(IOException ioe){
                System.err.println("Erro ao ler registros no arquivo.db");
                ioe.printStackTrace();
            }

            try{
                // cria arquivo texto para escrita
                FileWriter fw = new FileWriter(pathTxt);
                
                // escreve string com Musicas validas
                fw.write(text);
                fw.close();
            } catch(IOException ioe){
                System.err.println("Erro ao escrever arquivo como texto");
                ioe.printStackTrace();
            }
        }
    }
    /**
     * Refaz o arquivo, retirando os registros com lapide verdadeira (excluidos)
     */
    public void removeInvalid() {
        try{
            String tmpPath = "TP04/Data/musicasTMP.db";
            RandomAccessFile tmp = new RandomAccessFile(tmpPath, "rw");
            long pos, arqLen = arq.length();
            byte lapide;
            int regSize, ultimoID;

            // posiciona ponteiro no início, lê cabeçalho e salva posição
            arq.seek(0); 
            ultimoID = arq.readInt();
            tmp.writeInt(ultimoID); // escreve ultimo ID no arquivo tmp
            pos = arq.getFilePointer();
            
            while(pos != arqLen){
                // lê primeiros dados
                lapide = arq.readByte();
                regSize = arq.readInt();
                
                if(lapide == ' '){ // lapide falsa => registro não excluído
                    // lê registro em bytes e converte para objeto 
                    byte[] data = new byte[regSize];
                    arq.read(data);
                    Musica obj = new Musica();
                    obj.fromByteArray(data);
                    
                    // escreve registro no arquivo tmp
                    byte[] objectData = obj.toByteArray();
                    tmp.writeByte(' '); // lapide
                    tmp.writeInt(objectData.length); // tamanho do registro (bytes)
                    tmp.write(objectData);
                } else{
                    arq.skipBytes(regSize); // pula registro
                }
                
                pos = arq.getFilePointer(); // início do próximo registro (lápide)
            }

            // copia tmp p/arquivo original (criado de novo depois de deletado)
            if(deleteFile()){ 
                this.arq = new RandomAccessFile(this.path, "rw");
                
                // array de bytes c/tds os bytes de tmp
                int tmpLen = (int)tmp.length();
                byte[] tmpData = new byte[tmpLen];
                tmp.seek(0);
                tmp.read(tmpData);

                // escreve no arquivo original
                arq.seek(0);
                arq.write(tmpData);
            } else{
                System.err.println("Erro ao deletar arquivo original para refaze-lo");
            }

            tmp.close();
            File file = new File(tmpPath);
            file.delete();
        } catch(FileNotFoundException fnfe){
            System.err.println("Erro ao criar arquivo temporario para refazer arquivo");
            fnfe.printStackTrace();
        } catch(IOException ioe){
            System.err.println("Erro ao ler registros no arquivo");
            ioe.printStackTrace();
        }
    }
    /**
     * Copia registros do arquivo tmp final da ordenacao de volta para o arquivo original
     */
    public void copyTmp(String tmpPath) {
        try{
            RandomAccessFile tmp = new RandomAccessFile(tmpPath, "rw");
            
            long pos = 0, tmpLen = tmp.length() - 1;
            int regSize;
            byte lapide;
            byte[] data;
            
            // posiciona ponteiro depois do ultimoID
            arq.seek(0);
            arq.skipBytes(Integer.BYTES); 
            
            tmp.seek(0);
            
            while(pos < tmpLen){
                try{
                    // lê registro do tmp e escreve em arq
                    lapide = tmp.readByte();
                    arq.writeByte(lapide);
                   
                    regSize = tmp.readInt();
                    arq.writeInt(regSize);
                    
                    data = new byte[regSize];
                    tmp.read(data);
                    arq.write(data);
                    
                    pos = tmp.getFilePointer(); 
                } catch(EOFException eofe){
                    break;
                }
            }

            tmp.close();
        } catch(FileNotFoundException fnfe){
            System.err.println("Erro ao buscar arquivo temporario para copiar");
            fnfe.printStackTrace();
        } catch(IOException ioe){
            System.err.println("Erro ao ler registros no arquivo tmp");
            ioe.printStackTrace();
        }
    }
        /* Manipulação de registros */
    /**
     * Imprime todos os registros válidos por completo do arquivo (lapide falsa)
     */
    public void printAll() {
        try{
            long pos, arqLen = arq.length();
            byte lapide;
            int regSize;

            // posiciona ponteiro no início, pula cabeçalho e salva posição
            arq.seek(0); 
            arq.skipBytes(Integer.BYTES);
            pos = arq.getFilePointer();
            
            while(pos != arqLen){
                // lê primeiros dados
                lapide = arq.readByte();
                regSize = arq.readInt();
                
                if(lapide == ' '){ // lapide falsa => registro não excluído
                    // lê registro em bytes e converte para objeto 
                    byte[] data = new byte[regSize];
                    arq.read(data);
                    Musica obj = new Musica();
                    obj.fromByteArray(data);

                    // imprime registro
                    System.out.println("\n"+obj);
                } else{
                    arq.skipBytes(regSize); // pula registro
                }
                
                pos = arq.getFilePointer(); // início do próximo registro (lápide)
            }
        } catch(IOException ioe){
            System.err.println("Erro ao ler registros no arquivo");
            ioe.printStackTrace();
        }
    }
    /**
     * Conta todos os registros válidos do arquivo (lapide falsa)
     * @return int qtd de registros 
     */
    public int totalValid() {
        int qtd = 0;
        
        try{
            long pos, arqLen = arq.length();
            byte lapide;
            int regSize;

            // posiciona ponteiro no início, pula cabeçalho e salva posição
            arq.seek(0); 
            arq.skipBytes(Integer.BYTES);
            pos = arq.getFilePointer();
            
            while(pos != arqLen){
                // lê primeiros dados
                lapide = arq.readByte();
                regSize = arq.readInt();
                
                if(lapide == ' '){ // lapide falsa => registro não excluído
                    qtd++;
                }
                
                arq.skipBytes(regSize);
                pos = arq.getFilePointer(); // início do próximo registro (lápide)
            }
        } catch(IOException ioe){
            System.err.println("Erro ao contar registros validos no arquivo");
            ioe.printStackTrace();
        }

        return qtd;
    }
    /**
     * Conta todos os registros não válidos do arquivo (lapide verdadeira)
     * @return int qtd de registros 
     */
    public int totalNotValid() {
        int qtd = 0;
        
        try{
            long pos, arqLen = arq.length();
            byte lapide;
            int regSize;

            // posiciona ponteiro no início, pula cabeçalho e salva posição
            arq.seek(0); 
            arq.skipBytes(Integer.BYTES);
            pos = arq.getFilePointer();
            
            while(pos != arqLen){
                // lê primeiros dados
                lapide = arq.readByte();
                regSize = arq.readInt();
                
                if(lapide == '*'){ // lapide verdadeira => registro excluído
                    qtd++;
                }
                
                arq.skipBytes(regSize);
                pos = arq.getFilePointer(); // início do próximo registro (lápide)
            }
        } catch(IOException ioe){
            System.err.println("Erro ao contar registros excluidos no arquivo");
            ioe.printStackTrace();
        }

        return qtd;
    }
        /* CRUD */
    /**
     * Cria um registro de uma musica, lendo o último ID registrado para setar o ID atual,
     * atualizando o valor ao final 
     * @param obj Musica a ser registrada no arquivo 
     */
    public void create(Musica obj) {
        Musica objColunas = obj.clone();
        Musica objCeaser = obj.clone();
        Musica objVigenere = obj.clone();
        Colunas.create(objColunas);
        Ceaser.create(objCeaser);
        Vigenere.create(objVigenere);
    }
    /**
     * Percorre o arquivo procurando pelo ID da musica que se quer ler, quando encontra
     * lê o registro em bytes e converte para um objeto Musica, que é retornado
     * @param ID da musica a ser lida
     * @return objeto Musica lido do arquivo
     */
    public Musica read(int ID) {
        Musica obj = null;
        
        System.out.println("\nDescriptografando track_id da musica com Cifra de Cesar...");
        obj = Ceaser.read(ID);
        if(obj != null) System.out.println("Musica Descriptografada: \"" + obj + "\"\n");

        System.out.println("\nDescriptografando track_id da musica com Cifra de Vigenere...");
        obj = Vigenere.read(ID);
        if(obj != null) System.out.println("Musica Descriptografada: \"" + obj + "\"\n");
        

        System.out.println("\nDescriptografando track_id da musica com Cifra de Colunas...");
        obj = Colunas.read(ID);
        if(obj != null) System.out.println("Musica Descriptografada: \"" + obj + "\"\n");

        return obj;
    }
    /**
     * Atualiza um registro de musica, procurando registro antigo e comparando seus 
     * tamanhos. Se o novo registro for menor ou maior que o antigo, deleta o antigo e
     * cria o novo no fim do arquivo, se forem de mesmo tamanho apenas escreve no lugar
     * @param objNovo nova musica a ser registrada
     */
    public boolean update(Musica objNovo) {
        boolean found = false;

        Musica objColunas = objNovo.clone();
        Musica objCeaser = objNovo.clone();
        Musica objVigenere = objNovo.clone();

        if(Ceaser.update(objCeaser) && Colunas.update(objColunas) && Vigenere.update(objVigenere))
            found = true;

        return found;
    }
    /**
     * Deleta um registro do arquivo, procurando a musica que se deseja deletar pelo
     * ID e marcando sua lapide como verdadeira (*) quando encontrar
     * @param ID da musica a ser deletada
     */
    public boolean delete(int ID) {
        boolean found = false;
        
        if(Ceaser.delete(ID) && Colunas.delete(ID) && Vigenere.delete(ID))
            found = true;

        return found;
    }
}

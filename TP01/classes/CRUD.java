/** Pacotes **/
package TP01.classes;
import java.io.RandomAccessFile;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.EOFException;
import java.io.File;
import java.util.ArrayList;

/** Classe CRUD **/
public class CRUD {
    /* Atributos */
    protected RandomAccessFile arq;
    protected String path;

    /* Getters e Setters */
    public RandomAccessFile getArq() {
        return this.arq;
    }
    public void setArq(RandomAccessFile arq) {
        this.arq = arq;
    }
    public String getPath() {
        return this.path;
    }
    public void setPath(String path) {
        this.path = path;
    }

    /* Construtores */
    public CRUD() throws FileNotFoundException { 
        this("teste.db");
    }
    public CRUD(String path) throws FileNotFoundException {
        this.path = path; 
        this.arq = new RandomAccessFile(this.path, "rw");
        
        try{
            arq.seek(0);

            if(arq.length() == 0)
                arq.writeInt(0); // ultimoID inicial
        } catch(IOException ioe){
            System.err.println("Erro ao inicializar CRUD");
            ioe.printStackTrace();
        }
    }

    /* Métodos */
        /* Manipulação do arquivo */
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
     * Refaz o arquivo, retirando os registros com lapide verdadeira (excluidos)
     */
    public void removeInvalid() {
        try{
            String tmpPath = "TP01/data/musicasTMP.db";
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
     * Imprime n duration_ms dos registros válidos do arquivo (lapide falsa)
     * @param n qtd de registros a serem imprimidos
     */
    public void printNDurationMs(int n) {
        try{
            long pos, arqLen = arq.length();
            byte lapide;
            int regSize, i = 0;

            // posiciona ponteiro no início, pula cabeçalho e salva posição
            arq.seek(0); 
            arq.skipBytes(Integer.BYTES);
            pos = arq.getFilePointer();
            
            while(pos != arqLen && i < n){
                // lê primeiros dados
                lapide = arq.readByte();
                regSize = arq.readInt();
                
                if(lapide == ' '){ // lapide falsa => registro não excluído
                    // lê registro em bytes e converte para objeto 
                    byte[] data = new byte[regSize];
                    arq.read(data);
                    Musica obj = new Musica();
                    obj.fromByteArray(data);

                    // imprime duration_ms do registro
                    System.out.println(obj.getDuration_ms());

                    i++;
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
        int ultimoID = -1;
        byte[] objectData;
        long pos;

        try{
            arq.seek(0); // início do arquivo

            // lê ID do último registro em arquivo (0 se estiver vazio)
            ultimoID = arq.readInt();
            ultimoID++;
            obj.setID(ultimoID);

            // cria registro como array de bytes do objeto
            objectData = obj.toByteArray();
            pos = arq.length();
            arq.seek(pos); // fim do arquivo
            
            arq.writeByte(' '); // lapide
            arq.writeInt(objectData.length); // tamanho do registro (bytes)
            arq.write(objectData);

            arq.seek(0); // início do arquivo
            arq.writeInt(ultimoID);
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
    public Musica read(int ID) {
        Musica obj = null;
       
        try{
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
    public boolean update(Musica objNovo) {
        boolean found = false;

        try{
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
    public boolean delete(int ID) {
        boolean found = false;
        
        try{
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
        } catch(IOException ioe){
            System.err.println("Erro de leitura/escrita ao deletar registro no arquivo");
            ioe.printStackTrace();
        }

        return found;
    }
        /* Ordenação */
    /**
     * Ordena arquivo por intercalação balanceada (chama funções da classe Sort)
     * @param int tipo de ordenação
     */
    public void sort(int tipo) {
        Sort ordena = new Sort();
        
        this.removeInvalid();
        
        if(tipo == 1){ // Comum
            ordena.intercalaComum(this.path);
        } else if(tipo == 2){ // Blocos de tamanho variavel
            ordena.intercalaBlocosVar(this.path);
        } else if(tipo == 3){ // Selecao por substituicao
            ordena.intercalaSubstituicao(this.path);
        }
    }
    /**
     * Lê n registros, formando um bloco de tamanho n, salva em um array de Musica e 
     * retorna o bloco e a posicao do proximo bloco a ser buscado
     * @param bloco array de Musica com n registros (um bloco) 
     * @param pos posicao inicial do arquivo (primeiro registro do bloco)
     * @return long pos posicao final do arquivo (primeiro registro do próximo bloco)
     */
    public ArrayList<Musica> getBlock(long pos, int tam) {
        ArrayList<Musica> bloco = new ArrayList<Musica>();

        try{
            int regSize, i = 0;

            // posiciona ponteiro no início do bloco
            arq.seek(pos); 

            while(i < tam){
                try{
                    // lê primeiros dados
                    arq.skipBytes(1); // pula a lápide
                    regSize = arq.readInt();

                    // lê registro em bytes e converte para objeto 
                    byte[] data = new byte[regSize];
                    arq.read(data);
                    bloco.add( new Musica() );
                    bloco.get(i).fromByteArray(data);

                    i++;
                    pos = arq.getFilePointer(); // início do próximo registro (lápide)
                } catch(EOFException eofe){
                    break;
                }
            }
        } catch(IOException ioe){
            System.err.println("Erro de leitura/escrita ao ler bloco do arquivo");
            ioe.printStackTrace();
        }

        return bloco;
    }
}

/** Pacotes **/
package TP02.classes;
import TP02.classes.indices.arvore.ArvoreArq;
import TP02.classes.indices.hashing.HashEstendido;
import TP02.classes.indices.listas.ListasArq;
import java.io.RandomAccessFile;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.EOFException;
import java.io.File;

/** Classe CRUD **/
public class CRUD {
    /* Atributos */
    private RandomAccessFile arq;
    private final String path = "TP02/data/musicas.db";

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

    /* Metodos */
        /* Manipulacao do arquivo */
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
     * Refaz o arquivo, retirando os registros com lapide verdadeira (excluidos)
     */
    public void removeInvalid() {
        try{
            String tmpPath = "TP02/data/musicasTMP.db";
            RandomAccessFile tmp = new RandomAccessFile(tmpPath, "rw");
            long pos, arqLen = arq.length();
            byte lapide;
            int regSize, ultimoID;

            // posiciona ponteiro no inicio, le cabecalho e salva posicao
            arq.seek(0); 
            ultimoID = arq.readInt();
            tmp.writeInt(ultimoID); // escreve ultimo ID no arquivo tmp
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
                    
                    // escreve registro no arquivo tmp
                    byte[] objectData = obj.toByteArray();
                    tmp.writeByte(' '); // lapide
                    tmp.writeInt(objectData.length); // tamanho do registro (bytes)
                    tmp.write(objectData);
                } else{
                    arq.skipBytes(regSize); // pula registro
                }
                
                pos = arq.getFilePointer(); // inicio do proximo registro (lapide)
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
                    // le registro do tmp e escreve em arq
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
        /* Manipulacao de registros */
    /**
     * Imprime todos os registros validos por completo do arquivo (lapide falsa)
     */
    public void printAll() {
        try{
            long pos, arqLen = arq.length();
            byte lapide;
            int regSize;

            // posiciona ponteiro no inicio, pula cabecalho e salva posicao
            arq.seek(0); 
            arq.skipBytes(Integer.BYTES);
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

                    // imprime registro
                    System.out.println("\n"+obj);
                } else{
                    arq.skipBytes(regSize); // pula registro
                }
                
                pos = arq.getFilePointer(); // inicio do proximo registro (lapide)
            }
        } catch(IOException ioe){
            System.err.println("Erro ao ler registros no arquivo");
            ioe.printStackTrace();
        }
    }
    /**
     * Conta todos os registros validos do arquivo (lapide falsa)
     * @return int qtd de registros 
     */
    public int totalValid() {
        int qtd = 0;
        
        try{
            long pos, arqLen = arq.length();
            byte lapide;
            int regSize;

            // posiciona ponteiro no inicio, pula cabecalho e salva posicao
            arq.seek(0); 
            arq.skipBytes(Integer.BYTES);
            pos = arq.getFilePointer();
            
            while(pos != arqLen){
                // le primeiros dados
                lapide = arq.readByte();
                regSize = arq.readInt();
                
                if(lapide == ' '){ // lapide falsa => registro nao excluido
                    qtd++;
                }
                
                arq.skipBytes(regSize);
                pos = arq.getFilePointer(); // inicio do proximo registro (lapide)
            }
        } catch(IOException ioe){
            System.err.println("Erro ao contar registros validos no arquivo");
            ioe.printStackTrace();
        }

        return qtd;
    }
    /**
     * Conta todos os registros nao validos do arquivo (lapide verdadeira)
     * @return int qtd de registros 
     */
    public int totalNotValid() {
        int qtd = 0;
        
        try{
            long pos, arqLen = arq.length();
            byte lapide;
            int regSize;

            // posiciona ponteiro no inicio, pula cabecalho e salva posicao
            arq.seek(0); 
            arq.skipBytes(Integer.BYTES);
            pos = arq.getFilePointer();
            
            while(pos != arqLen){
                // le primeiros dados
                lapide = arq.readByte();
                regSize = arq.readInt();
                
                if(lapide == '*'){ // lapide verdadeira => registro excluido
                    qtd++;
                }
                
                arq.skipBytes(regSize);
                pos = arq.getFilePointer(); // inicio do proximo registro (lapide)
            }
        } catch(IOException ioe){
            System.err.println("Erro ao contar registros excluidos no arquivo");
            ioe.printStackTrace();
        }

        return qtd;
    }
        /* CRUD */
    /**
     * Cria um registro de uma musica, lendo o ultimo ID registrado para setar o ID atual,
     * atualizando o valor ao final. Por fim, cria registro nos arquivos de indice
     * @param obj Musica a ser registrada no arquivo 
     * @param tamBase int tamanho inicial da base de dados 
     * @return true se conseguir criar indices, false caso contrario
     */
    public boolean create(Musica obj, int tamBase) {
        boolean sucesso = false;
        int ultimoID = -1;
        byte[] objectData;
        long pos;
        
        // abre indices
        HashEstendido hash = new HashEstendido((int)(0.05 * tamBase));
        ArvoreArq arvore = new ArvoreArq();
        ListasArq listas = new ListasArq();

        try{
            arq.seek(0); // inicio do arquivo

            // le ID do ultimo registro em arquivo (0 se estiver vazio)
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

            arq.seek(0); // inicio do arquivo
            arq.writeInt(ultimoID);

            // atualiza arquivos de indice e verifica se retorna se deu certo
            if( arvore.create(ultimoID, pos) && hash.create(ultimoID, pos) && 
                listas.create(obj.getName(), obj.getArtists(), pos) ){
                sucesso = true;
            }
        } catch(IOException ioe){
            System.err.println("Erro de leitura/escrita ao criar registro no arquivo");
            ioe.printStackTrace();
        }

        return sucesso;
    }
    /**
     * Busca posicao do registro utilizando o indice solicitado, verifica se eh uma posicao
     * valida, se sim le do arquivo de dados, se nao retorna um objeto vazio 
     * @param ID da musica a ser lida
     * @param tamBase int tamanho inicial da base de dados 
     * @param indice int valor do indice escolhido pra pesquisa 
     * @return objeto Musica lido do arquivo
     */
    public Musica read(int ID, int tamBase, int indice) {
        Musica obj = null;

        // abre indices
        HashEstendido hash = new HashEstendido((int)(0.05 * tamBase));
        ArvoreArq arvore = new ArvoreArq();
       
        long pos = -1;
        
        // busca posicao do registro procurado nos indices
        switch(indice){
            case 1: pos = arvore.read(ID); break;
            case 2: pos = hash.read(ID); break; 
        }
        
        obj = readPos(pos);

        return obj;
    }
    /**
     * Le registro do arquivo na posicao passada, verificando se eh uma posicao valida, 
     * se sim le do arquivo de dados, se nao retorna um objeto vazio 
     * @param pos long endereco do registro em arquivo 
     * @return objeto Musica lido do arquivo
     */
    public Musica readPos(long pos) {
        Musica obj = null;

        try{
            byte lapide;
            int regSize;

            if(pos != -1){ // verifica se a posicao eh valida
                arq.seek(pos);

                // le primeiros dados
                lapide = arq.readByte();
                regSize = arq.readInt();

                if(lapide == ' '){ // lapide falsa => registro nao excluido
                    // le registro em bytes e converte para objeto 
                    byte[] data = new byte[regSize];
                    arq.read(data);
                    obj = new Musica();
                    obj.fromByteArray(data);
                }
            }
        } catch(IOException ioe){
            System.err.println("Erro de leitura/escrita ao ler registro no arquivo");
            ioe.printStackTrace();
        }

        return obj;
    }
    /**
     * Busca posicao do registro utilizando o indice solicitado e verifica se eh uma posicao
     * valida. Se sim, verifica se o registrou nao mudou de tamanho (atualiza apenas no 
     * arquivo de dados) ou se mudou (deleta registro logicamente do arquivo de dados, cria
     * novo no fim do arquivo e atualiza indices)
     * @param objNovo nova musica a ser registrada
     * @param tamBase int tamanho inicial da base de dados 
     * @param indice int valor do indice escolhido pra pesquisa 
     * @return true se conseguiu atualizar indices, false caso contrario
     */
    public boolean update(Musica objNovo, int tamBase, int indice) {
        boolean sucesso = false;

        // abre indices
        HashEstendido hash = new HashEstendido((int)(0.05 * tamBase));
        ArvoreArq arvore = new ArvoreArq();
        ListasArq listas = new ListasArq();

        try{
            long pos = -1;
            
            // busca posicao do registro procurado nos indices
            switch(indice){
                case 1: pos = arvore.read(objNovo.getID()); break;
                case 2: pos = hash.read(objNovo.getID()); break; 
            }
            
            byte lapide;
            int regSize, regSizeNovo;
            byte[] objectData;

            if(pos != -1){ // encontrou posicao valida no indice
                arq.seek(pos);

                // le primeiros dados
                lapide = arq.readByte();
                regSize = arq.readInt();

                if(lapide == ' '){ // lapide falsa => registro nao excluido
                    // le registro antigo em bytes e converte para objeto 
                    byte[] data = new byte[regSize];
                    arq.read(data);
                    Musica objAntigo = new Musica();
                    objAntigo.fromByteArray(data);
                    
                    // cria registro como array de bytes do objeto novo
                    objectData = objNovo.toByteArray();
                    regSizeNovo = objectData.length;
                    
                    if(regSizeNovo == regSize){ // mesmo tamanho => OK
                        arq.seek(pos);
                        arq.skipBytes(1 + Integer.BYTES);
                        arq.write(objectData);
                       
                        // verifica se nome ou artistas mudou
                        if( !objAntigo.getName().equals(objNovo.getName()) && 
                            listas.deleteNome(pos) && 
                            listas.createNome(objNovo.getName(), pos) ){
                            
                            sucesso = true;
                        } else if( !objAntigo.getArtists().equals(objNovo.getArtists()) && 
                                   listas.deleteArtistas(pos) &&
                                   listas.createArtistas(objNovo.getArtists(), pos) ){
                           
                            sucesso = true;
                        } else{
                            sucesso = true;
                        }
                    } else{ // maior ou menor => delete + create
                        arq.seek(pos); // retorna para posicao da lapide
                        arq.writeByte('*');

                        // deleta registro antigo dos indices e cria novo 
                        if( arvore.delete(objNovo.getID()) && hash.delete(objNovo.getID())  
                            && listas.delete(pos) && create(objNovo, tamBase) ){
                            sucesso = true;
                        } 
                        
                        System.out.println("Novo ID da musica: "+objNovo.getID());
                    }
                }
            }
        } catch(IOException ioe){
            System.err.println("Erro de leitura/escrita ao atualizar registro no arquivo");
            ioe.printStackTrace();
        }

        return sucesso;
    }
    /**
     * Busca posicao do registro utilizando o indice solicitado, verifica se eh uma posicao
     * valida, se sim deleta (logicamente) do arquivo de dados e retira chave dos indices, 
     * retornando se teve sucesso ou nao
     * @param ID da musica a ser deletada
     * @param tamBase int tamanho inicial da base de dados 
     * @param indice int valor do indice escolhido pra pesquisa
     * @return true se conseguir deletar dos indices, false caso contrario
     */
    public boolean delete(int ID, int tamBase, int indice) {
        boolean sucesso = false;
        
        // abre indices
        HashEstendido hash = new HashEstendido((int)(0.05 * tamBase));
        ArvoreArq arvore = new ArvoreArq();
        ListasArq listas = new ListasArq();

        try{
            long pos = -1;
            
            // busca posicao do registro procurado nos indices
            switch(indice){
                case 1: pos = arvore.read(ID); break;
                case 2: pos = hash.read(ID); break; 
            }

            byte lapide;

            if(pos != -1){ // encontrou posicao valida no indice
                arq.seek(pos);

                // le lapide
                lapide = arq.readByte();

                if(lapide == ' '){ // lapide falsa => registro nao excluido
                    arq.seek(pos); // retorna para posicao da lapide
                    arq.writeByte('*');

                    // deleta registro dos indices
                    sucesso = arvore.delete(ID) && (hash.delete(ID) && listas.delete(pos));
                }
            }
        } catch(IOException ioe){
            System.err.println("Erro de leitura/escrita ao deletar registro no arquivo");
            ioe.printStackTrace();
        }

        return sucesso;
    }
}

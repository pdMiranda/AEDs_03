/** Pacotes **/
package TP02.classes.indices.hashing;
import java.io.RandomAccessFile;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.File;

/** Classe HashEstendido **/
public class HashEstendido {
    /* Atributos */
    private final String pathDiretorio = "TP02/data/diretorioHash.db", // paths dos arquivos
                         pathBucket = "TP02/data/bucketHash.db"; 
    private RandomAccessFile rafDiretorio, // arquivos 
                             rafBucket;
    private int qtdChaves; // qtd de chaves do bucket

    /* Construtor */
    public HashEstendido(int qtdChaves) {
        this.qtdChaves = qtdChaves; // setar tamanho dos buckets

        // abrir ou criar arquivos p/diretorio e buckets
        try{
            rafDiretorio = new RandomAccessFile(pathDiretorio, "rw");
            rafBucket = new RandomAccessFile(pathBucket, "rw");

            // criar arquivos se nao existirem
            if(!exists()){
                // cria um novo diretorio, com profundidade de 1 
                Diretorio diretorio = new Diretorio();
                
                // cria buckets, com profundidade de 1 e liga ponteiros do diretorio
                rafBucket.seek(0);
                for(int i = 0; i < 2; i++){
                    diretorio.setEndereco(i,rafBucket.getFilePointer());

                    Bucket bucket = new Bucket(qtdChaves);
                    byte[] byteArray = bucket.toByteArray();
                    rafBucket.write(byteArray);
                }

                // escreve diretorio no arquivo
                rafDiretorio.seek(0);
                byte[] byteArray = diretorio.toByteArray();
                rafDiretorio.write(byteArray);
            }
        } catch(FileNotFoundException fnfe){
            System.err.println(fnfe.getMessage());
        } catch(IOException ioe){
            System.err.println(ioe.getMessage());
        }
    }
    
    /* Metodos */
        /* Manipulacao dos arquivos */
    /**
     * Verifica se arquivos ja existem 
     * @return true se sim, false se nao
     */
    public boolean exists() {
        boolean existem = false;

        try{
            if(rafDiretorio.length() != 0 && rafBucket.length() != 0)
                existem = true;
        } catch(IOException ioe){
            System.err.println(ioe.getMessage());
        }

        return existem;
    }
    /**
     * Fecha arquivos RandomAcessFile
     */
    public void close() {
        try{
            rafDiretorio.close();
            rafBucket.close();
        } catch(IOException ioe){
            System.err.println("Erro ao fechar arquivos");
            ioe.printStackTrace();
        }
    }
    /**
     * Deleta arquivos RandomAcessFile 
     * @return true se conseguir deletar, false caso contrario
     */
    public boolean deleteFiles() {
        boolean sucesso = false;
        
        File dir = new File(pathDiretorio);
        File bucket = new File(pathBucket);
        
        if(dir.delete() && bucket.delete())
            sucesso = true;

        return sucesso;
    }
        /* Funcoes auxiliares */
    /**
     * Imprime o diretorio e os buckets do hashing lidos dos arquivos
     */
    public void print() {
        try{
            // abre arquivos
            rafDiretorio = new RandomAccessFile(pathDiretorio, "rw");
            rafBucket = new RandomAccessFile(pathBucket, "rw");

            // le arquivo do diretorio de bytes pra classe 
            byte[] byteArray = new byte[ (int)rafDiretorio.length() ];
            rafDiretorio.seek(0);
            rafDiretorio.read(byteArray);
            Diretorio diretorio = new Diretorio();
            diretorio.fromByteArray(byteArray);

            // imprime diretorio
            System.out.println("\nDiretorio:");
            System.out.println(diretorio);
            
            // imprime buckets            
            System.out.println("\nBuckets:");

            // le arquivo de buckets e imprime cada bucket
            rafBucket.seek(0);
            long pos = rafBucket.getFilePointer();
            while(pos != rafBucket.length()){
                Bucket bucket = new Bucket(qtdChaves);
                byteArray = new byte[bucket.getTamBucket()];
                
                rafBucket.read(byteArray);
                bucket.fromByteArray(byteArray);
                System.out.println("Endereco " + pos + ": " + bucket);

                pos = rafBucket.getFilePointer();
            }
        } catch(FileNotFoundException fnfe){
            System.err.println(fnfe.getMessage());
        } catch(IOException ioe){
            System.err.println(ioe.getMessage());
        }
    }
    /**
     * Faz o calculo da funcao hash
     * @param chave elemento a ser inserido no hash
     * @param p profundidade com que se ira calcular a funcao
     * @return int posicao do hash (bucket a ser inserido)
     */
    private int hash(int chave, int p) {
        return chave % ((int) Math.pow(2, p));
    }
    /**
     * Aumenta em 1 a profundidade global do diretorio, criando novas posicoes e atualizando
     * os ponteiros, armazenando mudancas no arquivo 
     */
    private void aumentarPGlobal() {
        try{
            // abre arquivos
            rafDiretorio = new RandomAccessFile(pathDiretorio, "rw");
            rafBucket = new RandomAccessFile(pathBucket, "rw");

            // le arquivo do diretorio de bytes pra classe 
            byte[] byteArray = new byte[ (int)rafDiretorio.length() ];
            rafDiretorio.seek(0);
            rafDiretorio.read(byteArray);
            Diretorio diretorio = new Diretorio();
            diretorio.fromByteArray(byteArray);

            // aumentar pGlobal e ajustar ponteiros
            diretorio.aumentarP();

            // reescrever no arquivo
            rafDiretorio.seek(0);
            byteArray = diretorio.toByteArray();
            rafDiretorio.write(byteArray);
        } catch(FileNotFoundException fnfe){
            System.err.println(fnfe.getMessage());
        } catch(IOException ioe){
            System.err.println(ioe.getMessage());
        }        
    }
    /**
     * Aumenta em 1 a profundidade local do bucket passado, criando novo bucket e alterando
     * ponteiro do diretorio. Por fim, reorganiza as chaves e armazena de volta no arquivo
     * @param pL int profundidade local atual
     * @param bucket int qual o bucket que se quer aumentar a profundidade
     */
    private void aumentarPLocal(int bucket, int pL) {
        try{
            // abre arquivos
            rafDiretorio = new RandomAccessFile(pathDiretorio, "rw");
            rafBucket = new RandomAccessFile(pathBucket, "rw");

            // le arquivo do diretorio de bytes pra classe 
            byte[] byteArray = new byte[ (int)rafDiretorio.length() ];
            rafDiretorio.seek(0);
            rafDiretorio.read(byteArray);
            Diretorio diretorio = new Diretorio();
            diretorio.fromByteArray(byteArray);
            
            // verifica tamanho atual do diretorio
            int pG = diretorio.getPGlobal();
            int tamDir = (int)Math.pow(2, pG);

            // descobre qual bucket deve ser criado
            int bucketNovo = bucket + ((int)Math.pow(2, pL));
            if(bucketNovo >= tamDir){ // bucket passado por parametro ainda nao foi criado
                int tmp = bucket;
                bucket = bucket - ((int)Math.pow(2, pL)); // "bucket atual"
                bucketNovo = tmp;
            }

            // pega endereco do bucket atual do diretorio
            long posBAtual = diretorio.getEndereco(bucket);

            // vai p/posicao do bucket atual e cria objeto
            rafBucket.seek(posBAtual);
            Bucket bAtual = new Bucket(qtdChaves);
            byteArray = new byte[bAtual.getTamBucket()];
            rafBucket.read(byteArray);
            bAtual.fromByteArray(byteArray);
            
            // muda ponteiro no diretorio do bucket novo
            long posBNovo = rafBucket.length();
            long posDir = Integer.BYTES + (bucketNovo * Long.BYTES);
            rafDiretorio.seek(posDir);
            rafDiretorio.writeLong(posBNovo);
    
            // aumenta pLocal e cria novo bucket
            pL++; 
            bAtual.setPLocal(pL);
            Bucket bNovo = new Bucket(qtdChaves, pL);

            // organizar chaves
            int chave = -1;

            for(int i = 0, j = 0; i < qtdChaves; i++){
                chave = bAtual.getChave(i);
                int pos = hash(chave, pL); // calcula novo hash

                if(pos != bucket){ // chave tem que mudar de bucket
                    bNovo.setChave(j, chave);
                    bNovo.setEndereco(j, bAtual.getEndereco(i));

                    bAtual.deletePar(i);
                    j++;
                }
            }

            bAtual.reorganizarChaves();

            // escreve buckets de volta no arquivo
            rafBucket.seek(posBAtual);
            byteArray = bAtual.toByteArray();
            rafBucket.write(byteArray);
            rafBucket.seek(posBNovo);
            byteArray = bNovo.toByteArray();
            rafBucket.write(byteArray);
        } catch(FileNotFoundException fnfe){
            System.err.println(fnfe.getMessage());
        } catch(IOException ioe){
            System.err.println(ioe.getMessage());
        }        
    }
        /* CRUD do indice */
    /**
     * Insere um par chave e endereco no hash, calculando sua posicao pela funcao hash
     * e aumentando as profundidades global ou local quando necessario
     * @param chave int (ID) a ser inserido
     * @param endereco long posicao no arquivo de dados da chave
     * @return true se conseguir inserir, false se nao
     */
    public boolean create(int chave, long endereco) {
        boolean sucesso = false;

        try{
            // abre arquivos
            rafDiretorio = new RandomAccessFile(pathDiretorio, "rw");
            rafBucket = new RandomAccessFile(pathBucket, "rw");
            
            // le pGlobal do diretorio e salva pos
            rafDiretorio.seek(0);
            int pG = rafDiretorio.readInt();
            long posDir = rafDiretorio.getFilePointer();
            
            // calcula funcao hash da chave e le endereco do bucket onde deve inserir
            int bucket = hash(chave, pG);
            posDir = posDir + (bucket * Long.BYTES);
            rafDiretorio.seek(posDir);
            long posBucket = rafDiretorio.readLong();
            
            // vai p/posicao do bucket p/inserir chave e le pLocal e qtd de chaves
            rafBucket.seek(posBucket);
            int pL = rafBucket.readInt();
            int n = rafBucket.readInt();
            int tamPar = Integer.BYTES + Long.BYTES;

            if(n < qtdChaves){ // tem espaco no bucket => escreve dados normalmente
                // atualiza n
                n++;
                rafBucket.seek(posBucket);
                rafBucket.skipBytes(Integer.BYTES); // pula pLocal
                rafBucket.writeInt(n);

                // procura posicao de insercao
                posBucket = rafBucket.getFilePointer() + ((n-1) * tamPar);
                rafBucket.seek(posBucket);
                rafBucket.writeInt(chave);
                rafBucket.writeLong(endereco);
            } else{ // NAO tem espaco no bucket
                if(pL < pG){ // pGlobal ja aumentou => aumentarPLocal
                    aumentarPLocal(bucket, pL);
                    pL++;

                    // posiciona no endereco do bucket a inserir
                    rafDiretorio.seek(posDir);
                } else{ // aumentar pGlobal
                    aumentarPGlobal();
                    pG++;
                    aumentarPLocal(bucket, pL);
                    pL++;
                    
                    // recalcula hash e le endereco do novo bucket onde deve inserir
                    bucket = hash(chave, pG); 
                    posDir = Integer.BYTES + (bucket * Long.BYTES);
                    rafDiretorio.seek(posDir);
                }

                // atualiza endereco do bucket e procura posicao de insercao
                posBucket = rafDiretorio.readLong();
                rafBucket.seek(posBucket);
                rafBucket.skipBytes(Integer.BYTES); // pula pLocal
                n = rafBucket.readInt();

                // atualiza n
                n++;
                rafBucket.seek(posBucket);
                rafBucket.skipBytes(Integer.BYTES); // pula pLocal
                rafBucket.writeInt(n);

                // procura posicao de insercao
                posBucket = rafBucket.getFilePointer() + ((n-1) * tamPar);
                rafBucket.seek(posBucket);
                rafBucket.writeInt(chave);
                rafBucket.writeLong(endereco);
            }

            // verifica se dados foram escritos corretamente
            rafBucket.seek(posBucket);
            int chaveArq = rafBucket.readInt();
            long enderecoArq = rafBucket.readLong();
            
            if(chaveArq == chave && enderecoArq == endereco)
                sucesso = true;
        } catch(FileNotFoundException fnfe){
            System.err.println(fnfe.getMessage());
        } catch(IOException ioe){
            System.err.println(ioe.getMessage());
        }

        return sucesso;
    }
    /**
     * Pesquisa chave no hashing, calculando a funcao hash e buscando no bucket onde deveria
     * estar, retornando endereco da chave no arquivo de dados
     * @param chave int (ID) a ser pesquisado
     * @return long endereco no arquivo de dados da chave 
     */
    public long read(int chave) {
        long endereco = -1;

        try{
            // abre arquivos
            rafDiretorio = new RandomAccessFile(pathDiretorio, "rw");
            rafBucket = new RandomAccessFile(pathBucket, "rw");
            
            // le pGlobal do diretorio e salva pos
            rafDiretorio.seek(0);
            int pG = rafDiretorio.readInt();
            long posDir = rafDiretorio.getFilePointer();
            
            // calcula funcao hash da chave e le endereco do bucket onde deve pesquisar
            int bucket = hash(chave, pG);
            posDir = posDir + (bucket * Long.BYTES);
            rafDiretorio.seek(posDir);
            long posBucket = rafDiretorio.readLong();
            
            // vai p/posicao do bucket p/buscar chave e le qtd de chaves
            rafBucket.seek(posBucket);
            rafBucket.skipBytes(Integer.BYTES); // pula pLocal
            int n = rafBucket.readInt();
            
            // pesquisa chave no bucket
            int i = 0;
            boolean found = false;
            while( i < n && !found ){
                int chaveArq = rafBucket.readInt();
                
                if(chave == chaveArq){ // achou
                    endereco = rafBucket.readLong();
                    found = true;
                } else{ // nao achou: pula endereco
                    rafBucket.skipBytes(Long.BYTES);
                }

                i++;
            }
        } catch(FileNotFoundException fnfe){
            System.err.println(fnfe.getMessage());
        } catch(IOException ioe){
            System.err.println(ioe.getMessage());
        }

        return endereco;
    }
    /**
     * Pesquisa chave no hashing, calculando a funcao hash e lendo o bucket onde deveria
     * estar. Depois procura chave dentro do bucket encontrado, deletando par chave e 
     * endereco, reorganizando chaves e reescrevendo em arquivo
     * @param chave int (ID) a ser deletado
     * @return true se conseguir deletar chave, false caso contrario
     */
    public boolean delete(int chave) {
        boolean sucesso = false;

        try{
            // abre arquivos
            rafDiretorio = new RandomAccessFile(pathDiretorio, "rw");
            rafBucket = new RandomAccessFile(pathBucket, "rw");
            
            // le pGlobal do diretorio e salva pos
            rafDiretorio.seek(0);
            int pG = rafDiretorio.readInt();
            long posDir = rafDiretorio.getFilePointer();
            
            // calcula funcao hash da chave e le endereco do bucket onde deve pesquisar
            int bucket = hash(chave, pG);
            posDir = posDir + (bucket * Long.BYTES);
            rafDiretorio.seek(posDir);
            long posBucket = rafDiretorio.readLong();
            
            // vai p/posicao do bucket p/buscar chave e le bucket
            rafBucket.seek(posBucket);
            Bucket bEncontrado = new Bucket(qtdChaves);
            byte[] byteArray = new byte[bEncontrado.getTamBucket()];
            rafBucket.read(byteArray);
            bEncontrado.fromByteArray(byteArray);

            // pesquisa chave no bucket
            int n = bEncontrado.getN();
            int i = 0;
            while( i < n && !sucesso ){
                if(chave == bEncontrado.getChave(i)){ // achou
                    bEncontrado.deletePar(i); // deleta chave do bucket
                    sucesso = true;
                }

                i++;
            }

            bEncontrado.reorganizarChaves();

            // escreve bucket de volta no arquivo
            rafBucket.seek(posBucket);
            byteArray = bEncontrado.toByteArray();
            rafBucket.write(byteArray);
        } catch(FileNotFoundException fnfe){
            System.err.println(fnfe.getMessage());
        } catch(IOException ioe){
            System.err.println(ioe.getMessage());
        }

        return sucesso;
    } 
}

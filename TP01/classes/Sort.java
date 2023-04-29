/** Pacotes **/
package TP01.classes;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.EOFException;
import java.io.RandomAccessFile;
import java.util.ArrayList;

/** Classe Sort **/
public class Sort {
    /* Atributos (constantes) */
    protected final String PATH = "TP01/data/";
    protected final int TAM = 4; // qtd de registros p/bloco
    protected final int WAY = 2; // qtd de caminhos

    /* Getters */
    public String getPATH() {
        return PATH;
    }
    public int getTAM() {
        return TAM;
    }
    public int getWAY() {
        return WAY;
    }

    /* Métodos */
        /* Intercalações balanceadas */
    /**
     * Faz a ordenacao externa por intercalacao balanceada comum
     * @param pathCRUD String do caminho do arquivo original a ser ordenado
     */
    public void intercalaComum(String pathCRUD) {
        createTmpFiles();

        distribuir(pathCRUD);

        intercalar(pathCRUD);

        if(!deleteTmpFiles())
            System.err.println("Erro ao deletar arquivos temporarios");
    }
    /**
     * Faz a ordenacao externa por intercalacao balanceada com blocos de tamanho variavel
     * @param pathCRUD String do caminho do arquivo original a ser ordenado
     */
    public void intercalaBlocosVar(String pathCRUD) {
        System.out.println("Ordenacao nao implementada");
    }
    /**
     * Faz a ordenacao externa por intercalacao balanceada com seleção por substituição
     * @param pathCRUD String do caminho do arquivo original a ser ordenado
     */
    public void intercalaSubstituicao(String pathCRUD) {
        System.out.println("Ordenacao nao implementada");
    }
        /* Auxiliares */
    /**
     * Imprime n duration_ms dos registros válidos do arquivo tmp desejado
     * @param n qtd de registros a serem imprimidos
     * @param tmpPath String do caminho do arquivo tmp
     */
    public void printNDurationMs(String tmpPath, int n) {
        try{
            RandomAccessFile tmp = new RandomAccessFile(tmpPath, "rw");
            long pos, tmpLen = tmp.length();
            int regSize, i = 0;

            // posiciona ponteiro no início, pula cabeçalho e salva posição
            tmp.seek(0); 
            pos = tmp.getFilePointer();
            
            while(pos != tmpLen && i < n){
                // lê primeiros dados
                tmp.skipBytes(1);
                regSize = tmp.readInt();
                
                // lê registro em bytes e converte para objeto 
                byte[] data = new byte[regSize];
                tmp.read(data);
                Musica obj = new Musica();
                obj.fromByteArray(data);

                // imprime duration_ms do registro
                System.out.println(obj.getDuration_ms());

                i++;
                
                pos = tmp.getFilePointer(); // início do próximo registro (lápide)
            }

            tmp.close();
        } catch(FileNotFoundException fnfe){
            
            fnfe.printStackTrace();
        } catch(IOException ioe){
            System.err.println("Erro ao ler registros no arquivo");
            ioe.printStackTrace();
        }
    }
    /**
     * Distribui blocos de Musica nos WAY arquivos temporários, de forma alternada
     * @param pathCRUD String do caminho do arquivo original a ser ordenado 
     */
    private void distribuir(String pathCRUD) {
        RandomAccessFile[] tmp = new RandomAccessFile[WAY];

        // Abre metade dos arquivos tmp 
        try{ 
            for(int i = 0; i < WAY; i++){
                tmp[i] = new RandomAccessFile(PATH + "tmp"+(i+1)+".db", "rw");
                tmp[i].seek(0);
            }
        } catch(FileNotFoundException fnfe){
            System.err.println("Caminho de arquivo temporario nao encontrado");
            fnfe.printStackTrace();
        } catch(IOException ioe){
            System.err.println("Erro de I/O ao buscar no arquivo temporario na distribuicao");
            ioe.printStackTrace();
        }

        // Lê blocos do arquivo CRUD e salva nos temporários de forma intercalada
        try{
            CRUD crud = new CRUD(pathCRUD);
            long posCrud = Integer.BYTES; // posicao atual no arquivo original
            long crudLen = crud.arq.length() - 1;

            while(posCrud < crudLen){ // lê até fim do arquivo 
                // intercala arquivos tmp pra escrever blocos
                for(int i = 0; i < WAY && posCrud < crudLen; i++){
                    // lê um bloco
                    ArrayList<Musica> bloco = crud.getBlock(posCrud, TAM);
                   
                    // ordena em memória principal
                    quicksort( bloco, 0, (bloco.size() - 1) );
                    
                    // escreve bloco no arquivo tmp
                    int j = 0;
                    while(j < bloco.size()){
                        byte[] regByte = bloco.get(j).toByteArray();
                        
                        int regSize = regByte.length;
                        posCrud += 1 + Integer.BYTES + regSize; // atualiza pos no arquivo CRUD
                        
                        tmp[i].writeByte(' ');
                        tmp[i].writeInt(regSize);
                        tmp[i].write(regByte);

                        j++;
                    }
                }
            }

            // fecha tmps
            for(int i = 0; i < WAY; i++){
                tmp[i].close();
            }
        } catch(FileNotFoundException fnfe){
            System.err.println("Arquivo original nao encontrado");
            fnfe.printStackTrace();
        } catch(IOException ioe){
            System.err.println("Erro de I/O ao ler do arquivo original na distribuicao");
            ioe.printStackTrace();
        }
    }
    /**
     * Acha menor registro e escreve no tmp de saida 
     * @param msc ArrayList de Musica
     * @param pos posicao inicial do arquivo tmp
     * @param file String com caminho do arquivo tmp
     * @return long posicao final do arquivo tmp 
     */
    private long merge(ArrayList<Musica> msc , long pos, String file){
        try{
            RandomAccessFile arq = new RandomAccessFile(file, "rw");
            
            // ate lista ficar vazia
            while(!msc.isEmpty()){
                int menor = msc.get(0).getDuration_ms();
                int minIndex = 0;
                
                // procura menor (indice)
                for(int i = 1; i < msc.size(); i++){
                    if(msc.get(i).getDuration_ms() < menor){
                        menor = msc.get(i).getDuration_ms();
                        minIndex = i;
                    }
                }
                
                // escreve registro em bytes
                byte[] mscB = msc.get(minIndex).toByteArray();
                arq.seek(pos);
                arq.writeByte(' ');
                arq.writeInt(mscB.length);
                arq.write(mscB);

                pos = arq.getFilePointer();

                msc.remove(minIndex); // remove menor da lista
            }
            
            arq.close();
        } catch(FileNotFoundException fnfe){
            System.err.println("Erro ao achar o arquivo");
            fnfe.printStackTrace();
        } catch(IOException ioe){
            System.err.print("Erro ao escrever menor registro no arquivo tmp");
            ioe.printStackTrace();
        }

        return pos;
    }
    /**
     * Fase de intercalacao da intercalacao comum, le registros dos arquivos tmp de entrada,
     * ordena e escreve nos tmp de saida, chamando funcao do CRUD que copia registros do 
     * ultimo tmp (ordenado) de volta ao arquivo original 
     * @param pathCRUD String do caminho do arquivo original a ser ordenado 
     */
    private void intercalar(String pathCRUD) {
        try{
            ArrayList<Musica> coluna = new  ArrayList<Musica>();
            CRUD crud = new CRUD(pathCRUD);
            
            // abre tmps
            RandomAccessFile[] tmp = new RandomAccessFile[WAY*2];
            for(int i = 0; i < (WAY*2); i++){
                tmp[i] = new RandomAccessFile(PATH + "tmp"+(i+1)+".db", "rw");
                tmp[i].seek(0);
            }    
            
            int tamBloco = TAM, 
                qtdReg = crud.totalValid();
            int e = 1, s = WAY+1; // indica tmps de entrada/saida
            
            int b = countBlockTmp(tamBloco, e); // qtd de blocos do tmp de entrada
            while(tamBloco < qtdReg){ // aumenta tam de bloco ate que seja um bloco com tds registros 
                // posicoes dos arquivos tmp 
                long[] pos = new long[WAY*2];
                for(int i = 0; i < WAY*2; i++){
                    pos[i] = 0;
                }

                // percorre tds os blocos dos tmps de entrada 
                for(int i = 1; i <= b; i++){
                    int n = 0;

                    // posiciona ponteiros nos blocos dos tmps
                    tmp[e-1].seek(pos[e-1]);
                    tmp[e].seek(pos[e]);
                    tmp[s-1].seek(pos[s-1]);
                    tmp[s].seek(pos[s]);
                    
                    // le bloco de n registros
                    while(n < tamBloco){
                        try {
                            // le registro em bytes dos tmps e converte p/Musica
                            tmp[e-1].skipBytes(1);
                            int regSize1 = tmp[e-1].readInt();
                            Musica msc1 = new Musica();
                            byte[] mscB1 = new byte[regSize1];
                            tmp[e-1].read(mscB1);
                            msc1.fromByteArray(mscB1);
                            coluna.add(msc1);
                            pos[e-1] = tmp[e-1].getFilePointer();

                            tmp[e].skipBytes(1);
                            int regSize2 = tmp[e].readInt();
                            Musica msc2 = new Musica();
                            byte[] mscB2 = new byte[regSize2];
                            tmp[e].read(mscB2);
                            msc2.fromByteArray(mscB2);
                            coluna.add(msc2);
                            pos[e] = tmp[e].getFilePointer();
                            
                            n++;
                        } catch (EOFException eofe) {
                            try {
                                // le registros do tmp que contem mais registros
                                while(n < tamBloco){
                                    tmp[e-1].skipBytes(1);
                                    int regSize1 = tmp[e-1].readInt();
                                    Musica msc1 = new Musica();
                                    byte[] mscB1 = new byte[regSize1];
                                    tmp[e-1].read(mscB1);
                                    msc1.fromByteArray(mscB1);
                                    coluna.add(msc1);  
                                    pos[e-1] = tmp[e-1].getFilePointer();

                                    n++;
                                }
                            } catch (EOFException eofe2) {
                                break;
                            }

                            break;
                        }
                    }
                    
                    // ordena blocos e escreve nos arquivos de saida
                    if(i % WAY == 1){ 
                        pos[s-1] =  merge(coluna, pos[s-1], (PATH + "tmp"+(s)+".db"));
                    } else{
                        pos[s] = merge(coluna, pos[s], (PATH + "tmp"+(s+1)+".db"));
                    } 
                }

                // arquivos de entrada passam a ser de saida e vice-versa
                if(e == 1){ 
                    e = (WAY+1); s = 1;
                } else{ 
                    e = 1; s = (WAY+1);
                }
                
                tamBloco *= WAY; // aumenta tam de bloco
                b = countBlockTmp(tamBloco, e); // qtd de blocos do tmp de entrada
            }

            // arquivo ordenado => tmp (e)
            crud.copyTmp(PATH + "tmp"+(e)+".db");

            // fecha tmps
            for(int i = 0; i < WAY*2; i++){
                tmp[i].close();
            }
        } catch(Exception exc){
            System.err.println("Excecao na intercalacao");
            exc.printStackTrace();
        }
    }
    /**
     * Conta blocos do arquivo tmp
     * @param tamBloco tamanho do bloco (qtd de registros)
     * @param i indica qual tmp
     * @return int qtd de blocos
     */
    private int countBlockTmp(int tamBloco, int i) {
        int b = 0;
        
        try{
            RandomAccessFile tmp = new RandomAccessFile(PATH + "tmp"+i+".db", "rw");
    
            tmp.seek(0); // inicio do tmp
            long pos = 0, tmpLen = tmp.length() - 1;
            
            while(pos < tmpLen){
                try{
                    int n = 0;
                    int regSize;
                    
                    // conta n registros do bloco
                    while(n < tamBloco){
                        tmp.skipBytes(1); // pula a lápide
                        regSize = tmp.readInt();
                        tmp.skipBytes(regSize); // pula registro

                        n++;
                    }

                    pos = tmp.getFilePointer();
                    b++; // conta blocos
                } catch(EOFException eofe){
                    break;
                }
            }

            tmp.close();
        } catch(FileNotFoundException fnfe){
            System.err.println("Caminho de arquivo temporario nao encontrado");
            fnfe.printStackTrace();
        } catch(IOException ioe){
            System.err.println("Erro de leitura ao contar blocos do arquivo temporario");
            ioe.printStackTrace();
        }

        return b;
    }
    /**
     * Cria arquivos temporarios (qtd = WAY*2) 
     */
    private void createTmpFiles() {
        for(int i = 1; i <= WAY*2; i++){
            try{
                RandomAccessFile tmp = new RandomAccessFile(PATH + "tmp"+i+".db", "rw");
                tmp.close();
            } catch(FileNotFoundException fnfe){
                System.err.println("Caminho de arquivo temporario nao encontrado");
                fnfe.printStackTrace();
            } catch(IOException ioe){
                System.err.println("Erro de I/O ao criar arquivo temporario para ordenacao");
                ioe.printStackTrace();
            }
        }
    }
    /**
     * Deleta arquivos temporarios (qtd = WAY*2) 
     * @return true se conseguir deletar, false caso contrario
     */
    private boolean deleteTmpFiles() {
        boolean sucesso = false;

        for(int i = 1; i <= WAY*2; i++){
            File tmp = new File(PATH + "tmp"+i+".db");
        
            if(tmp.delete())
                sucesso = true;
        }

        return sucesso;
    }
    /**
     * Quicksort de um bloco de Musica em memoria principal
     * @param bloco array de Musica
     * @param esq index mais à esquerda (início do array)
     * @param dir index mais à direita (fim do array)
     */
    private void quicksort(ArrayList<Musica> bloco, int esq, int dir) {
        int i = esq, j = dir;
        Musica pivo = bloco.get( (dir + esq) / 2 );

        while(i <= j){
            while(bloco.get(i).getDuration_ms() < pivo.getDuration_ms())
                i++;
            while(bloco.get(j).getDuration_ms() > pivo.getDuration_ms())
                j--;
            
            if(i <= j){
                swap(bloco, i, j);
                
                i++;
                j--;
            }
        }

        if(esq < j)
            quicksort(bloco, esq, j);
        if(i < dir)
            quicksort(bloco, i, dir);
    }
    /**
     * Troca musicas de indices i e j dentro do bloco
     * @param bloco array de Musica
     * @param i index de uma musica
     * @param j index de outra musica
     */
    private void swap(ArrayList<Musica> bloco, int i, int j) {
        Musica tmp = bloco.get(i).clone();

        bloco.set(i, bloco.get(j));
        bloco.set(j, tmp);
    }
}

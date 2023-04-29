/** Pacotes **/
package TP02.classes.indices.arvore;
import java.io.RandomAccessFile;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.File;

/** Classe ArquivoArvore **/
public class ArvoreArq {
    /* Atributos */
    private final String pathArvore = "TP02/data/arvoreB.db"; // path do arquivo
    private RandomAccessFile rafArvore; // arquivo
    private int ordem; // ordem da arvore B
    private ArvoreB arvore;

    /* Construtor */  
    public ArvoreArq() {
        this(8);
    }
    public ArvoreArq(int ordem) {
        this.ordem = ordem;
        this.arvore = new ArvoreB(ordem);
        
        // abrir ou criar arquivo
        try{
            rafArvore = new RandomAccessFile(pathArvore, "rw");
            
            // criar arquivo se nao existir
            if(!exists()){
                // escreve endereco da pagina raiz no arquivo
                rafArvore.seek(0);
                rafArvore.writeLong(-1);
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
            if(rafArvore.length() != 0)
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
            rafArvore.close();
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
        
        File file = new File(pathArvore);
        
        if(file.delete())
            sucesso = true;

        return sucesso;
    }
        /* Funcoes auxiliares */
    /**
     * Imprime a arvore B da forma como esta armazenada em arquivo
     */
    public void print() {
        try{
            // abre arquivo
            rafArvore = new RandomAccessFile(pathArvore, "rw");
            
            // le endereco da raiz e imprime
            rafArvore.seek(0);
            long pontRaiz = rafArvore.readLong();
            System.out.println("0 | raiz = " + pontRaiz + " |");

            // le e imprime paginas
            long pos = rafArvore.getFilePointer();
            while(pos != rafArvore.length()){
                Pagina pag = new Pagina((ordem-1), true, pos);
                byte[] byteArray = new byte[pag.getTamPag()];
                rafArvore.read(byteArray);

                pag.fromByteArray(byteArray);
                System.out.println(pag);

                pos = rafArvore.getFilePointer(); 
            }   
        } catch(IOException ioe){
            System.err.println(ioe.getMessage());
        }   
    }
    /**
     * Escreve arvore B em arquivo, colocando endereco da raiz primeiro, depois as paginas
     * em suas respectivas posicoes (chama funcao recursiva que escreve as paginas)
     */
    private void escreveArvore() {
        try{
            // abre arquivo
            rafArvore = new RandomAccessFile(pathArvore, "rw");
            rafArvore.seek(0);

            // escreve arvore
            if(arvore.getRaiz() == null){ // verifica se raiz existe
                rafArvore.writeLong(-1);
            } else{
                // escreve endereco da raiz
                rafArvore.writeLong(arvore.getRaiz().getPosArq());

                // escreve paginas no arquivo
                escrevePaginas(arvore.getRaiz(), arvore.getRaiz().getPosArq());
            }
        } catch(FileNotFoundException fnfe){
            System.err.println(fnfe.getMessage());
        } catch(IOException ioe){
            System.err.println(ioe.getMessage());
        }
    }
    /**
     * Escreve paginas, recursivamente, em arquivo, passando do objeto Pagina para bytes
     * @param pag Pagina atual
     * @param pos endereco em arquivo da pagina atual
     */
    private void escrevePaginas(Pagina pag, long pos) {
        try{
            // abre arquivo
            rafArvore = new RandomAccessFile(pathArvore, "rw");
            
            if(pag != null){
                byte[] pagByte = pag.toByteArray();
                rafArvore.seek(pos);
                rafArvore.write(pagByte);
                
                // pagina nao eh folha => chama funcao p/filhas 
                if(!pag.isFolha()){
                    for(int j = 0; j < (pag.getN()+1); j++){
                        escrevePaginas(pag.getFilha(j), pag.getFilha(j).getPosArq());
                    }
                }
            }
        } catch(FileNotFoundException fnfe){
            System.err.println(fnfe.getMessage());
        } catch(IOException ioe){
            System.err.println(ioe.getMessage());
        }
    }
    /**
     * Le arvore do arquivo, chamando funcao que le paginas filhas recursivamente, se tiver
     */
    private void leArvore() {
        try{
            // abre arquivo
            rafArvore = new RandomAccessFile(pathArvore, "rw");
            
            // le endereco da raiz
            rafArvore.seek(0);
            long pontRaiz = rafArvore.readLong();

            if(pontRaiz == -1){ // verifica se raiz existe
                arvore.setRaiz(null);
            } else{
                // le raiz
                Pagina raiz = new Pagina((ordem-1), true, pontRaiz);
                byte[] byteArray = new byte[raiz.getTamPag()];
                rafArvore.seek(pontRaiz);
                rafArvore.read(byteArray);
                raiz.fromByteArray(byteArray);
                arvore.setRaiz(raiz);

                // raiz nao eh folha => le paginas filhas
                if(!arvore.getRaiz().isFolha()){
                    for(int i = 0; i < (arvore.getRaiz().getN()+1); i++){
                        lePaginasFilhas(arvore.getRaiz().getFilha(i));
                    }
                }
            }
        } catch(FileNotFoundException fnfe){
            System.err.println(fnfe.getMessage());
        } catch(IOException ioe){
            System.err.println(ioe.getMessage());
        }
    }
    /**
     * Le paginas filhas, recursivamente, do arquivo
     * @param pag Pagina atual
     */
    private void lePaginasFilhas(Pagina pag){
        try{
            // abre arquivo
            rafArvore = new RandomAccessFile(pathArvore, "rw");
            
            if(pag != null){
                // le pagina
                byte[] byteArray = new byte[pag.getTamPag()];
                rafArvore.seek(pag.getPosArq());
                rafArvore.read(byteArray);
                pag.fromByteArray(byteArray);
                
                // pagina nao eh folha => le paginas filhas
                if(!pag.isFolha()){
                    for(int i = 0; i < (pag.getN()+1); i++){
                        lePaginasFilhas(pag.getFilha(i));
                    }
                }
            }
        } catch(FileNotFoundException fnfe){
            System.err.println(fnfe.getMessage());
        } catch(IOException ioe){
            System.err.println(ioe.getMessage());
        }
    }
    /**
     * Pesquisa chave nas paginas filhas, recursivamente, retornando o endereco da chave
     * @param pag Pagina atual
     * @param chave int (ID) a ser pesquisado
     * @return long endereco no arquivo de dados da chave 
     */    
    private long pesquisaFilhas(Pagina pag, int chave){
        long endereco = -1;

        try{
            // abre arquivo
            rafArvore = new RandomAccessFile(pathArvore, "rw");
            
            if(pag != null){
                // le pagina
                byte[] byteArray = new byte[pag.getTamPag()];
                rafArvore.seek(pag.getPosArq());
                rafArvore.read(byteArray);
                pag.fromByteArray(byteArray);

                // procura chave na pagina
                int i = 0; 
                boolean achou = false, saiu = false;
                while(i < pag.getN() && !achou && !saiu){
                    if(chave == pag.getChave(i)){ 
                        achou = true;
                        endereco = pag.getEndereco(i);
                    } else if(chave > pag.getChave(i)){ 
                        i++;
                    } else{ 
                        saiu = true;
                    }
                }

                // nao achou na pag e ela nao eh folha => pesquisa nas paginas filhas
                if(!achou && !pag.isFolha()){
                    endereco = pesquisaFilhas(pag.getFilha(i), chave);
                }
            }
        } catch(FileNotFoundException fnfe){
            System.err.println(fnfe.getMessage());
        } catch(IOException ioe){
            System.err.println(ioe.getMessage());
        }

        return endereco;
    }
        /* CRUD do indice */
    /**
     * Insere par chave e endereco no indice em arvore B, lendo arvore atual do arquivo,
     * inserindo, em memoria primaria, o par na posicao correta e escrevendo arvore de volta
     * no arquivo 
     * @param chave int (ID) a ser inserido
     * @param endereco long posicao no arquivo de dados da chave
     * @return true se conseguir inserir, false caso contrario
     */
    public boolean create(int chave, long endereco) {
        boolean sucesso = false;

        try{
            // abre arquivo
            rafArvore = new RandomAccessFile(pathArvore, "rw");
            
            // le endereco da raiz
            rafArvore.seek(0);
            long pontRaiz = rafArvore.readLong();
            
            // se arvore nao estiver vazia, le do arquivo
            if(pontRaiz != -1){
                leArvore(); 
            }
            
            // faz insercao na arvore
            sucesso = arvore.inserir(chave, endereco);
            
            // escreve de volta no arquivo
            escreveArvore();
        } catch(FileNotFoundException fnfe){
            System.err.println(fnfe.getMessage());
        } catch(IOException ioe){
            System.err.println(ioe.getMessage());
        }

        return sucesso;
    }
    /**
     * Pesquisa chave na arvore, buscando qual pagina ela deve estar e retornando endereco 
     * da chave no arquivo de dados
     * @param chave int (ID) a ser pesquisado
     * @return long endereco no arquivo de dados da chave 
     */
    public long read(int chave) {
        long endereco = -1;

        try{
            // abre arquivo
            rafArvore = new RandomAccessFile(pathArvore, "rw");
            
            // le endereco da raiz
            rafArvore.seek(0);
            long pontRaiz = rafArvore.readLong();

            if(pontRaiz == -1){ // verifica se raiz existe
                System.err.println("Indice usando Arvore B esta vazio.");
            } else{
                // le raiz
                Pagina raiz = new Pagina((ordem-1), true, pontRaiz);
                byte[] byteArray = new byte[raiz.getTamPag()];
                rafArvore.seek(pontRaiz);
                rafArvore.read(byteArray);
                raiz.fromByteArray(byteArray);

                // procura chave na raiz
                int i = 0; 
                boolean achou = false, saiu = false;
                while(i < raiz.getN() && !achou && !saiu){
                    if(chave == raiz.getChave(i)){ 
                        achou = true;
                        endereco = raiz.getEndereco(i);
                    } else if(chave > raiz.getChave(i)){ 
                        i++;
                    } else{ 
                        saiu = true;
                    }
                }

                // nao achou na raiz e ela nao eh folha => pesquisa nas paginas filhas
                if(!achou && !raiz.isFolha()){
                    endereco = pesquisaFilhas(raiz.getFilha(i), chave);
                }
            }
        } catch(FileNotFoundException fnfe){
            System.err.println(fnfe.getMessage());
        } catch(IOException ioe){
            System.err.println(ioe.getMessage());
        }

        return endereco;
    }
    /**
     * Remove par chave e endereco do indice em arvore B, lendo arvore atual do arquivo,
     * removendo, em memoria primaria, o par da posicao correta e escrevendo arvore de volta
     * no arquivo 
     * @param chave int (ID) a ser removido
     * @return true se conseguir remover, false caso contrario
     */
    public boolean delete(int chave) {
        boolean sucesso = false;

        try{
            // abre arquivo
            rafArvore = new RandomAccessFile(pathArvore, "rw");
            
            // le endereco da raiz
            rafArvore.seek(0);
            long pontRaiz = rafArvore.readLong();
            
            // se arvore nao estiver vazia, faz delete
            if(pontRaiz != -1){
                // le arvore do arquivo
                leArvore(); 
                
                // faz remocao na arvore
                sucesso = arvore.remover(chave);
                
                // escreve de volta no arquivo
                escreveArvore();
            } else{
                System.out.println("Arvore esta vazia, nao foi possivel deletar.");
            }
        } catch(FileNotFoundException fnfe){
            System.err.println(fnfe.getMessage());
        } catch(IOException ioe){
            System.err.println(ioe.getMessage());
        }

        return sucesso;
    }
}

/** Pacotes **/
package TP02.classes.indices.arvore;
import java.lang.Math;

/** Classe ArvoreB **/
public class ArvoreB {
    /* Atributos */
    private int nMax; // qtd max de chaves
    private Pagina raiz; // pagina raiz
    
    /* Getters e Setters */
    public Pagina getRaiz() {
        return raiz;
    }
    public void setRaiz(Pagina raiz) {
        this.raiz = raiz;
    }

    /* Construtor */  
    public ArvoreB(int ordem) {
        this.nMax = (ordem - 1);
        this.raiz = null;
    }

    /* Metodos */
        /* Basicos */
    /**
     * Imprime a arvore 
     */
    public void print() {
        printPagina(raiz);
    }
    /**
     * Imprime paginas da arvore, fazendo caminhamento recursivamente
     * @param pag Pagina atual
     */
    private void printPagina(Pagina pag) {
        if(pag != null){
            System.out.println(pag);

            if(!pag.isFolha()){
                for(int i = 0; i < (pag.getN()+1); i++){
                    printPagina(pag.getFilha(i));
                }
            }
        }
    }
    /**
     * Seta as posicoes em arquivo das paginas existentes
     * @param pag Pagina atual
     * @param pos valor da posicao em arquivo em que a pagina atual sera armazenada
     * @return long pos da proxima pagina
     */
    private long setarPosArq(Pagina pag, long pos) {
        if(pag != null){
            pag.setPosArq(pos);

            if(!pag.isFolha()){
                for(int i = 0; i < (pag.getN()+1); i++){
                    pos = setarPosArq(pag.getFilha(i), (pos + pag.getTamPag()));
                }
            }
        }

        return pos;
    }
        /* Manipulacao da Arvore */
    /**
     * Remove par chave e endereco na arvore B. Pesquisa chave primeiro, verificando se 
     * existe. Depois, testa casos de remocao, fazendo passos necessarios dependendo da 
     * situacao. 
     * @param chave identificador a ser removido
     * @return true se conseguir remover, false caso contrario
     */
    public boolean remover(int chave) {
        boolean removeu = false;
        
        // pesquisa pagina de onde chave deve ser removida
        Pagina pag = pesquisar(raiz, chave);
        if(pag != null){ // chave existe
            if(pag.isFolha()){ // pagina eh folha
               
                if(pag.equals(raiz)){ // remocao de uma raiz folha: remove direto
                    raiz.remover(chave);
                    removeu = true;
                    // raiz ficou vazia
                    if(raiz.getN() == 0) raiz = null;

                } else if(pag.getN() > (nMax/2)){  // mantem ocupacao min: remove direto
                    pag.remover(chave);
                    removeu = true;
                
                } else{ // NAO mantem ocupacao min
                    Pagina pagIrma = getIrma(raiz, pag, chave);

                    if(pagIrma.getN() > (nMax/2)){
                        removeu = true;
                    } else{
                        System.err.println("Esse tipo de remocao da arvore nao foi implementado.");
                    }
                }

            } else{ // pagina NAO eh folha
                
                // busca antecessor da chave
                int pos = pag.pesquisar(chave);
                Pagina maiorEsq = getMaiorFilhaEsq(pag.getFilha(pos));
                int maiorN = maiorEsq.getN();

                // verifica se maiorEsq mantem ocupacao min
                if(maiorN > (nMax/2)){
                    // substitui chave a remover pela maior da esquerda
                    pag.setChave(pos, maiorEsq.getChave(maiorN-1));
                    pag.setEndereco(pos, maiorEsq.getEndereco(maiorN-1));
                    maiorEsq.setChave(maiorN-1, -1);
                    maiorEsq.setEndereco(maiorN-1, -1);
                    maiorEsq.setN(maiorN-1);

                    removeu = true;
                } else{
                    System.err.println("Esse tipo de remocao da arvore nao foi implementado.");
                }

            }
                
            setarPosArq(raiz, Long.BYTES);
        }

        return removeu;
    }
    /**
     * Retorna chave irma posterior a pagina procurada (ou anterior se procurada for ultima)
     * @param pai Pagina pai
     * @param procurada Pagina que se quer encontrar a irma
     * @param chave identificador a pesquisar
     * @return Pagina irma da procurada
     */
    private Pagina getIrma(Pagina pai, Pagina procurada, int chave) {
        Pagina irma = null;

        if(pai != null){
            // procura chave na pagina
            int i = 0; 
            boolean achou = false, saiu = false;
            while(i < pai.getN() && !achou && !saiu){
                if(chave == pai.getChave(i)){ 
                    achou = true;
                } else if(chave > pai.getChave(i)){ 
                    i++;
                } else{ 
                    saiu = true;
                }
            }

            if(!achou && !pai.getFilha(i).equals(procurada)){
                irma = pesquisar(pai.getFilha(i), chave);
            } else{
                if(i == (pai.getN()+1)){
                    irma = pai.getFilha(i-1);
                } else{
                    irma = pai.getFilha(i+1);
                }

                int irmaN = irma.getN();
                // verifica se irma mantem ocupacao min
                if(irmaN > (nMax/2)){
                    // substitui chave a remover pela da irma
                    int chaveIrma = irma.getChave(0);
                    long endIrma = irma.getEndereco(0);
                    
                    if(chaveIrma < chave){
                        chaveIrma = irma.getChave(irmaN-1); 
                        endIrma = irma.getEndereco(irmaN-1);
                    }
                    
                    int pos = procurada.pesquisar(chave);
                    procurada.setChave(pos, pai.getChave(i));
                    procurada.setEndereco(pos, pai.getEndereco(i));
                    pai.setChave(i, chaveIrma);
                    pai.setEndereco(i,endIrma);
                    irma.remover(chaveIrma);
                }
            } 
        }

        return irma;
    }
    /**
     * Procura pagina filha que tem os maiores valores a esquerda
     * @param pag Pagina atual
     * @return Pagina maior a esquerda
     */
    private Pagina getMaiorFilhaEsq(Pagina pag) {
        if(pag != null){
            // procura pagina mais a direita
            int i = 0;
            while(pag.getFilha(i) != null){
                i++;
            }

            if(pag.getFilha(i) != null)
                pag = getMaiorFilhaEsq(pag.getFilha(i));
        }

        return pag;
    }
    /**
     * Pesquisa chave na arvore, recursivamente passando pelas paginas onde chave pode estar
     * @param pag Pagina atual
     * @param chave identificador a pesquisar
     * @return Pagina onde chave pode estar
     */
    private Pagina pesquisar(Pagina pag, int chave) {
        if(pag != null){
            // procura chave na pagina
            int i = 0; 
            boolean achou = false, saiu = false;
            while(i < pag.getN() && !achou && !saiu){
                if(chave == pag.getChave(i)){ 
                    achou = true;
                } else if(chave > pag.getChave(i)){ 
                    i++;
                } else{ 
                    saiu = true;
                }
            }

            // nao achou na pagina e ela nao eh folha => pesquisa nas paginas filhas
            if(!achou && !pag.isFolha()){
                pag = pesquisar(pag.getFilha(i), chave);
            }
        }

        return pag;
    }
    /**
     * Insere par chave e endereco na arvore B. Se raiz nao existir, cria e insere nela.
     * Caso contrario, verifica se raiz esta cheia e faz split antes de inserir internamente
     * ou insere internamente direto se raiz tiver espaco (nao precisa fazer split p/mudar 
     * raiz) 
     * @param chave identificador a ser inserido
     * @param endereco posicao em arquivo da chave
     * @return true se conseguir inserir, false caso contrario
     */
    public boolean inserir(int chave, long endereco) {
        boolean inseriu = false;

        if(raiz == null){ // raiz nao existe => cria raiz
            raiz = new Pagina(nMax, true);
        } else if(raiz.getN() == nMax){ // raiz existe mas esta cheia
            // acha pagina filha onde chave pode ficar
            int posFilha = raiz.acharPosFilha(chave);
            Pagina filha = raiz.getFilha(posFilha);
            
            // raiz eh folha ou a filha tambem ta cheia => cria nova raiz
            if(filha != null && filha.getN() == nMax || raiz.isFolha())
                criaNovaRaiz(chave);
        } 

        // insere internamente (procura pagina folha)
        inseriu = inserir(raiz, chave, endereco);
        setarPosArq(raiz, Long.BYTES);

        return inseriu;
    }
    /**
     * Busca posicao de insercao (pagina folha) e insere par, fazendo split a partir
     * das paginas pai e filha quando necessario, para inserir em sequencia
     * @param pag Pagina atual 
     * @param chave identificador a ser inserido
     * @param endereco posicao em arquivo da chave
     * @return true se conseguir inserir, false caso contrario
     */
    private boolean inserir(Pagina pag, int chave, long endereco) {
        boolean inseriu = false;

        if(pag.isFolha()){ // pagina eh folha => insere par
            pag.inserir(chave, endereco);
            inseriu = true;
        } else{ // pagina nao eh folha
            // acha pagina filha onde chave pode ficar
            int posFilha = pag.acharPosFilha(chave);
            Pagina filha = pag.getFilha(posFilha);

            if(filha.getN() == nMax){ // nao tem espaco na pagina filha
                // faz split se necessario (nao tem nenhuma folha com espaco)
                boolean fezSplit = split(pag, filha, posFilha, chave);

                // pagina filha onde chave fica mudou (chave eh maior q a q "subiu" no split)
                if(fezSplit && chave > pag.getChave(posFilha)){
                    posFilha++;
                    filha = pag.getFilha(posFilha);
                }
            }

            // tem espaco na pagina filha (ja tinha ou fez split e liberou)
            inseriu = inserir(filha, chave, endereco);
        }

        return inseriu;
    }
    /**
     * Cria nova raiz, tornando a raiz atual filha da nova e fazendo split
     * @param chave identificador a ser inserido
     */
    private void criaNovaRaiz(int chave) {
        // cria nova raiz (raiz atual vira filha da nova)
        Pagina novaRaiz = new Pagina(nMax, false);
        novaRaiz.setFilha(0, raiz);
        raiz = novaRaiz;

        // faz split 
        split(raiz, raiz.getFilha(0), 0, chave);
    }
    /**
     * Verifica condicoes de split na arvore B, se existir alguma folha que tem espaco nao 
     * faz split (retorna falso), caso contrario faz, recursivamente, a partir da pagina pai
     * @param pai Pagina pai 
     * @param filha Pagina filha
     * @param posFilha posicao da Pagina filha
     * @param chave identificador a ser inserido
     * @return true se fizer split, false se nao
     */
    private boolean split(Pagina pai, Pagina filha, int posFilha, int chave) {
        boolean fazSplit = true;
        
        // nao esta criando nova raiz (n = 0) e filha nao eh folha
        if(pai.getN() != 0 && !filha.isFolha()){
            // acha pagina neta onde chave pode ficar
            int posNeta = filha.acharPosFilha(chave);
            Pagina neta = filha.getFilha(posNeta);

            // verifica, recursivamente, se alguma neta tem espaco (logo folha tem espaco)
            if(neta != null && neta.getN() == nMax){
                fazSplit = split(filha, neta, posNeta, chave);
            } else{ // neta tem espaco
                fazSplit = false;
            }
        }
        
        // pode fazer split a partir do pai (nao esta cheio)
        if(fazSplit && pai.getN() < nMax){
            split(pai, filha, posFilha);
        }

        return fazSplit;
    }
    /**
     * Faz split na arvore B, reorganizando chaves entre as paginas pai, filha e a nova
     * @param pai Pagina pai 
     * @param filha Pagina filha
     * @param posFilha posicao da Pagina filha
     */
    private void split(Pagina pai, Pagina filha, int posFilha) {
        // cria nova pagina filha
        Pagina novaFilha = new Pagina(nMax, filha.isFolha());
        int meio = (int) Math.ceil(nMax / 2.0);

        // copia chaves maiores da pagina filha (meio ate o fim) p/nova filha
        for(int i = 0; i < (meio-1); i++){
            // insere pares chave e endereco na novaFilha
            novaFilha.setChave(i, filha.getChave(i+meio));
            novaFilha.setEndereco(i, filha.getEndereco(i+meio));
            // remove pares chave e endereco da filha
            filha.setChave(i+meio, -1);
            filha.setEndereco(i+meio, -1);
        }
        // atualiza qtd de chaves na filha e novaFilha
        novaFilha.setN(meio-1);
        filha.setN(meio);

        // pagina filha nao eh folha (tem ponteiros de filhas p/copiar)
        if(!filha.isFolha()){
            // copia ponteiros das filhas da pagina filha (meio ate o fim) p/nova filha
            for(int i = 0; i < meio; i++){
                novaFilha.setFilha(i, filha.getFilha(i+meio));
                filha.setFilha(i+meio, null);
            } 
        }

        // liga nova filha ao pai
        pai.setFilha((posFilha+1), novaFilha);

        // insere pares chave e endereco no pai (sobe c/o do meio)
        pai.setChave(posFilha, filha.getChave(meio-1));
        pai.setEndereco(posFilha, filha.getEndereco(meio-1));
        // remove pares chave e endereco da filha
        filha.setChave((meio-1), -1);
        filha.setEndereco((meio-1), -1);
        // atualiza qtd de chaves
        pai.setN(pai.getN()+1); 
        filha.setN(filha.getN()-1);
    }
}

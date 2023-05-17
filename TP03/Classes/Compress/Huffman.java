package TP03.Classes.Compress;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;

class Node implements Comparable<Node> {

    //Parecido com uma arvore binario, porem tem o elemento frequencia
    private final int frequencia;
    private Node leftNode;
    private Node rightNode;

    /**
     * Construtor para folhas
     * @param leftNode no esquerdo
     * @param rightNode no direito
     */
    public Node(Node leftNode, Node rightNode) {
        this.leftNode = leftNode;
        this.rightNode = rightNode;
        this.frequencia = leftNode.getFrequencia() + rightNode.getFrequencia(); //frequencia é calculado pela soma da frequencia dos filhos 
    }

    /**
     * Construtor para raiz
     * @param frequencia int 
     */
    public Node(int frequencia) {
        this.frequencia = frequencia;
    }


    /** 
     * Retorna a frequencia do no
     * @return int frequencia
     */
    private int getFrequencia() {
        return this.frequencia;
    }

    /**
     * Compara a frequencia de dois nos
     * @param node no a ser comparado
     * @return int comparacao
     */
    @Override
    public int compareTo(Node node) {
        return Integer.compare(frequencia, node.getFrequencia());
    }


    /** 
     * Retorna o filho esquerdo do no
     * @return Node leftNode
     */
    public Node getLeftNode() {
        return this.leftNode;
    }

    /** 
     * Retorna o filho direito do no
     * @return Node rightNode
     */
    public Node getRightNode() {
        return this.rightNode;
    }

}

class Leaf extends Node {

    private final byte value; //valor armazenado na folha


    /** 
     * Construtor para folhas
     * @param value byte
     * @param frequencia int
     */
    public Leaf(byte value, int frequencia) { //construtor com o valor e a frequencia para a folha
        super(frequencia);
        this.value = value;
    }


    /**
     * Retorna o valor da folha
     * @return byte valor
     */
    public byte getValue() {
        return this.value;
    }

    /** 
     * Retorna o valor da folha como Byte
     * @return Byte valor
     */
    public Byte getCharacter() { // retorna o valor da folha como Byte(se for byte da problema com o Map)
        return this.value;
    }

}

public class Huffman { //Huffman é a classe principal e a arvore
    private Node root;
    private byte[] data;
    private Map<Byte, Integer> byteFrequencies; //Mapa de frequencia de cada byte
    private Map<Byte, String> huffmanCodes; //Mapa de codigos de huffman



    public Huffman(){
        //construtor vazio
    }

    /**
     * Construtor da classe Huffman
     * @param data array de bytes
     */
    public Huffman(byte[] data) {
        this.data = data;
        fillByteFrequencies();  
        this.huffmanCodes = new HashMap<>();
    }

    /**
     * Retorna o mapa de codigos de huffman
     * @return Map<Byte, String> huffmanCodes (por meio de referencia)
     */
    private void fillByteFrequencies() {    //preenche o mapa de frequencia
        byteFrequencies = new HashMap<>();
        for (Byte b : data) {  //percorre o array de bytes
            Integer integer = byteFrequencies.get(b);
            byteFrequencies.put(b, integer == null ? 1 : integer + 1);  //se o byte ja estiver no mapa, incrementa a frequencia, se nao, adiciona com frequencia 1
        }
    }

    /**
     * Retorna o mapa de frequencia
     * @return Map<Byte, Integer> byteFrequencies
     */
    public byte[] compress() {
        Queue<Node> queue = new PriorityQueue<>();  //fila de prioridade para armazenar os nos

        byteFrequencies.forEach((b, frequencia) -> queue.add(new Leaf(b, frequencia)));  //adiciona as folhas na fila de prioridade

        while (queue.size() > 1) { 
            queue.add(new Node(queue.poll(), queue.poll()));  //pega os dois menores nos da fila e cria um novo no com eles, adicionando na fila
        }
        generateHuffmanCodes(root = queue.poll(), "");  //gera os codigos de huffman
        return getEncodedBytes();
    }

        /**
     * Retorna o mapa de frequencia
     * @return Map<Byte, Integer> byteFrequencies
     */
    public byte[] compress(byte[] data) {
        this.data = data;
        Queue<Node> queue = new PriorityQueue<>();  //fila de prioridade para armazenar os nos

        byteFrequencies.forEach((b, frequencia) -> queue.add(new Leaf(b, frequencia)));  //adiciona as folhas na fila de prioridade

        while (queue.size() > 1) { 
            queue.add(new Node(queue.poll(), queue.poll()));  //pega os dois menores nos da fila e cria um novo no com eles, adicionando na fila
        }
        generateHuffmanCodes(root = queue.poll(), "");  //gera os codigos de huffman
        return getEncodedBytes();
    }

    /**
     * Retorna o mapa de codigos de huffman
     * @return Map<Byte, String> huffmanCodes (por meio de referencia)
     */
    private void generateHuffmanCodes(Node node, String code) {
        if (node instanceof Leaf) {  //se for uma folha, adiciona o codigo no mapa de codigos
            huffmanCodes.put(((Leaf) node).getCharacter(), code);  
            return;
        }
        generateHuffmanCodes(node.getLeftNode(), code + "0");  //se nao for uma folha, chama a funcao para os filhos, adicionando 0 para o filho da esquerda e 1 para o filho da direita
        generateHuffmanCodes(node.getRightNode(), code + "1");
    }

    /**
     * Retorna o array de bytes codificado
     * @return byte[] encodedBytes
     */
    private byte[] getEncodedBytes() {  
        StringBuilder stringBuilder = new StringBuilder();
        for (byte b : data) {
            stringBuilder.append(huffmanCodes.get(b));  
        }
        String encodedText = stringBuilder.toString();
        int extraPadding = 8 - (encodedText.length() % 8); //Calcula o numero de bits de preenchimento
        StringBuilder paddingBuilder = new StringBuilder(); 
        for (int i = 0; i < extraPadding; i++) {
            paddingBuilder.append("0");
        }
        String padding = paddingBuilder.toString();
        encodedText += padding; // Adiciona os bits de preenchimento
        int numBytes = encodedText.length() / 8;  //calcula o numero de bytes
        byte[] encodedBytes = new byte[numBytes];
        for (int i = 0; i < numBytes; i++) {
            String byteString = encodedText.substring(i * 8, (i + 1) * 8);  //pega os bytes de 8 em 8
            encodedBytes[i] = (byte) Integer.parseInt(byteString, 2);
        }
        return encodedBytes;
    }

    /**
     * Retorna o array de bytes decodificado
     * @return byte[] encodedBytes
     */
    public byte[] decompress(byte[] encodedBytes) {
        StringBuilder stringBuilder = new StringBuilder();
        for (byte b : encodedBytes) {
            stringBuilder.append(String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0')); //transforma o byte em uma string de bits
        }
        String encodedText = stringBuilder.toString();
        int numPaddingBits = encodedText.charAt(encodedText.length() - 1) - '0'; // Ultimo digito e o numero de bits de preenchimento
        encodedText = encodedText.substring(0, encodedText.length() - numPaddingBits - 1); // Remove os bits de preenchimento
        Node node = root;
        List<Byte> decompressedBytes = new ArrayList<>();
        for (char character : encodedText.toCharArray()) {  //percorre a string de bits
            node = character == '0' ? node.getLeftNode() : node.getRightNode();  //se for 0 vai para o filho da esquerda, se nao, para o filho da direita
            if (node instanceof Leaf) {  //se for uma folha, adiciona o valor no array de bytes e volta para a raiz
                decompressedBytes.add(((Leaf) node).getCharacter());
                node = root;
            }
        }
        byte[] decompressedArray = new byte[decompressedBytes.size()];
        for (int i = 0; i < decompressedBytes.size(); i++) {  //converte o arraylist para array
            decompressedArray[i] = decompressedBytes.get(i);
        }
        return decompressedArray;
    }


    /**
     * Retorna o array de bytes do arquivo
     * @param filePath
     * @return byte[] buffer 
     * @throws IOException
     */
    public static byte[] getBytesFromFile(String filePath) throws IOException {  //converte o arquivo para array de bytes (isso que resolve o problema de aumentar o arquivo)
        File file = new File(filePath);
        byte[] buffer = new byte[(int) file.length()];
        
        try (InputStream input = new FileInputStream(file)) {
            int bytesRead = input.read(buffer);
            if (bytesRead != buffer.length) {
                throw new IOException("Could not read entire file.");
            }
        }
        
        return buffer;
    }

    /**
     * Cria o arquivo a partir do array de bytes
     * @param filePath String
     * @param buffer byte[]
     * @throws IOException 
     */
    public static void BytestoFile(String filePath, byte[] buffer) throws IOException {  //converte o array de bytes para arquivo (criar o arquivo)
        File file = new File(filePath);
        try (OutputStream outputStream = new FileOutputStream(file)) {
            outputStream.write(buffer);
        }
        
    }

    /**
     * @param filePath caminho do arquivo 
     * @param n numero de vezes que o arquivo sera comprimido
     * @return long duration - tempo de compressao de todos os arquivos
     * @throws IOException
     */
    public long EncodeFinal(String filePath , int n) throws IOException {

        long startTime = 0;  //variaveis para calcular o tempo de compressao
        long endTime = 0;
        long duration = 0;
        long durationTotal = 0;

        String original = filePath.substring(0, filePath.length() - 3);  //pega o nome do arquivo original, sem o .db

        byte[] originalFile = getBytesFromFile(filePath);  //pega o arquivo original em bytes
        long tamanhoOriginal = (originalFile).length;  //pega o tamanho do arquivo original
        System.out.println("Tamanho do arquivo original: " + tamanhoOriginal + " bytes");

        for(int i = 0; i < n; i++){  //comprime o arquivo n vezes
            

            byte[] data = getBytesFromFile(filePath);  //pega o arquivo n em bytes
            Huffman huffman = new Huffman(data); 

            startTime = System.currentTimeMillis(); //comeca a contar o tempo de compressao

            byte[] encodedBytes = huffman.compress();  //comprime o arquivo

            endTime = System.currentTimeMillis();  //termina de contar o tempo de compressao
            duration = (endTime - startTime);  //soma o tempo de compressao de cada arquivo
            durationTotal += duration;  //soma o tempo de compressao de todos os arquivos

            BytestoFile(original + "HuffmanEncode" + (i + 1) + ".db", encodedBytes);

            System.out.println("Tempo de compressão de numero " + (i + 1) + ": " + duration + " ms");
            
            filePath = original + "HuffmanEncode" + (i + 1) + ".db";  //muda o caminho do arquivo para o comprimir o novo arquivo

            System.out.println("Tamanho do arquivo comprimido de numero " + (i + 1) + ": " + (encodedBytes).length + " bytes");
            System.out.println("Taxa de compressão de numero " + (i + 1) + ": " + ((float) (encodedBytes).length / tamanhoOriginal * 100 + "%\n"));
            
        }

        return durationTotal;

    }

    /**
     * @param filePath  caminho do arquivo
     * @param n  numero de vezes que o arquivo sera descomprimido
     * @return  long duration - tempo de descompressao de todos os arquivos
     * @throws IOException
     */    
    public long DecodeFinal(String filePath , int n) throws IOException {

        long startTime = 0;  //variaveis para calcular o tempo de descompressao
        long endTime = 0;
        long duration = 0;
        long durationTotal = 0;

        String original = filePath.substring(0, filePath.length() - 3);  //pega o nome do arquivo original, sem o .db
        byte[] originalFile = getBytesFromFile(filePath);  //pega o arquivo original em bytes
        long tamanhoOriginal = (originalFile).length;  //pega o tamanho do arquivo original

        System.out.println("Tamanho do arquivo original: " + tamanhoOriginal + " bytes");

        for(int i = 0; i < n; i++){  //descomprime o arquivo n vezes
            byte[] test = getBytesFromFile(original + "HuffmanEncode" + (i + 1) + ".db");  //pega o arquivo n em bytes

            if (test.length == 0) {  //se o arquivo nao existir, retorna -1
                System.out.println("Arquivo " + (i + 1) + " não existe");  
                return -1;   
            }
            else{ //
                byte[] data = getBytesFromFile(filePath);  //pega o arquivo n em bytes
                Huffman huffman = new Huffman(data);  
    
                byte[] encodedBytes = huffman.compress();  //se n fizer desse jeito da NullPointerException
                //BytestoFile(original + "HuffmanEncode" + (n - i) + ".db", encodedBytes);
    
                startTime = System.currentTimeMillis();  //comeca a contar o tempo de descompressao
                
                byte[] decodedBytes = huffman.decompress(encodedBytes);  //descomprime o arquivo 
    
                endTime = System.currentTimeMillis();  //termina de contar o tempo de descompressao
                duration = (endTime - startTime);  //soma o tempo de descompressao de cada arquivo
                durationTotal += duration;  //soma o tempo de descompressao de todos os arquivos
    
                BytestoFile(original + "HuffmanDecode" + (i + 1)  + ".db", decodedBytes);  //cria o arquivo n descomprimido
                System.out.println("Tempo de descompressão de numero " + (i + 1)  + ": " + duration + " ");
    
    
                filePath = original + "HuffmanEncode" + (i + 1 )  + ".db";  //muda o caminho do arquivo para o descomprimir o novo arquivo
    
                System.out.println("Tamanho do arquivo descomprimido de numero " + (i + 1)  + ": " + (decodedBytes).length + " bytes");
                System.out.println("Taxa de descompressão de numero " + (i + 1)  + ": " + (float) (decodedBytes).length / (float)data.length* 100 + "%\n");
            }

        }

        return durationTotal;

    }
    
    /**
     * @param filePath  caminho do arquivo
     * @param n  numero arquivos que serao deletados
     * @throws IOException
     */
    public void DeleteAllFiles(String filePath , int n) throws IOException {

        String original = filePath.substring(0, filePath.length() - 3);  //pega o nome do arquivo original, sem o .db
        for(int i = 0; i < n; i++){  //deleta todos os arquivos comprimidos criados
            File delete = new File(original + "HuffmanEncode" + (i + 1) + ".db");
            delete.delete();
        }

        for(int i = 0; i < n; i++){  //deleta todos os arquivos descomprimidos criados
            File delete = new File(original + "HuffmanDecode" + (i + 1) + ".db");
            delete.delete();
        }
    }

}    

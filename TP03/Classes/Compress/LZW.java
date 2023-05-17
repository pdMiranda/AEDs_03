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

public class LZW {

    /**
     * Codifica uma string para uma lista de inteiros
     * @param texto
     * @return List<Integer>
     */
    public static List<Integer> encode(String texto) { // retorna uma lista de inteiros com os codigos de cada caracter

        int dictSize = 256; // tamanho do dicionario (tabela acsii)
        Map<String, Integer> dicionario = new HashMap<String, Integer>(); // dicionario com os caracteres e seus codigos

        for (int i = 0; i < dictSize; i++) { // inicializa o dicionario com os caracteres da tabela ascii (n é muito util para arquivo binario)
            dicionario.put(String.valueOf((char) i), i);
        }

        String codigo = "";
        List<Integer> encode = new ArrayList<Integer>();
        for (char character : texto.toCharArray()) { // percorre o texto
            String codigoAux = codigo + character;
            if (dicionario.containsKey(codigoAux)) { // se o dicionario ja contem o codigo, adiciona o caracter ao codigo                                                    
                codigo = codigoAux;
            } else { // senao, adiciona o codigo no dicionario e adiciona o codigo do caracter atual
                     // na lista de codigos
                encode.add(dicionario.get(codigo));
                dicionario.put(codigoAux, dictSize++);
                codigo = String.valueOf(character);
            }
        }
        if (!codigo.isEmpty()) { // adiciona o ultimo codigo na lista
            encode.add(dicionario.get(codigo));
        }

        return encode;
    }

    /**
     * Decodifica uma lista de codigos para uma string
     * @param encodedText
     * @return String
     */
    public static String decode(List<Integer> encodedText) {

        int dictSize = 256;
        Map<Integer, String> dicionario = new HashMap<Integer, String>();

        for (int i = 0; i < dictSize; i++) {
            dicionario.put(i, String.valueOf((char) i));
        }

        String codigo = String.valueOf((char) encodedText.remove(0).intValue()); // pega o primeiro codigo e transforma em caracter
                                                                                
        StringBuffer decode = new StringBuffer(codigo); // adiciona o caracter no buffer

        for (int code : encodedText) { // percorre a lista de codigos

            String entrada = dicionario.containsKey(code) ? dicionario.get(code) : (codigo + codigo.charAt(0));  // se o dicionario contem o codigo, pega a palavra correspondente, senao, pega a palavra atual + o primeiro caracter da palavra atual
            decode.append(entrada);

            dicionario.put(dictSize++, codigo + entrada.charAt(0)); // adiciona a palavra atual + o primeiro caracter da palavra atual no dicionario

            codigo = entrada;
        }
        return decode.toString();
    }

    /**
     * Codifica um arquivo
     * @param bytes  byte[] - bytes do arquivo
     * @return  byte[] - bytes do arquivo codificado
     */
    public static byte[] encode(byte[] bytes) {

        int dictSize = 256;
        Map<String, Integer> dicionario = new HashMap<String, Integer>();
    
        for (int i = 0; i < dictSize; i++) {
            dicionario.put(String.valueOf((char) i), i);
        }
    
        String codigo = "";
        List<Integer> encode = new ArrayList<Integer>();
        for (byte b : bytes) {
            char character = (char) (b & 0xFF);
            String codigoAux = codigo + character;
            if (dicionario.containsKey(codigoAux)) {
                codigo = codigoAux;
            } else {
                encode.add(dicionario.get(codigo));
                dicionario.put(codigoAux, dictSize++);
                codigo = String.valueOf(character);
            }
        }
        if (!codigo.isEmpty()) {
            encode.add(dicionario.get(codigo));
        }
    
        byte[] encodedBytes = new byte[encode.size()];
        for (int i = 0; i < encode.size(); i++) {
            encodedBytes[i] = (byte) (int) encode.get(i);
        }
    
        return encodedBytes;
    }
    
    /**
     * Decodifica -- nao esta sendo usado
     * @param encodedBytes  byte[] - bytes do arquivo
     * @return  byte[] - bytes do arquivo decodificado
     */

     /* 
    public static byte[] decode(byte[] encodedBytes) {
    
        int dictSize = 256;
        Map<Integer, String> dicionario = new HashMap<Integer, String>();
    
        for (int i = 0; i < dictSize; i++) {
            dicionario.put(i, String.valueOf((char) i));
        }
    
        List<Integer> encodedText = new ArrayList<Integer>();
        for (byte b : encodedBytes) {
            encodedText.add((int) b & 0xFF);
        }
    
        String codigo = String.valueOf((char) encodedText.remove(0).intValue());
        StringBuffer decode = new StringBuffer(codigo);
    
        for (int code : encodedText) {
    
            String entrada = dicionario.containsKey(code) ? dicionario.get(code) : (codigo + codigo.charAt(0));
            decode.append(entrada);
    
            dicionario.put(dictSize++, codigo + entrada.charAt(0));
    
            codigo = entrada;
        }
        return decode.toString().getBytes();
    }
    */

    /**
     * Decodifica -- nao esta sendo usado
     * @param encodedBytes  byte[] - bytes do arquivo
     * @return  byte[] - bytes do arquivo decodificado
     */
    public static byte[] decode(byte[] encodedBytes) {

        List<Integer> encodedText = listFromBytes(encodedBytes);
        int dictSize = 256;
        Map<Integer, String> dicionario = new HashMap<Integer, String>();

        for (int i = 0; i < dictSize; i++) {
            dicionario.put(i, String.valueOf((char) i));
        }

        String codigo = String.valueOf((char) encodedText.remove(0).intValue()); // pega o primeiro codigo e transforma em caracter
                                                                                
        StringBuffer decode = new StringBuffer(codigo); // adiciona o caracter no buffer

        for (int code : encodedText) { // percorre a lista de codigos

            String entrada = dicionario.containsKey(code) ? dicionario.get(code) : (codigo + codigo.charAt(0));  // se o dicionario contem o codigo, pega a palavra correspondente, senao, pega a palavra atual + o primeiro caracter da palavra atual
            decode.append(entrada);

            dicionario.put(dictSize++, codigo + entrada.charAt(0)); // adiciona a palavra atual + o primeiro caracter da palavra atual no dicionario

            codigo = entrada;
        }
        return decode.toString().getBytes();
    }

    
    /**
     * Transforma um arquivo em uma string
     * @param io  String - nome do arquivo
     * @return String  - conteudo do arquivo
     */
    public static String arqToString(String io) {
        try {
            FileInputStream inputFile = new FileInputStream(io);
            String texto = "";
            int byteRead;
            while ((byteRead = inputFile.read()) != -1) {
                char c = (char) byteRead;
                texto += c;
            }
            inputFile.close();
            return texto;
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
        return "";
    }

    /**
     * Transforma uma List<Integer> em um arquivo
     * @param out  String - nome do arquivo
     * @param texto  List<Integer> - lista de inteiros
     */
    public static long stringToArq(String out, List<Integer> texto) {
        try {
            long tamanho = 0;
            FileOutputStream outputFile = new FileOutputStream(out);
            for (int i = 0; i < texto.size(); i++) {
                outputFile.write(texto.get(i));
            }
            tamanho = outputFile.getChannel().size();
            outputFile.close();
            return tamanho;
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
        return 0;
    }

    /**
     * Transforma um arquivo em uma lista de inteiros
     * @param io  String - nome do arquivo
     * @return  List<Integer> - lista de inteiros
     */
    public static List<Integer> arqToList(String io) {
        try {
            FileInputStream inputFile = new FileInputStream(io);
            List<Integer> texto = new ArrayList<Integer>();
            int byteRead;
            while ((byteRead = inputFile.read()) != -1) {
                texto.add(byteRead);
            }
            inputFile.close();
            return texto;
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
        return null;
    }

    /**
     * Transforma uma string em uma lista de inteiros
     * @param texto  String - texto a ser transformado em lista de inteiros
     * @return  List<Integer> - lista de inteiros
     */
    public static List<Integer> stringToList(String texto){
        List<Integer> lista = new ArrayList<Integer>();
        for (int i = 0; i < texto.length(); i++) {
            lista.add((int) texto.charAt(i));
        }
        return lista;
    }

    /**
     * Transforma uma lista de inteiros em um array de bytes
     * @param texto  List<Integer> - lista de inteiros a serem transformados em array de bytes
     * @return  byte[] - array de bytes
     */
    public static byte[] bytesFromList(List<Integer> texto){
        byte[] bytes = new byte[texto.size()];
        for (int i = 0; i < texto.size(); i++) {
            bytes[i] = texto.get(i).byteValue();
        }
        return bytes;
    }

    /**
     * Transforma um array de bytes em uma lista de inteiros
     * @param bytes  byte[] - bytes a serem transformados em lista
     * @return  List<Integer> - lista de inteiros
     */
    public static List<Integer> listFromBytes(byte[] bytes){
        List<Integer> lista = new ArrayList<Integer>();
        for (int i = 0; i < bytes.length; i++) {
            lista.add((int) bytes[i]);
        }
        return lista;
    }

    /**
     * Transforma uma string em um array de bytes
     * @param texto  String - texto a ser transformado em bytes
     * @return  byte[] - bytes do texto
     */
    public static byte[] stringToByte(String texto){
        byte[] bytes = new byte[texto.length()];
        for (int i = 0; i < texto.length(); i++) {
            bytes[i] = (byte) texto.charAt(i);
        }
        return bytes;
    }

    /**
     * Transforma um array de bytes em uma string
     * @param bytes  byte[] - bytes a serem transformados em texto
     * @return  String - texto  
     */
    public static String byteToString(byte[] bytes){
        String texto = "";
        for (int i = 0; i < bytes.length; i++) {
            texto += (char) bytes[i];
        }
        return texto;
    }

    /**
     * Transforma uma string em um arquivo
     * @param out  String - caminho do arquivo
     * @param texto  String - texto a ser escrito no arquivo
     * @return  long - tamanho do arquivo
     */
    public static long stringToArq(String out, String texto) {
        try {
            long tamanho = 0;
            FileOutputStream outputFile = new FileOutputStream(out);
            for (int i = 0; i < texto.length(); i++) {
                outputFile.write(texto.charAt(i));
            }
            tamanho = outputFile.getChannel().size();
            outputFile.close();
            return tamanho;
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
        return 0;
    }

    /**
     * Transforma um arquivo em um array de bytes
     * @param filePath  String - caminho do arquivo  
     * @return  byte[] - array de bytes do arquivo
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
     * Comprime o arquivo
     * @param filePath String
     * @param n int
     * @return long
     * @throws IOException 
     */
    public long EncodeFinal(String filePath, int n) throws IOException {

        long startTime = 0; // variaveis para calcular o tempo de execucao
        long endTime = 0;
        long duration = 0;

        long durationTotal = 0;

        String original = filePath.substring(0, filePath.length() - 3); // pega o nome do arquivo sem a extensao .db

        byte[] originalFile = getBytesFromFile(filePath); // transforma o arquivo em uma string
        long tamanhoOriginal = (originalFile).length; // pega o tamanho do arquivo original
        System.out.println("Tamanho do arquivo original: " + tamanhoOriginal + " bytes");

        for (int i = 0; i < n; i++) { // comprime o arquivo n vezes

            byte[] data = getBytesFromFile(filePath); // transforma o arquivo em uma string

            startTime = System.currentTimeMillis(); // pega o tempo de inicio da compressao
            byte[] encodedText = LZW.encode(data); // comprime a string

            endTime = System.currentTimeMillis(); // pega o tempo de fim da compressao
            duration = (endTime - startTime); // calcula o tempo de compressao

            durationTotal += duration;  // calcula o tempo total de compressao

            System.out.println("Tempo de compressão de numero " + (i + 1) + ": " + duration + " ms");

            BytestoFile(original + "LZWEncode" + (i + 1) + ".db", encodedText); // transforma a lista de codigos em um arquivo 

            filePath = original + "LZWEncode" + (i + 1) + ".db"; // pega o caminho do novo arquivo n a ser comprimido

            System.out.println("Tamanho do arquivo comprimido de numero " + (i + 1) + ": " + encodedText.length + " bytes");
            System.out.println("Taxa de compressão de numero " + (i + 1) + ": "+ ((float) (encodedText).length / tamanhoOriginal * 100 + "%\n"));

        }

        return durationTotal;
    }


    /**
     * Descomprime o arquivo
     * @param filePath String
     * @param n int
     * @return long
     * @throws IOException 
     */
    public long DecodeFinal(String filePath , int n) throws IOException{

        long startTime = 0;  //variaveis para calcular o tempo de descompressao
        long endTime = 0;
        long duration = 0;
        long durationTotal = 0;

        String original = filePath.substring(0, filePath.length() - 3);

        String originalFile = arqToString(filePath);
        long tamanhoOriginal = (originalFile).length();
        System.out.println("Tamanho do arquivo original: " + tamanhoOriginal + " bytes");

        for(int i = 0; i < n; i++){

            byte[] test = getBytesFromFile(original + "LZWEncode" + (i + 1) + ".db");
            if(test.length > 0){
                String data = arqToString(filePath);

                List<Integer> encodedText = LZW.encode(data);
                stringToArq(original + "LZWEncode" + (i + 1) + ".db", encodedText);
    
                startTime = System.currentTimeMillis();  //comeca a contar o tempo de descompressao
                String decodedText = LZW.decode(encodedText);
                endTime = System.currentTimeMillis();  //termina de contar o tempo de descompressao
                stringToArq(original + "LZWDecode" + (i + 1) + ".db",decodedText);
    
                duration = (endTime - startTime);  //soma o tempo de descompressao de cada arquivo
                durationTotal += duration;  //soma o tempo de descompressao de todos os arquivos
                filePath = original + "LZWEncode" + (i + 1) + ".db";
    
                System.out.println("Tempo de descompressão de numero " + (i + 1) + ": " + duration + " ms");
                System.out.println("Tamanho do arquivo descomprimido de numero " + (i + 1) + ": " + decodedText.length()+ " bytes");
                System.out.println("Taxa de descompressão de numero " + (i + 1) + ": " + (float) (decodedText).length() / data.length() * 100 + "%\n");
            }
            else{
                return -1;
            }

            
            
        }
        return durationTotal;

    }
    

    /* 
    public long DecodeFinal(String filePath , int n) throws IOException{

        long startTime = 0;  //variaveis para calcular o tempo de descompressao
        long endTime = 0;
        long duration = 0;
        long durationTotal = 0;

        String original = filePath.substring(0, filePath.length() - 3);

        String originalFile = arqToString(filePath);
        long tamanhoOriginal = (originalFile).length();
        System.out.println("Tamanho do arquivo original: " + tamanhoOriginal + " bytes");

        for(int i = 0; i < n; i++){

            byte[] test = getBytesFromFile(original + "LZWEncode" + (i + 1) + ".db");
            if(test.length > 0){
                byte[] data = getBytesFromFile(filePath);

                byte[] encodedText = LZW.encode(data);
                BytestoFile(original + "LZWEncode" + (i + 1) + ".db", encodedText);
    
                startTime = System.currentTimeMillis();  //comeca a contar o tempo de descompressao
                byte[] decodedText = LZW.decode(encodedText);
                endTime = System.currentTimeMillis();  //termina de contar o tempo de descompressao
                BytestoFile(original + "LZWDecode" + (i + 1) + ".db",decodedText);
    
                duration = (endTime - startTime);  //soma o tempo de descompressao de cada arquivo
                durationTotal += duration;  //soma o tempo de descompressao de todos os arquivos
                filePath = original + "LZWEncode" + (i + 1) + ".db";
    
                System.out.println("Tempo de descompressão de numero " + (i + 1) + ": " + duration + " ms");
                System.out.println("Tamanho do arquivo comprimido de numero " + (i + 1) + ": " + decodedText.length+ " bytes");
                System.out.println("Taxa de compressão de numero " + (i + 1) + ": " + (float) (decodedText).length / data.length * 100 + "%\n");
            }
            else{
                return -1;
            }

            
            
        }
        return durationTotal;

    }
    */
    
    /**
     * @param filePath Caminho do arquivo
     * @param n        Numero de arquivos a serem deletados
     */
    public void DeleteAllFiles(String filePath, int n) {
        String original = filePath.substring(0, filePath.length() - 3);

        for (int i = 0; i < n; i++) { // deleta os arquivos comprimidos
            File delete = new File(original + "LZWEncode" + (i + 1) + ".db");
            delete.delete();
        }

        for (int i = 0; i < n; i++) { // deleta os arquivos descomprimidos
            File delete = new File(original + "LZWDecode" + (i + 1) + ".db");
            delete.delete();
        }
    }
}

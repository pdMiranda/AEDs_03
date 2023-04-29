package TP03.Classes.Compress;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LZW{


    /**
     * Codifica uma string para uma lista de inteiros
     * @param texto
     * @return List<Integer>
     */
    public static List<Integer> encode(String texto){  //retorna uma lista de inteiros com os codigos de cada caracter

        int dictSize = 256;  //tamanho do dicionario (tabela acsii)
        Map<String,Integer> dicionario = new HashMap<String,Integer>();  //dicionario com os caracteres e seus codigos

        for(int i = 0; i < dictSize; i++){  //inicializa o dicionario com os caracteres da tabela ascii (n é muito util para arquivo binario)
            dicionario.put(String.valueOf((char)i), i);
        }

        String codigo = "";   
        List<Integer> encode = new ArrayList<Integer>();
        for(char character : texto.toCharArray()){  //percorre o texto
            String codigoAux = codigo + character;  
            if(dicionario.containsKey(codigoAux)){  //se o dicionario ja contem o codigo, adiciona o caracter ao codigo
                codigo = codigoAux;
            }else{  //senao, adiciona o codigo no dicionario e adiciona o codigo do caracter atual na lista de codigos
                encode.add(dicionario.get(codigo));
                dicionario.put(codigoAux, dictSize++);
                codigo = String.valueOf(character);
            }
        }
        if(!codigo.isEmpty()){  //adiciona o ultimo codigo na lista
            encode.add(dicionario.get(codigo));
        }

        return encode;
    }

    /**
     * Decodifica uma lista de codigos para uma string
     * @param encodedText 
     * @return String
     */
    public static String decode(List<Integer> encodedText){

        int dictSize = 256;
        Map<Integer,String> dicionario = new HashMap<Integer,String>();  

        for(int i = 0; i < dictSize; i++){  
            dicionario.put(i, String.valueOf((char)i));  
        }

        String codigo = String.valueOf((char) encodedText.remove(0).intValue());   //pega o primeiro codigo e transforma em caracter 
        StringBuffer decode = new StringBuffer(codigo);  //adiciona o caracter no buffer 

        for(int code : encodedText){  //percorre a lista de codigos

            String entrada = dicionario.containsKey(code) ? dicionario.get(code) : (codigo + codigo.charAt(0));  //se o codigo estiver no dicionario, pega a palavra correspondente, senao, pega a palavra atual + o primeiro caracter da palavra atual
            decode.append(entrada);

            dicionario.put(dictSize++, codigo + entrada.charAt(0));  //adiciona a codigo atual + o primeiro caracter da palavra atual no dicionario

            codigo = entrada;  
        }
        return decode.toString();
    }


    /**
     * Transforma um arquivo em uma string
     * @param io
     * @return String
     */
    public static String arqToString(String io){  
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
     * @param out 
     * @param texto 
     */
    public static long stringToArq(String out, List<Integer> texto){
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
     * Transforma uma string em um arquivo
     * @param out
     * @param texto
     */
    public static long stringToArq(String out, String texto){
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


    public long EncodeFinal(String filePath , int n){
        
        long desp = 0;
        long aux1 = 0; 
        long aux2 = 0;

        long startTimeTotal = System.currentTimeMillis();

        String original = filePath.substring(0, filePath.length() - 3);
        
        String originalFile = arqToString(filePath);
        long tamanhoOriginal = (originalFile).length();
        System.out.println("Tamanho do arquivo original: " + tamanhoOriginal + " bytes");

        for(int i = 0; i < n; i++){
           
            String data = arqToString(filePath);

            Long startTime = System.currentTimeMillis();

            List<Integer> encodedText = LZW.encode(data);

            Long endTime = System.currentTimeMillis();
            Long duration = (endTime - startTime);
            System.out.println("Tempo de compressão de numero " + (i + 1) + ": " + duration + " ms");

            stringToArq(original + "LZWEncode" + (i + 1) + ".db", encodedText);

            aux1 = System.currentTimeMillis();
            String decodedText = LZW.decode(encodedText);
            stringToArq(original + "LZWDecode" + (i + 1) + ".db",decodedText);
            aux2 = System.currentTimeMillis();
            desp += aux2 - aux1;

            filePath = original + "LZWEncode" + (i + 1) + ".db";

            System.out.println("Tamanho do arquivo comprimido de numero " + (i + 1) + ": " + encodedText.size() + " bytes");
            System.out.println("Taxa de compressão de numero " + (i + 1) + ": " + (float) (encodedText).size() / tamanhoOriginal * 100 + "%\n");
            
        }

        long endTimeTotal = System.currentTimeMillis();
        long durationTotal = (endTimeTotal - startTimeTotal);

        for(int i = 0; i < n; i++){
            File delete = new File(original + "LZWDecode" + (i + 1) + ".db");
            delete.delete();
        }

        return durationTotal - desp;
    }

    public long DecodeFinal(String filePath , int n){
        long desp = 0;
        long aux1 = 0;
        long aux2 = 0;


        long startTimeTotal = System.currentTimeMillis();

        String original = filePath.substring(0, filePath.length() - 3);

        String originalFile = arqToString(filePath);
        long tamanhoOriginal = (originalFile).length();
        System.out.println("Tamanho do arquivo original: " + tamanhoOriginal + " bytes");

        for(int i = 0; i < n; i++){
            aux1 = System.currentTimeMillis();
            String data = arqToString(filePath);
            List<Integer> encodedText = LZW.encode(data);

            stringToArq(original + "LZWEncode" + (i + 1) + ".db", encodedText);

            aux2 = System.currentTimeMillis();
            desp += aux2 - aux1;
            long startTime = System.currentTimeMillis();
            
            String decodedText = LZW.decode(encodedText);

            long endTime = System.currentTimeMillis();
            long duration = (endTime - startTime);
            System.out.println("Tempo de descompressão de numero " + (i + 1) + ": " + duration + " ");

            stringToArq(original + "LZWDecode" + (i + 1) + ".db",decodedText);

            filePath = original + "LZWEncode" + (i + 1) + ".db";

            System.out.println("Tamanho do arquivo comprimido de numero " + (i + 1) + ": " + decodedText.length()+ " bytes");
            System.out.println("Taxa de compressão de numero " + (i + 1) + ": " + (float) (decodedText).length() / data.length() * 100 + "%\n");
            
        }

        long endTimeTotal = System.currentTimeMillis();
        long durationTotal = (endTimeTotal - startTimeTotal);
        
        return durationTotal - desp;

    }

    public void DeleteAllFiles(String filePath , int n){
        String original = filePath.substring(0, filePath.length() - 3);

        for(int i = 0; i < n; i++){
            File delete = new File(original + "LZWEncode" + (i + 1) + ".db");
            delete.delete();
        }

        for(int i = 0; i < n; i++){
            File delete = new File(original + "LZWDecode" + (i + 1) + ".db");
            delete.delete();
        }
    }
}
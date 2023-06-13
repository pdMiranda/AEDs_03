package TP05.Classes.Criptografia;

import TP05.Classes.Musica;

public class Vigenere {

    private static final String key = "AEDs_III";  //chave para criptografia/descritografia

    /**
     * Criptografa uma string usando a cifra de Vigenere
     * @param original  string a ser criptografada
     * @param key     chave para criptografia
     * @return  string criptografada
     */
    public static String encrypt(String original, String key) {
        StringBuilder ciphertext = new StringBuilder();  //string criptografada
        int keyIndex = 0;  //indice da chave
        for (char c : original.toCharArray()) {  //para cada caractere da string original
            if (Character.isLetter(c)) {
                int shift = key.charAt(keyIndex) - 'a';  //deslocamento
                char shifted = (char) (((c - 'a' + shift) % 26) + 'a');  //caractere deslocado
                ciphertext.append(shifted);  //adiciona caractere deslocado a string criptografada
                keyIndex = (keyIndex + 1) % key.length();  //atualiza indice da chave
            } else {
                ciphertext.append(c);  //adiciona caractere a string criptografada
            }
        }
        return ciphertext.toString();
    }

    /**
     * Descriptografa uma string usando a cifra de Vigenere
     * @param ciphertext  string a ser descriptografada
     * @param key   chave para descriptografia
     * @return  string descriptografada
     */
    public static String decrypt(String ciphertext, String key ) {
        StringBuilder original = new StringBuilder();  //string descriptografada
        int keyIndex = 0;  //indice da chave
        for (char c : ciphertext.toCharArray()) {  //para cada caractere da string criptografada
            if (Character.isLetter(c)) {
                int shift = key.charAt(keyIndex) - 'a';  //deslocamento
                char shifted = (char) (((c - 'a' - shift + 26) % 26) + 'a');  //caractere deslocado
                original.append(shifted);  //adiciona caractere deslocado a string descriptografada
                keyIndex = (keyIndex + 1) % key.length();  //atualiza indice da chave
            } else {
                original.append(c);  //adiciona caractere a string descriptografada
            }
        }
        return original.toString();

    }
    
    /**
     * @param obj  objeto a ser criptografado
     */
    public static void encryptAll(Musica obj){
        obj.setTrack_id(encrypt(obj.getTrack_id(), key));
    }

    /**
     * @param obj  objeto a ser descriptografado
     */
    public static void decryptAll(Musica obj){
        obj.setTrack_id(decrypt(obj.getTrack_id(), key));
    }

}
package TP05.Classes.Criptografia;

import TP05.Classes.Musica;

public class Ceaser {

    private static final int key = 42;

    /**
     * Criptografa uma string usando a cifra de Ceaser
     * @param original  string a ser criptografada
     * @param key  chave para criptografia
     * @return  string criptografada
     */
    private static String encrypt(String original, int key) {
        StringBuilder ciphertext = new StringBuilder();
        for (char ch : original.toCharArray()) {
            if (Character.isLetter(ch)) {
                char shifted = (char) (((ch - 'a' + key) % 26) + 'a');
                ciphertext.append(shifted);
            } else {
                ciphertext.append(ch);
            }
        }
        return ciphertext.toString();
    }

    /**
     * Descriptografa uma string usando a cifra de Ceaser
     * @param ciphertext  string a ser descriptografada
     * @param key  chave para descriptografia
     * @return  string descriptografada
     */
    private static String decrypt(String ciphertext, int key) {
        StringBuilder original = new StringBuilder();  //string descriptografada
        for (char ch : ciphertext.toCharArray()) {  //para cada caractere da string criptografada
            if (Character.isLetter(ch)) {
                char shifted = (char) (((ch - 'a' - key + 26) % 26) + 'a');  //caractere deslocado
                original.append(shifted);
            } else {
                original.append(ch);  //adiciona caractere a string descriptografada
            }
        }
        return original.toString();  //retorna string descriptografada
    }

    public static void encrypt(Musica obj) {
        obj.setTrack_id(encrypt(obj.getTrack_id(), key));
    }

    public static void decrypt(Musica obj) {
        obj.setTrack_id(decrypt(obj.getTrack_id(), key));
    }

}

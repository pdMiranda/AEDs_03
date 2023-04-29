# TP01

Criação da base de dados e Ordenação Externa

- Link do vídeo: <https://youtu.be/43CDepMKqUg>
- Decisões de implementação:
    - Criação de um ID numérico para os registros no Create (base possuía ID em formato String)
    - Ao fazer Update, quando o registro diminui ou aumenta de tamanho, é colocado no fim do arquivo e o ID é atualizado (antigo é deletado por meio da lápide)
    - Apenas uma das três ordenações externas foi implementada (intercalação balanceada comum)
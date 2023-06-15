# TP05

Criptografia

- Link do vídeo: <https://youtu.be/xEYCwVe_TGs>
- Observações sobre a implementação:
  - Foram implementados os seguintes algoritmos de criptografia: Cifra de César, Cifra de Vigenère e Cifra de Colunas, cada um utilizando de chaves de criptografia diferentes.
  - Ao fazer manipulações de CRUD em algum registro (Música), o campo track_id é criptografado quando é escrito no arquivo .db e descriptografado quando é lido do arquivo para mostrar ao usuário.
  - Cada classe relativa a um dos algoritmos de criptografia possui um CRUD interno (que é o mesmo código em todos) e serve para criar um arquivo para cada criptografia e permitir a descriptografia de acordo com o nome do arquivo.

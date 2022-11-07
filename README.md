# Como utilizar

Os arquivos keys.backup.txt e keys.backup.2.txt são apenas para salvar as mensagens enviadas durante o tabalho. Por isto estes arquivos possuem mais variáveis do que o arquivo Keys.txt, já que estas variáveis serão geradas pelo programa durante a rotina de execução.
Enquanto o arquivo keys.txt é o de fato utilizado pelo programa, deve ser limpo antes de executar a primeira etapa.

## 1 - Primeiro passo, gerar chaves

Este projeto possui um arquivo chamado keys.txt, este arquivo é responsável por entrada e saída de informações do programa.

Para utilizar o mesmo, primeiramente atualize o arquivo keys.txt com os valores

- p_hex: B10B8F96 A080E01D DE92DE5E AE5D54EC 52C99FBC FB06A3C6 9A6A9DCA 52D23B61 6073E286 75A23D18 9838EF1E 2EE652C0 13ECB4AE A9061123 24975C3C D49B83BF ACCBDD7D 90C4BD70 98488E9C 219A7372 4EFFD6FA E5644738 FAA31A4F F55BCCC0 A151AF5F 0DC8B4BD 45BF37DF 365C1A65 E68CFDA7 6D4DA708 DF1FB2BC 2E4A4371
- g_hex: A4D1CBD5 C3FD3412 6765A442 EFB99905 F8104DD2 58AC507F D6406CFF 14266D31 266FEA1E 5C41564B 777E690F 5504F213 160217B4 B01B886A 5E91547F 9E2749F4 D7FBD7D3 B9A92EE1 909D0D22 63F80A76 A6A24C08 7A091F53 1DBF0A01 69B6A28A D662A4D1 8E73AFA3 2D779D59 18D08BC8 858F4DCE F97C2A24 855E6EEB 22B3B2E5

Agora apenas execute o programa -se estiver no linux ou mac- utilizando o script bash run.sh.

Após a exeução, podemos perceber a presença de novas informações no arquivo keys.txt

- p: A mesma variável anterior, porem agora em formato decimal
- g: A mesma variável anterior, porem agora em formato decimal
- a: Valor BigInteger aleatório com pelo menos 30 dígitos
- A: Resultado do cálculo g^a mod p
- A_HEX: A mesma variável anterior (A) em formato hexadecimal

## 2 - Segundo passo, interpretar B e calcular chave entre pares

Ao enviar a variável (A) ao par, o mesmo retorna o valor B e a mensagem cifrada (c).
Permitindo então adicionar no arquivo keys.txt as seguintes variáveis:

- B_HEX: Valor calculado pelo par.
- c: Mensagem cifrada

Executando o programa novamente, como o mesmo já possui as variáveis a, A e A_HEX, as mesmas não seria criadas novamente e será mantido as presentes no arquivo.
Após a segunda execução, as variáveis aparecerão no arquivo.

- V: Valor calculado através do valor recebido do par em (B^a mod p)
- V_HEX: Mesmo valor acima, porem em formato decimal
- K: Chave a ser utilizada pelos dois lados
- B: Valor (B_HEX) convertido em decimal
- c_PADDED: Mensagem cifrada separada do IV
- IV: Vetor de inicialização da mensagem cifrada
- m: Mensagem decifrada
- ml: (m') é a mensagem decifrada invertida
- IVl: É o vetor de inicialização a ser utilizado na (c')
- cl_PADDED: É a mensagem resposta cifrada sem o IV.
- cl: (c') É a mensagem resposta já cifrada com o IV ao início, pronta para ser enviada.

### Final das execuções

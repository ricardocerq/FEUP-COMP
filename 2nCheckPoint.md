##Summary

###O que é que já temos feito:
* Análise Lexical, Sintática, Semântica
* Geração de código
* Operações entre autómatos
* Ler autómatos, escrever automatos em ficheiros dot
* Desenhar automotos no ecrã
* Operações: Concatenação, Interceção, Negação, Inverso, União, XOR, kleene star, Conversão para DFA e minimização de DFA
* Conversão de expressões regulares para Automatos
* Como é que os Automatos são implementados: O estado é um inteiro. Existe um hashMap(de strings para arraylists de arraylists de inteiros) que para cada simbolo de entrada dá uma estrutura. Essa estrutura faz uma associação desse simbolo para cada estado. Resultando os estados resultantes após a aplicação desse simbolo.
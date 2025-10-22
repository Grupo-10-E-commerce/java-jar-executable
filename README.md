# java-jar-executable
# java-jar-executable

Repositório contendo o artefato executável (JAR) da aplicação Java desenvolvida pelo Grupo 10 – E-commerce.

## 📌 Visão Geral

Este projeto tem como objetivo gerar um **arquivo JAR executável** que encapsula a aplicação Java, facilitando a execução em diferentes ambientes.  
Ele foi desenvolvido com foco em:

- Modularização da aplicação Java  
- Gerenciamento de dependências  
- Empacotamento em JAR para fácil implantação  
- Distribuição simples e rápida para ambientes de produção ou testes  

## 🧱 Tecnologias Utilizadas

- Java (versão: X.Y ou conforme definida no build)  
- Sistema de build: Maven ou Gradle (especifique)  
- Dependências externas (listar principais, se houver)  
- Empacotamento: JAR executável (*“fat JAR” ou *uber-jar*)  


## 🚀 Como Executar

### 1. Pré-requisitos  
- Instalar Java JDK (versão compatível)  
- Ter o arquivo JAR gerado (localizado em `jar-fraux/` ou conforme build)  
- (Opcional) Variáveis de ambiente configuradas se a aplicação requerer  

### 2. Executar o JAR  
```bash
java -jar caminho/para/o/arquivo.jar


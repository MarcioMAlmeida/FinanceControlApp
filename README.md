# 📱 Finance Control App - Android (Jetpack Compose)

![Kotlin](https://img.shields.io/badge/Kotlin-B125EA?style=for-the-badge&logo=kotlin&logoColor=white)
![Android Studio](https://img.shields.io/badge/Android%20Studio-3DDC84.svg?style=for-the-badge&logo=android-studio&logoColor=white)
![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white)

Um aplicativo nativo Android desenvolvido para controle de finanças pessoais, consumindo uma API RESTful proprietária. O foco deste projeto é aplicar os conceitos modernos de desenvolvimento Android recomendados pelo Google, garantindo um código limpo, reativo e altamente testável.

> 🔗 **Nota:** Este é o front-end mobile. A API Back-End (Java/Spring Boot) que alimenta este aplicativo foi construída do zero por mim e está disponível [[Neste Repositório AQUI](https://github.com/MarcioMAlmeida/Finance-control-api)].

## 📸 Demonstração do App

<div align="center">
  <img width="250" src="https://github.com/user-attachments/assets/5ff09d76-8a7f-4b1b-bc98-bf7f8377afbc" />&nbsp;&nbsp;<img width="250" src="https://github.com/user-attachments/assets/5f4aeb59-2bca-4786-bf7b-7cc9b56465ad" />&nbsp;&nbsp;<img width="250" src="https://github.com/user-attachments/assets/8bf38157-d05b-45b2-9da8-2cef3172c31b" />
</div>

## 🏗️ Arquitetura e Padrões

O projeto foi rigorosamente estruturado utilizando a arquitetura **MVVM (Model-View-ViewModel)** em conjunto com o padrão de **Clean Architecture** para a separação de responsabilidades.

* **UI Layer:** Construída 100% de forma declarativa com **Jetpack Compose**. As telas (`Screens`) são burras e apenas observam os estados (`StateFlow`).
* **Presentation Layer:** Gerenciada por **ViewModels** (AndroidViewModel), responsáveis por processar as regras de negócio visuais e manter o estado seguro em casos de mudança de configuração.
* **Data/Network Layer:** Integração com a API usando **Retrofit** e desserialização via **Gson**, com interceptores customizados para injeção de tokens JWT. Gerenciamento de persistência local para a sessão do usuário (`TokenManager`).

## 🛠️ Tecnologias e Bibliotecas

* **Linguagem:** Kotlin
* **UI Toolkit:** Jetpack Compose (Material Design 3)
* **Navegação:** Compose Navigation (`NavHost`)
* **Assincronismo:** Kotlin Coroutines & Flows (`StateFlow`, `MutableStateFlow`)
* **Rede:** Retrofit2, OkHttp3
* **Persistência Local:** SharedPreferences (Para tokens de autenticação)

## 🧪 Qualidade de Código e Testes

Um dos pilares deste projeto é a garantia de qualidade através de **Testes Unitários**. Toda a camada de apresentação e regras de negócio foi coberta.

* **Ferramentas:** JUnit 4, **MockK** (para Mocks 100% nativos em Kotlin) e **Turbine** (para testes sequenciais de fluxos reativos do StateFlow).
* **Cobertura:** * Gerenciamento de sessão e proteção de rotas (Login, Logout, Expiração 401/403).
    * Comportamentos de UI e validações locais formulários.
    * Tratamento avançado de exceções HTTP e quedas de rede.
    * Verificação de conversões de tipo seguras (Prevenção de `NumberFormatException`).

## 🚀 Como executar este projeto

### Pré-requisitos
* Android Studio Ladybug (ou mais recente).
* API Backend rodando localmente na mesma rede ou hospedada em nuvem.

### Passos
1. Faça o clone do repositório:
   ```bash
   git clone [https://github.com/MarcioMAlmeida/Finance-Control-App.git](https://github.com/MarcioMAlmeida/Finance-Control-App.git)

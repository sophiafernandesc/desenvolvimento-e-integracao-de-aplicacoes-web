**Atividade 01 — 🌦️API REST de Clima**

Disciplina: **Desenvolvimento e Integração de Aplicações Web**

API REST desenvolvida em Java com Spring Boot para consultar informações meteorológicas de cidades utilizando a API Open-Meteo.

## 👥 Integrantes
- **Sophia Fernandes**
- **Nicolas De Almeida**

## ⚙️ Funcionamento
O `Controller` recebe as requisições e encaminha para o `Service`.

O `Service` realiza as requisições para a API Open-Meteo, processa os dados recebidos e retorna as informações solicitadas.

## 🛠️ Tecnologias
- Java
- Spring Boot
- Open-Meteo
- RestTemplate
- Maven

## 🌐 Endpoints
| Método | Endpoint | Função |
|---|---|---|
| GET | `/localizacao/{cidade}` | Consulta a localização |
| GET | `/temperatura/{cidade}` | Consulta a temperatura |
| GET | `/umidade/{cidade}` | Consulta a umidade |
| GET | `/vento/velocidade/{cidade}` | Consulta a velocidade do vento |
| GET | `/vento/direcao/{cidade}` | Consulta a direção do vento |
| GET | `/temperatura/maxmin/{cidade}` | Consulta temperaturas máxima e mínima |
| GET | `/condicoes/{cidade}` | Consulta as condições do tempo |
| GET | `/datahora/{cidade}` | Consulta data e horário |
| GET | `/clima/{cidade}` | Consulta os principais dados climáticos |

## ▶️ Como executar
1. Clone o repositório.
2. Abra o terminal na pasta do projeto.
3. Execute o comando:

```bash
mvn spring-boot:run

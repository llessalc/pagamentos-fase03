# pagamentos-fase03
Api PAgamentos Fiap-58


## Responsabilidades
Esse Serviço é responsável por criar, confirmar e cancelar pagamentos.
Sendo que a criação de um novo pagamento se dá pela publicação de 
um novo pedido pelo serviço de pedidos, quando o pagamento é criado,
o mesmo é persistido no banco de dados de pagamentos e publicado em
uma fila. Para a confirmação, o serviço disponibiliza um webhook
que deverá ser chamada pelo MercadoPago quando o clinte realizar o pagamento,
assim que o webhook é acionado, o serviço verifica a situação do
pagamento e, caso o mesmo esteja pago, atualiza o banco de 
pagamento e envia o pagamento confirmado para a fila de connfirmados.
Por fim, há também a possibilidade de o pagamento ser cancelado, nessa
situação o serviço também atualizará o seu próprio banco e publicará
o pagamento cancelado na file de cancelados.

## Comunicação com outros serviços
- pedidos-queue: O serviço escuta essa fila e cria pagamentos quando recebe novos pedidos.
- pagamentos-criados-queue: O Serviço publica nessa fila e notifica a criação de pagamento.
- pagamentos-confirmados-queue: O serviço publica nessa fila. O serviço de pedidos deverá consumir essa fila antes de enviar o pedido à cozinha.
- pagamentos-cancelados-queue: O serviço publica nessa fila. O serviço de pedidos deverá cancelar qualquer pedido relacionado a um pagamento dessa fila.

## Uso
Localmente, pode-se utilizar o localstack para emular as filas em SQS. Basta
a criação de um container com:
```
docker run \
  --rm -it \
  -p 127.0.0.1:4566:4566 \
  -p 127.0.0.1:4510-4559:4510-4559 \
  -d localstack/localstack
```

Em seguida, deve-se definir o endpoint da AWS pela propriedade 
**spring.cloud.aws.endpoint** para apontar para o localstack.
Deve-se também configurar um profile de test com **aws configure**.
Para testes diretos na AWS, não é necessário o uso do localstack 
e a configuração do novo profile.

Também é necessário que sejam definidas as variáveis:
- MYSQL_HOST
- MYSQL_PASSWORD
- QR_URL
- MP_TOKEN
- PAGAMENTO_WH
- PEDIDOS_QUEUE
- PAGAMENTOS_CRIADOS_QUEUE
- PAGAMENTOS_CONFIRMADOS_QUEUE
- PAGAMENTOS_CANCELADOS_QUEUE

Para testar a aplicação, usa-se o profile test:
``` 
mvn clean
mvn test -P test
```

As coberturas de teste geradas pelo Jacoco podem ser acessadas em
**target/site/index.html**

Para iniciar a aplicação, basta executar:
```
mvn clean
mvn spring-boot:run
```
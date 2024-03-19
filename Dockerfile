FROM maven:3.8.5-openjdk-17

WORKDIR /usr/src/pagamento
COPY . .
EXPOSE 8080
ENTRYPOINT ["/bin/bash"]
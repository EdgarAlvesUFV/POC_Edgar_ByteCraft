# Nome dos serviços no docker-compose
FRONTEND=frontend
BACKEND=backend

# ========================
# Comandos de desenvolvimento
# ========================

up:
	docker compose up
down:
	docker compose down

logs:
	docker compose logs -f

#Executa o back-end
spring-boot-run:
	docker compose exec -it $(BACKEND) mvn spring-boot:run 

# ==============================================
# LISTAR DEPENDÊNCIAS/VERSÕES
# =============================================

#Confere os módulos node do container
npm-list:
	docker compose exec -it $(FRONTEND) npm list

# =============================================
# TESTES e DOC 
# =============================================

#Confere as dependências do projeto que estão container em formato de lista
maven-dependencies-list:
	docker compose exec -it $(BACKEND) mvn dependency:list

#Confere as dependências do projeto que estão container em formato de árvore
maven-dependencies-tree:
	docker compose exec -it $(BACKEND) mvn dependency:tree

#Roda testes e produz relatório:
#Gerado em backend/target/site/jacoco/index.html
maven-jacoco:
	docker compose exec backend mvn verify

#Deleta target, roda testes e produz relatório:
#Gerado em backend/target/site/jacoco/index.html
maven-jacoco-clean:
	docker compose exec backend mvn clean verify

#Javadocs do projeto inteiro:
maven-javadocs-completo:
	docker compose exec backend mvn javadoc:javadoc

#Testes unitários
maven-test:
	docker compose exec backend mvn test

#Executa testes unitários no react:
test-front:
	docker compose exec -it frontend npx vitest run

#Executa testes unitários no react com cobertura:
test-front-cobertura:
	docker compose exec -it frontend npx vitest run --coverage

#===================================================
#DEPLOY
#===================================================

#Build do frontend
build-front:
	docker compose run --rm frontend npm run build

#Gerar .war
maven-war:
	docker compose run --rm backend mvn clean package -Pprod -DskipTests

#Copia o build do front para o back
front-back:
	cp -r frontend/dist/* backend/Desconecta/src/main/webapp

DOCKER_COMPOSE_FILE=docker-compose.yml
DOCKER_TEST_FILE=Dockerfile.test

# Build backend Docker image
build-containers:
	@docker-compose -f $(DOCKER_COMPOSE_FILE) build || (echo "Failed to build containers"; exit 1)

# Run tests
test-containers:
	cd PictsManager && docker build -f $(DOCKER_TEST_FILE) -t pictsmanager-test .
	@docker run --rm pictsmanager-test || (echo "Failed to run tests"; exit 1)

# Bring up Docker Compose
run-containers:
	@docker-compose -f $(DOCKER_COMPOSE_FILE) up -d || (echo "Failed to bring up Docker Compose"; exit 1)

# Define the default target
all: build-containers test-containers run-containers
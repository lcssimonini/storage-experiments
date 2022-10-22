MIGRATION_LABEL = "to-be-changed"
DATE_WITH_TIME := $(shell /bin/date "+%Y-%m-%d-%H:%M:%S")

banner:
	curl --get \
		 --data-urlencode "text=Storage Experiments" \
	     --data-urlencode "font=soft" \
	     "https://devops.datenkollektiv.de/renderBannerTxt" >> src/main/resources/banner.txt

build project:
	@echo "\033[92m 🛠  Storage Experiments \033[0m"
	./gradlew clean build

start:
	@echo "\033[92m 🛠  Starting Storage Experiments \033[0m"
	./gradlew clean bootRun

compose-down:
	@echo "\033[92m 💣 Removing Storage Experiments dependencies \033[0m"
	docker-compose down --remove-orphans --volumes

compose-up:
	@echo "\033[92m 💣 Starting Storage Experiments dependencies \033[0m"
	docker-compose up

migration:
	export CHANGELOG_FILE_NAME=${DATE_WITH_TIME}-${MIGRATION_LABEL}.sql && ./gradlew diffChangeLog -PrunList=diffMain

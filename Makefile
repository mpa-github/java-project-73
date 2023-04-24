# Makefile

.PHONY: build

run-dist: build test lint test-report run # Build, check and run app

build: # Build app
	./gradlew clean
	./gradlew installDist

test: # Run tests
	./gradlew test

lint: # Check code style
	./gradlew checkstyleMain checkstyleTest

test-report: # Check code coverage
	./gradlew jacocoTestReport

run: # Run app in development
	PROFILE=dev ./build/install/app/bin/app

migration: # Generate migrations
	./gradlew diffChangeLog

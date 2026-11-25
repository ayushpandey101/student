# CI/CD and deployment

The workflow at `../.github/workflows/ci-cd.yml` runs Maven verification and JaCoCo coverage for pull requests and pushes to `main`. Pushes to `main` also build and push the Docker image, then deploy it to the Ubuntu server over SSH.

Configure these GitHub Actions repository secrets before pushing to `main`:

- `DOCKERHUB_USERNAME` and `DOCKERHUB_TOKEN`
- `SERVER_HOST`, `SERVER_USER`, and `SERVER_SSH_KEY`
- `MYSQL_URL`, `MYSQL_USERNAME`, and `MYSQL_PASSWORD`

The Ubuntu server must have Docker installed, permit SSH on port 22, and allow inbound traffic on port 8080. The container listens on port 8082 and is published as host port 8080. `MYSQL_URL` must be reachable from inside the container.

The image is tagged as both `latest` and the commit SHA. Passwords and deployment credentials are supplied at runtime through GitHub secrets; they are not added to the image.

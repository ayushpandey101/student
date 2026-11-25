# CI

The workflow at `../.github/workflows/ci-cd.yml` runs Maven verification, generates JaCoCo coverage, and builds a Docker image for pull requests and pushes to `main`. It does not publish the image or deploy to a server, so it requires no GitHub secrets.

The Docker image is built on the GitHub runner and is not retained after the workflow completes. Publishing it to a registry or deploying it to a server requires registry, SSH, and database credentials.

For local runtime, configure `MYSQL_PASSWORD`; `MYSQL_URL` and `MYSQL_USERNAME` can override the default local database connection.

# Kubernetes

Base manifests for local or development Kubernetes deployments.

The base includes:

- namespace
- ConfigMap and example Secret
- app Deployments and Services
- API Gateway Ingress
- resource requests and limits
- readiness and liveness probes
- PostgreSQL, MongoDB, Redis, and Kafka infrastructure
- PVCs for PostgreSQL and MongoDB

Apply:

```bash
kubectl apply -k k8s/base
```

The ingress host is:

```text
social-media.local
```

Create real secrets before deploying outside local development. The file `k8s/base/secret.example.yaml` is only a local example.

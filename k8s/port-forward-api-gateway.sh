#!/usr/bin/env bash
# Port-forward local port 8080 to api-gateway svc in ratelimit namespace
kubectl port-forward svc/api-gateway 8080:8080 -n ratelimit


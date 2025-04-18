kubectl get svc ez-frame-generator-ms

### Verificar logs do pod
kubectl logs -l app=ez-frame-generator-ms
kubectl describe pod -l app=ez-frame-generator-ms
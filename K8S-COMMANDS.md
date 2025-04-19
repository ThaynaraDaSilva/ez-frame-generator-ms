kubectl get svc ez-frame-generator-ms


kubectl get pods

### Verificar logs do pod
kubectl logs -l app=ez-frame-generator-ms
kubectl describe pod -l app=ez-frame-generator-ms

kubectl logs --previous ez-frame-generator-ms-6bf7ccf6d5-qh5gn

kubectl exec -it ez-frame-generator-ms-6bf7ccf6d5-qh5gn -- env | findstr AWS
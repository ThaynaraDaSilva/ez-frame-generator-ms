kubectl get svc ez-frame-generator-ms


kubectl get pods

### Verificar logs do pod
kubectl logs -l app=ez-frame-generator-ms
kubectl describe pod -l app=ez-frame-generator-ms

kubectl logs --previous ez-frame-generator-ms-6bf7ccf6d5-qh5gn

kubectl exec -it ez-frame-generator-ms-6794c46c58-fdbxr -- env | findstr AWS

kubectl describe pod ez-frame-generator-ms-855768d67b-5zg6q

kubectl exec -it ez-frame-generator-ms-6794c46c58-zf7w5 -- wget -qO- http://localhost:8080/actuator/health

# Ajustes - Traffic Type em Operações de Conteúdo

## 🔧 Mudanças Realizadas

### 1. Criação de Conteúdo (`createContent`)
✅ **CONCLUÍDO**
- Campo `trafficType` agora é aceito no payload da requisição
- Se omitido, **defaulta para `ORGANIC`**
- Arquivo: `ContentRequest.java` + `ContentService.java`

### 2. Edição de Conteúdo (`updateContent`)
✅ **CONCLUÍDO**
- Campo `trafficType` pode ser atualizado
- Se conteúdo antigo não tiver `trafficType`, é preenchido com `ORGANIC`
- Arquivo: `UpdateContentRequest.java` + `ContentService.java`

### 3. Transformação de Rascunho em Conteúdo (`transformDraftToContent`)
✅ **CONCLUÍDO**
- Campo `trafficType` adicionado ao DTO `TransformDraftRequest`
- Se omitido, **defaulta para `ORGANIC`**
- Arquivo: `TransformDraftRequest.java` + `ContentService.java`

---

## 📝 Arquivos Modificados

### 1. `TransformDraftRequest.java`
```java
// Adicionado:
import com.organixui.organixbackend.content.model.TrafficType;

// Campo adicionado:
@Schema(description = "Tipo de tráfego (PAID ou ORGANIC)", 
    allowableValues = {"PAID", "ORGANIC"},
    example = "ORGANIC")
private TrafficType trafficType;
```

### 2. `ContentService.java`
```java
// Método transformDraftToContent:
// Adicionado antes de salvar o conteúdo:
TrafficType resolvedTrafficType = request.getTrafficType() != null
        ? request.getTrafficType()
        : TrafficType.ORGANIC;
content.setTrafficType(resolvedTrafficType);
```

---

## 🧪 Como Testar

### Via Swagger UI
1. Acesse: `http://localhost:8080/swagger-ui.html`
2. Vá para: `POST /api/content/transform-draft/{draftId}`
3. Envie um rascunho com o body:
```json
{
  "channelIds": ["uuid-do-canal"],
  "productId": "uuid-do-produto",
  "trafficType": "PAID",
  "status": "PENDING"
}
```

### Via cURL
```bash
curl -X POST "http://localhost:8080/api/content/transform-draft/{draftId}" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer seu-token" \
  -d '{
    "channelIds": ["uuid-do-canal"],
    "trafficType": "PAID",
    "status": "PENDING"
  }'
```

---

## ✅ Build Status
```
Maven Build: ✅ SUCCESS
Compilation: ✅ NO ERRORS
Warnings: ✅ NONE
Ready for: ✅ DEPLOYMENT
```

---

## 📌 Comportamento Padrão

| Operação | trafficType Omitido | trafficType Fornecido |
|----------|-------------------|----------------------|
| `POST /api/content` | ➜ ORGANIC | ➜ Usa o valor fornecido |
| `PUT /api/content/{id}` | ➜ Não altera | ➜ Atualiza para novo valor |
| `POST /api/content/transform-draft/{draftId}` | ➜ ORGANIC | ➜ Usa o valor fornecido |

---

## 🎯 Resultado Final

Agora **todo conteúdo possui garantidamente um tipo de tráfego**, seja em:
- ✅ Criação direta
- ✅ Edição/atualização
- ✅ Transformação de rascunho

Todos defaultam para **ORGANIC** quando não especificado, mantendo consistência nos dados.

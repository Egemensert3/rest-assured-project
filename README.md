# REST Assured – Servis Otomatik Regresyon Testi Projesi

**Yazılım Test Mühendisliği Proje Ödevi**

## 📌 Proje Hakkında

Bu proje, **REST Assured** kütüphanesi kullanılarak **Java / Maven / JUnit 5** altyapısında hazırlanmış
bir servis otomatik regresyon testi projesidir.  
Test edilen API: [JSONPlaceholder](https://jsonplaceholder.typicode.com) (ücretsiz, herkese açık örnek REST API)

---

## 🛠️ Kullanılan Teknolojiler

| Teknoloji | Versiyon | Amaç |
|---|---|---|
| Java | 11+ | Programlama dili |
| Maven | 3.8+ | Bağımlılık yönetimi & build |
| JUnit 5 | 5.10.1 | Test çatısı |
| REST Assured | 5.4.0 | HTTP istek/yanıt doğrulama |
| Hamcrest | 2.2 | Assertion matcher kütüphanesi |

---

## 📁 Proje Yapısı

```
rest-assured-regression/
├── pom.xml
└── src/
    └── test/
        └── java/
            └── com/testproject/
                ├── utils/
                │   └── BaseTest.java          ← Ortak yapılandırma
                └── tests/
                    ├── GetRequestTest.java    ← GET endpoint testleri
                    ├── PostRequestTest.java   ← POST endpoint testleri (request body)
                    └── ResponseTimeTest.java  ← Yanıt süresi testleri
```

---

## ✅ Test Kapsamı

### Her Testte Yapılan 3 Temel Kontrol

1. **Status Code Kontrolü** – Beklenen HTTP durum kodu doğrulanır (200, 201, 404 vb.)
2. **Response Body Kontrolleri** – Dönen JSON içindeki alanların varlığı ve değerleri doğrulanır
3. **Yanıt Süresi Kontrolü** – Servisin belirlenen süre (≤ 3000 ms) içinde yanıt verdiği doğrulanır

### Test Sınıfları

#### `GetRequestTest.java` – GET Endpoint Testleri
| Test | Açıklama |
|---|---|
| `getAllPosts_shouldReturn200AndNonEmptyList` | 100 postun döndüğünü doğrular |
| `getPostById_shouldReturnCorrectPost` | ID'ye göre tekil post getirir |
| `getPostById_invalidId_shouldReturn404` | Geçersiz ID için 404 kontrolü |
| `getPostsByUserId_shouldReturnFilteredList` | Query param ile filtreleme |
| `getAllUsers_shouldReturn200AndCorrectSize` | 10 kullanıcı listesi kontrolü |
| `getUserById_shouldHaveExpectedFields` | Kullanıcı alanları doğrulaması |

#### `PostRequestTest.java` – POST Endpoint Testleri (JSON Request Body)
| Test | Açıklama |
|---|---|
| `createPost_shouldReturn201AndCreatedResource` | Yeni post oluşturma, 201 dönüşü |
| `createPost_shouldReturnGeneratedId` | Oluşturulan kaynağa ID atandığını doğrular |
| `createComment_withJsonBody_shouldReturn201` | `/comments` endpoint'ine JSON body POST |
| `createPost_withMinimalBody_shouldStillReturn201` | Minimal body ile POST davranışı |

#### `ResponseTimeTest.java` – Yanıt Süresi Testleri
| Test | Açıklama |
|---|---|
| `getPostById_shouldRespondWithinTimeLimit` | Tekil GET ≤ 2 sn |
| `getUserById_shouldRespondWithinTimeLimit` | User GET ≤ 2 sn |
| `createPost_shouldRespondWithinTimeLimit` | POST ≤ 3 sn |
| `getPostsByMultipleIds_shouldRespondWithinTimeLimit` | 5 farklı ID için parametrik test |
| `getAllPosts_shouldRespondWithinRelaxedLimit` | Liste sorgusu ≤ 5 sn |

---

## 🚀 Projeyi Çalıştırma

### Ön Gereksinimler
- Java 11 veya üzeri (`java -version`)
- Maven 3.8+ (`mvn -version`)
- İnternet bağlantısı (JSONPlaceholder API'ye erişim için)

### Tüm Testleri Çalıştır
```bash
mvn test
```

### Belirli Bir Test Sınıfını Çalıştır
```bash
mvn test -Dtest=GetRequestTest
mvn test -Dtest=PostRequestTest
mvn test -Dtest=ResponseTimeTest
```

### Belirli Bir Metodu Çalıştır
```bash
mvn test -Dtest=GetRequestTest#getAllPosts_shouldReturn200AndNonEmptyList
```

---

## 📊 Test Çıktısı

Başarılı çalışma sonunda Maven çıktısı:

```
[INFO] Tests run: 15, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

---

## 🔗 Kaynaklar

- [REST Assured Resmi Site](https://rest-assured.io/)
- [REST Assured GitHub](https://github.com/rest-assured/rest-assured)
- [JSONPlaceholder API](https://jsonplaceholder.typicode.com)
- [JUnit 5 Dokümantasyonu](https://junit.org/junit5/)

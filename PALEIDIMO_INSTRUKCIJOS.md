# Food Booking System - Paleidimo Instrukcijos

## 🔧 Sistemos Architektūra

```
MySQL (XAMPP) ← JDBC ← Spring Boot REST API ← HTTP ← Android App
                            ↑
                         (Port 8080)
```

## 📋 Paleidimo Žingsniai

### 1. Paleisti MySQL duomenų bazę (XAMPP)

#### Windows:
1. Paleiskite XAMPP Control Panel
2. Spauskite "Start" ties MySQL
3. Importuokite duomenų bazę:

```bash
mysql -u root -p
CREATE DATABASE IF NOT EXISTS food_booking;
USE food_booking;
SOURCE database/schema.sql;
SOURCE database/schema_extensions.sql;
EXIT;
```

#### Test duomenys:
Duomenų bazėje jau yra test vartotojai:
- **Klientas**: `client1` / `password123`
- **Vairuotojas**: `driver1` / `password123`
- **Adminas**: `admin` / `admin123`

---

### 2. Paleisti Spring Boot REST API Server

```bash
cd /path/to/1Lab-2Lab
mvn spring-boot:run
```

**Serveris paleis ant:** http://localhost:8080

**Patikrinkite ar veikia:**
- HTML dokumentacija: http://localhost:8080/api
- Test endpoint: http://localhost:8080/api/auth/test

**Jei veikia, turėtumėte matyti:**
```
==============================================
Food Booking REST API Server Started
API Documentation: http://localhost:8080/api
==============================================
```

---

### 3. Paleisti Android Aplikaciją

#### Android Studio:
1. Atidarykite `android-app/` folderį su Android Studio
2. Palaukite kol Gradle sync baigsis
3. Pasirinkite emulatorių arba fizinį įrenginį
4. Spauskite "Run" (▶️)

#### Konfigūracija:
- **Emuliatoriui:** API endpoint `http://10.0.2.2:8080` (jau sukonfigūruota)
- **Fiziniam įrenginiui:** Pakeiskite `ApiClient.java` → `BASE_URL` į `http://YOUR_COMPUTER_IP:8080`

#### Test prisijungimas:
- Username: `client1` arba `driver1`
- Password: `password123`

---

## 🧪 Kaip Testuoti REST API (be Android)

### Per curl (Command Line):

```bash
# Test endpoint
curl http://localhost:8080/api/auth/test

# Login
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"client1","password":"password123"}'

# Get restaurants
curl http://localhost:8080/api/client/restaurants

# Get client orders
curl http://localhost:8080/api/client/orders/1
```

### Per Postman:
1. Import collection naudojant dokumentaciją: http://localhost:8080/api
2. Test visus endpointus

---

## ❌ Troubleshooting

### MySQL Connection Failed:
```
Klaida: Communications link failure
```
**Sprendimas:**
1. Patikrinkite ar MySQL paleistas XAMPP
2. Patikrinkite `DatabaseConnection.java`:
   - URL: `jdbc:mysql://localhost:3306/food_booking`
   - User: `root`
   - Password: `` (tuščias arba jūsų nustatytas)

### Spring Boot nepasileižia:
```
Port 8080 already in use
```
**Sprendimas:**
1. Sustabdykite procesą ant 8080 porto
2. Arba pakeiskite portą `application.properties`:
   ```properties
   server.port=8081
   ```
3. Tada Android app pakeiskite `ApiClient.java` BASE_URL

### Android neprisijungia prie API:
```
java.net.ConnectException: Failed to connect
```
**Sprendimas:**
1. Patikrinkite ar Spring Boot serveris veikia (žr. step 2)
2. Emuliatoriui VISADA naudokite `http://10.0.2.2:8080`
3. Fiziniam įrenginiui: kompiuteris ir telefonas turi būti tame pačiame WiFi, naudokite kompiuterio IP

### Duomenų bazė tuščia:
```
No users found / Login failed
```
**Sprendimas:**
Re-import schema.sql - jis turi INSERT statements su test duomenimis

---

## 📊 Duomenų Srautai

### Kliento užsakymas (Android → DB):
```
1. Android: POST /api/client/orders + JSON
2. Spring Boot: ClientController.createOrder()
3. Service: ClientService.createOrder()
4. DAO: OrderDAO.createOrder() + OrderItemDAO.createOrderItem()
5. MySQL: INSERT INTO orders, order_items
6. Response: JSON su order details
7. Android: Rodo success message
```

### Vairuotojas atnaujina būseną:
```
1. Android: PUT /api/driver/orders/5/status?status=DELIVERED
2. Spring Boot: DriverController.updateOrderStatus()
3. Service: DriverService.updateOrderStatus()
4. DAO: OrderDAO.updateOrder()
5. MySQL: UPDATE orders SET status='DELIVERED'
6. Response: JSON success message
7. Android: Refresh orders list
```

---

## 🎯 Veikimo Patikrinimas

### ✅ Viskas veikia jei:

1. **MySQL:**
   ```sql
   SELECT COUNT(*) FROM users; -- Turėtų grąžinti > 0
   ```

2. **Spring Boot:**
   ```bash
   curl http://localhost:8080/api/auth/test
   # Turėtų grąžinti: "Auth endpoint is working!"
   ```

3. **Android:**
   - Login screen atsidaro
   - Įvedus `client1` / `password123` prisijungia
   - Rodo restoranų sąrašą

---

## 📞 Pagalba

Jei kyla problemų:
1. Patikrinkite logs:
   - Spring Boot: Console output kur paleidote `mvn spring-boot:run`
   - Android: Logcat in Android Studio
2. Patikrinkite MySQL logs: XAMPP Control Panel → MySQL → Logs

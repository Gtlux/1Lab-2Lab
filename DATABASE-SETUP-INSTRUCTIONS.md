# 🗄️ NAUJOS DUOMENŲ BAZĖS SETUP INSTRUKCIJOS

## 📋 Kas bus sukurta:

- ✅ **Visiškai nauja** `food_booking_db` duomenų bazė
- ✅ **10 lentelių** su visais foreign keys
- ✅ **9 test vartotojai** (ID 1-9)
- ✅ **5 restoranai** (ID 1-5)
- ✅ **28 menu patiekalai** (ID 1-28)
- ✅ **3 užsakymai** su order items
- ✅ **Teisingi BCrypt hash'ai** (password: `password123`)
- ✅ **Nuoseklūs ID** prasidedantys nuo 1

---

## 🚀 KAIP PALEISTI (phpMyAdmin):

### 1️⃣ **Atidaryk phpMyAdmin**
   - URL: `http://localhost/phpmyadmin/`

### 2️⃣ **Spausk "SQL" tab viršuje**

### 3️⃣ **Nukopijuok VISĄ `setup-fresh-database.sql` turinį**
   - Failas: `/home/user/1Lab-2Lab/setup-fresh-database.sql`
   - **ARBA** phpMyAdmin: spausk "Import" → pasirink failą

### 4️⃣ **Įklijuok į SQL lauką ir spausk "Go"**

### 5️⃣ **Turėtų matyti:**
   ```
   Database food_booking_db created successfully
   10 table(s) created
   9 row(s) inserted into users
   5 row(s) inserted into restaurants
   28 row(s) inserted into menu_items
   3 row(s) inserted into orders
   9 row(s) inserted into order_items
   3 row(s) inserted into loyalty_points
   ```

---

## 👥 TEST VARTOTOJAI:

**Visi password'ai: `password123`**

| Username      | Role              | ID |
|---------------|-------------------|----| 
| admin         | ADMINISTRATOR     | 1  |
| jonas         | CLIENT            | 2  |
| petras        | CLIENT            | 3  |
| agne          | CLIENT            | 4  |
| pizza_owner   | RESTAURANT_OWNER  | 5  |
| burger_owner  | RESTAURANT_OWNER  | 6  |
| sushi_owner   | RESTAURANT_OWNER  | 7  |
| driver1       | DRIVER            | 8  |
| driver2       | DRIVER            | 9  |

---

## 🍕 RESTORANAI:

| ID | Pavadinimas    | Savininkas     | Patiekalų sk. |
|----|----------------|----------------|---------------|
| 1  | Pizza Paradise | pizza_owner    | 6             |
| 2  | Burger House   | burger_owner   | 6             |
| 3  | Sushi Master   | sushi_owner    | 5             |
| 4  | Kebab King     | pizza_owner    | 5             |
| 5  | Salad Bar      | burger_owner   | 4             |

---

## ✅ PO SETUP:

1. **Restart Spring Boot serverį** (IntelliJ)
2. **Rebuild Android app** (Android Studio)
3. **Testuok prisijungimą:** `jonas / password123`
4. **Testuok meniu:** Turėtų rodyti 5 restoranus
5. **Testuok užsakymą:** Pridėk į krepšelį ir užsakyk

---

## 🔍 PATIKRINIMAS:

**phpMyAdmin SQL:**
```sql
-- Patikrink vartotojus
SELECT id, username, role FROM users;

-- Patikrink restoranus
SELECT id, name, owner_id FROM restaurants;

-- Patikrink meniu patiekalus
SELECT id, name, restaurant_id, price FROM menu_items LIMIT 10;

-- Patikrink užsakymus
SELECT id, client_id, restaurant_id, status, total_amount FROM orders;
```

**Naršyklėje:**
```
http://localhost:8080/api/client/restaurants
http://localhost:8080/api/client/menu/restaurant/1
http://localhost:8080/api/client/orders/2
```

---

## ⚠️ SVARBU:

- ✅ **Senoji DB bus ištrinta** (`DROP DATABASE IF EXISTS`)
- ✅ **Visi seni duomenys bus prarasti**
- ✅ **Nuoseklūs ID** (users: 1-9, restaurants: 1-5, etc.)
- ✅ **Foreign keys veikia teisingai**


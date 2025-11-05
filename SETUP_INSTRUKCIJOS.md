# 🚀 Pilna Setup Instrukcija

Ši instrukcija parodo, ką reikia padaryti nuo pat pradžių, kai clone'inate projektą.

---

## 📋 Reikalavimai

- ✅ **Java 17** arba naujesnė ([Download](https://www.oracle.com/java/technologies/downloads/))
- ✅ **IntelliJ IDEA** ([Download](https://www.jetbrains.com/idea/download/))
- ✅ **XAMPP** ([Download](https://www.apachefriends.org/))
- ✅ **Git** ([Download](https://git-scm.com/downloads))

---

## 🔧 Žingsnis po žingsnio

### 1️⃣ Clone Projektą

```bash
git clone <your-repo-url>
cd 1Lab-2Lab
git checkout claude/food-booking-system-design-011CUpVKAtxe6dYm43cWHijF
```

---

### 2️⃣ Paruošti Duomenų Bazę

#### A. Paleisti XAMPP

1. Atidarykite **XAMPP Control Panel**
2. Paleiskite **MySQL** (Start mygtukas)
3. Paleiskite **Apache** (Start mygtukas)

#### B. Sukurti Duomenų Bazę

1. Atidarykite naršyklėje: **http://localhost/phpmyadmin**
2. Spustelėkite **"New"** (kairėje)
3. Database name: `food_booking_db`
4. Collation: `utf8mb4_unicode_ci`
5. Spustelėkite **"Create"**

#### C. Importuoti Schemą

**SVARBU**: Pirma reikia pataisyti slaptažodžius!

1. Atidarykite projektą IntelliJ IDEA
2. Palaukite, kol Maven parsisiųs dependencies (~1-2 min)
3. Raskite failą: `src/main/java/com/foodbooking/util/PasswordChecker.java`
4. Dešiniu pelės mygtuku → **Run 'PasswordChecker.main()'**
5. Console lange pamatysite:
   ```
   New hash: $2a$10$XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
   Copy this hash and replace in schema.sql
   ```
6. **Nukopijuokite** šį hash'ą

7. Atidarykite `database/schema.sql`
8. **Raskite visas eilutes** su `password` (eilutės 130, 135-136, 141-142, 147-148)
9. **Pakeiskite** senus hash'us į naują nukopijuotą hash'ą:

   **PRIEŠ:**
   ```sql
   VALUES ('admin', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'admin@...
   ```

   **PO:**
   ```sql
   VALUES ('admin', '$2a$10$JUSU_NAUJAS_HASH_CIA', 'admin@...
   ```

10. **Išsaugokite** `schema.sql` failą

11. **phpMyAdmin**:
    - Pasirinkite `food_booking_db` bazę (kairėje)
    - Spustelėkite **"Import"** (viršuje)
    - Spustelėkite **"Choose File"**
    - Pasirinkite `database/schema.sql`
    - Spustelėkite **"Go"** (apačioje)
    - Turėtumėte pamatyti: "Import has been successfully finished"

---

### 3️⃣ Atidaryt Projektą IntelliJ IDEA

1. **File** → **Open**
2. Pasirinkite projekto folderį (`1Lab-2Lab`)
3. Spustelėkite **"OK"**
4. IntelliJ paklausi "Trust and Open Maven Project?" → **Trust Project**

#### Maven Dependencies

IntelliJ automatiškai pradės atsisiųsti Maven dependencies.

**Jei ne**, dešiniame šone:
1. Spustelėkite **Maven** tab'ą
2. Spustelėkite **Reload** ikoną (apvalus rodyklės ženklas)

**Palaukite** ~2-5 minutes, kol visi dependencies bus atsisiųsti.

Žemiau dešinėje matysite: "Maven: Importing..."

---

### 4️⃣ Patikrinti Duomenų Bazės Prisijungimą

Failas: `src/main/java/com/foodbooking/util/DatabaseConnection.java`

Patikrinkite eilutes 8-10:
```java
private static final String URL = "jdbc:mysql://localhost:3306/food_booking_db";
private static final String USER = "root";
private static final String PASSWORD = "";
```

**XAMPP Default nustatymai:**
- User: `root`
- Password: `""` (tuščias)
- Port: `3306`

**Jei turite kitokius**, pakeiskite.

---

### 5️⃣ Paleisti Programą

1. IntelliJ IDEA, raskite failą: `src/main/java/com/foodbooking/MainApp.java`
2. Dešiniu pelės mygtuku ant failo → **Run 'MainApp.main()'**
3. Arba spustelėkite **žalią play ikoną** šalia `public class MainApp`

**Pirmas paleidimas** gali užtrukti ilgiau (~30 sek), kol JavaFX inicializuojasi.

---

### 6️⃣ Prisijungti

Dabar turėtumėte matyti prisijungimo langą!

**Testiniai prisijungimai:**

| Vartotojo Vardas | Slaptažodis | Rolė |
|-----------------|-------------|------|
| admin | password123 | Administratorius |
| owner1 | password123 | Restorano Savininkas |
| owner2 | password123 | Restorano Savininkas |
| driver1 | password123 | Vairuotojas |
| driver2 | password123 | Vairuotojas |
| client1 | password123 | Klientas |
| client2 | password123 | Klientas |

Spustelėkite **"Prisijungti"** su bet kuriuo iš jų.

---

## 🐛 Problemos Sprendimas

### ❌ "Cannot find symbol: class VBox"

**Sprendimas:**
```bash
mvn clean install
```

Arba IntelliJ:
- **File** → **Invalidate Caches** → **Invalidate and Restart**

---

### ❌ "Neteisingas vartotojo vardas arba slaptažodis"

**Sprendimas:**

Matyt praleistote žingsnį 2C (slaptažodžių atnaujinimas).

**Greitas fix:**

1. Paleiskite: `src/main/java/com/foodbooking/util/PasswordChecker.java`
2. Nukopijuokite naują hash'ą
3. phpMyAdmin → SQL tab:
   ```sql
   USE food_booking_db;
   UPDATE users SET password = 'NAUJAS_HASH_CIA';
   ```
4. Spustelėkite **"Go"**
5. Bandykite prisijungti iš naujo

---

### ❌ "Failed to connect to database"

**Patikrinkite:**

1. ✅ Ar XAMPP MySQL veikia? (žalia lemputė XAMPP Control Panel)
2. ✅ Ar sukurta `food_booking_db` bazė?
3. ✅ Ar teisingi prisijungimo duomenys `DatabaseConnection.java`?

**Test connection per phpMyAdmin:**
- Jei galite atidaryti http://localhost/phpmyadmin - MySQL veikia ✅

---

### ❌ Maven klaidos

**Sprendimas:**

```bash
mvn clean install -U
```

Arba ištrinkite Maven cache:
- Windows: `C:\Users\<username>\.m2\repository`
- Ištrinkite `repository` folderį ir bandykite iš naujo

---

### ❌ JavaFX klaidos

Patikrinkite `pom.xml` ar yra:
```xml
<dependency>
    <groupId>org.openjfx</groupId>
    <artifactId>javafx-controls</artifactId>
    <version>21.0.1</version>
</dependency>
```

---

## 📊 Projekto Struktūra

```
1Lab-2Lab/
├── src/main/java/com/foodbooking/
│   ├── MainApp.java                 ← PALEISTI ČIA
│   ├── model/                       (Domain klasės)
│   ├── dao/                         (Database operacijos)
│   ├── controller/                  (JavaFX controllers)
│   └── util/
│       ├── DatabaseConnection.java  ← DB nustatymai
│       └── PasswordChecker.java     ← Slaptažodžių fix
├── src/main/resources/fxml/         (GUI layouts)
├── database/
│   └── schema.sql                   ← Importuoti į phpMyAdmin
├── pom.xml                          (Maven config)
└── README.md                        (Dokumentacija)
```

---

## ✅ Checklist

Prieš paleisdami programą:

- [ ] XAMPP MySQL paleistas
- [ ] `food_booking_db` bazė sukurta
- [ ] `schema.sql` importuotas (su pataisytais hash'ais!)
- [ ] Maven dependencies atsisiųsti (IntelliJ apačioje be klaidų)
- [ ] `DatabaseConnection.java` turi teisingus duomenis
- [ ] `MainApp.java` paleidžiamas be kompiliavimo klaidų

Jei visi žingsniai ✅ - programa turėtų veikti!

---

## 🎓 Video Instrukcija (Trumpai)

1. **Clone** repo → Atidaryt IntelliJ
2. **XAMPP** → MySQL Start → phpMyAdmin
3. **Sukurti** `food_booking_db` bazę
4. **Paleisti** `PasswordChecker.java` → Copy hash
5. **Redaguoti** `schema.sql` → Replace hash
6. **Import** `schema.sql` į phpMyAdmin
7. **Paleisti** `MainApp.java`
8. **Login**: admin / password123

---

## 📞 Pagalba

Jei vis dar kyla problemų:

1. Patikrinkite IntelliJ **Event Log** (apačioje dešinėje)
2. Patikrinkite **Maven** tab'ą (dešinėje) ar nėra raudonų klaidų
3. Paleiskite: `mvn clean install` ir pažiūrėkite klaidas

---

**Paskutinis patarimas:** Atlikite visus žingsnius **eilės tvarka**. Nepraleiskite žingsnio 2C (slaptažodžių fix) - tai dažniausia problema! 🔑

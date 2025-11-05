# Prisijungimo Problemos Sprendimas

Jei negalite prisijungti su testiniais prisijungimais (admin / password123), problema yra su BCrypt slaptažodžių hash'avimu.

## Sprendimas 1: Sugeneruoti naują hash'ą (REKOMENDUOJAMA)

### 1. Paleiskite PasswordHashGenerator

```bash
# Per Maven
mvn exec:java -Dexec.mainClass="com.foodbooking.util.PasswordHashGenerator"

# Arba per IntelliJ IDEA
# Atidarykite: src/main/java/com/foodbooking/util/PasswordHashGenerator.java
# Dešiniu pelės mygtuku -> Run 'PasswordHashGenerator.main()'
```

### 2. Nukopijuokite hash'ą

Output atrodys taip:
```
Plain password: password123
BCrypt hash: $2a$10$XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
Verification: true
```

### 3. Atnaujinkite schema.sql

Atsidarykite `database/schema.sql` ir pakeiskite visus hash'us į naują sugeneruotą hash'ą (eilutės 130-148):

```sql
-- Pavyzdys:
INSERT INTO users (username, password, email, full_name, phone_number, role, active)
VALUES ('admin', 'ČIA_ĮKLIJUOKITE_NAUJĄ_HASH', 'admin@foodbooking.lt', 'Administratorius', '+37060000000', 'ADMINISTRATOR', TRUE);
```

### 4. Perkurkite duomenų bazę

phpMyAdmin:
1. Ištrinkite `food_booking_db` duomenų bazę
2. Sukurkite naują `food_booking_db` duomenų bazę
3. Importuokite atnaujintą `schema.sql`

## Sprendimas 2: Naudoti UPDATE SQL (Greitas būdas)

### 1. Sugeneruokite hash'ą (kaip aukščiau)

### 2. Paleiskite šį SQL

phpMyAdmin SQL lange:

```sql
-- Pakeiskite 'NAUJASIS_HASH' į jūsų sugeneruotą hash'ą
UPDATE users SET password = 'NAUJASIS_HASH';
```

Tai atnaujins visų vartotojų slaptažodžius.

## Sprendimas 3: Sukurti naują vartotoją per programą

### 1. Paleiskite programą

### 2. Spustelėkite "Registruotis"

### 3. Užsiregistruokite kaip naujas vartotojas

Registracijos forma automatiškai sukurs teisingą BCrypt hash'ą.

### 4. Padarykite jį administratoriumi

phpMyAdmin:
```sql
UPDATE users SET role = 'ADMINISTRATOR' WHERE username = 'jusu_vardas';
```

## Patikrinimas

Paleiskite šį SQL, kad pamatytumėte dabartinį hash'ą:

```sql
SELECT username, password, role FROM users;
```

Visi hash'ai turi prasidėti `$2a$10$` arba `$2b$10$` ir būti ~60 simbolių ilgio.

## Kodėl tai nutiko?

BCrypt sugeneruoja skirtingus hash'us kiekvieną kartą (dėl salt), todėl schema.sql faile esantis hash'as gali neatitikti tikrojo "password123" hash'o.

## Greitas Pataisymas Testuojant

Jei norite greitai išbandyti sistemą:

1. Užsiregistruokite kaip naujas vartotojas per programą
2. Pakeiskite jo rolę į ADMINISTRATOR per phpMyAdmin
3. Prisijunkite su tuo vartotoju

---

**Rekomenduojamas būdas**: Sprendimas 1 - sugeneruoti naują hash'ą ir atnaujinti schema.sql

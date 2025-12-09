# Food Booking Mobile Application

Android aplikacija klientams ir vairuotojams.

## Funkcijos

### Kliento Funkcijos:
- ✅ Prisijungimas
- ✅ Restoranų peržiūra
- ✅ Užsakymų sąrašo peržiūra
- ✅ Užsakymo detalės

### Vairuotojo Funkcijos:
- ✅ Prisijungimas
- ✅ Priskirtų užsakymų peržiūra
- ✅ Užsakymo būsenos atnaujinimas
- ✅ Real-time refresh

## Technologijos:
- **Android SDK**: API 24-34
- **Java 17**
- **Retrofit 2**: REST API komunikacija
- **Gson**: JSON serializacija
- **RecyclerView**: Sąrašų vaizdavimas
- **Material Design**: UI komponnetai

## Kaip paleisti:

1. Atidarykite projektą su **Android Studio**
2. Paleiskite Spring Boot serverį:
   ```bash
   cd ..
   mvn spring-boot:run
   ```
3. Paleiskite Android aplikaciją emuliatoriuje arba fiziniame įrenginyje

## API Endpoint:
- Emuliatoriui: `http://10.0.2.2:8080`
- Fiziniam įrenginiui: `http://YOUR_COMPUTER_IP:8080`

## Test Accounts:
- **Klientas**: username: `client1`, password: `password123`
- **Vairuotojas**: username: `driver1`, password: `password123`

## Ekranai:
1. **LoginActivity** - Prisijungimo ekranas
2. **ClientActivity** - Kliento sąsaja su restoranais ir užsakymais
3. **DriverActivity** - Vairuotojo sąsaja su užsakymų valdymu

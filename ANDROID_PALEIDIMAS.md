# Android Aplikacijos Paleidimo Instrukcijos

Šiame dokumente rasite išsamų vadovą kaip paleisti Food Booking Android aplikaciją.

---

## 1. Reikalinga Programinė Įranga

### 1.1 Android Studio
**Parsisiuntimas:**
- Eikite į: https://developer.android.com/studio
- Parsisiųskite naujausią versiją (Android Studio Hedgehog ar naujesnę)
- Įdiekite pagal savo operacinę sistemą

**Sistemos reikalavimai:**
- Windows: Windows 10/11 (64-bit)
- macOS: macOS 10.14 ar naujesnė
- Linux: 64-bit distribucija su GNU C Library (glibc) 2.31 ar naujesnė
- RAM: Mažiausiai 8GB (rekomenduojama 16GB)
- Laisvos vietos: 8GB diske

### 1.2 Java Development Kit (JDK)
Android Studio paprastai įdiegia JDK automatiškai, bet jei reikia:
- JDK 17 (rekomenduojama)
- Galite parsisiųsti iš: https://adoptium.net/

---

## 2. Android Studio Įdiegimas ir Konfigūracija

### 2.1 Pirminis Paleidimas

1. **Paleiskite Android Studio**
2. **Import Settings** - jei tai pirmas paleidimas, pasirinkite "Do not import settings"
3. **Setup Wizard** sekite žingsnius:
   - Pasirinkite "Standard" setup tipo
   - Pasirinkite UI theme (šviesią arba tamsią)
   - Patikrinkite nustatymus ir spauskite "Finish"
   - Palaukite kol atsisiųs SDK komponentus (tai gali užtrukti 15-30 min)

### 2.2 Android SDK Konfigūracija

1. Atidarykite **Tools → SDK Manager**
2. **SDK Platforms** tab'e pažymėkite:
   - Android 14.0 (API 34) - Target SDK
   - Android 7.0 (API 24) - Minimum SDK
3. **SDK Tools** tab'e įsitikinkite kad įdiegta:
   - Android SDK Build-Tools
   - Android Emulator
   - Android SDK Platform-Tools
   - Intel x86 Emulator Accelerator (HAXM installer) - jei Windows/Mac
4. Spauskite **Apply** ir palaukite kol viskas atsisiųs

---

## 3. Projekto Importavimas

### 3.1 Atidaryti Projektą

1. Paleiskite **Android Studio**
2. Pasirinkite **File → Open**
3. Naršykite į projekto aplanką: `/home/user/1Lab-2Lab/android-app`
4. Pasirinkite `android-app` aplanką ir spauskite **OK**

### 3.2 Gradle Sync

Kai projektas atsidarys:
1. Android Studio automatiškai pradės **Gradle Sync**
2. Apačioje matysite pranešimą "Syncing..."
3. **SVARBU:** Pirmą kartą tai gali užtrukti 5-15 minučių (atsisiunčia priklausomybes)
4. Jei klausiama "Trust Gradle project" → Spauskite **Trust Project**

### 3.3 Galimos Problemos ir Sprendimai

**Klaida: "SDK location not found"**
```
Sprendimas:
1. File → Project Structure → SDK Location
2. Nustatykite Android SDK kelią (paprastai: C:\Users\<user>\AppData\Local\Android\Sdk)
3. Spauskite Apply
```

**Klaida: "Gradle version too old"**
```
Sprendimas:
1. Atsidarykite failą: android-app/gradle/wrapper/gradle-wrapper.properties
2. Pakeiskite distributionUrl į naujesnę versiją (pvz., gradle-8.2-bin.zip)
3. Sync Project
```

**Klaida: Build Tools trūksta**
```
Sprendimas:
1. Atsidarykite pranešimą "Install Build Tools XX.X.X and sync project"
2. Spauskite ant nuorodos
3. Palaukite kol įdiegs
```

---

## 4. API Konfigūracija (LABAI SVARBU!)

### 4.1 Patikrinti API URL

**1. Atidarykite failą:**
```
android-app/app/src/main/java/com/foodbooking/mobile/api/ApiClient.java
```

**2. Raskite eilutę su BASE_URL:**
```java
private static final String BASE_URL = "http://10.0.2.2:8080/";
```

**3. URL Reikšmės:**

| Situacija | URL |
|-----------|-----|
| Android Emulator (Spring Boot lokaliai) | `http://10.0.2.2:8080/` |
| Tikras telefonas (Spring Boot lokaliai) | `http://192.168.x.x:8080/` |
| Spring Boot serveryje | `http://serverio-ip:8080/` |

**PASTABA:** `10.0.2.2` yra specialus IP, kuris Android emuliatorijuje reiškia jūsų kompiuterio `localhost`

### 4.2 Rasti Savo Vietinį IP (tikram telefonui)

**Windows:**
```bash
ipconfig
# Ieškokite "IPv4 Address" (pvz., 192.168.1.105)
```

**Linux/Mac:**
```bash
ifconfig
# arba
ip addr show
# Ieškokite inet 192.168.x.x
```

---

## 5. Spring Boot Backend Paleidimas

**SVARBU:** Prieš paleisdami Android aplikaciją, PRIVALO veikti Spring Boot serveris!

### 5.1 Paleisti Spring Boot

**IntelliJ IDEA:**
1. Atidarykite pagrindinį projektą (`1Lab-2Lab`)
2. Raskite `src/main/java/com/foodbooking/RestApiApplication.java`
3. Dešiniu mygtuku → **Run 'RestApiApplication'**
4. Palaukite kol pamatysite: `Tomcat started on port(s): 8080`

**Maven Command Line:**
```bash
cd /home/user/1Lab-2Lab
mvn spring-boot:run
```

### 5.2 Patikrinti ar Veikia

Atidarykite naršyklę ir eikite į:
```
http://localhost:8080/api
```

Turėtumėte matyti API dokumentacijos puslapį.

---

## 6. Android Emuliatorius

### 6.1 Sukurti Naują Virtual Device

1. Android Studio → **Tools → Device Manager**
2. Spauskite **Create Device**
3. Pasirinkite įrenginį:
   - **Category:** Phone
   - **Device:** Pixel 6 arba Pixel 5 (rekomenduojama)
   - Spauskite **Next**
4. Pasirinkite System Image:
   - **Release Name:** Tiramisu (API Level 33) arba UpsideDownCake (API 34)
   - **ABI:** x86_64 (greitesnis)
   - Jei nėra atsisiųsto → Spauskite **Download** prie norimo image
   - Spauskite **Next**
5. AVD Name: `Pixel_6_API_33` (arba kitas pavadinimas)
6. **Finish**

### 6.2 Paleisti Emuliatorių

**Būdas 1: Per Device Manager**
1. Tools → Device Manager
2. Raskite savo sukurtą įrenginį
3. Spauskite ▶ (Play) mygtuką

**Būdas 2: Per Toolbar**
1. Viršutiniame toolbar pasirinkite įrenginį iš dropdown
2. Spauskite žalią ▶ (Run) mygtuką

**PASTABA:** Pirmą kartą paleidžiant emuliatorių gali užtrukti 2-5 minutes.

### 6.3 Emuliatorius Neveikia?

**Intel procesoriai (Windows/Linux):**
```
1. Įsitikinkite kad BIOS'e įjungta Virtualization Technology (VT-x)
2. Windows: Išjunkite Hyper-V
   - Control Panel → Programs → Windows Features
   - Atžymėkite "Hyper-V"
   - Reboot
```

**AMD procesoriai:**
```
- Naudokite ARM system image vietoj x86_64
- Bus šiek tiek lėtesnis, bet veiks
```

**Mac M1/M2 (Apple Silicon):**
```
- Pasirinkite ARM 64 (arm64-v8a) system image
- Veiks labai greitai!
```

---

## 7. Fizinis Android Įrenginys (Tikras Telefonas)

### 7.1 Developer Mode Įjungimas

**Android 12+:**
1. Eikite į **Settings → About phone**
2. Raskite **Build number**
3. **Spauskite 7 kartus** ant Build number
4. Įveskite PIN/Password
5. Pamatysite pranešimą "You are now a developer!"

### 7.2 USB Debugging

1. Eikite į **Settings → System → Developer options**
2. Įjunkite **Developer options** (viršuje)
3. Įjunkite **USB debugging**
4. Patvirtinkite dialoge "Allow USB debugging"

### 7.3 Prijungti Telefoną

1. **Prijunkite telefoną prie kompiuterio USB laidu**
2. Telefone pasirinkite **File Transfer** arba **MTP** režimą
3. Telefone pamatysite dialogą "Allow USB debugging?"
   - Pažymėkite "Always allow from this computer"
   - Spauskite **OK**
4. Android Studio **Device Manager** turėtų parodyti jūsų telefoną

### 7.4 Wireless Debugging (Android 11+)

Jei nenorite laido:
1. **Settings → Developer options → Wireless debugging** (Įjungti)
2. Spauskite **Pair device with pairing code**
3. Android Studio: Tools → Device Manager → Pair Devices Using Wi-Fi
4. Įveskite kodą iš telefono

---

## 8. Aplikacijos Paleidimas

### 8.1 Paleidimo Žingsniai

1. **Įsitikinkite kad:**
   - ✅ Spring Boot serveris veikia (localhost:8080)
   - ✅ Emuliatorus paleistas ARBA telefonas prijungtas
   - ✅ ApiClient.java turi teisingą BASE_URL

2. **Android Studio toolbar:**
   - Device dropdown: Pasirinkite įrenginį (emuliatorių arba telefoną)
   - Configuration dropdown: Turėtų būti `app`

3. **Spauskite žalią ▶ (Run 'app') mygtuką**
   - Arba: **Run → Run 'app'**
   - Arba: **Shift + F10**

4. **Palaukite:**
   - Build process: 30-60 sekundžių
   - Installing APK: 10-20 sekundžių
   - Aplikacija automatiškai pasileis įrenginyje

### 8.2 Pirmas Paleidimas

Pamatysite **Login** ekraną:

```
+----------------------------------+
|        Food Booking App          |
|                                  |
|  [Username____________]          |
|  [Password____________]          |
|                                  |
|        [ Prisijungti ]           |
|        [ Registruotis ]          |
+----------------------------------+
```

### 8.3 Testavimo Vartotojai

Galite prisijungti su admin vartotoju:
- **Username:** `admin`
- **Password:** `admin123`

Arba sukurti naują paspaudus **Registruotis**

---

## 9. Debug ir Logs

### 9.1 Logcat

**Žiūrėti aplikacijos logs:**
1. Android Studio apačioje → **Logcat** tab
2. Filter: Pasirinkite savo įrenginį
3. Package: `com.foodbooking.mobile`

**Naudingi filtrai:**
```
Tag: System.out (matysite println išvestis)
Tag: AndroidRuntime (crash logs)
```

### 9.2 Dažnos Problemos

**"Unable to connect to server"**
```
Priežastys:
1. Spring Boot serveris neveikia
2. Neteisingas BASE_URL
3. Firewall blokuoja 8080 portą
4. Telefonas ne tame pačiame WiFi tinkle

Sprendimas:
- Patikrinkite http://localhost:8080/api naršyklėje
- Patikrinkite ApiClient.java BASE_URL
- Windows Firewall: Leiskite Java/Spring Boot
```

**"App keeps stopping"**
```
Sprendimas:
1. Žiūrėkite Logcat stack trace
2. Paprastai tai API klaida arba null pointer
3. Patikrinkite ar visi DTO laukai teisingi
```

**"Installation failed"**
```
Sprendimas:
1. Build → Clean Project
2. Build → Rebuild Project
3. Bandykite dar kartą
```

---

## 10. Aplikacijos Funkcionalumas

### 10.1 Klientams (CLIENT Role)

Po registracijos/prisijungimo:

1. **Restoranų sąrašas:**
   - Matysite visų restoranų sąrašą
   - Spauskite "Žiūrėti meniu" ant bet kurio restorano

2. **Meniu:**
   - Matysite restorano patiekalus
   - Galite keisti kiekį su +/- mygtukais
   - Spauskite "Pridėti į krepšelį"

3. **Krepšelis (🛒):**
   - Viršuje dešinėje spauskite "🛒 Krepšelis"
   - Matysite visus pridėtus patiekalus
   - Galite keisti kiekius arba ištrinti
   - Įveskite pristatymo adresą
   - Spauskite "Užsakyti"

4. **Mano užsakymai:**
   - Tab'as "Mano užsakymai"
   - Matysite savo užsakymų istoriją

### 10.2 Vairuotojams (DRIVER Role)

1. **Prieinami užsakymai:**
   - Matysite užsakymus kuriuos galite priimti
   - Spauskite "Priimti" ant užsakymo

2. **Mano pristatymai:**
   - Tab'as "Mano pristatymai"
   - Matysite priskirtus užsakymus
   - Galite atnaujinti užsakymo būseną:
     - Paimta → Pristatoma → Pristatyta

### 10.3 Dynamic Pricing

**Veikia automatiškai:**
- Piečių metas: 11:00-14:00 (+20% kaina)
- Vakarienės metas: 18:00-21:00 (+20% kaina)
- Kitu laiku: įprasta kaina

### 10.4 Loyalty Points

**Automatiškai skiriama:**
- Kai užsakymas pažymimas kaip "DELIVERED"
- 1 taškas už kiekvienus 10€
- Taškai matomi Desktop aplikacijoje

---

## 11. Hot Reload (Greitam Testavimui)

### 11.1 Instant Run

Android Studio palaiko kodo pakeitimus be pilno rebuild:

1. Pakeiskite UI (XML layout)
2. Spauskite **⚡ Apply Changes** (Ctrl+F10)
3. Pakeitimai atsiras per 2-3 sekundes

**PASTABA:** Java kodo pakeitimai reikalauja pilno rebuild (Run)

---

## 12. Build APK (Instaliacijai)

### 12.1 Debug APK

```
1. Build → Build Bundle(s) / APK(s) → Build APK(s)
2. Palaukite kol sukompiliuoja
3. APK vieta: android-app/app/build/outputs/apk/debug/app-debug.apk
4. Galite perkelti į telefoną ir įdiegti
```

### 12.2 Release APK (Produkcijai)

```
1. Build → Generate Signed Bundle / APK
2. Pasirinkite APK
3. Sekite instrukcijas sukurti keystore
4. Pasirinkite release build type
5. Finish
```

---

## 13. Troubleshooting Checklist

Jei aplikacija neveikia, patikrinkite:

- [ ] Spring Boot serveris veikia (http://localhost:8080/api)
- [ ] MySQL duomenų bazė veikia
- [ ] BASE_URL ApiClient.java yra teisingas
- [ ] Emuliatorus paleistas arba telefonas prijungtas
- [ ] USB debugging įjungtas (jei fizinis telefonas)
- [ ] Gradle sync baigtas be klaidų
- [ ] Nėra build errors (Build → Make Project)
- [ ] Firewall neleidžia prisijungti prie 8080 porto

---

## 14. Pagalba

### Dokumentacija:
- Android Docs: https://developer.android.com/docs
- Spring Boot REST API: http://localhost:8080/api
- Projekto README: `/home/user/1Lab-2Lab/README.md`
- Setup instrukcijos: `/home/user/1Lab-2Lab/PALEIDIMO_INSTRUKCIJOS.md`

### Logai:
- **Android:** Android Studio → Logcat
- **Spring Boot:** IntelliJ IDEA → Run console
- **MySQL:** XAMPP → MySQL logs

---

## Sėkmingo Paleidimo Pavyzdys

Kai viskas veikia teisingai:

```
1. IntelliJ IDEA Console:
   > Started RestApiApplication in 8.452 seconds
   > Tomcat started on port(s): 8080

2. Android Studio Run:
   > BUILD SUCCESSFUL in 45s
   > Installing APK...
   > Launching activity...

3. Emulator/Phone:
   > Food Booking App atsidarė
   > Rodomas Login ekranas
   > Galite prisijungti
```

---

**Sekmes paleisdami aplikaciją! 🚀**

Jei kiltų problemų - žiūrėkite Logcat ir Spring Boot console pranešimus.

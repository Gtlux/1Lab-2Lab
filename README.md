# Maisto Rezervavimo ir Valdymo Sistema

Maisto užsakymo ir pristatymo valdymo sistema, sukurta naudojant Java, JavaFX ir MySQL.

## Projekto Struktūra

```
1Lab-2Lab/
├── src/
│   └── main/
│       ├── java/
│       │   └── com/
│       │       └── foodbooking/
│       │           ├── MainApp.java                 # Pagrindinė aplikacijos klasė
│       │           ├── model/                       # Domain modeliai
│       │           │   ├── User.java
│       │           │   ├── UserRole.java
│       │           │   ├── Restaurant.java
│       │           │   ├── MenuItem.java
│       │           │   ├── Order.java
│       │           │   ├── OrderItem.java
│       │           │   ├── OrderStatus.java
│       │           │   └── Message.java
│       │           ├── dao/                         # Data Access Objects
│       │           │   ├── UserDAO.java
│       │           │   ├── RestaurantDAO.java
│       │           │   ├── MenuItemDAO.java
│       │           │   ├── OrderDAO.java
│       │           │   ├── OrderItemDAO.java
│       │           │   └── MessageDAO.java
│       │           ├── controller/                  # JavaFX Controllers
│       │           │   ├── LoginController.java
│       │           │   ├── RegisterController.java
│       │           │   ├── MainController.java
│       │           │   ├── UsersTabController.java
│       │           │   ├── UserDialogController.java
│       │           │   ├── RestaurantsTabController.java
│       │           │   ├── RestaurantDialogController.java
│       │           │   ├── MenuItemsTabController.java
│       │           │   ├── MenuItemDialogController.java
│       │           │   ├── OrdersTabController.java
│       │           │   ├── OrderDialogController.java
│       │           │   └── OrderViewDialogController.java
│       │           └── util/                        # Utility klasės
│       │               ├── DatabaseConnection.java
│       │               ├── SessionManager.java
│       │               ├── AlertHelper.java
│       │               └── ValidationHelper.java
│       └── resources/
│           └── fxml/                                # FXML layoutai
│               ├── Login.fxml
│               ├── Register.fxml
│               ├── Main.fxml
│               ├── UsersTab.fxml
│               ├── UserDialog.fxml
│               ├── RestaurantsTab.fxml
│               ├── RestaurantDialog.fxml
│               ├── MenuItemsTab.fxml
│               ├── MenuItemDialog.fxml
│               ├── OrdersTab.fxml
│               ├── OrderDialog.fxml
│               └── OrderViewDialog.fxml
├── database/
│   └── schema.sql                                   # MySQL duomenų bazės schema
├── pom.xml                                          # Maven konfigūracija
└── README.md                                        # Šis failas
```

## Funkcionalumas

### 1. Vartotojų Tipai
- **Klientai**: Užsako maistą, seka užsakymo statusą
- **Restoranų Savininkai**: Valdo savo restoraną ir meniu
- **Vairuotojai**: Paima ir pristato užsakymus
- **Administratoriai**: Pilna prieiga prie visos sistemos

### 2. CRUD Funkcionalumas
Visos klasės turi pilną CRUD funkcionalumą:
- **Create**: Naujų įrašų kūrimas
- **Read**: Duomenų skaitymas (pilnas ir filtruotas)
- **Update**: Duomenų atnaujinimas
- **Delete**: Duomenų šalinimas

### 3. GUI Komponentai
- **Pagrindinis langas** su menu bar ir tabs sistema
- **Dialogai**: User, Restaurant, MenuItem, Order
- **Lentelės**: Visi duomenys rodomi lentelėse
- **Filtravimas**: Duomenų filtravimas pagal parametrus
- **Validacija**: Input validacija su klaidų pranešimais

### 4. Duomenų Bazė
- **MySQL** su XAMPP
- **Pilnas CRUD** su duomenų baze
- **Foreign keys** ir relationship management
- **Sample data** testavimui

## Reikalavimai

- Java 17 arba naujesnė
- Maven 3.6+
- XAMPP (MySQL)
- IntelliJ IDEA (rekomenduojama)

## Instaliacijos Instrukcijos

### 1. Duomenų Bazės Paruošimas

1. Paleiskite XAMPP ir įjunkite MySQL serverį
2. Atidarykite phpMyAdmin (http://localhost/phpmyadmin)
3. Importuokite duomenų bazę:
   - Atidarykite SQL skirtuką
   - Nukopijuokite ir paleiskite `database/schema.sql` turinį
   - Arba importuokite failą per Import skirtuką

### 2. Projekto Paruošimas

```bash
# Clone arba nukopijuokite projektą
cd 1Lab-2Lab

# Sukompiliuokite projektą su Maven
mvn clean install

# Arba atidarykite IntelliJ IDEA:
# File -> Open -> Pasirinkite projekto katalogą
# Maven automatiškai parsisiųs dependencies
```

### 3. Paleidimas

#### Per IntelliJ IDEA:
1. Atidarykite projektą IntelliJ IDEA
2. Palaukite, kol Maven parsisiųs visus dependencies
3. Raskite `MainApp.java` klasę
4. Dešiniu pelės mygtuku spustelėkite ant failo
5. Pasirinkite "Run 'MainApp.main()'"

#### Per Maven:
```bash
mvn javafx:run
```

#### Per JAR failą:
```bash
mvn clean package
java -jar target/food-booking-system-1.0-SNAPSHOT.jar
```

## Testiniai Prisijungimai

Sistema turi testinių vartotojų:

| Vartotojo Vardas | Slaptažodis | Rolė |
|-----------------|-------------|------|
| admin | password123 | Administratorius |
| owner1 | password123 | Restorano Savininkas |
| owner2 | password123 | Restorano Savininkas |
| driver1 | password123 | Vairuotojas |
| driver2 | password123 | Vairuotojas |
| client1 | password123 | Klientas |
| client2 | password123 | Klientas |

## Naudojimo Instrukcijos

### Administratoriui
1. Prisijunkite kaip `admin`
2. Matote visus tabs: Vartotojai, Restoranai, Meniu, Užsakymai
3. Galite valdyti visus duomenis sistemoje
4. Naudokite CRUD mygtukus: Pridėti, Redaguoti, Ištrinti, Atnaujinti

### Restorano Savininkui
1. Prisijunkite kaip `owner1` arba `owner2`
2. Matote: Mano restoranas, Meniu, Užsakymai
3. Galite valdyti tik savo restorano duomenis
4. Galite keisti užsakymų statusus

### Vairuotojui
1. Prisijunkite kaip `driver1` arba `driver2`
2. Matote: Užsakymai
3. Galite pasiimti užsakymus ir keisti jų statusus
4. Matote tik savo priskirtus užsakymus

### Klientui
1. Prisijunkite kaip `client1` arba `client2`
2. Matote: Restoranai, Meniu, Mano užsakymai
3. Galite kurti naujus užsakymus
4. Sekti savo užsakymų statusus

## Technologijos

- **Java 17**: Programavimo kalba
- **JavaFX 21**: GUI framework
- **Maven**: Build tool
- **MySQL 8**: Duomenų bazė
- **Lombok**: Code generation
- **BCrypt**: Password hashing

## Klasių Diagrama

Sistema naudoja šiuos pagrindinius ryšius:

```
User (1) -----> (N) Restaurant (savininkas)
Restaurant (1) -----> (N) MenuItem (kompozicija)
Order (N) -----> (1) User (klientas)
Order (N) -----> (1) Restaurant
Order (N) -----> (1) User (vairuotojas)
Order (1) -----> (N) OrderItem (kompozicija)
OrderItem (N) -----> (1) MenuItem
Message (N) -----> (1) Order
Message (N) -----> (1) User (siuntėjas)
Message (N) -----> (1) User (gavėjas)
```

## Problemos Sprendimas

### Duomenų Bazės Klaidos
- Patikrinkite, ar XAMPP MySQL serveris veikia
- Patikrinkite prisijungimo duomenis `DatabaseConnection.java`:
  - URL: `jdbc:mysql://localhost:3306/food_booking_db`
  - Username: `root`
  - Password: `` (tuščias)

### Maven Klaidos
- Ištrinkite `.m2/repository` aplanką ir bandykite iš naujo
- Paleiskite: `mvn clean install -U`

### JavaFX Klaidos
- Įsitikinkite, kad naudojate Java 17+
- Patikrinkite, ar Maven parsisiuntė JavaFX dependencies

## Autoriai

Projektas sukurtas kaip laboratorinis darbas.

## Licencija

Šis projektas skirtas edukaciniais tikslais.

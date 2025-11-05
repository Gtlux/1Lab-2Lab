# Projekto Struktūra

## Sukurti Failai

### 1. Konfigūracijos Failai
- `pom.xml` - Maven dependencies ir build konfigūracija
- `.gitignore` - Git ignore taisyklės
- `README.md` - Projekto dokumentacija

### 2. Duomenų Bazė
- `database/schema.sql` - MySQL duomenų bazės schema su sample data

### 3. Domain Model (src/main/java/com/foodbooking/model/)
- `User.java` - Vartotojo modelis
- `UserRole.java` - Vartotojo rolių enum
- `Restaurant.java` - Restorano modelis
- `MenuItem.java` - Meniu elemento modelis
- `Order.java` - Užsakymo modelis
- `OrderItem.java` - Užsakymo elemento modelis
- `OrderStatus.java` - Užsakymo statuso enum
- `Message.java` - Žinutės modelis

### 4. Data Access Objects (src/main/java/com/foodbooking/dao/)
- `UserDAO.java` - Vartotojų CRUD operacijos
- `RestaurantDAO.java` - Restoranų CRUD operacijos
- `MenuItemDAO.java` - Meniu elementų CRUD operacijos
- `OrderDAO.java` - Užsakymų CRUD operacijos
- `OrderItemDAO.java` - Užsakymo elementų CRUD operacijos
- `MessageDAO.java` - Žinučių CRUD operacijos

### 5. Utility Classes (src/main/java/com/foodbooking/util/)
- `DatabaseConnection.java` - Duomenų bazės prisijungimas
- `SessionManager.java` - Sesijos valdymas
- `AlertHelper.java` - Alert dialogų pagalbinė klasė
- `ValidationHelper.java` - Input validacijos funkcijos

### 6. JavaFX Controllers (src/main/java/com/foodbooking/controller/)
- `LoginController.java` - Prisijungimo langas
- `RegisterController.java` - Registracijos langas
- `MainController.java` - Pagrindinis langas
- `UsersTabController.java` - Vartotojų tab
- `UserDialogController.java` - Vartotojo redagavimo dialogs
- `RestaurantsTabController.java` - Restoranų tab
- `RestaurantDialogController.java` - Restorano redagavimo dialogs
- `MenuItemsTabController.java` - Meniu elementų tab
- `MenuItemDialogController.java` - Meniu elemento redagavimo dialogs
- `OrdersTabController.java` - Užsakymų tab
- `OrderDialogController.java` - Užsakymo kūrimo dialogs
- `OrderViewDialogController.java` - Užsakymo peržiūros dialogs

### 7. FXML Layouts (src/main/resources/fxml/)
- `Login.fxml` - Prisijungimo forma
- `Register.fxml` - Registracijos forma
- `Main.fxml` - Pagrindinis langas su menu bar ir tabs
- `UsersTab.fxml` - Vartotojų valdymo tab
- `UserDialog.fxml` - Vartotojo dialogs
- `RestaurantsTab.fxml` - Restoranų valdymo tab
- `RestaurantDialog.fxml` - Restorano dialogs
- `MenuItemsTab.fxml` - Meniu elementų valdymo tab
- `MenuItemDialog.fxml` - Meniu elemento dialogs
- `OrdersTab.fxml` - Užsakymų valdymo tab
- `OrderDialog.fxml` - Užsakymo kūrimo dialogs
- `OrderViewDialog.fxml` - Užsakymo peržiūros dialogs

### 8. Main Application
- `src/main/java/com/foodbooking/MainApp.java` - JavaFX Application klasė

## Funkcionalumas

### CRUD Operacijos
Visos klasės turi pilną CRUD funkcionalumą:
- **Users**: Vartotojų valdymas (admin)
- **Restaurants**: Restoranų valdymas (admin, savininkai)
- **MenuItems**: Meniu valdymas (admin, savininkai)
- **Orders**: Užsakymų valdymas (visi)
- **OrderItems**: Užsakymo elementų valdymas
- **Messages**: Žinučių sistema (ateityje)

### GUI Komponentai
✅ Pagrindinis langas
✅ Menu Bar
✅ Tabs sistema
✅ Dialogai (perspėjimai, klaidos, patvirtinimai)
✅ Papildomi langai (dialogs)
✅ Standartiniai elementai (TextFields, Buttons, CheckBox)
✅ Sudėtingesni elementai (TableView, ComboBox)
✅ Input validacija
✅ Filtravimas

### Duomenų Bazė
✅ Prisijungimas prie DB
✅ Duomenų gavimas (pilnas ir filtruotas)
✅ Duomenų įrašymas
✅ Duomenų šalinimas
✅ Duomenų redagavimas

## Naudotos Technologijos

- **Java 17**
- **JavaFX 21** - GUI
- **Maven** - Build tool
- **MySQL 8** - Duomenų bazė
- **Lombok** - Code generation
- **BCrypt** - Password hashing

## Klasių Ryšiai

### Kompozicija
- `Restaurant` → `MenuItem` (restoranas turi meniu elementus)
- `Order` → `OrderItem` (užsakymas turi užsakymo elementus)

### Agregacija
- `User` → `Restaurant` (savininkas valdo restoraną)
- `User` → `Order` (klientas/vairuotojas turi užsakymus)
- `Restaurant` → `Order` (restoranas gauna užsakymus)

## OOP Principai

### Inkapsuliacija
- Visi laukai private su Lombok @Data
- Getter/Setter metodai
- Validacija per utility klases

### Paveldėjimas
- Bazinė User klasė su role hierarchy
- Bendri DAO metodai

### Polimorfizmas
- UserRole enum su display names
- OrderStatus enum su display names

### Abstrakcija
- DAO pattern
- Session management
- Database connection handling

## Užbaigti Reikalavimai

### Lab 1 (GUI + CRUD be DB)
✅ 1. Klasių diagrama (2 balai)
✅ 2. Klasės su kintamaisiais ir ryšiais (1 balas)
✅ 3. Pagrindinis + papildomi langai (1 balas)
✅ 4. Menu/Tabs sistema (1 balas)
✅ 5. Standartiniai elementai (1 balas)
✅ 6. Sudėtingesni elementai (1 balas)
✅ 7. Input validacija (1 balas)
✅ 8. CRUD funkcionalumas GUI (2 balai)

### Lab 2 (Database Integration)
✅ 1. DB prisijungimas (1.25 balai)
✅ 2. Duomenų gavimas (1.25 balai)
✅ 3. Duomenų įrašymas (2.5 balo)
✅ 4. Duomenų šalinimas (2.5 balo)
✅ 5. Duomenų redagavimas (2.5 balo)

## Kaip Paleisti

1. **Duomenų bazė**:
   ```bash
   # Paleisti XAMPP MySQL
   # phpMyAdmin: importuoti database/schema.sql
   ```

2. **Build**:
   ```bash
   mvn clean install
   ```

3. **Run**:
   ```bash
   mvn javafx:run
   ```

Arba per IntelliJ IDEA: Run `MainApp.main()`

## Testiniai Prisijungimai

- admin / password123 (Administratorius)
- owner1 / password123 (Savininkas)
- driver1 / password123 (Vairuotojas)
- client1 / password123 (Klientas)

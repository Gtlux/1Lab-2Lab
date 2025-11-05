# Klasių Diagrama

## Domain Model Klasės

### User
```
User
----
- id: Integer
- username: String
- password: String
- email: String
- fullName: String
- phoneNumber: String
- role: UserRole
- restaurantId: Integer (nullable)
- active: boolean
- createdAt: LocalDateTime
- updatedAt: LocalDateTime
----
+ isClient(): boolean
+ isRestaurantOwner(): boolean
+ isDriver(): boolean
+ isAdministrator(): boolean
```

**Ryšiai:**
- `User (1) --> (0..1) Restaurant` - Restaurant owner relationship

### UserRole (Enum)
```
<<enumeration>>
UserRole
----
CLIENT
RESTAURANT_OWNER
DRIVER
ADMINISTRATOR
----
+ getDisplayName(): String
```

### Restaurant
```
Restaurant
----
- id: Integer
- name: String
- address: String
- phoneNumber: String
- email: String
- description: String
- ownerId: Integer
- active: boolean
- createdAt: LocalDateTime
- updatedAt: LocalDateTime
- menuItems: List<MenuItem>
----
+ addMenuItem(item: MenuItem): void
+ removeMenuItem(item: MenuItem): void
```

**Ryšiai:**
- `Restaurant (1) *--> (N) MenuItem` - Composition
- `Restaurant (N) --> (1) User` - Owner relationship

### MenuItem
```
MenuItem
----
- id: Integer
- restaurantId: Integer
- name: String
- description: String
- price: BigDecimal
- category: String
- available: boolean
- imageUrl: String
- createdAt: LocalDateTime
- updatedAt: LocalDateTime
```

**Ryšiai:**
- `MenuItem (N) --> (1) Restaurant` - Belongs to restaurant

### Order
```
Order
----
- id: Integer
- clientId: Integer
- restaurantId: Integer
- driverId: Integer (nullable)
- status: OrderStatus
- totalAmount: BigDecimal
- deliveryAddress: String
- notes: String
- createdAt: LocalDateTime
- updatedAt: LocalDateTime
- deliveredAt: LocalDateTime (nullable)
- orderItems: List<OrderItem>
- clientName: String
- restaurantName: String
- driverName: String
----
+ addOrderItem(item: OrderItem): void
+ removeOrderItem(item: OrderItem): void
+ calculateTotal(): void
+ updateStatus(status: OrderStatus): void
```

**Ryšiai:**
- `Order (1) *--> (N) OrderItem` - Composition
- `Order (N) --> (1) User` - Client relationship
- `Order (N) --> (1) Restaurant` - Restaurant relationship
- `Order (N) --> (0..1) User` - Driver relationship (optional)

### OrderItem
```
OrderItem
----
- id: Integer
- orderId: Integer
- menuItemId: Integer
- menuItemName: String
- price: BigDecimal
- quantity: Integer
- subtotal: BigDecimal
- notes: String
----
+ calculateSubtotal(): void
```

**Ryšiai:**
- `OrderItem (N) --> (1) Order` - Belongs to order
- `OrderItem (N) --> (1) MenuItem` - References menu item

### OrderStatus (Enum)
```
<<enumeration>>
OrderStatus
----
PENDING
CONFIRMED
PREPARING
READY
PICKED_UP
DELIVERING
DELIVERED
CANCELLED
----
+ getDisplayName(): String
```

### Message
```
Message
----
- id: Integer
- orderId: Integer
- senderId: Integer
- receiverId: Integer
- content: String
- sentAt: LocalDateTime
- isRead: boolean
- senderName: String
- receiverName: String
```

**Ryšiai:**
- `Message (N) --> (1) Order` - Related to order
- `Message (N) --> (1) User` - Sender relationship
- `Message (N) --> (1) User` - Receiver relationship

## DAO Klasės

### UserDAO
```
UserDAO
----
+ createUser(user: User): boolean
+ getUserById(id: int): User
+ getUserByUsername(username: String): User
+ getAllUsers(): List<User>
+ getUsersByRole(role: UserRole): List<User>
+ updateUser(user: User): boolean
+ updatePassword(userId: int, password: String): boolean
+ deleteUser(id: int): boolean
+ authenticate(username: String, password: String): User
```

### RestaurantDAO
```
RestaurantDAO
----
+ createRestaurant(restaurant: Restaurant): boolean
+ getRestaurantById(id: int): Restaurant
+ getAllRestaurants(): List<Restaurant>
+ getActiveRestaurants(): List<Restaurant>
+ getRestaurantsByOwnerId(ownerId: int): List<Restaurant>
+ updateRestaurant(restaurant: Restaurant): boolean
+ deleteRestaurant(id: int): boolean
```

### MenuItemDAO
```
MenuItemDAO
----
+ createMenuItem(item: MenuItem): boolean
+ getMenuItemById(id: int): MenuItem
+ getAllMenuItems(): List<MenuItem>
+ getMenuItemsByRestaurantId(restaurantId: int): List<MenuItem>
+ getAvailableMenuItems(restaurantId: int): List<MenuItem>
+ updateMenuItem(item: MenuItem): boolean
+ deleteMenuItem(id: int): boolean
```

### OrderDAO
```
OrderDAO
----
+ createOrder(order: Order): boolean
+ getOrderById(id: int): Order
+ getAllOrders(): List<Order>
+ getOrdersByClientId(clientId: int): List<Order>
+ getOrdersByRestaurantId(restaurantId: int): List<Order>
+ getOrdersByDriverId(driverId: int): List<Order>
+ getAvailableOrdersForDrivers(): List<Order>
+ updateOrder(order: Order): boolean
+ assignDriver(orderId: int, driverId: int): boolean
+ deleteOrder(id: int): boolean
```

### OrderItemDAO
```
OrderItemDAO
----
+ createOrderItem(item: OrderItem): boolean
+ getOrderItemById(id: int): OrderItem
+ getOrderItemsByOrderId(orderId: int): List<OrderItem>
+ updateOrderItem(item: OrderItem): boolean
+ deleteOrderItem(id: int): boolean
+ deleteOrderItemsByOrderId(orderId: int): boolean
```

### MessageDAO
```
MessageDAO
----
+ createMessage(message: Message): boolean
+ getMessageById(id: int): Message
+ getMessagesByOrderId(orderId: int): List<Message>
+ getMessagesByUserId(userId: int): List<Message>
+ updateMessage(message: Message): boolean
+ markAsRead(messageId: int): boolean
+ deleteMessage(id: int): boolean
```

## Utility Klasės

### DatabaseConnection
```
DatabaseConnection
----
- URL: String (static)
- USER: String (static)
- PASSWORD: String (static)
- connection: Connection (static)
----
+ getConnection(): Connection (static)
+ closeConnection(): void (static)
+ testConnection(): boolean (static)
```

### SessionManager (Singleton)
```
SessionManager
----
- instance: SessionManager (static)
- currentUser: User
----
+ getInstance(): SessionManager (static)
+ setCurrentUser(user: User): void
+ getCurrentUser(): User
+ isLoggedIn(): boolean
+ logout(): void
+ isClient(): boolean
+ isRestaurantOwner(): boolean
+ isDriver(): boolean
+ isAdministrator(): boolean
```

### AlertHelper
```
AlertHelper
----
+ showError(title: String, content: String): void (static)
+ showWarning(title: String, content: String): void (static)
+ showInfo(title: String, content: String): void (static)
+ showSuccess(title: String, content: String): void (static)
+ showConfirmation(title: String, content: String): boolean (static)
+ showTextInputDialog(...): Optional<String> (static)
```

### ValidationHelper
```
ValidationHelper
----
+ isValidEmail(email: String): boolean (static)
+ isValidPhone(phone: String): boolean (static)
+ isNotEmpty(value: String): boolean (static)
+ isValidPassword(password: String): boolean (static)
+ isValidPrice(price: String): boolean (static)
+ isValidQuantity(quantity: String): boolean (static)
+ getValidationMessage(...): String (static)
```

## Controller Klasės

Kiekvienas controller atitinka savo FXML failą ir valdo UI logiką.

### Main Controllers
- `LoginController` - Prisijungimas
- `RegisterController` - Registracija
- `MainController` - Pagrindinis langas

### Tab Controllers
- `UsersTabController` - Vartotojų valdymas
- `RestaurantsTabController` - Restoranų valdymas
- `MenuItemsTabController` - Meniu valdymas
- `OrdersTabController` - Užsakymų valdymas

### Dialog Controllers
- `UserDialogController` - Vartotojo CRUD
- `RestaurantDialogController` - Restorano CRUD
- `MenuItemDialogController` - Meniu elemento CRUD
- `OrderDialogController` - Užsakymo kūrimas
- `OrderViewDialogController` - Užsakymo peržiūra

## Ryšių Tipai

### Kompozicija (◆--->)
1. **Restaurant *--> MenuItem**
   - Restaurant turi MenuItem objektus
   - MenuItem negali egzistuoti be Restaurant
   - Ištrinus Restaurant, ištrinami visi MenuItem

2. **Order *--> OrderItem**
   - Order turi OrderItem objektus
   - OrderItem negali egzistuoti be Order
   - Ištrinus Order, ištrinami visi OrderItem

### Agregacija (◇--->)
1. **User --> Restaurant**
   - User (savininkas) valdo Restaurant
   - Restaurant gali egzistuoti be User
   - Ištrinus User, Restaurant gali likti

2. **User --> Order**
   - User (klientas/vairuotojas) turi Order
   - Order gali egzistuoti be User (istoriniai duomenys)

3. **Restaurant --> Order**
   - Restaurant gauna Orders
   - Order susietas su Restaurant

4. **OrderItem --> MenuItem**
   - OrderItem nurodo MenuItem
   - MenuItem gali būti ištrintas, OrderItem lieka su kopija

### Asociacija (----)
- Message <--> User (siuntėjas/gavėjas)
- Message --> Order

## OOP Principai

### Encapsulation
- Private laukai
- Public getter/setter (per Lombok @Data)
- Validation per helper metodus

### Inheritance
- UserRole hierarchy
- OrderStatus hierarchy
- Bendri DAO patterns

### Polymorphism
- Enum display names
- Role-based permissions

### Abstraction
- DAO pattern (data access abstraction)
- Session management
- Database connection handling

## Design Patterns

1. **Singleton**: SessionManager
2. **DAO Pattern**: Visi DAO klasės
3. **MVC Pattern**: Controller - View (FXML) - Model
4. **Factory Pattern**: Alert dialogs
5. **Strategy Pattern**: Role-based permissions

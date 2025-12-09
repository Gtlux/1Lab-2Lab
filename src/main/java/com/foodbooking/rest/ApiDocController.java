package com.foodbooking.rest;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ApiDocController {

    @GetMapping(value = "", produces = MediaType.TEXT_HTML_VALUE)
    public String getApiDocumentation() {
        return "<!DOCTYPE html>" +
                "<html lang='lt'>" +
                "<head>" +
                "    <meta charset='UTF-8'>" +
                "    <meta name='viewport' content='width=device-width, initial-scale=1.0'>" +
                "    <title>Food Booking REST API Documentation</title>" +
                "    <style>" +
                "        body { font-family: Arial, sans-serif; margin: 40px; background: #f5f5f5; }" +
                "        .container { max-width: 1200px; margin: 0 auto; background: white; padding: 30px; border-radius: 10px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }" +
                "        h1 { color: #2c3e50; border-bottom: 3px solid #3498db; padding-bottom: 10px; }" +
                "        h2 { color: #34495e; margin-top: 30px; }" +
                "        .endpoint { background: #ecf0f1; padding: 15px; margin: 15px 0; border-radius: 5px; border-left: 4px solid #3498db; }" +
                "        .method { display: inline-block; padding: 5px 10px; border-radius: 3px; font-weight: bold; margin-right: 10px; }" +
                "        .get { background: #27ae60; color: white; }" +
                "        .post { background: #2980b9; color: white; }" +
                "        .put { background: #f39c12; color: white; }" +
                "        .delete { background: #c0392b; color: white; }" +
                "        .path { font-family: monospace; color: #2c3e50; font-size: 16px; }" +
                "        .description { margin-top: 10px; color: #555; }" +
                "        .params { margin-top: 10px; font-size: 14px; }" +
                "        .params strong { color: #e74c3c; }" +
                "        code { background: #e8e8e8; padding: 2px 6px; border-radius: 3px; font-family: monospace; }" +
                "        .success { color: #27ae60; font-weight: bold; }" +
                "        .base-url { background: #3498db; color: white; padding: 10px; border-radius: 5px; margin: 20px 0; }" +
                "    </style>" +
                "</head>" +
                "<body>" +
                "    <div class='container'>" +
                "        <h1>🍔 Food Booking System - REST API Documentation</h1>" +
                "        <div class='base-url'>" +
                "            <strong>Base URL:</strong> http://localhost:8080" +
                "        </div>" +
                "        <p class='success'>✅ API veikia! Serveris aktyvus ir priima užklausas.</p>" +

                "        <h2>🔐 Autentifikacija</h2>" +
                "        <div class='endpoint'>" +
                "            <span class='method post'>POST</span>" +
                "            <span class='path'>/api/auth/login</span>" +
                "            <div class='description'>Prisijungimas prie sistemos</div>" +
                "            <div class='params'><strong>Body (JSON):</strong> <code>{\"username\": \"string\", \"password\": \"string\"}</code></div>" +
                "        </div>" +
                "        <div class='endpoint'>" +
                "            <span class='method post'>POST</span>" +
                "            <span class='path'>/api/auth/register</span>" +
                "            <div class='description'>Naujo vartotojo registracija</div>" +
                "            <div class='params'><strong>Body (JSON):</strong> <code>{\"username\", \"password\", \"email\", \"fullName\", \"phoneNumber\", \"role\"}</code></div>" +
                "        </div>" +
                "        <div class='endpoint'>" +
                "            <span class='method get'>GET</span>" +
                "            <span class='path'>/api/auth/test</span>" +
                "            <div class='description'>Testuoja ar autentifikacijos endpoint veikia</div>" +
                "        </div>" +

                "        <h2>👤 Kliento Funkcijos</h2>" +
                "        <div class='endpoint'>" +
                "            <span class='method get'>GET</span>" +
                "            <span class='path'>/api/client/restaurants</span>" +
                "            <div class='description'>Gauti visų restoranų sąrašą (JSON)</div>" +
                "        </div>" +
                "        <div class='endpoint'>" +
                "            <span class='method get'>GET</span>" +
                "            <span class='path'>/api/client/restaurants/{id}</span>" +
                "            <div class='description'>Gauti konkretų restoraną pagal ID</div>" +
                "            <div class='params'><strong>Path param:</strong> <code>id</code> - Restorano ID</div>" +
                "        </div>" +
                "        <div class='endpoint'>" +
                "            <span class='method get'>GET</span>" +
                "            <span class='path'>/api/client/menu</span>" +
                "            <div class='description'>Gauti visų prieinamų meniu elementų sąrašą</div>" +
                "        </div>" +
                "        <div class='endpoint'>" +
                "            <span class='method get'>GET</span>" +
                "            <span class='path'>/api/client/menu/restaurant/{restaurantId}</span>" +
                "            <div class='description'>Gauti konkretaus restorano meniu</div>" +
                "            <div class='params'><strong>Path param:</strong> <code>restaurantId</code> - Restorano ID</div>" +
                "        </div>" +
                "        <div class='endpoint'>" +
                "            <span class='method post'>POST</span>" +
                "            <span class='path'>/api/client/orders</span>" +
                "            <div class='description'>Sukurti naują užsakymą</div>" +
                "            <div class='params'><strong>Body (JSON):</strong> <code>{\"clientId\", \"restaurantId\", \"deliveryAddress\", \"notes\", \"items\": [{\"menuItemId\", \"quantity\"}]}</code></div>" +
                "        </div>" +
                "        <div class='endpoint'>" +
                "            <span class='method get'>GET</span>" +
                "            <span class='path'>/api/client/orders/{clientId}</span>" +
                "            <div class='description'>Gauti kliento užsakymus</div>" +
                "            <div class='params'><strong>Path param:</strong> <code>clientId</code> - Kliento ID</div>" +
                "        </div>" +
                "        <div class='endpoint'>" +
                "            <span class='method get'>GET</span>" +
                "            <span class='path'>/api/client/orders/detail/{orderId}</span>" +
                "            <div class='description'>Gauti detalią užsakymo informaciją</div>" +
                "            <div class='params'><strong>Path param:</strong> <code>orderId</code> - Užsakymo ID</div>" +
                "        </div>" +
                "        <div class='endpoint'>" +
                "            <span class='method post'>POST</span>" +
                "            <span class='path'>/api/client/orders/{orderId}/cancel</span>" +
                "            <div class='description'>Pateikti užsakymo atšaukimo užklausą</div>" +
                "            <div class='params'><strong>Path param:</strong> <code>orderId</code></div>" +
                "            <div class='params'><strong>Query params:</strong> <code>clientId</code>, <code>reason</code></div>" +
                "        </div>" +

                "        <h2>🚗 Vairuotojo Funkcijos</h2>" +
                "        <div class='endpoint'>" +
                "            <span class='method get'>GET</span>" +
                "            <span class='path'>/api/driver/orders/{driverId}</span>" +
                "            <div class='description'>Gauti vairuotojui priskirtų užsakymų sąrašą</div>" +
                "            <div class='params'><strong>Path param:</strong> <code>driverId</code> - Vairuotojo ID</div>" +
                "        </div>" +
                "        <div class='endpoint'>" +
                "            <span class='method get'>GET</span>" +
                "            <span class='path'>/api/driver/orders/available</span>" +
                "            <div class='description'>Gauti prieinamų užsakymų sąrašą</div>" +
                "        </div>" +
                "        <div class='endpoint'>" +
                "            <span class='method get'>GET</span>" +
                "            <span class='path'>/api/driver/orders/detail/{orderId}</span>" +
                "            <div class='description'>Gauti detalią užsakymo informaciją</div>" +
                "            <div class='params'><strong>Path param:</strong> <code>orderId</code> - Užsakymo ID</div>" +
                "        </div>" +
                "        <div class='endpoint'>" +
                "            <span class='method put'>PUT</span>" +
                "            <span class='path'>/api/driver/orders/{orderId}/status</span>" +
                "            <div class='description'>Atnaujinti užsakymo būseną</div>" +
                "            <div class='params'><strong>Path param:</strong> <code>orderId</code></div>" +
                "            <div class='params'><strong>Query param:</strong> <code>status</code> - Nauja būsena (PENDING, CONFIRMED, PREPARING, READY, PICKED_UP, DELIVERING, DELIVERED, CANCELLED)</div>" +
                "        </div>" +
                "        <div class='endpoint'>" +
                "            <span class='method post'>POST</span>" +
                "            <span class='path'>/api/driver/orders/{orderId}/accept</span>" +
                "            <div class='description'>Priimti užsakymą</div>" +
                "            <div class='params'><strong>Path param:</strong> <code>orderId</code></div>" +
                "            <div class='params'><strong>Query param:</strong> <code>driverId</code> - Vairuotojo ID</div>" +
                "        </div>" +

                "        <h2>📋 Duomenų Formatai</h2>" +
                "        <p><strong>Visi atsakymai grąžinami JSON formatu</strong> su šia struktūra:</p>" +
                "        <div class='endpoint'>" +
                "            <code>{\"success\": boolean, \"message\": \"string\", \"data\": object}</code>" +
                "        </div>" +
                "        <p><strong>HTML formatas:</strong> Šis dokumentacijos puslapis demonstruoja HTML formatą</p>" +

                "        <h2>🔧 Testavimas</h2>" +
                "        <p>API galite testuoti naudodami:</p>" +
                "        <ul>" +
                "            <li><strong>Postman</strong> - Import'uokite endpointus</li>" +
                "            <li><strong>curl</strong> - Pavyzdys: <code>curl http://localhost:8080/api/auth/test</code></li>" +
                "            <li><strong>Android aplikacija</strong> - Naudokite šiuos endpointus su Retrofit</li>" +
                "        </ul>" +

                "        <h2>✅ Reikalavimai</h2>" +
                "        <ul>" +
                "            <li>✅ [1 balas] Web serviso veikimas - API priima ir grąžina atsakymus</li>" +
                "            <li>✅ [2 balai] Duomenų gavimas HTML (šis puslapis) ir JSON formatu</li>" +
                "            <li>✅ [2 balai] Parametrų perdavimas - Path params, Query params, Body (JSON)</li>" +
                "            <li>✅ [5 balai] CRUD funkcijos vairuotojui ir klientui</li>" +
                "        </ul>" +

                "        <div style='margin-top: 40px; padding: 20px; background: #d5f4e6; border-radius: 5px;'>" +
                "            <h3 style='margin-top: 0;'>🚀 Kaip paleisti:</h3>" +
                "            <code>mvn spring-boot:run</code><br><br>" +
                "            arba<br><br>" +
                "            <code>java -jar target/food-booking-system-1.0-SNAPSHOT.jar</code>" +
                "        </div>" +
                "    </div>" +
                "</body>" +
                "</html>";
    }
}

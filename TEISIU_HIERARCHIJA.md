# Vartotojų Teisės ir Apribojimai

Sistema turi **4 vartotojų roles** su skirtingomis teisėmis.

---

## 🔐 Teisių Hierarchija

### 1️⃣ **ADMINISTRATORIAI** (Pilna prieiga)

**Matomi Tab'ai:**
- ✅ Vartotojai
- ✅ Restoranai
- ✅ Meniu
- ✅ Užsakymai

**Teisės:**

| Funkcija | Vartotojai | Restoranai | Meniu | Užsakymai |
|----------|-----------|------------|-------|-----------|
| **Create** (Pridėti) | ✅ | ✅ | ✅ | ✅ |
| **Read** (Skaityti) | ✅ | ✅ | ✅ | ✅ |
| **Update** (Redaguoti) | ✅ | ✅ | ✅ | ✅ |
| **Delete** (Ištrinti) | ✅ | ✅ | ✅ | ✅ |

**Administratoriai gali:**
- ✅ Pridėti naujus restoranus
- ✅ Ištrinti bet kokį restoraną
- ✅ Redaguoti bet kokį restoraną
- ✅ Valdyti visus vartotojus
- ✅ Valdyti visus meniu elementus
- ✅ Matyti ir valdyti visus užsakymus
- ✅ Keisti bet kokį užsakymo statusą

---

### 2️⃣ **RESTORANŲ SAVININKAI** (Ribota prieiga)

**Matomi Tab'ai:**
- ✅ Mano restoranas
- ✅ Meniu
- ✅ Užsakymai

**Teisės:**

| Funkcija | Restoranas | Meniu | Užsakymai |
|----------|-----------|-------|-----------|
| **Create** (Pridėti) | ❌ | ✅ (savo) | ❌ |
| **Read** (Skaityti) | ✅ (savo) | ✅ (savo) | ✅ (savo) |
| **Update** (Redaguoti) | ✅ (savo) | ✅ (savo) | ✅ (savo) |
| **Delete** (Ištrinti) | ❌ | ✅ (savo) | ❌ |

**Restoranų savininkai gali:**
- ✅ Redaguoti **savo** restorano informaciją
- ✅ Pridėti naujus meniu elementus **savo** restoranui
- ✅ Redaguoti **savo** restorano meniu
- ✅ Ištrinti **savo** restorano meniu elementus
- ✅ Matyti **savo** restorano užsakymus
- ✅ Keisti **savo** restorano užsakymų statusus

**Restoranų savininkai NEGALI:**
- ❌ Pridėti naujų restoranų
- ❌ Ištrinti savo restorano (tik admin)
- ❌ Matyti kitų restoranų informacijos
- ❌ Valdyti kitų restoranų meniu
- ❌ Matyti kitų restoranų užsakymų
- ❌ Valdyti vartotojų

---

### 3️⃣ **VAIRUOTOJAI** (Užsakymų prieiga)

**Matomi Tab'ai:**
- ✅ Užsakymai

**Teisės:**

| Funkcija | Užsakymai |
|----------|-----------|
| **Create** (Pridėti) | ❌ |
| **Read** (Skaityti) | ✅ (prieinami + savo) |
| **Update** (Redaguoti) | ✅ (savo) |
| **Delete** (Ištrinti) | ❌ |

**Vairuotojai gali:**
- ✅ Matyti **prieinamus** užsakymus (status: READY, driver_id = NULL)
- ✅ Pasiimti užsakymą (priskiria save kaip vairuotoją)
- ✅ Keisti **savo** užsakymų statusus (PICKED_UP → DELIVERING → DELIVERED)
- ✅ Matyti **savo** priskirtus užsakymus

**Vairuotojai NEGALI:**
- ❌ Kurti naujų užsakymų
- ❌ Ištrinti užsakymų
- ❌ Matyti kitų vairuotojų užsakymų
- ❌ Valdyti restoranų ar meniu
- ❌ Valdyti vartotojų

---

### 4️⃣ **KLIENTAI** (Minimali prieiga)

**Matomi Tab'ai:**
- ✅ Mano užsakymai

**Teisės:**

| Funkcija | Užsakymai |
|----------|-----------|
| **Create** (Pridėti) | ✅ (savo) |
| **Read** (Skaityti) | ✅ (savo) |
| **Update** (Redaguoti) | ❌ |
| **Delete** (Ištrinti) | ✅ (savo) |

**Klientai gali:**
- ✅ Kurti **naujus** užsakymus
- ✅ Matyti **savo** užsakymus
- ✅ Sekti **savo** užsakymų statusus
- ✅ Ištrinti **savo** užsakymus (tik jei status: PENDING)

**Klientai NEGALI:**
- ❌ Matyti Restoranų valdymo tab'ą
- ❌ Matyti Meniu valdymo tab'ą
- ❌ Pridėti restoranų
- ❌ Valdyti meniu
- ❌ Matyti kitų klientų užsakymų
- ❌ Keisti užsakymų statusų
- ❌ Valdyti vartotojų

---

## 🛡️ Saugumo Mechanizmai

### 1. UI Lygmenyje
- Mygtukai paslėpti pagal roles (`setVisible(false)`, `setManaged(false)`)
- Tab'ai rodomi tik autorizuotiems vartotojams
- Dialogo langai adaptuojasi pagal role

### 2. Backend Lygmenyje
- Kiekvienas controller metodas tikrina teises
- Permission check'ai prieš DB operacijas
- Error messages aiškiai informuoja apie teisių trūkumą

### 3. Database Lygmenyje
- Foreign keys užtikrina duomenų integritetą
- Cascade delete tik administratoriams
- User-restaurant relationship per `owner_id`

---

## 📋 Teisių Patikrinimo Matrica

| Veiksmas | Admin | Savininkas | Vairuotojas | Klientas |
|----------|-------|-----------|-------------|----------|
| **Pridėti restoraną** | ✅ | ❌ | ❌ | ❌ |
| **Ištrinti restoraną** | ✅ | ❌ | ❌ | ❌ |
| **Redaguoti restoraną** | ✅ | ✅ (savo) | ❌ | ❌ |
| **Pridėti meniu elementą** | ✅ | ✅ (savo) | ❌ | ❌ |
| **Ištrinti meniu elementą** | ✅ | ✅ (savo) | ❌ | ❌ |
| **Kurti užsakymą** | ✅ | ❌ | ❌ | ✅ |
| **Pasiimti užsakymą** | ❌ | ❌ | ✅ | ❌ |
| **Keisti užsakymo statusą** | ✅ | ✅ (savo rest.) | ✅ (savo) | ❌ |
| **Ištrinti užsakymą** | ✅ | ❌ | ❌ | ✅ (savo) |
| **Valdyti vartotojus** | ✅ | ❌ | ❌ | ❌ |

---

## 🔍 Kaip Patikrinti

### Testiniai Prisijungimai:

```
Admin:         admin / password123
Savininkas:    owner1 / password123
Vairuotojas:   driver1 / password123
Klientas:      client1 / password123
```

### Testuoti:

1. **Administratoriui:**
   - Prisijunkite kaip `admin`
   - Turėtumėte matyti visus 4 tabs
   - Turėtumėte matyti "Pridėti naują" ir "Ištrinti" mygtukus Restoranų tab'e

2. **Savininkui:**
   - Prisijunkite kaip `owner1`
   - Turėtumėte matyti 3 tabs (Mano restoranas, Meniu, Užsakymai)
   - **NETURĖTUMĖTE** matyti "Pridėti naują" ir "Ištrinti" mygtukų Restoranų tab'e
   - Turėtumėte galėti redaguoti TIK savo restoraną

3. **Vairuotojui:**
   - Prisijunkite kaip `driver1`
   - Turėtumėte matyti 1 tab'ą (Užsakymai)
   - Galite pasiimti užsakymus su statusu READY

4. **Klientui:**
   - Prisijunkite kaip `client1`
   - Turėtumėte matyti 1 tab'ą (Mano užsakymai)
   - **NETURĖTUMĖTE** matyti Restoranų ar Meniu tabs
   - Galite kurti naujus užsakymus

---

## ⚠️ Svarbios Pastabos

1. **Restoranų savininkai NEGALI pridėti naujų restoranų** - tik administratoriai
2. **Restoranų savininkai NEGALI ištrinti savo restorano** - tik administratoriai
3. **Klientai nemato Restoranų ir Meniu valdymo** - tik užsakymai
4. **Vairuotojai mato tik užsakymus** - ne restoranus ar meniu

---

## 🐛 Jei Pastebėjote Klaidą

Jei pastebėjote, kad vartotojas gali atlikti veiksmą, kurio neturėtų:

1. Patikrinkite, ar prisijungėte su teisingu vartotoju
2. Atnaujinkite puslapį (Refresh mygtukas)
3. Atsijunkite ir prisijunkite iš naujo
4. Jei problema išlieka - praneškite apie saugumo spragą

---

**Paskutinis atnaujinimas:** 2025-01-05
**Versija:** 1.1 (Po teisių pataisymo)

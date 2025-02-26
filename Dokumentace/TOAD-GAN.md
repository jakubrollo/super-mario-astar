# TOAD-GAN

[⬅ Zpět na přehled](Overview.md)

## Shrnutí článku a jeho účel

- **Repozitář:** [GitHub - TOAD-GAN](https://github.com/Mawiszus/TOAD-GAN)
- **Cíl článku:** Generovat levely z jednoho příkladového levelu pomocí hierarchického GANu s více úrovněmi downsizingu a upsamplingu (SinGAN). 
- **Video od autorky o tomto článku:** [YouTube](https://youtu.be/_bnAtIYVx-s)
- **Náš cíl:**
  - Replikace výsledků a použití MFF A* místo původního Baumgarten agenta pro zjištění počtu dokončitelných levelů.
  - Analyzovat výsledky.


## Popis

**TOAD-GAN** (*Token-based One-shot Arbitrary Dimension Generative Adversarial Network*) je procedurální generátor obsahu, který umožňuje generování herních úrovní na základě jediného trénovacího příkladu. Využívá hierarchickou architekturu založenou na SinGAN-u, která umožňuje generovat úrovně s konzistentním stylem a v libovolné velikosti.

### Jak to funguje

- **Hierarchická trénovací architektura (SinGAN)** – TOAD-GAN se trénuje v několika úrovních downsampling-u a následného upsampling-u, čímž zachovává celkovou strukturu úrovně.
- **Uchování důležitých tokenů** – Abychom nepřicházeli o detaily částí levelu při zmenšování, používá algoritmus specifickou metodu downsamplingu, která chrání klíčové prvky úrovně.
- **Level Authoring** – Uživatel může částečně řídit generaci tím, že zadá určité tokeny v prvních fázích generování. (Lze vytvořit základní layout úrovně a TOAD-GAN vytvoří level podle daného návrhu.)

### Nedostatky

- **Dokončitelnost úrovní** – Vyhodnocena pomocí A* agenta od Baumgartena (který je méně úspěšný než MFF A*).
- **Chybějící záruka hratelnosti** – Vygenerované úrovně můžou být nehratelné.
- **Možné artefakty** – Algoritmus někdy generuje rozbité struktury (např. nekompletní trubky v Super Mario Bros.), což ovlivňuje vizuální (i funkční) kvalitu úrovní.
- **Žádná evoluční optimalizace** – TOAD-GAN se spoléhá pouze na vzory z jednoho příkladu a nemá přímý mechanismus pro optimalizaci dokončitelnosti úrovní.

### Navrhovaná vylepšení

- **Nahrazení A\* agenta od Baumgartena** pro testování hratelnosti agentem **MFF A\***, který ověří, zda je úroveň dokončitelná.
- Testování hratelnosti generovaných levelů na generátorech z více levelů pro zajištění větší obecnosti a rozmanitosti výsledků.


## Související soubory

- [Instalace a použití](Instalace-TOAD.md)  
- [Implementované změny a výsledky](Modifikace-TOAD.md)  

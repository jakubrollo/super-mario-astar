# Pomocné Python funkce

[⬅ Zpět na přehled](Overview.md)

---

Pro tento projekt bylo vytvořeno několik Python skriptů zaměřených na analýzu a práci s daty ze zkoumaných vědeckých článků. Tyto skripty usnadňují předzpracování dat, vizualizaci výsledků apod.

### marioLevelAnalyzer.py
Tento skript slouží k výpočtu potřebného počtu skoků ze stringové reprezentace „grid“ cesty nalezené algoritmem GridSearch. Následně byl přepsán do Javy pro použití při výpočtu fitness funkce založené na počtu grid skoků.

Skok je definován jako maximálně čtyři po sobě jdoucí stoupající prvky (ve sloupci), reprezentované symbolem ‘■’.  
Jako skok se také počítá, když se objeví následující vzor:
```
■ ■ ■  
X - X
```

### marioDagstuhlFrames.py
V původním projektu chyběly potřebné skripty pro předzpracování dat pro trénování modelu. Tento skript zajišťuje generování jednotlivých “frames” z levelů, které slouží jako trénovací data pro GAN.

### marioDagstuhlGraph.py
Skript slouží k vizualizaci dat. Z adresáře obsahujícího soubory typu timeline generuje grafy pro analýzu vývoje fitness hodnot a dalších metrik.

### marioDagstuhlGetLowest.py
Vyhledává nejnižší fitness hodnotu ze zadaného adresáře obsahující timeline soubory a vrací také odpovídající latent vector, který tuto hodnotu dosáhl.

### marioCalculateResult.py
Tento skript slouží k výpočtu úspěšnosti agentů na základě výsledků obsažených v daném souboru, k porovnání výkonnosti dvou agentů (Baumgarten vs. MFF AStarGrid).  
(Původně byl využit pro práci s levely generovanými pomocí TOAD-GAN, ale je univerzální a lze ho aplikovat i na jiné úrovně.)
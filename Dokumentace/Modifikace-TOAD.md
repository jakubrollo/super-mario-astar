# TOAD-GAN - Implementované změny a výsledky

[⬅ Zpět na přehled](Overview.md)

## Implementované změny

- V souboru `GUI.py` (TOAD\_GUI) byla upravena funkce `save_txt.py`, která nyní umožňuje vygenerovat a uložit 100 úrovní naráz.
- Do složky `generators/v2` byl přidán natrénovaný model na levelu `krys-1`.
- Do složky `levels` byly přidány vygenerované levely z modelů vytvořených na levelech `original_1`, `original_3-3` a `krys-1`.
- Také byl vytvořen soubor `random-orig-levels`, který obsahuje 1000 úrovní vygenerovaných náhodně vybraným modelem založeným na některé z originálních úrovní.
- TOAD-GAN nebyl upravován, pouze použit pro trénování vlastních modelů, proto není součástí repozitáře.

## Výsledky

### Generování 100 levelů z originálního levelu 1-1:

- 1 level byl nefunkční a nelze dokončit.
- Baumgarten selhal na 3 dokončitelných levelech (+ 1 nefunkční).
- MFF-A\* Grid selhal pouze na nedokončitelném levelu.
- MFF-A\* Grid měl lepší výkon než Baumgarten.

### Generování 100 levelů z originálního levelu 3-3:

- MFF AStar Grid: **50% win rate**.
- Baumgarten: **14% win rate**, žádné time-outy, **86% proher**.

### Generování 100 levelů z `krys-1`:

- MFF AStar Grid: **23% win rate**.
- Baumgarten: **10% win rate**, **42% timeoutů**, **48% proher**.

### Test na 1000 levelech z náhodně vybraných originálních levelů:

- MFF-A\* Grid selhal na **239 levelech** (**76,1% úspěšnost**).
- Baumgarten selhal na **582 levelech** (**41,8% úspěšnost**).

### Další poznámky

- Nalezeno pár levelů, které dokončil Baumgarten, ale ne MFF-A\* Grid – většina z nich vyžadovala specifické jumpBoosty.
- Mezi výsledky je velmi pravděpodobně velké množství nedokončitelných levelů.



## Související soubory
- [TOAD-GAN](TOAD-GAN.md)
- [Instalace a použití](Instalace-TOAD.md)   
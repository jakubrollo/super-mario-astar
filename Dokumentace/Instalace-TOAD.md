# TOAD-GAN - Instalace a použití

[⬅ Zpět na přehled](Overview.md)

## Použitý software
- **Operační systém:** Windows 11 (mělo by fungovat i na Linuxu a macOS)
- **Jazyk:** Python 3
- **Knihovny:** PyTorch, Pillow, numpy, wandb, torchvision, tqdm, umap-learn, loguru, matplotlib, seaborn, PyYAML, scikit-learn, py4j
- **Vývojové prostředí:** Visual Studio Code

## Implementované změny
- V souboru `GUI.py` (TOAD_GUI) byla upravena funkce `save_txt.py`, která nyní umožňuje vygenerovat a uložit 100 úrovní naráz.
- Do složky `generators/v2` byl přidán natrénovaný model na levelu *krys-1*.
- Do složky `levels` byly přidány vygenerované levely z modelů vytvořených na levelech *original_1*, *original_3-3* a *krys-1*.
- Vytvořen soubor *random-orig-levels*, obsahující 1000 úrovní vygenerovaných náhodně vybraným modelem založeným na některé z originálních úrovní.
- **TOAD-GAN nebyl upravován**, pouze použit pro trénování vlastních modelů, proto není součástí repozitáře.

## Instalace
Pro zprovoznění TOAD-GANu je nutné řídit se instrukcemi v README souboru repozitáře TOAD-GANu. 

Nejprve je potřeba stáhnout repozitář a nainstalovat všechny závislosti pomocí následujícího příkazu:

```
pip3 install -r requirements.txt -f "https://download.pytorch.org/whl/torch_stable.html"
```

## Trénování modelu
Po nainstalování všech potřebných závislostí lze TOAD-GAN trénovat spuštěním souboru `main.py`. Pro spuštění trénování s příkladem modelu TOAD-GAN na úrovni *1-1* ze Super Mario Bros. s 4000 iteracemi pro každou úroveň, použijte následující příkaz:

```
python main.py --input-dir input --input-name lvl_1-1.txt --num_layer 3 --alpha 100 --niter 4000 --nfc 64
```

## Generování úrovní
Pro generování úrovní pomocí natrénovaného modelu TOAD-GAN stačí spustit následující příkaz:

```
python generate_samples.py --out_path path/to/pretrained/TOAD-GAN --input-dir input --input-name lvl_1-1.txt --num_layer 3 --alpha 100 --niter 4000 --nfc 64
```

## TOAD-GUI rozšíření
Autorka projektu vytvořila rozšíření pro vizualizaci a manipulaci s generovanými úrovněmi, které se nazývá **TOAD-GUI**. Tento framework umožňuje lepší interakci s generovanými úrovněmi a usnadňuje jejich úpravy.

## Související odkazy
- [TOAD-GAN](TOAD-GAN.md)
- [Implementované změny a výsledky](Modifikace-TOAD.md)  

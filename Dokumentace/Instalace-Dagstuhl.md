# Dagstuhl-GAN - Instalace a použití

[⬅ Zpět na přehled](Overview.md)


## Důležité soubory
- **DagstuhlGAN-master/marioaiDagstuhl/src/cmates/CMAMarioSolver**  
  - Hlavní funkce pro spuštění CMA-ES evoluce (nastavení počtu iterací, levelů, ...). 
  - Po spuštění hlavní funkce se automaticky generují soubory pojmenované ve formátu "timeline*number*.txt". Každý soubor obsahuje seznam vektorů spolu s jejich příslušnými hodnotami fitness. Tento soubor reprezentuje jednu generaci CMA-ES optimalizace, která začíná náhodně vybraným počátečním vektorem. 
  - Během generace by mělo v “timeline” souborech docházet k postupnému snižování fitness hodnoty, jelikož algoritmus CMA-ES se snaží nalézt vektor s co nejnižší fitness hodnotou.
- **DagstuhlGAN-master/marioaiDagstuhl/src/cmates/MarioEvalFunction**  
  - Funkce `valueOf` pro výběr fitness funkce + několik funkcí pro práci s levely (např. získání levelu z vektoru).
- **DagstuhlGAN-master/marioaiDagstuhl/src/communication/MarioProcess**  
  - Implementace jednotlivých fitness funkcí (zde je potřeba manuálně nastavit agenta).
- **DagstuhlGAN-master/marioaiDagstuhl/src/viewer**  
  - **MarioLevelPlayer** - umožňuje hraní levelů uživatelem.  
  - **MarioLevelSaver** - umožňuje uložit levely do souboru pro daný vektor.  
  - **MarioLevelViewer** - vygeneruje “in-engine” obrázek levelu daného vektoru.
- **DagstuhlGAN-master/pytorch**: Vytváření a trénování GAN modelů, především důležitý soubor `main`.

## Jak zprovoznit projekt

Projekt DagstuhlGAN najdete na GitHubu: [https://github.com/CIGbalance/DagstuhlGAN](https://github.com/CIGbalance/DagstuhlGAN)

Při pokusu o jeho spuštění jsme narazili na několik problémů spojených s kompatibilitou knihoven a verzí JDK. 

- Projekt DagstuhlGAN vyžaduje velmi specifické verze knihoven a nástrojů, které jsou dnes již staré a některé nekompatibilní s Windows.
- Neexistuje funkční verze projektu pro novější verze PyTorch a větev od autorů projektu na GitHubu, která se o to pokoušela, je ve špatném stavu.
- Doporučujeme použít macOS nebo Linux s Pythonem 3.6 a PyTorch 0.3.1, které bude nejspíše potřeba nainstalovat “manuálně”.

Zde je podrobný návod, jak jsme tyto problémy vyřešili.

### 1. Nastavení v IntelliJ

Nejdříve je potřeba projekt naklonovat:
```
git clone https://github.com/CIGbalance/DagstuhlGAN
```
Otevřete projekt v hlavním souboru projektu.
- V IntelliJ přejděte do **File → Project Structure...**
  - **Language Level**: Nastavte na "SDK default".
  - **Compiler Output**: Přidejte cestu pro výstup kompilátoru (pokud není nenastavena).
  
**Import modulů**:
- Přejděte v **Project Structure** do **Modules** a klikněte na symbol "+" → **Import Module**.
- Vyberte soubor **"marioaiDagstuhl"**.
- Nastavte **Module SDK** na "Amazon Corretto 1.8.0".
  - Pokud tato verze není dostupná, mělo by ji být možné stáhnout pomocí **"Download JDK..."** → zvolte verzi 1.8. (Pokud ne, měla by být dostupná ke stažení na internetu).

**Přidání modulu pro Python**:
- V **Modules** přidejte modul pro Python 3.6, potřebný pro složku **pytorch**, kde se nachází .py soubory pro trénování GANu.

**Nastavení Run/Debug konfigurace**:
- V **Run/Debug Configurations** nastavte v sekci "Build and run" na Java 9.0 (Amazon Corretto 1.8.0).
  
Do projektu je také potřeba přidat soubor **"my_python_path.txt"** s cestou k Pythonu, např.:
```
/Library/Frameworks/Python.framework/Versions/3.6/bin/python3.6
```

### 2. Problémy s PyTorch

Projekt vyžaduje starou verzi PyTorch 0.3.1, která:
- Není kompatibilní s Windows.
- Nelze nainstalovat pomocí běžného příkazu:
  ```
  pip install torch==0.3.1
  ```

Na GitHubu lze najít větev, kde se autoři snažili projekt přizpůsobit novějším verzím PyTorch, ovšem tato větev nebyla nikdy dokončena a je v horším stavu než původní verze.

Autoři zároveň v issues uvádějí použité verze knihoven:
- **pytorch 0.3.1**
- **torchvision 0.2.0 or lower**

Pro instalaci starších verzí PyTorch jsme zjistili, že neexistuje žádná varianta této verze pro Windows.
Musíme tedy použít Linux (neověřeno, zda to lze na Linuxu opravdu zprovoznit) nebo macOS, pro které verze této knihovny existují.

### 3. Řešení na macOS

Měli jsme k dispozici starší **Macmini 7,1** s **macOS Monterey 12.7.4**, se kterým jsme postupovali následovně:

**Instalace Pythonu 3.6**:
Projekt vyžaduje Python 3.6 (použili jsme 3.6.15), který není snadné nainstalovat běžnými způsoby. Použili jsme tedy **Homebrew** pro nainstalování **pyenv** pro nainstalování Python verze 3.6.15 a následně instalovali pytorch 0.3.1.

1. **Instalace Homebrew**:
   ```
   /bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"
   ```
2. **Instalace pyenv**:
   ```
   brew install pyenv
   ```
3. **Instalace starší verze Pythonu**:
   ```
   pyenv install 3.6.15
   ```
   Pokud chcete použít tuto verzi jako globální, použijte:
   ```
   pyenv global 3.6.15
   ```

Ani na **MacOS** nelze **PyTorch 0.3.1** nainstalovat klasickým způsobem:
```
pip install torch==0.3.1
```
Náš postup byl následující:
1. Stáhněte .whl soubor z webu **PyTorch**: [PyTorch previous versions](https://pytorch.org/get-started/previous-versions/)
2. Proveďte instalaci pomocí příkazu:
   ```
   python3.6 -m pip install /your/path/to/torch-0.3.1-cp36-cp36m-macosx_10_7_x86_64.whl
   ```
3. Instalace **torchvision 0.2.0**:
   ```
   python3.6 -m pip install torchvision==0.2.0
   ```



## Související soubory

- [DagstuhlGAN](DagstuhlGAN.md)  
- [Implementované změny a výsledky](Modifikace-Dagstuhl.md)

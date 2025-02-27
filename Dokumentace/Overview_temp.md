# Reproduction and improvement of a Super Mario PCG paper using Generative Adversarial Networks

Tento projekt se zaměřuje na replikaci a vylepšení existujících studií zabývajících se generováním úrovní pro hru Super Mario pomocí Generative Adversarial Networks (GAN).

Primárním cílem je reprodukovat a vylepšit výsledky dvou studií:
- [TOAD-GAN](https://arxiv.org/pdf/2008.01531.pdf)
- [DagstuhlGAN](https://arxiv.org/pdf/1805.00728)

Obě tyto studie využívají různé architektury GAN-ů pro generování levelů z jednoho příkladového levelu. Tyto výsledky jsme replikovali a následně jsme modifikovali vyhledávací algoritmus pro analýzu hratelnosti generovaných úrovní, nahrazením původního testovacího agenta (Baumgarten) za MFF A*.

## Použité články a jejich účel

#### TOAD-GAN
- **Repozitář:** [GitHub - TOAD-GAN](https://github.com/Mawiszus/TOAD-GAN)
- **Cíl článku:** Generovat levely z jednoho příkladového levelu pomocí hierarchického GANu s více úrovněmi downsizingu a upsamplingu (SinGAN). 
- **Video od autorky o tomto článku:** [YouTube](https://youtu.be/_bnAtIYVx-s)
- **Náš cíl:**
  - Replikace výsledků a použití MFF A* místo původního Baumgarten agenta pro zjištění počtu dokončitelných levelů.
  - Analyzovat výsledky.

#### DagstuhlGAN
- **Repozitář:** [GitHub - DagstuhlGAN](https://github.com/CIGbalance/DagstuhlGAN)
- **Cíl článku:** Generovat levely z jednoho příkladového levelu pomocí jednoduché GAN architektury a optimalizovat vstupy pomocí CMA-ES.
- **Náš cíl:**
  - Replikace výsledků a použití MFF A* v evoluční fázi.
  - Vlastní fitness funkce pro evoluci vektorů.
  - Analýza evoluce a levelů (MFF A* vs. Baumgarten).

## Odkazy
### TOAD-GAN
- [TOAD-GAN](TOAD-GAN.md)  
- [Instalace a použití](Instalace-TOAD.md)  
- [Implementované změny a výsledky](Modifikace-TOAD.md)  

### DagstuhlGAN
- [DagstuhlGAN](DagstuhlGAN.md)  
- [Instalace a použití](Instalace-Dagstuhl.md)  
- [Implementované změny a výsledky](Modifikace-Dagstuhl.md)  

###
- [Pomocné skripty](Skripty.md)  


## Technické detaily
- **Operační systém:** Windows 11 (TOAD-GAN), macOS (DagstuhlGAN)
- **Programovací jazyk:** Java, Python
- **Vývojové prostředí:** IntelliJ, Visual Studio Code
- **Verzovací systém:** [GitLab](https://gitlab.com/mff_rolloj/super-mario-astar)
- **Frameworks:** Mario AI Framework (old & [new](https://github.com/amidos2006/Mario-AI-Framework))  
# Star Client (Fabric 1.21.11)

Minecraft 1.21.11 için Fabric client modu: kategorili ClickGUI, ayar sistemi (toggle / slider / mod seçimi), config kaydı ve HUD.

## Kullanım

- **Sağ Shift**: ClickGUI aç/kapa
- Sol tık: modülü aç/kapa · Sağ tık: modülün ayarlarını aç/kapa
- Slider'ı sürükleyerek değer ayarla; mod seçiminde sol/sağ tık ile değiştir
- Ayarlar `config/starclient.json` dosyasına kaydedilir

## Modüller

| Kategori | Modül | Tuş | Açıklama |
|----------|-------|-----|----------|
| Combat   | KillAura | K | Menzildeki en yakın hedefe otomatik vurur |
| Movement | Sprint | R | İleri giderken otomatik koşar |
| Movement | Fly | G | Creative tarzı uçuş |
| Movement | Step | H | Adım yüksekliği |
| Render   | HUD | – | Watermark + açık modül listesi |

Tuşlar Ayarlar > Kontroller > *Star Client* altından değiştirilebilir.

> Sadece kendi dünyanda veya izin verilen sunucularda kullan. Çoğu sunucuda hile kullanımı yasaktır ve ban sebebidir.

## Derleme

GitHub Actions: push et → **Actions** → son çalışma → **Artifacts** → `hackclient` (jar).

Yerelde (Java 21 + Gradle 9.2+): `gradle build` → `build/libs/hackclient-0.1.0.jar`.
Fabric Loader + Fabric API ile `mods/` klasörüne at.

## Yeni modül ekleme

1. `module/impl/` altında `Module`'den türeyen sınıf yaz; ayarları `add(new NumberSetting(...))` ile ekle.
2. `ModuleManager.init()` içine ekle. GUI ve config otomatik tanır.

# StarClient (Fabric 1.21.11)

Minecraft 1.21.11 için Fabric client modu. Modül sistemi + sağ üstte açık modül listesi (HUD) içerir.

## Modüller (varsayılan tuşlar, Ayarlar > Kontroller'den değiştirilebilir)

| Modül  | Tuş | Açıklama |
|--------|-----|----------|
| Sprint | R   | İleri giderken otomatik koşar |
| Fly    | G   | Creative tarzı uçuş (sunucu izin vermiyorsa çalışmaz/atılabilirsiniz) |
| Step   | H   | Adım yüksekliği 1 blok |

> Sadece kendi dünyanda veya izin verilen sunucularda kullan. Çoğu sunucuda hile kullanımı yasaktır ve ban sebebidir.

## Derleme

GitHub Actions: repoya push et → **Actions** sekmesi → son çalışma → **Artifacts** → `hackclient` indir (jar).

Yerelde (Java 21 + Gradle 9.2+):

```
gradle build
```

Jar: `build/libs/hackclient-0.1.0.jar`. Fabric Loader + Fabric API ile `mods/` klasörüne at.

## Yeni modül ekleme

1. `module/impl/` altında `Module`'den türeyen sınıf yaz (`onTick`, `onEnable`, `onDisable`).
2. `ModuleManager.init()` içine ekle.

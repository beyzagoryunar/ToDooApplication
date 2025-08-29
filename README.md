# ToDo Application - Modern Android Client

![Uygulama Logosu](<link_to_your_logo_image.png>) <!-- Buraya logonuzun bir resim linkini koyun -->

Bu proje, staj dönemimde geliştirdiğim, modern Android geliştirme prensiplerini ve kütüphanelerini kullanan, zengin özelliklere sahip bir ToDo (Yapılacaklar Listesi) uygulamasıdır. Uygulama, C# ve ASP.NET Core ile yazılmış bir backend API'si ile haberleşmektedir.

<!-- UYGULAMANIN EN GÜZEL GIF'İNİ VEYA EKRAN GÖRÜNTÜSÜNÜ BURAYA EKLEYİN -->
<!-- Örnek: ![Uygulama Demosu](demo.gif) -->
<p align="center">
  <img src="[BURAYA_HAZIRLADIĞINIZ_GIF_VEYA_MOCKUP_GÖRSELİNİN_LİNKİNİ_EKLEYİN]" width="300">
</p>

## 🚀 Hakkında

Bu projenin temel amacı, bir mobil uygulamanın uçtan uca nasıl geliştirildiğini deneyimlemekti. Jetpack Compose ile tamamen modern bir arayüz oluşturulmuş, Hilt ile bağımlılıklar yönetilmiş ve WorkManager ile güvenilir arka plan işlemleri sağlanmıştır.

## ✨ Özellikler

- **Dinamik Tema:** DataStore ile kalıcı hale getirilen **Açık, Koyu ve Sistem Varsayılanı** temaları arasında anında geçiş.
- **Güvenli Oturumlar:** Eksiksiz bir **giriş/kayıt** akışı ve "Beni Hatırla" özelliği.
- **Etkileşimli Bildirimler:** **OneSignal** entegrasyonu ile zengin push bildirimleri.
- **Akıllı Arka Plan İşlemleri:** **WorkManager** ile uygulama kapalıyken bile API ile haberleşen güvenilir servisler.
- **Tam Görev Yönetimi (CRUD):** Temaya tam uyumlu modern diyaloglar ile görev ekleme, düzenleme, silme.
- **Anlık Arama:** Görev listesi içinde hızlı ve anlık filtreleme.

## 🛠️ Kullanılan Teknolojiler

- **Dil:** %100 [Kotlin](https://kotlinlang.org/)
- **UI:** [Jetpack Compose](https://developer.android.com/jetpack/compose)
- **Mimari:** MVVM (Model-View-ViewModel)
- **Asenkron Programlama:** [Kotlin Coroutines](https://kotlinlang.org/docs/coroutines-overview.html) & Flow
- **Dependency Injection:** [Hilt](https://dagger.dev/hilt/)
- **Navigasyon:** [Navigation Compose](https://developer.android.com/jetpack/compose/navigation)
- **Ağ (Networking):** [Retrofit](https://square.github.io/retrofit/) & [OkHttp](https://square.github.io/okhttp/)
- **Veri Saklama:** [Jetpack DataStore](https://developer.android.com/topic/libraries/architecture/datastore) 
- **Arka Plan İşlemleri:** [WorkManager](https://developer.android.com/topic/libraries/architecture/workmanager)
- **Push Bildirimleri:** [OneSignal SDK](https://onesignal.com/)
- 
## 🔗 Backend API

Bu uygulamanın çalışması için gereken backend servisi C# ve ASP.NET Core ile geliştirilmiştir. Kaynak kodlarına aşağıdaki repodan ulaşabilirsiniz:

**🔗 [Buraya C# Backend Projenizin GitHub Linkini Ekleyin]**


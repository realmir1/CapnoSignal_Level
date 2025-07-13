package com.example.kapnoultra2 // Paket adınızın doğru olduğundan emin olun

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.content.Intent
import android.graphics.PorterDuff
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    // Arka plan animasyonu için değişkenler
    private lateinit var gradientDrawable: GradientDrawable

    // Kullanıcı girişi ve doğrulama için değişkenler
    private lateinit var editTextName: EditText
    private lateinit var editTextSurname: EditText
    private lateinit var devamEtButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // --- Arka Plan Animasyonu Başlangıcı ---
        // GradientDrawable'ı oluşturuyoruz (başlangıç renkleriyle)
        gradientDrawable = GradientDrawable(
            GradientDrawable.Orientation.TOP_BOTTOM,
            intArrayOf(
                ContextCompat.getColor(this, R.color.turkuaz1),
                ContextCompat.getColor(this, R.color.beyaz1)
            )
        )
        // Arka plan olarak ayarla
        window.decorView.background = gradientDrawable

        // Animasyonu başlat
        animateBackground()
        // --- Arka Plan Animasyonu Sonu ---

        // --- Kullanıcı Girişi ve Doğrulama Başlangıcı ---
        // Görünümleri başlat
        editTextName = findViewById(R.id.editTextName)
        editTextSurname = findViewById(R.id.editTextSurname)
        devamEtButton = findViewById(R.id.devamEtButton) // XML'deki button ID'si ile eşleşmeli

        // Düğmeye tıklama dinleyicisi ayarla
        devamEtButton.setOnClickListener {
            validateInputs()
        }
        // --- Kullanıcı Girişi ve Doğrulama Sonu ---
    }

    // --- Arka Plan Animasyonu Fonksiyonu ---
    private fun animateBackground() {
        // Renk geçişlerini tutacak çiftler:
        // R.color.turkuaz1, R.color.beyaz1, R.color.turkuaz2, R.color.beyaz2 renklerinin
        // 'res/values/colors.xml' dosyanızda tanımlı olması gerekir.
        val colorPairs = arrayOf(
            intArrayOf(
                ContextCompat.getColor(this, R.color.turkuaz1),
                ContextCompat.getColor(this, R.color.beyaz1)
            ),
            intArrayOf(
                ContextCompat.getColor(this, R.color.turkuaz2),
                ContextCompat.getColor(this, R.color.beyaz2)
            )
        )

        val animator = ValueAnimator.ofFloat(0f, 1f)
        animator.duration = 7000 // 15 saniye, yavaş geçiş
        // Sonsuz tekrar
         // Başlangıca geri dönerek tekrarla

        val evaluator = ArgbEvaluator() // Renkleri düzgün bir şekilde interpolate etmek için

        var colorIndex = 0 // Animasyonun hangi renk çifti arasında olduğunu takip etmek için

        animator.addUpdateListener { animation ->
            val fraction = animation.animatedFraction

            // Animasyon yönüne göre mevcut ve sonraki renk çiftini belirle
            val currentPairIndex = if (animation.animatedFraction < 0.5f && animator.repeatMode == ValueAnimator.REVERSE) {
                // İleri yönde veya geri dönüşün başlangıcında
                colorIndex % colorPairs.size
            } else {
                // Geri yönde (REVERSE)
                (colorIndex + 1) % colorPairs.size
            }

            val nextPairIndex = (currentPairIndex + 1) % colorPairs.size

            // İlk renk için interpolasyon
            val colorStart = evaluator.evaluate(
                fraction,
                colorPairs[currentPairIndex][0],
                colorPairs[nextPairIndex][0]
            ) as Int

            // İkinci renk için interpolasyon
            val colorEnd = evaluator.evaluate(
                fraction,
                colorPairs[currentPairIndex][1],
                colorPairs[nextPairIndex][1]
            ) as Int

            gradientDrawable.colors = intArrayOf(colorStart, colorEnd)

            // Animasyon bir döngüyü tamamladığında colorIndex'i güncelleyin
            if (fraction >= 0.99f && animation.repeatMode == ValueAnimator.REVERSE) {
                // Animasyon neredeyse sona erdiğinde (geri dönecek), sonraki renk çiftine geçmek için colorIndex'i artır
                colorIndex = (colorIndex + 1) % colorPairs.size
            }
        }
        animator.start()
    }


    // --- Kullanıcı Girişi Doğrulama Fonksiyonu ---
    private fun validateInputs() {
        val name = editTextName.text.toString().trim()
        val surname = editTextSurname.text.toString().trim()

        var isValid = true


        // İsim doğrulaması
        if (name.isEmpty()) {
            editTextName.error = "Lütfen isminizi girin"
            isValid = false
        } else {
            editTextName.error = null // Hatayı temizle
        }
        // Soyisim doğrulaması
        if (surname.isEmpty()) {
            editTextSurname.error = "Lütfen soyadınızı girin"
            isValid = false
        } else {
            editTextSurname.error = null // Hatayı temizle
        }

        // Her iki alan da geçerliyse
        if (isValid) {
            Toast.makeText(this, "Giriş başarılı!", Toast.LENGTH_SHORT).show()

            val intent = Intent(this, MainActivity2::class.java)
            startActivity(intent)
        } else {
            Toast.makeText(this, "Lütfen gerekli alanları doldurun.", Toast.LENGTH_SHORT).show()
        }



    }
}

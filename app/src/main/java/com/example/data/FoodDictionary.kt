package com.example.data

data class FoodItem(
    val name: String,
    val calories: Int,
    val protein: Float,
    val carbs: Float,
    val fat: Float
)

object FoodDictionary {
    val foods = listOf(
        FoodItem("Nasi Putih (1 porsi/100g)", 130, 2.7f, 28.1f, 0.3f),
        FoodItem("Nasi Padang (1 porsi)", 550, 20f, 60f, 25f),
        FoodItem("Nasi Kuning (1 porsi)", 380, 8f, 50f, 15f),
        FoodItem("Ayam Goreng (1 potong)", 260, 15f, 5f, 18f),
        FoodItem("Ayam Bakar (1 potong)", 220, 20f, 6f, 10f),
        FoodItem("Bebek Goreng (1 potong)", 320, 16f, 4f, 25f),
        FoodItem("Telur Dadar (1 butir)", 93, 7f, 1f, 7f),
        FoodItem("Telur Rebus (1 butir)", 78, 6.3f, 0.6f, 5.3f),
        FoodItem("Telur Ceplok (1 butir)", 110, 6f, 1f, 9f),
        FoodItem("Tempe Goreng (1 potong)", 34, 2f, 1.7f, 2.2f),
        FoodItem("Tahu Goreng (1 potong)", 35, 2.2f, 1.5f, 2.6f),
        FoodItem("Bakwan/Bala-bala (1 biji)", 137, 3f, 15f, 7f),
        FoodItem("Tahu Isi Goreng (1 biji)", 134, 4f, 12f, 8f),
        FoodItem("Soto Ayam (1 mangkuk)", 312, 15f, 25f, 12f),
        FoodItem("Bakso Sapi (1 porsi)", 280, 15f, 25f, 12f),
        FoodItem("Mie Ayam (1 porsi)", 420, 18f, 55f, 16f),
        FoodItem("Nasi Goreng (1 porsi)", 330, 12f, 40f, 14f),
        FoodItem("Indomie Goreng (1 bungkus)", 380, 8f, 54f, 14f),
        FoodItem("Indomie Kuah (1 bungkus)", 320, 7f, 45f, 12f),
        FoodItem("Sate Ayam (1 tusuk)", 34, 3f, 1f, 2f),
        FoodItem("Sate Kambing (1 tusuk)", 45, 4f, 1f, 3f),
        FoodItem("Ikan Lele Goreng (1 ekor)", 200, 15f, 5f, 11f),
        FoodItem("Ikan Nila Bakar (1 ekor)", 120, 20f, 0f, 3f),
        FoodItem("Gado-gado (1 porsi)", 318, 16f, 45f, 12f),
        FoodItem("Ketoprak (1 porsi)", 402, 14f, 48f, 18f),
        FoodItem("Batagor (1 porsi)", 410, 15f, 35f, 25f),
        FoodItem("Siomay Bandung (1 porsi)", 380, 18f, 40f, 15f),
        FoodItem("Martabak Manis (1 potong)", 270, 4f, 38f, 12f),
        FoodItem("Martabak Telur (1 potong)", 190, 7f, 15f, 12f),
        FoodItem("Roti Tawar (1 lembar)", 73, 2.4f, 13.7f, 1f),
        FoodItem("Susu Sapi (1 gelas/200ml)", 122, 6.5f, 9.6f, 6.5f),
        FoodItem("Kopi Susu Gula Aren (1 gelas)", 220, 3f, 35f, 8f),
        FoodItem("Pisang (1 buah sedang)", 105, 1.3f, 27f, 0.3f),
        FoodItem("Apel (1 buah sedang)", 95, 0.5f, 25f, 0.3f),
        FoodItem("Jeruk Manis (1 buah)", 45, 0.9f, 11f, 0.1f),
        FoodItem("Mangga (1 buah)", 150, 1.4f, 38f, 0.6f),
        FoodItem("Semangka (1 potong)", 46, 0.9f, 11f, 0.2f),
        FoodItem("Pepaya (1 potong)", 43, 0.5f, 11f, 0.1f),
        FoodItem("Alpukat (1 buah)", 322, 4f, 17f, 29f)
    )
}

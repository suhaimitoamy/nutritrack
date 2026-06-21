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
        FoodItem("Ayam Goreng (1 potong)", 260, 15f, 5f, 18f),
        FoodItem("Ayam Bakar (1 potong)", 220, 20f, 6f, 10f),
        FoodItem("Telur Dadar (1 butir)", 93, 7f, 1f, 7f),
        FoodItem("Telur Rebus (1 butir)", 78, 6.3f, 0.6f, 5.3f),
        FoodItem("Tempe Goreng (1 potong)", 34, 2f, 1.7f, 2.2f),
        FoodItem("Tahu Goreng (1 potong)", 35, 2.2f, 1.5f, 2.6f),
        FoodItem("Soto Ayam (1 mangkuk)", 312, 15f, 25f, 12f),
        FoodItem("Bakso Sapi (1 porsi)", 280, 15f, 25f, 12f),
        FoodItem("Nasi Goreng (1 porsi)", 330, 12f, 40f, 14f),
        FoodItem("Indomie Goreng (1 bungkus)", 380, 8f, 54f, 14f),
        FoodItem("Sate Ayam (1 tusuk)", 34, 3f, 1f, 2f),
        FoodItem("Ikan Lele Goreng (1 ekor)", 200, 15f, 5f, 11f),
        FoodItem("Ikan Nila Bakar (1 ekor)", 120, 20f, 0f, 3f),
        FoodItem("Gado-gado (1 porsi)", 318, 16f, 45f, 12f),
        FoodItem("Roti Tawar (1 lembar)", 73, 2.4f, 13.7f, 1f),
        FoodItem("Susu Sapi (1 gelas/200ml)", 122, 6.5f, 9.6f, 6.5f),
        FoodItem("Pisang (1 buah sedang)", 105, 1.3f, 27f, 0.3f),
        FoodItem("Apel (1 buah sedang)", 95, 0.5f, 25f, 0.3f)
    )
}

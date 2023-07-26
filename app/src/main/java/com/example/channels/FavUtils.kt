// FavUtils.kt
package com.example.channels

import java.util.HashSet

object FavUtils {
    val favSelectedPositions = HashSet<Int>()

    // Метод для добавления позиции в избранные
    fun addToFavorites(position: Int) {
        favSelectedPositions.add(position)
    }

    // Метод для удаления позиции из избранных
    fun removeFromFavorites(position: Int) {
        favSelectedPositions.remove(position)
    }

    // Метод для проверки, является ли позиция избранной
    fun isFavorite(position: Int): Boolean {
        return favSelectedPositions.contains(position)
    }
}


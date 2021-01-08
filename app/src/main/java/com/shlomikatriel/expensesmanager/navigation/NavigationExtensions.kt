package com.shlomikatriel.expensesmanager.navigation

import androidx.fragment.app.Fragment
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import com.shlomikatriel.expensesmanager.logs.logWarning

fun Fragment.navigate(navDirections: NavDirections) = try {
    findNavController().navigate(navDirections)
} catch (e: Exception) {
    logWarning("Failed to navigate to destination ${e.message}")
}
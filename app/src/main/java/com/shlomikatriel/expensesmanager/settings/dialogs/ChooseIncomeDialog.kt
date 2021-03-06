package com.shlomikatriel.expensesmanager.settings.dialogs

import android.content.Context
import android.content.SharedPreferences
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.shlomikatriel.expensesmanager.*
import com.shlomikatriel.expensesmanager.databinding.ChooseIncomeDialogBinding
import com.shlomikatriel.expensesmanager.logs.logDebug
import com.shlomikatriel.expensesmanager.logs.logInfo
import com.shlomikatriel.expensesmanager.sharedpreferences.FloatKey
import com.shlomikatriel.expensesmanager.sharedpreferences.getFloat
import com.shlomikatriel.expensesmanager.sharedpreferences.putFloat
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class ChooseIncomeDialog : BaseDialog() {

    @ApplicationContext
    @Inject
    lateinit var appContext: Context

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    @Inject
    lateinit var localizationManager: LocalizationManager

    private lateinit var binding: ChooseIncomeDialogBinding

    override fun layout() = R.layout.choose_income_dialog

    override fun bind(view: View) {
        binding = DataBindingUtil.bind<ChooseIncomeDialogBinding>(view)!!.apply {
            dialog = this@ChooseIncomeDialog
            incomeInputLayout.initialize(
                localizationManager.getCurrencySymbol(),
                sharedPreferences.getFloat(FloatKey.INCOME)
            )
        }
    }

    fun chooseClicked() {
        val income = binding.incomeInputLayout.income.text.toString()
        val incomeAsFloat = income.toFloatOrNull()
        logDebug("Trying to add expense [income=$income, incomeAsFloat=$incomeAsFloat]")
        if (binding.incomeInputLayout.isInputValid(appContext)) {
            sharedPreferences.putFloat(FloatKey.INCOME, incomeAsFloat!!)
            findNavController().popBackStack()
        }
    }

    fun cancelClicked() {
        logInfo("Canceling choose income")
        findNavController().popBackStack()
    }
}
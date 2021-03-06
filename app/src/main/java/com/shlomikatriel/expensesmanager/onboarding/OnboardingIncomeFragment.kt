package com.shlomikatriel.expensesmanager.onboarding

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.shlomikatriel.expensesmanager.LocalizationManager
import com.shlomikatriel.expensesmanager.R
import com.shlomikatriel.expensesmanager.databinding.OnboardingIncomeFragmentBinding
import com.shlomikatriel.expensesmanager.initialize
import com.shlomikatriel.expensesmanager.isInputValid
import com.shlomikatriel.expensesmanager.logs.logInfo
import com.shlomikatriel.expensesmanager.logs.logVerbose
import com.shlomikatriel.expensesmanager.sharedpreferences.FloatKey
import com.shlomikatriel.expensesmanager.sharedpreferences.getFloat
import com.shlomikatriel.expensesmanager.sharedpreferences.putFloat
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@AndroidEntryPoint
class OnboardingIncomeFragment : Fragment() {

    @ApplicationContext
    @Inject
    lateinit var appContext: Context

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    @Inject
    lateinit var localizationManager: LocalizationManager

    lateinit var binding: OnboardingIncomeFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.onboarding_income_fragment, container, false)

        configureIncomeInput()

        return binding.root
    }

    private fun configureIncomeInput() = binding.incomeInputLayout.apply {
        val currentIncome = sharedPreferences.getFloat(FloatKey.INCOME)

        initialize(localizationManager.getCurrencySymbol(), currentIncome)

        income.addTextChangedListener {
            val valid = isInputValid(appContext)
            logVerbose("Input text changed. [valid=$valid]")
        }

        // Color title
        title.setTextColor(ContextCompat.getColor(appContext, R.color.colored_text))
    }

    override fun onPause() {
        super.onPause()
        val valid = binding.incomeInputLayout.isInputValid(appContext)
        logInfo("Onboarding input fragment paused, storing income if valid [valid=$valid]")
        if (valid) {
            sharedPreferences.putFloat(
                FloatKey.INCOME,
                binding.incomeInputLayout.income.text.toString().toFloat()
            )
        }
    }
}
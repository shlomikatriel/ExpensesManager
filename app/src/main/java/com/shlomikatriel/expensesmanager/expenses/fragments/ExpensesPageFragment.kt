package com.shlomikatriel.expensesmanager.expenses.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.shlomikatriel.expensesmanager.LocalizationManager
import com.shlomikatriel.expensesmanager.R
import com.shlomikatriel.expensesmanager.database.model.ExpenseType
import com.shlomikatriel.expensesmanager.databinding.ExpensesPageFragmentBinding
import com.shlomikatriel.expensesmanager.expenses.components.ExpensesPageRecyclerAdapter
import com.shlomikatriel.expensesmanager.expenses.mvi.ExpensesPageEvent
import com.shlomikatriel.expensesmanager.expenses.mvi.ExpensesPageViewModel
import com.shlomikatriel.expensesmanager.expenses.mvi.ExpensesPageViewState
import com.shlomikatriel.expensesmanager.logs.logInfo
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@AndroidEntryPoint
class ExpensesPageFragment : Fragment() {

    @ApplicationContext
    @Inject
    lateinit var appContext: Context

    @Inject
    lateinit var localizationManager: LocalizationManager

    private val args: ExpensesPageFragmentArgs by navArgs()

    private lateinit var binding: ExpensesPageFragmentBinding

    private val model: ExpensesPageViewModel by viewModels()

    private lateinit var expensesRecyclerAdapter: ExpensesPageRecyclerAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate<ExpensesPageFragmentBinding>(
            inflater,
            R.layout.expenses_page_fragment,
            container,
            false
        ).apply {
            fragment = this@ExpensesPageFragment
        }

        model.apply {
            postEvent(ExpensesPageEvent.Initialize(args.month))
            getViewState().observe(viewLifecycleOwner, { render(it) })
        }

        configureRecycler()

        return binding.root
    }

    private fun configureRecycler() = binding.expensesRecycler.apply {
        layoutManager = LinearLayoutManager(requireContext())
        expensesRecyclerAdapter = ExpensesPageRecyclerAdapter(
            requireContext(),
            this@ExpensesPageFragment,
            localizationManager.getCurrencyFormat(),
            args.month
        )
        adapter = expensesRecyclerAdapter
    }

    fun onCheckedChanged() {
        val selectedExpenseTypes = getSelectedExpenseTypes()
        logInfo("Selected expense types changed [selectedExpenseTypes=$selectedExpenseTypes]")
        model.postEvent(ExpensesPageEvent.SelectedExpenseTypesChange(selectedExpenseTypes))
    }

    private fun render(viewState: ExpensesPageViewState) {
        expensesRecyclerAdapter.updateData(viewState.expenses)
        binding.total = localizationManager.getCurrencyFormat().format(viewState.total)
    }

    private fun getSelectedExpenseTypes() = binding.chipGroup.checkedChipIds
        .mapNotNull { binding.chipGroup.findViewById<View>(it).tag as ExpenseType? }
        .toSet()
}
package com.deonvanooijen.budgettracker

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.addTextChangedListener
import androidx.room.Room
import com.deonvanooijen.budgettracker.databinding.ActivityAddTransactionBinding
import com.deonvanooijen.budgettracker.databinding.ActivityDetailBinding
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding
    private lateinit var transaction: Transaction

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.root_view)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        transaction = intent.getSerializableExtra("transaction") as Transaction

        binding.labelInput.setText(transaction.label)
        binding.amountInput.setText(transaction.amount.toString())
        binding.descriptionInput.setText(transaction.description)

        binding.rootView.setOnClickListener {
            this.window.decorView.clearFocus()

            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(it.windowToken, 0)
        }

        binding.labelInput.addTextChangedListener {
            binding.updateButton.visibility = View.VISIBLE
            if (it!!.count() > 0)
                binding.labelLayout.error = null
        }

        binding.amountInput.addTextChangedListener {
            binding.updateButton.visibility = View.VISIBLE
            if (it!!.count() > 0)
                binding.amountLayout.error = null
        }

        binding.descriptionInput.addTextChangedListener {
            binding.updateButton.visibility = View.VISIBLE
        }

        binding.updateButton.setOnClickListener {
            val label = binding.labelInput.text.toString()
            val description = binding.descriptionInput.text.toString()
            val amount = binding.amountInput.text.toString().toDoubleOrNull()

            if (label.isEmpty())
                binding.labelLayout.error = "Please enter a valid label"
            else if (amount == null)
                binding.amountLayout.error = "Please enter a valid amount"
            else {
                val transaction = Transaction(transaction.id, label, amount, description)
                update(transaction)
            }
        }

        binding.closeButton.setOnClickListener {
            finish()
        }
    }

    private fun update(transaction: Transaction) {
        val db = Room.databaseBuilder(
            this, AppDatabase::class.java,
            "transactions"
        ).build()

        GlobalScope.launch {
            db.transactionDao().update(transaction)
            finish()
        }
    }

}
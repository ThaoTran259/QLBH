package com.example.quanlych.ui.Cart

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.quanlych.R
import com.example.quanlych.adapter.CartAdapter
import com.example.quanlych.data.User
import com.example.quanlych.data.UserRepository
import com.example.quanlych.databinding.FragmentCartBinding
import com.example.quanlych.model.Product
import com.example.quanlych.utils.CartManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

class CartFragment : Fragment() {

    private var _binding: FragmentCartBinding? = null
    private val binding get() = _binding!!
    private lateinit var cartAdapter: CartAdapter
    private var userEmail: String? = null // Change to store user email

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCartBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Retrieve user email from UserSession
        userEmail = UserRepository.UserSession.email

        cartAdapter = CartAdapter(
            CartManager.cartItems,
            ::onQuantityChanged,
            ::onProductChecked
        )
        binding.recycleviewgiohang.layoutManager = LinearLayoutManager(context)
        binding.recycleviewgiohang.adapter = cartAdapter

        // Setup ItemTouchHelper for swipe to delete
        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                CartManager.cartItems.removeAt(position)
                cartAdapter.notifyItemRemoved(position)
                updateTotalPrice()
                checkIfCartIsEmpty()
            }
        })
        itemTouchHelper.attachToRecyclerView(binding.recycleviewgiohang)

        updateTotalPrice()
        checkIfCartIsEmpty()

        binding.btnmuahang.setOnClickListener {
            val totalPrice = calculateTotalPrice()
            val selectedProducts = CartManager.cartItems.filter { it.isSelected }

            val bundle = Bundle().apply {
                putDouble("totalPrice", totalPrice)
                putParcelableArrayList("selectedProducts", ArrayList(selectedProducts))
                putString("userEmail", userEmail) // Use userEmail from UserSession
            }

            findNavController().navigate(R.id.action_nav_cart_to_nav_thanhtoan, bundle)
        }
        return root
    }

    private fun onQuantityChanged(product: Product) {
        updateTotalPrice()
    }

    private fun onProductChecked(product: Product, isChecked: Boolean) {
        updateTotalPrice()
    }

    private fun calculateTotalPrice(): Double {
        return CartManager.cartItems.filter { it.isSelected }
            .sumOf { it.quantity * it.price }
    }

    private fun updateTotalPrice() {
        val totalPrice = calculateTotalPrice()
        binding.txttongtien.text = String.format("%.2fđ", totalPrice)
        checkIfCartIsEmpty()
    }

    private fun checkIfCartIsEmpty() {
        if (CartManager.cartItems.isEmpty()) {
            binding.recycleviewgiohang.visibility = View.GONE
            binding.textCart.visibility = View.VISIBLE
            binding.txttongtien.visibility = View.GONE
            binding.btnmuahang.visibility = View.GONE
        } else {
            binding.recycleviewgiohang.visibility = View.VISIBLE
            binding.textCart.visibility = View.GONE
            binding.txttongtien.visibility = View.VISIBLE
            binding.btnmuahang.visibility = View.VISIBLE
        }
    }
}

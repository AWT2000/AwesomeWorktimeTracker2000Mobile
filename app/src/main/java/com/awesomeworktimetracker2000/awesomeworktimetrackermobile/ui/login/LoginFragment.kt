package com.awesomeworktimetracker2000.awesomeworktimetrackermobile.ui.login

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.R
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.databinding.LoginFragmentBinding

class LoginFragment : Fragment() {

    private lateinit var viewModel: LoginViewModel

    private lateinit var binding: LoginFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // initialize data binding
        binding = DataBindingUtil.inflate(inflater, R.layout.login_fragment, container, false)
        binding.lifecycleOwner = this

        // initialize view model
        viewModel = ViewModelProvider(this).get(LoginViewModel::class.java);
        binding.loginViewModel = viewModel

        // observe view model's live data
        viewModel.canContinue.observe(viewLifecycleOwner, Observer { canContinue ->
            if (canContinue) {
                this.findNavController().navigate(R.id.action_loginFragment_to_mainFragment)
            }
        })

        // add watcher for fields
        binding.editTextUsername.addTextChangedListener(credentialWatcher)
        binding.editTextTextPassword.addTextChangedListener(credentialWatcher)

        // set on click event listeners
        binding.buttonLogin.setOnClickListener {
            tryLogin()
        }

        return binding.root
    }

    /**
     * Observer for credential fields, enable login button only when both have values
     */
    private val credentialWatcher = object : TextWatcher {
        override fun afterTextChanged(p0: Editable?) {
            val usernameIsNotEmpty = binding.editTextUsername.text.toString().isNotEmpty()
            val passwordIsNotEmpty = binding.editTextTextPassword.text.toString().isNotEmpty()

            if (usernameIsNotEmpty && passwordIsNotEmpty) {
                viewModel.enableLoginButton()
            }
        }
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
    }

    /**
     * Try login with given username and password.
     */
    private fun tryLogin() {
        Log.i("login", "LoginFragment@tryLogin")
        viewModel.tryLogin(
            binding.editTextUsername.text.toString(),
            binding.editTextTextPassword.text.toString()
        )
    }
}
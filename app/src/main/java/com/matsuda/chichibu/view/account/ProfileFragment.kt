package com.matsuda.chichibu.view.account

import android.content.Intent
import com.matsuda.chichibu.actions.ProfileActionCreator
import com.matsuda.chichibu.databinding.ProfileFragmentBinding
import com.matsuda.chichibu.stores.ProfileStore

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.matsuda.chichibu.dispatchers.Dispatcher
import androidx.lifecycle.ViewModelProviders
import com.matsuda.chichibu.MainActivity
import com.matsuda.chichibu.R
import com.matsuda.chichibu.view.owner.OwnerActivity

class ProfileFragment : Fragment() {
    private val profileStore: ProfileStore by lazy {
        ViewModelProviders.of(this).get(ProfileStore::class.java)
    }
    private var binding: ProfileFragmentBinding? = null

    companion object {
        fun newInstance(): ProfileFragment {
            return ProfileFragment()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Dispatcher.register(profileStore)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.profile_fragment,
            container, false
        ) ?: return null

        binding?.run {
            lifecycleOwner = this@ProfileFragment
            viewModel = profileStore
        }
        profileStore.loading.postValue(
            true
        )

        MainActivity.aWSAppSyncClient?.run {
            ProfileActionCreator.fetchAreas()
        }

        // TODO show on only owner account
        binding?.ownerMode?.setOnClickListener {
            startActivity(Intent(context, OwnerActivity::class.java))
        }

        return binding?.root
    }

    override fun onDestroy() {
        super.onDestroy()
        Dispatcher.unregister(profileStore)
    }
}
package com.example.purchaseautomation

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_profile.view.*

class ProfileAdapter(private val profiles: MutableList<Profile>):
    RecyclerView.Adapter<ProfileAdapter.ProfileViewHolder>() {
    class ProfileViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfileViewHolder {
        return ProfileViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_profile, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ProfileViewHolder, position: Int) {
        val curProfile = profiles[position]
        holder.itemView.apply {
            tvProfile.text = curProfile.profileName
        }
    }

    override fun getItemCount(): Int {
        return profiles.size
    }

    fun addProfile(profile: Profile) {
        profiles.add(profile)
        notifyItemInserted(profiles.size - 1)
    }

    fun clearProfiles() {
        profiles.removeAll { true }
        notifyDataSetChanged()
    }
}
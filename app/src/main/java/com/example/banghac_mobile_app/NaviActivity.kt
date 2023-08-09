package com.example.banghac_mobile_app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.banghac_mobile_app.databinding.ActivityNaviBinding

private const val TAG_CALENDAR = "fragment_calendar"
private const val TAG_HOME = "fragment_home"
private const val TAG_PROFILE = "fragment_profile"
private const val TAG_CHAT = "fragment_chat"

class NaviActivity : AppCompatActivity() {
    private lateinit var binding : ActivityNaviBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNaviBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setFragment(TAG_HOME, HomeFragment())

        binding.navigationView.setOnItemSelectedListener {item ->
            when(item.itemId){
                R.id.calendarFragment -> setFragment(TAG_CALENDAR, CalendarFragment())
                R.id.homeFragment -> setFragment(TAG_HOME, HomeFragment())
                R.id.profileFragment -> setFragment(TAG_PROFILE, ProfileFragment())
                R.id.chatFragment -> setFragment(TAG_CHAT, ChatFragment())
            }
            true
        }
    }

    private fun setFragment(tag: String, fragment: Fragment){
        val manager: FragmentManager = supportFragmentManager
        val fragTransaction = manager.beginTransaction()

        if(manager.findFragmentByTag(tag) == null){
            fragTransaction.add(R.id.mainFrameLayout, fragment, tag)
        }

        val newOrder = manager.findFragmentByTag(TAG_CALENDAR)
        val home = manager.findFragmentByTag(TAG_HOME)
        val profile = manager.findFragmentByTag(TAG_PROFILE)
        val chat = manager.findFragmentByTag(TAG_CHAT)

        if(newOrder != null){
            fragTransaction.hide(newOrder)
        }
        if(home != null){
            fragTransaction.hide(home)
        }
        if(profile != null){
            fragTransaction.hide(profile)
        }
        if(chat != null){
            fragTransaction.hide(chat)
        }

        // 네비 프래그먼트를 다시 누르거나 나갔다가 돌아오면 화면이 보이지 않는 내용을 수정
        if(tag == TAG_CHAT){
            if(chat != null){
                fragTransaction.show(chat)
            }
        }
        else if(tag == TAG_HOME){
            if(home != null){
                fragTransaction.show(home)
            }
        }
        else if(tag == TAG_PROFILE){
            if(profile != null){
                fragTransaction.show(profile)
            }
        }
        else if(tag == TAG_CALENDAR){
            if(newOrder != null){
                fragTransaction.show(newOrder)
            }
        }

        // commit() 메서드를 사용하도록 변경
        fragTransaction.commit()
    }
}
package ru.skillbranch.kotlinexample

import android.provider.ContactsContract
import androidx.annotation.VisibleForTesting
import java.lang.IllegalArgumentException

object UserHolder {
    private val map = mutableMapOf<String, User>()

    fun registerUser(
        fullName: String,
        email: String,
        password: String
    ): User {
        User.makeUser(fullName, email = email, password = password)
            .let {
                require(!map.containsKey(it.login)) { "A user with this email already exists" }
                map[it.login] = it
                return it
            }
    }


    fun registerUserByPhone(
        fullName: String,
        rawPhone: String
    ): User {
        User.makeUser(fullName, phone = rawPhone)
            .let {
                require(!map.containsKey(rawPhone.normalizePhone())) { "A user with this phone already exists" }
                map[rawPhone.normalizePhone()] = it
                return it
            }
    }

    private fun String.normalizePhone(): String = this.replace("""[^\d+]""".toRegex(), "")

    fun requestAccessCode(login: String): Unit? {
        return map[login.normalizePhone()]?.run {
            requestAccessCode()
        }
    }


    fun loginUser(login: String, password: String): String? {
        val key = if (login.contains('@')) login else login.normalizePhone()
        return map[key]?.let {
            if (it.checkPassword(password)) {
                it.userInfo
            } else {
                null
            }
        }
    }


    @VisibleForTesting(otherwise = VisibleForTesting.NONE)
    fun clearHolder() {
        map.clear()
    }
}
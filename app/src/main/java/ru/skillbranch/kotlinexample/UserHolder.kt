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
        return User.makeUser(fullName, email = email, password = password)
            .also { user ->
                if (map[user.login] == null) map[user.login] =
                    user else throw IllegalArgumentException("A user with this email already exists")
            }
    }


    fun registerUserByPhone(fullName: String, rawPhone: String): User {
        val regex = """\+(.*\d){11}""".toRegex()
        if (!regex.matches(rawPhone)) {
            throw IllegalArgumentException("Enter a valid phone number starting with a + and containing 11 digits")
        }
        return User.makeUser(fullName, rawPhone)
            .also { user ->
                if (map[rawPhone] == null) map[rawPhone] = user
                else throw  IllegalArgumentException("A user with this phone already exists")
            }
    }


    fun loginUser(login: String, password: String): String? {
        return map[login.trim()]?.run {
            if (checkPassword(password)) this.userInfo
            else null
        }
    }

    fun requestAccessCode(login: String): Unit {
        map[login.trim()]?.let {
            if (it.accessCode != null) it.changePassword(it.accessCode!!, it.generatorAccessCode())
        }
    }

    @VisibleForTesting(otherwise = VisibleForTesting.NONE)
    fun clearHolder() {
        map.clear()
    }
}
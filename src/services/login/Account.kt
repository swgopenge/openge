package services.login

import database.odb.PersistentObject

class Account(id : Long, userName : String?, passwordHash : String?) : PersistentObject {

    val id : Long = id
    var userName  = userName
    var passwordHash  = passwordHash

    override fun getPersistenceLevel(): Int = 1

}
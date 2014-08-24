package services.login

import database.odb.ObjectDatabase
import main.Core
import utils.PasswordEncryption

class LocalDbLoginProvider(odb : ObjectDatabase) : ILoginProvider {

    val accountODB = odb;

    override fun getAccountId(username: String?, password: String?, remoteAddress: String?): Int {

        val core : Core? = Core.getInstance()
        val autoReg : Boolean = core!!.loginService!!.isAutoRegistration()
        var account: Account? = accountODB.get(username) as Account
        val hash : String? = PasswordEncryption.generatePasswordHash(password)

        if(account == null && !autoReg)
            return -2;
        else if(account != null) {
            val passMatch : Boolean = PasswordEncryption.validatePassword(password, account?.passwordHash)
            when {
                passMatch -> return 0
                !passMatch -> return -3
            }
        } else {
            account = Account(core.loginService!!.generateAccountId(), username, hash)
            accountODB.put(username, account)
            return 0
        }

        return 0
    }


}

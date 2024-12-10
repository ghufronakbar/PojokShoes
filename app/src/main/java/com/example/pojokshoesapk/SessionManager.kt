import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("SessionPrefs", Context.MODE_PRIVATE)
    private val editor: SharedPreferences.Editor = sharedPreferences.edit()

    companion object {
        const val KEY_KERANJANG_ID = "keranjang_id"
        const val KEY_KERANJANG_STATUS = "keranjang_status"
        const val KEY_USER_ID = "id_user"
        const val KEY_TOKEN = "token_user"
        const val KEY_KERANJANG_OWNER_ID = "keranjang_owner_id"
    }

    // Fungsi untuk menghapus seluruh data sesi (Logout)
    fun clearSessionData() {
        editor.clear()  // Menghapus semua data yang tersimpan
        editor.apply()  // Menyimpan perubahan
    }

    // Fungsi logout yang membersihkan data sesi
    fun logout() {
        clearSessionData()  // Menghapus semua data sesi
    }

    // Menyimpan ID Keranjang
    fun saveKeranjangId(keranjangId: Int?) {
        if (keranjangId != null) {
            editor.putInt(KEY_KERANJANG_ID, keranjangId)
        } else {
            editor.remove(KEY_KERANJANG_ID)
        }
        editor.apply()
    }

    // Mendapatkan ID Keranjang
    fun getKeranjangId(): Int? {
        val id = sharedPreferences.getInt(KEY_KERANJANG_ID, -1)
        return if (id == -1) null else id
    }

    // Menyimpan Status Keranjang
    fun saveKeranjangStatus(status: Int) {
        editor.putInt(KEY_KERANJANG_STATUS, status)
        editor.apply()
    }

    // Mendapatkan Status Keranjang
    fun getKeranjangStatus(): Int {
        return sharedPreferences.getInt(KEY_KERANJANG_STATUS, 0)
    }

    // Membersihkan Data Keranjang (ID, Status, dan Pemilik)
    fun clearKeranjangData() {
        editor.remove(KEY_KERANJANG_ID)
        editor.remove(KEY_KERANJANG_STATUS)
        editor.remove(KEY_KERANJANG_OWNER_ID)
        editor.apply()
    }

    // Menyimpan User ID
    fun saveUserId(userId: Int) {
        editor.putInt(KEY_USER_ID, userId)
        editor.apply()
    }

    // Mendapatkan User ID
    fun getUserId(): Int? {
        val id = sharedPreferences.getInt(KEY_USER_ID, -1)
        return if (id == -1) null else id
    }

    // Menyimpan Token
    fun saveToken(token: String) {
        editor.putString(KEY_TOKEN, token)
        editor.apply()
    }

    // Mendapatkan Token
    fun getToken(): String? {
        return sharedPreferences.getString(KEY_TOKEN, null)
    }

    // Menyimpan ID Pemilik Keranjang
    fun saveKeranjangOwnerId(userId: Int?) {
        if (userId != null) {
            editor.putInt(KEY_KERANJANG_OWNER_ID, userId)
        } else {
            editor.remove(KEY_KERANJANG_OWNER_ID)
        }
        editor.apply()
    }

    // Mendapatkan ID Pemilik Keranjang
    fun getKeranjangOwnerId(): Int? {
        val id = sharedPreferences.getInt(KEY_KERANJANG_OWNER_ID, -1)
        return if (id == -1) null else id
    }
}

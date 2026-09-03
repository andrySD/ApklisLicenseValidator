package cu.uci.android.apklis_license_validator.models

import com.google.gson.annotations.SerializedName

data class VerifyLicenseResponse(
    @SerializedName("license") val license: String?,
    @SerializedName("activated_at") val activatedAt: String? = null,
    @SerializedName("expire_at") val expireAt: String? = null,
    @SerializedName("expire_in") val expireIn: String? = null,
) {
    fun toJsonString(): String {
        return """{"license": "$license", "expire_in": "$expireIn"}"""
    }
}

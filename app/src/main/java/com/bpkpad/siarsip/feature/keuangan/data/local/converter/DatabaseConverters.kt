package com.example.arsipbpkpad.data.local.converter

import androidx.room.TypeConverter
import com.example.arsipbpkpad.domain.model.ArchiveMetadata
import com.example.arsipbpkpad.domain.model.DocCondition
import com.example.arsipbpkpad.domain.model.DocCopyType
import com.example.arsipbpkpad.domain.model.DocStatus
import org.json.JSONObject

class DatabaseConverters {

    @TypeConverter
    fun fromDocStatus(value: DocStatus): String = value.name

    @TypeConverter
    fun toDocStatus(value: String): DocStatus = try { DocStatus.valueOf(value) } catch (e: Exception) { DocStatus.AVAILABLE }

    @TypeConverter
    fun fromDocCopyType(value: DocCopyType): String = value.name

    @TypeConverter
    fun toDocCopyType(value: String): DocCopyType = try { DocCopyType.valueOf(value) } catch (e: Exception) { DocCopyType.ORIGINAL }

    @TypeConverter
    fun fromDocCondition(value: DocCondition): String = value.name

    @TypeConverter
    fun toDocCondition(value: String): DocCondition = try { DocCondition.valueOf(value) } catch (e: Exception) { DocCondition.GOOD }

    @TypeConverter
    fun fromMetadata(value: ArchiveMetadata?): String? {
        if (value == null) return null
        val json = JSONObject()
        json.put("bankName", value.bankName)
        json.put("accountNumber", value.accountNumber)
        json.put("paymentPurpose", value.paymentPurpose)
        json.put("budgetCode", value.budgetCode)
        json.put("warehouse", value.warehouse)
        json.put("rack", value.rack)
        json.put("boxNumber", value.boxNumber)
        return json.toString()
    }

    @TypeConverter
    fun toMetadata(value: String?): ArchiveMetadata? {
        if (value.isNullOrBlank()) return null
        return try {
            val json = JSONObject(value)
            ArchiveMetadata(
                bankName = if (json.has("bankName") && !json.isNull("bankName")) json.getString("bankName") else null,
                accountNumber = if (json.has("accountNumber") && !json.isNull("accountNumber")) json.getString("accountNumber") else null,
                paymentPurpose = if (json.has("paymentPurpose") && !json.isNull("paymentPurpose")) json.getString("paymentPurpose") else null,
                budgetCode = if (json.has("budgetCode") && !json.isNull("budgetCode")) json.getString("budgetCode") else null,
                warehouse = if (json.has("warehouse") && !json.isNull("warehouse")) json.getString("warehouse") else null,
                rack = if (json.has("rack") && !json.isNull("rack")) json.getString("rack") else null,
                boxNumber = if (json.has("boxNumber") && !json.isNull("boxNumber")) json.getString("boxNumber") else null
            )
        } catch (e: Exception) {
            null
        }
    }
}

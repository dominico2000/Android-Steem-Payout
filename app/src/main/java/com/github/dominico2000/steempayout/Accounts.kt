package com.github.dominico2000.steempayout


import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

/**
 * Created by dominik on 09.02.18.
 */
@Entity(tableName = "accounts")
data class Accounts(@PrimaryKey(autoGenerate = true) var id: Long = 0,
                    @ColumnInfo(name = "name") var name: String,
                    @ColumnInfo(name = "timestamp") var timestamp: Long,
                    @ColumnInfo(name = "SBD") var SBD: Float,
                    @ColumnInfo(name = "SP") var SP: Float) {

    constructor():this(0, "", 0, 0F, 0F)

}


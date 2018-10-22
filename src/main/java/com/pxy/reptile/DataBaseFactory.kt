package com.pxy.reptile

import com.pxy.reptile.databaseentity.MVideo
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

class DataBaseFactory {
    companion object {
        fun init() {
            Database.connect("jdbc:mysql://localhost/test?useSSL=false&serverTimezone=UTC&useUnicode=true&characterEncoding=UTF-8&zeroDateTimeBehavior=convertToNull", "com.mysql.jdbc.Driver", "root", "123456")
            transaction {
                SchemaUtils.createMissingTablesAndColumns(MVideo)
            }
        }
    }
}
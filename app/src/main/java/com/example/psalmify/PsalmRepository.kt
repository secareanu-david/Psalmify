package com.example.psalmify

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PsalmRepository(private val psalmDao: PsalmDao,
                      private val favoriteList: List<Int>) {

    val allPsalmItems: Flow<List<PsalmItem>> = getPsalmItems()

    private fun getPsalmItems(): Flow<List<PsalmItem>> {
        return psalmDao.getAllPsalms().map { psalms: List<Psalm> ->
            psalms.map { psalm: Psalm ->
                PsalmItem(psalm, favoriteList.contains(psalm.id))
            }
        }
    }
}

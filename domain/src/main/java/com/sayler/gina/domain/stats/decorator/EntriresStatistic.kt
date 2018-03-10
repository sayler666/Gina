package com.sayler.gina.domain.stats.decorator

import com.sayler.gina.domain.IDay
import com.sayler.gina.domain.stats.Statistic
import com.sayler.gina.domain.stats.StatisticPair

/**
 * Created by sayler on 03.10.2017.
 */
class EntriresStatistic : Statistic {
    override fun generate(days: List<IDay>): MutableList<StatisticPair> {

        val list = mutableListOf<StatisticPair>()
        list.add(StatisticPair("Entries", days.count().toString()))

        return list
    }

}
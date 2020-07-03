package com.sayler.monia.domain.stats.decorator

import com.sayler.monia.domain.IDay
import com.sayler.monia.domain.stats.Statistic
import com.sayler.monia.domain.stats.StatisticPair

/**
 * Created by sayler on 03.10.2017.
 */
open class StatisticDecorator(val stat: Statistic) : Statistic {

    override fun generate(days: List<IDay>): MutableList<StatisticPair> {
        return stat.generate(days)
    }

}
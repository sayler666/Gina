package com.sayler.gina.stats.decorator

import com.sayler.gina.domain.IDay
import com.sayler.gina.stats.Statistic
import com.sayler.gina.stats.StatisticPair

/**
 * Created by sayler on 03.10.2017.
 */
open class StatisticDecorator(val stat: Statistic) : Statistic {

    override fun generate(days: List<IDay>): MutableList<StatisticPair> {
        return stat.generate(days)
    }

}
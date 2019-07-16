package com.sayler.gina.domain.stats

import com.sayler.gina.domain.IDay

/**
 * Created by sayler on 03.10.2017.
 */
interface Statistic {
    fun generate(days: List<IDay>): MutableList<StatisticPair>
}
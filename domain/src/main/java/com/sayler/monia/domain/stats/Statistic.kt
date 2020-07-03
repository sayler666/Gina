package com.sayler.monia.domain.stats

import com.sayler.monia.domain.IDay

/**
 * Created by sayler on 03.10.2017.
 */
interface Statistic {
    fun generate(days: List<IDay>): MutableList<StatisticPair>
}
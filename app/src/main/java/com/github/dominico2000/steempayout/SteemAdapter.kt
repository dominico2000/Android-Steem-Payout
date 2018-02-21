package com.github.dominico2000.steempayout

import android.util.Log
import eu.bittrade.libs.steemj.SteemJ
import eu.bittrade.libs.steemj.apis.follow.model.CommentBlogEntry
import eu.bittrade.libs.steemj.base.models.AccountName
import eu.bittrade.libs.steemj.enums.RewardFundType



class SteemAdapter constructor(private val accountName: AccountName) {



    fun getPostsPotencialReward(): Float {

        val steemJ = SteemJ()


        Log.d("Steem Ticker",  steemJ.ticker.toString())

        val rewardFund = steemJ.getRewardFund(RewardFundType.POST)
        val rewardBalance = rewardFund.rewardBalance.amount;
        val recentClaims = rewardFund.recentClaims;
        val base = steemJ.currentMedianHistoryPrice.base.amount
        val foundPerShare = rewardBalance.toFloat() / recentClaims.toFloat()
        val correctionDivider = 1000000

        var postsLimit = 0
        var aa: List<CommentBlogEntry>
        do{
            postsLimit += 10
            aa = steemJ.getBlog(accountName, 0, postsLimit.toShort() )

            val lastPostRshares = aa[aa.size - 1].comment.voteRshares
        }while(lastPostRshares > 0)


        var payout = 0.toFloat()
        for (a in aa) {
             payout += a.comment.voteRshares.toFloat() * foundPerShare * base / correctionDivider
        }
        return payout
    }


    fun reward5050(totalPostReward: Float): List<Float> {
        var reward = totalPostReward * 0.8
        var sbd = (reward*0.5).toFloat()
        var sp = (reward*0.5).toFloat()
        return listOf(sbd, sp)
    }

    fun reward100sp(totalPostReward: Float): List<Float> {
        var reward = totalPostReward * 0.8
        var sbd = 0.toFloat()
        var sp = reward.toFloat()
        return listOf(sbd, sp)
    }
}
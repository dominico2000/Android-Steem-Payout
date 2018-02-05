package com.github.dominico2000.steempayout

import eu.bittrade.libs.steemj.SteemJ
import eu.bittrade.libs.steemj.apis.follow.model.CommentBlogEntry
import eu.bittrade.libs.steemj.base.models.AccountName
import eu.bittrade.libs.steemj.enums.RewardFundType


class SteemAdapter constructor(val accountName: AccountName) {



    fun getPostsPotencialReward(): Float {

        val steemJ = SteemJ()

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
            val aaSize = aa.size
            val lastPostRshares = aa[aaSize-1].comment.voteRshares
        }while(lastPostRshares > 0)


        var payout = 0.toFloat()
        for (a in aa) {
             payout = a.comment.voteRshares.toFloat() * foundPerShare * base / correctionDivider
        }
        return payout
    }
        
}
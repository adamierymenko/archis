package org.api.archis.universe;

/**
 * <p>Interface for classes providing a reward function.</p>
 *
 * <p>
 * A reward function provides a reward to cells that provide a certain
 * output when given a certain input.  This is different from a 'fitness
 * function' in what I refer to as 'theistic' genetic programming implementations
 * in that good performance in the reward function is not a requirement for
 * survival.  Instead, the reward function should simply reward organisms that
 * provide good output.
 * </p>
 *
 * <p>
 * In nature, photosynthesis is analogous to a reward function.  Plants solve
 * the problem of capturing photons and are rewarded with energy.  In this
 * simulator, the reward function can be used to give the entire simulation a
 * problem to solve in order to measure how well it evolves.  A reward function
 * can also be a means of linking the simulator to some practical application of
 * machine learning, as can be seen in StockMarketRewardFunction.
 * </p>
 *
 * <p>
 * A reward function at the moment is just an environmental element with some
 * statistics gathering methods.
 * </p>
 *
 * @author Adam Ierymenko
 * @version 2.0
 */

public interface RewardFunction extends Condition,IOHandler
{
}

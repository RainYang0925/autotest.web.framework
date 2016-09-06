/**
 * http://surenpi.com
 */
package org.suren.autotest.web.framework.report.record;

import java.util.List;

/**
 * 搜索历史记录
 * @author suren
 * @date 2016年9月6日 下午8:29:33
 */
public class SearchRecord extends ExceptionRecord
{
	/** 搜索策略 */
	private String strategy;
	/** 定位方法 */
	private List<String> byList;
	/** 耗时 */
	private long cost;
	public SearchRecord(String strategy, long cost)
	{
		this.strategy = strategy;
		this.cost = cost;
	}
	public SearchRecord(long cost)
	{
		this.cost = cost;
	}
	/**
	 * @return the strategy
	 */
	public String getStrategy()
	{
		return strategy;
	}
	/**
	 * @param strategy the strategy to set
	 */
	public void setStrategy(String strategy)
	{
		this.strategy = strategy;
	}
	/**
	 * @return the byList
	 */
	public List<String> getByList()
	{
		return byList;
	}
	/**
	 * @param byList the byList to set
	 */
	public void setByList(List<String> byList)
	{
		this.byList = byList;
	}
	/**
	 * @return the cost
	 */
	public long getCost()
	{
		return cost;
	}
	/**
	 * @param cost the cost to set
	 */
	public void setCost(long cost)
	{
		this.cost = cost;
	}
}

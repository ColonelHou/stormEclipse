package com.storm.test.window;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import storm.starter.util.TupleHelpers;
import backtype.storm.Config;
import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;

public abstract class WindowedBolt extends BaseRichBolt {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8849434942882466073L;

	private static final Logger LOG = Logger.getLogger(WindowedBolt.class);

	private final static int DEFAULT_WINDOW_LEN_IN_SECS = 12;
	private final static int DEFAULT_WINDOW_EMIT_FREQ = 4;

	private int windowLengthInSeconds;
	private int emitFrequencyInSeconds;
	
	protected SlidingWindowCache<Tuple> cache;
	
	
	public WindowedBolt(){
		this(DEFAULT_WINDOW_LEN_IN_SECS,DEFAULT_WINDOW_EMIT_FREQ);
	}
	
	public WindowedBolt(int windowLenInSecs, int emitFrequency){
		if(windowLenInSecs%emitFrequency!=0){
			LOG.warn(String.format("Actual window length(%d) isnot emitFrequency(%d)'s times"));
		}
		this.windowLengthInSeconds = windowLenInSecs;
		this.emitFrequencyInSeconds = emitFrequency;
		cache = new SlidingWindowCache<Tuple>(getSlots(this.windowLengthInSeconds,this.emitFrequencyInSeconds));
	}
	
	private int getSlots(int windowLenInSecs, int emitFrequency){
		return windowLenInSecs/emitFrequency;
	}
	
	

	@Override
	public void execute(Tuple tuple) {
		if (TupleHelpers.isTickTuple(tuple)) {
			LOG.info("====>Received tick tuple, triggering emit of current window counts");
			emitCurrentWindowCounts();
		} else {
			emitNormal(tuple);
		}
	}

	
	private void emitNormal(Tuple tuple){
		cache.add(tuple);
	}
	
	public abstract void prepare(Map stormConf, TopologyContext context, OutputCollector collector);
	
	public abstract void emitCurrentWindowCounts();
	
	public abstract void declareOutputFields(OutputFieldsDeclarer declarer);
	
	@Override
	public Map<String, Object> getComponentConfiguration() {
		Map<String, Object> conf = new HashMap<String, Object>();
		conf.put(Config.TOPOLOGY_TICK_TUPLE_FREQ_SECS, emitFrequencyInSeconds);
		return conf;
	}

}

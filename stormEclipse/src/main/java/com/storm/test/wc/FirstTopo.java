package com.storm.test.wc;

import java.util.HashMap;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.utils.Utils;

public class FirstTopo {

	/**
	 * storm jar stormEclipse-0.0.1-SNAPSHOT.jar com.storm.test.wc.FirstTopo
	 * storm jar负责连接到Nimbus并且上传jar包
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		TopologyBuilder builder = new TopologyBuilder();
		builder.setSpout("spout", new RandomSpout());
		builder.setBolt("bolt", new SenqueceBolt()).shuffleGrouping("spout");
		Config conf = new Config();
		conf.setDebug(false);
		conf.setNumWorkers(3);
		
/*		StormSubmitter.submitTopology("firstTopo", conf,
				builder.createTopology());*/
		LocalCluster cluster = new LocalCluster();
		cluster.submitTopology("firstTopo", conf, builder.createTopology());
		Utils.sleep(100000);
		cluster.killTopology("firstTopo");
		cluster.shutdown();
		
		/*
		if (args != null && args.length > 0) {
			conf.setNumWorkers(3);
			StormSubmitter.submitTopology("firstTopo", conf,
					builder.createTopology());
		} else {

			LocalCluster cluster = new LocalCluster();
			cluster.submitTopology("firstTopo", conf, builder.createTopology());
			Utils.sleep(100000);
			cluster.killTopology("firstTopo");
			cluster.shutdown();
		}*/
	}
}

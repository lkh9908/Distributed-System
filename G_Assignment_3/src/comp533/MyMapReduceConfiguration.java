package comp533;

import org.eclipse.ui.internal.Model;

import comp533.barrier.Barrier;
import comp533.client.ClientInterface;
import comp533.client.Client;
import comp533.controller.Controller;
import comp533.joiner.Joiner;
import comp533.keyValue.KeyValueInterface;
import comp533.main.TokenCounterModel;
import comp533.main.RemoteModel;
import comp533.partitioner.Partitioner;
import comp533.partitioner.PartitionerFactory;
import comp533.reducer.ReducerInterface;
import comp533.reducer.ReducerFactory;
import comp533.server.IntegerSummerServer;
import comp533.server.TokenCounterServer;
import comp533.slave.SlaveInterface;
import comp533.view.View;
import facebook.RemoteClientFacebookMapReduce;
import facebook.ServerFacebookMapReduce;
import facebook.StandAloneFacebookMapReduce;
import gradingTools.comp533s21.assignment1.interfaces.MapReduceConfiguration;

import comp533.SumMain;

import comp533.mapper.MapperInterface;
import comp533.mapper.SumMapper;
import comp533.mapper.MapperFactory;

public class MyMapReduceConfiguration implements MapReduceConfiguration {

	public Class<?> getSlaveClass() {
		return SlaveInterface.class;
	}
	
	public Object getBarrier(final int aNumThreads) {
		return new Barrier(aNumThreads);
	}

	public Class<?> getBarrierClass() {
		return Barrier.class;
	}

	public Class<?> getClientTokenCounter() {
		return Client.class;
	}
	
	public Class<?> getRemoteClientObjectClass() {
		return Client.class;
	}

	public Class<?> getRemoteClientObjectInterface() {
		return ClientInterface.class;
	}

	public Class<?> getControllerClass() {
		return Controller.class;
	}

	public Object getIntSummingMapper() {
		return null;
	}

	public Class<?> getIntSummingMapperClass() {
		return SumMapper.class;
	}
	
	public Class<?> getServerIntegerSummer() {
		return IntegerSummerServer.class;
	}
	
	public Class<?> getStandAloneIntegerSummer() {
		return SumMain.class;
	}

	public Object getJoiner(final int aNumThreads) {
		return new Joiner(aNumThreads);
	}

	public Class<?> getJoinerClass() {
		 return Joiner.class;
	}

	public Class<?> getKeyValueClass() {
		return KeyValueInterface.class;
	}

	public Class<?> getMapperFactory() {
		return MapperFactory.class;
	}

	public Class<?> getModelClass() {
		return TokenCounterModel.class;
	}

	public Object getPartitioner() {
		return PartitionerFactory.getPartitioner();
	}

	public Class<?> getPartitionerClass() {
		return Partitioner.class;
	}

	public Class<?> getPartitionerFactory() {
		return PartitionerFactory.class;
	}

	public Object getReducer() {
		return ReducerFactory.getReducer();
	}

	public Class<?> getReducerClass() {
		return ReducerInterface.class;
	}

	public Class<?> getReducerFactory() {
		return ReducerFactory.class;
	}

	public Class<?> getRemoteClientFacebookMapReduce() {
		return RemoteClientFacebookMapReduce.class;
	}


	public Class<?> getRemoteModelInterface() {
		return RemoteModel.class;
	}

	public Class<?> getServerFacebookMapReduce() {
		return ServerFacebookMapReduce.class;
	}




	public Class<?> getServerTokenCounter() {
		return TokenCounterServer.class;
	}

	public Class<?> getStandAloneFacebookMapReduce() {
		return StandAloneFacebookMapReduce.class;
	}


	public Class<?> getStandAloneTokenCounter() {
		return MyStandAloneTokenCounter.class;
	}

	public Object getTokenCountingMapper() {
		return MapperFactory.getMapper();
	}

	public Class<?> getTokenCountingMapperClass() {
		return MapperInterface.class;
	}

	public Class<?> getViewClass() {
		return View.class;
	}

}
